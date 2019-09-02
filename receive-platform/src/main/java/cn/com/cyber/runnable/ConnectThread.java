package cn.com.cyber.runnable;

/**
 * 服务监控
 */

import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.model.AppService;
import cn.com.cyber.socket.SpringUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.protocol.http.Handler;

import java.net.HttpURLConnection;
import java.net.URL;
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
                List<AppService> serviceList = appServiceMapper.getList(appService);
                if (serviceList != null && serviceList.size() > 0) {
                    for (AppService service : serviceList) {
                        try {
                            Map<String, Object> map = testConnect(service.getUrlSuffix());
                            service.setControlState(Integer.valueOf(map.get("code").toString()));
                            if (map.get("msg") != null) {
                                service.setErrRemark(map.get("msg").toString());
                            }
                            appServiceMapper.updateControl(service);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            continue;
                        }
                    }
                }
                Thread.sleep(600 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
        result.put("code", code);
        return result;
    }

    public static void main(String[] args) {
        String url = "http://www.jb51.net/article/90864.htm";//http://user:pass@192.9.168.11:1122/
    }
}
