package cn.com.cyber.dao;

import cn.com.cyber.model.Developer;
import cn.com.cyber.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeveloperMapper{

    List<Developer> getDeveloperList(Developer developer);

    Developer getDeveloperByUserName(String userName);
}