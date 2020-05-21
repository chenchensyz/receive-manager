package cn.com.cyber.controller;

import cn.com.cyber.util.*;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/file")
public class FileUpController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUpController.class);

    @Autowired
    private JedisPool jedisPool;

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
                                        @RequestParam("introduction") String introduction, long size) {
        int msgCode;
        if (StringUtils.isBlank(appKey)) {
            msgCode = CodeUtil.REQUEST_APPKEY_NULL;
            return RestResponse.failure(messageCodeUtil.getMessage(msgCode));
        }
        if (StringUtils.isBlank(serviceKey)) {
            msgCode = CodeUtil.REQUEST_SERVICEKEY_NULL;
            return RestResponse.failure(messageCodeUtil.getMessage(msgCode));
        }

        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.JEDIS_APPVALID_INDEX); //2号
        try {
//            String upUrl = validAppAndService(jedis, appKey, serviceKey);
//            if (upUrl == null) { //验证appkey和servicekey
//                msgCode = CodeUtil.REQUEST_KEY_FILED;
//                return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
//            }

            List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
            if(CollectionUtils.isEmpty(files)){
                return RestResponse.failure("请添加文件后重试");
            }

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
                map.put("appKey", appKey);
                map.put("serviceKey", serviceKey);
                map.put("state", "0");
                map.put("times", "0"); //发送次数
                map.put("uuid", uuid);
                String key = CodeUtil.JEDIS_FILE_PREFIX + uuid;
                jedis.hmset(key, map);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return RestResponse.failure(e.toString());
        } finally {
            jedis.close();
        }
        return RestResponse.success("添加上传任务成功");
    }

    //验证成功的服务url
    private String getVcCode(String appKey, String serviceKey) {
        return CodeUtil.JEDIS_APPVALID_CODE + appKey + "-" + serviceKey;
    }

    //验证appkey和servicekey
    private String validAppAndService(Jedis jedis, String appKey, String serviceKey) {
        String upUrl = jedis.get(getVcCode(appKey, serviceKey));
        if (StringUtils.isBlank(upUrl)) {
            String url = env.getProperty(CodeUtil.PLATFORM_URL) + CodeUtil.PLATFORM_APP_VALID_URL;
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("appKey", appKey);
            jsonParam.put("serviceKey", serviceKey);
            ResultData resultData = HttpConnection.httpRequest(url, CodeUtil.RESPONSE_POST, CodeUtil.CONTEXT_JSON, jsonParam.toString(), null, null);
            if (resultData != null && CodeUtil.HTTP_OK == resultData.getCode()) {
                JSONObject jsonObject = JSONObject.parseObject(resultData.getResult());
                if (CodeUtil.BASE_SUCCESS == jsonObject.getInteger("code")) {
                    upUrl = jsonObject.getString("data");
                    jedis.set(getVcCode(appKey, serviceKey), upUrl);
                }
            }
        }
        return upUrl;
    }


    //将未发送成功重发，state==3的改为state=1
    @RequestMapping("repeatSend")
    @ResponseBody
    public RestResponse repeatSend() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.JEDIS_APPVALID_INDEX); //2号
        Set<String> keys = jedis.keys(CodeUtil.JEDIS_FILE_PREFIX + "*");
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
        jedis.select(CodeUtil.JEDIS_APPVALID_INDEX); //2号
        Set<String> keys = jedis.keys(CodeUtil.JEDIS_FILE_PREFIX + "*");
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


    @RequestMapping(value = "/photo/{pmUserId:.+}", method = RequestMethod.GET)
    public void getPmUserPhoto(HttpServletRequest request, HttpServletResponse response,
                               @PathVariable("pmUserId") String userId) {
        String type = request.getHeader("type");
        String path = "http://localhost:8082/pmmanage/api/pmuser/photo/xieg";
//        String path = "http://localhost:8082/pmmanage/pmuser/initdomains?pmUserId=xieg";
        ResultData resultData = HttpConnection.httpRequest(path, "GET",
                null, null, CodeUtil.RESPONSE_FILE_TYPE, null);
        if (resultData.getCode() == CodeUtil.HTTP_OK) {
            JSONObject result = JSONObject.parseObject(resultData.getResult());
            byte[] resultbytes = Base64.decodeBase64(result.get("responseData").toString().getBytes(CodeUtil.cs));
            setResponseImage(response, resultbytes);
            return;
        }
        System.out.println(resultData);
    }

}
