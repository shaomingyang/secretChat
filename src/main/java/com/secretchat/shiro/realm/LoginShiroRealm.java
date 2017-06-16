package com.secretchat.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretchat.mybatis.beans.UserBean;
import com.secretchat.mybatis.service.UserService;

public class LoginShiroRealm implements Realm {
	private static Logger logger = LoggerFactory.getLogger(LoginShiroRealm.class);
	
	private UserService userService;
	public LoginShiroRealm() {
		userService = new UserService();
	}
	
	@Override
	public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token)
			throws AuthenticationException {
		// TODO Auto-generated method stub

		String username = (String) token.getPrincipal(); // 得到用户名
		String password = new String((char[]) token.getCredentials()); // 得到密码
		UserBean uBean = userService.findByUserName(username);
		if(uBean == null) {  
			logger.info("平台账户不存在：{}",username);
            throw new UnknownAccountException();//没找到帐号  
        }
		
		if(!uBean.getPassword().equals(password)) {
			logger.info("平台账户密码不正确：{}",password);
            throw new IncorrectCredentialsException(); //如果密码错误  
        } 
		
		// 如果身份认证验证成功，返回一个AuthenticationInfo实现；
		return new SimpleAuthenticationInfo(uBean.getUserName(), uBean.getPassword(), getName());
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "LoginShiroRealm";
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		// TODO Auto-generated method stub
		return token instanceof UsernamePasswordToken;
	}

}
