package com.secretchat.mybatis.mapper;

import com.secretchat.mybatis.beans.UserBean;

public interface UserMapper {
	/**
	 * 根据用户名获取用户信息
	 * @param userName
	 * @return
	 */
	public UserBean findByUserName(String userName);

	/**
	 * 根据用户名更新用户信息
	 * @param bean
	 * @return
	 */
	public int updateUserByName(UserBean bean);
}
