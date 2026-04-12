package com.dsc.spos.ninetyone.response;

import java.util.List;

/**
 * 25. 訂單清單查詢
 * /V2/SalesOrder/GetList
 * /V1/Order/GetOrderList
 */
public class NOSalesOrderListReq extends NOBasicReq {

	private SalesOrderListData Data;
	
	public SalesOrderListData getData() {
		return Data;
	}

	public void setData(SalesOrderListData data) {
		Data = data;
	}

	public static class SalesOrderListData {
		private int TotalCount;//本次查询结果总笔数
		private List<SalesOrderCode> List;
		public int getTotalCount() {
			return TotalCount;
		}
		public void setTotalCount(int totalCount) {
			TotalCount = totalCount;
		}
		public List<SalesOrderCode> getList() {
			return List;
		}
		public void setList(List<SalesOrderCode> list) {
			List = list;
		}
		
	}
	
	public static class SalesOrderCode {
		private String TGCode;//购物车编号  V1 不回覆此栏位
		private String TMCode;//主单编号
		//订单编号
		//备注 : 每次最多只会列出 100 笔的订单编号
		private String TSCode;
		public String getTGCode() {
			return TGCode;
		}
		public void setTGCode(String tGCode) {
			TGCode = tGCode;
		}
		public String getTMCode() {
			return TMCode;
		}
		public void setTMCode(String tMCode) {
			TMCode = tMCode;
		}
		public String getTSCode() {
			return TSCode;
		}
		public void setTSCode(String tSCode) {
			TSCode = tSCode;
		}
		
	}

}
