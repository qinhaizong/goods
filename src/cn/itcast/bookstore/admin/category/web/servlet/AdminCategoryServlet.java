package cn.itcast.bookstore.admin.category.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryException;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminCategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();
	
	private String toJson(Category c) {
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"cid\":").append("\"").append(c.getCid()).append("\",");
		sb.append("\"cname\":").append("\"").append(c.getCname()).append("\"");
		sb.append("}");
		return sb.toString();
	}
	
	private String toJsonArray(List<Category> categoryList) {
		StringBuilder sb = new StringBuilder("[");
		int size = categoryList.size();
		for(int i = 0; i < size; i++) {
			sb.append(toJson(categoryList.get(i)));
			if(i < size - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 支付异步请求，查询指定1级分类下的所有2级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String childrenForAjax(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String pid = req.getParameter("pid");
		List<Category> children = categoryService.findChildren(pid);
		String json = toJsonArray(children);
		resp.getWriter().print(json);
		return null;
	}
	
	/**
	 * 查询所有分类（为图书管理模块）
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAllForBook(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute("parents", categoryService.findAll());
		return "/adminjsps/admin/book/left.jsp";
	}
	
	/**
	 * 查看所有分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute("cList", categoryService.findAll());
		return "/adminjsps/admin/category/list.jsp";
	}
	
	/**
	 * 添加一级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addOneLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Category category = CommonUtils.toBean(req.getParameterMap(), Category.class);
		category.setCid(CommonUtils.uuid());
		categoryService.add(category);
		return findAll(req, resp);
	}
	
	/**
	 * 添加二级分类：第一步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addTwoLevelPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String pid = req.getParameter("pid");
		req.setAttribute("parents", categoryService.findParents());
		req.setAttribute("pid", pid);
		return "f:/adminjsps/admin/category/add2.jsp";
	}
	
	/**
	 * 添加二级分类：第二步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addTwoLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		child.setCid(CommonUtils.uuid());
		Category parent = new Category();
		parent.setCid(req.getParameter("pid"));
		child.setParent(parent);
		categoryService.add(child);
		return findAll(req, resp);
	}
	
	/**
	 * 修改一级分类：第一步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editOneLevelPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cid = req.getParameter("cid");
		req.setAttribute("category", categoryService.load(cid));
		return "f:/adminjsps/admin/category/edit.jsp";
	}
	
	/**
	 * 修改一级分类：第二步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editOneLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Category category = CommonUtils.toBean(req.getParameterMap(), Category.class);
		categoryService.edit(category);
		return findAll(req, resp);
	}
	
	/**
	 * 修改二级分类：第一步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editTwoLevelPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cid = req.getParameter("cid");
		req.setAttribute("child", categoryService.load(cid));
		req.setAttribute("parents", categoryService.findParents());
		return "/adminjsps/admin/category/edit2.jsp";
	}
	
	/**
	 * 修改二级分类：第二步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editTwoLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		String pid = req.getParameter("pid");
		Category parent = new Category();
		parent.setCid(pid);
		child.setParent(parent);
		
		categoryService.edit(child);
		return findAll(req, resp);
	}

	/**
	 * 删除一级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deleteOneLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cid = req.getParameter("cid");
		try {
			categoryService.deleteOneLevel(cid);
		} catch (CategoryException e) {
			req.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
		return findAll(req, resp);
	}
	
	/**
	 * 删除二级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deleteTwoLevel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cid = req.getParameter("cid");
		try {
			categoryService.deleteTwoLevel(cid);
		} catch (CategoryException e) {
			req.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
		return findAll(req, resp);
	}
}
