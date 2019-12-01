package cn.com.cyber.controller;

import cn.com.cyber.socket.SocketClient;
import cn.com.cyber.util.*;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.com.cyber.util.CodeUtil.cs;

/**
 * http版本
 */

@Controller
@ConditionalOnExpression("'${redirect_type}'.equals('http')")
@RequestMapping("/redirect")
public class RedirectController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectController.class);

    @Autowired
    private Environment env;

    @Autowired
    private JedisPool jedisPool;

    @Value("${server.port}")
    private String projectPort;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    //请求转发-http
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void redirect(HttpServletRequest request, HttpServletResponse response,
                         @RequestBody(required = false) String jsonData) {
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
            String url = env.getProperty(CodeUtil.PLATFORM_URL);
            //测试用
            if (serviceUrl != null && "getTest".equals(serviceUrl)) {
                url += "/getTest";
            }
            Map<String, String> paramHeader = Maps.newHashMap();
            paramHeader.put("appKey", appKey);
            paramHeader.put("serviceKey", serviceKey);
            Map<String, Object> resultMap = HttpConnection.httpRequest(url, CodeUtil.RESPONSE_POST, CodeUtil.CONTEXT_JSON, jsonObject.toString(), null, paramHeader);
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

    @RequestMapping("/getTest")
    @ResponseBody
    public RestResponse getTest() {
        LOGGER.info("测试连接:{}");
        return RestResponse.success().setData("连接成功");
    }

    @RequestMapping("/getInter")
    @ResponseBody
    public String getInter() {
        JSONObject jsonObject = new JSONObject();
        String messageId = projectPort + ":" + HttpConnection.getUUID();
        jsonObject.put("messageId", messageId);
        jsonObject.put("type", "test");
        LOGGER.info("测试通信jsonObject:{}", jsonObject);
        String baseParam = "";
        try {
            byte[] bytes = Base64.encodeBase64(jsonObject.toString().getBytes(cs));
            baseParam = new String(bytes, cs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SocketClient.send(baseParam);
        LOGGER.info("发送成功:{}");
        int i = 0;
        String cacheable = "";
        int sleepTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_SLEEPTIME));
        int maxTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_MAXTIME));
        Jedis jedis = jedisPool.getResource();
        try {
            do {
                Thread.sleep(sleepTime);
                cacheable = jedis.get(messageId);
                i++;
//                LOGGER.info("i:{},cacheable:{}", i, cacheable);
            } while (StringUtils.isBlank(cacheable) && i < maxTime / sleepTime);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return cacheable;
    }

    @RequestMapping("/getData")
    @ResponseBody
    public RestResponse getData() {
        LOGGER.info("测试mybatis:{}");
        String message = messageCodeUtil.getMessage(99103);
        int sleepTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_SLEEPTIME));
        int maxTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_MAXTIME));
        LOGGER.info("sleepTime:{},maxTime:{}", maxTime, sleepTime);
        return RestResponse.success().setData(message);
    }


    @RequestMapping("/getExport")
    public String getExport() {
        return "exportList";
    }

    @RequestMapping("/export")
    @ResponseBody
    public RestResponse export() {
        LOGGER.info("请求时间统计:{}");
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys(CodeUtil.TIME_JEDIS_PREFIX + "*");
        List<Map<String, String>> maps = Lists.newArrayList();
        for (String key : keys) {
            Map<String, String> map = jedis.hgetAll(key);
            map.put("key", key);
            maps.add(map);
        }
        return RestResponse.success().setData(maps).setTotal(Long.valueOf(maps.size())).setPage(1);
    }
}
