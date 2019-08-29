package cn.com.cyber.controller.appInfo;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.TreeModel;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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

    @RequestMapping("list")
    public String getAppInfoList() {
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
    public RestResponse getCheckedService(@RequestParam("appId") Integer appId) {
        int code = CodeUtil.BASE_SUCCESS;
        String checkedService = appInfoService.getCheckedService(appId);
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(checkedService);
    }

    //获取选中接口
    @RequestMapping("saveAppService")
    @ResponseBody
    public RestResponse saveAppService(@RequestBody String param) {
        int code = CodeUtil.BASE_SUCCESS;
        JSONObject jsonObject = JSONObject.parseObject(param);
        Integer appId = jsonObject.getInteger("appId");
        List<TreeModel> params = JSONArray.parseArray(jsonObject.getString("params"), TreeModel.class);
        try {
            appInfoService.saveAppService(appId, params, getShiroUser().userId);
        }catch (ValueRuntimeException e){
            code=(Integer) e.getValue();
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }
}
