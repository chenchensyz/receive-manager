package cn.com.cyber.controller.company;

import cn.com.cyber.model.Developer;
import cn.com.cyber.service.DeveloperService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.common.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;


    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    @RequestMapping
    public String toRegister() {
        return "register";
    }

    /**
     * 注册开发者
     *
     * @param cofirmPwd
     * @param developer
     * @return
     */
    @RequestMapping("registerUser")
    @ResponseBody
    public RestResponse registerUser(@RequestParam("cofirmPwd") String cofirmPwd, @Valid Developer developer) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        try {
            developerService.addDeveloper(cofirmPwd, developer);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }


}
