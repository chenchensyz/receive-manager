package cn.com.cyber.service;

import cn.com.cyber.model.SysRole;

import java.util.List;

public interface SysRoleService {

    SysRole getById(Long id);

    List<SysRole> getList(SysRole sysRole);

    int insert(SysRole sysRole);

    int update(SysRole sysRole);

    List<SysRole> getRoleByUserId(long userId);

    int deleteRoleById(long id);
}
