package cn.com.cyber.fileUpload;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//netty客户端
public class UploadClient implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadClient.class);

    private int port;
    private String host;
    private FileUploadFile fileUploadFile;

    public UploadClient(int port, String host, FileUploadFile fileUploadFile) {
        this.port = port;
        this.host = host;
        this.fileUploadFile = fileUploadFile;
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new UploadClientHandler(fileUploadFile));
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
