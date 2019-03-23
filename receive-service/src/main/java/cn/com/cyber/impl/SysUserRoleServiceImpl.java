package cn.com.cyber.impl;

import cn.com.cyber.SysUserRoleService;
import cn.com.cyber.dao.SysUserRoleMapper;
import cn.com.cyber.model.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sysUserRoleService")
public class SysUserRoleServiceImpl implements SysUserRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<SysUserRole> getByUserId(long userId) {
        return sysUserRoleMapper.getByUserId(userId);
    }

    @Override
    public int insert(SysUserRole sysUserRole) {
        return sysUserRoleMapper.insertSelective(sysUserRole);
    }

    @Override
    public int update(SysUserRole sysUserRole) {
        return sysUserRoleMapper.updateByPrimaryKeySelective(sysUserRole);
    }

    @Override
    public int deleteUserRole(long userId, long roleId) {
        return sysUserRoleMapper.deleteUserRole(userId, roleId);
    }
}
