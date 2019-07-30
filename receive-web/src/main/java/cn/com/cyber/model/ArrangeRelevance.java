package cn.com.cyber.model;

public class ArrangeRelevance extends BaseEntity {
    private Long id;

    private Long arrangeId;

    private Long appId;

    private Long serviceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArrangeId() {
        return arrangeId;
    }

    public void setArrangeId(Long arrangeId) {
        this.arrangeId = arrangeId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}