package cn.com.cyber.service;

import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.ArrangeInfo;

import java.util.List;
import java.util.Map;

public interface ArrangeInfoService {

    List<ArrangeInfo> getArrangeInfoList(ArrangeInfo arrangeInfo);

    ArrangeInfo getArrangeInfoById(Long id);

    void addOrEditArrangeInfo(String param);

    List<Map<String,Object>> getAppServiceList();

    //查询选中的接口
    List<Long> getServiceCheck(long arrangeId);


    void deleteArrangeInfo(Long arrangeInfoId);
}
