package cn.com.cyber.tools;

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

}