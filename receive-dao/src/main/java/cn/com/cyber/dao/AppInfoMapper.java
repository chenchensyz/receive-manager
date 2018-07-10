package cn.com.cyber.dao;

import cn.com.cyber.common.BaseDao;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;

import java.util.Map;

public interface AppInfoMapper extends BaseDao<AppInfo> {

    AppInfo selectByAppAndService(Map<String,Object> map);

    AppModel selectAppModel(Map<String,Object> map);
}