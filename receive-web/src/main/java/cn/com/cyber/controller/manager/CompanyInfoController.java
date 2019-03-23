package cn.com.cyber.controller.manager;

import cn.com.cyber.CompanyInfoService;
import cn.com.cyber.SysUserRoleService;
import cn.com.cyber.SysUserService;
import cn.com.cyber.controller.BaseController;
import cn.com.cyber.controller.manager.filter.CompanyInfoFilter;
import cn.com.cyber.model.CompanyInfo;
import cn.com.cyber.model.SysUser;
import cn.com.cyber.model.SysUserRole;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.EncryptUtils;
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

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/companyInfo")
public class CompanyInfoController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyInfoController.class);

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @RequestMapping("getCompanyList")
    public String getCompanyList() {
        return "company/companyList";
    }

    @RequestMapping("queryCompanyListData")
    @ResponseBody
    public RestResponse queryCompanyListData(CompanyInfo companyInfo) {
        LOGGER.info("获取公司列表数据:{}");
        PageHelper.startPage(companyInfo.getPageNum(), companyInfo.getPageSize());
        List<CompanyInfo> companyInfos = companyInfoService.getList(companyInfo);
        PageInfo<CompanyInfo> companyInfoPage = new PageInfo<CompanyInfo>(companyInfos);
        Object parseCompanyInfos = filterParam(companyInfos, CompanyInfoFilter.INFO_FILTER);
        return RestResponse.success().setData(parseCompanyInfos).setTotal(companyInfoPage.getTotal()).
                setPage(companyInfoPage.getLastPage());
    }

    @RequestMapping("getCompanyInfo")
    public String getCompanyInfo(@RequestParam(value = "compId", defaultValue = "0") long compId, Model model) {
        model.addAttribute("compId", compId);
        return "company/companyInfo";
    }

    @RequestMapping("queryCompanyInfoData")
    @ResponseBody
    public RestResponse queryCompanyInfoData(long compId) {
        LOGGER.info("获取公司数据:{}", compId);
        if (compId == -1) { //开发者列表查询公司详情
            compId = getShiroUser().companyId;
        }
        CompanyInfo companyInfo = companyInfoService.getById(compId);
        if (companyInfo == null) {
            int code = CodeUtil.COMPANYINFO_ERR_SELECT;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("companyInfo", companyInfo);
        map.put("owner", getShiroUser().owner);
        map.put("source", getShiroUser().source);
        return RestResponse.success().setData(map);
    }

    @RequestMapping("addOrEdit")
    @ResponseBody
    public RestResponse addOrEdit(@Valid CompanyInfo companyInfo) {
        int count;
        if (companyInfo.getId() == -1) { //公司管理员修改
            companyInfo.setId(getShiroUser().companyId);
        }
        if (companyInfo != null && companyInfo.getId() > 0) {
            LOGGER.info("编辑公司:{}", companyInfo.getId());
            companyInfo.setUpdateTime(new Date());
            count = companyInfoService.update(companyInfo);
        } else {
            LOGGER.info("添加公司:{}");
            companyInfo.setState(1);
            companyInfo.setCreateTime(new Date());
            count = companyInfoService.insert(companyInfo);
        }
        if (count == 0) {
            int code = CodeUtil.COMPANYINFO_ERR_OPERATION;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        return RestResponse.success();
    }

    @RequestMapping("delCompany")
    @ResponseBody
    public RestResponse delCompany(long companyId, int state) {
        LOGGER.info("删除公司companyId:{},state:{}", companyId, state);
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setId(companyId);
        companyInfo.setState(state);
        int count = companyInfoService.update(companyInfo);
        if (count == 0) {
            int code = CodeUtil.COMPANYINFO_ERR_DELECT;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        return RestResponse.success();
    }

    @RequestMapping("getDeveloperList")
    public String getDeveloperList(Long compId, Model model) {
        LOGGER.info("开发者列表:{}");
        model.addAttribute("compId", compId);
        return "company/compDeveloper";
    }

    @RequestMapping("getDeveloperListData")
    @ResponseBody
    public RestResponse getDeveloperListData(SysUser sysUser) {
        LOGGER.info("开发者列表数据:{}");
        if (getShiroUser().source == 1) {
            sysUser.setCompanyId(getShiroUser().companyId);
        }
        sysUser.setSource(1);
        sysUser.setUserSelf(getShiroUser().userId);  //不查询自己
        PageHelper.startPage(sysUser.getPageNum(), sysUser.getPageSize());
        List<SysUser> users = sysUserService.getList(sysUser);
        PageInfo<SysUser> usersPage = new PageInfo<SysUser>(users);
        return RestResponse.success().setData(users)
                .setTotal(usersPage.getTotal()).setPage(usersPage.getLastPage());
    }


    @RequestMapping("getCompanyUser")
    public String getCompanyUser(@RequestParam(value = "userId", defaultValue = "0") Long userId, Model model) {
        LOGGER.info("跳转开发者详情:{}", userId);
        model.addAttribute("userId", userId);
        return "company/companyUser";
    }

    @RequestMapping("getCompanyUserData")
    @ResponseBody
    public RestResponse getCompanyUserData(@RequestParam("userId") Long userId) {
        LOGGER.info("获取开发者编辑数据:{}", userId);
        int code;
        SysUser sysUser = sysUserService.getUserAndRoles(userId);
        if (sysUser == null) {
            code = CodeUtil.USERINFO_ERR_SELECT;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        return RestResponse.success().setData(sysUser);
    }


    @RequestMapping("addOrEditCompanyUser")
    @ResponseBody
    public RestResponse addOrEditCompanyUser(@Valid SysUser sysUser) {
        LOGGER.info("新增编辑用户:{}", sysUser.getUserId());
        int added;
        if (sysUser != null && sysUser.getId() > 0) {
            sysUser.setUpdateTime(new Date());
            added = sysUserService.update(sysUser);
        } else {
            SysUser user = sysUserService.getByUserId(sysUser.getUserId());
            if (user != null) {
                return RestResponse.failure("用户名已存在");
            }
            sysUser.setState(1);
            sysUser.setSource(1);
            sysUser.setCompanyId(getShiroUser().companyId);
            sysUser.setCreateTime(new Date());
            sysUser.setPassword(EncryptUtils.MD5Encode(CodeUtil.DEFAULT_PASSWORD));
            added = sysUserService.insert(sysUser);
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(sysUser.getId());
            sysUserRole.setRoleId(CodeUtil.ROLE_COMPDEVELOPER);  //角色3 开发者
            sysUserRoleService.insert(sysUserRole);
        }
        if (added == 0) {
            int code = CodeUtil.USERINFO_ERR_ADD;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        return RestResponse.success().setData(sysUser);
    }
}
