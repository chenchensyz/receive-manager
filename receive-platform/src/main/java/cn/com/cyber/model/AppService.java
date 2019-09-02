package cn.com.cyber.model;


import cn.com.cyber.util.DateUtil;

import java.util.Date;

public class AppService extends BaseEntity {
    private Long id;

    private String serviceName;

    private String serviceKey;

    private Long appId;

    private String urlSuffix;

    private String method;

    private String contentType;

    private Integer serviceType;

    private Integer state;

    private Long creator;

    private Long reviser;

    private Date createTime;

    private String createTimeStr;

    private Date updateTime;

    private String updateTimeStr;

    private String createUser;

    private String updateUser;

    private Integer appState;

    private String appName;

    private Integer controlState;

    private String errRemark;

    private Date controlTime;

    private String controlTimeStr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getUrlSuffix() {
        return urlSuffix;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Long getReviser() {
        return reviser;
    }

    public void setReviser(Long reviser) {
        this.reviser = reviser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeStr() {
        return createTime == null ? "" : DateUtil.format(createTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTimeStr() {
        return updateTime == null ? "" : DateUtil.format(updateTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Integer getAppState() {
        return appState;
    }

    public void setAppState(Integer appState) {
        this.appState = appState;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    public Integer getControlState() {
        return controlState;
    }

    public void setControlState(Integer controlState) {
        this.controlState = controlState;
    }

    public String getErrRemark() {
        return errRemark;
    }

    public void setErrRemark(String errRemark) {
        this.errRemark = errRemark;
    }

    public Date getControlTime() {
        return controlTime;
    }

    public void setControlTime(Date controlTime) {
        this.controlTime = controlTime;
    }

    public String getControlTimeStr() {
        return controlTime == null ? "" : DateUtil.format(controlTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setControlTimeStr(String controlTimeStr) {
        this.controlTimeStr = controlTimeStr;
    }
}