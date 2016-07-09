package cn.itcast.bookstore.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

/**
 * 会员持久类
 * @author qdmmy6
 *
 */
public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 校验指定登录名会员是否存在
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public boolean validateLoginname(String loginname) throws SQLException {
		String sql = "select count(1) from t_user where loginname=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), loginname);
		return cnt == null ? false : cnt.intValue() > 0;
	}
	
	/**
	 * 校验指定email的会员是否存在
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public boolean validateEmail(String email) throws SQLException {
		String sql = "select count(1) from t_user where email=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), email);
		return cnt == null ? false : cnt.intValue() > 0;
	}
	
	/**
	 * 添加User
	 * @param user
	 * @throws SQLException
	 */
	public void add(User user) throws SQLException {
		String sql = "insert into t_user values(?,?,?,?,?,?)";
		Object[] params = {user.getUid(), user.getLoginname(), user.getLoginpass(), 
				user.getEmail(), user.isStatus(), user.getActivationCode()};
		qr.update(sql, params);
	}
	
	/**
	 * 通过激活码查询用户
	 * @param activationCode
	 * @return
	 * @throws SQLException
	 */
	public User findByActivationCode(String activationCode) throws SQLException {
		String sql = "select * from t_user where activationCode=?";
		return qr.query(sql, new BeanHandler<User>(User.class), activationCode);
	}
	
	/**
	 * 通过uid修改用户状态
	 * @param uid
	 * @param status
	 * @throws SQLException
	 */
	public void updateStatus(String uid, boolean status) throws SQLException {
		String sql = "update t_user set status=? where uid=?";
		qr.update(sql, status, uid);
	}

	/**
	 * 按用户名和密码查询用户
	 * @param loginname
	 * @param loginpass
	 * @return
	 * @throws SQLException 
	 */
	public User findByLoginnameAndLoginpass(String loginname, String loginpass) throws SQLException {
		String sql = "select * from t_user where loginname=? and loginpass=?";
		return qr.query(sql, new BeanHandler<User>(User.class), loginname, loginpass);
	}

	/**
	 * 修改密码
	 * @param uid
	 * @param newpass
	 * @throws SQLException 
	 */
	public void updatePassword(String uid, String newpass) throws SQLException {
		String sql = "update t_user set loginpass=? where uid=?";
		qr.update(sql, newpass, uid);
	}
}
