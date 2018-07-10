package cn.com.cyber.socket;

import cn.com.cyber.AppInfoService;
import cn.com.cyber.impl.AppInfoServiceImpl;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.tools.HttpClient;
import cn.com.cyber.tools.RestResponse;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.ReadCodeUtil;
import cn.com.cybertech.commons.util.Base64Utils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

@Component
public class ForwardMsg {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForwardMsg.class);

    public boolean forward(JSONObject json) {
        String messageId = json.getString("messageId");
        String result = "";
        String appKey = json.getString("appKey");
        String serviceKey = json.getString("serviceKey");
        String serviceUrl = json.getString("serviceUrl");
        LOGGER.info("接收请求json:{}", json.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageId", messageId);
        //根据appKey和serviceKey查询appinfo信息
        AppInfoService appInfoService = SpringUtil.getBean(AppInfoService.class);
        LOGGER.info("appInfoService:{}", appInfoService);
        AppInfo appInfo = appInfoService.getByAppAndService(appKey, serviceKey);
        AppModel appModel = null;
        if (appInfo != null) {
            LOGGER.info("接收请求appInfo:{}", appInfo.getUrlPrefix());
            appModel = appInfoService.selectAppModel(serviceUrl, appInfo.getId());
            LOGGER.info("接收请求appModel:{} , content:{}", appModel.getUrlPrefix(), appModel.getContentType());

            String params = json.getString("params");
            if (!CodeUtil.CONTEXT_JSON.equals(appModel.getContentType())) {
                params = "";
                String paramsString = json.getString("params");
                Map<String, Object> paramMap = (Map<String, Object>) JSON.parse(paramsString);
                for (String key : paramMap.keySet()) {
                    params += key + "=" + paramMap.get(key) + "&";
                }
            }
            LOGGER.info("params:{}", params);
            result = HttpClient.httpRequest(appModel, params);
        } else {
            result = RestResponse.res(CodeUtil.REQUEST_USE_FILE, ReadCodeUtil.getConfig(CodeUtil.REQUEST_USE_FILE)).toString();
        }
        LOGGER.info("result:{}", result);
        try {
            jsonObject.put("params", result);
            LOGGER.info("jsonObject:{}", jsonObject.toString());
            String baseResult = new String(Base64Utils.encode(jsonObject.toString().getBytes("UTF-8")));
            LOGGER.info("baseResult:{}", baseResult);
            SocketClient.send(CodeUtil.RECEIVE_URL, CodeUtil.SOCKET_PORT, baseResult);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return true;
    }
}
