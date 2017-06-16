package com.secretchat.netty.chatServer;

import com.secretchat.netty.chatServer.handler.WebSocketServerHandler;
import com.secretchat.tools.common.Constants;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatServer {
	private int port;
	public ChatServer(int port) {
		// TODO Auto-generated constructor stub
		this.port = port;
	}
	
	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("http-codec",new HttpServerCodec());					// Http消息编码解码  
					pipeline.addLast("aggregator",new HttpObjectAggregator(64*1024));	 	// Http消息组装  
					pipeline.addLast("http-chunked",new ChunkedWriteHandler());				// WebSocket通信支持  
					//pipeline.addLast(new WebSocketServerProtocolHandler("/"+Constants.WS_METHOD));
					/*pipeline.addLast(new HttpRequestHandler("/ws"));
					pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
					pipeline.addLast(new TextWebSocketFrameHandler());*/
					pipeline.addLast("handler",new WebSocketServerHandler());
				}
				
			}).option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			// 绑定端口，开始接收进来的连接
			ChannelFuture f = b.bind(port).sync();
			
			System.out.println( this.getClass().getSimpleName() + "聊天服务器启动");
			//new Thread(new CheckRunningPoll()).start();
			
			// 等待服务器 socket 关闭 。
			// 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			System.out.println( this.getClass().getSimpleName() + "聊天服务器关闭");
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = Constants.WS_PORT;
		}
		
		new ChatServer(port).run();
	}
}
