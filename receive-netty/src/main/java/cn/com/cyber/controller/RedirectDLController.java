package cn.com.cyber.controller;
/**
 * 大连http版本，appkey及service无法通过header传输，放在body中传输
 */

import cn.com.cyber.util.*;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
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
@RequestMapping("/redirect_dl")
public class RedirectDLController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectDLController.class);

    @Autowired
    private Environment env;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    //大连服务
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String redirect(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData) {
        LOGGER.error("请求开始:{}", jsonData);
        String result = "";
        int msgCode = CodeUtil.BASE_SUCCESS;
        //组装返回结果
        JSONObject jsonResult = new JSONObject();
        ReceiveDL receiveDL = new ReceiveDL();
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
//            LOGGER.error("jsonObject转化后:{}", jsonObject);
            JSONArray parameter = jsonObject.getJSONArray("parameter");  //parameter中为实际用到参数
            JSONObject jsonValied = parameter.getJSONObject(0);
            String appKey = jsonValied.getString("appKey");
            String serviceKey = jsonValied.getString("serviceKey");
            if (StringUtils.isBlank(appKey)) {
                appKey = jsonValied.getString("appkey");
            }
            if (StringUtils.isBlank(serviceKey)) {
                serviceKey = jsonValied.getString("servicekey");
            }

            String serviceHeader = jsonValied.getString("serviceHeader"); //应用头消息
            String serviceUrl = jsonValied.getString("serviceUrl");

            String validToken = env.getProperty(CodeUtil.VALID_TOKEN);
            if (Boolean.valueOf(validToken)) {
                validToken(request, response);//校验token
            }
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("params", jsonValied.getString("params"));
            if (StringUtils.isNotBlank(serviceHeader)) {
                jsonParam.put("serviceHeader", serviceHeader);
            }

            String url = env.getProperty(CodeUtil.PLATFORM_URL);
            //测试用
            if (serviceUrl != null && "getTest".equals(serviceUrl)) {
                url += "/getTest";
            }
            Map<String, String> paramHeader = Maps.newHashMap();
            paramHeader.put("appKey", appKey);
            paramHeader.put("serviceKey", serviceKey);
            ResultData resultData = HttpConnection.httpRequest(url, CodeUtil.RESPONSE_POST, CodeUtil.CONTEXT_JSON, jsonParam.toString(), null, paramHeader);
            if (resultData.getCode() != null) {
                result = resultData.getResult();
            }
            response.setStatus(resultData.getCode());
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        } catch (Exception e) {
            msgCode = CodeUtil.REQUEST_CONN_FILED;
            LOGGER.error(e.getMessage(), e);
        }
        Map<String, Object> contentMap = Maps.newHashMap();
        if (msgCode != CodeUtil.BASE_SUCCESS) {
            contentMap.put("content", RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)));
        } else {
            contentMap.put("content", result);
        }
        receiveDL.getData().add(contentMap);
        Object o = JSONObject.toJSON(receiveDL);
        jsonResult.put("result", o);
        result = jsonResult.toString();
        LOGGER.info("本次请求结束 :{}", result);
        return result;
    }
}
