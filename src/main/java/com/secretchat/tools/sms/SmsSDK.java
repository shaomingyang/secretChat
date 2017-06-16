package com.secretchat.tools.sms;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.secretchat.tools.common.ReadPropsUtils;

/**
 * 创蓝短信接口通用代码
 * 
 * @author chec
 */
public class SmsSDK {
	private static Logger logger = LoggerFactory.getLogger(SmsSDK.class); 
	/**
	 * 
	 * @param uri 应用地址，类似于http://ip:port/msg/
	 * @param account 账号
	 * @param pswd 密码
	 * @param mobiles 手机号码，多个号码使用","分割
	 * @param content 短信内容
	 * @param needstatus 是否需要状态报告，需要true，不需要false
	 * @return 返回值定义参见HTTP协议文档
	 * @throws Exception
	 */
	public static String send( String mobiles, String content,
			 String product, String extno) throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		//读取配置文件的类
		ReadPropsUtils readPropsUtils=new ReadPropsUtils();
		String uri = readPropsUtils.getValue("sms.url");
		String account = readPropsUtils.getValue("sms.account");
		String pswd= readPropsUtils.getValue("sms.password");
		boolean needstatus=true;
		try {
			URI base = new URI(uri, false);
			method.setURI(new URI(base, "HttpSendSM", false));
			method.setQueryString(new NameValuePair[] { 
					new NameValuePair("account", account),
					new NameValuePair("pswd", pswd), 
					new NameValuePair("mobile", mobiles),
					new NameValuePair("needstatus", String.valueOf(needstatus)), 
					new NameValuePair("msg", content),
					new NameValuePair("product", product), 
					new NameValuePair("extno", extno), 
				});
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}

	}

	/**
	 * 
	 * @param uri 应用地址，类似于http://ip:port/msg/
	 * @param account 账号
	 * @param pswd 密码
	 * @param mobiles 手机号码，多个号码使用","分割
	 * @param content 短信内容
	 * @param needstatus 是否需要状态报告，需要true，不需要false
	 * @return 返回值定义参见HTTP协议文档
	 * @throws Exception
	 */
	public static String batchSend(String mobiles, String content,
			 String product, String extno) throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		//读取配置文件的类
		ReadPropsUtils readPropsUtils=new ReadPropsUtils();
		String uri = readPropsUtils.getValue("sms.url");
		String account = readPropsUtils.getValue("sms.account");
		String pswd= readPropsUtils.getValue("sms.password");
		boolean needstatus=true;
		try {
			URI base = new URI(uri, false);
			method.setURI(new URI(base, "HttpBatchSendSM", false));
			method.setQueryString(new NameValuePair[] { 
				new NameValuePair("account", account),
				new NameValuePair("pswd", pswd), 
				new NameValuePair("mobile", mobiles),
				new NameValuePair("needstatus", String.valueOf(needstatus)), 
				new NameValuePair("msg", content),
				new NameValuePair("product", product),
				new NameValuePair("extno", extno), 
			});
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}

	}
	
	/**
	 * 创蓝语音服务接口
	 * 模版ID
		105119 您管理的车辆[plateNumber]已持续超速[time]，请您尽快处理，以免发生安全事故
	 * @param callingline	要发送的手机号
	 * @param telTemp 		语音模板id 向服务商提供文字模版申请模版ID
	 * @param contextparm	模板内容有变量则必填，无变量则不填 默认发送模板内容
	 * @return
	 * @throws Exception
	 */
	public static String sendNotice( String callingLine,String telTemp, String contextParm) throws Exception {
		//读取配置文件的类
		ReadPropsUtils readPropsUtils=new ReadPropsUtils();
		String url = readPropsUtils.getValue("notice.url");				//String url="http://audio.253.com";
		String company = readPropsUtils.getValue("notice.company");		//String company="YM4967885";					//帐号名
		String passwd = readPropsUtils.getValue("notice.password");		//String passwd="8JFDmOdboK4b82";				//密码
		String key = readPropsUtils.getValue("notice.key");				//String k="4c238b35bb1feee3c10288652f83b0a4";	//密钥key	
		String telNo = readPropsUtils.getValue("notice.telNo");			//String telno="95213176"; 						//去电显号，服务商提供不能修改
		String sex = readPropsUtils.getValue("notice.sex");				//String sex="2";								//1男声  2女生 3自定义
		//String teltemp="105119";										//语音模板id 向服务商提供文字模版申请模版ID	
		//String callingline="18651401703";								//要发送的手机号 15312989999
		//String contextparm="plateNumber:苏A12345,time:2分钟"; 			//模板内容有变量则必填，无变量则不填 默认发送模板内容
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String keytime = sdf.format(new Date()); //当前时间戳
		String keys=MD5(key+passwd+keytime);
		
		JSONObject params = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("contextparm", contextParm);
		data.put("company", company); 
		data.put("teltemp", telTemp);	
		data.put("telno",telNo);		 	
		data.put("callingline", callingLine);
		data.put("key", keys);
		data.put("sex",sex);	
		data.put("requestid", "2");	
		data.put("keytime", keytime);
		data.put("ivr", null);
		params.put("userinfo", data);
		
		HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
		PostMethod method = new PostMethod();
		try {
			URI base = new URI(url, false);
			method.setURI(new URI(base, "/noticeapi/noticeapi_api", false));
			method.setRequestBody(new NameValuePair[] {
				new NameValuePair("userinfo", params.toString())
			});
			HttpMethodParams param = new HttpMethodParams();
			param.setContentCharset("utf-8");
			method.setParams(param);
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				byte[] ba = method.getResponseBody();
				method.getResponseBodyAsStream();
				String str= new String(ba,"GBK");
				logger.info("语音短信模版{}发送到{},响应值为:{}",telTemp,callingLine,str);
				return URLDecoder.decode(str, "GBK");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}
	}
	
	/**
	 * MD5加密
	 * @param decript
	 * @return
	 */
	private static String MD5(String decript) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	}
	
}
