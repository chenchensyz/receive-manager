package cn.com.cyber.netty;

import cn.com.cyber.runnable.MyThreadPool;
import cn.com.cyber.runnable.WorkerThread;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.DateUtil;
import cn.com.cyber.util.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

/**
 * 服务端处理器 . <br>
 *
 * @author hkb
 */
public class NettyIoHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyIoHandler.class);

    private StringBuffer stringBuffer = new StringBuffer();

    private Charset cs = Charset.forName("UTF-8");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        LOGGER.info("收到客户端连接:{}", ctx.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();//打印异常
        ctx.close();// 当出现异常就关闭连接
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //读取数据
        ByteBuf buf = (ByteBuf) msg;
//        LOGGER.info("读取数据 buf.readableBytes():{}", buf.readableBytes());
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        String body = new String(req, cs);
//        LOGGER.info("进入方法 stringBuffer.length:{}", stringBuffer.length());
        buf.release();
        int blockIndex = 0;
        for (int i = 0; i < req.length; i++) {
            if (req[i] == 0) {
                int blockSize = i - blockIndex;
                if (blockSize > 0) {
                    byte[] selectByte = new byte[i - blockIndex];
                    System.arraycopy(req, blockIndex, selectByte, 0, selectByte.length);
                    stringBuffer.append(new String(selectByte, cs));
//                    LOGGER.info("找到结束标志  stringBuffer.length:{}", stringBuffer.length());
                }
                Environment env = SpringUtil.getBean(Environment.class);
                if ("inter".equals(env.getProperty(CodeUtil.PROJECT_ENVIRONMENT))) {
                    decodeResponseMsg(stringBuffer);
                } else {
                    decodeRequestMsg(stringBuffer);
                }
//                    MyThreadPool.getThreadPool().execute(new WorkerThread(stringBuffer.toString()));
                stringBuffer.setLength(0);
                blockIndex = i + 1;
            }
        }
        if (req.length > blockIndex) {
            byte[] endByte = new byte[req.length - blockIndex];
            System.arraycopy(req, blockIndex, endByte, 0, endByte.length);
            stringBuffer.append(new String(endByte, cs));
//            LOGGER.info("没有结束标志 放入缓存:{}", stringBuffer.length());
        }
//        LOGGER.info("释放资源 buf.release():{}", buf.readableBytes());
//            //向客户端写数据
//            LOGGER.info("server向client发送数据");
//            String currentTime = new Date(System.currentTimeMillis()).toString();
//            ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
//            ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        LOGGER.info("server 读取数据完毕.." + ctx.toString());
        //ctx.flush();//刷新后才将数据发出到SocketChannel
    }

    private void decodeResponseMsg(StringBuffer cacheMsg) {
        if (cacheMsg.length() == 0) {
            return;
        }
        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] cacheMsgbytes = Base64.decodeBase64(cacheMsg.toString().getBytes(cs));
            String baseRequestMsg = new String(cacheMsgbytes, cs);
            JSONObject json = JSONObject.parseObject(baseRequestMsg);
            String messageId = json.get("messageId").toString();
            String params = json.get("params").toString();

            //移动网返回时间-------
            Map<String, String> map = jedis.hgetAll(CodeUtil.TIME_JEDIS_PREFIX + messageId);
            if (!map.isEmpty()) {
                Map<String, String> testExport = (Map<String, String>) json.get("testExport");
                map.put("interResTime", DateUtil.format(new Date(), DateUtil.YMD_DASH_WITH_TIME));
                map.putAll(testExport);
                jedis.hmset(CodeUtil.TIME_JEDIS_PREFIX + messageId, map);
            }
            //---------------------

            //保存到redis
            Environment env = SpringUtil.getBean(Environment.class);
            jedis.setex(messageId, Integer.valueOf(env.getProperty(CodeUtil.CACHE_TIME)), params);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    private void decodeRequestMsg(StringBuffer cacheMsg) {
        if (cacheMsg.length() == 0) {
            return;
        }
        try {
            byte[] cacheMsgbytes = Base64.decodeBase64(cacheMsg.toString().getBytes(cs));
            String baseRequestMsg = new String(cacheMsgbytes, cs);
            MyThreadPool.getThreadPool().execute(new WorkerThread(baseRequestMsg));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

}