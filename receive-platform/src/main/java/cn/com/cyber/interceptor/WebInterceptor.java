package cn.com.cyber.interceptor;

import cn.com.cyber.util.common.RestResponse;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * web后台验证
 */
public class WebInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyInteceptor.class);

    private static Map<Integer, String> messageMap = Maps.newHashMap();

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
        if (isAjax(request)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.setStatus(401);
            RestResponse restResponse = new RestResponse();
            restResponse.res(401, "登录认证失效，请重新登录!");
            response.getWriter().write(JSONObject.toJSON(restResponse).toString());
        } else {
            response.sendRedirect("login/toLogin");
        }
        return false;
    }

    private boolean isAjax(ServletRequest request) {
        String header = ((HttpServletRequest) request).getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(header)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}