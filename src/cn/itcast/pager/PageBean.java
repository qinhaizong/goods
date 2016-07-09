package cn.itcast.pager;

import java.util.List;

/**
 * 分页Bean
 * 封装所有与分页相关的数据
 * @author qdmmy6
 *
 */
public class PageBean<T> {
	private int pc;//当前页码
	private int tr;//总记录数
	private int ps;//每页记录数
	private List<T> dataList;//当前页记录
	private String url;//请求的url
	
	/**
	 * 计算总页数
	 * @return
	 */
	public int getTp() {
		int tp = tr / ps;//总记录数/每页记录数
		return tr % ps == 0 ? tp : tp+1;//如果是整除，返回tp，否则再加1。
	}
	
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getTr() {
		return tr;
	}
	public void setTr(int tr) {
		this.tr = tr;
	}
	public int getPs() {
		return ps;
	}
	public void setPs(int ps) {
		this.ps = ps;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
