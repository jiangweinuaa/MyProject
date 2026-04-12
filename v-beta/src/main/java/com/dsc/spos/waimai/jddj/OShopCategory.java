package com.dsc.spos.waimai.jddj;

public class OShopCategory {
	private long id;
	private long pid;
	private String shopCategoryName;
	private int shopCategoryLevel;
	private long sort;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public String getShopCategoryName() {
		return shopCategoryName;
	}
	public void setShopCategoryName(String shopCategoryName) {
		this.shopCategoryName = shopCategoryName;
	}
	public int getShopCategoryLevel() {
		return shopCategoryLevel;
	}
	public void setShopCategoryLevel(int shopCategoryLevel) {
		this.shopCategoryLevel = shopCategoryLevel;
	}
	public long getSort() {
		return sort;
	}
	public void setSort(long sort) {
		this.sort = sort;
	}
	
}
