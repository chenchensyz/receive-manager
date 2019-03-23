package cn.com.cyber.controller.user;

import cn.com.cyber.SysPermissionService;
import cn.com.cyber.SysRoleService;
import cn.com.cyber.model.SysPermission;
import cn.com.cyber.model.SysRole;
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
    private SysRoleService sysRoleService;

    @Autowired
    private SysPermissionService sysPermissionService;

    @RequestMapping("/getRoleList")
    public String getUserList() {
        return "user/roleList";
    }

    @RequestMapping("/queryRoleListData")
    @ResponseBody
    public RestResponse queryRoleListData(SysRole sysRole) {
        LOGGER.info("角色列表数据:{}");
        PageHelper.startPage(sysRole.getPageNum(), sysRole.getPageSize());
        List<SysRole> roles = sysRoleService.getList(sysRole);
        PageInfo<SysRole> rolesPage = new PageInfo<SysRole>(roles);
        return RestResponse.success().setData(roles)
                .setTotal(rolesPage.getTotal()).setPage(rolesPage.getLastPage());
    }

    @RequestMapping("getRoleInfo")
    public String getRoleInfo(@RequestParam(value = "roleId", defaultValue = "0") Long roleId, Model model) {
        LOGGER.info("跳转角色新增/编辑页面:{}", roleId);
        model.addAttribute("roleId", roleId);
        return "user/roleInfo";
    }

    @RequestMapping("getRoleInfoData")
    @ResponseBody
    public RestResponse getRoleInfoData(@RequestParam("roleId") Long roleId) {
        LOGGER.info("获取角色编辑数据:{}", roleId);
        SysRole sysRole = sysRoleService.getById(roleId);
        if (sysRole == null) {
            int code = CodeUtil.USERINFO_ERR_SELECT;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        List<SysPermission> permissions = sysPermissionService.getList(new SysPermission());
        Map<String, Object> map = Maps.newHashMap();
        map.put("role", sysRole);
        map.put("permissions", permissions);
        return RestResponse.success().setData(map);
    }


    @RequestMapping("addOrEditRole")
    @ResponseBody
    public RestResponse addOrEditRole(SysRole sysRole) {
        LOGGER.info("新增或编辑角色:{}", sysRole.getId());
        int count = 0;
        if (sysRole.getPermissions().endsWith(",")) {
            sysRole.setPermissions(sysRole.getPermissions().substring(0, sysRole.getPermissions().lastIndexOf(",")));
        }
        if (sysRole.getId() > 0) {
            count = sysRoleService.update(sysRole);
        } else {
            count = sysRoleService.insert(sysRole);
        }
        if (count == 0) {
            return RestResponse.failure("新增或编辑角色失败");
        }
        return RestResponse.success();
    }

    @RequestMapping("deleteRole")
    @ResponseBody
    public RestResponse deleteRole(long roleId) {
        LOGGER.info("删除角色:{}");
        if (sysRoleService.deleteRoleById(roleId) == 0) {
            return RestResponse.success();
        }
        return RestResponse.success();
    }
}
