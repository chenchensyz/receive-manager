package cn.com.cyber.service.impl;

import cn.com.cyber.config.shiro.ShiroDbRealm;
import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.model.AppService;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.excel.ExcelUtil;
import cn.com.cyber.util.excel.FileOperateUtil;
import cn.com.cyber.util.excel.ServiceKeyExcel;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class AppServiceServiceImpl implements AppServiceService {

    @Autowired
    private AppServiceMapper appServiceMapper;

    @Autowired
    private Environment environment;

    @Override
    public AppService getById(Long id) {
        return appServiceMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<AppService> getList(AppService appService) {
        return appServiceMapper.getList(appService);
    }

    @Override
    public int insert(AppService appService) {
        return appServiceMapper.insertSelective(appService);
    }

    @Override
    public int getCountServiceKey(String serviceKey) {
        return appServiceMapper.getCountServiceKey(serviceKey, null);
    }

    @Override
    public AppService getEditByServiceId(long serviceId) {
        return appServiceMapper.getEditByServiceId(serviceId);
    }

    @Override
    @Transactional
    public void addOrEditAppService(Long userId, AppService appService) {
        int count;
        if (appService.getAppId() == null) {  //独立接口
            appService.setServiceType(1);
        }
        if (appService.getId() != null) { //编辑
            appService.setReviser(userId);
            count = appServiceMapper.updateByPrimaryKeySelective(appService);
        } else {
            String uuid;
            long serviceKey;
            do {
                uuid = CodeUtil.getUUID();
                serviceKey = appServiceMapper.getCountServiceKey(uuid, null);
            } while (serviceKey > 0);
            appService.setServiceKey(CodeUtil.getUUID());
            appService.setCreator(userId);
            count = appServiceMapper.insertSelective(appService);
        }
        if (count == 0) {
            throw new ValueRuntimeException(CodeUtil.APPSERVICE_ERR_SAVE);
        }
    }

    @Override
    @Transactional
    public void uploadMoreService(MultipartFile file, Long appId) {
        List<Object> datas = ExcelUtil.readExcel(file, new ServiceKeyExcel());
        if (datas == null || datas.size() == 0) {
            throw new ValueRuntimeException(CodeUtil.APPINFO_NULL_SERVICEFILE);
        }
        ShiroDbRealm.ShiroUser shiroUser = (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
        for (Object o : datas) {
            ServiceKeyExcel serviceExcel = (ServiceKeyExcel) o;
            if (StringUtils.isBlank(serviceExcel.getUrlSuffix()) ||
                    !serviceExcel.getUrlSuffix().startsWith("http")) {
                throw new ValueRuntimeException(CodeUtil.APPINFO_ERR_SERVICEKEY);
            }
            AppService service = new AppService();
            service.setId(0l);
            service.setServiceName(serviceExcel.getServiceName());
            service.setUrlSuffix(serviceExcel.getUrlSuffix());
            if (appId == null) {
                service.setServiceType(1); //接口类型 0：应用接口  1：独立接口
            } else {
                service.setServiceType(0);
            }
            service.setAppId(appId);
            service.setMethod(serviceExcel.getMethod().toUpperCase());
            service.setContentType(serviceExcel.getContentType());
            service.setCreator(shiroUser.id);

            String uuid;
            long serviceKey;
            do {
                uuid = CodeUtil.getUUID();
                serviceKey = appServiceMapper.getCountServiceKey(uuid, null);
            } while (serviceKey > 0);
            service.setServiceKey(CodeUtil.getUUID());
            int count = appServiceMapper.insertSelective(service);
            if (count == 0) {
                throw new ValueRuntimeException(CodeUtil.APPSERVICE_ERR_SAVE);
            }
        }
    }

    @Override
    public String uploadFile(HttpServletRequest request, String pathSuffix) {
        String filePath = environment.getProperty(CodeUtil.FILE_SAVE_PATH) + CodeUtil.SERVICE_FILE_PATH + File.separator;
        if (StringUtils.isNotBlank(pathSuffix)) {
            File sourceFile = new File(filePath + pathSuffix);  // 源文件
            FileOperateUtil.delFile(sourceFile.getParentFile()); //删除源文件目录
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap.size() != 1) {  //只能上传单个文件
            throw new ValueRuntimeException(CodeUtil.BASE_FILE_ONLY_UP);
        }

//        String filePath = "D:\\file\\" + MEETING_PATH + File.separator;
        long currentTime = System.currentTimeMillis();
        String meeting = FileOperateUtil.uploadFile(fileMap, filePath + currentTime); //保存文件
        String resultPath = File.separator + currentTime + File.separator + meeting; //返回终端格式
        return resultPath;
    }

    @Override
    public void changeAppService(String appServiceIds, int state, Long updateUserId) {
        String[] serviceArray = appServiceIds.split(",");
        List<Integer> ids = Lists.newArrayList();
        for (String serviceId : serviceArray) {
            if (StringUtils.isNotBlank(serviceId)) {
                ids.add(Integer.valueOf(serviceId));
            }
        }
        int count;
        if (-1 == state) {
            count = appServiceMapper.deleteMoreAppService(ids);
        } else {
            count = appServiceMapper.updateMoreAppService(ids, state);
        }
        if (count != ids.size()) {
            throw new ValueRuntimeException(CodeUtil.APPSERVICE_ERR_OPTION);
        }
    }

    @Override
    public AppService getByAppKeyAndServiceKey(String appKey, String serviceKey) {
        return appServiceMapper.getByAppKeyAndServiceKey(appKey, serviceKey);
    }

    @Override
    public AppService getValidAppAndService(String appKey, String serviceKey) {
        return appServiceMapper.getValidAppAndService(appKey, serviceKey);
    }
}
