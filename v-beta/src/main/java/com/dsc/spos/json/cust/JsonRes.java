package com.dsc.spos.json.cust;

import com.dsc.spos.json.JsonBasicRes;

public class JsonRes extends JsonBasicRes {

    private int totalRecords;              //总笔数
    private int totalPages;                //总页数
    private int pageNumber;                //当前页
    private int pageSize;                  //每页笔数
    //private List<Map<String, Object>> datas;  //销售数据数组
    
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
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
	
	//public List<Map<String, Object>> getDatas() {
	//	return datas;
	//}
	//public void setDatas(List<Map<String, Object>> datas) {
	//	this.datas = datas;
	//}
}
