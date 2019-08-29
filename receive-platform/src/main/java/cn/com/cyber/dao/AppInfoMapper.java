package cn.com.cyber.dao;

import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.model.TreeModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AppInfoMapper extends BaseDao<AppInfo> {

    AppModel selectAppModel(Map<String, Object> map);

    int getCountAppKey(String appKey);

    AppInfo getEditById(Long id);

    int getCountAppInfoByState(@Param("companyId") long companyId, @Param("state") int state);

    List<TreeModel> getAppServiceTree();

    List<TreeModel> getAppListTree(@Param("companyId") Long companyId, @Param("state") int state);

    String getCheckedService(Integer appId);

    List<AppInfo> getReceiveAppRanking();

    int saveAppServiceMore(List<AppModel> appModelList);

    int deleteAppServiceByAppId(Integer appId);
}