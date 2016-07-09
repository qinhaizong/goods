package cn.itcast.bookstore.user.service;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.Session;

import cn.itcast.bookstore.user.dao.UserDao;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

public class UserService {
	private UserDao userDao = new UserDao();
	
	/**
	 * 校验指定登录名的会员是否存在
	 * @param loginname
	 * @return
	 */
	public boolean validateLoginname(String loginname) {
		try {
			return userDao.validateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 校验指定Email的会员是否存在
	 * @param loginname
	 * @return
	 */
	public boolean validateEmail(String email) {
		try {
			return userDao.validateEmail(email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 注册功能
	 * @param user
	 */
	public void regist(User user) {
		try {
			/*
			 * 1. 对user进行数据补全
			 */
			user.setUid(CommonUtils.uuid());
			user.setActivationCode(CommonUtils.uuid() + CommonUtils.uuid());
			user.setStatus(false);
			
			/*
			 * 2. 向数据库添加记录
			 */
			userDao.add(user);
			
			/*
			 * 3. 向用户注册邮箱地址发送“激活”邮件
			 */
			
			// 读取email模板中的数据
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader()
					.getResourceAsStream("email_template.properties"));
			String host = props.getProperty("host");//获取邮件服务器地址
			String username = props.getProperty("username");//获取用户名
			String password = props.getProperty("password");//获取密码
			String from = props.getProperty("from");//获取发件人地址
			String to = user.getEmail();//获取收件人地址
			String subject = props.getProperty("subject");//获取主题
			//获取内容模板，替换其中的激活码
			String content = MessageFormat.format(props.getProperty("content"), 
					user.getActivationCode());
			
			// 发送邮件
			Session session = MailUtils.createSession(host, username, password);
			Mail mail = new Mail(from, to, subject, content);
			MailUtils.send(session, mail);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}
	
	/**
	 * 激活功能
	 * @param activationCode
	 * @throws UserException
	 */
	public void activation(String activationCode) throws UserException {
		User user;
		try {
			/*
			 * 1. 通过激活码查询用户
			 */
			user = userDao.findByActivationCode(activationCode);
			/*
			 * 2. 如果没有查询到用户，说明激活码无效
			 */
			if(user == null) throw new UserException("无效的激活码！");
			/*
			 * 3. 如果用户的状态已经是激活状态，那么说明用户二次激活
			 */
			if(user.isStatus()) throw new UserException("您已经激活了，不要二次激活！");
			/*
			 * 4. 修改用户状态为true，即激活状态。
			 */
			userDao.updateStatus(user.getUid(), true);
		} catch (SQLException e) {
			throw new RuntimeException(e); 
		}
		
	}

	/**
	 * 登录功能
	 * @param formBean
	 * @return
	 */
	public User login(User formBean) {
		try {
			return userDao.findByLoginnameAndLoginpass(
					formBean.getLoginname(), formBean.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException(e); 
		}
	}

	/**
	 * 修改密码
	 * @param uid
	 * @param newpass
	 */
	public void updatePassword(String uid, String newpass) {
		try {
			userDao.updatePassword(uid, newpass);
		} catch (SQLException e) {
			throw new RuntimeException(e); 
		}
	}
}
