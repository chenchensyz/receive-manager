package cn.com.cyber.api.app;

/**
 * 应用权限分配
 */

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.common.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appInfo")
public class AppInfoApiController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoApiController.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;


    //查询用户可见的应用及接口
    @RequestMapping("appServiceTree")
    public RestResponse getAppServiceTree(Long companyId) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        rest.setData(appInfoService.getAppServiceTree(companyId));
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }


}
