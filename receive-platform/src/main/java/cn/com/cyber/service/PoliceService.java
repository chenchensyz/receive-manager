package cn.com.cyber.service;

import cn.com.cyber.model.TreeModel;

import java.util.List;

public interface PoliceService {

    List<TreeModel> getUserTree(String nodeId);

    List<String> getUserChecked(String userName);

    void saveUserService(String userName, List<TreeModel> params, String creator);

}
