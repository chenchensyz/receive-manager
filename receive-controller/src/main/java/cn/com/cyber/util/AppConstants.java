package cn.com.cyber.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统常量
 * 
 * @author xiegang
 * @since 2011-12-11
 */
@Component
public class AppConstants {
    /**
     * 系统编码
     */
    public static final String APP_ENCODING = "UTF-8";
    /**
     * redis编码
     */
    public static final String REDIS_ENCODING = "GB2312";
    /**
     * 横杠 分隔符
     */
    public static final String SEPERATOR = "-";
    /**
     * 时间模式（yyyyMMddHHmmssSSS）
     */
    public static final String DATETIME_PATTERN = "yyyyMMddHHmmssSSS";
    /**
     *
     */
    public static final String RESTFULLTEMPLATE_HOST = "http://localhost:8081/sa-web/";

    public static final String SESSION_PAGE_SIZE = "pageSize";

    /**
     * 回话中user属性的名称
     */
    public static final String SESSION_USER = "sessionuser";

    /**
     * 操作提示，成功或失败
     */
    public final static String OPERATION_FAILURE = "op_failure";
    public final static String OPERATION_SUCCESS = "op_success";
    /**
     * 客户警告
     */
    public final static int CUSTOMER_WARN_DAY = 6;
    public final static int CUSTOMER_WARN_NOTICE= 7;
    /**
     * 工单信息
     */
    public final static int ORDER_INFO = 7;

    public final static int NOTE_STAT_INTERVAL = 4;

    public final static String RETURNURL = "returnUrl";
    public final static String HOLIDAYTIP = "拜访记录提交日期为假期";
    public final static String NOTETIMEVALID = "拜访记录的拜访时间不正确";
    public final static String NOTEPRENULL = "客户尚未设置基点";

    public static final String FILE_IMAGE = "image";
    public static final String FILE_UPLOAD = "upload";         						// 上传附件存放文件夹
    public static final String FILE_CUT = "cut";
    public static final String FILE_GROUP_LOGO = "group";

    public static final int VERSION_FILE_NOAUTH = 1;								// 上传目录没有写权限
    public static final int VERSION_FILE_NOMULTI = 2;								// 不能上传多个文件


    public static String USERLEAVESETTING_UPLOAD_FILEPATH = "E:\\pmmanage\\upload\\userleavesetting";
    public static String VERSION_UPLOAD_FILEPATH = "E:\\pmmanage\\temp";								// 临时上传的版本文件保存的目录（注入）
    public static String VERSION_DOWNLOAD_FILEPATH = "E:\\pmmanage\\version";							// 文件真正存放的地址（注入）
    public static String VERSION_SERVERPATH = "http://127.0.0.1:8080/download/";
    public static String CONFIGURATIONS_UPLOAD_FILEPATH = "E:\\pmmanage\\configuration";                        // 配置文件保存的文件目录

    public static String GROUPFILE_DOWNLOAD_FILEPATH = "E:\\pmmanage\\temp";								// 临时上传的版本文件保存的目录（注入）
    public static final String GROUPFILE_DOWNLOAD_FILEPATH_FRAGMENT = "groupfile";

	public final static String GROUPFILE_TYPE_GROUP = "group";
	public final static String GROUPFILE_TYPE_DISCUSS = "discuss";

    public static final String VERSION_DOWNLOAD_FILEPATH_FRAGMENT_APP = "app";
    public static final String APPVERSION = "version";

//    public static String LEAVE_ATTACH_UPLOAD_FILEPATH = "E:\\pmmanage\\leave";								// 上传的请假附件保存的目录（注入）
    public static final String LEAVE_ATTACH_DOWNLOAD_FILEPATH_FRAGMENT = "leave";

    public static String ATTACHEMENT_FILESERVER_PATH = "http://localhot:8080/";								// 附件下载地址

    public static String REDIS_PRESENCE_PSG = "psg:*";
    public static String REDIS_PRESENCE_PSG_SUFFIX = ":online";
    public static String REDIS_PRESENCE_PREFIX = "1:";
    public static String REDIS_PRESENCE_HASHKEY_IMPRESENCE = "im_presence";
    public static String REDIS_PRESENCE_HASHKEY_ONLINE = "online";
    public static String REDIS_PRESENCE_HASHKEY_LOGINTIME = "logintime";
    public static String REDIS_ROSTER_PREFIX = "r:";
    public static String REDIS_ROSTER_R_PREFIX = "rr:";
    public static String REDIS_ROSTER_VERSION_PREFIX = "rv:";
    public static String REDIS_ROSTER_VERSION_KEY = "roster_v";
    public static String REDIS_API_AUTH_PREFIX = "as:";

    public static String DEPARTMENT_TREE_NODETYPE_USER = "u";
    public static String DEPARTMENT_TREE_NODETYPE_DEPART = "d";

    public static int ONLINE_STATISTIC_COUNT_NORMAL = 24;
    public static int ONLINE_STATISTIC_COUNT_MORE = 200;

    public static int DICT_NOTICEMESSAGE_TYPE = 2;

    public static final String VERSION_MILESTONE = "1";

    public static final String CONTACTS_DEFAULT_CEILING = "0";
    public static final int CONTACTS_DEFAULT_LEVEL = 2;


    public static final int PMUSER_TYPE_DEFAULT = 0;
    public static final int PMUSER_TYPE_DOMAIN = 1;


    // 返回的空的消息
    public static final String RET_MESSAGE_EMPTY = "";
    public static final String RET_MESSAGE_DATAS = "datas";

    // 运维用户状态 1.待审核， 2. 正常， 3.禁用
    public static final int OPERATOR_STATE_VERIFYING = 1;
    public static final int OPERATOR_STATE_NORMAL = 2;
    public static final int OPERATOR_STATE_FORBIDDING = 3;

    public static final int OPERATOR_TYPE_SUBSCRIPTION = 1;

    // 订阅号用户回话名称
    public static final String SESSION_OPERATOR = "sessionoperator";
    // 考勤用户会话名称
    public static final String SESSION_WORKATTENDANCE = "sessionworkattendance";
    public static final String SESSION_LEAVE = "sessionleave";
    public static final String SESSION_RED_PACKET = "sessionredpacket";

    // 用户认证APPID、SECURITY_KEY 默认长度
    public static final int CLINET_LOGIN_KEY_LENGTH = 32;

    // 公众平台票据有效时长
    public static final int PUBLICPLATFORM_TOKEN_EXPIRE = 2 * 60 * 60;
    public static final int PUBLICPLATFORM_TOKEN_EXPIRE_DELAY = 1 * 60;

    // 公众平台票据保存字段
    public static final String PUBLICPLATFORM_TOKEN_FILED_APPID = "appid";
    public static final String PUBLICPLATFORM_TOKEN_FILED_SECRET = "secret";
    public static final String PUBLICPLATFORM_TOKEN_FILED_NAME = "name";
    public static final String PUBLICPLATFORM_TOKEN_FILED_NUM = "num";
    public static final String PUBLICPLATFORM_TOKEN_FILED_ID = "id";

    // 查询消息的默认数量
    public static final int MESSAGE_COUNT_DEFAULT = 10;

    // 组消息一次推送的数量










    public static final int LEAVE_ANNUAL_LEAVE = 0;
    public static final int LEAVE_CASUAL_LEAVE = 1;
    public static final int LEAVE_SICK_LLEAVE = 2;
    public static final int LEAVE_MARRIAGE_LLEAVE = 3;
    public static final int LEAVE_BEREAVEMENT_LLEAVE = 4;
    public static final int LEAVE_MATERMITY_LLEAVE = 5;
    public static final int LEAVE_PATERNITY_LLEAVE = 6;
    public static final int LEAVE_LIEU_LLEAVE = 7;
    
    public static Map<Integer, String> typeMap = new HashMap<Integer, String>();
    static {
    	typeMap.put(AppConstants.LEAVE_ANNUAL_LEAVE, "年假");
    	typeMap.put(AppConstants.LEAVE_CASUAL_LEAVE, "事假");
    	typeMap.put(AppConstants.LEAVE_SICK_LLEAVE, "病假");
    	typeMap.put(AppConstants.LEAVE_MARRIAGE_LLEAVE, "婚假");
    	typeMap.put(AppConstants.LEAVE_BEREAVEMENT_LLEAVE, "丧假");
    	typeMap.put(AppConstants.LEAVE_MATERMITY_LLEAVE, "产假");
    	typeMap.put(AppConstants.LEAVE_PATERNITY_LLEAVE, "陪护假");
    	typeMap.put(AppConstants.LEAVE_LIEU_LLEAVE, "调休假");
    }
    public static Map<String, Integer> typeMapR = new HashMap<String, Integer>();
    static {
    	typeMapR.put("年假", AppConstants.LEAVE_ANNUAL_LEAVE);
    	typeMapR.put("事假",AppConstants.LEAVE_CASUAL_LEAVE);
    	typeMapR.put("病假", AppConstants.LEAVE_SICK_LLEAVE);
    	typeMapR.put("婚假", AppConstants.LEAVE_MARRIAGE_LLEAVE);
    	typeMapR.put("丧假", AppConstants.LEAVE_BEREAVEMENT_LLEAVE);
    	typeMapR.put("产假", AppConstants.LEAVE_MATERMITY_LLEAVE);
    	typeMapR.put("陪护假", AppConstants.LEAVE_PATERNITY_LLEAVE);
    	typeMapR.put("调休假", AppConstants.LEAVE_LIEU_LLEAVE);
    }
    
    public static Map<Integer, String> noonMap = new HashMap<Integer, String>();
    static {
    	noonMap.put(0, "上午");
    	noonMap.put(1, "下午");
    }
    public static Map<String, Integer> noonMapR = new HashMap<String, Integer>();
    static {
    	noonMapR.put("上午", 0);
    	noonMapR.put("下午", 1);
    }
    public static final String LEAVE_SCHEDULETIME = "LEAVE_SCHEDULETIME";

    public static String DEPARTMENT_APPROVELISTMAP_JSONSTR = "{\"88880004\":\"xujg\",\"88880002\":\"jiangy\"}";
    public static String CCLIST_STR = "wuyy";

    public static String LEAVE_SUBSCRIPTION_APPID = "";
    public static String MEETINGNOTICE_SUBSCRIPTION_APPID = "";

    public static String SUBSCRIPTION_SERVER_SEND_URL = "";

}
