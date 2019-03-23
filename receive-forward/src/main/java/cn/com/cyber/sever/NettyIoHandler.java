package cn.com.cyber.sever;

import cn.com.cyber.runnable.MyThreadPool;
import cn.com.cyber.runnable.WorkerThread;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * 服务端处理器 . <br>
 *
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
                MyThreadPool.getThreadPool().execute(new WorkerThread(stringBuffer.toString()));//发送数据
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
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        LOGGER.info("server 读取数据完毕.." + ctx.toString());
        //ctx.flush();//刷新后才将数据发出到SocketChannel
    }
}