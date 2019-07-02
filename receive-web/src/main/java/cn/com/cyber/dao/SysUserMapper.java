package cn.com.cyber.dao;

import cn.com.cyber.model.SysUser;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper extends BaseDao<SysUser> {

   int deleteUserState(@Param("userId") String userId, @Param("state") int state);

   SysUser getUserAndRoles(long id);

   SysUser getByUserId(String userId);
}