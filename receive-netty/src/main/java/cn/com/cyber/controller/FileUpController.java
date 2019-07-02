package cn.com.cyber.controller;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpConnection;
import cn.com.cyber.util.RestResponse;
import com.alibaba.fastjson.JSONObject;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
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

@Controller
@RequestMapping("/file")
public class FileUpController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUpController.class);

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private Environment env;

    @RequestMapping("/getUpload")
    public String getUpload() {
        return "upload";
    }

    @RequestMapping("upload")
    @ResponseBody
    public RestResponse multifileUpload(HttpServletRequest request,
                                        @RequestParam("introduction") String introduction,
                                        @RequestParam("size") long size) {

        LOGGER.info("上传文件...{}", size);
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            int fileSize = (int) file.getSize();
            System.out.println(fileName + "-->" + fileSize);
            if (fileSize == 0) {
                continue;
            }
            String filePath = env.getProperty(CodeUtil.FILE_SAVE_PATH) + File.separator + "new" + File.separator + fileName;
            File dest = new File(filePath);
            if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                dest.getParentFile().mkdir();
            }
            try {
                file.transferTo(dest);
            } catch (Exception e) {
                e.printStackTrace();
                return RestResponse.failure(e.toString());
            }
        }
        return RestResponse.success();
    }

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


    @RequestMapping(value = "/photo/{pmUserId:.+}", method = RequestMethod.GET)
    public void getPmUserPhoto(HttpServletRequest request, HttpServletResponse response,
                               @PathVariable("pmUserId") String userId) {
        String type = request.getHeader("type");
        String path = "http://localhost:8082/pmmanage/api/pmuser/photo/xieg";
//        String path = "http://localhost:8082/pmmanage/pmuser/initdomains?pmUserId=xieg";
        Map<String, Object> map = HttpConnection.httpRequest(path, "GET",
                null, null, CodeUtil.RESPONSE_FILE_TYPE, null);
        if ((Integer) map.get("code") == 200) {
            JSONObject result = JSONObject.parseObject(map.get("result").toString());
            byte[] resultbytes = Base64.decodeBase64(result.get("responseData").toString().getBytes(CodeUtil.cs));
            setResponseImage(response, resultbytes);
            return;
        }
        System.out.println(map);
    }

}
