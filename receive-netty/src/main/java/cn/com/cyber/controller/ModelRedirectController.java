package cn.com.cyber.controller;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpConnection;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/modelRedirect")
public class ModelRedirectController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRedirectController.class);

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping
    @ResponseBody
    public String redirect(@RequestBody String jsonData) {
        LOGGER.info("接收二类网请求：{}", jsonData);
        JSONObject json = JSONObject.parseObject(jsonData);

        String params = getString(json, "params");
        String requestUrl = getString(json, "requestUrl");
        String method = getString(json, "method");
        String contentType = getString(json, "contentType");
        String responseType = json.getString("responseType");
        String serviceHeader = getString(json, "serviceHeader");
        LOGGER.info("接收请求json：{}", json);
        String result = "";
        try {
            if (StringUtils.isNoneBlank(requestUrl, method)) {
                LOGGER.info("requestUrl:{} , method:{} , contentType:{}", requestUrl, method, contentType);
                if (StringUtils.isNotBlank(params)) {
                    String paramsString = params;
                    Map<String, Object> paramMap = (Map<String, Object>) JSONObject.parseObject(paramsString);
                    params = HttpConnection.newParams(paramMap, params, method, contentType, requestUrl);
                }
                //请求http接口
                LOGGER.info("请求内网参数：{}", params);
                Map<String, Object> resultMap = HttpConnection.httpRequest(requestUrl, method, contentType, params, responseType, serviceHeader);
                if (resultMap.get("code") != null && CodeUtil.HTTP_OK == (Integer) resultMap.get("code")) {
                    result = resultMap.get("result").toString();
                } else {
                    result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, messageCodeUtil.getMessage(CodeUtil.REQUEST_USE_FILED) + resultMap.get("error")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, messageCodeUtil.getMessage(CodeUtil.REQUEST_USE_FILED)));
            return result;
        }
        return result;
    }

    @RequestMapping("/getTest")
    @ResponseBody
    public RestResponse getTest() {
        LOGGER.info("测试连接");
        return RestResponse.success().setData("Hello World");
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(new String[]{"223"}));
    }

}
