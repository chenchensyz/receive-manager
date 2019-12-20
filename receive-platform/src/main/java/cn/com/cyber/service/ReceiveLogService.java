package cn.com.cyber.service;

import cn.com.cyber.model.AppService;
import cn.com.cyber.model.ReceiveLog;
import cn.com.cyber.util.RestResponse;

import java.util.List;

public interface ReceiveLogService {

    void saveReceiveLog(ReceiveLog receiveLog);

    RestResponse getReceiveLogList(ReceiveLog receiveLog);

    List<AppService> getControlListData(AppService appService);

}
