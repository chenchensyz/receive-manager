package cn.com.cyber.controller.manager;

import cn.com.cyber.SysPermissionService;
import cn.com.cyber.SysUserService;
import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.CodeInfo;
import cn.com.cyber.model.SysPermission;
import cn.com.cyber.model.SysUser;
import cn.com.cyber.util.CodeInfoUtils;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.EncryptUtils;
import cn.com.cyber.util.RestResponse;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/index")
public class IndexController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private SysPermissionService sysPermissionService;

    @Autowired
    private SysUserService sysUserService;

    @RequestMapping()
    public String getIndex() {
        LOGGER.info("首页:{}");
        return "index";
    }

    @RequestMapping("/getPermMenu")
    @ResponseBody
    public RestResponse getPermMenu() {
        List<SysPermission> permissons = Lists.newArrayList();
        try {
            if (getShiroUser().getPermissions() == null) {
                LOGGER.info("获取权限菜单userId:{}",getShiroUser().userId);
                String permStr = sysPermissionService.getPermByUserId(getShiroUser().userId);
                if (StringUtils.isNotBlank(permStr)) {
                    String[] permissionArr = permStr.split(",");
                    Set<String> set = new HashSet(Arrays.asList(permissionArr));
                    permissons = sysPermissionService.getPermByCode(set);
                    getShiroUser().setPermissions(permissons);
                }
            } else {
                permissons = getShiroUser().getPermissions();
            }
        } catch (NullPointerException e) {
            return RestResponse.failure("请联系管理员分配权限");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (permissons.size() == 0) {
            return RestResponse.failure("请联系管理员分配权限");
        }
        return RestResponse.success().setData(permissons);
    }

    @RequestMapping("/modifyPassword")
    @ResponseBody
    public RestResponse modifyPassword(@RequestParam("oldPassword") String oldPassword,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword) {
        LOGGER.info("修改密码");
        try {
            SysUser sysUser = sysUserService.getByUserId(getShiroUser().userId);
            if (!EncryptUtils.MD5Encode(oldPassword).equals(sysUser.getPassword())) { //原密码不匹配
                return RestResponse.failure("原密码不匹配");
            }
            if (!password.equals(confirmPassword)) { //两次输入密码不同
                return RestResponse.failure("两次输入密码不同");
            }
            sysUser.setPassword(EncryptUtils.MD5Encode(confirmPassword));
            sysUser.setUpdateTime(new Date());
            sysUserService.update(sysUser);
        } catch (Exception e) {
            return RestResponse.failure("获取用户失败");
        }
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return RestResponse.success();
    }


    @RequestMapping("/downLoadText")
    @ResponseBody
    public RestResponse downLoadText() {
        LOGGER.info("下载操作指南");
        CodeInfo codeInfo = CodeInfoUtils.getCodeByNameAndType().get(CodeUtil.CODE_INSTRUCTIONSPATH + "-" + CodeUtil.CODE_FILE_TYPE);
        if (codeInfo == null) {
            RestResponse.failure("下载失败");
        }
        return RestResponse.success().setData("/" + CodeUtil.CODE_FILE_TYPE + "/" + codeInfo.getName());
    }
}
