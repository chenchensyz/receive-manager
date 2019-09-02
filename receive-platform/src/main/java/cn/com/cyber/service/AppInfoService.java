package cn.com.cyber.service;

import cn.com.cyber.model.*;

import java.util.List;
import java.util.Map;

public interface AppInfoService {

    AppInfo getById(Long id);

    List<AppInfo> getList(AppInfo appInfo);

    AppModel getAppModel(String serviceKey, String appKey);

    int getCountAppInfoByState(Long companyId, int state);

    List<TreeModel> getAppServiceTree(Long companyId);

    List<TreeModel> getAppListTree(Long companyId, int state);

    String getCheckedService(Integer appId);

    //接口审核。申请
    void apply(Integer appId, List<TreeModel> params, String creator);

    List<AppServiceRecord> getAppServiceRecordList(AppServiceRecord appServiceRecord);

    List<AppService> getAppServiceByRecordId(Integer recordId);

    void check(AppServiceRecord appServiceRecord);

}
