package cn.com.cyber.dao;

import cn.com.cyber.common.BaseDao;
import cn.com.cyber.model.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserRoleMapper extends BaseDao<SysUserRole> {

    List<SysUserRole> getByUserId(long userId);

    int deleteUserRole(@Param("userId") long userId, @Param("roleId") long roleId);
}