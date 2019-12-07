package cn.com.cyber.service;

import cn.com.cyber.model.CodeInfo;

import java.util.List;

/**
 * 码表
 */

public interface CodeInfoService {

    CodeInfo selectCodeInfoById(Integer id);

    List<CodeInfo> getCodeInfoList(CodeInfo codeInfo);
}