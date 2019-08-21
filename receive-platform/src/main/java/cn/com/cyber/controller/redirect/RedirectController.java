package cn.com.cyber.controller.redirect;


import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.ReceiveLog;
import cn.com.cyber.service.ReceiveLogService;
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
import org.springframework.core.env.Environment;
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
    private Environment env;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private ReceiveLogService receiveLogService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String redirect(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData) {
        ReceiveLog receiveLog = new ReceiveLog(); //日志
        receiveLog.setRequestTime(new Date());
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        String appKey = jsonObject.getString("appKey");
        String serviceKey = jsonObject.getString("serviceKey");
        int msgCode;
        String result = "";
        try {
            //appKey,serviceKey
            AppService appService = valiedParams(appKey, serviceKey);
            //appKey,serviceKey写入日志
            receiveLog.setAppKey(appKey);
            receiveLog.setServiceKey(serviceKey);
            //发送请求
            Map<String, Object> resultMap = HttpConnection.httpRequest(appService.getUrlSuffix(), appService.getMethod(), appService.getContentType(), jsonData, null, null);
            receiveLog.setResponseTime(new Date());
            if (resultMap.get("code") != null) {
                if (CodeUtil.HTTP_OK != (Integer) resultMap.get("code")) {
                    receiveLog.setRemark(resultMap.get("result").toString());
                }
                result = resultMap.get("result").toString();
                receiveLog.setResponseCode((Integer) resultMap.get("code"));
            }
            receiveLogService.saveReceiveLog(receiveLog); //保存日志
            response.setStatus((Integer) resultMap.get("code"));
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
            result = JSON.toJSONString(RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)));
        }
        LOGGER.info("本次请求结束 result:{}", result);
        return result;
    }

    @RequestMapping("/getTest")
    @ResponseBody
    public RestResponse getTest() {
        LOGGER.info("测试连接");
        return RestResponse.success().setData("Hello World");
    }
}
