package cn.com.cyber.dao;

import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;

import java.util.List;
import java.util.Map;

public interface AppInfoMapper extends BaseDao<AppInfo> {

    AppModel selectAppModel(Map<String, Object> map);

    int getCountAppKey(String appKey);

    AppInfo getEditById(Long id);

    int getCountAppInfoByState(Map<String, Object> map);

    List<Map<String,Object>> getAppServiceList(Long companyId);
}