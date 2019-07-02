package cn.com.cyber.controller.redirect;
/**
 * tcp版本
 */

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.service.AppInfoService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.com.cyber.util.CodeUtil.cs;

@Controller
@RequestMapping("/redirect")
public class RedirectController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectController.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private JedisPool jedisPool;

    @Value("${server.port}")
    private String projectPort;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    //请求转发
    @RequestMapping(method = RequestMethod.POST)
    public void redirect(HttpServletResponse response, HttpServletRequest request,
                         @RequestBody(required = false) String jsonData) {
        String messageId = projectPort + ":" + HttpClient.getUUID();
        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        String responseType = request.getHeader("responseType");
        int msgCode;
        Jedis jedis = null;
        try {
            AppModel appModel = valiedParams(appKey, serviceKey);
            JSONObject jsonObject = new JSONObject();
            if (StringUtils.isNotBlank(jsonData)) {
                jsonObject = JSONObject.parseObject(jsonData);
            }
            jsonObject.put("messageId", messageId);
            jsonObject.put("requestUrl", appModel.getUrlPrefix() + appModel.getUrlSuffix());
            jsonObject.put("method", appModel.getMethod());
            jsonObject.put("contentType", appModel.getContentType());
            if (StringUtils.isNotBlank(responseType)) {
                jsonObject.put("responseType", responseType);
            }
            //发送请求
            String baseParam = "";
            try {
                byte[] bytes = Base64.encodeBase64(jsonObject.toString().getBytes(cs));
                baseParam = new String(bytes, cs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SocketClient.send(baseParam);
            int i = 0;
            String cacheable = "";
            int sleepTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_SLEEPTIME));
            int maxTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_MAXTIME));
            jedis = jedisPool.getResource();
            do {
                Thread.sleep(sleepTime);
                cacheable = jedis.get(messageId);
                i++;
//                LOGGER.info("i:{},cacheable:{}", i, cacheable);
            } while (StringUtils.isBlank(cacheable) && i < maxTime / sleepTime);

            if (StringUtils.isBlank(cacheable)) {
                msgCode = CodeUtil.REQUEST_TIMEOUT;
                throw new ValueRuntimeException(msgCode);
            }
//        LOGGER.info("本次请求结束 cacheable:{}", cacheable.length());
            if (StringUtils.isNotBlank(responseType) && CodeUtil.RESPONSE_FILE_TYPE.equals(responseType)) {
                JSONObject result = JSONObject.parseObject(cacheable);
                if (StringUtils.isBlank(result.getString("responseData"))) {
                    setResponseText(response, cacheable);  //返回json文本
                } else {
                    byte[] resultbytes = Base64.decodeBase64(result.getString("responseData").getBytes(cs));
                    setResponseFile(response, resultbytes, result.getString("responseContent")); //输出文件流
                }
            } else {
                setResponseText(response, cacheable);
            }
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
            response.setStatus(500);
            setResponseText(response, JSON.toJSONString(RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode))));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    //请求转发
    @RequestMapping("/redirectTest")
    public void redirectTest(HttpServletResponse response, HttpServletRequest request,
                            @RequestBody(required = false) String jsonData) {
        Map<String, String> map = Maps.newHashMap();
        map.put("interReceiveTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));//接收请求时间

        String messageId = "testTime:" + HttpClient.getUUID();
        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        String responseType = request.getHeader("responseType");
        LOGGER.info("appKey:{},serviceKey:{}", appKey, serviceKey);
        Jedis jedis = jedisPool.getResource();
        LOGGER.info("jedis获取:{}", CodeUtil.TIME_JEDIS_PREFIX + messageId);
        int msgCode;
        try {
            AppModel appModel = valiedParams(appKey, serviceKey);
            //---测试超时保存时间----------
            jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
            //-----------------------------
            LOGGER.info("jedis保存：{}", map);
            JSONObject jsonObject = new JSONObject();
            if (StringUtils.isNotBlank(jsonData)) {
                jsonObject = JSONObject.parseObject(jsonData);
            }
            jsonObject.put("messageId", messageId);
            jsonObject.put("requestUrl", appModel.getUrlPrefix() + appModel.getUrlSuffix());
            jsonObject.put("method", appModel.getMethod());
            jsonObject.put("contentType", appModel.getContentType());
            if (StringUtils.isNotBlank(responseType)) {
                jsonObject.put("responseType", responseType);
            }
            //发送请求
            LOGGER.info("发送请求{}");
            String baseParam = "";
            try {
                byte[] bytes = Base64.encodeBase64(jsonObject.toString().getBytes(cs));
                baseParam = new String(bytes, cs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SocketClient.send(baseParam);

            //---测试超时保存时间----------
            map = jedis.hgetAll(CodeUtil.TIME_JEDIS_PREFIX + messageId);
            map.put("interForwardTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));
            jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
            //-----------------------------
            LOGGER.info("发送完毕 map:{}", map);

            int i = 0;
            String cacheable;
            int sleepTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_SLEEPTIME));
            int maxTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_MAXTIME));
            do {
                Thread.sleep(sleepTime);
                cacheable = jedis.get(messageId);
                i++;
//                LOGGER.info("i:{},cacheable:{}", i, cacheable);
            } while (StringUtils.isBlank(cacheable) && i < maxTime / sleepTime);

            if (StringUtils.isBlank(cacheable)) {
                //---测试超时保存时间----------
                map = jedis.hgetAll(CodeUtil.TIME_JEDIS_PREFIX + messageId);
                map.put("error", "请求超时");
                jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
                msgCode = CodeUtil.REQUEST_TIMEOUT;
                throw new ValueRuntimeException(msgCode);
            }

            LOGGER.info("本次请求结束 cacheable:{}", cacheable);
//        LOGGER.info("本次请求结束 cacheable:{}", cacheable.length());
            if (StringUtils.isNotBlank(responseType) && CodeUtil.RESPONSE_FILE_TYPE.equals(responseType)) {
                JSONObject result = JSONObject.parseObject(cacheable);
                if (StringUtils.isBlank(result.getString("responseData"))) {
                    setResponseText(response, cacheable);  //返回json文本
                } else {
                    byte[] resultbytes = Base64.decodeBase64(result.getString("responseData").getBytes(cs));
                    setResponseFile(response, resultbytes, result.getString("responseContent")); //输出文件流
                }
            } else {
                setResponseText(response, cacheable);
            }
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
            response.setStatus(500);
            setResponseText(response, JSON.toJSONString(RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode))));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @RequestMapping("/getIndex")
    public String getIndex(Model model) {
        LOGGER.info("跳转页面:{}");
        return "test";
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
        String messageId = projectPort + ":" + HttpClient.getUUID();
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
