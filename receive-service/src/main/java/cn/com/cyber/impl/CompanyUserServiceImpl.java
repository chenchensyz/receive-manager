package cn.com.cyber.impl;

import cn.com.cyber.CompanyUserService;
import cn.com.cyber.dao.CompanyUserMapper;
import cn.com.cyber.model.CompanyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("companyUserService")
@Transactional
public class CompanyUserServiceImpl implements CompanyUserService {

    @Autowired
    private CompanyUserMapper companyUserMapper;

    @Override
    public int deleteByCompanyId(long companyId) {
        return companyUserMapper.deleteByCompanyId(companyId);
    }

    @Override
    public List<CompanyUser> getUserCompanyInfo(String userId) {
        return companyUserMapper.getUserCompanyInfo(userId);
    }
}
