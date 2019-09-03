package cn.com.cyber.service.impl;

import cn.com.cyber.dao.AppInfoMapper;
import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.dao.AppServiceRecordMapper;
import cn.com.cyber.model.*;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("appInfoService")
@Transactional
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Autowired
    private AppServiceRecordMapper appServiceRecordMapper;

    @Autowired
    private AppServiceMapper appServiceMapper;

    @Override
    public AppInfo getById(Long id) {
        return appInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<AppInfo> getList(AppInfo appInfo) {
        return appInfoMapper.getList(appInfo);
    }

    @Override
    public AppModel getAppModel(String serviceKey, String appKey) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appKey", appKey);
        map.put("serviceKey", serviceKey);
        return appInfoMapper.selectAppModel(map);
    }

    @Override
    public int getCountAppInfoByState(Long companyId, int state) {
        return appInfoMapper.getCountAppInfoByState(companyId, state);
    }

    @Override
    public List<TreeModel> getAppServiceTree(Long companyId) {
        List<TreeModel> appServiceList = appInfoMapper.getAppServiceTree(companyId);
        List<TreeModel> onlyServiceList = appInfoMapper.getOnlyServiceTree(companyId);
        appServiceList.addAll(onlyServiceList);
        return appServiceList;
    }

    @Override
    public String getCheckedService(Integer appId) {
        return appInfoMapper.getCheckedService(appId);
    }

    @Override
    public List<TreeModel> getAppListTree(Long companyId, int state) {
        return appInfoMapper.getAppListTree(companyId, state);
    }

    @Override
    public List<AppServiceRecord> getAppServiceRecordList(AppServiceRecord appServiceRecode) {
        return appServiceRecordMapper.getAppServiceRecordList(appServiceRecode);
    }

    @Override
    public List<AppService> getAppServiceByRecordId(Integer recordId) {
        return appServiceMapper.getAppServiceByRecordId(recordId);
    }

    @Override
    @Transactional
    public void apply(Integer appId, List<TreeModel> params, String creator) {
        int recordId = appServiceRecordMapper.selectRecordId();
        AppServiceRecord appServiceRecode = new AppServiceRecord();
        appServiceRecode.setId(recordId);
        appServiceRecode.setAppId(appId);
        appServiceRecode.setApply(creator);
        int count = appServiceRecordMapper.insertAppServiceRecord(appServiceRecode);
        if (count == 0) {
            throw new ValueRuntimeException(CodeUtil.SERVICE_RECORD_ERR_SAVE);
        }

        List<AppModel> appModelList = Lists.newArrayList();
        for (TreeModel model : params) {
            if (StringUtils.isBlank(model.getParentId()) || "null".equals(model.getParentId())) {
                continue;
            }
            AppModel appModel = new AppModel();
            if (StringUtils.isBlank(model.getBasicData()) || "null".equals(model.getBasicData())) {
                appModel.setAppKey(model.getParentId());
            } else {
                appModel.setAppKey(model.getBasicData().replaceAll("\\\"", ""));
            }
            appModel.setServiceKey(model.getParentId());
            appModel.setApply(creator);
            appModel.setAppId(appId);
            appModel.setRecordId(recordId);
            appModelList.add(appModel);
        }
        if (appModelList.isEmpty()) {
            return;
        }
        int count2 = appInfoMapper.applyAppServiceMore(appModelList);
        if (count2 == 0) {
            throw new ValueRuntimeException(CodeUtil.SERVICE_APPLY_ERR_SAVE);
        }
    }

    @Override
    @Transactional
    public void check(AppServiceRecord appServiceRecord) {
        if (appServiceRecord.getState() == 2) {  //拒绝
            int count = appServiceRecordMapper.updateAppServiceRecord(appServiceRecord);
            if (count == 0) {
                throw new ValueRuntimeException(CodeUtil.SERVICE_APPROVE_ERR_SAVE);
            }
            return;
        } else if (appServiceRecord.getState() == 1) { //同意
            List<AppModel> applyList = appServiceRecordMapper.getAppServiceApplyList(appServiceRecord.getId());
            appInfoMapper.deleteAppServiceByAppId(appServiceRecord.getAppId());
            int save = appInfoMapper.approveAppServiceMore(applyList);
            if (save != applyList.size()) {
                throw new ValueRuntimeException(CodeUtil.SERVICE_RECORD_ERR_SAVE);
            }
            int count = appServiceRecordMapper.updateAppServiceRecord(appServiceRecord);
            if (count == 0) {
                throw new ValueRuntimeException(CodeUtil.SERVICE_APPROVE_ERR_SAVE);
            }
        }
    }

    @Override
    public List<AppService> getAppValidListData(AppService appService) {
        return appServiceMapper.getAppValidListData(appService);
    }

    @Override
    public void saveAppService(Integer appId, List<TreeModel> params, String creator) {
        List<AppModel> appModelList = Lists.newArrayList();
        for (TreeModel model : params) {
            if (StringUtils.isBlank(model.getParentId()) || "null".equals(model.getParentId())) {
                continue;
            }
            AppModel appModel = new AppModel();
            if (StringUtils.isBlank(model.getBasicData()) || "null".equals(model.getBasicData())) {
                appModel.setAppKey(model.getParentId());
            } else {
                appModel.setAppKey(model.getBasicData().replaceAll("\\\"", ""));
            }
            appModel.setServiceKey(model.getParentId());
            appModel.setApply(creator);
            appModel.setAppId(appId);
            appModel.setRecordId(0);
            appModelList.add(appModel);
        }
        appInfoMapper.deleteAppServiceByAppId(appId);
        int save = appInfoMapper.approveAppServiceMore(appModelList);
        if (save != appModelList.size()) {
            throw new ValueRuntimeException(CodeUtil.SERVICE_RECORD_ERR_SAVE);
        }
    }
}
