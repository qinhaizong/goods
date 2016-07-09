package cn.itcast.bookstore.user.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.service.UserException;
import cn.itcast.bookstore.user.service.UserService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();

	/**
	 * 异步校验登录名
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String loginname = req.getParameter("loginname");
		boolean flag = userService.validateLoginname(loginname);// 如果登录名已被注册返回true
		resp.getWriter().print(flag + "");
		return null;
	}

	/**
	 * 校验老密码
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateLoginpass(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 从session中获取当前用户，再获取当前用户的用户名
		 */
		User user = (User) req.getSession().getAttribute("user");
		String loginname = user.getLoginname();

		/*
		 * 2. 获取老密码
		 */
		String loginpass = req.getParameter("loginpass");

		/*
		 * 3. 创建formBean，调用userService.login()方法进行校验
		 */
		User formBean = new User();
		formBean.setLoginname(loginname);
		formBean.setLoginpass(loginpass);
		User _user = userService.login(formBean);

		/*
		 * 4. 如果用户名密码正确，返回true，否则返回false
		 */
		boolean flag = _user != null;
		resp.getWriter().print(flag + "");
		return null;
	}

	/**
	 * 异步校验Email
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateEmail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		boolean flag = userService.validateEmail(email);// 如果Email已被注册返回true
		resp.getWriter().print(flag + "");
		return null;
	}

	/**
	 * 异步校验验证码
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String vCode = (String) req.getSession().getAttribute("vCode");
		String verifyCode = req.getParameter("verifyCode");
		boolean flag = vCode.equalsIgnoreCase(verifyCode);// 如果验证码正确返回true
		resp.getWriter().print(flag + "");
		return null;
	}

	/**
	 * 注册功能
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User对象中
		 */
		User user = CommonUtils.toBean(req.getParameterMap(), User.class);

		/*
		 * 2. 对表单数据进行服务器端校验
		 */
		Map<String, String> errors = validateRegist(user, req);
		if (errors != null && errors.size() > 0) {// 是否存在校验错误信息
			req.setAttribute("errors", errors);
			req.setAttribute("user", user);
			return "f:/jsps/user/regist.jsp";
		}

		/*
		 * 3. 调用userService完成注册
		 */
		userService.regist(user);

		/*
		 * 4. 保存注册成功信息，转发到msg.jsp显示
		 */
		req.setAttribute("code", "success");// 是成功信息还是错误信息
		req.setAttribute("msg", "恭喜，注册成功！请马上到邮箱完成激活！");
		return "f:/jsps/msg.jsp";
	}

	/**
	 * 激活功能
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String activation(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1. 获取激活码
		 */
		String activationCode = req.getParameter("activationCode");
		try {
			/*
			 * 2. 调用userService的activation方法完成激活操作
			 */
			userService.activation(activationCode);
			/*
			 * 3. 如果没有抛出异常，那么保存成功信息
			 */
			req.setAttribute("code", "success");
			req.setAttribute("msg", "恭喜，激活成功，请马上登录！");
		} catch (UserException e) {
			/*
			 * 4. 如果抛出了异步，保存错误信息
			 */
			req.setAttribute("code", "error");
			req.setAttribute("msg", e.getMessage());
		}
		// 无论成功还是失败，都会转发到msg.jsp显示信息。
		return "f:/jsps/msg.jsp";
	}

	/**
	 * 登录功能
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User对象中
		 */
		User formBean = CommonUtils.toBean(req.getParameterMap(), User.class);

		/*
		 * 2. 对formBean进行服务器端表单校验
		 */
		Map<String, String> errors = validateLogin(formBean, req);
		if (errors != null && errors.size() > 0) {// 是否存在校验错误信息
			req.setAttribute("errors", errors);
			req.setAttribute("user", formBean);
			return "f:/jsps/user/login.jsp";
		}

		/*
		 * 3. 调用userService#login(User user)完成登录
		 */
		User user = userService.login(formBean);
		/*
		 * 4. 判断是否查询到User
		 */
		if (user == null) {// 如果用户登录失败
			req.setAttribute("user", formBean);
			req.setAttribute("msg", "用户名或密码错误！");
			return "f:/jsps/user/login.jsp";
		} else if (!user.isStatus()) {// 如果用户还没有激活
			req.setAttribute("user", formBean);
			req.setAttribute("msg", "您还没有激活！");
			return "f:/jsps/user/login.jsp";
		} else {// 如果登录成功
			req.getSession().setAttribute("sessionUser", user);
			// 保存cookie，为了login.jsp页面可以记住当前用户名
			String name = "loginname";
			String value = URLEncoder.encode(user.getLoginname(), "UTF-8");
			Cookie cookie = new Cookie(name, value);
			cookie.setMaxAge(1000 * 60 * 60 * 24);
			resp.addCookie(cookie);
			return "r:/index.jsp";// 重定向到主页
		}
	}

	/**
	 * 退出功能
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String quit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getSession().removeAttribute("user");
		return "r:/jsps/user/login.jsp";
	}

	/**
	 * 修改密码
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User
		 */
		User formBean = CommonUtils.toBean(req.getParameterMap(), User.class);

		/*
		 * 2. 调用userService的updatePassword(String uid, String newpass)方法修改密码
		 */
		User user = (User) req.getSession().getAttribute("user");
		userService.updatePassword(user.getUid(), formBean.getNewpass());

		/*
		 * 3. 转发到msg.jsp
		 */
		req.setAttribute("code", "success");
		req.setAttribute("msg", "恭喜，密码修改成功！");
		return "f:/jsps/msg.jsp";
	}

	/*
	 * 登录校验方法
	 */
	private Map<String, String> validateLogin(User user, HttpServletRequest req) {
		Map<String, String> errors = new HashMap<String, String>();
		// 对loginname进行校验
		String loginname = user.getLoginname();
		if (loginname == null || loginname.isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		} else if (loginname.length() < 3 || loginname.length() > 20) {
			errors.put("loginname", "用户名长度必须在3~20之间！");
		}

		// 对loginpass进行校验
		String loginpass = user.getLoginpass();
		if (loginpass == null || loginpass.isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		} else if (loginpass.length() < 3 || loginpass.length() > 20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}

		// 对验证码进行校验
		String verifyCode = user.getVerifyCode();
		String vCode = (String) req.getSession().getAttribute("vCode");
		if (verifyCode == null || verifyCode.isEmpty()) {
			errors.put("verifyCode", "验证码不能为空！");
		} else if (verifyCode.length() != 4) {
			errors.put("verifyCode", "错误的验证码！");
		} else if (!verifyCode.equalsIgnoreCase(vCode)) {
			errors.put("verifyCode", "错误的验证码！");
		}
		return errors;
	}

	/*
	 * 服务器端表单校验 如果校验通过，返回长度为0的Map，如果校验失败，那么返回的Map中保存的是错误信息。 Map中key为字段名称，值为错误信息。
	 */
	private Map<String, String> validateRegist(User user, HttpServletRequest req) {
		Map<String, String> errors = new HashMap<String, String>();
		// 对loginname进行校验
		String loginname = user.getLoginname();
		if (loginname == null || loginname.isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		} else if (loginname.length() < 3 || loginname.length() > 20) {
			errors.put("loginname", "用户名长度必须在3~20之间！");
		} else if (userService.validateLoginname(loginname)) {
			errors.put("loginname", "用户名已被注册过！");
		}

		// 对loginpass进行校验
		String loginpass = user.getLoginpass();
		if (loginpass == null || loginpass.isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		} else if (loginpass.length() < 3 || loginpass.length() > 20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}

		// 对确认密码进行校验
		String reloginpass = user.getReloginpass();
		if (reloginpass == null || reloginpass.isEmpty()) {
			errors.put("reloginpass", "确认密码不能为空！");
		} else if (!reloginpass.equalsIgnoreCase(loginpass)) {
			errors.put("reloginpass", "两次输入密码不一致！");
		}

		// 对Email进行校验
		String email = user.getEmail();
		if (email == null || email.isEmpty()) {
			errors.put("email", "Email不能为空！");
		} else if (!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
			errors.put("email", "错误的Email格式！");
		} else if (userService.validateEmail(email)) {
			errors.put("email", "Email已被注册过！");
		}

		// 对验证码进行校验
		String verifyCode = user.getVerifyCode();
		String vCode = (String) req.getSession().getAttribute("vCode");
		if (verifyCode == null || verifyCode.isEmpty()) {
			errors.put("verifyCode", "验证码不能为空！");
		} else if (verifyCode.length() != 4) {
			errors.put("verifyCode", "错误的验证码！");
		} else if (!verifyCode.equalsIgnoreCase(vCode)) {
			errors.put("verifyCode", "错误的验证码！");
		}
		return errors;
	}
}
