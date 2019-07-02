package cn.com.cyber.dao;

import cn.com.cyber.model.CompanyUser;

import java.util.List;

public interface CompanyUserMapper extends BaseDao<CompanyUser> {

    int deleteByCompanyId(long companyId);

    List<CompanyUser> getUserCompanyInfo(String userId);
}