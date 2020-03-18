package cn.com.cyber.controller;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    //判断返回值形式是否json
    protected boolean ifJson(String cacheable) {
        boolean flag = true;
        try {
            JSONObject.parseObject(cacheable);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
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
            e.printStackTrace();
        }
    }

    //将图片输出至网页
    public static void setResponseImage(HttpServletResponse response, byte[] defalutImg) {
        try {
            if (defalutImg == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("image/png");
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

    public String getString(JSONObject json, String key) {
        Object value = json.get(key);

        if (value == null) {
            return "";
        }

        return value.toString();
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

    protected String errorMsg(String message, String errorCode, String errorMsg) {
        String result = message + errorCode + ":" + errorMsg;
        return result;
    }

    //校验token
    public void validToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        String username = request.getHeader("username");
        if (StringUtils.isBlank(token)) {
            response.setStatus(401);
            throw new ValueRuntimeException(CodeUtil.REQUEST_TOKEN_NULL);
        }
        if (StringUtils.isBlank(username)) {
            response.setStatus(401);
            throw new ValueRuntimeException(CodeUtil.REQUEST_USER_NULL);
        }

        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.PSTORE_LOGIN_REDIS_INDEX);
        try {
            String tokenSec = jedis.get(CodeUtil.PSTORE_LOGIN_REDIS_PREFIX + username);
            if (StringUtils.isBlank(tokenSec) || !tokenSec.equals(token)) {
                response.setStatus(401);
                throw new ValueRuntimeException(CodeUtil.REQUEST_TOKEN_ERR);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setStatus(401);
            throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_VALIED); //用户登陆失败
        } finally {
            jedis.close();
        }
    }
}
