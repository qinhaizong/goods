package cn.itcast.bookstore.admin.book.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.pager.PageBean;
import cn.itcast.servlet.BaseServlet;

public class AdminBookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();

	/*
	 * 获取当前页码
	 */
	private int getPageCode(HttpServletRequest req) {
		String pageCode = req.getParameter("pc");
		if (pageCode == null)
			return 1;
		try {
			return Integer.parseInt(pageCode);
		} catch (RuntimeException e) {
			return 1;
		}
	}

	/*
	 * 获取请求的url，但去除pc参数
	 */
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		int fromIndex = url.lastIndexOf("&pc=");
		if (fromIndex == -1)
			return url;
		int toIndex = url.indexOf("&", fromIndex + 1);
		if (toIndex == -1)
			return url.substring(0, fromIndex);
		return url.substring(0, fromIndex) + url.substring(toIndex);
	}

	/**
	 * 按分类查询图书（分页）
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByCategory(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 获取当前页码
		 */
		int pc = getPageCode(request);
		/*
		 * 2. 使用BookService查询，得到PageBean
		 */
		String cid = request.getParameter("cid");
		PageBean<Book> pb = bookService.findByCategory(cid, pc);
		/*
		 * 3. 获取url，设置给PageBean
		 */
		String url = getUrl(request);
		pb.setUrl(url);
		/*
		 * 4. 把PageBean保存到request，转发到/jsps/book/list.jsp
		 */
		request.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 按作者查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByAuthor(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 获取当前页码
		 */
		int pc = getPageCode(request);
		/*
		 * 2. 使用BookService查询，得到PageBean
		 */
		String author = request.getParameter("author");
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
		/*
		 * 3. 获取url，设置给PageBean
		 */
		String url = getUrl(request);
		pb.setUrl(url);
		/*
		 * 4. 把PageBean保存到request，转发到/jsps/book/list.jsp
		 */
		request.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 按出版社查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByPress(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 获取当前页码
		 */
		int pc = getPageCode(request);
		/*
		 * 2. 使用BookService查询，得到PageBean
		 */
		String press = request.getParameter("press");
		PageBean<Book> pb = bookService.findByPress(press, pc);
		/*
		 * 3. 获取url，设置给PageBean
		 */
		String url = getUrl(request);
		pb.setUrl(url);
		/*
		 * 4. 把PageBean保存到request，转发到/jsps/book/list.jsp
		 */
		request.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 多条件组合查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByCombination(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 获取当前页码
		 */
		int pc = getPageCode(request);
		/*
		 * 2. 使用BookService查询，得到PageBean
		 */
		Book book = CommonUtils.toBean(request.getParameterMap(), Book.class);
		PageBean<Book> pb = bookService.findByCombination(book, pc);
		/*
		 * 3. 获取url，设置给PageBean
		 */
		String url = getUrl(request);
		pb.setUrl(url);
		/*
		 * 4. 把PageBean保存到request，转发到/jsps/book/list.jsp
		 */
		request.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 加载图书
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse reps)
			throws ServletException, IOException {
		/*
		 * 1. 获取bid,通过bid加载book，保存到request中
		 */
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		/*
		 * 2. 获取所有1级分类，保存到request中
		 */
		req.setAttribute("parents", categoryService.findParents());
		/*
		 * 3. 获取当前图书所属1级分类下的所有2级分类
		 */
		// 获取当前图书所属1级分类cid
		String pid = book.getCategory().getParent().getCid();
		req.setAttribute("children", categoryService.findChildren(pid));
		return "f:/adminjsps/admin/book/desc.jsp";
	}
	
	/**
	 * 编辑图书
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String edit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Book book = CommonUtils.toBean(req.getParameterMap(), Book.class);
		Category category = CommonUtils.toBean(req.getParameterMap(), Category.class);
		Category parent = new Category();
		parent.setCid(req.getParameter("pid"));
		category.setParent(parent);
		book.setCategory(category);
		
		bookService.edit(book);
		
		req.setAttribute("msg", "编辑图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
	
	/**
	 * 删除图书
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String bid = req.getParameter("bid");
		bookService.delete(bid);
		req.setAttribute("msg", "删除图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
	
	/**
	 * 添加图书：第一步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute("parents", categoryService.findParents());
		return "f:/adminjsps/admin/book/add.jsp";
	}
}
