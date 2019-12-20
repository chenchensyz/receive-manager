package cn.com.cyber.model;

import cn.com.cyber.util.DateUtil;

import java.util.Date;

public class ReceiveLog extends BaseEntity {
    private Long id;

    private String appKey;

    private String serviceKey;

    private Date requestTime;

    private Date responseTime;

    private Integer responseCode;

    private String remark;

    private String params; //请求参数

    private String url;

    private String requestTimeStr;

    private String responseTimeStr;

    private Integer creator;    //用户id

    private String appName;

    private String serviceName;

    private String beginTime;

    private String endTime;

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
        this.appKey = appKey == null ? null : appKey.trim();
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey == null ? null : serviceKey.trim();
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestTimeStr() {
        return requestTime == null ? null : DateUtil.format(requestTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setRequestTimeStr(String requestTimeStr) {
        this.requestTimeStr = requestTimeStr;
    }

    public String getResponseTimeStr() {
        return responseTime == null ? null : DateUtil.format(responseTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setResponseTimeStr(String responseTimeStr) {
        this.responseTimeStr = responseTimeStr;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}