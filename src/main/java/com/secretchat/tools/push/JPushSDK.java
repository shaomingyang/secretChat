package com.secretchat.tools.push;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretchat.tools.common.Constants;

import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 * 极光推送
 * @author chec
 * @date 2015/12/10
 */
public class JPushSDK {
	
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(JPushSDK.class); 
    
    /**************************************************
     *  以下方法已确认
     *  管理人：chec
     *  管理时间：17/03/28
     **************************************************/
    /**
     * （此方法暂时没用到）
     * 仅发送通知
	 * 构建推送对象：平台是 Android 
	 * 目标是 tag 为 "alias" 的设备，内容是 Android 通知 msg，并且标题为 title。
     * @param 	title	标题
     * @param 	msg		推送消息
     * @param 	alias 	目标别名，对应用户userid
	 * @return 	tags 	目标标签，对应用户分组，暂时分为测试和线上
	 */
    public static PushPayload pushAlertAndroidWithTitle(String title,String msg,List<String> alias,List<String> tags) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(tags))
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.android(msg, title, null)).build();
    }
    
    
    /**
     * （此方法暂时没用到）
     * 仅发送通知
	 * 构建推送对象：平台是 iOS 
	 * 推送目标是 "tag1", "tag_all" 的并集，推送内容同时包括通知与消息 - 通知信息是 ALERT，角标数字为 5，通知声音为 "happy"，
	 * 并且附加字段 from = "JPush"；消息内容是 MSG_CONTENT。通知是 APNs 推送通道的，消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）
	 * 
     * @param tag			目标标签，对应用户分组，暂时分为测试和线上
     * @param alias			目标别名，对应用户userid
     * @param msg			消息内容
     * @param production	苹果设备中判断是线上还是线下
     * @return
	 */
    public static PushPayload pushAlertIOS(List<String> tag,List<String> alias,String msg,boolean production) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.tag(tag))
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(msg)
                                .autoBadge()
                                .setContentAvailable(true)
                                .setSound("happy")
                                .addExtra("from", "JPush")
                                .build()).build())
                 .setOptions(Options.newBuilder()
                         .setApnsProduction(production)//生成true false测试
                         .build()).build();
    }
    
    /**
     * 仅发送自定义消息
     * 构建推送对象：平台是 Andorid 与 iOS  
     * 推送目标是 （"tag1" 与 "tag2" 的交集）并（"alias1" 与 "alias2" 的交集），推送内容是 - 内容为 MSG_CONTENT 的消息，并且附加字段 from = JPush。
     * @param msg			消息内容
     * @param tag			目标标签，对应用户分组，暂时分为测试和线上
     * @param alias			目标别名，对应用户userid
     * @return
     */
    public static PushPayload pushMsgAll(String msg, List<String> tags, List<String> alias) {
    	return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag(tags))
                        .addAudienceTarget(AudienceTarget.alias(alias)).build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg)
                        .addExtra("from", "JPush").build()).build();
    }
    
    /**
     * 仅发送通知 
  	 * 构建推送对象：所有平台  
  	 * 推送目标是别名为 "alias"，通知内容为 msg。
  	 * @param msg	消息内容
  	 * @param alias 目标别名，对应用户userid
  	 * @return
  	 */
	public static PushPayload pushAlertAll(String msg,List<String> alias,List<String> tags) {
		return PushPayload.newBuilder()
				.setPlatform(Platform.all())
				.setAudience(
						Audience.newBuilder()
								.addAudienceTarget(AudienceTarget.tag(tags))
								.addAudienceTarget(AudienceTarget.alias(alias))
								.build())
				.setNotification(
						Notification
								.newBuilder()
								.addPlatformNotification(
										AndroidNotification.alert(msg))
								.addPlatformNotification(
										IosNotification.newBuilder()
												.setAlert(msg).autoBadge()
												.setContentAvailable(true)
												.setSound("happy")
												.addExtra("from", "JPush")
												.build()).build())
				.setOptions(
						Options.newBuilder()
								.setApnsProduction(Constants.APNS_PRODUCTION)
								.build()).build();
     }
	
	/**
     * 发送通知和自定义消息 
  	 * 构建推送对象：所有平台  
  	 * 推送目标是别名为 "alias"，通知内容为 msg。
  	 * @param msg	消息内容
  	 * @param alias 目标别名，对应用户userid
  	 * @return
  	 */
	public static PushPayload pushAlertMsgAll(String alert,String msg,List<String> alias,List<String> tags) {
		return PushPayload.newBuilder()
				.setPlatform(Platform.all())
				.setAudience(
						Audience.newBuilder()
								.addAudienceTarget(AudienceTarget.tag(tags))
								.addAudienceTarget(AudienceTarget.alias(alias))
								.build())
				.setNotification(
						Notification
								.newBuilder()
								.addPlatformNotification(
										AndroidNotification.alert(alert))
								.addPlatformNotification(
										IosNotification.newBuilder()
												.setAlert(alert).autoBadge()
												.setContentAvailable(true)
												.setSound("happy")
												.build()).build())
				.setMessage(Message.newBuilder()
                        .setMsgContent(msg)
                        .build())
				.setOptions(
						Options.newBuilder()
								.setApnsProduction(Constants.APNS_PRODUCTION)
								.build()).build();
     }
}
