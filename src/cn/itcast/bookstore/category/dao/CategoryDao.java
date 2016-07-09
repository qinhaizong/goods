package cn.itcast.bookstore.category.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 返回所有分类
	 * @return
	 * @throws SQLException
	 */
	public List<Category> findAll() throws SQLException {
		/*
		 * 1. 获取所有一级分类
		 *   pid为null就是一级分类。
		 */
		String sql = "select * from t_category where pid is null order by orderBy";
		List<Category> parents = qr.query(sql, 
				new BeanListHandler<Category>(Category.class));
		
		/*
		 * 2. 循环遍历每个一级分类，为其加载它的所有二级分类
		 */
		sql = "select * from t_category where pid=? order by orderBy";
		for(Category parent : parents) {
			// 获取当前一级分类的所有二级分类
			List<Category> children = qr.query(sql, 
					new BeanListHandler<Category>(Category.class), 
					parent.getCid());
			// 给当前一级分类设置二级分类
			parent.setChildren(children);
			// 为每个二级分类设置一级分类
			for(Category child : children) {
				child.setParent(parent);
			}
		}
		/*
		 * 3. 返回一级分类List，每个一级分类都包含了自己的二级分类
		 */
		return parents;
	}

	/**
	 * 添加一级分类
	 * @param category
	 * @throws SQLException 
	 */
	public void add(Category category) throws SQLException {
		String sql = "insert into t_category(cid, cname, pid, `desc`) values(?,?,?,?)";
		String pid = null;
		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}
		qr.update(sql, category.getCid(), category.getCname(), 
				pid, category.getDesc());
	}
	
	/**
	 * 获取所有一级分类
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findParents() throws SQLException {
		String sql = "select * from t_category where pid is null order by orderBy";
		return qr.query(sql, new BeanListHandler<Category>(Category.class));
	}

	/**
	 * 加载分类
	 * @param cid
	 * @return
	 * @throws SQLException 
	 */
	public Category load(String cid) throws SQLException {
		String sql = "select * from t_category where cid=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(), cid);
		Category category = CommonUtils.toBean(map, Category.class);
		Object pid = map.get("pid");
		if(pid != null) {
			Category parent = new Category();
			parent.setCid((String)pid);
			category.setParent(parent);
		}
		return category;
	}

	/**
	 * 修改分类
	 * @param category
	 * @throws SQLException 
	 */
	public void edit(Category category) throws SQLException {
		String sql = "update t_category set cname=?,pid=?,`desc`=? where cid=?";
		String pid = null;
		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCname(), pid,
				category.getDesc(), category.getCid()};
		qr.update(sql, params);
	}
	
	/**
	 * 查看某1级分类下子分类个数
	 * @param cid
	 * @return
	 * @throws SQLException
	 */
	public int findChildrenCount(String cid) throws SQLException {
		String sql = "select count(1) from t_category where pid=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), cid);
		return cnt == null ? 0 : cnt.intValue();
	}
	
	/**
	 * 删除指定分类
	 * @param cid
	 * @throws SQLException
	 */
	public void delete(String cid) throws SQLException {
		String sql = "delete from t_category where cid=?";
		qr.update(sql, cid);
	}

	/**
	 * 加载指定父分类下所有子分类
	 * @param pid
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findChildren(String pid) throws SQLException {
		String sql = "select * from t_category where pid=? order by orderBy";
		return qr.query(sql, new BeanListHandler<Category>(Category.class), pid);
	}
}
