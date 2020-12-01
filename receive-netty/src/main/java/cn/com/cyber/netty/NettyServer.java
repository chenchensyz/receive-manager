package cn.com.cyber.netty;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * netty服务端 . <br>
 *
 * @author hkb
 */
public class NettyServer implements Runnable {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    @Override
    public void run() {
        /**
         * 定义一对线程组（两个线程池）
         *
         */
        //主线程组，用于接收客户端的链接，但不做任何处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //定义从线程组，主线程组会把任务转给从线程组进行处理
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建ServerBootstrap实例来引导绑定和启动服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //创建NioEventLoopGroup对象来处理事件，如接受新连接、接收数据、写数据等等
            Environment env = SpringUtil.getBean(Environment.class);
            int port = Integer.valueOf(env.getProperty(CodeUtil.SOCKET_SERVER_PORT));
            //指定通道类型为NioServerSocketChannel，设置InetSocketAddress让服务器监听某个端口已等待客户端连接。
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer());
            // 最后绑定服务器等待直到绑定完成，调用sync()方法会阻塞直到服务器完成绑定,然后服务器等待通道关闭，因为使用sync()，所以关闭操作也会被阻塞。
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();  //启动
            LOGGER.warn("开始监听，端口为：{}", channelFuture.channel().localAddress());
            //获取某个客户端所对应的chanel，关闭并设置同步方式
            channelFuture.channel().closeFuture().sync(); //关闭
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
