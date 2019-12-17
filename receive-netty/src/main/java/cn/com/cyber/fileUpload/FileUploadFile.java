package cn.com.cyber.fileUpload;

/**
 * 文件传输-封装对象
 */

import java.io.Serializable;

public class FileUploadFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private String filePath;// 文件
    private String fileName;// 文件名
    private int fileSize;// 读取大小
    private String introduction;// 描述信息
    private String appKey;// appKey
    private String serviceKey;// serviceKey
    private String uuid;// 描述信息
    private byte[] bytes;// 文件字节码
    private int startPos;// 读取开始位置
    private int endPos;// 读取结束位置

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }
}
