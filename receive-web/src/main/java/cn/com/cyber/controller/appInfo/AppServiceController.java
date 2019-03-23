package cn.com.cyber.controller.appInfo;

import cn.com.cyber.AppServiceService;
import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.CodeInfo;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpClient;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/appService")
public class AppServiceController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppServiceController.class);

    @Autowired
    private AppServiceService appServiceService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("getAppServiceList")
    public String getAppInfoList(@RequestParam(value = "appId") Long appId, Model model) {
        model.addAttribute("appId", appId);
        return "appInfo/appServiceList";
    }

    //获取接口数据
    @RequestMapping("queryAppServiceListData")
    @ResponseBody
    public RestResponse queryAppInfoListData(AppService appService) {
        appService.setState(1);
        PageHelper.startPage(appService.getPageNum(), appService.getPageSize());
        List<AppService> appServices = appServiceService.getList(appService);
        PageInfo<AppService> appServicePage = new PageInfo<AppService>(appServices);
        int code = CodeUtil.SELECT_SUCCESS;
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(appServices)
                .setTotal(appServicePage.getTotal()).setPage(appServicePage.getLastPage());
    }

    //跳转接口编辑页面
    @RequestMapping("getAppService")
    public String getAppService(Long appId, @RequestParam(defaultValue = "0") Long appServiceId, Model model) {
        model.addAttribute("appId", appId);
        model.addAttribute("appServiceId", appServiceId);
        return "appInfo/appService";
    }

    //获取接口编辑数据
    @RequestMapping("getAppServiceData")
    @ResponseBody
    public RestResponse getAppInfoData(@RequestParam("appServiceId") Long appServiceId) {
        int code = CodeUtil.SELECT_SUCCESS;
        AppService appService = appServiceService.getEditByServiceId(appServiceId);
        if (appService == null) {
            code = CodeUtil.APPINFO_ERR_SELECT;
            return RestResponse.res(code, messageCodeUtil.getMessage(code));
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(appService);
    }

    @PostMapping(value = "addOrEdit")
    @ResponseBody
    public RestResponse addOrEdit(@Valid AppService appService) {
        int count = 0;
        int code = CodeUtil.SELECT_SUCCESS;
        if (appService.getId() > 0) {
            LOGGER.info("编辑接口:{}", appService.getId());
            appService.setLastUpdateTime(new Date());
            appService.setLastUpdateUserId(getShiroUser().id);
            count = appServiceService.update(appService);
        } else {
            LOGGER.info("新增接口:{}");
            appService.setCreateTime(new Date());
            appService.setState(1);
            String uuid;
            long serviceKey;
            do {
                uuid = HttpClient.getUUID();
                serviceKey = appServiceService.getCountServiceKey(uuid);
            } while (serviceKey > 0);
            appService.setServiceKey(HttpClient.getUUID());
            appService.setCreateUserId(getShiroUser().id);
            count = appServiceService.insert(appService);
        }
        if (count == 0) {
            code = CodeUtil.APPINFO_ERR_OPERATION;
            return RestResponse.res(code, messageCodeUtil.getMessage(code));
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }

    @RequestMapping(value = "getContentType")
    @ResponseBody
    public RestResponse getContentType() {
        Map<String, Object> map = Maps.newHashMap();
        List<CodeInfo> method = getCodeInfoList(CodeUtil.CODE_METHOD);
        map.put("method", method);
        List<CodeInfo> contentType = getCodeInfoList(CodeUtil.CODE_CONTENTTYPE);
        map.put("contentType", contentType);
        return RestResponse.success().setData(map);
    }

    @RequestMapping("deleteAppService")
    @ResponseBody
    public RestResponse deleteAppService(@RequestParam("appServiceId") Long appServiceId,
                                         @RequestParam("state") int state) {
        LOGGER.info("删除接口appServiceId:{},state:{}", appServiceId, state);
        AppService appService = new AppService();
        appService.setId(appServiceId);
        appService.setState(state);
        appService.setLastUpdateUserId(getShiroUser().id);
        int count = appServiceService.update(appService);
        if (count == 0) {
            return RestResponse.failure("操作失败");
        }
        return RestResponse.success();
    }

}
