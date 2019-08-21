package cn.com.cyber.service.impl;

import cn.com.cyber.dao.RoleMapper;
import cn.com.cyber.model.Role;
import cn.com.cyber.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Role getById(Long id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Role> getList(Role role) {
        return roleMapper.getList(role);
    }

    @Override
    public int insert(Role role) {
        return roleMapper.insertSelective(role);
    }

    @Override
    public int update(Role role) {
        return roleMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    public List<Role> getRoleByUserId(long userId) {
        return roleMapper.getRoleByUserId(userId);
    }

    @Override
    public int deleteRoleById(long id) {
        return roleMapper.deleteByPrimaryKey(id);
    }
}
