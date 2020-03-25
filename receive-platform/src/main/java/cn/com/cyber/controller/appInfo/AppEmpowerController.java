package cn.com.cyber.controller.appInfo;

/**
 * 应用权限分配
 */

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.common.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/appEmpower")
public class AppEmpowerController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppEmpowerController.class);

    @Autowired
    private AppServiceService appServiceService;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private Environment environment;

    @RequestMapping("list")
    public String getAppInfoList(Model model) {
        model.addAttribute("push_area", environment.getProperty(CodeUtil.PUSH_AREA));
        return "appInfo/appEmpower";
    }

    //获取接口数据
    @RequestMapping("queryAppServiceListData")
    @ResponseBody
    public RestResponse queryAppInfoListData(AppService appService) {
        PageHelper.startPage(appService.getPageNum(), appService.getPageSize());
        List<AppService> appServices = appServiceService.getList(appService);
        PageInfo<AppService> appServicePage = new PageInfo<AppService>(appServices);
        int code = CodeUtil.BASE_SUCCESS;
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(appServices)
                .setTotal(appServicePage.getTotal()).setPage(appServicePage.getLastPage());
    }

    //获取选中接口
    @RequestMapping("getCheckedService")
    @ResponseBody
    public RestResponse getCheckedService(@RequestParam("appId") Integer appId, @RequestParam("pushArea") Integer pushArea) {
        int code = CodeUtil.BASE_SUCCESS;
        List<String> checkedService = appInfoService.getCheckedService(appId, pushArea);
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(checkedService);
    }

    //管理员授权接口
    @RequestMapping("saveAppService")
    @ResponseBody
    public RestResponse saveAppService(@RequestBody String param) {
        int code = CodeUtil.BASE_SUCCESS;
        try {
            appInfoService.saveAppService(param, getShiroUser().userId);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }


}
