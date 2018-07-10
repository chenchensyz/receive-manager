package cn.com.cyber;

import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;

import java.util.List;

public interface AppInfoService {

    AppInfo getById(Long id);

    List<AppInfo> getList(AppInfo appInfo);

    int insert(AppInfo appInfo);

    int update(AppInfo appInfo);

    AppInfo getByAppAndService(String appKey,String serviceKey);

    AppModel selectAppModel(String urlSuffix,long appId);

}
