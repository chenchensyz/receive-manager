package cn.com.cyber.service.impl;

import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.dao.ReceiveLogMapper;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.ReceiveLog;
import cn.com.cyber.service.ReceiveLogService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("receiveLogService")
public class ReceiveLogServiceImpl implements ReceiveLogService {

    @Autowired
    private ReceiveLogMapper receiveLogMapper;

    @Autowired
    private AppServiceMapper appServiceMapper;

    @Autowired
    private Environment environment;

    @Override
    public void saveReceiveLog(ReceiveLog receiveLog) {
        int count = receiveLogMapper.insertReceiveLog(receiveLog);
    }

    @Override
    public RestResponse getReceiveLogList(ReceiveLog receiveLog) {
        RestResponse restResponse = new RestResponse();
        int total = receiveLogMapper.getReceiveLogListCount(receiveLog);
        int lastPage = 0;
        int size = receiveLog.getPageSize();
        if (total > 0) {
            if (CodeUtil.MAPPER_DB_ORACLE.equals(environment.getProperty(CodeUtil.MAPPER_DB))) { //oracle数据库
                size = receiveLog.getPageNum() * receiveLog.getPageSize();
            }
            receiveLog.setPageNum(receiveLog.getOffset());
            receiveLog.setPageSize(size);
            List<ReceiveLog> receiveLogList = receiveLogMapper.getReceiveLogList(receiveLog);
            int count = receiveLogList.size();
            lastPage = total / count;
            if (total / count > 0) {
                lastPage += 1;
            }
            restResponse.setData(receiveLogList);
        }
        restResponse.setTotal(Long.valueOf(total));
        restResponse.setPage(lastPage);
        restResponse.setCode(CodeUtil.BASE_SUCCESS);
        restResponse.setPage(receiveLog.getPageNum());
        return restResponse;
    }

    @Override
    public List<AppService> getControlListData(AppService appService) {
        return appServiceMapper.getServiceList(appService);
    }
}
