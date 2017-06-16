package com.secretchat.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

import com.secretchat.shiro.tool.CryptoUtils;

public class LoginManager {
	
	static {
		//1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager  
	    Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.cfg.ini");  
	    //2、得到SecurityManager实例 并绑定给SecurityUtils  
	    org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();  
	    SecurityUtils.setSecurityManager(securityManager);  
	}
	
	public void login(String userName,String password) {
		/*//1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager  
	    Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.cfg.ini");  
	    //2、得到SecurityManager实例 并绑定给SecurityUtils  
	    org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();  
	    SecurityUtils.setSecurityManager(securityManager);  */
	    //3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）  
	    Subject subject = SecurityUtils.getSubject();
	    
	    //使用户密码加密
	  	String pwdmd5= CryptoUtils.encryptMD5(password);
	    UsernamePasswordToken token = new UsernamePasswordToken(userName, pwdmd5);  
	    
	    try {  
	        //4、登录，即身份验证  
	        subject.login(token);  
	    } catch (AuthenticationException e) {  
	        //5、身份验证失败  
	    }  
	    //Assert.assertEquals(true, subject.isAuthenticated()); //断言用户已经登录 
	}
	
	public void logout() {
		// TODO Auto-generated method stub
		Subject subject = SecurityUtils.getSubject();  
		//6、退出  
		if (subject != null) {
			subject.logout();
		}
	}
	
	public boolean loginCheck() {
		// TODO Auto-generated method stub
		Subject subject = SecurityUtils.getSubject();
		boolean bool = false;
		if (subject.isAuthenticated()) {
			bool = true;
		}
		return bool;
	}
}
