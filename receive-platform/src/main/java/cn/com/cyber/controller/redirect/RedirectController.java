package cn.com.cyber.controller.redirect;


import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.ReceiveLog;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.service.ReceiveLogService;
import cn.com.cyber.util.*;
import cn.com.cyber.util.exception.ValueRuntimeException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/redirect")
public class RedirectController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectController.class);

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private ReceiveLogService receiveLogService;

    @Autowired
    private AppServiceService appServiceService;

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
            AppService appService = validParams(appKey, serviceKey);
            //appKey,serviceKey写入日志
            receiveLog.setAppKey(appKey);
            receiveLog.setServiceKey(serviceKey);

            String params = null;
            Map<String, String> serviceHeader = null;
            if (StringUtils.isNotBlank(jsonData)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonData);
                params = jsonObject.getString("params");
                serviceHeader = JSON.parseObject(jsonObject.getString("serviceHeader"), new TypeReference<Map<String, String>>() {
                });
            }
            //发送请求
            LOGGER.info("params:{}", params);
            ResultData resultData = HttpConnection.requestNewParams(params, appService.getMethod(), appService.getContentType(), appService.getUrlSuffix(), serviceHeader);
            receiveLog.setResponseTime(new Date());
            if (resultData != null && resultData.getCode() != null) {
                //日志
                receiveLog.setParams(params);
                receiveLog.setRemark(resultData.getResult());
                receiveLog.setResponseCode(resultData.getCode());
                //返回值
                result = resultData.getResult();
            }
            receiveLogService.saveReceiveLog(receiveLog); //保存日志
            response.setStatus(resultData.getCode());
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
            result = JSON.toJSONString(RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            msgCode = CodeUtil.REQUEST_SERVICE_FILED;
            result = JSON.toJSONString(RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)));
        }
        LOGGER.info("本次请求结束 result:{}", result);
        return result;
    }

    //校验appkey和servicekey是否正确
    @RequestMapping("/validAppAndService")
    @ResponseBody
    public RestResponse validAppAndService(String appKey, String serviceKey) {
        int code = CodeUtil.BASE_SUCCESS;
        AppService service = appServiceService.getByAppKeyAndServiceKey(appKey, serviceKey);
        if (service == null) {
            code = CodeUtil.REQUEST_KEY_FILED;
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(service.getUrlSuffix());
    }

    @RequestMapping("/getTest")
    @ResponseBody
    public RestResponse getTest() {
        LOGGER.info("测试连接");
        return RestResponse.success().setData("Hello World");
    }
}
