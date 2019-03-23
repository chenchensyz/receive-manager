package cn.com.cyber.client;

import cn.com.cyber.util.CodeEnv;
import cn.com.cyber.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class SocketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);

    public static void send(String url, int port, String data) {
        try {
            Socket socket = new Socket(url, port);
            byte[] jsonByte = data.getBytes("UTF-8");
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//            LOGGER.info("发的数据长度为:{}", jsonByte.length);
            int len = 4 * 1024;
            int offset = 0;
            if (jsonByte.length > len) {
                int size = jsonByte.length / len;
                if (jsonByte.length % len > 0) {
                    size += 1;
                }
                for (int i = 0; i < size; i++) {
                    if (jsonByte.length - offset < len) {
                        len = jsonByte.length - offset;//最后一次写入
                    }
                    byte[] formByte = new byte[len];
                    System.arraycopy(jsonByte, offset, formByte, 0, len);
                    outputStream.write(formByte);
                    if (i == size - 1) {
                        outputStream.write(new byte[1]);
                    }
                    outputStream.flush();
                    offset = offset + len;
                }
//                LOGGER.info("分段发送次数:{}", size);
            } else {
//                LOGGER.info("一次发送");
                outputStream.write(jsonByte);
                outputStream.write(new byte[1]);
                outputStream.flush();
            }
            LOGGER.info("传输数据完毕");
            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "";
        String ns = "";
        byte[] jsonByte = s.getBytes("UTF-8");
        System.out.println(s.length());
        System.out.println(jsonByte.length);
        int len = 4 * 1024;
        int offset = 0;
        if (jsonByte.length > len) {
            int size = jsonByte.length / len;
            if (jsonByte.length / len > 0) {
                size += 1;
            }
            for (int i = 0; i < size; i++) {
                if (jsonByte.length - offset < len) {
                    len = jsonByte.length - offset;//最后一次写入
                }
                StringBuffer paramData = new StringBuffer();
                paramData.append("haoren1223" + ":").append(i + 1 + ":").append(size + ":");
                byte[] paramBytes = paramData.toString().getBytes("UTF-8");
                byte[] formByte = new byte[paramBytes.length + len];
                System.arraycopy(paramBytes, 0, formByte, 0, paramBytes.length);
                System.arraycopy(jsonByte, offset, formByte, paramBytes.length, len);
                offset = offset + len;
                String a = new String(formByte);
                ns = a;
                System.out.println(ns);
            }
        }
//        StringBuffer paramData = new StringBuffer();
//        paramData.append("nihao" + ":").append(1 + ":").append(1 + ":");
//        byte[] paramBytes = paramData.toString().getBytes("UTF-8");
//        byte[] formByte = new byte[paramBytes.length +len];
//        System.arraycopy(paramBytes, 0, formByte, 0, paramBytes.length);
//        System.arraycopy(jsonByte,  offset , formByte, paramBytes.length, len);
//        System.out.println(new String(formByte));
//        System.out.println(s);
        byte[] formByte = new byte[]{0x12, 0x32, 0x32, 0x23, 0x22, 0};
        System.out.println(formByte);
        byte[] bb = new byte[1];
        for (int i = 0, ll = formByte.length; i < ll; i++) {
            if (formByte[i] == 0) {
                System.out.println("......" + i);
            }
        }

        String ush = "0";
        System.out.println(ush.getBytes()[0]);
        byte[] endByte = new byte[1];
    }
}
