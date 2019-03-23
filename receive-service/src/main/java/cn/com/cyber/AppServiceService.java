package cn.com.cyber;

import cn.com.cyber.model.AppService;

import java.util.List;
import java.util.Map;

public interface AppServiceService {

     AppService getById(Long id);

     List<AppService> getList(AppService appService);

     int insert(AppService appService);

     int update(AppService appService);

     int getCountServiceKey(String serviceKey);

     AppService  getEditByServiceId(long serviceId);
}
