package cn.itcast.bookstore.cart.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.bookstore.cart.service.CartItemService;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

/**
 * 购物车Web层
 * @author qdmmy6
 *
 */
public class CartItemServlet extends BaseServlet {
	private CartItemService cartItemService = new CartItemService();
	
	/**
	 * 我的购物车
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String myCart(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取session中的user，并购物user的uid
		 */
		User user = (User)req.getSession().getAttribute("sessionUser");
		String uid = user.getUid();
		/*
		 * 2. 调用service的myCart(String uid)方法获取当前用户的所有购物车条目 
		 */
		List<CartItem> cartItemList = cartItemService.myCart(uid);
		/*
		 * 3. 保存到request中，转发到/jsps/cart/list.jsp
		 */
		req.setAttribute("cartItemList", cartItemList);
		return "/jsps/cart/list.jsp";
	}
	
	/**
	 * 添加购物车条目
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单中的bid到Book对象中 
		 */
		Book book = CommonUtils.toBean(req.getParameterMap(), Book.class);
		/*
		 * 2. 从session中获取当前用户
		 */
		User owner = (User)req.getSession().getAttribute("sessionUser");
		/*
		 * 3. 把表单中的quantity封装到CartItem中
		 */
		CartItem cartItem = CommonUtils.toBean(req.getParameterMap(), CartItem.class);
		/*
		 * 4. 设置CartItem的id
		 *    设置book
		 *    设置owner
		 */
		cartItem.setCartItemId(CommonUtils.uuid());
		cartItem.setBook(book);
		cartItem.setOwner(owner);
		
		/*
		 * 5. 把cartItem添加到数据库
		 */
		cartItemService.add(cartItem);
		/*
		 * 6. 调用myCart()，即查询所有条目，保存到request中，转发到/jsps/cart/list.jsp
		 */
		return myCart(req, resp);
	}
	
	/**
	 * 批量删除购物车条目
	 * 也能删除一个条目
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cartItemIds = req.getParameter("cartItemIds");
		cartItemService.deleteBatch(cartItemIds);
		return myCart(req, resp);
	}
	
	/**
	 * 修改条目数量
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updateQuantity(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取cartItemId和quantity
		 */
		String cartItemId = req.getParameter("cartItemId");
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		/*
		 * 2. 调用cartItemService#updateQuantity()方法修改数量
		 */
		cartItemService.updateQuantity(cartItemId, quantity);
		/*
		 * 3. 通过cartItemId加载CartItem对象
		 */
		CartItem cartItem = cartItemService.load(cartItemId);
		/*
		 * 4. 把cartItem对象的quantity和subtotal封装成json对象返回
		 */
		String result = "{\"quantity\":" + quantity + ", \"subtotal\":" + cartItem.getSubtotal() + "}";
		resp.getWriter().print(result);
		return null;
	}
	
	/**
	 * 加载多个条目
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cartItemIds = req.getParameter("cartItemIds");
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("cartItemIds", cartItemIds);
		return "/jsps/cart/showitem.jsp";
	}
}
