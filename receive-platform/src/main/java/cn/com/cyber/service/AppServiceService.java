package cn.com.cyber.service;

import cn.com.cyber.model.AppService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AppServiceService {

    AppService getById(Long id);

    List<AppService> getList(AppService appService);

    int insert(AppService appService);

    int getCountServiceKey(String serviceKey);

    AppService getEditByServiceId(long serviceId);

    void addOrEditAppService(Long userId, AppService appService);

    //读取文件-批量增加接口
    void uploadServiceFile(MultipartFile file, Long appId);

    void changeAppService(String appServiceIds, int state, Long updateUserId);

    AppService getByAppKeyAndServiceKey(String appKey, String serviceKey);

}
