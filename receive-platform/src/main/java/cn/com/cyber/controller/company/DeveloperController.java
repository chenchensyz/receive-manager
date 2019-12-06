package cn.com.cyber.controller.company;


import cn.com.cyber.controller.BaseController;
import cn.com.cyber.controller.manager.filter.UserInfoFilter;
import cn.com.cyber.model.Developer;
import cn.com.cyber.service.DeveloperService;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/developer")
public class DeveloperController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeveloperController.class);

    @Autowired
    private DeveloperService developerService;

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
