package cn.com.cyber.controller.redirect;

/**
 * http版本
 */

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.model.ReceiveLog;
import cn.com.cyber.service.ReceiveLogService;
import cn.com.cyber.util.*;
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
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/redirect/wh")
public class RedirectWHController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectWHController.class);

    @Autowired
    private Environment env;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private ReceiveLogService receiveLogService;

    @Autowired
    private JedisPool jedisPool;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String redirect(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String jsonData) {
        ReceiveLog receiveLog = new ReceiveLog(); //日志
        receiveLog.setRequestTime(new Date());
        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        int msgCode;
        String result = "";
        try {
            //appKey,serviceKey
            AppModel appModel = valiedParams(appKey, serviceKey);
            //appKey,serviceKey写入日志
            receiveLog.setAppKey(appKey);
            receiveLog.setServiceKey(serviceKey);

            String params = null;
            if (StringUtils.isNotBlank(jsonData)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonData);
                params = jsonObject.getString("params");
            }
            //发送请求
            LOGGER.info("params:{}", params);
            Map<String, Object> resultMap = HttpConnection.requestNewParams(params, appModel.getMethod(), appModel.getContentType(), appModel.getUrlSuffix(), null);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("本次请求结束 result:{}", result);
        return result;
    }


    @RequestMapping(value = "/test", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String test() {
        LOGGER.info("测试:{}");
        String url = "http://" + env.getProperty(CodeUtil.SOCKET_URL) + ":" + env.getProperty(CodeUtil.SOCKET_PORT) + CodeUtil.MODEL_REQUSET_URL;
        //测试用
        url += "/getTest";
        Map<String, Object> resultMap = HttpClient.httpRequest(url, CodeUtil.METHOD_POST, CodeUtil.CONTEXT_JSON, null);
        String result;
        if ((Boolean) resultMap.get("flag")) {
            if (resultMap.get("result") == null) {
                result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, messageCodeUtil.getMessage(CodeUtil.REQUEST_USE_FILED)));
            } else {
                result = resultMap.get("result").toString();
            }
        } else {
            result = resultMap.get("error").toString();
        }
        LOGGER.info("本次请求结束 result:{}", result.length());
        return result;
    }


}

