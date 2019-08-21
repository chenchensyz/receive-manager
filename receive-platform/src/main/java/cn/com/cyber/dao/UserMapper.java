package cn.com.cyber.dao;

import cn.com.cyber.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseDao<User> {

    int deleteUserState(@Param("userId") String userId, @Param("state") int state);

    User getUserAndRoles(long id);

    User getByUserId(String userId);

    User getValiedUser(String userName);

}