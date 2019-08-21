package cn.com.cyber.service.impl;

import cn.com.cyber.dao.PermissionMapper;
import cn.com.cyber.model.Permission;
import cn.com.cyber.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("permissionService")
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Permission getById(Long id) {
        return permissionMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Permission> getList(Permission permission) {
        return permissionMapper.getList(permission);
    }

    @Override
    public int insert(Permission permission) {
        return permissionMapper.insertSelective(permission);
    }

    @Override
    public int update(Permission permission) {
        return permissionMapper.updateByPrimaryKeySelective(permission);
    }

    @Override
    public Permission getByParentId(Long parentId) {
        return permissionMapper.selectByParentId(parentId);
    }

    @Override
    public Permission selectBySelf(long id) {
        return permissionMapper.selectBySelf(id);
    }

    @Override
    public long getMaxChildId(long parentId) {
        return permissionMapper.getMaxChildId(parentId);
    }

    @Override
    public long delPermission(long id, long parentId) {
        return permissionMapper.delPermission(id, parentId);
    }

    @Override
    public String getPermByUserId(String userId) {
        return permissionMapper.getPermByUserId(userId);
    }

    @Override
    public List<Permission> getPermByCode(Set<String> codes) {
        return permissionMapper.getPermByCode(codes);
    }
}
