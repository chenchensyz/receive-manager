package cn.com.cyber.socket;

import com.alibaba.fastjson.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class TalkClient {

    public static void send(int port, String url, Map<String, String> map) {
        FileInputStream fis = null;
        DataOutputStream dos = null;
        Socket socket = null;
        try {
            socket = new Socket(url, port);
            File file = new File(map.get("filePath"));
            fis = new FileInputStream(file);
            // 发送给服务器的数据
            dos = new DataOutputStream(socket.getOutputStream());
            JSONObject jsonObject = new JSONObject();  //传输文件相关信息
            jsonObject.put("fileName", map.get("fileName"));
            jsonObject.put("fileSize", map.get("fileSize"));
            jsonObject.put("uuid", map.get("uuid"));
            jsonObject.put("introduction", map.get("introduction"));
            jsonObject.put("upUrl", map.get("upUrl"));
            dos.write(jsonObject.toString().getBytes("UTF-8"));
            dos.write("$_".getBytes("UTF-8"));
            dos.flush();
            Thread.sleep(100);
            System.out.println("========" + map.get("fileName") + " 开始传输文件 ========");
            int size = 80 * 1024;
            byte[] bytes = new byte[size];
            if (Integer.valueOf(map.get("fileSize")) < size) {
                bytes = new byte[Integer.valueOf(map.get("fileSize"))];
                dos.write(bytes);
                dos.flush();
            } else {
                int length = 0;
                long progress = 0;
                while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    System.out.print("| " + (100 * progress / file.length()) + "% |");
                }
            }
            dos.write("$_".getBytes("UTF-8"));
            System.out.println("========" + map.get("fileName") + "文件传输成功 ========");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket == null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}