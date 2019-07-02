package cn.com.cyber.dao;

import cn.com.cyber.model.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysPermissionMapper extends BaseDao<SysPermission> {

    SysPermission selectByParentId(Long parentId);

    SysPermission selectBySelf(long id);

    long getMaxChildId(long parentId);

    long delPermission(@Param("id") long id, @Param("parentId") long parentId);

    String getPermByUserId(String userId);

    List<SysPermission> getPermByCode(@Param("codes") Set<String> codes);
}