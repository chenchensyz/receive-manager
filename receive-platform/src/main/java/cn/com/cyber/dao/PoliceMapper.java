package cn.com.cyber.dao;

import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.model.TreeModel;

import java.util.List;

public interface PoliceMapper extends BaseDao<AppInfo> {

    List<TreeModel> getUserTree(String nodeId);

    String getUserChecked(String userName);

    int deleteUserServiceByUserName(String userName);

   int saveUserService(List<AppModel> appModelList);
}