package cn.com.cyber.dao;

import cn.com.cyber.common.BaseDao;
import cn.com.cyber.model.AppService;

import java.util.List;
import java.util.Map;

public interface AppServiceMapper extends BaseDao<AppService> {

    int getCountServiceKey(String serviceKey);

    AppService  getEditByServiceId(long serviceId);
}