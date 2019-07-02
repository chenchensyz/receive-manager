package cn.com.cyber.service;

import cn.com.cyber.model.SysUserRole;

import java.util.List;

public interface SysUserRoleService {

    List<SysUserRole> getByUserId(long userId);

    int insert(SysUserRole sysUserRole);

    int update(SysUserRole sysUserRole);

    int deleteUserRole(long userId, long roleId);
}
