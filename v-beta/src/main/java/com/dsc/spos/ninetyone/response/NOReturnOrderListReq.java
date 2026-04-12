package com.dsc.spos.ninetyone.response;

import java.util.List;

/**
 * 27. 退貨清單查詢
 * /V2/ReturnGoodsOrder/GetList
 * /V1/Order/GetReturnGoodList
 */
public class NOReturnOrderListReq extends NOBasicReq {

	private ReturnOrderListData Data;
	

	public ReturnOrderListData getData() {
		return Data;
	}

	public void setData(ReturnOrderListData data) {
		Data = data;
	}

	public static class ReturnOrderListData {
		private int TotalCount;//本次查询结果总笔数
		private List<ReturnOrderCode> List;
		public int getTotalCount() {
			return TotalCount;
		}
		public void setTotalCount(int totalCount) {
			TotalCount = totalCount;
		}
		public List<ReturnOrderCode> getList() {
			return List;
		}
		public void setList(List<ReturnOrderCode> list) {
			List = list;
		}
		
	}
	
	public static class ReturnOrderCode {
		//退货单序号
		//备注 : 每次最多只会列出 100 笔的退货单序号
		private String ReturnGoodDetailId;//long
		private String TSCode;//来源订单编号 string(20)
		public String getReturnGoodDetailId() {
			return ReturnGoodDetailId;
		}
		public void setReturnGoodDetailId(String returnGoodDetailId) {
			ReturnGoodDetailId = returnGoodDetailId;
		}
		public String getTSCode() {
			return TSCode;
		}
		public void setTSCode(String tSCode) {
			TSCode = tSCode;
		}
	}

}
