package cn.com.cyber.model;


import cn.com.cyber.util.DateUtil;

import java.util.Date;

public class AppServiceRecord extends BaseEntity {
    private Integer id;

    private Integer appId;

    private Integer state;

    private String remark;

    private String apply;

    private String approver;

    private Date applyTime;

    private Date approveTime;

    private String appName;

    private String userName;

    private String applyTimeStr;

    private String approveTimeStr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    public String getApplyTimeStr() {
        return applyTime == null ? "" : DateUtil.format(applyTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setApplyTimeStr(String applyTimeStr) {
        this.applyTimeStr = applyTimeStr;
    }

    public String getApproveTimeStr() {
        return approveTime == null ? "" : DateUtil.format(approveTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setApproveTimeStr(String approveTimeStr) {
        this.approveTimeStr = approveTimeStr;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}