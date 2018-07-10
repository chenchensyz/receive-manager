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
    public AppInfo getByAppAndService(String appKey, String serviceKey) {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("appKey",appKey);
        map.put("serviceKey",serviceKey);
        return appInfoMapper.selectByAppAndService(map);
    }

    @Override
    public AppModel selectAppModel(String urlSuffix, long appId) {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("urlSuffix",urlSuffix);
        map.put("appId",appId);
        return appInfoMapper.selectAppModel(map);
    }
}
