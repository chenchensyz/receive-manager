package cn.com.cyber.service.impl;

import cn.com.cyber.dao.CodeInfoMapper;
import cn.com.cyber.model.CodeInfo;
import cn.com.cyber.service.CodeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("codeInfoService")
@Transactional
public class CodeInfoServiceImpl implements CodeInfoService {

	@Autowired
	private CodeInfoMapper codeInfoMapper;

	@Override
	public CodeInfo getById(Long id) {
		return codeInfoMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<CodeInfo> getList(CodeInfo record) {
		return codeInfoMapper.getList(record);
	}

	@Override
	public int save(CodeInfo record) {
		return codeInfoMapper.insertSelective(record);
	}

	@Override
	public int updateById(CodeInfo record) {
		return codeInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<String> getAllTypes(Map<String,Object> map) {
		return codeInfoMapper.getCodeTypeList(map);
	}

	@Override
	public Map<String, CodeInfo> getCodeInfoMap() {
		List<CodeInfo> list = getList(new CodeInfo());
		Map<String, CodeInfo> map = new HashMap<String, CodeInfo>();
		for (CodeInfo codeInfo : list) {
			map.put(codeInfo.getCode() + "-" + codeInfo.getType(), codeInfo);
		}
		return map;
	}

	@Override
	public Map<String, List<CodeInfo>> getCodeListMap() {
		//TODO :根据codeType 查询 codeList 放到CodeInfoUtils.setCodoListMap();
		List<String> codeTypeList = getAllTypes(new HashMap<String,Object>());
		Map<String,List<CodeInfo>> maps = new HashMap<String,List<CodeInfo>>();
		for (String string : codeTypeList) {
			CodeInfo param = new CodeInfo();
			param.setType(string);
			List<CodeInfo> typeList = getList(param);
			maps.put(string, typeList);
		}
		return maps;
	}

}