package cn.com.cyber.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Set;

@Component
public class JedisClient {

    @Autowired
    private JedisPool jedisPool;

    //获取key的value值
    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String str = "";
        try {
            str = jedis.get(key);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public String set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String str = "";
        try {
            str = jedis.set(key, value);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public String setex(String key, int seconds, String value) {
        Jedis jedis = jedisPool.getResource();
        String str = "";
        try {
            str = jedis.setex(key, seconds, value);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public String hmset(String filed, Map<String, String> map) {
        Jedis jedis = jedisPool.getResource();
        String str;
        try {
            str = jedis.hmset(filed, map);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }


    public Map<String, String> hgetAll(String filed) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> map;
        try {
            map = jedis.hgetAll(filed);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public long hset(String key, String filed, String value) {
        Jedis jedis = jedisPool.getResource();
        long success;
        try {
            success = jedis.hset(key, filed, value);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public String hget(String key,String fileds) {
        Jedis jedis = jedisPool.getResource();
        String hget;
        try {
            hget = jedis.hget(key, fileds);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hget;
    }

    public Set<String> keys(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> keys;
        try {
            keys = jedis.keys(key);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return keys;
    }
}