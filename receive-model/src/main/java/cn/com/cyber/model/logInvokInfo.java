package cn.com.cyber.model;

import cn.com.cyber.BaseEntity;
import java.util.Date;

public class logInvokInfo extends BaseEntity {
    /**
     * 
     */
    private Long id;

    /**
     * app_info表id
     */
    private Long appId;

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 响应时间
     */
    private Date responseTime;

    /**
     * 
     * @return id 
     */
    public Long getId() {
        return id;
    }

    /**
     * 
     * @param id 
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * app_info表id
     * @return app_id app_info表id
     */
    public Long getAppId() {
        return appId;
    }

    /**
     * app_info表id
     * @param appId app_info表id
     */
    public void setAppId(Long appId) {
        this.appId = appId;
    }

    /**
     * 请求地址
     * @return request_url 请求地址
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * 请求地址
     * @param requestUrl 请求地址
     */
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl == null ? null : requestUrl.trim();
    }

    /**
     * 请求时间
     * @return request_time 请求时间
     */
    public Date getRequestTime() {
        return requestTime;
    }

    /**
     * 请求时间
     * @param requestTime 请求时间
     */
    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    /**
     * 响应时间
     * @return response_time 响应时间
     */
    public Date getResponseTime() {
        return responseTime;
    }

    /**
     * 响应时间
     * @param responseTime 响应时间
     */
    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }
}