package cn.com.cyber.dao;

import cn.com.cyber.model.ReceiveLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReceiveLogMapper {

    int insertReceiveLog(ReceiveLog receiveLog);

    List<ReceiveLog> getReceiveLogList(ReceiveLog receiveLog);

    int getReceiveLogCount();

    List<Integer> getReceiveLogRanking(@Param("startTime") String startTime,
                                       @Param("endTime") String endTime,
                                       @Param("dateList") List<String> dateList,
                                       @Param("code") Integer code);
}

