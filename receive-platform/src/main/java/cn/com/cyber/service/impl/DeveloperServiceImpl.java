package cn.com.cyber.service.impl;

import cn.com.cyber.dao.DeveloperMapper;
import cn.com.cyber.dao.UserMapper;
import cn.com.cyber.model.Developer;
import cn.com.cyber.model.User;
import cn.com.cyber.service.DeveloperService;
import cn.com.cyber.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("developerService")
public class DeveloperServiceImpl implements DeveloperService {

    @Autowired
    private DeveloperMapper developerMapper;

    @Override
    public List<Developer> getDeveloperList(Developer developer) {
        return developerMapper.getDeveloperList(developer);
    }

    @Override
    public Developer getDeveloperByUserName(String userName) {
        return developerMapper.getDeveloperByUserName(userName);
    }
}
