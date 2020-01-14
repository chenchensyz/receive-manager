package cn.com.cyber.interceptor;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.common.RestResponse;
import cn.com.cyber.util.SpringUtil;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * api接口验证
 */
public class ApiInterceptor implements HandlerInterceptor {

    private static Map<Integer, String> messageMap = Maps.newHashMap();

    static {
        messageMap.put(CodeUtil.REQUEST_USER_NULL, "头信息中缺少 用户 信息");
        messageMap.put(CodeUtil.REQUEST_TOKEN_NULL, "头信息中缺少 token 信息");
        messageMap.put(CodeUtil.REQUEST_TOKEN_ERR, "token验证失败，请重新获取");
        messageMap.put(CodeUtil.REQUEST_TOKEN_USER_FILED, "用户验证失败");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MyInteceptor.class);

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
        String userId = request.getHeader("userId");
        if (StringUtils.isBlank(token)) {
            writeJsonResult(response, 401, CodeUtil.REQUEST_TOKEN_NULL, messageMap.get(CodeUtil.REQUEST_TOKEN_NULL));
            return false;
        }

        if (StringUtils.isBlank(userId)) {
            writeJsonResult(response, 401, CodeUtil.REQUEST_USER_NULL, messageMap.get(CodeUtil.REQUEST_USER_NULL));
            return false;
        }

        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        try {
            Map<String, String> tokenMap = jedis.hgetAll(CodeUtil.REDIS_PREFIX + token);
            if (tokenMap.isEmpty()) {
                writeJsonResult(response, 401, CodeUtil.REQUEST_TOKEN_ERR, messageMap.get(CodeUtil.REQUEST_TOKEN_ERR));
                return false;
            }
            if (!userId.equals(tokenMap.get("userId"))) {
                writeJsonResult(response, 401, CodeUtil.REQUEST_TOKEN_USER_FILED, messageMap.get(CodeUtil.REQUEST_TOKEN_USER_FILED));
                return false;
            }
            jedis.expire(CodeUtil.REDIS_PREFIX + token, CodeUtil.REDIS_EXPIRE_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_LOGIN); //用户登陆失败
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