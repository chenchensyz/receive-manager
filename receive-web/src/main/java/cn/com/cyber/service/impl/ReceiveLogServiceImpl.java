package cn.com.cyber.service.impl;

import cn.com.cyber.dao.ReceiveLogMapper;
import cn.com.cyber.model.ReceiveLog;
import cn.com.cyber.service.ReceiveLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("receiveLogService")
public class ReceiveLogServiceImpl implements ReceiveLogService {

    @Autowired
    private ReceiveLogMapper receiveLogMapper;

    @Override
    public void saveReceiveLog(ReceiveLog receiveLog) {
        receiveLogMapper.insertReceiveLog(receiveLog);
    }

    @Override
    public List<ReceiveLog> getReceiveLogList(ReceiveLog receiveLog) {
        return receiveLogMapper.getReceiveLogList(receiveLog);
    }
}