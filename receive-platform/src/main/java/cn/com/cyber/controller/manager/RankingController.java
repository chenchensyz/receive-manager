package cn.com.cyber.controller.manager;

/**
 * 首页统计
 */

import cn.com.cyber.controller.BaseController;
import cn.com.cyber.model.AppService;
import cn.com.cyber.service.RankingService;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
import cn.com.cyber.util.common.RestResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ranking")
public class RankingController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RankingController.class);


    @Autowired
    private RankingService rankingService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    //访问排行
    @RequestMapping("/app/top")
    @ResponseBody
    public RestResponse appRanking() {
        int code = CodeUtil.BASE_SUCCESS;
        Long creator = null;
        if (getShiroUser().source == 1) {
            creator = getShiroUser().id;
        }
        List<AppService> appRanking = rankingService.getReceiveServiceRanking(creator);
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(appRanking);
    }

    //快捷入口
    @RequestMapping("/inlet")
    @ResponseBody
    public RestResponse inlet() {
        int code = CodeUtil.BASE_SUCCESS;
        Long creator = null;
        if (getShiroUser().source == 1) {
            creator = getShiroUser().id;
        }
        Map<String, Object> map = rankingService.inletCount(creator);
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(map);
    }

    //服务请求统计
    @RequestMapping("/receiveLog")
    @ResponseBody
    public RestResponse receiveLog(@RequestBody String param) {
        int code = CodeUtil.BASE_SUCCESS;
        JSONObject jsonObject = JSONObject.parseObject(param);
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        JSONArray dateList = jsonObject.getJSONArray("dateList");
        Long creator = null;
        if (getShiroUser().source == 1) {
            creator = getShiroUser().id;
        }
        Map<String, Object> map = rankingService.receiveLogRanking(creator,startTime, endTime, dateList.toJavaList(String.class));
        return RestResponse.res(code, messageCodeUtil.getMessage(code)).setData(map);
    }
}
