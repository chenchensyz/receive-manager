package cn.com.cyber.controller.manager;

import cn.com.cyber.util.EncryptUtils;
import cn.com.cyber.util.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    //跳转登录页
    @RequestMapping("toLogin")
    public String toLogin(HttpServletResponse response) {
        response.setStatus(401);
        return "login";
    }

    //登录
    @RequestMapping("login")
    @ResponseBody
    public RestResponse login(@RequestParam("userId") String userId,
                              @RequestParam("password") String password,
                              HttpServletRequest request) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(password)) {
            return RestResponse.failure("请填写完整登录信息");
        }
        UsernamePasswordToken token = new UsernamePasswordToken(userId, EncryptUtils.MD5Encode(password));
//        token.setRememberMe(true);
        Subject subject = SecurityUtils.getSubject();
        String msg = "登录失败";
        try {
            subject.login(token);
            if (subject.isAuthenticated()) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", userId);
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
}
