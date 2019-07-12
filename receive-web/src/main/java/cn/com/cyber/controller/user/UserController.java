package cn.com.cyber.controller.user;


import cn.com.cyber.controller.BaseController;
import cn.com.cyber.controller.manager.filter.UserInfoFilter;
import cn.com.cyber.model.SysRole;
import cn.com.cyber.model.SysUser;
import cn.com.cyber.model.SysUserRole;
import cn.com.cyber.service.SysRoleService;
import cn.com.cyber.service.SysUserRoleService;
import cn.com.cyber.service.SysUserService;
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
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    /**
     * 获取用户表数据
     * @return
     */
    @RequestMapping("/getUserList")
    public String getUserList() {
        return "user/userList";
    }

    /**
     * 获取用户表数据
     * @return
     */
    @RequestMapping("/queryUserListData")
    @ResponseBody
    public RestResponse queryUserListData(SysUser sysUser) {
        if (sysUser.getState() == null) {
            sysUser.setState(CodeUtil.APP_STATE_ENABLE);
        }
        sysUser.setSource(0);
        sysUser.setUserSelf(getShiroUser().userId);  //不查询自己
        PageHelper.startPage(sysUser.getPageNum(), sysUser.getPageSize());
        List<SysUser> users = sysUserService.getList(sysUser);
        PageInfo<SysUser> usersPage = new PageInfo<SysUser>(users);
        Object parseUserInfos = filterParam(users, UserInfoFilter.INFO_FILTER);
        return RestResponse.success().setData(parseUserInfos)
                .setTotal(usersPage.getTotal()).setPage(usersPage.getLastPage());
    }

    /**
     * 跳转用户新增/编辑页面
     * @return
     */
    @RequestMapping("getUserInfo")
    public String getUserInfo(@RequestParam(value = "userId", defaultValue = "0") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "user/userInfo";
    }


    /**
     * 获取用户编辑数据
     * @return
     */
    @RequestMapping("getUserInfoData")
    @ResponseBody
    public RestResponse getUserInfoData(@RequestParam("userId") Long userId) {
        int code;
        SysUser sysUser = sysUserService.getUserAndRoles(userId);
        if (sysUser == null) {
            code = CodeUtil.USERINFO_ERR_SELECT;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        SysRole sysRole = new SysRole();
        List<SysRole> roles = sysRoleService.getList(sysRole);
        Map<String, Object> map = Maps.newHashMap();
        sysUser.setPassword(null);
        map.put("user", sysUser);
        map.put("roles", roles);
        return RestResponse.success().setData(map);
    }

    /**
     * 新增编辑用户
     * @return
     */
    @RequestMapping("addOrEditUser")
    @ResponseBody
    public RestResponse addOrEditUser(@Valid SysUser sysUser, @RequestParam("roleIds") String roleIds,
                                      String confirmPassword) {
        int added;
        List<String> roleList = Arrays.asList(roleIds.split(","));
        if (sysUser != null && sysUser.getId() > 0) {
            sysUser.setUpdateTime(new Date());
            added = sysUserService.update(sysUser);
            //删除已保存，本次未勾选的角色
            List<SysUserRole> userRoles = sysUserRoleService.getByUserId(sysUser.getId());
            for (SysUserRole userRole : userRoles) {
                if (!roleList.contains(userRole.getRoleId())) {
                    sysUserRoleService.deleteUserRole(sysUser.getId(), userRole.getRoleId());
                } else {
                    roleList.remove(userRole.getRoleId()); //去掉已存在的角色，不做操作
                }
            }
        } else {
            SysUser user = sysUserService.getByUserId(sysUser.getUserId());
            if (user != null) {
                return RestResponse.failure("用户名已存在");
            }
            if (StringUtils.isBlank(confirmPassword) ||
                    (StringUtils.isNotBlank(confirmPassword) && !confirmPassword.equals(sysUser.getPassword()))) {
                return RestResponse.failure("两次输入密码不符");
            }
            sysUser.setState(1);
            sysUser.setSource(getShiroUser().source);
            sysUser.setCompanyId(getShiroUser().companyId);
            sysUser.setCreateTime(new Date());
            sysUser.setPassword(EncryptUtils.MD5Encode(sysUser.getPassword()));
            added = sysUserService.insert(sysUser);
        }

        if (added == 0) {
            int code = CodeUtil.USERINFO_ERR_ADD;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }

        //保存本次勾选的角色
        if (!roleList.isEmpty() && roleList.size() > 0) {
            for (String roleId : roleList) {
                if (StringUtils.isNotBlank(roleId)) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setRoleId(Long.valueOf(roleId));
                    sysUserRole.setUserId(sysUser.getId());
                    sysUserRoleService.insert(sysUserRole);
                }
            }
        }

        return RestResponse.success().setData(sysUser);
    }

    /**
     * 删除用户
     * @return
     */
    @RequestMapping("deleteUser")
    @ResponseBody
    public RestResponse deleteUser(String userId, int state) {
        int del = sysUserService.deleteUserState(userId, state);
        if (del == 0) {
            int code = CodeUtil.USERINFO_ERR_DEL;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        return RestResponse.success();
    }

    /**
     * 修改密码
     * @return
     */
    @RequestMapping("releasePassword")
    @ResponseBody
    public RestResponse releasePassword(long userId, String oldPassword, String newPassword) {
        SysUser sysUser = sysUserService.getById(userId);
        oldPassword = EncryptUtils.MD5Encode(oldPassword);
        if (sysUser != null && !sysUser.getPassword().equals(oldPassword)) {
            return RestResponse.success();
        }
        return RestResponse.success();
    }

    /**
     * 首页 用户详情页面
     * @return
     */
    @RequestMapping("getUserSerf")
    @ResponseBody
    public RestResponse getUserSerf() {
        SysUser sysUser = null;
        try {
            sysUser = sysUserService.getByUserId(getShiroUser().userId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (sysUser == null) {
            return RestResponse.failure("获取用户失败");
        }
        return RestResponse.success().setData(sysUser);
    }

    /**
     * 首页 编辑用户详情
     * @return
     */
    @RequestMapping("editUserSerf")
    @ResponseBody
    public RestResponse editUserSerf(@Valid SysUser sysUser) {
        int count = 0;
        try {
            sysUser.setId(getShiroUser().id);
            count = sysUserService.update(sysUser);
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
        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        sysUser.setPassword(EncryptUtils.MD5Encode(CodeUtil.DEFAULT_PASSWORD));
        int update = sysUserService.update(sysUser);
        if (update == 0) {
            return RestResponse.failure("重置密码失败");
        }
        return RestResponse.success();
    }
}
