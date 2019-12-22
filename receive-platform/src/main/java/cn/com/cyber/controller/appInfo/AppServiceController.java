package cn.com.cyber.controller.appInfo;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.CodeInfo;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.service.CodeInfoService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/appService")
public class AppServiceController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppServiceController.class);

    @Autowired
    private AppServiceService appServiceService;

    @Autowired
    private CodeInfoService codeInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("list")
    public String getAppInfoList() {
        return "appInfo/appServiceList";
    }

    //获取接口数据
    @RequestMapping("queryAppServiceListData")
    @ResponseBody
    public RestResponse queryAppInfoListData(AppService appService) {
        if (getShiroUser().source == 1) {  //开发者用户
            appService.setCreator(getShiroUser().id);
        }
        int code = CodeUtil.BASE_SUCCESS;
        PageHelper.startPage(appService.getPageNum(), appService.getPageSize());
        List<AppService> appServices = appServiceService.getList(appService);
        PageInfo<AppService> appServicePage = new PageInfo<AppService>(appServices);
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(appServices)
                .setTotal(appServicePage.getTotal()).setPage(appServicePage.getLastPage());
    }

    //获取接口配置项
    @RequestMapping("serviceCodeData")
    @ResponseBody
    public RestResponse getServiceCodeData() {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse restResponse = new RestResponse();
        CodeInfo codeInfo = new CodeInfo();
        //请求方法
        codeInfo.setType(CodeUtil.CODE_METHOD);
        List<CodeInfo> methodList = codeInfoService.getCodeInfoList(codeInfo);
        //请求方法编码
        codeInfo.setType(CodeUtil.CODE_CONTENTTYPE);
        List<CodeInfo> contentTypeList = codeInfoService.getCodeInfoList(codeInfo);
        //编码规则
        codeInfo.setType(CodeUtil.CODEINFO_ENCODED);
        List<CodeInfo> encodedList = codeInfoService.getCodeInfoList(codeInfo);
        //资源类型
        codeInfo.setType(CodeUtil.CODEINFO_SERVICETYPE);
        List<CodeInfo> serviceTypeList = codeInfoService.getCodeInfoList(codeInfo);
        //返回值
        restResponse.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        restResponse.setAny(CodeUtil.CODE_METHOD, methodList);
        restResponse.setAny(CodeUtil.CODE_CONTENTTYPE, contentTypeList);
        restResponse.setAny(CodeUtil.CODEINFO_SERVICETYPE, serviceTypeList);
        restResponse.setAny(CodeUtil.CODEINFO_ENCODED, encodedList);
        return restResponse;
    }

    //跳转接口编辑页面
    @RequestMapping("getAppService")
    public String getAppService(Long appId, Long appServiceId, Model model) {
        model.addAttribute("appId", appId);
        model.addAttribute("appServiceId", appServiceId);
        return "appInfo/appService";
    }

    //获取接口编辑数据
    @RequestMapping("getAppServiceData")
    @ResponseBody
    public RestResponse getAppInfoData(@RequestParam("appServiceId") Long appServiceId) {
        int code = CodeUtil.BASE_SUCCESS;
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
        int code = CodeUtil.BASE_SUCCESS;
        try {
            appServiceService.addOrEditAppService(getShiroUser().id, appService);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }

    @RequestMapping("changeAppService")
    @ResponseBody
    public RestResponse changeAppService(@RequestParam("appServiceIds") String appServiceIds,
                                         @RequestParam("state") int state) {
        LOGGER.info("删除接口appServiceId:{},state:{}", appServiceIds, state);
        int code = CodeUtil.BASE_SUCCESS;
        try {
            appServiceService.changeAppService(appServiceIds, state, getShiroUser().id);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }


    //批量增加接口
    @RequestMapping("uploadMoreService")
    @ResponseBody
    public RestResponse uploadMoreService(MultipartFile file, Long appId) {
        int code = CodeUtil.BASE_SUCCESS;
        try {
            appServiceService.uploadMoreService(file, appId);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }

    //上传文件
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse uploadFile(HttpServletRequest request, String pathSuffix, Long serviceId) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        try {
            String filePath = appServiceService.uploadFile(request, pathSuffix, serviceId);
            rest.setData(filePath);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }


    //我的服务
    @RequestMapping("/onlyList")
    public String onlyList() {
        return "appInfo/serviceOnlyList";
    }

    //待审批接口
    @RequestMapping("/waitList")
    public String waitList() {
        return "appInfo/serviceWaitList";
    }

    //接口注册
    @RequestMapping("/register")
    public String register() {
        return "service/register";
    }

    //服务检索
    @RequestMapping("/search")
    public String search() {
        return "service/search";
    }

    //服务检索接口数据
    @RequestMapping("searchData")
    @ResponseBody
    public RestResponse searchData(AppService appService) {
        PageHelper.startPage(appService.getPageNum(), appService.getPageSize());
        List<AppService> appServices = appServiceService.getList(appService);
        PageInfo<AppService> appServicePage = new PageInfo<AppService>(appServices);
        int code = CodeUtil.BASE_SUCCESS;
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(appServices)
                .setTotal(appServicePage.getTotal()).setPage(appServicePage.getLastPage());
    }
}
