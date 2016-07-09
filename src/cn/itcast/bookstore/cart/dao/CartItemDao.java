package cn.itcast.bookstore.cart.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

/**
 * 购物车持久层
 * @author qdmmy6
 *
 */
public class CartItemDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/*
	 * 把Map映射成CartItem
	 */
	private CartItem toBean(Map<String,Object> map) {
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User owner = CommonUtils.toBean(map, User.class);
		cartItem.setBook(book);
		cartItem.setOwner(owner);
		return cartItem;
	}
	
	/*
	 * 把List<Map>映射成List<CartItem>
	 */
	private List<CartItem> toBeanList(List<Map<String,Object>> mapList) {
		List<CartItem> cartItemList = new ArrayList<CartItem>();
		for(Map<String,Object> map : mapList) {
			cartItemList.add(toBean(map));
		}
		return cartItemList;
	}
	
	/**
	 * 按uid查询
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public List<CartItem> findByUser(String uid) throws SQLException {
		// 不只是查询购物车条目，还要查询条目关联的图书
		String sql = "select * from t_cartItem ci, t_book b where ci.bid=b.bid and uid=? order by ci.orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), uid);
		return toBeanList(mapList);
	}

	/**
	 * 添加购物车条目
	 * @param cartItem
	 * @throws SQLException
	 */
	public void add(CartItem cartItem) throws SQLException {
		String sql = "insert into t_cartItem(cartItemId,quantity,bid,uid) values(?,?,?,?)";
		Object[] params = {cartItem.getCartItemId(), cartItem.getQuantity(),
				cartItem.getBook().getBid(), cartItem.getOwner().getUid()};
		qr.update(sql, params);
	}
	
	/**
	 * 查询当前用户指定bid的购物车条目是否存在
	 * 在添加条目时需要使用，用来判断是否合并条目
	 * @param uid
	 * @param bid
	 * @return
	 * @throws SQLException
	 */
	public CartItem findByUserAndBook(String uid, String bid) throws SQLException {
		String sql = "select * from t_cartItem where uid=? and bid=?";
		return qr.query(sql, new BeanHandler<CartItem>(CartItem.class), uid, bid);
	}
	
	/**
	 * 修改条目的数量
	 * @param cartItemId
	 * @param quantity
	 * @throws SQLException
	 */
	public void updateQuantity(String cartItemId, int quantity) throws SQLException {
		String sql = "update t_cartItem set quantity=? where cartItemId=?";
		qr.update(sql, quantity, cartItemId);
	}
	
	/**
	 * 批量删除条目
	 * @param cartItemIds
	 * @throws SQLException 
	 */
	public void deleteBatch(String cartItemIds) throws SQLException {
		/*
		 * 1. 拼凑SQL语句
		 */
		StringBuilder sql = new StringBuilder("delete from t_cartitem where cartItemId in ");
		Object[] params = cartItemIds.split(",");//必须是Object[]类型，而不能是String[]类型！
		sql.append("(");
		for(int i = 0; i < params.length; i++) {
			sql.append("?");
			if(i < params.length - 1) {
				sql.append(",");
			}
		}
		sql.append(")");
		/*
		 * 2. 执行SQL语句
		 */
		// 如果这里的params是String[]类型，那么那么就表示可变参数的一个元素了。
		qr.update(sql.toString(), params);
	}
	
	/**
	 * 加载CartItem
	 * @param cartItemId
	 * @return
	 * @throws SQLException 
	 */
	public CartItem load(String cartItemId) throws SQLException {
		String sql = "select * from t_cartitem ci, t_book b where ci.bid=b.bid and cartItemId=?";
		return toBean(qr.query(sql, new MapHandler(), cartItemId));
	}

	/**
	 * 加载多个CartItem
	 * @param cartItemIds
	 * @return
	 * @throws SQLException 
	 */
	public List<CartItem> loadCartItems(String cartItemIds) throws SQLException {
		/*
		 * 1. 拼凑SQL语句
		 */
		StringBuilder sql = new StringBuilder("select * from t_cartitem ci, t_book b where ci.bid=b.bid and cartItemId in ");
		Object[] params = cartItemIds.split(",");//必须是Object[]类型，而不能是String[]类型！
		sql.append("(");
		for(int i = 0; i < params.length; i++) {
			sql.append("?");
			if(i < params.length - 1) {
				sql.append(",");
			}
		}
		sql.append(") order by ci.orderBy");
		/*
		 * 2. 执行SQL语句，得到List<CartItem>
		 */
		List<Map<String,Object>> map = qr.query(sql.toString(), new MapListHandler(), params);
		return toBeanList(map);
	}
}
