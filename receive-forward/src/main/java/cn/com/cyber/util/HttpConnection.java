package cn.com.cyber.util;

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

public class HttpConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnection.class);

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
        boolean flag = true;
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
            CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
            int maxTime = codeEnv.getRequest_maxtime();
            conn.setConnectTimeout(maxTime);
            conn.setReadTimeout(maxTime);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(method);
            if (StringUtils.isNotBlank(contentType)) {
                conn.setRequestProperty("Content-type", contentType);
            }
            // 当outputStr不为null时向输出流写数据
            if (StringUtils.isNotBlank(outputStr)) {
                outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));// 注意编码格式
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
                LOGGER.error("响应异常未收到200 requestUrl:{},code:{},msg:{}", requestUrl, responseCode, conn.getResponseMessage());
            }
        } catch (Exception e) {
            flag = false;
            LOGGER.error("请求异常 requestUrl:{},error:{}", requestUrl, e.toString());
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
        map.put("flag", flag);
        map.put("result", result);
        return map;
    }


}