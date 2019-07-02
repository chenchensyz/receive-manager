package cn.com.cyber.dao;

import cn.com.cyber.model.CodeInfo;

import java.util.List;
import java.util.Map;

public interface CodeInfoMapper extends BaseDao<CodeInfo> {

    List<String> getCodeTypeList(Map<String, Object> map);
}