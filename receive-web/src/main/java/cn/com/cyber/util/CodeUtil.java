package cn.com.cyber.util;


import java.nio.charset.Charset;

public class CodeUtil {

    //code_info表类型
    public static final String CODE_METHOD ="method";
    public static final String CODE_CONTENTTYPE ="contentType";
    public static final String CODE_INSTRUCTIONSPATH ="instructionsPath";
    public static final String CODE_FILE_TYPE ="file";

    public static final String DEFAULT_PASSWORD = "888888";  //默认密码

    public static final Charset cs = Charset.forName("UTF-8");

    //content-type编码格式
    public static final String CONTEXT_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTEXT_FORM_DATA = "application/form_data";
    public static final String CONTEXT_JSON = "application/json";
    public static final String RESPONSE_FILE_TYPE ="file";
    public static final String RESPONSE_TEXT_TYPE ="text";

    public static final String METHOD_POST= "POST";
    public static final String METHOD_GET = "GET";
    public static final String MODEL_REQUSET_URL = "/netty/modelRedirect";

    public static final String FILE_UPLOAD_PATH = "D:\\path\\";  //文件保存位置
    public static final String FILE_JEDIS_PREFIX = "file:";  //文件保存到redis前缀

    public static final String TIME_JEDIS_PREFIX = "time:";  //测试请求时间保存

    //接口响应时间设置
    public static final String REQUEST_MAXTIME = "request_maxtime";  //最大超时时间
    public static final String REQUEST_SLEEPTIME = "request_sleeptime";  //扫描响应结果间隔时间

    //响应状态码
    public static final int SELECT_SUCCESS = 0; //请求成功
    public static final int REQUEST_TIMEOUT = 99101; //服务接口请求超时
    public static final int REQUEST_CONN_FILE = 99102; //服务接口连接失败
    public static final int REQUEST_USE_FILE = 99103; //访问内网异常
    public static final int REQUEST_PARAM_NULL = 99104;//请求的参数中缺少查询条件
    public static final int REQUEST_APPKEY_NULL = 99105; //请求的参数中appkey元素为空
    public static final int REQUEST_SERVICEKEY_NULL = 99106;//请求的参数中servicekey元素为空
    public static final int REQUEST_SERVICEURL_NULL = 99107;//请求的参数中serviceurl元素为空
    public static final int REQUEST_KEY_FILED = 99108;//请检查appKey或serviceKey是否正确

    //管理平台状态码
    //应用
    public static final int APPINFO_ERR_SELECT = 10101; //未查到应用
    public static final int APPINFO_ERR_OPERATION = 10102; //添加或修改应用失败
    public static final int APPINFO_ERR_UNENABLE = 10103; //应用未通过审核
    //用户
    public static final int USERINFO_ERR_SELECT = 20101; //未查到用户
    public static final int USERINFO_ERR_ADD = 20102; //添加或编辑用户失败
    public static final int USERINFO_ERR_DEL= 20103; //删除用户失败
    //公司
    public static final int COMPANYINFO_ERR_SELECT = 30101; //未查到公司
    public static final int COMPANYINFO_ERR_OPERATION = 30102; //添加或修改公司失败
    public static final int COMPANYINFO_ERR_DELECT = 30103; //删除公司失败
    /**
     * app状态 审核通过
     */
    public static final int APP_STATE_ENABLE = 1;
    /**
     * app状态 待审核
     */
    public static final int APP_STATE_REGISTER = 0;
    /**
     * app状态 拒绝
     */
    public static final int APP_STATE_CANCEL = -1;

    /**
     * app状态 待提交
     */
    public static final int APP_STATE_REFUSE = 2;


    public static final long ROLE_COMPMANAGER = 2l;  //公司管理员角色id
    public static final long ROLE_COMPDEVELOPER = 3l;  //公司开发者角色id
}
