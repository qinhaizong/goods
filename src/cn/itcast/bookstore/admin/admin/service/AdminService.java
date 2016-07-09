package cn.itcast.bookstore.admin.admin.service;

import java.sql.SQLException;

import cn.itcast.bookstore.admin.admin.dao.AdminDao;
import cn.itcast.bookstore.admin.admin.domain.Admin;

public class AdminService {
	private AdminDao adminDao = new AdminDao();
	
	/**
	 * 管理员登录
	 * @param adminname
	 * @param adminpwd
	 * @return
	 */
	public Admin login(String adminname, String adminpwd) {
		try {
			return adminDao.findByAdminnameAndAdminpwd(adminname, adminpwd);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
