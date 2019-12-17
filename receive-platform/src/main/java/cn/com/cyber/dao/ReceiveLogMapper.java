package cn.com.cyber.dao;

import cn.com.cyber.model.ReceiveLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReceiveLogMapper {

    int insertReceiveLog(ReceiveLog receiveLog);

    List<ReceiveLog> getReceiveLogList(ReceiveLog receiveLog);

    int getReceiveLogCount(@Param("creator") Long creator);

    List<Integer> getReceiveLogRanking(@Param("creator") Long creator, @Param("startTime") String startTime,
                                       @Param("endTime") String endTime,
                                       @Param("dateList") List<String> dateList,
                                       @Param("code") Integer code);
}

