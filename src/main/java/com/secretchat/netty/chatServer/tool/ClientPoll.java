package com.secretchat.netty.chatServer.tool;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class ClientPoll {
	public static Map<String,ChannelHandlerContext> clientMap=new HashMap<String, ChannelHandlerContext>();
}
