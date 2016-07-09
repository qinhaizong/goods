package cn.itcast.bookstore.cart.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.bookstore.cart.dao.CartItemDao;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.jdbc.JdbcUtils;

/**
 * 购物车业务类
 * @author qdmmy6
 *
 */
public class CartItemService {
	private CartItemDao cartItemDao = new CartItemDao();
	
	/**
	 * 我的购物车
	 * @param uid
	 * @return
	 */
	public List<CartItem> myCart(String uid) {
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加购物车条目
	 * @param cartItem
	 */
	public void add(CartItem cartItem) {
		try {
			/*
			 * 查询这个条目是否已经存在，如果存在，那么就合并条目，而不是添加条目
			 */
			JdbcUtils.beginTransaction();
			CartItem _cartItem = cartItemDao.findByUserAndBook(
					cartItem.getOwner().getUid(), cartItem.getBook().getBid());
			if(_cartItem == null) {//如果原来不存在这一条目，那么添加条目
				cartItemDao.add(cartItem);
			} else {//如果原来存在这一条目，那么把原条目和新条目的数量合并，然后修改条目数量
				int quantity = cartItem.getQuantity() + _cartItem.getQuantity();
				cartItemDao.updateQuantity(_cartItem.getCartItemId(), quantity);
			}
			JdbcUtils.commitTransaction();
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 批量删除条目
	 * @param cartItemIds
	 */
	public void deleteBatch(String cartItemIds) {
		try {
			cartItemDao.deleteBatch(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改条目数量
	 * @param cartItemId
	 * @param quantity
	 */
	public void updateQuantity(String cartItemId, int quantity) {
		try {
			cartItemDao.updateQuantity(cartItemId, quantity);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 加载CartItem
	 * @param cartItemId
	 * @return
	 */
	public CartItem load(String cartItemId) {
		try {
			return cartItemDao.load(cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 加载多个条目
	 * @param cartItemIds
	 * @return
	 */
	public List<CartItem> loadCartItems(String cartItemIds) {
		try {
			return cartItemDao.loadCartItems(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}
}
