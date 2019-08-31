package cn.com.cyber.controller.appInfo;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
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
@RequestMapping("/appValid")
public class AppValidController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppValidController.class);

    @Autowired
    private AppServiceService appServiceService;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("request/list")
    public String getAppInfoList() {
        return "appInfo/appRequestList";
    }

    //获取应用列表数据
    @RequestMapping("queryAppInfoListData")
    @ResponseBody
    public RestResponse queryAppInfoListData(AppInfo appInfo) {
        RestResponse rest = new RestResponse();
        int code = CodeUtil.BASE_SUCCESS;
        try {
            PageHelper.startPage(appInfo.getPageNum(), appInfo.getPageSize());
            List<AppInfo> appInfos = appInfoService.getList(appInfo);
            PageInfo<AppInfo> appInfoPage = new PageInfo<AppInfo>(appInfos);
            rest.setData(appInfos).setTotal(appInfoPage.getTotal()).setPage(appInfoPage.getLastPage());
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }
}
