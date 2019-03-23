package cn.com.cyber.impl;

import cn.com.cyber.AppInfoService;
import cn.com.cyber.dao.AppInfoMapper;
import cn.com.cyber.model.AppInfo;
import cn.com.cyber.model.AppModel;
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
    public int getCountAppKey(String appKey) {
        return appInfoMapper.getCountAppKey(appKey);
    }

    @Override
    public AppInfo getEditById(Long id) {
        return appInfoMapper.getEditById(id);
    }

    @Override
    public int deleteByAppId(long appId) {
        return appInfoMapper.deleteByPrimaryKey(appId);
    }

    @Override
    public int getCountAppInfoByState(long companyId, int state) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("companyId", companyId);
        map.put("state", state);
        return appInfoMapper.getCountAppInfoByState(map);
    }
}
