package cn.com.cyber.dao;

import cn.com.cyber.model.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleMapper extends BaseDao<UserRole> {

    List<UserRole> getByUserId(long userId);

    int deleteUserRole(@Param("userId") long userId, @Param("roleId") long roleId);

}