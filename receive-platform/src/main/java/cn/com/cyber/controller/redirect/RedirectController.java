package cn.com.cyber.controller.redirect;


import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.ReceiveLog;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.service.ReceiveLogService;
import cn.com.cyber.util.*;
import cn.com.cyber.util.common.RestResponse;
import cn.com.cyber.util.common.ResultData;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
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
        int msgCode;
        String result = "";
        try {
            //appKey,serviceKey
            AppService appService = validParams(request);
            //appKey,serviceKey写入日志
            receiveLog.setAppKey(appService.getAppKey());
            receiveLog.setServiceKey(appService.getServiceKey());

            String params = null;
            Map<String, String> serviceHeader = null;
            String serviceSuffix = null;

            if (StringUtils.isNotBlank(jsonData)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonData);
                params = jsonObject.getString("params");
                serviceHeader = JSON.parseObject(jsonObject.getString("serviceHeader"), new TypeReference<Map<String, String>>() {
                });
                serviceSuffix = jsonObject.getString("serviceSuffix");
            }
            String realUrl = appService.getUrlSuffix();
            if (serviceSuffix != null) {  //get请求地址栏拼接
                realUrl += serviceSuffix;
            }
            //发送请求
//            LOGGER.info("params:{}", params);
            ResultData resultData = HttpConnection.requestNewParams(params, appService.getMethod(), appService.getContentType(), realUrl, serviceHeader);
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
//        LOGGER.info("本次请求结束 result:{}", result);
        return result;
    }

    //校验appkey和servicekey是否正确
    @RequestMapping("/validAppAndService")
    @ResponseBody
    public RestResponse validAppAndService(@RequestBody String jsonData) {
        int code = CodeUtil.BASE_SUCCESS;
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        String appKey = jsonObject.getString("appKey");
        String serviceKey = jsonObject.getString("serviceKey");
        AppService service = appServiceService.getByAppKeyAndServiceKey(appKey, serviceKey);
        if (service == null) {
            code = CodeUtil.REQUEST_KEY_FILED;
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(service.getUrlSuffix());
    }

    @RequestMapping("/fileUp")
    @ResponseBody
    public RestResponse multifileUpload(HttpServletRequest request,
                                        @RequestParam("appKey") String appKey,
                                        @RequestParam("serviceKey") String serviceKey,
                                        @RequestParam("introduction") String introduction, long size) {
        LOGGER.info("文件传输，接收appKey:{},serviceKey:{},introduction:{}", appKey, serviceKey, introduction);
        int msgCode = CodeUtil.BASE_FILE_ERR_UP;

        AppService service = appServiceService.getByAppKeyAndServiceKey(appKey, serviceKey);
        if (service == null) {
            msgCode = CodeUtil.REQUEST_KEY_FILED;
            return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
        }

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        for (MultipartFile file : files) {
//            String fileName = file.getOriginalFilename();
//            String suffix = fileName.substring(fileName.lastIndexOf("."));
//            String uuid = UUID.randomUUID().toString();
//            String filePath = env.getProperty(CodeUtil.FILE_SAVE_PATH) + File.separator + uuid + suffix;
//            File dest = new File(filePath);
//            if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
//                dest.getParentFile().mkdir();
//            }

            try {
//                file.transferTo(dest);

                Map<String, MultipartFile> fileMap = Maps.newHashMap();
                Map<String, String> requestParamsMap = Maps.newHashMap();
                requestParamsMap.put("introduction", introduction);
                requestParamsMap.put("size", size + "");

                fileMap.put(file.getOriginalFilename(), file);
                ResultData resultData = FileUpConnection.postFileUp(service.getUrlSuffix(), requestParamsMap, fileMap);
                if (resultData != null && CodeUtil.HTTP_OK == resultData.getCode()) {
                    JSONObject object = JSONObject.parseObject(resultData.getResult());
                    LOGGER.info("请求内网返回值,object:{}", object);
//                    if (object != null && object.get("success") != null && (Boolean) object.get("success")) {
                    msgCode = CodeUtil.BASE_SUCCESS;
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping("/fileDown")
    public void fileDown(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
            String serviceSuffix = jsonObject.getString("serviceSuffix");
            if (StringUtils.isBlank(serviceSuffix)) {  //没有后缀，路径不完整
                throw new ValueRuntimeException(CodeUtil.BASE_FILE_PATH_NULL);
            }
            AppService appService = validParams(request);
            String url = appService.getUrlSuffix() + serviceSuffix;  //文件地址
            byte[] fileBytes = FileUtil.getFileBytes(url);
            String fileName = serviceSuffix.substring(serviceSuffix.lastIndexOf("/") + 1);
            FileUtil.setResponseBytes(response, fileName, fileBytes);
            LOGGER.info("发送完毕");

        } catch (ValueRuntimeException e) {
            int code = (Integer) e.getValue();
            setResponseJson(response, JSON.toJSONString(RestResponse.res(code, messageCodeUtil.getMessage(code))));
        }
    }


    @RequestMapping("/getTest")
    @ResponseBody
    public RestResponse getTest() {
        LOGGER.info("测试连接");
        return RestResponse.success().setData("Hello World");
    }
}
