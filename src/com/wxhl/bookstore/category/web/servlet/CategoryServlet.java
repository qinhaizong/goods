package com.wxhl.bookstore.category.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wxhl.bookstore.category.domain.Category;
import com.wxhl.bookstore.category.service.CategoryService;
import com.wxhl.tools.servlet.BaseServlet;

public class CategoryServlet extends BaseServlet {
	private final CategoryService categoryService = new CategoryService();
	
	/**
	 * 返回所有分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取所有分类
		 */
		List<Category> parents = categoryService.findAll();
		/*
		 * 2. 保存到request中
		 */
		req.setAttribute("parents", parents);
		/*
		 * 3. 转发到left.jsp
		 */
		return "f:/jsps/left.jsp";
	}
}
