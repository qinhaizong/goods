package cn.itcast.bookstore.admin.book.web.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;

public class AdminAddBookServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload sfu = new ServletFileUpload(factory);
		try {
			List<FileItem> itemList = sfu.parseRequest(request);
			Book book = new Book();//创建图书对象
			book.setBid(CommonUtils.uuid());//设置bid
			book.setBname(itemList.get(0).getString("utf-8"));//设置书名
			
			FileItem image_w = itemList.get(1);//获取大图字段
			String image_w_name = image_w.getName();//获取上传大图的路径
			//截取路径中文件名称
			int index = image_w_name.lastIndexOf("\\");
			if(index > 0) {
				image_w_name = image_w_name.substring(index+1);
			}
			// 校验文件扩展名
			if(!image_w_name.toLowerCase().endsWith("jpg") &&
					!image_w_name.toLowerCase().endsWith("png") &&
					!image_w_name.toLowerCase().endsWith("bmp")) {
				CategoryService categoryService = new CategoryService();
				request.setAttribute("parents", categoryService.findParents());
				request.setAttribute("msg", "图片扩展名只能是：jpg、png、bmp！");
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
					.forward(request, response);
				return;
			}
			// 保存图片
			String bookSavePath = this.getServletContext().getRealPath("/book_img");
			image_w_name = CommonUtils.uuid() + "_" + image_w_name;
			File destFile = new File(bookSavePath, image_w_name);
			image_w.write(destFile);
			book.setImage_w("book_img/" + image_w_name);
			
			FileItem image_b = itemList.get(2);
			String image_b_name = image_b.getName();
			//截取路径中文件名称
			index = image_b_name.lastIndexOf("\\");
			if(index > 0) {
				image_b_name = image_b_name.substring(index+1);
			}
			// 校验文件扩展名
			if(!image_b_name.toLowerCase().endsWith("jpg") &&
					!image_b_name.toLowerCase().endsWith("png") &&
					!image_b_name.toLowerCase().endsWith("bmp")) {
				CategoryService categoryService = new CategoryService();
				request.setAttribute("parents", categoryService.findParents());
				request.setAttribute("msg", "图片扩展名只能是：jpg、png、bmp！");
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
					.forward(request, response);
				return;
			}
			
			// 保存图片
			image_b_name = CommonUtils.uuid() + "_" + image_b_name;
			destFile = new File(bookSavePath, image_b_name);
			image_b.write(destFile);
			book.setImage_b("book_img/" + image_b_name);
			
			book.setCurrPrice(Double.parseDouble(itemList.get(3).getString()));//设置当前价
			book.setPrice(Double.parseDouble(itemList.get(4).getString()));//设置定价
			book.setDiscount(Double.parseDouble(itemList.get(5).getString()));//设置折扣
			book.setAuthor(itemList.get(6).getString("utf-8"));//设置作者
			book.setPress(itemList.get(7).getString("utf-8"));//设置出版社
			book.setPublishtime(itemList.get(8).getString("utf-8"));//设置出版时间
			book.setEdition(itemList.get(9).getString("utf-8"));//设置版次
			book.setPageNum(Integer.parseInt(itemList.get(10).getString("utf-8")));//设置页数
			book.setWordNum(Integer.parseInt(itemList.get(11).getString("utf-8")));//设置字数
			book.setPrinttime(itemList.get(12).getString("utf-8"));//设置印刷时间
			book.setBooksize(Integer.parseInt(itemList.get(13).getString("utf-8")));//设置开本
			book.setPaper(itemList.get(14).getString("utf-8"));//设置纸质
			
			// 设置2级分类
			Category category = new Category();
			category.setCid(itemList.get(16).getString());
			book.setCategory(category);
			
			// 添加图书
			BookService bookService = new BookService();
			bookService.add(book);
			
			request.setAttribute("msg", "添加图书成功");
			request.getRequestDispatcher("/adminjsps/msg.jsp").forward(request, response);
		} catch (Exception e) {
			CategoryService categoryService = new CategoryService();
			request.setAttribute("parents", categoryService.findParents());

			if(e instanceof FileUploadBase.FileSizeLimitExceededException) {
				request.setAttribute("msg", "单个图片大小超出限制！");
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
					.forward(request, response);
				return;
			}
			if(e instanceof FileUploadBase.SizeLimitExceededException) {
				request.setAttribute("msg", "整个请求大小超出限制！");
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
					.forward(request, response);
				return;
			}			
		}
	}
}
