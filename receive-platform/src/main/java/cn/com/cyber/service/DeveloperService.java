package cn.com.cyber.service;

import cn.com.cyber.model.Developer;
import cn.com.cyber.model.DeveloperValid;
import cn.com.cyber.util.common.RestResponse;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DeveloperService {

    List<Developer> getDeveloperList(Developer developer);

    Developer getDeveloperByUserName(String userName);

    RestResponse validDeveloper(RestResponse rest, DeveloperValid developerValid);

    void addDeveloper(String cofirmPwd, Developer developer);

    void changePwd(String userId, String old, String password, String confirm);


}
