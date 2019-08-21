package cn.com.cyber.controller.redirect;
/**
 * 贵阳服务
 */

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpClient;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/redirect/gy")
public class RedirectGYController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectGYController.class);

    @Autowired
    private Environment env;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void redirect(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String jsonData) {

        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        String serviceUrl = request.getHeader("serviceUrl");
        int msgCode;
        String result = "";
        try {
            //appKey,serviceKey
            JSONObject jsonObject = new JSONObject();
            if (StringUtils.isNotBlank(jsonData)) {
                jsonObject = JSONObject.parseObject(jsonData);
            }
            jsonObject.put("appKey", appKey);
            jsonObject.put("serviceKey", serviceKey);
            //发送请求
            String url = "http://" + env.getProperty(CodeUtil.SOCKET_URL) + ":" + env.getProperty(CodeUtil.SOCKET_PORT) + CodeUtil.MODEL_REQUSET_URL;
            //测试用
            if (serviceUrl != null && "getTest".equals(serviceUrl)) {
                url += "/getTest";
            }
            Map<String, Object> resultMap = HttpClient.httpRequest(url, CodeUtil.METHOD_POST, CodeUtil.CONTEXT_JSON, jsonObject.toString());
            if (resultMap.get("code") != null && CodeUtil.HTTP_OK == (Integer) resultMap.get("code")) {
                result = resultMap.get("result").toString();
            } else {
                result = resultMap.get("error").toString();
            }
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
            result = JSON.toJSONString(RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)));
        }
        LOGGER.info("本次请求结束 result:{}", result);
        setResponseText(response, result);
    }
}
