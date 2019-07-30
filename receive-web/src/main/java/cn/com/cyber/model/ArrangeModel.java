package cn.com.cyber.model;

import java.util.List;

public class ArrangeModel {

    private Long id;

    private String title;

    private List<ArrangeModel> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ArrangeModel> getChildren() {
        return children;
    }

    public void setChildren(List<ArrangeModel> children) {
        this.children = children;
    }
}
