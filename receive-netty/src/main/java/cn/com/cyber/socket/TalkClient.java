package cn.com.cyber.socket;

import java.io.*;
import java.net.*;

public class TalkClient {

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
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        send("127.0.0.1", 8084, "irjfdsfhgiuashfiusdogfusgafusgdufasdg");
    }

}