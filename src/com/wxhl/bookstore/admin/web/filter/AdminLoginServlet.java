package com.wxhl.bookstore.admin.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.wxhl.bookstore.admin.admin.domain.Admin;

public class AdminLoginServlet implements Filter {
    @Override
	public void destroy() {
	}

    @Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		Admin admin = (Admin)httpReq.getSession().getAttribute("admin");
		if(admin == null) {
			request.setAttribute("msg", "您还没有登录！");
			httpReq.getRequestDispatcher("/adminjsps/login.jsp").forward(request, response);
		} else {
			chain.doFilter(request, response);
		}
	}

    
    @Override
	public void init(FilterConfig fConfig) throws ServletException {

	}
}
