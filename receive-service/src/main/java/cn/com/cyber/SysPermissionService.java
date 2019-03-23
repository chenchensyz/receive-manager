package cn.com.cyber;

import cn.com.cyber.model.SysPermission;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SysPermissionService {

    SysPermission getById(Long id);

    List<SysPermission> getList(SysPermission sysPermission);

    int insert(SysPermission sysPermission);

    int update(SysPermission sysPermission);

    SysPermission getByParentId(Long parentId);

    SysPermission selectBySelf(long id);

    //获取子权限最大值
    long getMaxChildId(long parentId);

    long delPermission(long id,long parentId);

    String getPermByUserId(String userId);

    List<SysPermission> getPermByCode(Set<String> codes);
}
