package com.dsc.spos.utils;

import java.util.List;
import java.util.Map;

public class PageQueryInfo {

	private int totalPages;	//总页数
	private int totalRecords;	//总行数
	private int pageNumber;	//当前页码
	private int pageSize;		//每页行数
	private List<Map<String,Object>> datas;//SQL获取的当前页数据

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<Map<String, Object>> getDatas() {
		return datas;
	}

	public void setDatas(List<Map<String, Object>> datas) {
		this.datas = datas;
	}
	
	
	

}
