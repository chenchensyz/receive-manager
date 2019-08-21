package cn.com.cyber.dao;

import cn.com.cyber.model.AppService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppServiceMapper extends BaseDao<AppService> {

    int getCountServiceKey(String serviceKey);

    AppService getEditByServiceId(long serviceId);

    int updateMoreAppService(@Param("ids") List<Integer> ids, @Param("state") Integer state);

    int deleteMoreAppService(List<Integer> ids);

    AppService getByAppKeyAndServiceKey(@Param("appKey") String appKey, @Param("serviceKey") String serviceKey);

}