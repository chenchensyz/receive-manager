package cn.com.cyber.controller.manager;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.User;
import cn.com.cyber.service.UserService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.EncryptUtils;
import cn.com.cyber.util.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.UnauthorizedException;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;

    //初始化
    @RequestMapping(value = "config")
    @ResponseBody
    public RestResponse config() {
        String title = getApplication(environment, CodeUtil.PLATFORM_TITLE);
        return RestResponse.success().setData(title);
    }

    //跳转登录页
    @RequestMapping("toLogin")
    public String toLogin() {
        return "login";
    }

    //权限登录页
    @RequestMapping("authLogin")
    public String authLogin() {
        return "/error/401";
    }

    //登录
    @RequestMapping("login")
    @ResponseBody
    public RestResponse login(@RequestParam("userId") String userId,
                              @RequestParam("password") String password,
                              @RequestParam("source") String source,
                              HttpServletRequest request) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(password)) {
            return RestResponse.failure("请填写完整登录信息");
        }
        if ("0".equals(source)) {
            getPstoreUser(userId, password);
            password = EncryptUtils.MD5Encode(password);
        }
        UsernamePasswordToken token = new UsernamePasswordToken(userId, password, source);
//        token.setRememberMe(true);
        Subject subject = SecurityUtils.getSubject();
        String msg = "登录失败";
        try {
            subject.login(token);
            if (subject.isAuthenticated()) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", userId);
                map.put("source", getShiroUser().source);
//                isValiedUser(userId);
                return RestResponse.success().setData(map);
            } else {
                request.getSession().removeAttribute("sessionId");
                return RestResponse.failure(msg);
            }
        } catch (IncorrectCredentialsException e) {
            msg = "登录密码错误";
        } catch (ExcessiveAttemptsException e) {
            msg = "登录失败次数过多";
        } catch (LockedAccountException e) {
            msg = "帐号 " + token.getPrincipal() + " 已被锁定";
        } catch (DisabledAccountException e) {
            msg = "帐号 " + token.getPrincipal() + " 已被禁用";
        } catch (ExpiredCredentialsException e) {
            msg = "帐号 " + token.getPrincipal() + " 已过期";
        } catch (UnknownAccountException e) {
            msg = "帐号 " + token.getPrincipal() + " 不存在";
        } catch (UnauthorizedException e) {
            msg = "您没有得到相应的授权！" + e.getMessage();
        }
        return RestResponse.failure(msg);
    }

    //登出
    @RequestMapping(value = "quit")
    public String quit() {
        Subject subject = SecurityUtils.getSubject();
        subject.getSession().setTimeout(0);
        subject.logout();
        return "redirect:/login/toLogin";
    }


    //pstore登录
    @RequestMapping("redirect")
    public String redirect(@RequestParam("userId") String userId,
                           @RequestParam("password") String password,
                           @RequestParam("source") String source,
                           HttpServletRequest request, Model model) {
        Subject subject1 = SecurityUtils.getSubject();
        subject1.getSession().setTimeout(0);
        subject1.logout();
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(password)) {
            model.addAttribute("err", "请填写完整登录信息");
            return "redirect:/index";
        }
        if ("0".equals(source)) {
            getPstoreUser(userId, password);
            password = EncryptUtils.MD5Encode(password);
        }
        UsernamePasswordToken token = new UsernamePasswordToken(userId, password, source);
        Subject subject = SecurityUtils.getSubject();
        String msg = "登录失败";
        try {
            subject.login(token);
            if (subject.isAuthenticated()) {
                return "redirect:/index";
            } else {
                request.getSession().removeAttribute("sessionId");
                model.addAttribute("err", msg);
                return "redirect:/index";
            }
        } catch (IncorrectCredentialsException e) {
            msg = "登录密码错误";
        } catch (ExcessiveAttemptsException e) {
            msg = "登录失败次数过多";
        } catch (LockedAccountException e) {
            msg = "帐号 " + token.getPrincipal() + " 已被锁定";
        } catch (DisabledAccountException e) {
            msg = "帐号 " + token.getPrincipal() + " 已被禁用";
        } catch (ExpiredCredentialsException e) {
            msg = "帐号 " + token.getPrincipal() + " 已过期";
        } catch (UnknownAccountException e) {
            msg = "帐号 " + token.getPrincipal() + " 不存在";
        } catch (UnauthorizedException e) {
            msg = "您没有得到相应的授权！" + e.getMessage();
        }
        model.addAttribute("err", msg);
        return "redirect:/index";
    }

    private void getPstoreUser(String userId, String password) {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        try {
            int i = userService.selectAdmin(user);
            if (i > 0) {
                User localUser = userService.getByUserId(userId);
                String newPass = EncryptUtils.MD5Encode(password);
                int count;
                if (localUser == null) {
                    user.setRoleId(1);
                    user.setPassword(newPass);
                    user.setNickName("pstore用户");
                    count = userService.insert(user);
                } else {
                    localUser.setPassword(newPass);
                    count = userService.update(localUser);
                }
            }
        } catch (Exception e) {
            LOGGER.info("未合并pstore");
        }
    }
}
