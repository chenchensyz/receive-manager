package cn.com.cyber.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //通过socketChannel去获得对应的管道
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        /**
         * pipeline中会有很多handler类（也称之拦截器类）
         * 获得pipeline之后，可以直接.add，添加不管是自己开发的handler还是netty提供的handler
         *
         */
        channelPipeline.addLast(new ReadTimeoutHandler(40)); //40秒无读事件自动 channel close
        channelPipeline.addLast(new NettyIoHandler());

    }
}
