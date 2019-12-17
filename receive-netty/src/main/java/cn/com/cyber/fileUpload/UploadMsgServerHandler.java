package cn.com.cyber.fileUpload;

/**
 * 文件传输-互联网接收返回值
 */

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

public class UploadMsgServerHandler extends ChannelInboundHandlerAdapter {

    private Logger LOGGER = LoggerFactory.getLogger(UploadMsgServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelActive(ctx);
        LOGGER.info("服务端：channelActive()");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelInactive(ctx);
        LOGGER.info("服务端：channelInactive()");
        ctx.flush();
        ctx.close();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.JEDIS_APPVALID_INDEX); //2号
        ByteBuf buf = (ByteBuf) msg;
        LOGGER.info("读取数据 buf.readableBytes():{}", buf.readableBytes());
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        buf.release();
        String body = new String(req, CodeUtil.cs);
        try {
            byte[] bodybytes = Base64.decodeBase64(body.getBytes(CodeUtil.cs));
            String baseRequestMsg = new String(bodybytes, CodeUtil.cs);
            JSONObject object = JSONObject.parseObject(baseRequestMsg);
            LOGGER.info("接受状态返回：{}", object);
            if (object != null && object.get("success") != null && (Boolean) object.get("success")) {
                String key = CodeUtil.JEDIS_FILE_PREFIX + object.get("uuid");
                Map<String, String> map = jedis.hgetAll(key);
                map.put("state", object.get("state").toString());
                jedis.hmset(key, map);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        LOGGER.info("FileUploadServerHandler--exceptionCaught()");
    }
}
