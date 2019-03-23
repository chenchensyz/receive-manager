package cn.com.cyber.dao;

import cn.com.cyber.common.BaseDao;
import cn.com.cyber.model.CompanyInfo;
import org.apache.ibatis.annotations.Param;

public interface CompanyInfoMapper extends BaseDao<CompanyInfo> {

    int getCountByOwner(@Param("owner") String owner,@Param("id") long id);
}