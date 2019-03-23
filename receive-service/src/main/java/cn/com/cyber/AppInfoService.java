package cn.com.cyber;

import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;

import java.util.List;

public interface AppInfoService {

    AppInfo getById(Long id);

    List<AppInfo> getList(AppInfo appInfo);

    int insert(AppInfo appInfo);

    int update(AppInfo appInfo);

    AppModel getAppModel(String serviceKey, String appKey);

    int getCountAppKey(String appKey);

    AppInfo getEditById(Long id);

    int deleteByAppId(long appId);

    int getCountAppInfoByState(long companyId, int state);

}
