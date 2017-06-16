package com.secretchat.mybatis.service;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretchat.mybatis.beans.UserBean;
import com.secretchat.mybatis.mapper.UserMapper;
import com.secretchat.tools.common.TypeUtils;
import com.secretchat.tools.druid.DBTools;

public class UserService {
	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	/**
	 * 根据用户名获取用户信息
	 * @param userName
	 * @return
	 */
	public UserBean findByUserName(String userName) {
		SqlSession session = DBTools.getSession();
		UserMapper mapper = session.getMapper(UserMapper.class);
		try {
			UserBean userBean = mapper.findByUserName(userName);
			session.commit();
			if (!TypeUtils.isEmpty(userBean)) {
				return userBean;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			session.rollback();
		} finally {
			session.close();
		}
		return null;
	}
	
	/**
	 * 根据ID更新GPS报警数据（单条）
	 * 
	 * @param id 报警ID
	 * @return
	 * @throws Exception
	 */
	public boolean updateUserByName(UserBean bean) {
		boolean bool = false;
		SqlSession session = DBTools.getSession();
		UserMapper mapper = session.getMapper(UserMapper.class);
		try {
			int index = mapper.updateUserByName(bean);
			session.commit();
			return index > 0 ? true : false;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			session.rollback();
		} finally {
			session.close();
		}
		return bool;
	}
	
}
