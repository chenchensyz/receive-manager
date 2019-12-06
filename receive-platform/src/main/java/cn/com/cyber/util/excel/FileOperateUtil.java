package cn.com.cyber.util.excel;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.exception.ValueRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

public class FileOperateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileOperateUtil.class);


    public static String uploadFile(Map<String, MultipartFile> fileMap, String filePath) {
        // 保存路径设置
        File file = new File(filePath);
        // 新建目录
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        // 文件夹是否拥有写权限
        // if(!file.canWrite()){
        // ret.put("error", 1);
        // ret.put("message", "上传目录没有写权限。");
        // return ret;
        // }
        String multipartFileName = null;
        try {
            for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
                // 上传文件名
                MultipartFile mf = entity.getValue();
                // 文件名称名称处理
                multipartFileName = mf.getOriginalFilename();
                File uploadFile = new File(file.getPath() + File.separator + multipartFileName);

                // 如果文件已存在,则删除文件
                if (uploadFile.exists()) {
                    uploadFile.delete();
                }
                // 复制文件到保存地址
                FileCopyUtils.copy(mf.getBytes(), uploadFile);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ValueRuntimeException(CodeUtil.BASE_FILE_ERR_UP);
        }
        return multipartFileName;
    }

    public static void copyFile(File sourceFile, String targetFilePath) {
        if (!sourceFile.exists()) {
            throw new ValueRuntimeException(CodeUtil.BASE_FILE_NULL);
        }
        // 目标文件路径
        File targetPath = new File(targetFilePath);
        File path = new File(targetPath.getParent());
        if (!path.exists()) {
            path.mkdirs();
        }
        // 版本目标文件
        File targetFile = new File(targetFilePath);
        // 拷贝临时文件到目标目录
        try {
            FileCopyUtils.copy(new FileInputStream(sourceFile), new FileOutputStream(targetFile));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ValueRuntimeException(CodeUtil.BASE_FILE_COPY_ERR);
        }
    }

    //删除文件夹 文件
    public static boolean delFile(File file) {
        if (!file.exists()) {
            throw new ValueRuntimeException(CodeUtil.BASE_FILE_NULL);
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }
}