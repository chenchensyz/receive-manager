package cn.com.cyber.fileUpload;

import cn.com.cyber.util.CodeEnv;
import cn.com.cyber.util.SpringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServer.class);

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) {
                    LOGGER.warn("有客户端连接:" + ch.localAddress().toString());
                    CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
                    if ("inter".equals(codeEnv.getProject_environment())) {
                        ch.pipeline().addLast(new UploadMsgServerHandler());
                    } else {
                        ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                        ch.pipeline().addLast(new UploadServerHandler());
                    }
                }
            });
            CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
            LOGGER.info("file server 等待连接:{}", codeEnv.getFile_sever_port());
            ChannelFuture f = b.bind(codeEnv.getFile_sever_port()).sync();
            f.channel().closeFuture().sync();
            LOGGER.info("file end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
