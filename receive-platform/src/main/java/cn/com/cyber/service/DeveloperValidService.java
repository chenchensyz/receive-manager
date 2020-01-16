package cn.com.cyber.service;

import cn.com.cyber.model.DeveloperValid;

import java.util.List;

public interface DeveloperValidService {

    List<DeveloperValid> getDeveloperValidList(DeveloperValid developerValid);

    DeveloperValid validLogin(DeveloperValid developerValid);

}
