package cn.com.cyber.service;

import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.model.TreeModel;

import java.util.List;
import java.util.Map;

public interface AppInfoService {

    AppInfo getById(Long id);

    List<AppInfo> getList(AppInfo appInfo);

    int insert(AppInfo appInfo);

    int update(AppInfo appInfo);

    AppModel getAppModel(String serviceKey, String appKey);

    int getCountAppInfoByState(long companyId, int state);

    List<TreeModel> getAppServiceTree();

    List<TreeModel> getAppListTree(Long companyId, int state);

    String getCheckedService(Integer appId);

    void saveAppService(Integer appId, List<TreeModel> params, String creator);

}
