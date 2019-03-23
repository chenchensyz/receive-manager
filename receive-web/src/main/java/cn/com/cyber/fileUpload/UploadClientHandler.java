package cn.com.cyber.fileUpload;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class UploadClientHandler extends ChannelInboundHandlerAdapter {
    private int byteRead;
    private volatile int lastLength = 0;
    public RandomAccessFile randomAccessFile;
    private FileUploadFile fileUploadFile;
    private Logger LOGGER = LoggerFactory.getLogger(UploadClientHandler.class);

    public UploadClientHandler(FileUploadFile ef) {
        File file = new File(ef.getFilePath());
        if (file.exists()) {
            if (!file.isFile()) {
                System.out.println("Not a file :" + file);
                return;
            }
        }
        this.fileUploadFile = ef;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelInactive(ctx);
        LOGGER.info("channelInactive()");
    }

    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("开始传输文件");
        try {
            randomAccessFile = new RandomAccessFile(fileUploadFile.getFilePath(), "r");
            String path = fileUploadFile.getFilePath();
            randomAccessFile.seek(0);
            // lastLength = (int) randomAccessFile.length() / 10;
            lastLength = 1024 * 100;
            if (fileUploadFile.getFileSize() < lastLength) {
                lastLength = fileUploadFile.getFileSize();
            }
            byte[] bytes = new byte[lastLength];
            int start = 0;
            while ((byteRead = randomAccessFile.read(bytes)) != -1) {
                fileUploadFile.setStartPos(start);
                start += byteRead;
                randomAccessFile.seek(start);
                fileUploadFile.setEndPos(byteRead);
                if (byteRead < bytes.length) {
                    byte[] endBytes = new byte[byteRead];
                    System.arraycopy(bytes, 0, endBytes, 0, byteRead);
                    fileUploadFile.setBytes(endBytes);
                } else {
                    fileUploadFile.setBytes(bytes);
                }
                ctx.writeAndFlush(fileUploadFile);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LOGGER.info("发送完毕:{} path:{}", start, path);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                randomAccessFile.close();
                LOGGER.info("执行关闭");
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public static void main(String[] args) {
        System.out.println((int) ((Math.random() * 9 + 1) * 100000));
    }
}
