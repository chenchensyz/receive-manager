package cn.com.cyber.controller;

import cn.com.cyber.socket.SocketClient;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpConnection;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static cn.com.cyber.util.CodeUtil.cs;

/**
 * tcp版本
 */

@Controller
@ConditionalOnExpression("'${redirect_type}'.equals('tcp')")
@RequestMapping("/redirect")
public class RedirectTcpController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectController.class);

    @Autowired
    private Environment env;

    @Autowired
    private JedisPool jedisPool;

    @Value("${server.port}")
    private String projectPort;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    //请求转发-tcp
    @RequestMapping(method = RequestMethod.POST)
    public void redirect(HttpServletRequest request, HttpServletResponse response,
                         @RequestBody(required = false) String jsonData) {
        String messageId = projectPort + ":" + HttpConnection.getUUID();
        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        if (StringUtils.isBlank(appKey)) {
            appKey = request.getHeader("appkey");
        }
        if (StringUtils.isBlank(serviceKey)) {
            serviceKey = request.getHeader("servicekey");
        }
        String responseType = request.getHeader("responseType");
        int msgCode;
        Jedis jedis = null;
        try {
            String validToken = env.getProperty(CodeUtil.VALID_TOKEN);
            if (Boolean.valueOf(validToken)) {
                validToken(request, response);//校验token
            }
            JSONObject jsonObject = new JSONObject();
            if (StringUtils.isNotBlank(jsonData)) {
                jsonObject = JSONObject.parseObject(jsonData);
            }
            jsonObject.put("messageId", messageId);
            jsonObject.put("appKey", appKey);
            jsonObject.put("serviceKey", serviceKey);
            if (StringUtils.isNotBlank(responseType)) {
                jsonObject.put("responseType", responseType);
            }
            //发送请求
            String baseParam = "";
            try {
                byte[] bytes = Base64.encodeBase64(jsonObject.toString().getBytes(cs));
                baseParam = new String(bytes, cs);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
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
                    responseOutWithJson(response, cacheable);  //返回json文本
                } else {
                    byte[] resultbytes = Base64.decodeBase64(result.getString("responseData").getBytes(cs));
                    setResponseFile(response, resultbytes, result.getString("responseContent")); //输出文件流
                }
            } else {
                if (ifJson(cacheable)) {
                    responseOutWithJson(response, cacheable);   //返回json
                } else {
                    setResponseText(response, cacheable); //返回text
                }
            }
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
            responseOutWithJson(response, JSON.toJSONString(RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode))));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


}
