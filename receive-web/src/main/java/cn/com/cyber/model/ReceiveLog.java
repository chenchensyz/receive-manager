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

    private String remark; //返回值

    private String params; //请求参数

    private String url;

    private String requestTimeStr;

    private String responseTimeStr;

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
        return requestTime==null?null: DateUtil.format(requestTime,DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setRequestTimeStr(String requestTimeStr) {
        this.requestTimeStr = requestTimeStr;
    }

    public String getResponseTimeStr() {
        return responseTime==null?null: DateUtil.format(responseTime,DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setResponseTimeStr(String responseTimeStr) {
        this.responseTimeStr = responseTimeStr;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}