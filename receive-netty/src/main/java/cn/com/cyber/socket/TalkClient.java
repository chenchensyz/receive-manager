package cn.com.cyber.socket;

import cn.com.cyber.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TalkClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalkClient.class);


    public static void send(String url, int port, String data) {
        DataOutputStream out = null;
        Socket socket = null;
        try {
            socket = new Socket(url, port);
            byte[] jsonByte = data.getBytes("UTF-8");
            // 发送给服务器的数据
            out = new DataOutputStream(socket.getOutputStream());
            out.write(jsonByte);
            out.flush();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) {
        send("127.0.0.1", 8084, "irjfdsfhgiuashfiusdogfusgafusgdufasdg");
    }

}