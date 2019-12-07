package cn.com.cyber.dao;

import cn.com.cyber.model.CodeInfo;

import java.util.List;

public interface CodeInfoMapper {

    CodeInfo selectCodeInfoById(Integer id);

    List<CodeInfo> getCodeInfoList(CodeInfo codeInfo);
}