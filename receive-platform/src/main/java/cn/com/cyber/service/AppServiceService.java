package cn.com.cyber.service;

import cn.com.cyber.model.AppService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AppServiceService {

    AppService getByServiceKey(String serviceKey);

    List<AppService> getList(AppService appService);

    int insert(AppService appService);

    int getCountServiceKey(String serviceKey);

    AppService getEditByServiceId(long serviceId);

    void addOrEditAppService(Long userId, AppService appService);

    //读取文件-批量增加接口
    void uploadMoreService(MultipartFile file, Long appId);

    //上传文件
    String uploadFile(HttpServletRequest request, String pathSuffix, Long serviceId);

    void changeAppService(String appServiceIds, int state, Long updateUserId);

    AppService getByAppKeyAndServiceKey(String appKey, String serviceKey);

    AppService getValidAppAndService(String appKey, String serviceKey);
}
