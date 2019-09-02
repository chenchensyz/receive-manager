package cn.com.cyber.service;

import cn.com.cyber.model.AppService;
import cn.com.cyber.model.ReceiveLog;

import java.util.List;

public interface ReceiveLogService {

    void saveReceiveLog(ReceiveLog receiveLog);

    List<ReceiveLog> getReceiveLogList(ReceiveLog receiveLog);

    List<AppService> getControlListData(AppService appService);

}
