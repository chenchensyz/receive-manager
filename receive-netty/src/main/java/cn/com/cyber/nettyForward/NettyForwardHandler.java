package cn.com.cyber.nettyForward;

import cn.com.cyber.runnable.MyThreadPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * 移动信息网转发服务，未使用  <br>
 *
 * @author hkb
 */
public class NettyForwardHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyForwardHandler.class);

    private StringBuffer stringBuffer = new StringBuffer();

    private Charset cs = Charset.forName("UTF-8");

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
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
                MyThreadPool.getThreadPool().execute(new ForwardThread(stringBuffer.toString(),ctx));
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
}