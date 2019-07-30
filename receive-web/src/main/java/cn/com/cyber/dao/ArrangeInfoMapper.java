package cn.com.cyber.dao;

import cn.com.cyber.model.ArrangeInfo;
import cn.com.cyber.model.ArrangeRelevance;

import java.util.List;

public interface ArrangeInfoMapper {

    List<ArrangeInfo> getArrangeInfoList(ArrangeInfo arrangeInfo);

    ArrangeInfo getArrangeInfoById(Long id);

    int insertArrangeInfo(ArrangeInfo arrangeInfo);

    int updateArrangeInfo(ArrangeInfo arrangeInfo);

    int deleteArrangeInfo(long id);

    int insertRelevanceMore(ArrangeInfo arrangeInfo);

    int deleteRelevanceByArrangeId(long arrangeId);

    //查询选中的接口
    List<Long> getServiceCheck(long arrangeId);

}