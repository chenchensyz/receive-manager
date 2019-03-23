package cn.com.cyber.util;

import cn.com.cyber.model.AppModel;
import cn.com.cyber.socket.SpringUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.protocol.http.Handler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
            int available;
            int i = 0;
            do {
                inputStream = conn.getInputStream(); // 从输入流读取返回内容
                available = inputStream.available();
                i++;
            } while (available <= 0 && i < 3);
            LOGGER.info("读取返回值 available:{}", available);
            if (available > 0) {
                inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                bufferedReader = new BufferedReader(inputStreamReader);
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                LOGGER.info("buffer:{}", buffer.toString() == null ? "读取为null" : "读取到返回值");
                result = buffer.toString();
            }
        } catch (Exception e) {
            flag = false;
            LOGGER.error("请求异常:{}", e.toString());
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

    public static String doGet(String httpurl) {
        LOGGER.error("httpurl start：{}", httpurl);
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpurl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            LOGGER.error("connection.getResponseCode()：{}", connection.getResponseCode());
            // 通过connection连接，获取输入流
//            if (connection.getResponseCode() == 200) {
//                is = connection.getInputStream();
//                // 封装输入流is，并指定字符集
//                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//                // 存放数据
//                StringBuffer sbf = new StringBuffer();
//                String temp = null;
//                while ((temp = br.readLine()) != null) {
//                    sbf.append(temp);
//                    sbf.append("\r\n");
//                }
//                result = sbf.toString();
//            }
            LOGGER.error("httpurl end：{}", httpurl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }
        return result;
    }

    public static Map<String, Object> doPost(AppModel appModel, String outputStr) {
        LOGGER.info("进入连接:{}", appModel.getUrlPrefix() + appModel.getUrlSuffix());
        boolean flag = true;
        Map<String, Object> map = Maps.newHashMap();
        String result = null;
        HttpURLConnection conn = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        String requestUrl = appModel.getUrlPrefix() + appModel.getUrlSuffix();
        try {
            URL url = new URL(null, requestUrl, new Handler());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //设置超时
            MessageCodeUtil messageCodeUtil = SpringUtil.getBean(MessageCodeUtil.class);
            int maxTime = Integer.valueOf(messageCodeUtil.getMessage(CodeUtil.REQUEST_MAXTIME));
//            conn.setConnectTimeout(maxTime);
//            conn.setReadTimeout(maxTime);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(appModel.getMethod());
            conn.setRequestProperty("Content-type", appModel.getContentType());
            conn.setRequestProperty("appkey", "E813C6F7651A4119927B32E5B1F92091");
            conn.setRequestProperty("servicekey", "616C8656478D4E3BBE5C9CD7D91A253B");

            // 当outputStr不为null时向输出流写数据
            if (StringUtils.isNotBlank(outputStr)) {
                outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
            }
            // 从输入流读取返回内容
            inputStream = conn.getInputStream();
            if (inputStream.available() != 0) {
                inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                bufferedReader = new BufferedReader(inputStreamReader);
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                result = buffer.toString();
            }
            LOGGER.info("result：{}", result.length());
        } catch (Exception e) {
            flag = false;
            LOGGER.error("请求异常:{}", e.getMessage());
            map.put("error", e);
        } finally {
            // 释放资源
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
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

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            String uuid = getUUID();
            System.out.println(uuid.toUpperCase());
        }
    }
}