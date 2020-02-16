package cn.com.cyber.service.impl;

import cn.com.cyber.dao.DeveloperMapper;
import cn.com.cyber.model.Developer;
import cn.com.cyber.model.DeveloperValid;
import cn.com.cyber.service.DeveloperService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.EncryptUtils;
import cn.com.cyber.util.common.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

@Service("developerService")
public class DeveloperServiceImpl implements DeveloperService {

    @Autowired
    private DeveloperMapper developerMapper;

    @Autowired
    private JedisPool jedisPool;

    @Override
    public List<Developer> getDeveloperList(Developer developer) {
        return developerMapper.getDeveloperList(developer);
    }

    @Override
    public Developer getDeveloperByUserName(String userName) {
        return developerMapper.getDeveloperByUserName(userName);
    }

    @Override
    public RestResponse validDeveloper(RestResponse rest, DeveloperValid developerValid) {
        Developer developer = developerMapper.getDeveloperByCompanyKey(developerValid.getUserName(), developerValid.getCompanyKey());
        if (developer == null) {
            throw new ValueRuntimeException(CodeUtil.REQUEST_TOKEN_USER_FILED);
        }
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        try {
            Map<String, String> map = Maps.newHashMap();
            String token = EncryptUtils.MD5Encode(developerValid.getUserId() + "!**");
            map.put("userId", developerValid.getUserId());
            map.put("token", token);
            String key = CodeUtil.REDIS_PREFIX + token;
            jedis.hmset(key, map);
            map.put("companyId", developer.getId() + "");
            rest.setData(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_LOGIN); //用户登陆失败
        } finally {
            jedis.close();
        }
        return rest;
    }

    @Override
    public void addDeveloper(String cofirmPwd, Developer developer) {
        Developer localDeveloper = developerMapper.getDeveloperByUserName(developer.getUserName());
        if (localDeveloper != null) { //用户已存在
            throw new ValueRuntimeException(CodeUtil.USERINFO_EXIST);
        }
        developer.setPassword(cofirmPwd);
        int count = developerMapper.insertDeveloper(developer);
        if (count == 0) {
            throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_ADD);
        }
    }

    @Override
    public void changePwd(String userId, String old, String password, String confirm) {
        Developer developer = developerMapper.getDeveloperByUserName(userId);
        if (!developer.getPassword().equals(old)) {
            throw new ValueRuntimeException(CodeUtil.USERINFO_OLDPWD_NOT_EQUALS);
        }
        developer.setPassword(confirm);
        if (developerMapper.changePwd(developer) == 0) {
            throw new ValueRuntimeException(CodeUtil.USERINFO_ERR_CHANGE_PWD);
        }
    }
}
