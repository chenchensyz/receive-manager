package cn.com.cyber.service.impl;

import cn.com.cyber.config.shiro.ShiroDbRealm;
import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.model.AppService;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.excel.ExcelUtil;
import cn.com.cyber.util.excel.ServiceKeyExcel;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AppServiceServiceImpl implements AppServiceService {

    @Autowired
    private AppServiceMapper appServiceMapper;

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
        if (appService.getId() > 0) { //编辑
            appService.setReviser(userId);
            count = appServiceMapper.updateByPrimaryKeySelective(appService);
        } else {
            String uuid;
            long serviceKey;
            do {
                uuid = CodeUtil.getUUID();
                serviceKey = appServiceMapper.getCountServiceKey(uuid,null);
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
    public void uploadServiceFile(MultipartFile file, Long appId) {
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
            service.setAppId(appId);
            service.setMethod(serviceExcel.getMethod().toUpperCase());
            service.setContentType(serviceExcel.getContentType());
            service.setServiceType(1);
            service.setCreator(shiroUser.id);

            String uuid;
            long serviceKey;
            do {
                uuid = CodeUtil.getUUID();
                serviceKey = appServiceMapper.getCountServiceKey(uuid,null);
            } while (serviceKey > 0);
            service.setServiceKey(CodeUtil.getUUID());
            int count = appServiceMapper.insertSelective(service);
            if (count == 0) {
                throw new ValueRuntimeException(CodeUtil.APPSERVICE_ERR_SAVE);
            }
        }
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
