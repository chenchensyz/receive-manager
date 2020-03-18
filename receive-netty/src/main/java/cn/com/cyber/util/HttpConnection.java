package cn.com.cyber.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.protocol.http.Handler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

    public static ResultData httpRequest(String requestUrl, String method, String contentType, String outputStr, String responseType, Map<String, String> paramHeader) {
        ResultData resultData = new ResultData();
        String result;
        HttpURLConnection conn = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        int code = 402;  //内网返回错误
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
            if (StringUtils.isNotBlank(contentType)) {
                conn.setRequestProperty("Content-type", contentType);
            }
            if (paramHeader != null && !paramHeader.isEmpty()) {  //传输头消息
                for (Map.Entry<String, String> entry : paramHeader.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 当outputStr不为null时向输出流写数据
            if (StringUtils.isNotBlank(outputStr)) {
                outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));// 注意编码格式
            }
            if (StringUtils.isBlank(responseType)) {
                responseType = CodeUtil.RESPONSE_TEXT_TYPE; //指定返回值类型，默认为text
            }
            int responseCode = conn.getResponseCode();
            if (CodeUtil.HTTP_OK == responseCode && CodeUtil.RESPONSE_FILE_TYPE.equals(responseType)) { //文件类型
                String responseData = getBase64FromInputStream(conn.getInputStream());
                JSONObject json = new JSONObject();
                json.put("responseContent", conn.getContentType());
                json.put("responseLength", conn.getContentLength());
                json.put("responseData", responseData);
                result = json.toString();
            } else {
                if (CodeUtil.HTTP_OK == responseCode) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }
                inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                bufferedReader = new BufferedReader(inputStreamReader);
                String str;
                StringBuffer buffer = new StringBuffer();
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                result = buffer.toString();
            }
            code = responseCode;
        } catch (Exception e) {
            LOGGER.error("请求异常 requestUrl:{},error:{}", requestUrl, e);
            result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILED, requestUrl + ":" + e.toString()));
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
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        resultData.setCode(code);
        resultData.setResult(result);
        return resultData;
    }

    public static String getBase64FromInputStream(InputStream in) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        String encode = new String(Base64.encodeBase64(data), CodeUtil.cs);
        return encode;
    }


    public static byte[] httpRequestBytes(String requestUrl, String method, String contentType, String outputStr, Map<String, String> paramHeader) {
        HttpURLConnection conn = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        DataInputStream dataInputStream = null;
        ByteArrayOutputStream byteArrayoutput = null;
        byte[] context = new byte[0];

        int code = 402;  //内网返回错误
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
            if (StringUtils.isNotBlank(contentType)) {
                conn.setRequestProperty("Content-type", contentType);
            }
            if (paramHeader != null && !paramHeader.isEmpty()) {  //传输头消息
                for (Map.Entry<String, String> entry : paramHeader.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 当outputStr不为null时向输出流写数据
            if (StringUtils.isNotBlank(outputStr)) {
                outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));// 注意编码格式
            }

            int responseCode = conn.getResponseCode();
            if (CodeUtil.HTTP_OK == responseCode) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }
            dataInputStream = new DataInputStream(inputStream);
            byteArrayoutput = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                byteArrayoutput.write(buffer, 0, length);
            }
            context = byteArrayoutput.toByteArray();
        } catch (Exception e) {
            LOGGER.error("请求异常 requestUrl:{},error:{}", requestUrl, e);
        } finally {
            // 释放资源
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                if (byteArrayoutput != null) {
                    byteArrayoutput.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return context;
    }
}