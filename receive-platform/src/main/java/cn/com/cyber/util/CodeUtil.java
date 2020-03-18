package cn.com.cyber.util;


import java.nio.charset.Charset;
import java.util.UUID;

public class CodeUtil {

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr = str.replace("-", "");
        return uuidStr;
    }

    //api验证
    public static final int REDIS_DBINDEX = 1;    //token保存
    public static final String REDIS_PREFIX = "session:";    //token保存
    public static final int REDIS_EXPIRE_TIME = 60 * 30;  //30分

    //code_info表类型
    public static final String CODE_METHOD = "method";
    public static final String CODE_CONTENTTYPE = "content_type";
    public static final String CODE_INSTRUCTIONSPATH = "instructionsPath";
    public static final String CODE_SERVICE_EXCEL = "serviceExcel";
    public static final String FILE_DOWNLOAD_URL = "fileDownloadUrl";
    public static final String CODE_FILE_TYPE = "/file/";
    public static final String CODE_FILE_SERVICEEXCEL = "serviceExcel";

    public static final String DEFAULT_PASSWORD = "888888";  //默认密码

    public static final Charset cs = Charset.forName("UTF-8");

    //content-type编码格式
    public static final String CONTEXT_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTEXT_FORM_DATA = "application/form_data";
    public static final String CONTEXT_JSON = "application/json";
    public static final String RESPONSE_FILE_TYPE = "file";
    public static final String RESPONSE_TEXT_TYPE = "text";

    public static final int HTTP_OK = 200;
    public static final int HTTP_VALID_ERR = 401;
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String MODEL_REQUSET_URL = "/netty/modelRedirect";

    //编码表类型
    public static final String CODEINFO_SERVICETYPE = "service_type";
    public static final String CODEINFO_ENCODED = "encoded";

    public static final String FILE_JEDIS_PREFIX = "file:";  //文件保存到redis前缀

    public static final String TIME_JEDIS_PREFIX = "time:";  //测试请求时间保存

    //接口响应时间设置
    public static final String REQUEST_MAXTIME = "request_maxtime";  //最大超时时间
    public static final String REQUEST_SLEEPTIME = "request_sleeptime";  //扫描响应结果间隔时间

    public static final String PLATFORM_TITLE = "platform_title";
    public static final String SOCKET_URL = "socket_url";
    public static final String SOCKET_PORT = "socket_port";
    public static final String THREADPOOLSIZE = "threadPoolSize";
    public static final String FILE_SEVER_PORT = "file_sever_port";
    public static final String SOCKET_OPEN = "socket_open";
    public static final String FILE_ROOT_PATH = "file_root_path";
    public static final String FILE_DOWN_URL = "file_down_url";
    public static final String DEFAULT_DEPARTMENT = "default_department";  //顶层机构
    public static final String DEVELOPER_VALID_URL = "developer_valid_url";  //跨区域开发者验证ip
    public static final String USER_EDIT = "user_edit";  //用户是否可操作，修改密码
    public static final String PUSH_AREA = "push_area";  //发布区域

    public static final String UP_FILE_PATH = "upload";  //接口附件
    public static final String SERVICE_FILE_PATH = "service";  //接口附件


    //跨区域开发者验证
    public static final String API_VALID_URL = "/api/developer/valid";
    public static final String API_SERVICETREE_URL = "/api/appInfo/appServiceTree";

    //pstore用户校验
    public static final String PSTORE_LOGIN_URL = "pstore_login_url";
    public static final int PSTORE_LOGIN_REDIS_INDEX = 1; //redis保存1号库
    public static final String PSTORE_LOGIN_REDIS_PREFIX = "u:";

    //数据库判断
    public static final String MAPPER_SOURCE = "mapper_source";
    public static final String MAPPER_DB = "mapper_db";
    public static final String MAPPER_DB_ORACLE = "oracle";
    public static final String MAPPER_DB_MYSQL = "mysql";


    //响应状态码
    public static final int BASE_SUCCESS = 0; //请求成功
    public static final int BASE_VALED = 401; //登陆信息失效，请重新登陆
    public static final int REQUEST_TIMEOUT = 99101; //服务接口请求超时
    public static final int REQUEST_CONN_FILED = 99102; //服务接口连接失败
    public static final int REQUEST_USE_FILED = 99103; //访问内网异常
    public static final int REQUEST_PARAM_NULL = 99104;//请求的参数中缺少查询条件
    public static final int REQUEST_APPKEY_NULL = 99105; //请求的参数中appkey元素为空
    public static final int REQUEST_SERVICEKEY_NULL = 99106;//请求的参数中servicekey元素为空
    public static final int REQUEST_SERVICEURL_NULL = 99107;//请求的参数中serviceurl元素为空
    public static final int REQUEST_KEY_FILED = 99108;//请检查appKey或serviceKey是否正确
    public static final int CONNECT_ERR_MOBLIE = 99109;//无法连接移动信息网
    public static final int REQUEST_TOKEN_NULL = 99110;//头信息中缺少 token 信息
    public static final int REQUEST_USER_NULL = 99111;//头信息中缺少 用户 信息
    public static final int REQUEST_TOKEN_ERR = 99112;//token验证失败，请重新获取
    public static final int REQUEST_SERVICE_FILED = 99113;//服务器内部异常
    public static final int BASE_FILE_ERR_UP = 99114;      // 文件上传失败
    public static final int BASE_FILE_ONLY_UP = 99115;    //只能上传单个文件
    public static final int BASE_FILE_NULL = 99116;       //文件不存在
    public static final int BASE_FILE_COPY_ERR = 99117;       //文件拷贝失败
    public static final int REQUEST_KEY_NOT_ONLY = 99118;    //当前接口不是独立接口，请填写正确的appKey
    public static final int REQUEST_TOKEN_USER_FILED = 99119; //用户验证失败
    public static final int BASE_FILE_PATH_NULL = 99120; //请填写文件路径


    //管理平台状态码
    //应用/接口
    public static final int APPINFO_ERR_SELECT = 10101; //未查到应用
    public static final int APPINFO_ERR_OPERATION = 10102; //保存应用失败
    public static final int APPINFO_ERR_UNENABLE = 10103; //应用未通过审核
    public static final int APPINFO_REFUSE_SERVICE = 10104; //接口已被禁用
    public static final int CREATE_ERR_FILE = 10105; //创建文件失败
    public static final int APPINFO_ERR_SERVICETYPE = 10106; //接口文件格式错误
    public static final int APPINFO_ERR_SERVICEKEY = 10107; //请检查内容填写,接口地址以http形式填写
    public static final int APPINFO_ERR_SERVICEREAD = 10108; //读取接口文件失败
    public static final int APPINFO_NULL_SERVICEFILE = 10109; //接口文件不能为空
    public static final int APPSERVICE_ERR_SAVE = 10110; //保存接口失败
    public static final int APPSERVICE_ERR_OPTION = 10111; //接口操作失败
    public static final int APPSERVICE_ERR_VALID = 10112; //接口未授权
    public static final int SERVICE_RECORD_ERR_SAVE = 10113; //接口申请记录保存失败
    public static final int SERVICE_APPLY_ERR_DEL = 10114; //修改旧接口记录失败
    public static final int SERVICE_APPLY_ERR_SAVE = 10115; //接口申请失败
    public static final int SERVICE_APPROVE_ERR_SAVE = 10116; //接口审核失败
    public static final int SERVICE_METHOD_CONTENTTYPE = 10117; //POST请求,请选择请求格式


    //编排接口
    public static final int ARRANGEINFO_ERR_SELECT = 11101; //未查到编排接口
    public static final int ARRANGEINFO_ERR_ADD = 11102; //编排接口保存失败
    public static final int ARRANGERELEVANCE_ERR_ADD = 11103; //编排接口绑定失败
    public static final int ARRANGEINFO_ERR_DEL = 11104; //编排接口删除失败
    public static final int ARRANGEINFO_MORE_ADD = 11105; //对不起，选择接口不能超过3个

    //用户
    public static final int USERINFO_ERR_SELECT = 20101; //未查到用户
    public static final int USERINFO_ERR_ADD = 20102; //添加或编辑用户失败
    public static final int USERINFO_ERR_DEL = 20103; //删除用户失败
    public static final int USERINFO_ERR_CONNECT = 20104; //连接用户查询服务器失败
    public static final int USERINFO_ERR_VALIED = 20105; //用户校验失败，请确认用户信息是否正确
    public static final int USERINFO_ERR_LOGIN = 20106; //用户登陆失败
    public static final int USERINFO_ERR_DEVELOPER_BIND = 20107; //开发者绑定失败
    public static final int USERINFO_ERR_DEVELOPER_DEL = 20108; //开发者绑定记录删除失败
    public static final int USERINFO_NULL_DEVELOPER_BIND = 20109; //请先绑定开发者
    public static final int USERINFO_EXIST = 20110; //用户已存在
    public static final int USERINFO_OLDPWD_NOT_EQUALS = 20111; //原密码不匹配
    public static final int USERINFO_ERR_CHANGE_PWD = 20112; //修改密码失败


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
    public static final Integer ROLE_COMPDEVELOPER = 3;  //公司开发者角色id

}
