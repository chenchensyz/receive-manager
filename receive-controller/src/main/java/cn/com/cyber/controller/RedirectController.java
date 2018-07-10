package cn.com.cyber.controller;

import cn.com.cyber.AppInfoService;
import cn.com.cyber.CacheMapUtil;
import cn.com.cyber.cache.CacheModel;
import cn.com.cyber.dao.AppInfoMapper;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.socket.SocketClient;
import cn.com.cyber.socket.SpringUtil;
import cn.com.cyber.tools.HttpClient;
import cn.com.cyber.tools.RestResponse;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.ReadCodeUtil;
import cn.com.cybertech.commons.util.Base64Utils;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/redirect")
public class RedirectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectController.class);

    @Resource(name = "appInfoService")
    private AppInfoService appInfoService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String redirect(HttpServletRequest request, @RequestBody String jsonData) {
        LOGGER.info("接收请求:{}", jsonData);
        String messageId = HttpClient.getUUID() + System.currentTimeMillis();
        LOGGER.info("messageId:{}", messageId);
        String appKey = request.getHeader("appKey");
        String serviceKey = request.getHeader("serviceKey");
        String serviceUrl = request.getHeader("serviceUrl");
        if (StringUtils.isBlank(appKey)) {
            return JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_APPKEY_NULL,ReadCodeUtil.getConfig(CodeUtil.REQUEST_APPKEY_NULL)));
        }
        if (StringUtils.isBlank(serviceKey)) {
            return JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_SERVICEKEY_NULL,ReadCodeUtil.getConfig(CodeUtil.REQUEST_SERVICEKEY_NULL)));
        }
        if (StringUtils.isBlank(serviceUrl)) {
            return JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_SERVICEURL_NULL,ReadCodeUtil.getConfig(CodeUtil.REQUEST_SERVICEURL_NULL)));
        }
        JSONObject jsonObject = JSONObject.fromObject(jsonData);
        if (jsonObject == null || jsonData != null
                &&jsonObject.get("params")!=null && StringUtils.isBlank(jsonObject.getString("params"))) {
            return JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_PARAM_NULL,ReadCodeUtil.getConfig(CodeUtil.REQUEST_PARAM_NULL)));
        }
        jsonObject.put("appKey", appKey);
        jsonObject.put("serviceKey", serviceKey);
        jsonObject.put("serviceUrl", serviceUrl);
        jsonObject.put("messageId", messageId);
        //发送请求
        String baseParam ="";
        try {
            baseParam= new String(Base64Utils.encode(jsonObject.toString().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LOGGER.info("baseParam:{}", baseParam);
        SocketClient.send(CodeUtil.FORWARD_URL, CodeUtil.SOCKET_PORT, baseParam);
        int i = 0;
        String cacheable = "";
        CacheModel cacheModel = null;
        do {
            try {
                Thread.sleep(1000);
                cacheModel = CacheMapUtil.getCacheMap(messageId);
                i++;
                LOGGER.info("i:{},cacheable:{}", i, cacheable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (cacheModel == null && i < 25);
        if (cacheModel == null) {
            return JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_TIMEOUT,ReadCodeUtil.getConfig(CodeUtil.REQUEST_TIMEOUT)));
        }
        cacheable = cacheModel.getData();
        LOGGER.info("cacheable:{}", cacheable);
        CacheMapUtil.deleteCacheMap(messageId);
        return cacheable;
    }


    @RequestMapping("/getIndex")
    public String getIndex(Model model) {
        LOGGER.info("跳转页面:{}");
        return "index";
    }

    @RequestMapping("/getTest")
    @ResponseBody
    public RestResponse getTest() {
        LOGGER.info("测试连接:{}");
        return RestResponse.success().setData("连接成功");
    }

    @RequestMapping("/getInter")
    @ResponseBody
    public String getInter(@RequestParam("appKey") String appKey,
                                 @RequestParam("serviceKey") String serviceKey) {
        LOGGER.info("测试mybatis:{}");
        AppInfoService appInfos = SpringUtil.getBean(AppInfoService.class);
        AppInfo appInfo = appInfoService.getByAppAndService(appKey, serviceKey);
        AppModel appModel = appInfos.selectAppModel("getColumnTypeAppeals.do", 1);
        return RestResponse.res(1000,"yiahdad").toString();
    }
}
