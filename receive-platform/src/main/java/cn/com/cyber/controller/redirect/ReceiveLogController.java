package cn.com.cyber.controller.redirect;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.ReceiveLog;
import cn.com.cyber.service.ReceiveLogService;
import cn.com.cyber.util.RestResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/receiveLog")
public class ReceiveLogController extends BaseController {

    @Autowired
    private ReceiveLogService receiveLogService;

    //日志列表
    @RequestMapping(value = "list")
    public String receiveLogList() {
        return "receiveLog/receiveLogList";
    }

    @RequestMapping("/queryReceiveLogListData")
    @ResponseBody
    public RestResponse queryReceiveLogListData(ReceiveLog receiveLog) {
        if (getShiroUser().source == 1) {
            receiveLog.setCreator(getShiroUser().id.intValue());
        }
        RestResponse restResponse = receiveLogService.getReceiveLogList(receiveLog);
        return restResponse;
    }

    //服务监控列表
    @RequestMapping(value = "/controlList")
    public String controlList() {
        return "receiveLog/controlList";
    }

    @RequestMapping("/controlListData")
    @ResponseBody
    public RestResponse controlListData(AppService appService) {
        PageHelper.startPage(appService.getPageNum(), appService.getPageSize());
        List<AppService> controlListData = receiveLogService.getControlListData(appService);
        PageInfo<AppService> controlListPage = new PageInfo<AppService>(controlListData);
        return RestResponse.success().setData(controlListData).setTotal(controlListPage.getTotal()).
                setPage(controlListPage.getLastPage());
    }
}
