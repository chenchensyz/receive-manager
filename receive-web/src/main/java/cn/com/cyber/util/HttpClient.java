package cn.com.cyber.util;

import cn.com.cyber.socket.SpringUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.protocol.http.Handler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class HttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    /**
     * 生成uuid
     *
     * @return
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr = str.replace("-", "");
        return uuidStr;
    }

    public static Map<String, Object> httpRequest(String requestUrl, String method, String contentType, String outputStr) {
        LOGGER.info("进入连接:{}",requestUrl);
        Map<String, Object> map = Maps.newHashMap();
        String result = null;
        HttpURLConnection conn = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(null, requestUrl, new Handler());
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //设置超时
            MessageCodeUtil messageCodeUtil = SpringUtil.getBean(MessageCodeUtil.class);
            int maxTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_MAXTIME));
            conn.setConnectTimeout(maxTime);
            conn.setReadTimeout(maxTime);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-type", contentType);
            // 当outputStr不为null时向输出流写数据
            if (StringUtils.isNotBlank(outputStr)) {
                outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
            }
            int responseCode = conn.getResponseCode();
            if (CodeUtil.HTTP_OK == responseCode) {
                inputStream = conn.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                bufferedReader = new BufferedReader(inputStreamReader);
                String str;
                StringBuffer buffer = new StringBuffer();
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                result = buffer.toString();
            } else {
                map.put("error", responseCode + ":" + conn.getResponseMessage());
                LOGGER.error("响应异常code:{}, requestUrl:{} ,msg:{}", responseCode, requestUrl, conn.getResponseMessage());
            }
            map.put("code", responseCode);
        } catch (Exception e) {
            LOGGER.error("请求异常 requestUrl:{},error:{}", requestUrl, e);
            map.put("error", e.toString());
        } finally {
            // 释放资源
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        map.put("result", result);
        return map;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            String uuid = getUUID();
            System.out.println(uuid.toUpperCase());
        }
    }
}