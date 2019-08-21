package cn.com.cyber.service;

import cn.com.cyber.model.User;
import cn.com.cyber.model.User;

import java.util.List;

public interface UserService {

    User getById(Long id);

    List<User> getList(User user);

    int insert(User user);

    int update(User user);

    //平台删除用户
    int deleteUserState(String userId, int state);

    User getUserAndRoles(long id);

    User getByUserId(String userId);

    User getValiedUser(String userName);
}
