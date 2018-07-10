package cn.com.cyber.socket;

import cn.com.cybertech.commons.util.Base64Utils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * nio socket服务端
 */
public class SocketServerForward {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServerForward.class);
    //解码buffer
    private Charset cs = Charset.forName("UTF-8");
    //接受数据缓冲区
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
    //选择器（叫监听器更准确些吧应该）
    private static Selector selector;

    /**
     * 启动socket服务，开启监听
     *
     * @param port
     * @throws IOException
     */
    public void startSocketServer(int port) {
        try {
            //打开通信信道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            //获取套接字
            ServerSocket serverSocket = serverSocketChannel.socket();
            //绑定端口号
            serverSocket.bind(new InetSocketAddress(port));
            //打开监听器
            selector = Selector.open();
            //将通信信道注册到监听器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            //监听器会一直监听，如果客户端有请求就会进入相应的事件处理
            while (true) {
                selector.select();//select方法会一直阻塞直到有相关事件发生或超时
                Set<SelectionKey> selectionKeys = selector.selectedKeys();//监听到的事件
                for (SelectionKey key : selectionKeys) {
                    handle(key);
                }
                selectionKeys.clear();//清除处理过的事件
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 处理不同的事件
     *
     * @param selectionKey
     * @throws IOException
     */
    private void handle(SelectionKey selectionKey) throws IOException {
        LOGGER.info("SocketServerForward进入");
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        StringBuffer requestMsg = new StringBuffer();
        if (selectionKey.isAcceptable()) {
            LOGGER.info("接收通道:{}");
            //每有客户端连接，即注册通信信道为可读
            serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            int count = 0;
            socketChannel = (SocketChannel)selectionKey.channel();
            ByteBuffer rBuffer = ByteBuffer.allocate(32 * 1024);
            //读取数据
            while ((count = socketChannel.read(rBuffer)) > 0) {
                rBuffer.flip();
                byte[] bytes = new byte[count];
                rBuffer.get(bytes, 0, count);
                requestMsg.append(new String(bytes, cs));
                rBuffer.clear();
            }
            LOGGER.info("socket监听成功");
            String baseRequestMsg = new String(Base64Utils.decode(requestMsg.toString().getBytes(cs)), cs);
            JSONObject json = JSONObject.fromObject(baseRequestMsg);
            String messageId = json.getString("messageId");
            LOGGER.info("socket监听messageId:{}", messageId);
            ForwardMsg forwardMsg = new ForwardMsg();
            forwardMsg.forward(json);
//            //返回数据
//            sBuffer = ByteBuffer.allocate(responseMsg.getBytes("UTF-8").length);
//            sBuffer.put(responseMsg.getBytes("UTF-8"));
//            sBuffer.flip();
//            socketChannel.write(sBuffer);
//            socketChannel.close();
        }
    }

}
