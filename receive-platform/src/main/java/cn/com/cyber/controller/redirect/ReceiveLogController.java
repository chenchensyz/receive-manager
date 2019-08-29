package cn.com.cyber.controller.redirect;

import cn.com.cyber.controller.BaseController;
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
        PageHelper.startPage(receiveLog.getPageNum(), receiveLog.getPageSize());
        if (getShiroUser().source == 1) {
            receiveLog.setCompanyId(getShiroUser().id.intValue());
        }
        List<ReceiveLog> receiveLogList = receiveLogService.getReceiveLogList(receiveLog);
        PageInfo<ReceiveLog> receiveLogPage = new PageInfo<ReceiveLog>(receiveLogList);
        return RestResponse.success().setData(receiveLogList).setTotal(receiveLogPage.getTotal()).
                setPage(receiveLogPage.getLastPage());
    }


}
