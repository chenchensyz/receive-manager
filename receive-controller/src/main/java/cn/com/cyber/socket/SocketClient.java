package cn.com.cyber.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class SocketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);

    public static void send(String url,int port,String data) {
        Socket socket = null;
        LOGGER.info("url:{}, port:{}",url,port);
        try {
            socket = new Socket(url, port);
            LOGGER.info("连接已经建立");
            //将json转化为String类型
            //将String转化为byte[]
            //byte[] jsonByte = new byte[jsonString.length()+1];
            byte[] jsonByte = data.getBytes();
            DataOutputStream outputStream = null;
            outputStream = new DataOutputStream(socket.getOutputStream());
            LOGGER.info("发的数据长度为:{}",jsonByte.length);
            outputStream.write(jsonByte);
            outputStream.flush();
            LOGGER.info("传输数据完毕");
            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
