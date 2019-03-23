package cn.com.cyber.impl;

import cn.com.cyber.SysUserService;
import cn.com.cyber.dao.SysUserMapper;
import cn.com.cyber.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sysUserService")
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SysUser> getList(SysUser sysUser) {
        return sysUserMapper.getList(sysUser);
    }

    @Override
    public int insert(SysUser sysUser) {
        return sysUserMapper.insertSelective(sysUser);
    }

    @Override
    public int update(SysUser sysUser) {
        return sysUserMapper.updateByPrimaryKeySelective(sysUser);
    }

    @Override
    public int deleteUserState(String userId,int state) {
        return sysUserMapper.deleteUserState(userId,state);
    }

    @Override
    public SysUser getUserAndRoles(long id) {
        return sysUserMapper.getUserAndRoles(id);
    }

    @Override
    public SysUser getByUserId(String userId) {
        return sysUserMapper.getByUserId(userId);
    }
}
