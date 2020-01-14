package cn.com.cyber.controller.user;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.Permission;
import cn.com.cyber.service.PermissionService;
import cn.com.cyber.util.common.RestResponse;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/permission")
public class PermissionController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private PermissionService permissionService;

    @RequestMapping("/getPermList")
    public String getPermList(Model model) {
        return "user/permList";
    }

    @RequestMapping("/queryPermListData")
    @ResponseBody
    public RestResponse queryPermListData() {
        Permission permission = new Permission();
        permission.setState(1);
        List<Permission> permList = permissionService.getList(permission);
        int i;
        List<Permission> resultList = Lists.newArrayList(permList);
        for (Permission perm : permList) {
            if (perm.getParentId() == 0) {
                resultList.add(perm);
            }
        }
        return RestResponse.success().setData(permList);
    }

    @RequestMapping("/getPermission")
    public String getPermission(@RequestParam(value = "permId", defaultValue = "0") long permId,
                                @RequestParam(value = "parentId") long parentId, Model model) {
        model.addAttribute("permId", permId);
        model.addAttribute("parentId", parentId);
        return "user/permission";
    }

    //获取权限编辑数据
    @RequestMapping("/getPermissionData")
    @ResponseBody
    public RestResponse getPermissionData(@RequestParam("permId") long permId) {
        Permission permission = permissionService.selectBySelf(permId);
        Map map = transBean2Map(permission, Arrays.asList("parentPerm"));
        if (permission.getParentPerm() != null) {
            map.put("parentPerm", permission.getParentPerm().getName());
        } else {
            map.put("parentPerm", "");
        }
        return RestResponse.success().setData(map);
    }

    //新增或编辑权限
    @RequestMapping("addOrEditPermission")
    @ResponseBody
    public RestResponse addOrEditPermission(Permission sysPermission) {
        int count = 0;
        if (sysPermission.getId() > 0) {
            sysPermission.setUpdateTime(new Date());
            count = permissionService.update(sysPermission);
        } else {
            sysPermission.setCreateTime(new Date());
            sysPermission.setType(0);
            sysPermission.setState(1);
            if (sysPermission.getParentId() > 0) {
                long maxChildId = permissionService.getMaxChildId(sysPermission.getParentId());
                if (maxChildId > 0) {
                    sysPermission.setId(maxChildId + 1);
                } else {
                    sysPermission.setId(sysPermission.getParentId() * 100 + 1);
                }
            } else {
                long maxChildId = permissionService.getMaxChildId(0); //创建根节点
                sysPermission.setId(maxChildId + 1);
            }
            count = permissionService.insert(sysPermission);
        }
        if (count == 0) {
            return RestResponse.failure("新增或编辑权限失败");
        }
        return RestResponse.success();
    }

    //获取权限编辑数据
    @RequestMapping("/getAddCilidPerm")
    @ResponseBody
    public RestResponse getAddCilidPerm(@RequestParam("permId") long permId) {
        Permission permission = permissionService.getById(permId);
        if (permission == null) {
            return RestResponse.failure("获取权限失败");
        }
        return RestResponse.success().setData(permission);
    }

    //删除权限
    @RequestMapping("/delPermission")
    @ResponseBody
    public RestResponse delPermission(@RequestParam("permId") long permId,
                                      @RequestParam("parentId") long parentId) {
        long del = permissionService.delPermission(permId, parentId);
        if (permissionService.getById(parentId) == null) {
            permissionService.delPermission(permId, 1);
        }
        if (del == 0) {
            return RestResponse.failure("删除权限失败");
        }
        return RestResponse.success();
    }
}
