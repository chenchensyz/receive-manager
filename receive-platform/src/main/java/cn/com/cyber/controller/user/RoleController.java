package cn.com.cyber.controller.user;

import cn.com.cyber.model.Permission;
import cn.com.cyber.model.Role;
import cn.com.cyber.service.PermissionService;
import cn.com.cyber.service.RoleService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping("/getRoleList")
    public String getUserList() {
        return "user/roleList";
    }

    //角色列表数据
    @RequestMapping("/queryRoleListData")
    @ResponseBody
    public RestResponse queryRoleListData(Role role) {
        PageHelper.startPage(role.getPageNum(), role.getPageSize());
        List<Role> roles = roleService.getList(role);
        PageInfo<Role> rolesPage = new PageInfo<Role>(roles);
        return RestResponse.success().setData(roles)
                .setTotal(rolesPage.getTotal()).setPage(rolesPage.getLastPage());
    }

    //跳转角色新增/编辑页面
    @RequestMapping("getRoleInfo")
    public String getRoleInfo(@RequestParam(value = "roleId", defaultValue = "0") Long roleId, Model model) {
        model.addAttribute("roleId", roleId);
        return "user/roleInfo";
    }

    //获取角色编辑数据
    @RequestMapping("getRoleInfoData")
    @ResponseBody
    public RestResponse getRoleInfoData(@RequestParam("roleId") Long roleId) {
        Role role = roleService.getById(roleId);
        if (role == null) {
            int code = CodeUtil.USERINFO_ERR_SELECT;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        List<Permission> permissions = permissionService.getList(new Permission());
        Map<String, Object> map = Maps.newHashMap();
        map.put("role", role);
        map.put("permissions", permissions);
        return RestResponse.success().setData(map);
    }

    //新增或编辑角色
    @RequestMapping("addOrEditRole")
    @ResponseBody
    public RestResponse addOrEditRole(Role sysRole) {
        int count = 0;
        if (sysRole.getPermissions().endsWith(",")) {
            sysRole.setPermissions(sysRole.getPermissions().substring(0, sysRole.getPermissions().lastIndexOf(",")));
        }
        if (sysRole.getId() > 0) {
            count = roleService.update(sysRole);
        } else {
            count = roleService.insert(sysRole);
        }
        if (count == 0) {
            return RestResponse.failure("新增或编辑角色失败");
        }
        return RestResponse.success();
    }

    //删除角色
    @RequestMapping("deleteRole")
    @ResponseBody
    public RestResponse deleteRole(long roleId) {
        if (roleService.deleteRoleById(roleId) == 0) {
            return RestResponse.success();
        }
        return RestResponse.success();
    }
}
