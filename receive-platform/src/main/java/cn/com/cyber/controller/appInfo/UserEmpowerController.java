package cn.com.cyber.controller.appInfo;

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.model.TreeModel;
import cn.com.cyber.service.AppServiceService;
import cn.com.cyber.service.PoliceService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.RestResponse;
import cn.com.cyber.util.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/userEmpower")
public class UserEmpowerController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEmpowerController.class);

    @Autowired
    private PoliceService policeService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("list")
    public String getUserEmpower() {
        return "appInfo/userEmpower";
    }

    //查询警员机构树
    @RequestMapping("userTree")
    @ResponseBody
    public RestResponse getUserTree(String nodeId) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        rest.setData(policeService.getUserTree(nodeId));
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }

    //查询警员选中的接口
    @RequestMapping("getUserChecked")
    @ResponseBody
    public RestResponse getUserChecked(String userName) {
        int code = CodeUtil.BASE_SUCCESS;
        RestResponse rest = new RestResponse();
        rest.setData(policeService.getUserChecked(userName));
        rest.setCode(code).setMessage(messageCodeUtil.getMessage(code));
        return rest;
    }

    //获取选中接口
    @RequestMapping("saveUserService")
    @ResponseBody
    public RestResponse saveUserService(@RequestBody String param) {
        int code = CodeUtil.BASE_SUCCESS;
        JSONObject jsonObject = JSONObject.parseObject(param);
        String userName = jsonObject.getString("userName");
        List<TreeModel> params = JSONArray.parseArray(jsonObject.getString("params"), TreeModel.class);
        try {
            policeService.saveUserService(userName, params, getShiroUser().userId);
        } catch (ValueRuntimeException e) {
            code = (Integer) e.getValue();
        }
        return RestResponse.res(code, messageCodeUtil.getMessage(code));
    }
}