package cn.com.cyber.netty;

/**
 * 消息处理--线程池
 */

import cn.com.cyber.socket.SocketClient;
import cn.com.cyber.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

public class WorkerThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    private String command;

    public WorkerThread(String s) {
        this.command = s;
    }

    @Override
    public void run() {
        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Environment env = SpringUtil.getBean(Environment.class);
        Jedis jedis = jedisPool.getResource();
        try {
            JSONObject json = JSONObject.parseObject(command);
            String messageId = json.getString("messageId");

            //移动网接到互联网请求时间-------
            Map<String, String> map = Maps.newHashMap();
            if (messageId.startsWith("testTime:")) {
                LOGGER.info("移动网接收command：{}", command);
                LOGGER.info("移动网转换后json：{}", json);
                map.put("mobileReceiveTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));
                jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
            }
            //---------------------

            MessageCodeUtil messageCodeUtil = SpringUtil.getBean(MessageCodeUtil.class);
            String result = JSON.toJSONString(RestResponse.res(1, "请检查应用接口配置是否完整"));
            String params = json.getString("params");
            String appKey = json.getString("appKey");
            String serviceKey = json.getString("serviceKey");
//            String method = json.getString("method");
//            String contentType = json.getString("contentType");
            String responseType = json.getString("responseType");
//            String serviceHeader = json.getString("serviceHeader");
//            LOGGER.info("接收请求json.length:{}", json.toString().length());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("messageId", messageId);
            if (StringUtils.isNoneBlank(appKey, serviceKey)) {
//                LOGGER.info("requestUrl:{} , method:{} , contentType:{}", requestUrl, method, contentType);
//                LOGGER.info("requestUrl:{} , method:{} , contentType:{}", requestUrl, method, contentType);
                //-----------------------
                if (messageId.startsWith("testTime:")) {
                    map = jedis.hgetAll(CodeUtil.TIME_JEDIS_PREFIX + messageId);
                    map.put("mobileRequsetTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));
                    jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
                    LOGGER.info("移动网发送内网：{}", params);
                }
                //--------------------------
                //请求http接口
                String url = env.getProperty(CodeUtil.PLATFORM_URL);
                Map<String, String> paramHeader = Maps.newHashMap();
                paramHeader.put("appKey", appKey);
                paramHeader.put("serviceKey", serviceKey);
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("params", params);
                LOGGER.info("移动网发送内网appKey:{},serviceKey:{},jsonParams:{}", appKey, serviceKey, jsonParams);
                ResultData resultData = HttpConnection.httpRequest(url, CodeUtil.RESPONSE_POST, CodeUtil.CONTEXT_JSON, jsonParams.toString(), responseType, paramHeader);

                //-----------------------
                if (messageId.startsWith("testTime:")) {
                    map = jedis.hgetAll(CodeUtil.TIME_JEDIS_PREFIX + messageId);
                    map.put("mobileResponseTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));
                    jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
                }
                //--------------------------
                if (resultData.getCode() != null && CodeUtil.HTTP_OK == resultData.getCode()) {
                    result = resultData.getResult();
                } else {
                    result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, messageCodeUtil.getMessage(CodeUtil.REQUEST_USE_FILED) + resultData.getResult()));
                    //---------------------
                    if (messageId.startsWith("testTime:")) {
                        map = jedis.hgetAll(CodeUtil.TIME_JEDIS_PREFIX + messageId);
                        map.put("error", result);
                        jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
                    }
                    //---------------------
                }
            }
            jsonObject.put("params", result);
            //---------------------
            if (messageId.startsWith("testTime:")) {
                map = jedis.hgetAll(CodeUtil.TIME_JEDIS_PREFIX + messageId);
                map.put("mobileFrwardTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));
                jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
                jsonObject.put("testExport", map);
            }
            //---------------------
            byte[] bytes = Base64.encodeBase64(jsonObject.toString().getBytes(CodeUtil.cs));
            String baseResult = new String(bytes, CodeUtil.cs);
            SocketClient.send(baseResult);
        } catch (Exception e) {
            LOGGER.error("msg:{}", e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public String toString() {
        return this.command;
    }

}
