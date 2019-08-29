package cn.com.cyber.service;

import cn.com.cyber.model.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    Permission getById(Long id);

    List<Permission> getList(Permission permission);

    int insert(Permission permission);

    int update(Permission permission);

    Permission getByParentId(Long parentId);

    Permission selectBySelf(long id);

    //获取子权限最大值
    long getMaxChildId(long parentId);

    long delPermission(long id, long parentId);

    String getPermByRoleId(Integer roleId);

    List<Permission> getPermByCode(Set<String> codes);
}
