package cn.com.cyber.runnable;

/**
 * 文件传输服务--定时任务
 */

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.FileUpConnection;
import cn.com.cyber.util.ResultData;
import cn.com.cyber.util.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SendMobileFileThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMobileFileThread.class);

    @Override
    public void run() {
        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.JEDIS_APPVALID_INDEX); //2号
        Environment env = SpringUtil.getBean(Environment.class);
        try {
            while (true) {
                try {
                    Set<String> keys = jedis.keys(CodeUtil.JEDIS_FILE_PREFIX + "*");
                    if (keys != null && keys.size() > 0) {
                        for (String key : keys) {
//                            Map<String, String> map = jedis.hgetAll(key);
//                            if (Integer.valueOf(map.get("state")) < 2 && Integer.valueOf(map.get("times")) < 3) {
//                                if ((StringUtils.isNotBlank(map.get("times")) && Integer.valueOf(map.get("times")) < 3)
//                                        || StringUtils.isBlank(map.get("times"))) {
//                                    MyThreadPool.getThreadPool().execute(new FileUpThread(key));
//                                } else {
//                                    map.put("state", "3"); //上传失败
//                                    jedis.hmset(key, map);
//                                }
//                            }
                            mobileFileUp(env, jedis, key);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    continue;
                }
                Thread.sleep(300 * 1000);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    private void mobileFileUp(Environment env, Jedis jedis, String key) {
        Map<String, String> map = jedis.hgetAll(key);
        if (Integer.valueOf(map.get("state")) < 2 && Integer.valueOf(map.get("times")) < 3) {
            if ((StringUtils.isNotBlank(map.get("times")) && Integer.valueOf(map.get("times")) < 3) || StringUtils.isBlank(map.get("times"))) {
                String state = "1";
                Map<String, File> fileMap = new HashMap<String, File>();
                File file = new File(map.get("filePath"));

                Map<String, String> requestParamsMap = new HashMap<String, String>();
                requestParamsMap.put("introduction", map.get("introduction"));
                requestParamsMap.put("size", map.get("fileSize"));
                requestParamsMap.put("appKey", map.get("appKey"));
                requestParamsMap.put("serviceKey", map.get("serviceKey"));

                fileMap.put(file.getName(), file);
                String url = env.getProperty(env.getProperty(CodeUtil.PLATFORM_URL) + CodeUtil.PLATFORM_FILEUP_URL);
                ResultData resultData = FileUpConnection.postFileUp(url, requestParamsMap, fileMap);
                if (resultData != null && CodeUtil.HTTP_OK == resultData.getCode()) {
                    JSONObject object = JSONObject.parseObject(resultData.getResult());
                    LOGGER.info("请求内网返回值uuid:{},object:{}", map.get("uuid"), object);
                    if (object != null && CodeUtil.BASE_SUCCESS == object.getInteger("code")) {
                        state = "2"; //上传成功
                    }
                }
                map.put("state", state);
                int times = 0;
                if (StringUtils.isNotBlank(map.get("times"))) {
                    times = Integer.valueOf(map.get("times"));
                }
                map.put("times", times + 1 + "");
                jedis.hmset(key, map);
            } else {
                map.put("state", "3"); //上传失败
                jedis.hmset(key, map);
            }
        }
    }
}
