package cn.com.cyber.dao;

import cn.com.cyber.model.DeveloperValid;

import java.util.List;

public interface DeveloperValidMapper {

    List<DeveloperValid> getDeveloperValidList(DeveloperValid developerValid);

    int insertDeveloperValid(DeveloperValid developerValid);

    int updateDeveloperValid(DeveloperValid developerValid);

    int deleteDeveloperValid(Integer id);

}
