package cn.com.cyber.runnable;

import cn.com.cyber.socket.SocketClient;
import cn.com.cyber.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.URLEncoder;
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
        Jedis jedis = jedisPool.getResource();
        try {
            JSONObject json = JSONObject.parseObject(command);
            String messageId = json.getString("messageId");

            //移动网接到互联网请求时间-------
            Map<String, String> map = Maps.newHashMap();
            if (messageId.startsWith("testTime:")) {
                map.put("mobileReceiveTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));
                jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
            }
            //---------------------

            CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
            String result = JSON.toJSONString(RestResponse.res(1, "请检查应用接口配置是否完整")).toString();
            String params = getString(json, "params");
            String requestUrl = getString(json, "requestUrl");
            String method = getString(json, "method");
            String contentType = getString(json, "contentType");
            String responseType = getString(json, "responseType");
//            LOGGER.info("接收请求json.length:{}", json.toString().length());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("messageId", messageId);
            if (StringUtils.isNoneBlank(requestUrl, method)) {
//                LOGGER.info("requestUrl:{} , method:{} , contentType:{}", requestUrl, method, contentType);
                if (StringUtils.isNotBlank(params)) {
                    String paramsString = params;
                    params = "";
                    Map<String, Object> paramMap = (Map<String, Object>) JSONObject.parseObject(paramsString);
                    if (CodeUtil.RESPONSE_POST.equals(method) && !CodeUtil.CONTEXT_JSON.equals(contentType)) {
                        int i = 1;
                        for (String key : paramMap.keySet()) {
                            String value = paramMap.get(key).toString();
                            params += key + "=" + URLEncoder.encode(value, "UTF-8");
                            if (i < paramMap.size()) {
                                params += "&";
                            }
                            i++;
                        }
                    } else if (CodeUtil.RESPONSE_GET.equals(method)) {
                        if (requestUrl.contains("{")) { //拼在地址栏
                            for (String key : paramMap.keySet()) {
                                String value = paramMap.get(key).toString();
                                String replace = requestUrl.replace("{" + key + "}", value);
                                requestUrl = replace;
                            }
                        } else {
                            requestUrl = requestUrl + "?";
                            int i = 1;
                            for (String key : paramMap.keySet()) {
                                String value = paramMap.get(key).toString();
                                requestUrl += key + "=" + URLEncoder.encode(value, "UTF-8");
                                if (i < paramMap.size()) {
                                    requestUrl += "&";
                                }
                                i++;
                            }
                        }
                    }
                }

//                LOGGER.info("requestUrl:{} , method:{} , contentType:{}", requestUrl, method, contentType);

                //-----------------------
                if (messageId.startsWith("testTime:")) {
                    map = jedis.hgetAll(CodeUtil.TIME_JEDIS_PREFIX + messageId);
                    map.put("mobileRequsetTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));
                    jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
                }
                //--------------------------

                //请求http接口
                Map<String, Object> resultMap = HttpConnection.httpRequest(requestUrl, method, contentType, params, responseType);

                //-----------------------
                if (messageId.startsWith("testTime:")) {
                    map = jedis.hgetAll(CodeUtil.TIME_JEDIS_PREFIX + messageId);
                    map.put("mobileResponseTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));
                    jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
                }
                //--------------------------

                if (resultMap.get("code") != null) {
                    if (CodeUtil.HTTP_OK == (Integer) resultMap.get("code")) {
                        result = resultMap.get("result").toString();
                    } else {
                        result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, codeEnv.getMessage(CodeUtil.REQUEST_USE_FILED) + resultMap.get("code"))).toString();
                    }
                } else {
                    result = resultMap.get("error").toString();
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


    public String getString(JSONObject json, String key) {
        Object value = json.get(key);

        if (value == null) {
            return "";
        }

        return value.toString();
    }

    public static void main(String[] args) {
        String url = "http://10.48.3.189:9982/pmmanage/api/pmuser/photo/{userId}/{uww}";
        String nu = url.replace("{userId}", "1111");
        url = nu;
        String uww = url.replace("{uww}", "2222");
        System.out.println(uww);
    }

}
