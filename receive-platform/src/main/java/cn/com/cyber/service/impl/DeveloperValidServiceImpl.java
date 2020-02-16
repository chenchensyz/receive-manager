package cn.com.cyber.service.impl;

import cn.com.cyber.dao.DeveloperValidMapper;
import cn.com.cyber.model.DeveloperValid;
import cn.com.cyber.service.DeveloperValidService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.HttpConnection;
import cn.com.cyber.util.common.ResultData;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("developerValidService")
public class DeveloperValidServiceImpl implements DeveloperValidService {

    @Autowired
    private Environment environment;

    @Autowired
    private DeveloperValidMapper developerValidMapper;

    @Override
    public List<DeveloperValid> getDeveloperValidList(DeveloperValid developerValid) {
        return developerValidMapper.getDeveloperValidList(developerValid);
    }

    @Override
    @Transactional
    public DeveloperValid validLogin(DeveloperValid developerValid) {
        if (developerValid.getId() == null
                && (StringUtils.isBlank(developerValid.getUserId()) || StringUtils.isBlank(developerValid.getCompanyKey()))) {
            throw new ValueRuntimeException(CodeUtil.REQUEST_PARAM_NULL); //缺少查询条件
        }
        List<DeveloperValid> valids = developerValidMapper.getDeveloperValidList(developerValid);
        DeveloperValid localValid;
        if (!CollectionUtils.isEmpty(valids)) { //记录已存在
            localValid = valids.get(0);
        } else {
            localValid = developerValid;
        }
        String url = environment.getProperty(CodeUtil.DEVELOPER_VALID_URL) + CodeUtil.API_VALID_URL;
        ResultData resultData = HttpConnection.httpRequest(url, CodeUtil.METHOD_POST, CodeUtil.CONTEXT_JSON, JSON.toJSONString(localValid), null, null);
        if (resultData != null && CodeUtil.HTTP_OK == resultData.getCode()) {
            JSONObject jsonObject = JSONObject.parseObject(resultData.getResult());
            if (jsonObject.getInteger("code") != 0) {
                throw new ValueRuntimeException(CodeUtil.REQUEST_TOKEN_USER_FILED);
            }
            JSONObject data = JSONObject.parseObject(jsonObject.getString("data"));
            localValid.setToken(data.getString("token"));
            localValid.setCompanyId(data.getInteger("companyId"));
            int count;
            if (localValid.getId() == null) {
                count = developerValidMapper.insertDeveloperValid(localValid);
            } else {
                count = developerValidMapper.updateDeveloperValid(localValid);
            }
            if (count == 0) {
                throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_DEVELOPER_BIND);
            }
        }
        return localValid;
    }

    @Override
    public void validDelete(Integer id) {
        int count = developerValidMapper.deleteDeveloperValid(id);
        if (count == 0) {
            throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_DEVELOPER_DEL);  //开发者绑定记录删除失败
        }
    }
}
