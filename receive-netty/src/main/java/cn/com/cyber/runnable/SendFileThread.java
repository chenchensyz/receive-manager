package cn.com.cyber.runnable;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Set;

public class SendFileThread implements Runnable {


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
                    e.printStackTrace();
                    continue;
                }
                Thread.sleep(300 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }


}
