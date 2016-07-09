package cn.itcast.bookstore.category.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.bookstore.book.dao.BookDao;
import cn.itcast.bookstore.category.dao.CategoryDao;
import cn.itcast.bookstore.category.domain.Category;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	private BookDao bookDao = new BookDao();
	
	/**
	 * 返回所有分类
	 * @return
	 */
	public List<Category> findAll() {
		try {
			return categoryDao.findAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加分类
	 * @param category
	 */
	public void add(Category category) {
		try {
			categoryDao.add(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 查询所有一级分类
	 * @return
	 */
	public List<Category> findParents() {
		try {
			return categoryDao.findParents();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}

	/**
	 * 加载分类
	 * @param cid
	 * @return
	 */
	public Category load(String cid) {
		try {
			return categoryDao.load(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}	
	}

	/**
	 * 修改分类
	 * @param category
	 */
	public void edit(Category category) {
		try {
			categoryDao.edit(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}	
	}
	
	/**
	 * 删除指定分类
	 * @param cid
	 * @throws CategoryException 
	 */
	public void deleteOneLevel(String cid) throws CategoryException {
		try {
			int cnt = categoryDao.findChildrenCount(cid);
			if(cnt > 0) throw new CategoryException("删除1级分类失败！该分类下还存在子分类，不能删除！");
			categoryDao.delete(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}

	/**
	 * 删除2级分类
	 * @param cid
	 * @throws CategoryException
	 */
	public void deleteTwoLevel(String cid) throws CategoryException {
		try {
			int cnt = bookDao.findCountByCategory(cid);
			if(cnt > 0) throw new CategoryException("删除2级分类失败！该分类下还存在图书，不能删除！");
			categoryDao.delete(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}			
	}

	/**
	 * 加载指定父分类下所有子分类
	 * @param pid
	 * @return
	 */
	public List<Category> findChildren(String pid) {
		try {
			return categoryDao.findChildren(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}	
	}
}
