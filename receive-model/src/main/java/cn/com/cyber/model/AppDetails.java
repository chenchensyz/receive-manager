package cn.com.cyber.model;

import cn.com.cyber.BaseEntity;
import java.util.Date;

public class AppDetails extends BaseEntity {
    /**
     * 
     */
    private Long id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * app_info表id
     */
    private Long appId;

    /**
     * 请求地址后缀
     */
    private String urlSuffix;

    /**
     * 请求方式get/post
     */
    private String method;

    /**
     * post请求参数编码格式
     */
    private String contentType;

    /**
     * 
     */
    private Integer state;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

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
     * 接口名称
     * @return name 接口名称
     */
    public String getName() {
        return name;
    }

    /**
     * 接口名称
     * @param name 接口名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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
     * 请求地址后缀
     * @return url_suffix 请求地址后缀
     */
    public String getUrlSuffix() {
        return urlSuffix;
    }

    /**
     * 请求地址后缀
     * @param urlSuffix 请求地址后缀
     */
    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix == null ? null : urlSuffix.trim();
    }

    /**
     * 请求方式get/post
     * @return method 请求方式get/post
     */
    public String getMethod() {
        return method;
    }

    /**
     * 请求方式get/post
     * @param method 请求方式get/post
     */
    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    /**
     * post请求参数编码格式
     * @return content_type post请求参数编码格式
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * post请求参数编码格式
     * @param contentType post请求参数编码格式
     */
    public void setContentType(String contentType) {
        this.contentType = contentType == null ? null : contentType.trim();
    }

    /**
     * 
     * @return state 
     */
    public Integer getState() {
        return state;
    }

    /**
     * 
     * @param state 
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 
     * @return create_time 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 
     * @param createTime 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 
     * @return update_time 
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 
     * @param updateTime 
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}