package cn.com.cyber.runnable;

/**
 * 文件传输服务--接收互联网文件
 */
import cn.com.cyber.fileUpload.FileUploadFile;
import cn.com.cyber.socket.TalkClient;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

public class FileReceiveThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReceiveThread.class);

    private FileUploadFile fileUploadFile;
    private RandomAccessFile randomAccessFile;
    private ChannelHandlerContext ctx;

    public FileReceiveThread(FileUploadFile fileUploadFile, RandomAccessFile randomAccessFile, ChannelHandlerContext ctx) {
        this.fileUploadFile = fileUploadFile;
        this.randomAccessFile = randomAccessFile;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] bytes = fileUploadFile.getBytes();
            String fileName = fileUploadFile.getFileName();//文件名
            int start = fileUploadFile.getStartPos();
            randomAccessFile.seek(start);
            randomAccessFile.write(bytes);
            long size = randomAccessFile.length();
//            LOGGER.info("getEndPos:{}", fileUploadFile.getEndPos());
//            LOGGER.info("size：{}", size);
            if (fileUploadFile.getEndPos() + start >= fileUploadFile.getFileSize()) {  //文件读取完毕
                randomAccessFile.close();
                Map<String, String> map = Maps.newHashMap();
                map.put("fileName", fileName);
                map.put("filePath", fileUploadFile.getFilePath());
                map.put("fileSize", fileUploadFile.getFileSize() + "");
                map.put("introduction", fileUploadFile.getIntroduction());
                map.put("uuid", fileUploadFile.getUuid());
                map.put("upUrl", fileUploadFile.getUpUrl());
                map.put("state", "0");
                map.put("times", "0");
                String key = CodeUtil.FILE_JEDIS_PREFIX + fileUploadFile.getUuid();

                jedis.hmset(key, map);
                jedis.expire(key, 604800);

//                MyThreadPool.getThreadPool().execute(new FileUpThread(key));
//                receiveMsg(key);
                LOGGER.info("文件保存成功 uuid:{},size:{}", fileUploadFile.getUuid(), size);
                //文件保存成功，返回移动网状态为2
                Environment env = SpringUtil.getBean(Environment.class);
                JSONObject param = new JSONObject();
                param.put("success", true);
                param.put("uuid", map.get("uuid"));
                param.put("state", 2);
//                LOGGER.info("url:{},port:{}", codeEnv.getSocket_url(), codeEnv.getFile_sever_port());
                byte[] baseByte = Base64.encodeBase64(param.toString().getBytes(CodeUtil.cs));
                String baseParam = new String(baseByte, CodeUtil.cs);
                int port = Integer.valueOf(env.getProperty(CodeUtil.FILE_SEVER_PORT));
                String url = env.getProperty(CodeUtil.SOCKET_URL);
                TalkClient.send(url, port, baseParam);
            }
        } catch (Exception e) {
            try {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                randomAccessFile.close();
            } catch (IOException e1) {
                LOGGER.error(e1.toString(), e1);
                e1.printStackTrace();
            }
            LOGGER.error(e.toString(), e);
        } finally {
            jedis.close();
        }
    }

//    public void receiveMsg(String key, Jedis jedis) throws Exception {
//        Map<String, String> map = jedis.hgetAll(key);
//        String url = map.get("upUrl");
//        String state = "1";
//        Map<String, File> files = new HashMap<String, File>();
//        File file = new File(map.get("filePath"));
//
//        Map<String, String> requestParamsMap = new HashMap<String, String>();
//        requestParamsMap.put("introduction", map.get("introduction"));
//        requestParamsMap.put("size", map.get("fileSize"));
//
//        files.put(file.getName(), file);
//        Map<String, Object> resultMap = FileUpConnection.postFileUp(url, requestParamsMap, files);
//        if (StringUtils.isNotBlank(resultMap.get("code").toString())
//                && CodeUtil.HTTP_OK == Integer.valueOf(resultMap.get("code").toString())) {
//            JSONObject object = JSONObject.fromObject(resultMap.get("result"));
//
//            LOGGER.info("请求内网返回值object:{}", object);
//            if (object != null && object.get("success") != null && (Boolean) object.get("success")) {
//                state = "2";
//                CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
//                JSONObject param = new JSONObject();
//                param.put("success", true);
//                param.put("uuid", map.get("uuid"));
//                param.put("state", 2);
//                LOGGER.info("url:{},port:{}", codeEnv.getSocket_url(), codeEnv.getFile_sever_port());
//                String baseParam = new String(Base64Utils.encode(param.toString().getBytes("UTF-8")));
//                TalkClient.send(codeEnv.getSocket_url(), codeEnv.getFile_sever_port(), baseParam);
//            }
//        }
//        map.put("state", state);
//        int sendCount = 0;
//        if (StringUtils.isNotBlank(map.get("sendCount"))) {
//            sendCount = Integer.valueOf(map.get("sendCount"));
//        }
//        map.put("sendCount", sendCount + 1 + "");
//        jedis.hmset(key, map);
//        jedis.expire(key, 604800);
//    }

}
