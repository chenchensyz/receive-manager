package cn.com.cyber.dao;

import cn.com.cyber.model.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface PermissionMapper extends BaseDao<Permission> {

    Permission selectByParentId(Long parentId);

    Permission selectBySelf(long id);

    long getMaxChildId(long parentId);

    long delPermission(@Param("id") long id, @Param("parentId") long parentId);

    String getPermByRoleId(Integer roleId);

    List<Permission> getPermByCode(@Param("codes") Set<String> codes);
}