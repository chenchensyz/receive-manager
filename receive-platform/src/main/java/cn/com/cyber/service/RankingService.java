package cn.com.cyber.service;

import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;

import java.util.List;
import java.util.Map;

public interface RankingService {

    //统计排行
    List<AppInfo> getReceiveAppRanking();

    Map<String,Object> inletCount();

    Map<String,Object> receiveLogRanking(String startTime, String endTime, List<String> dateList);
}
