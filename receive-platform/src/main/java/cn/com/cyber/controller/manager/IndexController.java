package cn.com.cyber.controller.manager;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.Permission;
import cn.com.cyber.model.User;
import cn.com.cyber.service.PermissionService;
import cn.com.cyber.service.UserService;
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
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    //首页
    @RequestMapping()
    public String getIndex() {
        return "index";
    }

    //获取权限菜单
    @RequestMapping("/getPermMenu")
    @ResponseBody
    public RestResponse getPermMenu() {
        List<Permission> permissons = Lists.newArrayList();
        try {
            if (getShiroUser().getPermissions() == null) {
                String permStr = permissionService.getPermByUserId(getShiroUser().userId);
                if (StringUtils.isNotBlank(permStr)) {
                    String[] permissionArr = permStr.split(",");
                    Set<String> set = new HashSet(Arrays.asList(permissionArr));
                    permissons = permissionService.getPermByCode(set);
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

    //修改密码
    @RequestMapping("/modifyPassword")
    @ResponseBody
    public RestResponse modifyPassword(@RequestParam("oldPassword") String oldPassword,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword) {
        try {
            User sysUser = userService.getByUserId(getShiroUser().userId);
            if (!EncryptUtils.MD5Encode(oldPassword).equals(sysUser.getPassword())) { //原密码不匹配
                return RestResponse.failure("原密码不匹配");
            }
            if (!password.equals(confirmPassword)) { //两次输入密码不同
                return RestResponse.failure("两次输入密码不同");
            }
            sysUser.setPassword(EncryptUtils.MD5Encode(confirmPassword));
            sysUser.setUpdateTime(new Date());
            userService.update(sysUser);
        } catch (Exception e) {
            return RestResponse.failure("获取用户失败");
        }
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return RestResponse.success();
    }


//    @RequestMapping("/downLoadText")
//    @ResponseBody
//    public RestResponse downLoadText() {
//        LOGGER.info("下载操作指南");
//        CodeInfo codeInfo = CodeInfoUtils.getCodeByNameAndType().get(CodeUtil.CODE_INSTRUCTIONSPATH + "-" + CodeUtil.CODE_FILE_TYPE);
//        CodeInfo fileCode = CodeInfoUtils.getCodeByNameAndType().get(CodeUtil.FILE_DOWNLOAD_URL + "-" + CodeUtil.CODE_FILE_TYPE);
//        if (codeInfo == null || fileCode == null) {
//            RestResponse.failure("下载失败");
//        }
//        return RestResponse.success().setData(fileCode.getName() + codeInfo.getName());
//    }
}