package cn.com.cyber.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class FileUtil {

    private static String url = "https://app.liweihuo.com/pkg/apk/1.87-an.apk";


    public static byte[] getFileBytes(String fileUrl) {
        DataInputStream dataInputStream = null;
        byte[] context = new byte[0];
        try {
            URL url = new URL(fileUrl);
            dataInputStream = new DataInputStream(url.openStream());
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            context = output.toByteArray();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return context;
    }


    public static void saveFile(String filePath, byte[] context) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
            fileOutputStream.write(context);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setResponseBytes(HttpServletResponse response, String fileName , byte[] context) {
        try {
            if (context == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("UTF-8");
            if (context == null) {
                response.setContentLength(context.length);
                response.getOutputStream().write(context, 0, context.length);
                return;
            }
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentLength(context.length);

            OutputStream opStream = response.getOutputStream();
            InputStream inStream = new ByteArrayInputStream(context);

            int dataLen = 4 * 1024;
            byte[] data = new byte[dataLen];

            int len;
            while ((len = inStream.read(data, 0, dataLen)) != -1) {
                opStream.write(data, 0, len);
            }
            opStream.close();
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filePath = "D:\\qiwen\\";
        filePath += url.substring(url.lastIndexOf("/") + 1);
        byte[] bytes = getFileBytes(url);
        saveFile(filePath, bytes);
    }
}
