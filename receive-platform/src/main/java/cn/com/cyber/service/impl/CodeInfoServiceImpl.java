package cn.com.cyber.service.impl;

import cn.com.cyber.dao.CodeInfoMapper;
import cn.com.cyber.model.CodeInfo;
import cn.com.cyber.service.CodeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("codeInfoService")
public class CodeInfoServiceImpl implements CodeInfoService {

    @Autowired
    private CodeInfoMapper codeInfoMapper;


    @Override
    public CodeInfo selectCodeInfoById(Integer id) {
        return codeInfoMapper.selectCodeInfoById(id);
    }

    @Override
    public List<CodeInfo> getCodeInfoList(CodeInfo codeInfo) {
        return codeInfoMapper.getCodeInfoList(codeInfo);
    }
}