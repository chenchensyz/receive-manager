package cn.com.cyber.dao;

import cn.com.cyber.common.BaseDao;
import cn.com.cyber.model.SysRole;

import java.util.List;

public interface SysRoleMapper extends BaseDao<SysRole> {

    List<SysRole> getRoleByUserId(long userId);
}