package cn.com.cyber.service.impl;

import cn.com.cyber.dao.AppInfoMapper;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;
import cn.com.cyber.model.TreeModel;
import cn.com.cyber.service.AppInfoService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("appInfoService")
@Transactional
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Override
    public AppInfo getById(Long id) {
        return appInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<AppInfo> getList(AppInfo appInfo) {
        return appInfoMapper.getList(appInfo);
    }

    @Override
    public int insert(AppInfo appInfo) {
        return appInfoMapper.insertSelective(appInfo);
    }

    @Override
    public int update(AppInfo appInfo) {
        return appInfoMapper.updateByPrimaryKeySelective(appInfo);
    }

    @Override
    public AppModel getAppModel(String serviceKey, String appKey) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appKey", appKey);
        map.put("serviceKey", serviceKey);
        return appInfoMapper.selectAppModel(map);
    }

    @Override
    public int getCountAppInfoByState(long companyId, int state) {
        return appInfoMapper.getCountAppInfoByState(companyId, state);
    }

    @Override
    public List<TreeModel> getAppServiceTree() {
        List<TreeModel> appServiceList = appInfoMapper.getAppServiceTree();
        return appServiceList;
    }

    @Override
    public String getCheckedService(Integer appId) {
        return appInfoMapper.getCheckedService(appId);
    }

    @Override
    public List<TreeModel> getAppListTree(Long companyId, int state) {
        return appInfoMapper.getAppListTree(companyId, state);
    }

    @Override
    @Transactional
    public void saveAppService(Integer appId, List<TreeModel> params, String creator) {
        List<AppModel> appModelList = Lists.newArrayList();
        for (TreeModel model : params) {
            if (StringUtils.isBlank(model.getParentId()) || "null".equals(model.getParentId())) {
                continue;
            }
            AppModel appModel = new AppModel();
            appModel.setAppKey(model.getBasicData().replaceAll("\\\"", ""));
            appModel.setServiceKey(model.getParentId());
            appModel.setCreator(creator);
            appModel.setAppId(appId);
            appModelList.add(appModel);
        }
        if(appModelList.isEmpty()){
           return;
        }
        appInfoMapper.deleteAppServiceByAppId(appId);
        appInfoMapper.saveAppServiceMore(appModelList);
    }
}
