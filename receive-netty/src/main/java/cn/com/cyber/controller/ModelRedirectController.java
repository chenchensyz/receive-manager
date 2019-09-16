package cn.com.cyber.controller;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpConnection;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
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
    public String redirect(HttpServletResponse response, @RequestBody String jsonData) {
        LOGGER.info("接收二类网请求：{}", jsonData);
        JSONObject json = JSONObject.parseObject(jsonData);

        String params = json.getString("params");
        String requestUrl = json.getString("requestUrl");
        String method = json.getString("method");
        String contentType = json.getString("contentType");
        String responseType = json.getString("responseType");
        Map<String, String> serviceHeader = JSON.parseObject(json.getString("serviceHeader"), new TypeReference<Map<String, String>>() {
        });
        LOGGER.info("接收请求json：{}", json);
        String result = "";
        try {
            if (StringUtils.isNoneBlank(requestUrl, method)) {
                Map<String, Object> resultMap = HttpConnection.requestNewParams(params, method, contentType, requestUrl, serviceHeader);
                if (resultMap.get("code") != null) {
                    result = resultMap.get("result").toString();
                }
                response.setStatus((Integer) resultMap.get("code"));
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
