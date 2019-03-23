package cn.com.cyber.impl;

import cn.com.cyber.SysPermissionService;
import cn.com.cyber.dao.SysPermissionMapper;
import cn.com.cyber.model.SysPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("sysPermissionService")
public class SysPermissionServiceImpl implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public SysPermission getById(Long id) {
        return sysPermissionMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SysPermission> getList(SysPermission sysUser) {
        return sysPermissionMapper.getList(sysUser);
    }

    @Override
    public int insert(SysPermission sysUser) {
        return sysPermissionMapper.insertSelective(sysUser);
    }

    @Override
    public int update(SysPermission sysUser) {
        return sysPermissionMapper.updateByPrimaryKeySelective(sysUser);
    }

    @Override
    public SysPermission getByParentId(Long parentId) {
        return sysPermissionMapper.selectByParentId(parentId);
    }

    @Override
    public SysPermission selectBySelf(long id) {
        return sysPermissionMapper.selectBySelf(id);
    }

    @Override
    public long getMaxChildId(long parentId) {
        return sysPermissionMapper.getMaxChildId(parentId);
    }

    @Override
    public long delPermission(long id, long parentId) {
        return sysPermissionMapper.delPermission(id, parentId);
    }

    @Override
    public String getPermByUserId(String userId) {
        return sysPermissionMapper.getPermByUserId(userId);
    }

    @Override
    public List<SysPermission> getPermByCode(Set<String> codes) {
        return sysPermissionMapper.getPermByCode(codes);
    }
}
