package cn.com.cyber.service.impl;

import cn.com.cyber.dao.SysRoleMapper;
import cn.com.cyber.model.SysRole;
import cn.com.cyber.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sysRoleService")
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public SysRole getById(Long id) {
        return sysRoleMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SysRole> getList(SysRole sysUser) {
        return sysRoleMapper.getList(sysUser);
    }

    @Override
    public int insert(SysRole sysUser) {
        return sysRoleMapper.insertSelective(sysUser);
    }

    @Override
    public int update(SysRole sysUser) {
        return sysRoleMapper.updateByPrimaryKeySelective(sysUser);
    }

    @Override
    public List<SysRole> getRoleByUserId(long userId) {
        return sysRoleMapper.getRoleByUserId(userId);
    }

    @Override
    public int deleteRoleById(long id) {
        return sysRoleMapper.deleteByPrimaryKey(id);
    }
}
