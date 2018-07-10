package cn.com.cyber.impl;

import cn.com.cyber.AppDetailsService;
import cn.com.cyber.dao.AppDetailsMapper;
import cn.com.cyber.model.AppDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppDetailsServiceImpl implements AppDetailsService {
    
    @Autowired
    private AppDetailsMapper appDetailsMapper;
    
    @Override
    public AppDetails getById(Long id) {
        return appDetailsMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<AppDetails> getList(AppDetails appDetails) {
        return appDetailsMapper.getList(appDetails);
    }

    @Override
    public int insert(AppDetails appDetails) {
        return appDetailsMapper.insertSelective(appDetails);
    }

    @Override
    public int update(AppDetails appDetails) {
        return appDetailsMapper.updateByPrimaryKeySelective(appDetails);
    }
}
