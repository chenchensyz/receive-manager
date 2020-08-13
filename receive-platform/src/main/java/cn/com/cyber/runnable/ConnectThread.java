package cn.com.cyber.runnable;

/**
 * 服务监控
 */

import cn.com.cyber.controller.webSocket.WebSocketServer;
import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.dao.UserMapper;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.User;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpConnection;
import cn.com.cyber.util.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import sun.net.www.protocol.http.Handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConnectThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectThread.class);

    @Override
    public void run() {
        AppServiceMapper appServiceMapper = SpringUtil.getBean(AppServiceMapper.class);
        try {
            while (true) {
                AppService appService = new AppService();
                appService.setState(1);
                List<AppService> serviceList = appServiceMapper.getServiceList(appService);
                List<String> errors = Lists.newArrayList();

                StringBuilder leaveRecordStr = new StringBuilder();
                leaveRecordStr.append("异常预警： 已下服务运行异常，请登录后台查看\r\n");
                leaveRecordStr.append("\r\n");

                if (serviceList != null && serviceList.size() > 0) {
                    for (AppService service : serviceList) {
                        try {
                            Map<String, Object> map = testConnect(service.getUrlSuffix());
                            service.setControlState(Integer.valueOf(map.get("code").toString()));
                            if (map.get("msg") != null) {
                                service.setErrRemark(map.get("msg").toString());
                            }
                            appServiceMapper.updateControl(service);
                            if (Integer.valueOf(map.get("code").toString()) == 0) {
                                errors.add(service.getServiceName());
                                leaveRecordStr.append(service.getServiceName() + "\r\n");
                            }
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            continue;
                        }
                    }

                    Environment env = SpringUtil.getBean(Environment.class);

                    if (Boolean.valueOf(env.getProperty(CodeUtil.SUB_PUSH_OPEN)) && leaveRecordStr.length() > 0) { //推送订阅号
                        Map<String, String> headers = Maps.newHashMap();
                        headers.put("appId", env.getProperty(CodeUtil.SUB_PUSH_APPID));
                        headers.put("servicenoticetype", "subscription_text");


                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type", "text");
                        jsonObject.put("users", Arrays.asList(env.getProperty(CodeUtil.SUB_PUSH_USERS)));
                        jsonObject.put("content", leaveRecordStr.toString());

                        String url = env.getProperty(CodeUtil.SUB_PUSH_URL);
                        HttpConnection.httpRequest(url, CodeUtil.METHOD_POST, CodeUtil.CONTEXT_JSON, jsonObject.toString(), null, headers);
                    }

                    UserMapper userMapper = SpringUtil.getBean(UserMapper.class);//获取后台用户
                    List<User> userList = userMapper.getUserList(new User());
                    try {
                        for (User user : userList) {
                            WebSocketServer.sendInfo(JSON.toJSONString(errors), user.getUserId());
                        }
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                Thread.sleep(5 * 60 * 1000);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public Map<String, Object> testConnect(String surl) {
        Map<String, Object> result = Maps.newHashMap();
        HttpURLConnection connection = null;
        int code = 1;  //运行正常
        try {
            URL url = new URL(null, surl, new Handler());
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setConnectTimeout(2000);
            connection.connect();
        } catch (Exception e) {
            result.put("msg", e.getMessage());
            code = 0;
        } finally {// 释放资源
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        result.put("code", code);
        return result;
    }

    public static void main(String[] args) {
        String url = "http://www.jb51.net/article/90864.htm";//http://user:pass@192.9.168.11:1122/
    }
}
