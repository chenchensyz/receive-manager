package cn.com.cyber.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
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

    public static Map<String, Object> httpRequest(String requestUrl, String method, String contentType, String outputStr, String responseType, Map<String, String> serviceHeader) {
        Map<String, Object> map = Maps.newHashMap();
        String result = null;
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
            if (serviceHeader != null && !serviceHeader.isEmpty()) {  //传输头消息
                for (Map.Entry<String, String> entry : serviceHeader.entrySet()) {
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
                e.printStackTrace();
            }
        }
        map.put("code", code);
        map.put("result", result);
        return map;
    }

    public static String newParams(Map<String, Object> paramMap, String method, String contentType, String requestUrl) throws UnsupportedEncodingException {
        String newParam = "";
        if (CodeUtil.RESPONSE_POST.equals(method)) {
            if (CodeUtil.CONTEXT_JSON.equals(contentType)) {  //json格式
                newParam = JSONObject.toJSON(paramMap).toString();
            } else {
                int i = 1;
                for (String key : paramMap.keySet()) {
                    String value = paramMap.get(key).toString();
                    newParam += key + "=" + URLEncoder.encode(value, "UTF-8");
                    if (i < paramMap.size()) {
                        newParam += "&";
                    }
                    i++;
                }
            }
        } else if (CodeUtil.RESPONSE_GET.equals(method)) {
            if (requestUrl.contains("{")) { //拼在地址栏
                for (String key : paramMap.keySet()) {
                    String value = paramMap.get(key).toString();
                    String replace = requestUrl.replace("{" + key + "}", value);
                    requestUrl = replace;
                }
            } else {
                requestUrl = requestUrl + "?";
                int i = 1;
                for (String key : paramMap.keySet()) {
                    String value = paramMap.get(key).toString();
                    requestUrl += key + "=" + URLEncoder.encode(value, "UTF-8");
                    if (i < paramMap.size()) {
                        requestUrl += "&";
                    }
                    i++;
                }
            }
        }
        return newParam;
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
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String encode = new String(Base64.encodeBase64(data), CodeUtil.cs);
        return encode;
    }

    public static Map<String, Object> requestNewParams(String params, String method, String contentType, String requestUrl, Map<String, String> serviceHeader) throws UnsupportedEncodingException {
        String newParam = "";
        if (StringUtils.isNotBlank(params)) {
            JSONObject dataParams = JSONObject.parseObject(params);
            JSONObject jsonParams = new JSONObject();

            boolean getFlag = true;
            if (requestUrl.contains("{")) { //拼在地址栏
                for (String key : dataParams.keySet()) {
                    Object value = dataParams.get(key);
                    if (!requestUrl.contains("{" + key + "}")) {
                        jsonParams.put(key, value);
                    }
                    String replace = requestUrl.replace("{" + key + "}", value.toString());
                    requestUrl = replace;
                    getFlag = false;
                }
            } else {
                jsonParams = dataParams;
            }
            if (CodeUtil.RESPONSE_POST.equals(method)) {
                if (CodeUtil.CONTEXT_JSON.equals(contentType)) {  //json格式
                    newParam = jsonParams.toJSONString();
                } else {
                    int i = 1;
                    for (String key : jsonParams.keySet()) {
                        String value = jsonParams.get(key).toString();
                        newParam += key + "=" + URLEncoder.encode(value, "UTF-8");
                        if (i < jsonParams.size()) {
                            newParam += "&";
                        }
                        i++;
                    }
                }
            } else if (CodeUtil.RESPONSE_GET.equals(method) && getFlag) {//地址栏未变化
                requestUrl = requestUrl + "?";
                int i = 1;
                for (String key : jsonParams.keySet()) {
                    String value = jsonParams.get(key).toString();
                    requestUrl += key + "=" + URLEncoder.encode(value, "UTF-8");
                    if (i < jsonParams.size()) {
                        requestUrl += "&";
                    }
                    i++;
                }
            }
        }
        LOGGER.info("newParam:{}", newParam);
        LOGGER.info("method:{},ContentType:{},url:{}", method, contentType, requestUrl);
        Map<String, Object> resultMap = httpRequest(requestUrl, method, contentType, newParam, null, serviceHeader);
        return resultMap;
    }
}