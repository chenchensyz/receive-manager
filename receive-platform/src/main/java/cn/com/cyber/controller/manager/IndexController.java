package cn.com.cyber.controller.manager;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.PermModel;
import cn.com.cyber.model.User;
import cn.com.cyber.service.PermissionService;
import cn.com.cyber.service.UserService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.EncryptUtils;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.common.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private Environment environment;

    //首页
    @RequestMapping()
    public String getIndex(Model model) {
        model.addAttribute("userId", getShiroUser().userId);
        model.addAttribute("source", getShiroUser().source);
        return "index";
    }

    //获取权限菜单
    @RequestMapping("/getPermMenu")
    @ResponseBody
    public RestResponse getPermMenu() {
        RestResponse rest = new RestResponse();
        int code = CodeUtil.BASE_SUCCESS;
        Map<String, Object> resultMap = Maps.newHashMap();
        List<PermModel> permissons = Lists.newArrayList();
        try {
            if (getShiroUser().getPermissions() == null) {
                String permStr = permissionService.getPermByRoleId(getShiroUser().roleId);
                if (StringUtils.isNotBlank(permStr)) {
                    String[] permissionArr = permStr.split(",");
                    Set<String> set = new HashSet(Arrays.asList(permissionArr));
                    permissons = permissionService.getPermValidByCode(set);
                    getShiroUser().setPermissions(permissons);
                }
            } else {
                permissons = getShiroUser().getPermissions();
            }
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        resultMap.put("permissons", permissons);
        String title = getApplication(environment, CodeUtil.PLATFORM_TITLE);
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code)).setData(resultMap);
        rest.setAny("title", title);
        rest.setAny("userId", getShiroUser().userId);
        rest.setAny("source", getShiroUser().source);
        int edit = environment.getProperty(CodeUtil.USER_EDIT) != null && Integer.valueOf(environment.getProperty(CodeUtil.USER_EDIT)) == 1 ? 1 : 0;
        rest.setAny("user_edit", edit);
        return rest;
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

    //首页
    @RequestMapping("/home")
    public String getHome() {
        return "home";
    }

    //登出
    @RequestMapping(value = "quit")
    public String quit() {
        Subject subject = SecurityUtils.getSubject();
        subject.getSession().setTimeout(0);
        subject.logout();
        return "redirect:/login/toLogin";
    }
}
