package cn.itcast.bookstore.book.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.pager.PageBean;
import cn.itcast.servlet.BaseServlet;

public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	
	/*
	 * 获取当前页码
	 */
	private int getPageCode(HttpServletRequest req) {
		String pageCode = req.getParameter("pc");
		if(pageCode == null) return 1;
		try {
			return Integer.parseInt(pageCode); 
		} catch(RuntimeException e) {
			return 1;
		}
	}
	
	/*
	 * 获取请求的url，但去除pc参数
	 */
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		int fromIndex = url.lastIndexOf("&pc=");
		if(fromIndex == -1) return url;
		int toIndex = url.indexOf("&", fromIndex + 1);
		if(toIndex == -1) return url.substring(0, fromIndex);
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
		return "/jsps/book/list.jsp";
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
		return "/jsps/book/list.jsp";
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
		return "/jsps/book/list.jsp";
	}
	
	/**
	 * 按图名查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByBname(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. 获取当前页码
		 */
		int pc = getPageCode(request);
		/*
		 * 2. 使用BookService查询，得到PageBean
		 */
		String bname = request.getParameter("bname");
		PageBean<Book> pb = bookService.findByBname(bname, pc);
		/*
		 * 3. 获取url，设置给PageBean
		 */
		String url = getUrl(request);
		pb.setUrl(url);
		/*
		 * 4. 把PageBean保存到request，转发到/jsps/book/list.jsp
		 */
		request.setAttribute("pb", pb);
		return "/jsps/book/list.jsp";
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
		return "/jsps/book/list.jsp";
	}
	
	/**
	 * 加载图书
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String bid = request.getParameter("bid");
		request.setAttribute("book", bookService.load(bid));
		return "/jsps/book/desc.jsp";
	}
}
