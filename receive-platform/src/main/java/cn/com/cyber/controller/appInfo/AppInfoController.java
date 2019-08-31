package cn.com.cyber.controller.appInfo;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.TreeModel;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.service.UserService;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/appInfo")
public class AppInfoController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoController.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("list")
    public String getAppInfoList(Model model, @RequestParam(value = "state", defaultValue = "1") int state) {
        model.addAttribute("state", state);
        return "appInfo/appInfoList";
    }

    //获取应用列表数据
    @RequestMapping("queryAppInfoListData")
    @ResponseBody
    public RestResponse queryAppInfoListData(AppInfo appInfo) {
        RestResponse rest = new RestResponse();
        int code = CodeUtil.BASE_SUCCESS;
        try {
            if (getShiroUser().source == 1) {
                appInfo.setCreator(getShiroUser().id);
            }
            PageHelper.startPage(appInfo.getPageNum(), appInfo.getPageSize());
            List<AppInfo> appInfos = appInfoService.getList(appInfo);
            PageInfo<AppInfo> appInfoPage = new PageInfo<AppInfo>(appInfos);
            rest.setData(appInfos).setTotal(appInfoPage.getTotal()).setPage(appInfoPage.getLastPage());
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code);
        rest.setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }

    //统计应用数量
    @RequestMapping("queryCountAppInfo")
    @ResponseBody
    public RestResponse queryCountAppInfo() {
        RestResponse rest = new RestResponse();
        int code = CodeUtil.BASE_SUCCESS;
        try {
            long companyId = getShiroUser().id;   //用户id
            int enable = appInfoService.getCountAppInfoByState(companyId, CodeUtil.APP_STATE_ENABLE);
            int register = appInfoService.getCountAppInfoByState(companyId, CodeUtil.APP_STATE_REGISTER);
            int refuse = appInfoService.getCountAppInfoByState(companyId, CodeUtil.APP_STATE_REFUSE);
            int cancel = appInfoService.getCountAppInfoByState(companyId, CodeUtil.APP_STATE_CANCEL);
            Integer[] data = new Integer[]{enable, register, refuse, cancel};
            rest.setData(data);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code);
        rest.setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }

    //获取应用列表数据-接口列表展示
    @RequestMapping("queryAppList")
    @ResponseBody
    public RestResponse queryAppList() {
        RestResponse rest = new RestResponse();
        int code = CodeUtil.BASE_SUCCESS;
        Long companyId = null;
        try {
            if (getShiroUser().source == 1) {
                companyId = getShiroUser().id;
            }
            List<TreeModel> appListTree = appInfoService.getAppListTree(companyId, 1);
            rest.setData(appListTree);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code);
        rest.setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }

    //查询用户可见的应用及接口
    @RequestMapping("appServiceTree")
    @ResponseBody
    public RestResponse getAppServiceTree() {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        rest.setData(appInfoService.getAppServiceTree());
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }
}
