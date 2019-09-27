package cn.com.cyber.controller;

import cn.com.cyber.util.*;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/redirect")
public class NettyRedirectController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRedirectController.class);
    @Autowired
    private Environment env;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private JedisPool jedisPool;


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void redirect(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String jsonData) {
        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        String serviceUrl = request.getHeader("serviceUrl");
        int msgCode;
        String result = "";
        try {
            String validToken = env.getProperty(CodeUtil.VALID_TOKEN);
            if (Boolean.valueOf(validToken)) {
                validToken(request, response);//校验token
            }
            JSONObject jsonObject = new JSONObject();
            if (StringUtils.isNotBlank(jsonData)) {
                jsonObject = JSONObject.parseObject(jsonData);
            }
            //发送请求
            String url = messageCodeUtil.getMessage(CodeUtil.PLATFORM_REQUSET_URL);
            //测试用
            if (serviceUrl != null && "getTest".equals(serviceUrl)) {
                url += "/getTest";
            }
            Map<String, String> serviceHeader = Maps.newHashMap();
            serviceHeader.put("appKey", appKey);
            serviceHeader.put("serviceKey", serviceKey);
            Map<String, Object> resultMap = HttpConnection.httpRequest(url, CodeUtil.RESPONSE_POST, CodeUtil.CONTEXT_JSON, jsonObject.toString(), null, serviceHeader);
            if (resultMap.get("code") != null) {
                result = resultMap.get("result").toString();
                if (CodeUtil.HTTP_OK != (Integer) resultMap.get("code")) { //组装错误返回
                    msgCode = CodeUtil.REQUEST_USE_FILED;
                    result = JSON.toJSONString(RestResponse.res(msgCode, errorMsg(messageCodeUtil.getMessage(msgCode), resultMap.get("code").toString(), result)));
                }
            }
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
            result = JSON.toJSONString(RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)));
        }
        LOGGER.info("本次请求结束 result:{}", result);
        setResponseText(response, result);
    }


    @RequestMapping("/login")
    @ResponseBody
    public RestResponse login(String username, String password) {
        RestResponse rest = new RestResponse();
        int msgCode = CodeUtil.BASE_SUCCESS;
        try {
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                throw new ValueRuntimeException(CodeUtil.REQUEST_PARAM_NULL);
            }
            StringBuffer url = new StringBuffer(messageCodeUtil.getMessage(CodeUtil.PSTORE_LOGIN_URL));
            url.append("?username=" + username);
            String passwordParam = EncryptUtils.MD5Encode(username + password + "*!!");
            url.append("&password=" + passwordParam);
            Map<String, Object> resultMap = HttpConnection.httpRequest(url.toString(), CodeUtil.RESPONSE_GET, null, null,
                    null, null);
            if (resultMap.get("code") != null && CodeUtil.HTTP_OK == (Integer) resultMap.get("code")) {
                JSONObject jsonObject = JSONObject.parseObject(resultMap.get("result").toString());
                Integer ret = jsonObject.getInteger("ret");
                if (ret == 0) {
                    rest.setData(createToken(username));
                } else {
                    throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_VALIED);
                }
            } else {
                throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_CONNECT);
            }
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        rest.setCode(msgCode).setMessage(messageCodeUtil.getMessage(msgCode));
        return rest;
    }

    private String createToken(String username) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.PSTORE_LOGIN_REDIS_INDEX);
        String token = jedis.get(CodeUtil.PSTORE_LOGIN_REDIS_PREFIX + username);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        token = HttpConnection.getUUID();
        jedis.setex(CodeUtil.PSTORE_LOGIN_REDIS_PREFIX + username, 60 * 60 * 2, token);
        return token;
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
            e.printStackTrace();
            response.setStatus(401);
            throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_VALIED); //用户登陆失败
        } finally {
            jedis.close();
        }
    }

    @RequestMapping("/getTest")
    @ResponseBody
    public RestResponse getTest() {
        LOGGER.info("测试连接");
        return RestResponse.success().setData("Hello World");
    }

}
