package com.dsc.spos.utils.websocket;

import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class OnLineUsers {
	
	private String cliendId;
	private String Company;
	private String Shop;
	private String TableNo;
	private String SaleNo;
	private String SaleStatus; //这个字段服务自定义， 如果有人结账了， 当前桌台在线客户全部改为 OVER
	private String isPayer; //是否付款人， 不接受掉其他人点菜的信息

	// 2021/5/13 wangzyc  根据点餐类型 后结场景增加单号的接收与限制条件
	private String ScanType;	// 点餐类型 用于区分是先结还是后结
	
	private Map<String, WebSocketSession> users;
	private JSONArray shoppingCart;

	public String getScanType() {
		return ScanType;
	}

	public void setScanType(String scanType) {
		ScanType = scanType;
	}

	public Map<String, WebSocketSession> getUsers() {
		return users;
	}
	public void setUsers(Map<String, WebSocketSession> users) {
		this.users = users;
	}
	public String getCliendId() {
		return cliendId;
	}
	public void setCliendId(String cliendId) {
		this.cliendId = cliendId;
	}
	public String getCompany() {
		return Company;
	}
	public String getShop() {
		return Shop;
	}
	public String getTableNo() {
		return TableNo;
	}
	public String getSaleNo() {
		return SaleNo;
	}
	public void setCompany(String company) {
		Company = company;
	}
	public void setShop(String shop) {
		Shop = shop;
	}
	public void setTableNo(String tableNo) {
		TableNo = tableNo;
	}
	public void setSaleNo(String saleNo) {
		SaleNo = saleNo;
	}
	public JSONArray getShoppingCart() {
		return shoppingCart;
	}
	public void setShoppingCart(JSONArray shoppingCart) {
		this.shoppingCart = shoppingCart;
	}
	public String getSaleStatus() {
		return SaleStatus;
	}
	public void setSaleStatus(String saleStatus) {
		SaleStatus = saleStatus;
	}
	public String getIsPayer() {
		return isPayer;
	}
	public void setIsPayer(String isPayer) {
		this.isPayer = isPayer;
	}

	
}
