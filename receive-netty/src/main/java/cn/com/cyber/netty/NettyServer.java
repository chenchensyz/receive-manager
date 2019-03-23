package cn.com.cyber.netty;

import cn.com.cyber.util.CodeEnv;
import cn.com.cyber.util.SpringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * netty服务端 . <br>
 *
 * @author hkb
 */
public class NettyServer implements Runnable{

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建ServerBootstrap实例来引导绑定和启动服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //创建NioEventLoopGroup对象来处理事件，如接受新连接、接收数据、写数据等等
            CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
            int port = codeEnv.getSocket_server_port();
            //指定通道类型为NioServerSocketChannel，设置InetSocketAddress让服务器监听某个端口已等待客户端连接。
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        //设置childHandler执行所有的连接请求
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new ReadTimeoutHandler(40)); //40秒无读事件自动 channel close
                            ch.pipeline().addLast(new NettyIoHandler());
                        }
                    });
            // 最后绑定服务器等待直到绑定完成，调用sync()方法会阻塞直到服务器完成绑定,然后服务器等待通道关闭，因为使用sync()，所以关闭操作也会被阻塞。
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            LOGGER.warn("开始监听，端口为：{}", channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
