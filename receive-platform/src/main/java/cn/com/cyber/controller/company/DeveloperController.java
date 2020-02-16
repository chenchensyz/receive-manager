package cn.com.cyber.controller.company;


import cn.com.cyber.controller.BaseController;
import cn.com.cyber.controller.company.filter.ValidListFilter;
import cn.com.cyber.model.Developer;
import cn.com.cyber.model.DeveloperValid;
import cn.com.cyber.service.DeveloperService;
import cn.com.cyber.service.DeveloperValidService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.common.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/developer")
public class DeveloperController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeveloperController.class);

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private DeveloperValidService developerValidService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    /**
     * 跳转开发者列表
     */
    @RequestMapping("/developerList")
    public String getUserList() {
        return "company/developerList";
    }

    /**
     * 获取开发者数据
     *
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public RestResponse queryUserListData(Developer developer) {
        PageHelper.startPage(developer.getPageNum(), developer.getPageSize());
        List<Developer> users = developerService.getDeveloperList(developer);
        PageInfo<Developer> usersPage = new PageInfo<Developer>(users);
        return RestResponse.success().setData(users)
                .setTotal(usersPage.getTotal()).setPage(usersPage.getLastPage());
    }

    /**
     * 修改密码
     */
    @RequestMapping("/changePwd")
    @ResponseBody
    public RestResponse changePwd(@RequestParam String old, @RequestParam String password, @RequestParam String confirm) {
        RestResponse rest = new RestResponse();
        int code = CodeUtil.BASE_SUCCESS;
        try {
            developerService.changePwd(getShiroUser().userId, old, password, confirm);
            Subject subject = SecurityUtils.getSubject();
            subject.getSession().setTimeout(0);
            subject.logout();
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code);
        rest.setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }

    /**
     * 获取开发者跨区域对应列表
     */
    @RequestMapping("/valid/list")
    @ResponseBody
    public RestResponse validList(DeveloperValid developerValid) {
        PageHelper.startPage(developerValid.getPageNum(), developerValid.getPageSize());
        developerValid.setUserId(getShiroUser().userId);
        List<DeveloperValid> validList = developerValidService.getDeveloperValidList(developerValid);
        PageInfo<DeveloperValid> validPage = new PageInfo<>(validList);
        Object validListFilter = filterParam(validList, ValidListFilter.INFO_FILTER);
        return RestResponse.success().setData(validListFilter).setTotal(validPage.getTotal()).setPage(validPage.getLastPage());
    }

    /**
     * 开发者跨区域登陆
     */
    @RequestMapping("/valid/login")
    @ResponseBody
    public RestResponse validLogin(@RequestBody DeveloperValid developerValid) {
        RestResponse rest = new RestResponse();
        int code = CodeUtil.BASE_SUCCESS;
        try {
            developerValid.setUserId(getShiroUser().userId);
            DeveloperValid valid = developerValidService.validLogin(developerValid);
            rest.setData(valid);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code);
        rest.setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }

    /**
     * 开发者跨区域记录删除
     */
    @RequestMapping("/valid/delete")
    @ResponseBody
    public RestResponse validDelete(Integer id) {
        RestResponse rest = new RestResponse();
        int code = CodeUtil.BASE_SUCCESS;
        try {
            developerValidService.validDelete(id);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code);
        rest.setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }
}
