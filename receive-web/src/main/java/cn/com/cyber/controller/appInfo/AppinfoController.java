package cn.com.cyber.controller.appInfo;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.SysUser;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.service.SysUserService;
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
@RequestMapping("/appInfo")
public class AppinfoController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppinfoController.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("getAppInfoList")
    public String getAppInfoList(Model model, @RequestParam(value = "state", defaultValue = "1") int state) {
        try {
            model.addAttribute("state", state);
            model.addAttribute("owner", getShiroUser().owner);
            model.addAttribute("source", getShiroUser().source);
        } catch (NullPointerException e) {
            return "appInfo/appInfoList";
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return "appInfo/appInfoList";
    }

    //获取应用列表数据
    @RequestMapping("queryAppInfoListData")
    @ResponseBody
    public RestResponse queryAppInfoListData(AppInfo appInfo) {
        try {
            if (getShiroUser().source == 1) { //开发者查询
                appInfo.setCompanyId(getShiroUser().companyId);//用户所在公司的id
                if (appInfo.getState() == 2) {
                    appInfo.setCreateUserId(getShiroUser().id);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        PageHelper.startPage(appInfo.getPageNum(), appInfo.getPageSize());
        List<AppInfo> appInfos = appInfoService.getList(appInfo);
        PageInfo<AppInfo> appInfoPage = new PageInfo<AppInfo>(appInfos);
        return RestResponse.success().setData(appInfos)
                .setTotal(appInfoPage.getTotal()).setPage(appInfoPage.getLastPage());
    }

    //统计应用数量
    @RequestMapping("queryCountAppInfo")
    @ResponseBody
    public RestResponse queryCountAppInfo() {
        long companyId = getShiroUser().source == 1 ? getShiroUser().companyId : 0;
        int enable = appInfoService.getCountAppInfoByState(companyId, CodeUtil.APP_STATE_ENABLE);
        int register = appInfoService.getCountAppInfoByState(companyId, CodeUtil.APP_STATE_REGISTER);
        int refuse = appInfoService.getCountAppInfoByState(companyId, CodeUtil.APP_STATE_REFUSE);
        int cancel = appInfoService.getCountAppInfoByState(companyId, CodeUtil.APP_STATE_CANCEL);
        Integer[] data = new Integer[]{enable, register, refuse, cancel};
        return RestResponse.success().setData(data);
    }

    //跳转app编辑页面
    @RequestMapping("getAppInfo")
    public String getAppInfoEdit(@RequestParam(value = "appId", defaultValue = "0") Long appId, Model model) {
        model.addAttribute("appId", appId);
        return "appInfo/appInfo";
    }

    //获取app编辑数据
    @RequestMapping("getAppInfoData")
    @ResponseBody
    public RestResponse getAppInfoData(@RequestParam("appId") Long appId) {
        int code = CodeUtil.SELECT_SUCCESS;
        AppInfo appInfo = appInfoService.getEditById(appId);
        if (appInfo == null) {
            code = CodeUtil.APPINFO_ERR_SELECT;
            return RestResponse.failure(messageCodeUtil.getMessage(code));
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("appInfo", appInfo);
        map.put("owner", getShiroUser().owner);
        map.put("source", getShiroUser().source);
        return RestResponse.success(messageCodeUtil.getMessage(code)).setData(map);
    }

    @PostMapping(value = "addOrEdit")
    @ResponseBody
    public RestResponse addOrEdit(@Valid AppInfo appInfo) {
        int count = 0;
        int code = CodeUtil.SELECT_SUCCESS;
        SysUser sysUser = sysUserService.getByUserId(getShiroUser().userId);
        if (getShiroUser().companyId == null) {
            return RestResponse.failure("未找到公司");
        }
        if (appInfo.getId() > 0) {
            LOGGER.info("编辑应用:{}", appInfo.getId());
            if (getShiroUser().source == 1) {
                appInfo.setLastUpdateTime(new Date());
                appInfo.setLastUpdateUserId(sysUser.getId());
            }
            count = appInfoService.update(appInfo);
        } else {
            LOGGER.info("新增应用:{}");
            String uuid;
            long appKey;
            do {
                uuid = HttpClient.getUUID();
                appKey = appInfoService.getCountAppKey(uuid);
            } while (appKey > 0);
            appInfo.setCompanyId(getShiroUser().companyId);
            appInfo.setCreateTime(new Date());
            appInfo.setCreateUserId(sysUser.getId());
            appInfo.setCompanyId(getShiroUser().companyId);
            appInfo.setAppKey(uuid);
            count = appInfoService.insert(appInfo);
        }
        if (count == 0) {
            code = CodeUtil.APPINFO_ERR_OPERATION;
            return RestResponse.res(code, messageCodeUtil.getMessage(code));
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(appInfo.getState());
    }

    @RequestMapping("deleteAppInfo")
    @ResponseBody
    public RestResponse deleteAppInfo(@RequestParam("appId") Long appId,
                                      @RequestParam("state") int state) {
        LOGGER.info("删除appId:{},state:{}", appId, state);
        AppInfo appInfo = new AppInfo();
        appInfo.setId(appId);
        appInfo.setState(state);
        appInfo.setCreateUserId(getShiroUser().id);
        int count = appInfoService.update(appInfo);
        if (count == 0) {
            return RestResponse.failure("操作失败");
        }
        return RestResponse.success();
    }

    //获取应用列表数据-接口列表展示
    @RequestMapping("queryAppList")
    @ResponseBody
    public RestResponse queryAppList() {
        AppInfo appInfo = new AppInfo();
        if (getShiroUser().source == 1) { //开发者查询
            appInfo.setCompanyId(getShiroUser().companyId);//用户所在公司的id
            appInfo.setState(1);
        }
        List<AppInfo> appInfos = appInfoService.getList(appInfo);
        return RestResponse.success().setData(appInfos);
    }
}
