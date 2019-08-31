package cn.com.cyber.model;

import java.util.List;

public class PermModel {

    private Integer id;

    private String title;

    private String href;

    private String icon;

    private String target = "_self";

    private List<PermModel> child;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<PermModel> getChild() {
        return child;
    }

    public void setChild(List<PermModel> child) {
        this.child = child;
    }
}
