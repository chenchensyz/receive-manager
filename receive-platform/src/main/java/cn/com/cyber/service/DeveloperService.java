package cn.com.cyber.service;

import cn.com.cyber.model.Developer;
import cn.com.cyber.model.DeveloperValid;
import cn.com.cyber.util.common.RestResponse;

import java.util.List;

public interface DeveloperService {

    List<Developer> getDeveloperList(Developer developer);

    Developer getDeveloperByUserName(String userName);

    RestResponse validDeveloper(RestResponse rest, DeveloperValid developerValid);

}
