package cn.com.cyber.service;

import cn.com.cyber.model.SysUser;

import java.util.List;

public interface SysUserService {

    SysUser getById(Long id);

    List<SysUser> getList(SysUser sysUser);

    int insert(SysUser sysUser);

    int update(SysUser sysUser);

    //平台删除用户
    int deleteUserState(String userId, int state);

    SysUser getUserAndRoles(long id);

    SysUser getByUserId(String userId);
}
