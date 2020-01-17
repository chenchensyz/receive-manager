package cn.com.cyber.model;

public class AppModel extends BaseEntity {

    private String appKey;

    private String serviceKey;

    private Integer appId;

    private String apply;

    private Integer recordId;

    private String userName;

    private Integer pushArea; //接口发布区域

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

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getPushArea() {
        return pushArea;
    }

    public void setPushArea(Integer pushArea) {
        this.pushArea = pushArea;
    }

}
