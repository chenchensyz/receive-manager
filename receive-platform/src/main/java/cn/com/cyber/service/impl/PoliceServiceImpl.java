package cn.com.cyber.service.impl;

import cn.com.cyber.dao.AppInfoMapper;
import cn.com.cyber.dao.PoliceMapper;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.model.TreeModel;
import cn.com.cyber.service.PoliceService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("policeService")
@Transactional
public class PoliceServiceImpl implements PoliceService {

    @Autowired
    private PoliceMapper policeMapper;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Override
    public List<TreeModel> getUserTree(String nodeId) {
        if (StringUtils.isBlank(nodeId)) {
            nodeId = messageCodeUtil.getMessage(CodeUtil.DEFAULT_DEPARTMENT);  //顶层部门
        }
        List<TreeModel> userTree = policeMapper.getUserTree(nodeId);
        for (TreeModel user : userTree) {
            if ("d".equals(user.getBasicData())) {  //部门
                List<TreeModel> childTree = policeMapper.getUserTree(user.getId());
                user.setChildren(childTree);
            }
        }
        return userTree;
    }

    @Override
    public String getUserChecked(String userName) {
        return policeMapper.getUserChecked(userName);
    }

    @Override
    public void saveUserService(String userName, List<TreeModel> params, String creator) {
        List<AppModel> appModelList = Lists.newArrayList();
        for (TreeModel model : params) {
            if (StringUtils.isBlank(model.getParentId()) || "null".equals(model.getParentId())) {
                continue;
            }
            AppModel appModel = new AppModel();
            appModel.setAppKey(model.getBasicData().replaceAll("\\\"", ""));
            appModel.setServiceKey(model.getParentId());
            appModel.setCreator(creator);
            appModel.setUserName(userName);
            appModelList.add(appModel);
        }
        policeMapper.deleteUserServiceByUserName(userName);
        policeMapper.saveUserService(appModelList);
    }
}
