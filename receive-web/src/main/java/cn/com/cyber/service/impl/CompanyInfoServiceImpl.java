package cn.com.cyber.service.impl;

import cn.com.cyber.dao.CompanyInfoMapper;
import cn.com.cyber.model.CompanyInfo;
import cn.com.cyber.service.CompanyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("companyInfoService")
public class CompanyInfoServiceImpl implements CompanyInfoService {

    @Autowired
    private CompanyInfoMapper companyInfoMapper;

    @Override
    public CompanyInfo getById(Long id) {
        return companyInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CompanyInfo> getList(CompanyInfo companyInfo) {
        return companyInfoMapper.getList(companyInfo);
    }

    @Override
    public int insert(CompanyInfo companyInfo) {
        return companyInfoMapper.insertSelective(companyInfo);
    }

    @Override
    public int update(CompanyInfo companyInfo) {
        return companyInfoMapper.updateByPrimaryKeySelective(companyInfo);
    }

    @Override
    public int getCountByOwner(String owner, long id) {
        return companyInfoMapper.getCountByOwner(owner, id);
    }
}
