package cn.com.cyber.service;

import cn.com.cyber.model.Developer;
import cn.com.cyber.model.User;

import java.util.List;

public interface DeveloperService {

    List<Developer> getDeveloperList(Developer developer);

    Developer getDeveloperByUserName(String userName);

}
