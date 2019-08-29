package cn.com.cyber.controller.company;


import cn.com.cyber.controller.BaseController;
import cn.com.cyber.controller.manager.filter.UserInfoFilter;
import cn.com.cyber.model.Developer;
import cn.com.cyber.model.Role;
import cn.com.cyber.model.User;
import cn.com.cyber.model.UserRole;
import cn.com.cyber.service.DeveloperService;
import cn.com.cyber.service.RoleService;
import cn.com.cyber.service.UserRoleService;
import cn.com.cyber.service.UserService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.EncryptUtils;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/developer")
public class DeveloperController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeveloperController.class);

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    /**
     *跳转开发者列表
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
    @RequestMapping("/queryDeveloperListData")
    @ResponseBody
    public RestResponse queryUserListData(Developer developer) {
        PageHelper.startPage(developer.getPageNum(), developer.getPageSize());
        List<Developer> users = developerService.getDeveloperList(developer);
        PageInfo<Developer> usersPage = new PageInfo<Developer>(users);
        Object parseUserInfos = filterParam(users, UserInfoFilter.INFO_FILTER);
        return RestResponse.success().setData(parseUserInfos)
                .setTotal(usersPage.getTotal()).setPage(usersPage.getLastPage());
    }
}
