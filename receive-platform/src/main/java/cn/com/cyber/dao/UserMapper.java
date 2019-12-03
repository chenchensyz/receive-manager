package cn.com.cyber.dao;

import cn.com.cyber.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User selectUserById(long id);

    int deleteUserState(@Param("userId") String userId, @Param("state") int state);

    User getUserAndRoles(long id);

    User getByUserId(String userId);

    User getValiedUser(String userName);

    int insertUser(User user);

    int updateUser(User user);

   List<User> getUserList(User user);

    int selectAdmin(User user);

}