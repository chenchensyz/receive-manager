package cn.com.cyber.controller.redirect;

import cn.com.cyber.model.AppModel;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/file")
public class FileUpController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUpController.class);

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private Environment env;

    @RequestMapping("/getUpload")
    public String getUpload() {
        return "upload";
    }

    @RequestMapping("upload")
    @ResponseBody
    public RestResponse multifileUpload(HttpServletRequest request,
                                        @RequestParam("appKey") String appKey,
                                        @RequestParam("serviceKey") String serviceKey,
                                        @RequestParam("introduction") String introduction,
                                        @RequestParam("size") long size) {
        int msgCode;
        if (StringUtils.isBlank(appKey)) {
            msgCode = CodeUtil.REQUEST_APPKEY_NULL;
            return RestResponse.failure(messageCodeUtil.getMessage(msgCode));
        }
        if (StringUtils.isBlank(serviceKey)) {
            msgCode = CodeUtil.REQUEST_SERVICEKEY_NULL;
            return RestResponse.failure(messageCodeUtil.getMessage(msgCode));
        }

        //根据appKey和serviceKey查询appinfo信息
        AppModel appModel = appInfoService.getAppModel(serviceKey, appKey);
        if (appModel == null) {
            msgCode = CodeUtil.REQUEST_KEY_FILED;
            return RestResponse.failure(messageCodeUtil.getMessage(msgCode));
        }
        if (CodeUtil.APP_STATE_ENABLE != appModel.getAppState()) {
            msgCode = CodeUtil.APPINFO_ERR_UNENABLE;
            return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
        }

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        Jedis jedis = jedisPool.getResource();
        try {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                long fileSize = file.getSize();
                if (fileSize == 0) {
                    continue;
                }
                String uuid = UUID.randomUUID().toString();
                LOGGER.info("fileName:{},fileSize:{}", uuid, fileSize);
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                String filePath = env.getProperty(CodeUtil.FILE_SAVE_PATH) + File.separator + uuid + suffix;
                File dest = new File(filePath);
                if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }

                file.transferTo(dest);
                Map<String, String> map = Maps.newHashMap();
                map.put("fileName", fileName);
                map.put("filePath", filePath);
                map.put("fileSize", fileSize + "");
                map.put("introduction", introduction);
                map.put("upUrl", appModel.getUrlPrefix() + appModel.getUrlSuffix());
//                map.put("upUrl", "http://localhost:8083/netty/file/upload");
                map.put("state", "0");
                map.put("times", "0"); //发送次数
                map.put("uuid", uuid);
                String key = CodeUtil.FILE_JEDIS_PREFIX + uuid;
                jedis.hmset(key, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.failure(e.toString());
        } finally {
            jedis.close();
        }
        return RestResponse.success("添加上传任务成功");
    }

    //将未发送成功重发，state==3的改为state=1
    @RequestMapping("repeatSend")
    @ResponseBody
    public RestResponse repeatSend() {
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys(CodeUtil.FILE_JEDIS_PREFIX + "*");
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                try {
                    Map<String, String> map = jedis.hgetAll(key);
                    if (Integer.valueOf(map.get("state")) == 3) {
                        map.put("state", "1");
                        jedis.hmset(key, map);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    continue;
                }

            }
        }
        return RestResponse.success();
    }

    //删除文件不存在的记录
    @RequestMapping("/editState")
    @ResponseBody
    public RestResponse editState() {
        LOGGER.info("更改redis state:{}");
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys(CodeUtil.FILE_JEDIS_PREFIX + "*");
        for (String key : keys) {
            Map<String, String> map = jedis.hgetAll(key);
            File exfile = new File(map.get("filePath"));
            if (!exfile.exists()) {
                LOGGER.info("文件不存在：{}", key);
                jedis.del(key);
                continue;
            }
        }
        return RestResponse.success();
    }
}
