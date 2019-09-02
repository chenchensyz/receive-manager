package cn.com.cyber.controller.appInfo;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.AppServiceRecord;
import cn.com.cyber.model.TreeModel;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/appValid")
public class AppValidController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppValidController.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("/applyList")
    public String getAppInfoList() {
        return "appInfo/appApplyList";
    }

    //申请选中接口
    @RequestMapping("/apply")
    @ResponseBody
    public RestResponse applyAppService(@RequestBody String param) {
        int code = CodeUtil.BASE_SUCCESS;
        JSONObject jsonObject = JSONObject.parseObject(param);
        Integer appId = jsonObject.getInteger("appId");
        List<TreeModel> params = JSONArray.parseArray(jsonObject.getString("params"), TreeModel.class);
        try {
            appInfoService.apply(appId, params, getShiroUser().userId);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }


    @RequestMapping("/approveList")
    public String getApproveList() {
        return "appInfo/appApproveList";
    }

    //获取待审核接口数据
    @RequestMapping("/approveListData")
    @ResponseBody
    public RestResponse approveListData(AppServiceRecord appServiceRecord) {
        PageHelper.startPage(appServiceRecord.getPageNum(), appServiceRecord.getPageSize());
        List<AppServiceRecord> appServices = appInfoService.getAppServiceRecordList(appServiceRecord);
        PageInfo<AppServiceRecord> appServicePage = new PageInfo<AppServiceRecord>(appServices);
        int code = CodeUtil.BASE_SUCCESS;
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(appServices)
                .setTotal(appServicePage.getTotal()).setPage(appServicePage.getLastPage());
    }

    //根据recordId获取接口
    @RequestMapping("/approveService")
    @ResponseBody
    public RestResponse approveService(Integer recordId) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        try {
            List<AppService> services = appInfoService.getAppServiceByRecordId(recordId);
            rest.setData(services);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }

    //接口审核
    @RequestMapping("/check")
    @ResponseBody
    public RestResponse check(AppServiceRecord appServiceRecord) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        try {
            appServiceRecord.setApprover(getShiroUser().userId);
            appInfoService.check(appServiceRecord);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }
}
