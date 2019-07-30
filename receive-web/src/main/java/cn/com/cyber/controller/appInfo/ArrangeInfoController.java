package cn.com.cyber.controller.appInfo;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.ArrangeInfo;
import cn.com.cyber.service.ArrangeInfoService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/arrangeInfo")
public class ArrangeInfoController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArrangeInfoController.class);

    @Autowired
    private ArrangeInfoService arrangeInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;


    @RequestMapping("list")
    public String getArrangeInfoList() {
        return "appInfo/arrangeInfoList";
    }

    //获取编排接口数据
    @RequestMapping("queryArrangeInfoListData")
    @ResponseBody
    public RestResponse queryArrangeInfoListData(ArrangeInfo arrangeInfo) {
        if (getShiroUser().source == 1) { //开发者查询
            arrangeInfo.setCompanyId(getShiroUser().companyId);//用户所在公司的id
        }
        arrangeInfo.setState(1);
        PageHelper.startPage(arrangeInfo.getPageNum(), arrangeInfo.getPageSize());
        List<ArrangeInfo> arrangeInfos = arrangeInfoService.getArrangeInfoList(arrangeInfo);
        PageInfo<ArrangeInfo> ArrangeInfoPage = new PageInfo<ArrangeInfo>(arrangeInfos);
        return RestResponse.success().setData(arrangeInfos)
                .setTotal(ArrangeInfoPage.getTotal()).setPage(ArrangeInfoPage.getLastPage());
    }


    //获取编排接口数据
    @RequestMapping("getArrangeInfoData")
    @ResponseBody
    public RestResponse getArrangeInfoData(@RequestParam("arrangeInfoId") Long arrangeInfoId) {
        int code = CodeUtil.SELECT_SUCCESS;
        RestResponse restResponse = new RestResponse();
        try {
            restResponse.setData(arrangeInfoService.getArrangeInfoById(arrangeInfoId));
            restResponse.setAny("services", arrangeInfoService.getServiceCheck(arrangeInfoId));
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        restResponse.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return restResponse;
    }

    @RequestMapping(value = "addOrEdit")
    @ResponseBody
    public RestResponse addOrEdit(@RequestBody String param) {
        int code = CodeUtil.SELECT_SUCCESS;
        RestResponse restResponse = new RestResponse();
        try {
            arrangeInfoService.addOrEditArrangeInfo(param);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        restResponse.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return restResponse;
    }

    //查询用户可见的应用及接口
    @RequestMapping("getAppServiceList")
    @ResponseBody
    public RestResponse getAppServiceList() {
        int code = CodeUtil.SELECT_SUCCESS;
        RestResponse restResponse = new RestResponse();
        restResponse.setData(arrangeInfoService.getAppServiceList());
        restResponse.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return restResponse;
    }

    @RequestMapping("deleteArrangeInfo")
    @ResponseBody
    public RestResponse deleteArrangeInfo(@RequestParam("arrangeInfoId") Long arrangeInfoId) {
        int code = CodeUtil.SELECT_SUCCESS;
        RestResponse restResponse = new RestResponse();
        try {
            arrangeInfoService.deleteArrangeInfo(arrangeInfoId);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        restResponse.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return restResponse;
    }
}
