package cn.com.cyber.model;

public class SysRole extends BaseEntity {
    private Long id;

    private String roleCode;

    private String roleName;

    private String introduction;

    private String permissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPermissions() {
        return permissions == null ? "" : permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}