package cn.com.cyber.controller.manager;

import cn.com.cyber.model.CompanyInfo;
import cn.com.cyber.model.SysUser;
import cn.com.cyber.model.SysUserRole;
import cn.com.cyber.service.CodeInfoService;
import cn.com.cyber.service.CompanyInfoService;
import cn.com.cyber.service.SysUserRoleService;
import cn.com.cyber.service.SysUserService;
import cn.com.cyber.util.CodeInfoUtils;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.EncryptUtils;
import cn.com.cyber.util.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;

@Controller
@RequestMapping("/regiest")
public class RegiestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegiestController.class);

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private CodeInfoService codeInfoService;

    @RequestMapping("toRegiest")
    public String toRegiest() {
        LOGGER.info("跳转注册页面:{}");
        return "regiest";
    }

    @RequestMapping("regiestUser")
    @ResponseBody
    @Transactional
    public RestResponse regiestUser(@RequestParam("companyName") String companyName,
                                    String companyPhone, String companyEmail, String remark,
                                    @RequestParam("cofirmPwd") String cofirmPwd, @Valid SysUser sysUser) {
        LOGGER.info("注册用户:{}");
        SysUser user = sysUserService.getByUserId(sysUser.getUserId());
        if (user != null) {
            return RestResponse.failure("用户名已存在");
        }
        if(!cofirmPwd.equals(sysUser.getPassword())){
            return RestResponse.failure("两次输入密码不一致");
        }
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setCompanyName(companyName);
        companyInfo.setTelephone(companyPhone);
        companyInfo.setEmail(companyEmail);
        companyInfo.setRemark(remark);
        companyInfo.setState(1);
        companyInfo.setOwner(sysUser.getUserId());
        companyInfo.setCreateTime(new Date());
        int count1 = companyInfoService.insert(companyInfo);//保存公司信息
        if (count1 == 0) {
            return RestResponse.failure("添加公司失败");//添加公司失败
        }
        sysUser.setCompanyId(companyInfo.getId());
        sysUser.setSource(1);
        sysUser.setState(1);
        sysUser.setPassword(EncryptUtils.MD5Encode(sysUser.getPassword()));
        sysUser.setCreateTime(new Date());
        int count2 = sysUserService.insert(sysUser);
        if (count2 == 0) {
            return RestResponse.failure("添加用户失败");//添加用户失败
        }
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(sysUser.getId());
        sysUserRole.setRoleId(CodeUtil.ROLE_COMPMANAGER);  //角色2 公司管理员
        int count3 = sysUserRoleService.insert(sysUserRole);
        if (count3 == 0) {
            return RestResponse.failure("添加角色失败");//添加角色失败
        }
        return RestResponse.success();
    }

    @RequestMapping(value = "updateCodeInfo", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public RestResponse updateCodeInfo() {
        LOGGER.info("更新codeinfo");
        CodeInfoUtils.setCodeListMap(codeInfoService.getCodeListMap());
        CodeInfoUtils.setCodeInfoMap(codeInfoService.getCodeInfoMap());
        return RestResponse.success();
    }

}
