package com.secretchat.tools.push;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretchat.tools.common.Constants;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;

/**
 * 推送用户和内容设置
 * @author chec
 * @date 2015/12/10
 */
public class JPushHelper {
	private static Logger logger = LoggerFactory.getLogger(JPushHelper.class);
	
    /**************************************************
     *  以下方法已确认
     *  管理人：chec
     *  管理时间：17/03/28
     **************************************************/
    
    /**************************************************
     * 以下为私有工具方法 
     */
    private static PushResult sendPush(PushPayload pushPayload,JPushClient client){
    	PushResult result = new PushResult();
    	try {
			result = client.sendPush(pushPayload);
			logger.info("Got result - " + result);
		} catch (APIConnectionException e) {
			e.printStackTrace();
			logger.error("Connection error, should retry later", e);
			logger.error("连接错误，稍后重试", Locale.ENGLISH);
		} catch (APIRequestException e) {
			e.printStackTrace();
			logger.error("Should review the error, and fix the request", e, Locale.ENGLISH);
			logger.info("HTTP Status: " + e.getStatus());
			logger.info("Error Code: " + e.getErrorCode());
			logger.info("Error Message: " + e.getErrorMessage());
			logger.error("应复查错误，并修复请求", e);
		}
		return result;
    }
    
    /**
     * 仅发送通知 （司机）
     * 用于功能：	1.GPS报警信息推送
     * @param msg
     * @param alias
     * @return
     */
    @SuppressWarnings("unchecked")
    public static PushResult sendJPushAliasForMg(String msg, Object alias, String... messType) {
    	JPushClient client = new JPushClient(Constants.MASTER_SECRET,Constants.APP_KEY);
    	List<String> aliasList = null; 
    	if(alias instanceof String) {
    		aliasList = Arrays.asList(alias.toString());
    	}else {
    		aliasList = ((List<String>)alias);
    	}
    	
    	String tag = Constants.APNS_PRODUCTION ? "pro":"test";
    	PushPayload pushPayload = JPushSDK.pushAlertAll(msg, aliasList, Arrays.asList(tag));
    	return sendPush(pushPayload,client);
    }
    
}
