package cn.com.cyber.util;

public class MessageInfo {

    public static final int AUTH_HEADER_SIGNATURE_NOTEXIST = 921001;            // 头信息中缺少 signature 信息
    public static final int AUTH_HEADER_APIKEY_NOTEXIST = 921002;               // 头信息中缺少 api_key 信息
    public static final int AUTH_HEADER_TIMESTAMP_NOTEXIST = 921003;            // 头信息中缺少 timestamp 信息
    public static final int AUTH_CACHE_NULL = 921004;                           // 尚未授权或授权已失效
    public static final int AUTH_CACHE_SECURITY_NULL = 921005;                  // 尚未授权或授权已失效
    public static final int AUTH_SIGNATURE_ERR = 921006;                        // 签名错误
    public static final int AUTH_ENCRYPT_ERR = 921007;                          // 加密错误
    public static final int AUTH_HEADER_TIMESTAMP_EXPIRE = 921008;              // 请求已过期
    public static final int AUTH_HEADER_TIMESTAMP_ERR = 921009;                 // 无效的时间戳
    public static final int AUTH_HEADER_PMUSER_NOTEXIST = 921010;               // 用户不存在
    public static final int AUTH_HEADER_PMUSER_INVALID = 921011;                // 无效的用户

    // 公共的一些提示代码
    public static final int SPRING_COMMMON_EXCEPTION = 111000;
    public static final int SPRING_BIND = 111001;
    public static final int SPRING_HTTP_MESSAGE_NOT_READABLE = 111002;
    public static final int PARAMS_MISSING_SERVLET_REQUEST_PART = 111003;
    public static final int PARAMS_MISSING_SERVLET_REQUEST_PARAMTER = 111004;
    public static final int PARAMS_TYPE_MISMATCH = 111005;
    public static final int PARAMS_METHOD_ARGUMENT_NOT_VALID = 111006;

    public static final int PARAMS_ERR_VALIDE = 111100; // 接口入参参数验证不通过

    // 用户模块的提示代码
    public static final int USER_ERR_NOT_EXIST = 121001; // 用户不存在
    public static final int USER_ERR_PASSWORD = 121002; // 密码不正确
    public static final int TOKEN_ERR_NOT_EXIST = 121003; // 令牌无效

    // 用户登陆、用户权限、用户管理、机构 220000
    public static final int USER_ERR_PASSWORD_UPDATE = 221000;// 用户密码更新失败
    public static final int USER_ERR_OLDPASSWORD = 221032;// 原始密码错误
    public static final int USER_SUCC_PASSWORD_UPDATE = 220001;// 用户密码更新成功
    public static final int USER_ERR_ADD = 221002;// 用户添加失败
    public static final int USER_SUCC_ADD = 220003;// 用户添加成功
    public static final int USER_ERR_DEL = 221004;// 用户删除失败
    public static final int USER_SUCC_DEL = 220005;// 用户删除成功
    public static final int USER_EXSIST = 221031;// 该用户已经存在
    public static final int USER_CODE_EXSIST = 221091;// 该警员编号已存在
    public static final int USER_VALIDATE_ERR = 221006;// 获取验证码失败

    public static final int PASSWORD_ERR_UPDATE = 221007;// 密码更新失败
    public static final int PASSWORD_SUCC_UPDATE = 220008;// 密码更新成功

    public static final int PRIVILEGE_SUCC_ADD = 220009;// 权限添加成功
    public static final int PRIVILEGE_ERR_ADD = 221010;// 权限添加失败
    public static final int PRIVILEGE_ERR_UPDATE = 221011;// 权限更新失败
    public static final int PRIVILEGE_SUCC_UPDATE = 220012;// 权限更新成功

    public static final int RESOURCE_ERR_UPDATE = 221012;// 资源更新失败
    public static final int RESOURCE_SUCC_UPDATE = 220013;// 资源更新成功

    public static final int RESOURCE_ERR_ADD = 221014;// 资源添加失败
    public static final int RESOURCE_SUCC_ADD = 220015;// 资源添加成功

    public static final int ALLOT_RESOURCE_FOR_PRIVILEGE_ERROR = 221016;// 分配资源失败
    public static final int ALLOT_RESOURCE_FOR_PRIVILEGE_SUCC = 220017;// 分配资源成功
    public static final int ALLOT_PRIVILEGE_FOR_ROLE_ERROR = 221018;// 分配权限失败
    public static final int ALLOT_PRIVILEGE_FOR_ROLE_SUCC = 220019;// 分配权限成功

    public static final int ALLOT_ROLE_FOR_USER_ERROR = 221020;// 分配角色失败
    public static final int ALLOT_ROLE_FOR_USER_SUCC = 220021;// 分配角色成功

    public static final int ROLE_ERR_ADD = 221022;// 角色添加失败
    public static final int ROLE_SUCC_ADD = 220023;// 角色添加成功

    public static final int LOGIN_SUCC = 220024;// 登陆成功
    public static final int LOGIN_NULL = 221025;// 登陆信息为空
    public static final int LOGIN_VALI_WRONG = 221026;// 验证码错误
    public static final int LOGIN_USER_PASSWRANG = 221027;// 密码错误
    public static final int LOGIN_ERROR = 221028;// 登陆失败
    public static final int LOGIN_USER_NULL = 221030;// 用户不存在

    public static final int USER_SUCC_UPDATE = 220031;// 用户更新成功
    public static final int USER_ERR_UPDATE = 221033;// 用户更新失败

    public static final int RESOURCE_SUCC_DEL = 220034;// 资源删除成功
    public static final int RESOURCE_ERR_DEL = 221035;// 资源删除失败

    public static final int RESOURCE_ERR_DEL_USED = 221036;// 删除失败,该资源已经被分配给对应的权限

    public static final int PRIVILEGE_ERR_DEL = 221037;// 权限删除失败
    public static final int PRIVILEGE_SUCC_DEL = 220038;// 权限删除成功

    public static final int ROLE_ERR_DEL = 221039;// 角色删除失败
    public static final int ROLE_SUCC_DEL = 220040;// 角色删除成功

    public static final int ROLE_ERR_DEL_USED = 221041;// 删除失败,该角色已经被分配给用户

    public static final int ROLE_ERR_UPDATE = 221042;// 角色更新失败
    public static final int ROLE_SUCC_UPDATE = 220043;// 角色更新成功

    public static final int DEPT_ERR_UPDATE = 221044;// 机构更新失败
    public static final int DEPT_SUCC_UPDATE = 220045;// 机构更新成功

    public static final int DEPT_ERR_ADD = 221046;// 机构添加失败
    public static final int DEPT_SUCC_ADD = 220047;// 机构添加成功

    public static final int DEPT_ERR_EXSIST = 221048;// 该机构已经存在

    public static final int DEPT_ERR_DEL = 221049;// 机构删除失败
    public static final int DEPT_SUCC_DEL = 220050;// 机构删除成功

    public static final int FILE_SUCC_UPLOAD = 220051; // 上传文件成功
    public static final int FILE_ERR_UPLOAD = 221051; // 上传文件失败
    public static final String FILE_ERR_DOWNLOAD = "下载文件失败";

    public static final int FILE_DIR_CONFIG_ERR = 221052; //读取路径配置文件错误！

    public static final int REPORT_TEMPLATE_SUCC_UPLOAD = 220053; // 上传模板文件成功
    public static final int REPORT_TEMPLATE_ERR_UPLOAD = 221053; // 上传模板文件失败

    public static final int REPORT_WELCOME_PAGE_ERR = 221054; // 填充数据到首页失败

    public static final int ADVICE_ADD_SUCC = 800001; //意见新增成功
    public static final int ADVICE_ADD_ERR = 801001; //意见新增失败
    public static final int ADVICE_REPLY_SUCC = 800002; //意见回复成功
    public static final int ADVICE_REPLY_ERR = 801002; //意见回复失败

}