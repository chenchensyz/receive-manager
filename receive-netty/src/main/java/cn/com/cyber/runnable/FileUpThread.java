package cn.com.cyber.runnable;

/**
 * 文件传输服务--转发内网
 */
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.FileUpConnection;
import cn.com.cyber.util.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class FileUpThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUpThread.class);

    private Charset cs = Charset.forName("UTF-8");

    private String command;

    public FileUpThread(String path) {
        this.command = path;
    }

    @Override
    public void run() {
        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        Map<String, String> map = jedis.hgetAll(command);
        String url = map.get("upUrl");
        String state = "1";
        try {
            Map<String, File> fileMap = new HashMap<String, File>();
            File file = new File(map.get("filePath"));

            Map<String, String> requestParamsMap = new HashMap<String, String>();
            requestParamsMap.put("introduction", map.get("introduction"));
            requestParamsMap.put("size", map.get("fileSize"));

            fileMap.put(file.getName(), file);
            Map<String, Object> resultMap = FileUpConnection.postFileUp(url, requestParamsMap, fileMap);
            if (StringUtils.isNotBlank(resultMap.get("code").toString())
                    && CodeUtil.HTTP_OK == Integer.valueOf(resultMap.get("code").toString())) {
                JSONObject object = JSONObject.parseObject(resultMap.get("result").toString());
                LOGGER.info("请求内网返回值uuid:{},object:{}", map.get("uuid"), object);
                if (object != null && object.get("success") != null && (Boolean) object.get("success")) {
                    state = "2";
                }
            }
            map.put("state", state);
            int times = 0;
            if (StringUtils.isNotBlank(map.get("times"))) {
                times = Integer.valueOf(map.get("times"));
            }
            map.put("times", times + 1 + "");
            jedis.hmset(command, map);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public String toString() {
        return this.command;
    }
}
