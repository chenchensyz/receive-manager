package cn.com.cyber.controller.appInfo;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.CodeInfo;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.util.CodeInfoUtils;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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
    public String getAppInfoList() {
        return "appInfo/appServiceList";
    }

    //获取接口数据
    @RequestMapping("queryAppServiceListData")
    @ResponseBody
    public RestResponse queryAppInfoListData(AppService appService) {
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
        int code = CodeUtil.SELECT_SUCCESS;
        try {
            appServiceService.addOrEditAppService(getShiroUser().id, appService);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
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

    @RequestMapping("changeAppService")
    @ResponseBody
    public RestResponse changeAppService(@RequestParam("appServiceIds") String appServiceIds,
                                         @RequestParam("state") int state) {
        LOGGER.info("删除接口appServiceId:{},state:{}", appServiceIds, state);
        int code = CodeUtil.SELECT_SUCCESS;
        try {
            appServiceService.changeAppService(appServiceIds, state, getShiroUser().id);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }


    //批量增加接口
    @RequestMapping("uploadServiceFile")
    @ResponseBody
    public RestResponse uploadServiceFile(MultipartFile file, Long appId) {
        int code = CodeUtil.SELECT_SUCCESS;
        try {
            appServiceService.uploadServiceFile(file, appId);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }

    //下载示例文件
    @RequestMapping("/downLoadServiceExcel")
    @ResponseBody
    public RestResponse downLoadServiceExcel() {
        CodeInfo codeInfo = CodeInfoUtils.getCodeByNameAndType().get(CodeUtil.CODE_SERVICE_EXCEL + "-" + CodeUtil.CODE_FILE_TYPE);
        CodeInfo fileCode = CodeInfoUtils.getCodeByNameAndType().get(CodeUtil.FILE_DOWNLOAD_URL + "-" + CodeUtil.CODE_FILE_TYPE);
        if (codeInfo == null || fileCode == null) {
            RestResponse.failure("下载失败");
        }
        return RestResponse.success().setData(fileCode.getName() + codeInfo.getName());
    }

}
