package cn.com.cyber.dao;

import cn.com.cyber.model.Role;

import java.util.List;

public interface RoleMapper extends BaseDao<Role> {

    List<Role> getRoleByUserId(long userId);
}