package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 订单生产接单同意/拒绝DCP_OrderProductionAgreeOrReject
 *
 */
public class DCP_OrderProductionAgreeOrReject_OpenRes extends JsonBasicRes {
	private String version; // 外部接口返回 内部接口不返回

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public levRes datas;

	public levRes getDatas() {
		return datas;
	}

	public void setDatas(levRes datas) {
		this.datas = datas;
	}
	
	public class levRes{
		private List<ErrorOrderList> errorOrderList;

		public List<ErrorOrderList> getErrorOrderList() {
			return errorOrderList;
		}

		public void setErrorOrderList(List<ErrorOrderList> errorOrderList) {
			this.errorOrderList = errorOrderList;
		}
		
	}
	
	
	public class ErrorOrderList{
		private String orderNo;
		private String errorDesc;
		public String getOrderNo() {
			return orderNo;
		}
		public String getErrorDesc() {
			return errorDesc;
		}
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		public void setErrorDesc(String errorDesc) {
			this.errorDesc = errorDesc;
		}
		
	}
	
}	
