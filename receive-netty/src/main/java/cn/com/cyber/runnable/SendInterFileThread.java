package cn.com.cyber.runnable;

import cn.com.cyber.fileUpload.FileUploadFile;
import cn.com.cyber.fileUpload.UploadClient;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Set;

public class SendInterFileThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendInterFileThread.class);

    @Override
    public void run() {
        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.JEDIS_APPVALID_INDEX); //2号
        Environment env = SpringUtil.getBean(Environment.class);
        try {
            while (true) {
                Set<String> keys = jedis.keys(CodeUtil.JEDIS_FILE_PREFIX + "*");
                if (keys != null && keys.size() > 0) {
                    for (String key : keys) {
                        try {
                            Map<String, String> map = jedis.hgetAll(key);
                            int times = StringUtils.isBlank(map.get("times")) ? 0 : Integer.valueOf(map.get("times"));
                            int state = Integer.valueOf(map.get("state"));
                            long sendTime = StringUtils.isBlank(map.get("sendTime")) ? System.currentTimeMillis() : Long.valueOf(map.get("sendTime"));
                            if (times < 3) {
                                if (state == 0 || (System.currentTimeMillis() - sendTime > 1000 * 60 * 20 && state == 1)) {
                                    FileUploadFile uploadFile = new FileUploadFile();
                                    uploadFile.setFilePath(map.get("filePath"));
                                    uploadFile.setFileName((map.get("fileName")));
                                    uploadFile.setIntroduction(map.get("introduction"));
                                    uploadFile.setUuid(map.get("uuid"));
                                    uploadFile.setAppKey(map.get("appKey"));
                                    uploadFile.setServiceKey(map.get("serviceKey"));
                                    uploadFile.setFileSize(Integer.valueOf(map.get("fileSize")));
                                    int port = Integer.valueOf(env.getProperty(CodeUtil.FILE_SEVER_PORT));
                                    MyThreadPool.getThreadPool().execute(new UploadClient(port, env.getProperty(CodeUtil.SOCKET_URL), uploadFile));
                                    map.put("state", "1");
                                    map.put("sendTime", System.currentTimeMillis() + "");

                                    jedis.hmset(key, map);  //修改状态，保存到redis

                                    int i = 0;
                                    long maxTime = 3 * 60 * 1000;
                                    Map<String, String> success;
                                    do {
                                        success = jedis.hgetAll(key);
                                        Thread.sleep(1000);
                                        i += 1000;
                                        if ("2".equals(success.get("state"))) {
                                            map.put("state", success.get("state"));
                                        }
                                    } while (!"2".equals(success.get("state")) && i < maxTime);
                                    LOGGER.info("结束循环i:{}", i);
                                    map.put("times", times + 1 + "");
                                    jedis.hmset(key, map);
                                }
                            } else {
                                map.put("state", "3"); //上传失败
                                jedis.hmset(key, map);
                                jedis.expire(key, 259200); //3天
                            }
                        } catch (NumberFormatException ne) {
                            LOGGER.error("解析异常:{}，error:{}", key, ne.toString());
                            continue;
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            continue;
                        }
                    }
                }
                Thread.sleep(10 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    public static void main(String[] args) {
        int size = 5453908;
        System.out.println(size / 1000000);
    }
}
