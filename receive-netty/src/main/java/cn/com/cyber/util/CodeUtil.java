package cn.com.cyber.util;


import java.nio.charset.Charset;

public class CodeUtil {

    //content-type编码格式
    public static final String CONTEXT_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTEXT_FORM_DATA = "application/form_data";
    public static final String CONTEXT_JSON = "application/json";

    public static final int HTTP_OK = 200;
    public static final String RESPONSE_POST = "POST";
    public static final String RESPONSE_GET = "GET";
    public static final String RESPONSE_FILE_TYPE = "file";
    public static final String RESPONSE_TEXT_TYPE = "text";

    public static final String FILE_UPLOAD_PATH = "D:\\source\\";  //文件保存位置
    public static final String FILE_JEDIS_PREFIX = "file:";  //保存到redis前缀

    public static final String TIME_JEDIS_PREFIX = "time:";  //测试请求时间保存

    public static final Charset cs = Charset.forName("UTF-8");

    //接口响应时间设置
    public static final String REQUEST_MAXTIME = "request_maxtime";  //最大超时时间
    public static final String REQUEST_SLEEPTIME = "request_sleeptime";  //扫描响应结果间隔时间

    public static final String PROJECT_ENVIRONMENT = "project_environment";
    public static final String SOCKET_URL = "socket_url";
    public static final String SOCKET_SERVER_PORT = "socket_server_port";
    public static final String SOCKET_CLIENT_PORT = "socket_client_port";
    public static final String THREADPOOLSIZE = "threadPoolSize";
    public static final String FILE_SEVER_PORT = "file_sever_port";
    public static final String FILE_SAVE_PATH = "file_save_path";
    public static final String CACHE_TIME = "cache_time";
    public static final String SOCKET_OPEN = "socket_open";

    //响应状态码
    public static final int REQUEST_TIMEOUT = 99101; //服务接口请求超时
    public static final int REQUEST_CONN_FILED = 99102; //服务接口连接失败
    public static final int REQUEST_USE_FILED = 99103; //访问内网异常
    public static final int REQUEST_PARAM_NULL = 99104;//请求的参数中缺少查询条件
    public static final int REQUEST_APPKEY_NULL = 99105; //请求的参数中appkey元素为空
    public static final int REQUEST_SERVICEKEY_NULL = 99106;//请求的参数中servicekey元素为空
    public static final int REQUEST_SERVICEURL_NULL = 99107;//请求的参数中serviceurl元素为空
    public static final int REQUEST_KEY_FILED = 99108;//请检查appKey或serviceKey是否正确

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
}
