package cn.com.cyber.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class NettyIoClientHandler extends ChannelInboundHandlerAdapter {
    private String data;
    private Logger LOGGER = LoggerFactory.getLogger(NettyIoClientHandler.class);
    private StringBuffer stringBuffer = new StringBuffer();

    private Charset cs = Charset.forName("UTF-8");


    public NettyIoClientHandler(String data) {
        this.data = data;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelInactive(ctx);
        LOGGER.info("channelInactive()");
        ctx.flush();
        ctx.close();
    }

    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("开始传输");
        try {
            byte[] jsonByte = data.getBytes("UTF-8");
            int len = 4 * 1024;
            int offset = 0;
            if (jsonByte.length > len) {
                int size = jsonByte.length / len;
                if (jsonByte.length % len > 0) {
                    size += 1;
                }
                for (int i = 0; i < size; i++) {
                    if (jsonByte.length - offset < len) {
                        len = jsonByte.length - offset;//最后一次写入
                    }
                    byte[] formByte = new byte[len];
                    System.arraycopy(jsonByte, offset, formByte, 0, len);
                    ctx.write(formByte);
                    if (i == size - 1) {
                        ctx.write(new byte[1]);
                    }
                    ctx.flush();
                    offset = offset + len;
                }
//                LOGGER.info("分段发送次数:{}", size);
            } else {
//                LOGGER.info("一次发送");
                ctx.write(jsonByte);
                ctx.write(new byte[1]);
                ctx.flush();
            }
            LOGGER.info("传输数据完毕");
            channelInactive(ctx);
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
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
//                MyThreadPool.getThreadPool().execute(new WorkerThread(stringBuffer.toString()));//发送数据
                System.out.println(stringBuffer);
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


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
