package com.secretchat.netty.chatServer.tool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map.Entry;

public class CheckRunningPoll implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
            while (true) {
                for (Entry<String, ChannelHandlerContext> entry : ClientPoll.clientMap.entrySet()) {
                    String clientIP=entry.getKey();
                    ChannelHandlerContext ctx=entry.getValue();
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("您的服务器IP是："+clientIP+",服务器发给您的消息是："+ new java.util.Date().toString()));
                }

                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
