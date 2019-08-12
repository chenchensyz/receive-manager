package cn.com.cyber.interceptor;

import cn.com.cyber.socket.SpringUtil;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Configuration
public class MyInteceptor implements WebMvcConfigurer {


@Autowired
private Environment environment;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterfaceAuthCheckInterceptor()).addPathPatterns("/redirect/wh");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        String fileRootPath = environment.getProperty(CodeUtil.FILE_ROOT_PATH);
        System.out.println(fileRootPath);
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
        String token = request.getHeader("token");
        String username = request.getHeader("username");
        if (StringUtils.isBlank(token)) {
            writeJsonResult(response, 401, CodeUtil.REQUEST_TOKEN_NULL, messageMap.get(CodeUtil.REQUEST_TOKEN_NULL));
            return false;
        }
        if (StringUtils.isBlank(username)) {
            writeJsonResult(response, 401, CodeUtil.REQUEST_USER_NULL, messageMap.get(CodeUtil.REQUEST_USER_NULL));
            return false;
        }

        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.PSTORE_LOGIN_REDIS_INDEX);
        try {
            String tokenSec = jedis.get(CodeUtil.PSTORE_LOGIN_REDIS_PREFIX + username);
            if (StringUtils.isBlank(tokenSec) || !tokenSec.equals(token)) {
                writeJsonResult(response, 401, CodeUtil.REQUEST_TOKEN_ERR, messageMap.get(CodeUtil.REQUEST_TOKEN_ERR));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_VALIED); //用户登陆失败
        } finally {
            jedis.close();
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
            e.printStackTrace();
        }
        out.print(RestResponse.res(code, msg));
        out.flush();
    }
}