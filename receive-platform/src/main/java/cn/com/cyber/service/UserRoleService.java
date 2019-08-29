package cn.com.cyber.service;

import cn.com.cyber.model.UserRole;

import java.util.List;

public interface UserRoleService {

    UserRole getByUserId(long userId);

    int insert(UserRole userRole);

    int update(UserRole userRole);

    int deleteUserRole(long userId, long roleId);

}
