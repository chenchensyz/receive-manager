package cn.com.cyber.impl;

import cn.com.cyber.AppServiceService;
import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.model.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AppServiceServiceImpl implements AppServiceService {

    @Autowired
    private AppServiceMapper appServiceMapper;

    @Override
    public AppService getById(Long id) {
        return appServiceMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<AppService> getList(AppService appService) {
        return appServiceMapper.getList(appService);
    }

    @Override
    public int insert(AppService appService) {
        return appServiceMapper.insertSelective(appService);
    }

    @Override
    public int update(AppService appService) {
        return appServiceMapper.updateByPrimaryKeySelective(appService);
    }

    @Override
    public int getCountServiceKey(String serviceKey) {
        return appServiceMapper.getCountServiceKey(serviceKey);
    }

    @Override
    public AppService getEditByServiceId(long serviceId) {
        return appServiceMapper.getEditByServiceId(serviceId);
    }
}
