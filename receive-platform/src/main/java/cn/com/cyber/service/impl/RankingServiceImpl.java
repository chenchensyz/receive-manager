package cn.com.cyber.service.impl;

import cn.com.cyber.dao.AppInfoMapper;
import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.dao.ReceiveLogMapper;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppService;
import cn.com.cyber.service.RankingService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Map<String, Object> inletCount(Long creator) {
        int countAppInfo = appInfoMapper.getCountAppInfoByState(creator, 1);
        int countService = appServiceMapper.getCountServiceKey(null, 1, creator);
        int receiveLogCount = receiveLogMapper.getReceiveLogCount(creator);
        Map<String, Object> map = Maps.newHashMap();
        map.put("countAppInfo", countAppInfo);
        map.put("countService", countService);
        map.put("receiveLogCount", receiveLogCount);
        return map;
    }

    @Override
    public Map<String, Object> receiveLogRanking(Long creator, String startTime, String endTime, List<String> dateList) {
        List<Integer> sucCount = receiveLogMapper.getReceiveLogRanking(creator, startTime, endTime, dateList, 1);
        List<Integer> errCount = receiveLogMapper.getReceiveLogRanking(creator, startTime, endTime, dateList, 2);
        Map<String, Object> map = Maps.newHashMap();
        map.put("sucCount", sucCount);
        map.put("errCount", errCount);
        return map;
    }

    @Override
    public List<AppService> getReceiveServiceRanking(Long creator) {
        return appServiceMapper.getReceiveServiceRanking(creator);
    }
}
