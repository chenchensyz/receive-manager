package cn.com.cyber.nettyForward;
/**
 * 移动信息网转发服务，未使用
 */

import cn.com.cyber.util.CodeUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class ForwardThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForwardThread.class);

    private Charset cs = Charset.forName("UTF-8");

    private String command;
    private ChannelHandlerContext ctx;

    public ForwardThread(String s, ChannelHandlerContext ctx) {
        this.command = s;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        try {
//            JSONObject json = JSONObject.fromObject(command);
//            //正式查询
//            String messageId = json.getString("messageId");
//            CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
//            String result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_KEY_FILED, codeEnv.getMessage(CodeUtil.REQUEST_KEY_FILED)));
//            String params = json.get("params") == null ? "" : json.get("params").toString();
//            String requestUrl = json.get("requestUrl") == null ? "" : json.get("requestUrl").toString();
//            String method = json.get("method") == null ? "" : json.get("method").toString();
//            String contentType = json.get("contentType") == null ? "" : json.get("contentType").toString();
////            LOGGER.info("接收请求json.length:{}", json.toString().length());
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("messageId", messageId);
//            if (StringUtils.isNoneBlank(requestUrl, method, contentType)) {
////                LOGGER.info("requestUrl:{} , method:{} , contentType:{}", requestUrl, method, contentType);
//                if (!CodeUtil.CONTEXT_JSON.equals(contentType)) {
//                    String paramsString = params;
//                    params = "";
//                    Map<String, Object> paramMap = (Map<String, Object>) JSON.parse(paramsString);
//                    for (String key : paramMap.keySet()) {
//                        String value = paramMap.get(key).toString();
//                        params += key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
//                    }
//                }
//                //请求http接口
//                Map<String, Object> resultMap = HttpConnection.httpRequest(requestUrl, method, contentType, params);
//                if ((Boolean) resultMap.get("flag")) {
//                    if (resultMap.get("result") == null) {
//                        result = JSON.toJSONString(RestResponse.res(CodeUtil.REQUEST_USE_FILE, codeEnv.getMessage(CodeUtil.REQUEST_USE_FILE)));
//                    } else {
//                        result = resultMap.get("result").toString();
//                    }
//                } else {
//                    result = resultMap.get("error").toString();
//                }
//            }
//            jsonObject.put("params", result);
            byte[] baseByte = Base64.encodeBase64(command.getBytes(cs));
            String baseResult = new String(baseByte, CodeUtil.cs);
            ctx.writeAndFlush(baseResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.command;
    }
}
