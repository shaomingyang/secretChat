package com.secretchat.netty;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretchat.netty.chatServer.ChatServer;
import com.secretchat.tools.common.Constants;


public class StartBusTx implements ServletContextListener{
	private static Logger logger = LoggerFactory.getLogger(StartBusTx.class); 
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("###### Netty转发程序启动失败 ######");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("###### Netty转发程序启动开始 ######");
        new Thread(){  
            @Override  
            public  void run(){  
                try {  
                	new ChatServer(Constants.WS_PORT).run();
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }.start();  
        
        /*new Thread(){  
            @Override  
            public  void run(){  
                try {  
                	new GwServer().bind();
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }.start(); 
        
        new Thread(){  
            @Override  
            public  void run(){  
                try {  
                	new GwClient().connect();
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }.start(); */ 
        logger.info("###### Netty转发程序启动结束 ######");
	}
}
