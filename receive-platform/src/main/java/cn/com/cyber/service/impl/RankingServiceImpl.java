package cn.com.cyber.service.impl;

import cn.com.cyber.dao.AppInfoMapper;
import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.dao.ReceiveLogMapper;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.service.RankingService;
import com.google.common.collect.Maps;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("rankingService")
public class RankingServiceImpl implements RankingService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Autowired
    private AppServiceMapper appServiceMapper;

    @Autowired
    private ReceiveLogMapper receiveLogMapper;

    @Override
    public List<AppInfo> getReceiveAppRanking() {
        return appInfoMapper.getReceiveAppRanking();
    }

    @Override
    public Map<String, Object> inletCount() {
        int countAppInfo = appInfoMapper.getCountAppInfoByState(null, 1);
        int countService = appServiceMapper.getCountServiceKey(null, 1);
        int receiveLogCount = receiveLogMapper.getReceiveLogCount();
        Map<String, Object> map = Maps.newHashMap();
        map.put("countAppInfo", countAppInfo);
        map.put("countService", countService);
        map.put("receiveLogCount", receiveLogCount);
        return map;
    }

    @Override
    public Map<String, Object> receiveLogRanking(String startTime, String endTime) {
        List<Integer> sucCount = receiveLogMapper.getReceiveLogRanking(startTime, endTime, 1);
        List<Integer> errCount = receiveLogMapper.getReceiveLogRanking(startTime, endTime, 2);
        Map<String, Object> map = Maps.newHashMap();
        map.put("sucCount", sucCount);
        map.put("errCount", errCount);
        return map;
    }
}
