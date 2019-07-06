package cn.com.cyber.interceptor;

import cn.com.cyber.util.CodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class MyInteceptor implements WebMvcConfigurer {

    private final static String FILEPATH = "D:\\";
//    private final static String FILEPATH="/software/";

@Autowired
private Environment environment;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterfaceAuthCheckInterceptor()).addPathPatterns("/api");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        String fileRootPath = environment.getProperty(CodeUtil.FILE_ROOT_PATH);
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/file/**").addResourceLocations("file:" + fileRootPath);
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowCredentials(true).allowedHeaders("Origin, X-Requested-With, Content-Type, Accept")
//                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS");
//    }

}

/**
 * 微服务间接口访问密钥验证
 *
 * @author xiaochangwei
 */
class InterfaceAuthCheckInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyInteceptor.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;

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
        String sessionId = request.getSession().getAttribute("sessionId") + "";
        String sessId = request.getSession().getId();
        LOGGER.info("ValidateSessionInterceptor :{}", sessionId);
        boolean isLogin = false;
        if (StringUtils.isBlank(sessionId) || "null".equalsIgnoreCase(sessionId)) {
            isLogin = true;
        } else if (!sessionId.equals(sessId)) {
            isLogin = true;
        }
        if (isLogin) {
//            LOGGER.info("sendRedirect URL:{}", request.getContextPath() + "/login");
//            response.sendRedirect(request.getContextPath() + "/login/toLogin");
            response.setHeader("sessionstatus", "timeout");
            response.sendError(401, "session timeout.");
            response.setStatus(401);
            response.sendError(401);
            return false;
        }
        return true;
    }
}