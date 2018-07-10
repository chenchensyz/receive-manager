package cn.com.cyber.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * @author chenchen
 * @ClassName: ReadCodeUtil
 * @Description: 获取配置文件信息
 * @date: 2018年05月22日
 */
public class ReadCodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReadCodeUtil.class);
    private static String filePath = "i18n/codeInfo.properties";

    /**
     * 读取properties文件
     *
     * @param key 根据key值获取
     * @return
     */
    public static String getConfig(Object key) {

        Properties properties = new Properties();
//        String path = Thread.currentThread().getContextClassLoader().getResource(filePath).getPath();
        InputStream is = null;
        InputStreamReader isr = null;
        try {
            String path = ReadCodeUtil.class.getClassLoader().getResource(filePath).getPath();
            is = ReadCodeUtil.class.getClassLoader().getResourceAsStream(filePath);
            isr = new InputStreamReader(is, codeString(path));
            properties.load(isr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("读取失败 ：{}", e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("读取失败 ：{}", e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("读取失败 ：{}", e);
        }finally {
            try {
                if (is!=null) {
                    is.close();
                }
                if (isr!=null){
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String value = properties.getProperty(key.toString());
        return value;
    }

    /**
     * 判断文件所用编码
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String codeString(String fileName) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(
                new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        String code = null;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }

        return code;
    }


    public static void main(String[] args) throws IOException {
        String config = getConfig(220001);
        System.out.println(config);
    }
}