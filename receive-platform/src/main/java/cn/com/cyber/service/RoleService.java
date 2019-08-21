package cn.com.cyber.service;

import cn.com.cyber.model.Role;

import java.util.List;

public interface RoleService {

    Role getById(Long id);

    List<Role> getList(Role role);

    int insert(Role role);

    int update(Role role);

    List<Role> getRoleByUserId(long userId);

    int deleteRoleById(long id);
}
