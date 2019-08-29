package cn.com.cyber.service.impl;

import cn.com.cyber.dao.UserMapper;
import cn.com.cyber.model.User;
import cn.com.cyber.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getById(Long id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public List<User> getList(User sysUser) {
        return userMapper.getUserList(sysUser);
    }

    @Override
    public int insert(User sysUser) {
        return userMapper.insertUser(sysUser);
    }

    @Override
    public int update(User sysUser) {
        return userMapper.updateUser(sysUser);
    }

    @Override
    public int deleteUserState(String userId,int state) {
        return userMapper.deleteUserState(userId,state);
    }

    @Override
    public User getUserAndRoles(long id) {
        return userMapper.getUserAndRoles(id);
    }

    @Override
    public User getByUserId(String userId) {
        return userMapper.getByUserId(userId);
    }

    @Override
    public User getValiedUser(String userName) {
        return userMapper.getValiedUser(userName);
    }
}
