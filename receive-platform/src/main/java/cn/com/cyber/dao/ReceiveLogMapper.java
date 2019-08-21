package cn.com.cyber.dao;

import cn.com.cyber.model.ReceiveLog;

import java.util.List;

public interface ReceiveLogMapper{

   int insertReceiveLog(ReceiveLog receiveLog);

   List<ReceiveLog> getReceiveLogList(ReceiveLog receiveLog);
}

