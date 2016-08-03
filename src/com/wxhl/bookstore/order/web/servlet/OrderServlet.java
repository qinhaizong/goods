package com.wxhl.bookstore.order.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wxhl.bookstore.cart.domain.CartItem;
import com.wxhl.bookstore.cart.service.CartItemService;
import com.wxhl.bookstore.order.domain.Order;
import com.wxhl.bookstore.order.domain.OrderItem;
import com.wxhl.bookstore.order.service.OrderService;
import com.wxhl.bookstore.user.domain.User;
import com.wxhl.tools.commons.CommonUtils;
import com.wxhl.pager.PageBean;
import com.wxhl.tools.servlet.BaseServlet;

public class OrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	private CartItemService  cartItemService = new CartItemService();
	
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
	 * 生成订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String create(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取所有购物车条目id，以及收货地址
		 */
		String cartItemIds = req.getParameter("cartItemIds");
		String address = req.getParameter("address");
		/*
		 * 2. 通过CartItemService加载所有购物车条目
		 */
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		/*
		 * 3. 创建订单
		 */
		Order order = new Order();
		order.setOid(CommonUtils.uuid());//设置oid
		order.setOrdertime(String.format("%tF %<tT", new java.util.Date()));//设置下单时间为当前时间
		// 设置合计
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartItem : cartItemList) {
			total = total.add(new BigDecimal(Double.toString(cartItem.getSubtotal())));
		}
		order.setTotal(total.doubleValue());
		
		order.setStatus(1);// 设置状态，刚生成的订单为1状态，表示未付款
		order.setAddress(address);// 设置地址
		// 设置所属会员
		User owner = (User)req.getSession().getAttribute("sessionUser");
		order.setOwner(owner);
		/*
		 * 4. 通过List<CartItem>来创建List<OrderItem>，再把List<OrderItem>设置给Order
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(CartItem cartItem : cartItemList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemId(CommonUtils.uuid());//设置orderItemId
			orderItem.setQuantity(cartItem.getQuantity());//设置数量
			orderItem.setSubtotal(cartItem.getSubtotal());//设置小计
			orderItem.setBook(cartItem.getBook());//设置book
			orderItem.setOrder(order);//设置所属订单
			
			orderItemList.add(orderItem);
		}
		order.setOrderItemList(orderItemList);//把订单条目设置给订单
		/*
		 * 5. 调用orderService方法生成order
		 */
		orderService.create(order);//设置order
		// 删除购物车中用来生成订单的条目
		cartItemService.deleteBatch(cartItemIds);
		/*
		 * 6. 保存Order到request中，转发到/jsps/order/ordersucc.jsp
		 */
		req.setAttribute("order", order);
		return "f:/jsps/order/ordersucc.jsp";
	}
	
	/**
	 * 我的订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String myOrders(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取当前页码
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取当前用户uid
		 */
		User user = (User)req.getSession().getAttribute("sessionUser");
		PageBean<Order> pb = orderService.myOrders(user.getUid(), pc);
		/*
		 * 3. 获取url，设置给PageBean
		 */
		String url = getUrl(req);
		pb.setUrl(url);
		/*
		 * 4. 把PageBean保存到request，转发到/jsps/order/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/jsps/order/list.jsp";
	}
	
	/**
	 * 添加订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");//获取订单编号
		String oper = req.getParameter("oper");//获取操作，包括：查看、确认收货、取消
		req.setAttribute("order", orderService.load(oid));//加载订单并保存到request中
		req.setAttribute("oper", oper);//把操作也保存到request中，在desc.jsp中会通过oper来显示不同的按钮
		return "/jsps/order/desc.jsp";//转发到desc.jsp
	} 
	
	/**
	 * 取消订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String cancel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		orderService.updateStatus(oid, 5);//设置状态为5，表示取消状态
		req.setAttribute("code", "success");
		req.setAttribute("msg", "订单已取消！");
		return "/jsps/msg.jsp";
	}
	
	/**
	 * 确认收货
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String confirm(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		orderService.updateStatus(oid, 4);//设置状态为5，表示交易成功状态
		req.setAttribute("code", "success");
		req.setAttribute("msg", "订单结束，交易成功！");
		return "/jsps/msg.jsp";
	}
	
	/**
	 * 加载订单，转发到pay.jsp
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String prepareForPayment(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		req.setAttribute("order", orderService.load(oid));
		return "/jsps/order/pay.jsp";
	}
	
	/**
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String payment(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 准备访问易宝网关的数据
		 */
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader()
				.getResourceAsStream("payment.properties"));

		String p0_Cmd = "Buy";// 业务类型
		String p1_MerId = props.getProperty("p1_MerId");// 商户编号，从配置文件上加载
		String p2_Order = req.getParameter("oid");// 商户订单号
		String p3_Amt = "0.01";// 支付金额
		String p4_Cur = "CNY";// 交易币种
		String p5_Pid = "";// 商品名称
		String p6_Pcat = "";// 商品种类
		String p7_Pdesc = "";// 商品描述
		String p8_Url = props.getProperty("p8_Url");// 商户接收支付成功数据的地址，从配置文件上加载
		String p9_SAF = "";// 送货地址
		String pa_MP = "";// 商户扩展信息
		String pd_FrpId = req.getParameter("yh");// 支付通道编码
		String pr_NeedResponse = "1";// 应答机制
		
		// 使用密钥生成hmac，它是不可逆的
		String keyValue = props.getProperty("keyValue");// 密钥，从配置文件上加载
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);
		
		/* 
		 * 把数据保存到request中，
		 * 转发到/WEB-INF/jsp/sendpay.jsp页面，
		 * 这个页面会把数据显示在表单中，表单会自动提交到易宝网关。
		 */
		req.setAttribute("p0_Cmd", p0_Cmd);
		req.setAttribute("p1_MerId", p1_MerId);
		req.setAttribute("p2_Order", p2_Order);
		req.setAttribute("p3_Amt", p3_Amt);
		req.setAttribute("p4_Cur", p4_Cur);
		req.setAttribute("p5_Pid", p5_Pid);
		req.setAttribute("p6_Pcat", p6_Pcat);
		req.setAttribute("p7_Pdesc", p7_Pdesc);
		req.setAttribute("p8_Url", p8_Url);
		req.setAttribute("p9_SAF", p9_SAF);
		req.setAttribute("pa_MP", pa_MP);
		req.setAttribute("pd_FrpId", pd_FrpId);
		req.setAttribute("pr_NeedResponse", pr_NeedResponse);
		req.setAttribute("hmac", hmac);

		return "/WEB-INF/jsp/sendpay.jsp";
	}
	
	/**
	 * 支付成功后访问的地址
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String back(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String p1_MerId = req.getParameter("p1_MerId");//商户编号
		String r0_Cmd = req.getParameter("r0_Cmd");// 业务类型
		String r1_Code = req.getParameter("r1_Code");//支付结果
		String r2_TrxId = req.getParameter("r2_TrxId");//易宝支付交易流水号
		String r3_Amt = req.getParameter("r3_Amt");//支付金额
		String r4_Cur = req.getParameter("r4_Cur");//交易币种
		String r5_Pid = req.getParameter("r5_Pid");// 商品名称
		String r6_Order = req.getParameter("r6_Order");//商户订单号
		String r7_Uid = req.getParameter("r7_Uid");//易宝支付会员ID
		String r8_MP = req.getParameter("r8_MP");//商户扩展信息
		String r9_BType = req.getParameter("r9_BType");//交易结果返回类型
		String hmac = req.getParameter("hmac");//签名数据
		
		// 校验签名是否有效
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader()
				.getResourceAsStream("payment.properties"));
		String keyValue = props.getProperty("keyValue");// 密钥，从配置文件上加载
		
		boolean flag = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd, 
				r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, 
				r7_Uid, r8_MP, r9_BType, keyValue);
		if(flag) {
			if("1".equals(r1_Code)) {
				int status = orderService.findStatusByOid(r6_Order);
				if(status == 1) {//如果订单状态为未支付
					orderService.updateStatus(r6_Order, 2);//修改订单状态为已支付
				}
				if("1".equals(r9_BType)) {//重定向访问本方法，转发到msg.jsp
					req.setAttribute("code", "success");
					req.setAttribute("msg", "支付成功！");
				} else if("2".equals(r9_BType)){//点对点访问本方法，回写success
					resp.getWriter().print("success");
					return null;
				}
			} else {
				System.out.println("不知道因为为啥！");
				req.setAttribute("code", "error");
				req.setAttribute("msg", "支付失败，不知道因为为啥！");			
			}
		} else {
			System.out.println("无效的签名");
			req.setAttribute("code", "error");
			req.setAttribute("msg", "支付失败，无效的签名！");
		}
		return "f:/jsps/msg.jsp";
	}
}
