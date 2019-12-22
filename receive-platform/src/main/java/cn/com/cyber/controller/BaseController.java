package cn.com.cyber.controller;

import cn.com.cyber.config.shiro.ShiroDbRealm;
import cn.com.cyber.model.AppService;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private AppServiceService appServiceService;

    protected Object filterParam(Object o, String filters) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        Set<String> userSet = Sets.newHashSet(Arrays.asList(filters.split(",")));
        filter.getExcludes().addAll(userSet);
        String jsonString = JSONObject.toJSONString(o, filter);
        return JSONObject.parse(jsonString);
    }

    public String getUserId() {
        ShiroDbRealm.ShiroUser user = (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
        return user.userId;
    }

    public String getApplication(Environment environment, String code) {
        String title = null;
        try {
            byte[] bytes = environment.getProperty(code).getBytes("ISO-8859-1");
            title = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return title;
    }

    //bean转换map
    public static Map transBean2Map(Object javaBean, List<String> excloude) {
        Map map = new HashMap();
        try {
            // 获取javaBean属性
            BeanInfo beanInfo = Introspector.getBeanInfo(javaBean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (propertyDescriptors != null && propertyDescriptors.length > 0) {
                String propertyName = null; // javaBean属性名
                Object propertyValue = null; // javaBean属性值
                for (PropertyDescriptor pd : propertyDescriptors) {
                    propertyName = pd.getName();
                    if (excloude == null || (excloude != null && !excloude.contains(propertyName))) {
                        if (!propertyName.equals("class")) {
                            Method readMethod = pd.getReadMethod();
                            propertyValue = readMethod.invoke(javaBean, new Object[0]);
                            map.put(propertyName, propertyValue);
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return map;
    }

    public ShiroDbRealm.ShiroUser getShiroUser() {
        ShiroDbRealm.ShiroUser shiroUser = (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
        if (shiroUser == null) {
            Subject subject = SecurityUtils.getSubject();
            subject.getSession().setTimeout(0);
            subject.logout();
            throw new ValueRuntimeException(CodeUtil.BASE_VALED);
        }
        return shiroUser;
    }

    //将图片输出至网页
    public static void setResponseFile(HttpServletResponse response, byte[] defalutImg,
                                       String contentType) {
        try {
            if (defalutImg == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType(contentType);
            response.setCharacterEncoding("UTF-8");
            if (defalutImg == null) {
                response.setContentLength(defalutImg.length);
                response.getOutputStream().write(defalutImg, 0, defalutImg.length);
                return;
            }

            response.setContentLength(defalutImg.length);

            OutputStream opStream = response.getOutputStream();
            InputStream inStream = new ByteArrayInputStream(defalutImg);

            int dataLen = 4 * 1024;
            byte[] data = new byte[dataLen];

            int len;
            while ((len = inStream.read(data, 0, dataLen)) != -1) {
                opStream.write(data, 0, len);
            }
            opStream.close();
            inStream.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 以text格式输出
     *
     * @param response
     */
    public void setResponseText(HttpServletResponse response, String responseObject) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(responseObject);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 以JSON格式输出
     *
     * @param response
     */
    public void setResponseJson(HttpServletResponse response, String responseObject) {
        //将实体对象转换为JSON Object转换
        JSONObject responseJSONObject = JSONObject.parseObject(responseObject);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(responseJSONObject.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public AppService validParams(String appKey, String serviceKey) {
        int msgCode;
        //根据appKey和serviceKey查询appinfo信息
        if (StringUtils.isBlank(appKey) || StringUtils.isBlank(serviceKey)) {
            throw new ValueRuntimeException(CodeUtil.REQUEST_PARAM_NULL); //请求的参数中缺少查询条件
        }

        AppService appService;
        if (appKey.equals(serviceKey)) { //独立接口
            appService = appServiceService.getByServiceKey(serviceKey);
            if (appService != null && appService.getServiceType() != 1) {  //不是独立接口
                throw new ValueRuntimeException(CodeUtil.REQUEST_KEY_NOT_ONLY);
            }
        } else { //应用接口
            appService = appServiceService.getByAppKeyAndServiceKey(appKey, serviceKey);
            if (appService == null) {  //应用接口需授权
                appService = appServiceService.getValidAppAndService(appKey, serviceKey);
            }
        }

        if (appService == null) {
            throw new ValueRuntimeException(CodeUtil.REQUEST_KEY_FILED);
        }

        if (appService.getAppState() != null && CodeUtil.APP_STATE_ENABLE != appService.getAppState()) {
            throw new ValueRuntimeException(CodeUtil.APPINFO_ERR_UNENABLE);
        }
        if (CodeUtil.APP_STATE_ENABLE != appService.getState()) {
            throw new ValueRuntimeException(CodeUtil.APPINFO_REFUSE_SERVICE);
        }
        return appService;
    }

}
