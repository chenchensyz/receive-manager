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

    private String refuseMsg;

    private String introduce;

    private String remark;

    private Date createTime;

    private String createTimeStr;

    private Date updateTime;

    private String updateTimeStr;

    private String createUser;

    private String updateUser;

    private Integer controlState;

    private String errRemark;

    private Date controlTime;

    private String controlTimeStr;

    private Integer appState;

    private String appName;

    private String appKey;

    private String approveAppName;  //授权的应用

    private String serviceRule;  //编码规则

    private String sourceType;  //资源类型

    private String filePath;  //文件路径后缀

    private Integer receiveNum;  //访问量

    private Integer isOpen;  //接口权限 0：公开 1：私有

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

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getRefuseMsg() {
        return refuseMsg;
    }

    public void setRefuseMsg(String refuseMsg) {
        this.refuseMsg = refuseMsg;
    }

    public String getApproveAppName() {
        return approveAppName;
    }

    public void setApproveAppName(String approveAppName) {
        this.approveAppName = approveAppName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getServiceRule() {
        return serviceRule;
    }

    public void setServiceRule(String serviceRule) {
        this.serviceRule = serviceRule;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(Integer receiveNum) {
        this.receiveNum = receiveNum;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }
}