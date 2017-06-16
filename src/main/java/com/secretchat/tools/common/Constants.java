package com.secretchat.tools.common;

import java.nio.charset.Charset;

/**
 * 项目中使用的常量
 * 
 * @author smachen
 */
public class Constants {
	public static final ReadPropsUtils read = new ReadPropsUtils();
	
	public static final String string_encoding = "GBK";
	public static final Charset string_charset = Charset.forName(string_encoding);
	

	public static String APP_KEY = "";
    public static String MASTER_SECRET = "";
    public static boolean APNS_PRODUCTION = read.getValue("sys.verion").equals("pro") ? true : false;
    
	/**
	 *  聊天端口和请求方法
	 */ 
    public static int WS_PORT = read.getInt("ws.port");					//服务器端口
    public static String WS_METHOD = read.getValue("ws.req");					//服务器请求方法
}
