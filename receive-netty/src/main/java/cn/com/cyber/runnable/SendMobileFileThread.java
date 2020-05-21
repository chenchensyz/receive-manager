package cn.com.cyber.runnable;

/**
 * 文件传输服务--定时任务
 */

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.FileUpConnection;
import cn.com.cyber.util.ResultData;
import cn.com.cyber.util.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
                            mobileFileUp(jedis, key);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    continue;
                }
                Thread.sleep(10 * 1000);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    private void mobileFileUp(Jedis jedis, String key) throws FileNotFoundException {
        Map<String, String> map = jedis.hgetAll(key);
        int times = StringUtils.isBlank(map.get("times")) ? 0 : Integer.valueOf(map.get("times"));
        int state = Integer.valueOf(map.get("state"));
        long sendTime = StringUtils.isBlank(map.get("sendTime")) ? System.currentTimeMillis() : Long.valueOf(map.get("sendTime"));
        if (times < 3) {
            if (state == 0 || (System.currentTimeMillis() - sendTime > 1000 * 60 * 20 && state == 1)) {
                Map<String, InputStream> fileMap = Maps.newHashMap();
                File file = new File(map.get("filePath"));

                Map<String, String> requestParamsMap = new HashMap<String, String>();
                requestParamsMap.put("introduction", map.get("introduction"));
                requestParamsMap.put("size", map.get("fileSize"));
                requestParamsMap.put("appKey", map.get("appKey"));
                requestParamsMap.put("serviceKey", map.get("serviceKey"));

                fileMap.put(file.getName(), new FileInputStream(file));
                ResultData resultData = FileUpConnection.postFileUp(CodeUtil.PLATFORM_FILEUP_URL, requestParamsMap, fileMap);
                if (resultData != null && CodeUtil.HTTP_OK == resultData.getCode()) {
                    JSONObject object = JSONObject.parseObject(resultData.getResult());
                    LOGGER.info("请求内网返回值uuid:{},object:{}", map.get("uuid"), object);
                    if (object != null && CodeUtil.BASE_SUCCESS == object.getInteger("code")) {
                        jedis.del(key); //上传成功
                    } else {
                        map.put("state",  "1");
                        map.put("times", times + 1 + "");
                        map.put("sendTime", System.currentTimeMillis() + "");
                        jedis.hmset(key, map);
                    }
                }
            }
        } else {
            map.put("state", "3"); //上传失败
            jedis.hmset(key, map);
            jedis.expire(key, 259200); //3天
        }
    }
}
