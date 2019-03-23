package cn.com.cyber.fileUpload;

/**
 * 文件传输-移动网接收文件、保存
 */

import cn.com.cyber.runnable.FileReceiveThread;
import cn.com.cyber.runnable.FileUpPool;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class UploadServerHandler extends ChannelInboundHandlerAdapter {

    private Logger LOGGER = LoggerFactory.getLogger(UploadServerHandler.class);

    private final static String fileSavePath = "/home/upFile";
    //        private final static String fileSavePath = "D:\\source\\";
    private RandomAccessFile randomAccessFile;
    private boolean ok = false;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelActive(ctx);
//        LOGGER.info("服务端：channelActive()");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FileUploadFile) {   //移动网接收文件
            try {
                FileUploadFile fileUploadFile = (FileUploadFile) msg;
                if (fileUploadFile.getStartPos() == 0) {
                    String fileName = fileUploadFile.getFileName();
                    String suffix = fileName.substring(fileName.lastIndexOf("."));
                    String path = fileSavePath + File.separator + fileUploadFile.getUuid() + suffix;
                    randomAccessFile = new RandomAccessFile(path, "rw");
                    fileUploadFile.setFilePath(path);
                }
                FileUpPool.getThreadPool(1).execute(new FileReceiveThread(fileUploadFile, randomAccessFile,ctx));
                ok = true;
            } catch (Exception e) {
                LOGGER.info("连接异常：{}", ctx.toString());
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                LOGGER.error(e.toString(), e);
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (IOException e1) {
                        LOGGER.error(e1.toString(), e1);
                    }
                }
            }
        }
        LOGGER.info("channelRead读取完毕：{}", ctx.toString());
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//       System.out.println("channel 通道 Read 读取 Complete 完成");
        ctx.flush();
        if (!ok) {
            ctx.read();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        try {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        LOGGER.info("连接异常执行关闭:{}", ctx.toString());
//        ctx.channel().closeFuture().addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                if (!channelFuture.isSuccess()) {
//                    LOGGER.info("连接异常执行关闭");
//                    channelFuture.channel().close();
//                }
//            }
//        });
//        CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
//        if ("inter".equals(codeEnv.getProject_environment())) {
//            ctx.pipeline().addLast(new UploadMsgServerHandler());
//        } else {
//            ctx.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
//            ctx.pipeline().addLast(new UploadServerHandler());
//        }
    }

}
