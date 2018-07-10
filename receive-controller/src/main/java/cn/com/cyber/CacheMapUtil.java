package cn.com.cyber;

import cn.com.cyber.cache.CacheModel;
import com.google.common.collect.Maps;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CacheMapUtil {

    public static Map<String, CacheModel> cacheMap= Maps.newConcurrentMap();

    public static CacheModel getCacheMap(String key) {
        return cacheMap.get(key);
    }

    public static void setCacheMap(String key, CacheModel cacheModel) {
        cacheMap.put(key, cacheModel);
    }

    public static void deleteCacheMap(String key) {
        cacheMap.remove(key);
    }
}
