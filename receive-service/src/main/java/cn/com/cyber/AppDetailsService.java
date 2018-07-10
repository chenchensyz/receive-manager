package cn.com.cyber;

import cn.com.cyber.model.AppDetails;

import java.util.List;

public interface AppDetailsService {

     AppDetails getById(Long id);

     List<AppDetails> getList(AppDetails appInfo);

     int insert(AppDetails appInfo);

     int update(AppDetails appInfo);
}
