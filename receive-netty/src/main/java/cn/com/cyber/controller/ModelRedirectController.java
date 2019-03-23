package cn.com.cyber.controller;

import cn.com.cyber.util.CodeEnv;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpConnection;
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

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/modelRedirect")
public class ModelRedirectController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRedirectController.class);

    @Autowired
    private CodeEnv codeEnv;

    @RequestMapping
    @ResponseBody
    public String redirect(@RequestBody String jsonData) {
        LOGGER.info("接收二类网请求：{}", jsonData);
        JSONObject json = JSONObject.parseObject(jsonData);

        String params = getString(json, "params");
        String requestUrl = getString(json, "requestUrl");
        String method = getString(json, "method");
        String contentType = getString(json, "contentType");
        LOGGER.info("接收请求json.length:{}", json.toString().length());
        String result = "";
        try {
            if (StringUtils.isNoneBlank(requestUrl, method)) {
                LOGGER.info("requestUrl:{} , method:{} , contentType:{}", requestUrl, method, contentType);
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
                //请求http接口
                Map<String, Object> resultMap = HttpConnection.httpRequest(requestUrl, method, contentType, params, null);
                if (resultMap.get("code") != null) {
                    if (CodeUtil.HTTP_OK == (Integer) resultMap.get("code")) {
                        result = resultMap.get("result").toString();
                    } else {
                        result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, codeEnv.getMessage(CodeUtil.REQUEST_USE_FILED) + resultMap.get("code"))).toString();
                    }
                } else {
                    result = resultMap.get("error").toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, codeEnv.getMessage(CodeUtil.REQUEST_USE_FILED))).toString();
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
        System.out.println(Arrays.toString(new String[]{"223"}) );
    }

}
