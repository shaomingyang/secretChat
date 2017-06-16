SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE IF EXISTS users;




/* Create Tables */
CREATE TABLE users (
	-- 用户主键ID
	id int(11) NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
	-- 登录用户名
	userName varchar(19) NOT NULL COMMENT '登录用户名',
	-- 用户昵称
	nickName varchar(19) COMMENT '用户昵称',
	-- 密码(shiro加密)
	password varchar(19) COMMENT '密码(shiro加密)',
	-- 性别：男、女
	sex char(2) COMMENT '性别：男、女',
	-- 联系手机号
	mobile varchar(19) COMMENT '联系手机号',
	-- 最后登录IP
	loginIp varchar(19) COMMENT '最后登录IP',
	-- 最后登录时间
	loginTime varchar(19) COMMENT '最后登录时间',
	PRIMARY KEY (id)
);



