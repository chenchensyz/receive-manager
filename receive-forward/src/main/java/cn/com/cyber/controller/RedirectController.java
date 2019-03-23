package cn.com.cyber.controller;

import cn.com.cyber.client.NettyClient;
import cn.com.cyber.runnable.MyThreadPool;
import cn.com.cyber.runnable.WorkerThread;
import cn.com.cyber.util.CodeEnv;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpConnection;
import cn.com.cyber.util.RestResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/redirect")
public class RedirectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectController.class);

    @Autowired
    private CodeEnv codeEnv;

    @Autowired
    private JedisPool jedisPool;

    @Value("${server.port}")
    private String projectPort;

    //请求转发
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String redirect(HttpServletRequest request, @RequestBody(required = false) String jsonData) {
        String messageId = projectPort + ":" + HttpConnection.getUUID();
        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        if (StringUtils.isBlank(appKey)) {
            return JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_APPKEY_NULL, codeEnv.getMessage(CodeUtil.REQUEST_APPKEY_NULL)));
        }
        if (StringUtils.isBlank(serviceKey)) {
            return JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_SERVICEKEY_NULL, codeEnv.getMessage(CodeUtil.REQUEST_SERVICEKEY_NULL)));
        }
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isNotBlank(jsonData)) {
            jsonObject = JSONObject.parseObject(jsonData);
        }
        jsonObject.put("messageId", messageId);
        jsonObject.put("requestUrl", "https://www.cnblogs.com");
        jsonObject.put("method", "POST");
        jsonObject.put("contentType", "application/json");
        //发送请求
        String baseParam = "";
        try {
            baseParam = new String(Base64.encodeBase64(jsonObject.toString().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MyThreadPool.getThreadPool().execute(new WorkerThread(baseParam));
        int i = 0;
        String cacheable = "";
        int sleepTime = codeEnv.getRequest_sleeptime();
        int maxTime = codeEnv.getRequest_maxtime();
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
        if (StringUtils.isBlank(cacheable)) {
            return JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_TIMEOUT, codeEnv.getMessage(CodeUtil.REQUEST_TIMEOUT)));
        }
//        LOGGER.info("本次请求结束 cacheable:{}", cacheable.length());
        return cacheable;
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
        String messageId = projectPort + ":" + HttpConnection.getUUID();
        jsonObject.put("messageId", messageId);
        jsonObject.put("type", "test");
        LOGGER.info("测试通信jsonObject:{}", jsonObject);
        String baseParam = "";
        try {
            baseParam = new String(Base64.encodeBase64(jsonObject.toString().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MyThreadPool.getThreadPool().execute(new NettyClient(codeEnv.getSocket_server_port(), codeEnv.getSocket_url(), baseParam));
        LOGGER.info("发送成功:{}");
        int i = 0;
        String cacheable = "";
        int sleepTime = codeEnv.getRequest_sleeptime();
        int maxTime = codeEnv.getRequest_maxtime();
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
}
