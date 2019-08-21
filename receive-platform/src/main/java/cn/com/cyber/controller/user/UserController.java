package cn.com.cyber.controller.user;


import cn.com.cyber.controller.BaseController;
import cn.com.cyber.controller.manager.filter.UserInfoFilter;
import cn.com.cyber.model.Role;
import cn.com.cyber.model.User;
import cn.com.cyber.model.UserRole;
import cn.com.cyber.service.RoleService;
import cn.com.cyber.service.UserRoleService;
import cn.com.cyber.service.UserService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.EncryptUtils;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    /**
     * 获取用户表数据
     *
     * @return
     */
    @RequestMapping("/getUserList")
    public String getUserList() {
        return "user/userList";
    }

    /**
     * 获取用户表数据
     *
     * @return
     */
    @RequestMapping("/queryUserListData")
    @ResponseBody
    public RestResponse queryUserListData(User user) {
        if (user.getState() == null) {
            user.setState(CodeUtil.APP_STATE_ENABLE);
        }
        user.setSource(0);
        user.setUserSelf(getShiroUser().userId);  //不查询自己
        PageHelper.startPage(user.getPageNum(), user.getPageSize());
        List<User> users = userService.getList(user);
        PageInfo<User> usersPage = new PageInfo<User>(users);
        Object parseUserInfos = filterParam(users, UserInfoFilter.INFO_FILTER);
        return RestResponse.success().setData(parseUserInfos)
                .setTotal(usersPage.getTotal()).setPage(usersPage.getLastPage());
    }

    /**
     * 跳转用户新增/编辑页面
     *
     * @return
     */
    @RequestMapping("getUserInfo")
    public String getUserInfo(@RequestParam(value = "userId", defaultValue = "0") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "user/userInfo";
    }


    /**
     * 获取用户编辑数据
     *
     * @return
     */
    @RequestMapping("getUserInfoData")
    @ResponseBody
    public RestResponse getUserInfoData(@RequestParam("userId") Long userId) {
        int code;
        User user = userService.getUserAndRoles(userId);
        if (user == null) {
            code = CodeUtil.USERINFO_ERR_SELECT;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        Role role = new Role();
        List<Role> roles = roleService.getList(role);
        Map<String, Object> map = Maps.newHashMap();
        user.setPassword(null);
        map.put("user", user);
        map.put("roles", roles);
        return RestResponse.success().setData(map);
    }

    /**
     * 新增编辑用户
     *
     * @return
     */
    @RequestMapping("addOrEditUser")
    @ResponseBody
    public RestResponse addOrEditUser(@Valid User user, @RequestParam("roleIds") String roleIds,
                                      String confirmPassword) {
        int added;
        List<String> roleList = Arrays.asList(roleIds.split(","));
        if (user != null && user.getId() > 0) {
            user.setUpdateTime(new Date());
            added = userService.update(user);
            //删除已保存，本次未勾选的角色
            List<UserRole> userRoles = userRoleService.getByUserId(user.getId());
            for (UserRole userRole : userRoles) {
                if (!roleList.contains(userRole.getRoleId())) {
                    userRoleService.deleteUserRole(user.getId(), userRole.getRoleId());
                } else {
                    roleList.remove(userRole.getRoleId()); //去掉已存在的角色，不做操作
                }
            }
        } else {
            User userSec = userService.getByUserId(user.getUserId());
            if (userSec != null) {
                return RestResponse.failure("用户名已存在");
            }
            if (StringUtils.isBlank(confirmPassword) ||
                    (StringUtils.isNotBlank(confirmPassword) && !confirmPassword.equals(user.getPassword()))) {
                return RestResponse.failure("两次输入密码不符");
            }
            userSec.setState(1);
            userSec.setCreateTime(new Date());
            userSec.setPassword(EncryptUtils.MD5Encode(user.getPassword()));
            added = userService.insert(userSec);
        }

        if (added == 0) {
            int code = CodeUtil.USERINFO_ERR_ADD;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }

        //保存本次勾选的角色
        if (!roleList.isEmpty() && roleList.size() > 0) {
            for (String roleId : roleList) {
                if (StringUtils.isNotBlank(roleId)) {
                    UserRole userRole = new UserRole();
                    userRole.setRoleId(Long.valueOf(roleId));
                    userRole.setUserId(user.getId());
                    userRoleService.insert(userRole);
                }
            }
        }
        return RestResponse.success().setData(user);
    }

    /**
     * 删除用户
     *
     * @return
     */
    @RequestMapping("deleteUser")
    @ResponseBody
    public RestResponse deleteUser(String userId, int state) {
        int del = userService.deleteUserState(userId, state);
        if (del == 0) {
            int code = CodeUtil.USERINFO_ERR_DEL;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        return RestResponse.success();
    }

    /**
     * 修改密码
     *
     * @return
     */
    @RequestMapping("releasePassword")
    @ResponseBody
    public RestResponse releasePassword(long userId, String oldPassword, String newPassword) {
        User user = userService.getById(userId);
        oldPassword = EncryptUtils.MD5Encode(oldPassword);
        if (user != null && !user.getPassword().equals(oldPassword)) {
            return RestResponse.success();
        }
        return RestResponse.success();
    }

    /**
     * 首页 用户详情页面
     *
     * @return
     */
    @RequestMapping("getUserSerf")
    @ResponseBody
    public RestResponse getUserSerf() {
        User user = null;
        try {
            user = userService.getByUserId(getShiroUser().userId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (user == null) {
            return RestResponse.failure("获取用户失败");
        }
        return RestResponse.success().setData(user);
    }

    /**
     * 首页 编辑用户详情
     *
     * @return
     */
    @RequestMapping("editUserSerf")
    @ResponseBody
    public RestResponse editUserSerf(@Valid User user) {
        int count = 0;
        try {
            user.setId(getShiroUser().id);
            count = userService.update(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (count == 0) {
            return RestResponse.failure("编辑用户失败");
        }
        return RestResponse.success();
    }

    //重置密码
    @RequestMapping("reSetPassword")
    @ResponseBody
    public RestResponse reSetPassword(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setPassword(EncryptUtils.MD5Encode(CodeUtil.DEFAULT_PASSWORD));
        int update = userService.update(user);
        if (update == 0) {
            return RestResponse.failure("重置密码失败");
        }
        return RestResponse.success();
    }
}
