package cn.com.cyber.service;

import cn.com.cyber.model.CompanyInfo;

import java.util.List;

public interface CompanyInfoService {

    CompanyInfo getById(Long id);

    List<CompanyInfo> getList(CompanyInfo companyInfo);

    int insert(CompanyInfo companyInfo);

    int update(CompanyInfo companyInfo);

    int getCountByOwner(String owner, long id);
}
