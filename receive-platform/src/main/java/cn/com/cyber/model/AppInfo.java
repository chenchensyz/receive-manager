package cn.com.cyber.model;


import cn.com.cyber.util.DateUtil;

import java.util.Date;
import java.util.List;

public class AppInfo extends BaseEntity {
    private Long id;

    private String appKey;

    private String appName;

    private Integer state;

    private Long creator;

    private Date createTime;

    private String createTimeStr;

    private List<AppService> services;

    private Integer receiveNum;  //访问量

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
        this.appKey = appKey;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    public List<AppService> getServices() {
        return services;
    }

    public void setServices(List<AppService> services) {
        this.services = services;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Integer getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(Integer receiveNum) {
        this.receiveNum = receiveNum;
    }
}