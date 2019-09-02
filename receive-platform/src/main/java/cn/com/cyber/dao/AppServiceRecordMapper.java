package cn.com.cyber.dao;

import cn.com.cyber.model.AppModel;
import cn.com.cyber.model.AppServiceRecord;

import java.util.List;

public interface AppServiceRecordMapper {

    int selectRecordId();

    int insertAppServiceRecord(AppServiceRecord appServiceRecord);

    int updateAppServiceRecord(AppServiceRecord appServiceRecord);

    List<AppServiceRecord> getAppServiceRecordList(AppServiceRecord appServiceRecord);

    List<AppModel> getAppServiceApplyList(Integer recordId);

}