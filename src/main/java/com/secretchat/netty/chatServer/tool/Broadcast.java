package com.secretchat.netty.chatServer.tool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map.Entry;

public class Broadcast {
	public static void broadcast(String message) {
		for (Entry<String, ChannelHandlerContext> entry : ClientPoll.clientMap.entrySet()) {
			ChannelHandlerContext ctx = entry.getValue();
			ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
		}
	}
}
