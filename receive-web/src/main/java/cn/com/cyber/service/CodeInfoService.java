package cn.com.cyber.service;

import cn.com.cyber.model.CodeInfo;

import java.util.List;
import java.util.Map;

/**  
 * 创建时间：2015-5-30 
 * @author xumin  
 */

public interface CodeInfoService {
	CodeInfo getById(Long id);
	
	List<CodeInfo> getList(CodeInfo record);
	
	int save(CodeInfo record);
	
	int updateById(CodeInfo record);
	
	List<String> getAllTypes(Map<String, Object> map);

	Map<String, CodeInfo> getCodeInfoMap();

	Map<String,List<CodeInfo>> getCodeListMap();
}