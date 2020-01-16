package cn.com.cyber.api.developer;

/**
 * 应用权限分配
 */

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.DeveloperValid;
import cn.com.cyber.service.DeveloperService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.common.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/developer")
public class DeveloperApiController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeveloperApiController.class);

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    //验证用户
    @RequestMapping("valid")
    public RestResponse validDeveloper(@RequestBody DeveloperValid developerValid) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        try {
            rest = developerService.validDeveloper(rest, developerValid);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }
}
