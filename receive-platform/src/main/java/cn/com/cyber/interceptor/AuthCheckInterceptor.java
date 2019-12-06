package cn.com.cyber.interceptor;

import cn.com.cyber.config.shiro.ShiroDbRealm;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.RestResponse;
import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 微服务间接口访问密钥验证
 *
 * @author xiaochangwei
 */
public class AuthCheckInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyInteceptor.class);

    private static Map<Integer, String> messageMap = Maps.newHashMap();

    static {
        messageMap.put(CodeUtil.REQUEST_TOKEN_NULL, "头信息中缺少 token 信息");
        messageMap.put(CodeUtil.REQUEST_USER_NULL, "头信息中缺少 用户 信息");
        messageMap.put(CodeUtil.REQUEST_TOKEN_ERR, "token验证失败，请重新获取");
    }

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ShiroDbRealm.ShiroUser shiroUser = (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
        if (shiroUser == null) {
            writeJsonResult(response, 401, CodeUtil.REQUEST_TOKEN_ERR, messageMap.get(CodeUtil.REQUEST_TOKEN_ERR));
            return false;
        }
        return true;
    }

    private void writeJsonResult(HttpServletResponse response, int httpCode, int code, String msg) {
        response.setStatus(httpCode);
        response.setContentType(CodeUtil.CONTEXT_JSON);
        response.setCharacterEncoding(CodeUtil.cs.toString());
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        out.print(RestResponse.res(code, msg));
        out.flush();
    }
}