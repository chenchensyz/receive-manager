package cn.com.cyber.service.impl;

import cn.com.cyber.config.shiro.ShiroDbRealm;
import cn.com.cyber.dao.AppInfoMapper;
import cn.com.cyber.dao.ArrangeInfoMapper;
import cn.com.cyber.model.ArrangeInfo;
import cn.com.cyber.model.ArrangeModel;
import cn.com.cyber.model.ArrangeRelevance;
import cn.com.cyber.service.ArrangeInfoService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("arrangeInfoService")
@Transactional
public class ArrangeInfoServiceImpl implements ArrangeInfoService {

    @Autowired
    private ArrangeInfoMapper arrangeInfoMapper;

    @Autowired
    private AppInfoMapper appInfoMapper;


    @Override
    public List<ArrangeInfo> getArrangeInfoList(ArrangeInfo arrangeInfo) {
        return arrangeInfoMapper.getArrangeInfoList(arrangeInfo);
    }

    @Override
    public ArrangeInfo getArrangeInfoById(Long id) {
        ArrangeInfo arrangeInfo = arrangeInfoMapper.getArrangeInfoById(id);
        if (arrangeInfo == null) {
            throw new ValueRuntimeException(CodeUtil.ARRANGEINFO_ERR_SELECT); //未查到编排接口
        }
        return arrangeInfo;
    }

    @Override
    @Transactional
    public void addOrEditArrangeInfo(String param) {
        JSONObject jsonObject = JSONObject.parseObject(param);
        Long id = jsonObject.getLong("id");
        String interfaceName = jsonObject.getString("interfaceName");
        List<ArrangeModel> arrangeModels = JSONArray.parseArray(jsonObject.getString("arrangeModel"), ArrangeModel.class);
        ArrangeInfo arrangeInfo = new ArrangeInfo();
        int count = 0;
        ShiroDbRealm.ShiroUser user = (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
        if (id == null) {  //新增
            arrangeInfo.setInterfaceName(interfaceName);
            arrangeInfo.setCompanyId(user.companyId);
            arrangeInfo.setCreateUser(user.id);
            count = arrangeInfoMapper.insertArrangeInfo(arrangeInfo);
        } else {
            arrangeInfo.setId(id);
            arrangeInfo.setUpdateUser(user.id);
            count = arrangeInfoMapper.updateArrangeInfo(arrangeInfo);
        }
        if (count == 0) {
            throw new ValueRuntimeException(CodeUtil.ARRANGEINFO_ERR_ADD); //编排接口保存失败
        }

        List<ArrangeRelevance> relevances = Lists.newArrayList();
        for (ArrangeModel model : arrangeModels) {
            for (ArrangeModel child : model.getChildren()) {
                ArrangeRelevance relevance = new ArrangeRelevance();
                relevance.setArrangeId(arrangeInfo.getId());
                relevance.setAppId(model.getId());
                relevance.setServiceId(child.getId());
                relevances.add(relevance);
            }
        }

        if(relevances.size()>3){
            throw new ValueRuntimeException(CodeUtil.ARRANGEINFO_MORE_ADD); //选择接口不能超过3个
        }

        arrangeInfoMapper.deleteRelevanceByArrangeId(arrangeInfo.getId());  //删除原有记录
        arrangeInfo.setRelevances(relevances);
        int count2 = arrangeInfoMapper.insertRelevanceMore(arrangeInfo);  //保存新记录
        if (count2 != relevances.size()) {
            throw new ValueRuntimeException(CodeUtil.ARRANGERELEVANCE_ERR_ADD); //编排接口绑定失败
        }
    }


    @Override
    public List<Map<String, Object>> getAppServiceList() {
        ShiroDbRealm.ShiroUser user = (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
        List<Map<String, Object>> appServiceList = appInfoMapper.getAppServiceList(user.companyId);
        return appServiceList;
    }

    @Override
    public List<Long> getServiceCheck(long arrangeId) {
        return arrangeInfoMapper.getServiceCheck(arrangeId);
    }

    @Override
    public void deleteArrangeInfo(Long arrangeInfoId) {
        arrangeInfoMapper.deleteRelevanceByArrangeId(arrangeInfoId); //删除接口绑定
        int count = arrangeInfoMapper.deleteArrangeInfo(arrangeInfoId);
        if(count==0){
            throw new ValueRuntimeException(CodeUtil.ARRANGEINFO_ERR_DEL); //编排接口删除失败
        }
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}
