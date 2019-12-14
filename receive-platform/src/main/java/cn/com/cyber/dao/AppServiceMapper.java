package cn.com.cyber.dao;

import cn.com.cyber.model.AppService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppServiceMapper {

    AppService getByServiceKey(String serviceKey);

    List<AppService> getServiceList(AppService appService);

    int insertService(AppService appService);

    int updateService(AppService appService);

    int getCountServiceKey(@Param("serviceKey") String serviceKey, @Param("state") Integer state);

    AppService getEditByServiceId(long serviceId);

    int updateMoreAppService(@Param("ids") List<Integer> ids, @Param("state") Integer state);

    int deleteMoreAppService(List<Integer> ids);

    AppService getByAppKeyAndServiceKey(@Param("appKey") String appKey, @Param("serviceKey") String serviceKey);

    AppService getValidAppAndService(@Param("appKey") String appKey, @Param("serviceKey") String serviceKey);

    int updateControl(AppService appService);

    //接口申请
    List<AppService> getAppServiceByRecordId(Integer recordId);

    List<AppService> getAppValidListData(AppService appService);

    List<AppService> getReceiveServiceRanking();

}