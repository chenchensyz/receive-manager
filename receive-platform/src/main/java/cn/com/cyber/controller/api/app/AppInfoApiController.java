package cn.com.cyber.controller.api.app;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/appInfo")
public class AppInfoApiController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoApiController.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private AppServiceService appServiceService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;



    //查询用户可见的应用及接口
    @GetMapping("services/{appKey}")
    public RestResponse getResource(@PathVariable("appKey") String appKey) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        AppService appService=new AppService();
        appService.setAppKey(appKey);
        List<AppService> appServices = appServiceService.getList(appService);
        rest.setData(appServices);
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }

    //查询用户可见的应用及接口
    @RequestMapping("appServiceTree")
    public RestResponse getAppServiceTree(String companyIds) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        rest.setData(appInfoService.getAppServiceTree(companyIds));
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }


}
