package cn.com.cyber.runnable;

/**
 * 文件传输服务--定时任务
 */

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Set;

public class SendFileThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendFileThread.class);

    @Override
    public void run() {
        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        try {
            while (true) {
                try {
                    Set<String> keys = jedis.keys(CodeUtil.FILE_JEDIS_PREFIX + "*");
                    if (keys != null && keys.size() > 0) {
                        for (String key : keys) {
                            Map<String, String> map = jedis.hgetAll(key);
                            if (Integer.valueOf(map.get("state")) < 2 && Integer.valueOf(map.get("times")) < 3) {
                                if ((StringUtils.isNotBlank(map.get("times")) && Integer.valueOf(map.get("times")) < 3)
                                        || StringUtils.isBlank(map.get("times"))) {
                                    MyThreadPool.getThreadPool().execute(new FileUpThread(key));
                                } else {
                                    map.put("state", "3"); //上传失败
                                    jedis.hmset(key, map);
                                }
                            }
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


}
