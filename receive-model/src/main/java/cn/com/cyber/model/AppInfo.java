package cn.com.cyber.model;

import cn.com.cyber.BaseEntity;
import cn.com.cyber.DateUtil;

import java.util.Date;

public class AppInfo extends BaseEntity {
    private Long id;

    private String appKey;

    private String appName;

    private String urlPrefix;

    private Integer state;

    private String introduce;

    private String remark;

    private Long companyId;

    private CompanyInfo companyInfo;

    private String createUser;

    private String lastUpdateUser;

    private Long createUserId;

    private Long lastUpdateUserId;

    private Date createTime;

    private String createTimeStr;

    private Date lastUpdateTime;

    private String lastUpdateTimeStr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey == null ? "" : appKey.trim();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? "" : appName.trim();
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix == null ? "" : urlPrefix.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(Long lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCreateTimeStr() {
        return createTime == null ? "" : DateUtil.format(createTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getLastUpdateTimeStr() {
        return lastUpdateTime == null ? "" : DateUtil.format(lastUpdateTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setLastUpdateTimeStr(String lastUpdateTimeStr) {
        this.lastUpdateTimeStr = lastUpdateTimeStr;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser == null ? "" : lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
    }
}