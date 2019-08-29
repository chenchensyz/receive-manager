package cn.com.cyber.service.impl;

import cn.com.cyber.dao.UserRoleMapper;
import cn.com.cyber.dao.UserRoleMapper;
import cn.com.cyber.model.UserRole;
import cn.com.cyber.service.UserRoleService;
import cn.com.cyber.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userRoleService")
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public UserRole getByUserId(long userId) {
        return userRoleMapper.getByUserId(userId);
    }

    @Override
    public int insert(UserRole userRole) {
        return userRoleMapper.insertSelective(userRole);
    }

    @Override
    public int update(UserRole userRole) {
        return userRoleMapper.updateByPrimaryKeySelective(userRole);
    }

    @Override
    public int deleteUserRole(long userId, long roleId) {
        return userRoleMapper.deleteUserRole(userId, roleId);
    }
}
