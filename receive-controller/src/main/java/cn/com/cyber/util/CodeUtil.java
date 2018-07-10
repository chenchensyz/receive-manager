package cn.com.cyber.util;


public class CodeUtil {

	//content-type编码格式
	public static final String CONTEXT_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String CONTEXT_FORM_DATA = "application/form_data";
	public static final String CONTEXT_JSON = "application/json";

	//转发地址
//	public static final String RECEIVE_URL = "http://192.168.1.88:8082/receive/redirect/receiveReturn";
//	public static final String FORWARD_URL = "http://192.168.1.88:8082/receive/forward";
	public static final String RECEIVE_URL = "192.168.22.131";
	public static final String FORWARD_URL = "192.168.20.182";
//    public static final String RECEIVE_URL = "192.168.1.88";
//    public static final String FORWARD_URL = "192.168.1.88";
	public static final int SOCKET_PORT=8081;

	public static final int REQUEST_TIMEOUT= 99101; //服务接口请求超时
	public static final int REQUEST_CONN_FILE= 99102; //=服务接口连接失败
	public static final int REQUEST_USE_FILE= 99103; //服务接口调用异常
	public static final int REQUEST_PARAM_NULL= 99104;//请求的参数中缺少查询条件
	public static final int REQUEST_APPKEY_NULL=99105; //请求的参数中appkey元素为空
	public static final int REQUEST_SERVICEKEY_NULL=99106;//请求的参数中servicekey元素为空
	public static final int REQUEST_SERVICEURL_NULL=99107;//请求的参数中serviceurl元素为空
}
