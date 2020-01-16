package cn.com.cyber.service.impl;

import cn.com.cyber.dao.AppInfoMapper;
import cn.com.cyber.dao.AppServiceMapper;
import cn.com.cyber.dao.AppServiceRecordMapper;
import cn.com.cyber.model.*;
import cn.com.cyber.service.AppInfoService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

    @Autowired
    private Environment environment;


    @Override
    public List<AppInfo> getAppInfoList(AppInfo appInfo) {
        return appInfoMapper.getAppInfoList(appInfo);
    }

    @Override
    public void saveAppInfo(Long userId, AppInfo appInfo) {
        int count = 0;
        if (appInfo.getId() != null) { //编辑
            count = appInfoMapper.updateAppInfo(appInfo);
        } else {
            String uuid;
            long serviceKey;
            do {
                uuid = CodeUtil.getUUID();
                serviceKey = appInfoMapper.getCountAppKey(uuid);
            } while (serviceKey > 0);
            appInfo.setAppKey(CodeUtil.getUUID());
            appInfo.setCreator(userId);
            appInfo.setState(0);
            count = appInfoMapper.insertAppInfo(appInfo);
        }
        if (count == 0) {
            throw new ValueRuntimeException(CodeUtil.APPINFO_ERR_OPERATION);
        }
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
    public List<String> getCheckedService(Integer appId) {
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
    public void apply(String param, String creator) {
        JSONObject jsonObject = JSONObject.parseObject(param);
        Integer appId = jsonObject.getInteger("appId");
        String appKey = jsonObject.getString("appKey");
        Integer pushArea = jsonObject.getInteger("pushArea");
        List<TreeModel> params = JSONArray.parseArray(jsonObject.getString("params"), TreeModel.class);
        AppServiceRecord appServiceRecode = new AppServiceRecord();
        if (CodeUtil.MAPPER_DB_ORACLE.equals(environment.getProperty(CodeUtil.MAPPER_DB))) { //oracle数据库
            int recordId = appServiceRecordMapper.selectRecordId();
            appServiceRecode.setId(recordId);
        }
        appServiceRecode.setAppId(appId);
        appServiceRecode.setApply(creator);
        int count = appServiceRecordMapper.insertAppServiceRecord(appServiceRecode);
        if (count == 0) {

        }

        List<AppModel> appModelList = Lists.newArrayList();
        for (TreeModel model : params) {
            if (StringUtils.isBlank(model.getParentId()) || "null".equals(model.getParentId())) {
                continue;
            }
            AppModel appModel = new AppModel();
            appModel.setAppKey(appKey);
            appModel.setServiceKey(model.getParentId());
            appModel.setApply(creator);
            appModel.setAppId(appId);
            appModel.setRecordId(appServiceRecode.getId());
            appModel.setPushArea(pushArea);
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
    public void saveAppService(String param, String creator) {
        JSONObject jsonObject = JSONObject.parseObject(param);
        Integer appId = jsonObject.getInteger("appId");
        String appKey = jsonObject.getString("appKey");
        List<TreeModel> params = JSONArray.parseArray(jsonObject.getString("params"), TreeModel.class);
        List<AppModel> appModelList = Lists.newArrayList();
        if (params.size() == 0) {
            appInfoMapper.deleteAppServiceByAppId(appId);
        } else {
            for (TreeModel model : params) {
                if (StringUtils.isBlank(model.getParentId())) {
                    continue;
                }
                AppModel appModel = new AppModel();
                appModel.setAppKey(appKey);
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
}
