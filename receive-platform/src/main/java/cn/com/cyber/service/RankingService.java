package cn.com.cyber.service;

import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppService;

import java.util.List;
import java.util.Map;

public interface RankingService {

    //统计排行
    List<AppInfo> getReceiveAppRanking();

    Map<String,Object> inletCount(Long creator);

    Map<String,Object> receiveLogRanking(Long creator,String startTime, String endTime, List<String> dateList);

    //统计接口访问排行
    List<AppService> getReceiveServiceRanking(Long creator);
}
