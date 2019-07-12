package cn.com.cyber.controller.redirect;

/**
 * http版本
 */

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.controller.manager.filter.CompanyInfoFilter;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.model.CompanyInfo;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.service.CompanyInfoService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpClient;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/redirect_wh")
public class RedirectWHController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectWHController.class);

    @Autowired
    private Environment env;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private CompanyInfoService companyInfoService;

    //武汉服务
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void repeat(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String jsonData) {
        LOGGER.info("请求开始:{}", jsonData.length());
        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        String serviceUrl = request.getHeader("serviceUrl");
        int msgCode;
        String result = "";
        try {
            AppModel appModel = valiedParams(appKey, serviceKey);
            JSONObject jsonObject = new JSONObject();
            if (StringUtils.isNotBlank(jsonData)) {
                jsonObject = JSONObject.parseObject(jsonData);
            }
            jsonObject.put("requestUrl", appModel.getUrlPrefix() + appModel.getUrlSuffix());
            jsonObject.put("method", appModel.getMethod());
            jsonObject.put("contentType", appModel.getContentType());
            //发送请求
            String url = "http://" + env.getProperty(CodeUtil.SOCKET_URL) + ":" + env.getProperty(CodeUtil.SOCKET_PORT) + CodeUtil.MODEL_REQUSET_URL;
            //测试用
            if (serviceUrl != null && "getTest".equals(serviceUrl)) {
                url += "/getTest";
            }
            Map<String, Object> resultMap = HttpClient.httpRequest(url, CodeUtil.METHOD_POST, CodeUtil.CONTEXT_JSON, jsonObject.toString());
            if (resultMap.get("code") != null && CodeUtil.HTTP_OK == (Integer) resultMap.get("code")) {
                result = resultMap.get("result").toString();
            } else {
                result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, messageCodeUtil.getMessage(CodeUtil.REQUEST_USE_FILED) + resultMap.get("error")));
            }
            setResponseText(response, result);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
            response.setStatus(500);
            setResponseText(response, JSON.toJSONString(RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode))));
        }
        LOGGER.info("本次请求结束 result:{}", result);
    }

    @RequestMapping(value = "/test", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String test() {
        LOGGER.info("测试:{}");
        String url = "http://" + env.getProperty(CodeUtil.SOCKET_URL) + ":" + env.getProperty(CodeUtil.SOCKET_PORT) + CodeUtil.MODEL_REQUSET_URL;
        //测试用
        url += "/getTest";
        Map<String, Object> resultMap = HttpClient.httpRequest(url, CodeUtil.METHOD_POST, CodeUtil.CONTEXT_JSON, null);
        String result;
        if ((Boolean) resultMap.get("flag")) {
            if (resultMap.get("result") == null) {
                result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, messageCodeUtil.getMessage(CodeUtil.REQUEST_USE_FILED)));
            } else {
                result = resultMap.get("result").toString();
            }
        } else {
            result = resultMap.get("error").toString();
        }
        LOGGER.info("本次请求结束 result:{}", result.length());
        return result;
    }

    @RequestMapping("queryCompanyListData")
    @ResponseBody
    public RestResponse queryCompanyListData(CompanyInfo companyInfo) {
        LOGGER.info("获取公司列表数据:{}");
        PageHelper.startPage(companyInfo.getPageNum(), companyInfo.getPageSize());
        List<CompanyInfo> companyInfos = companyInfoService.getList(companyInfo);
        PageInfo<CompanyInfo> companyInfoPage = new PageInfo<CompanyInfo>(companyInfos);
        Object parseCompanyInfos = filterParam(companyInfos, CompanyInfoFilter.INFO_FILTER);
        return RestResponse.success().setData(parseCompanyInfos).setTotal(companyInfoPage.getTotal()).
                setPage(companyInfoPage.getLastPage());
    }

}

