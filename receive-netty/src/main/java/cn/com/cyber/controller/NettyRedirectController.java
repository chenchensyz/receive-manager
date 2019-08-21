package cn.com.cyber.controller;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpConnection;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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


    //武汉服务
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void redirect(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String jsonData) {
        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        String serviceUrl = request.getHeader("serviceUrl");
        int msgCode;
        String result = "";
        try {
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
            jsonObject.put("appKey", appKey);
            jsonObject.put("serviceKey", serviceKey);
            Map<String, Object> resultMap = HttpConnection.httpRequest(url, CodeUtil.RESPONSE_POST, CodeUtil.CONTEXT_JSON, jsonObject.toString(), null, null);
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

    @RequestMapping("/getTest")
    @ResponseBody
    public RestResponse getTest() {
        LOGGER.info("测试连接");
        return RestResponse.success().setData("Hello World");
    }

}
