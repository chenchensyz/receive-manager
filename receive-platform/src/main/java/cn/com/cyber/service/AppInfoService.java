package cn.com.cyber.service;

import cn.com.cyber.model.*;

import java.util.List;

public interface AppInfoService {

    List<AppInfo> getAppInfoList(AppInfo appInfo);

    void saveAppInfo(Long userId, AppInfo appInfo);

    AppModel getAppModel(String serviceKey, String appKey);

    int getCountAppInfoByState(Long companyId, int state);

    List<TreeModel> getAppServiceTree(Long companyId, Integer pushArea);

    List<TreeModel> getAppListTree(Long companyId, int state);

    List<String> getCheckedService(Integer appId, Integer pushArea);

    //接口审核。申请
    void apply(String param, String creator);

    List<AppServiceRecord> getAppServiceRecordList(AppServiceRecord appServiceRecord);

    List<AppService> getAppServiceByRecordId(Integer recordId);

    void check(AppServiceRecord appServiceRecord);

    List<AppService> getAppValidListData(AppService appService);

    void saveAppService(String param, String creator);
}
