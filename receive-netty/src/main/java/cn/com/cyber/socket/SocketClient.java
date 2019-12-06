package cn.com.cyber.socket;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class SocketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);

    private static Socket socket;

    private static synchronized void SocketConfig() {
        try {
            Environment env = SpringUtil.getBean(Environment.class);
            int port = Integer.valueOf(env.getProperty(CodeUtil.SOCKET_CLIENT_PORT));
            String url = env.getProperty(CodeUtil.SOCKET_URL);
            LOGGER.info("SocketClient初始化 port:{},url:{}", port, url);
            socket = new Socket(url, port);
            socket.setKeepAlive(true);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void init() {
        if (socket == null) {
            SocketConfig();
        }
    }

    public static Socket getSocket() {
        if (socket == null) {
            SocketConfig();
        }
        return socket;
    }

    public static void setNull() {
        socket = null;
    }

    public static void send(String data) {
        try {
            //将json转化为String类型
            //将String转化为byte[]
            //byte[] jsonByte = new byte[jsonString.length()+1];
            byte[] jsonByte = data.getBytes("UTF-8");
            DataOutputStream outputStream = new DataOutputStream(getSocket().getOutputStream());
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
//            LOGGER.info("传输数据完毕");
//            socket.shutdownOutput();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            setNull();
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
