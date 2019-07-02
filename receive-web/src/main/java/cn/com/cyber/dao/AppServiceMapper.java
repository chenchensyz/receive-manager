package cn.com.cyber.dao;

import cn.com.cyber.model.AppService;
import cn.com.cyber.util.excel.ServiceKeyExcel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppServiceMapper extends BaseDao<AppService> {

    int getCountServiceKey(String serviceKey);

    AppService getEditByServiceId(long serviceId);

    int updateMoreAppService(@Param("ids") List<Integer> ids, @Param("state") Integer state);

    int deleteMoreAppService(List<Integer> ids);

}