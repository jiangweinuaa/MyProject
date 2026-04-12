package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 订单生产完工入库
 *
 */
public class DCP_OrderProductionFinish_OpenReq extends JsonBasicReq {

	private levReq request;

	public levReq getRequest() {
		return request;
	}

	public void setRequest(levReq request) {
		this.request = request;
	}
	
	
	public class levReq{
		private String eId ;
		private String shopId;
		private String opNo;
		private String opName;
		private String opType;
		
		List<OrderList> orderList ;

		public String geteId() {
			return eId;
		}

		public String getShopId() {
			return shopId;
		}

		public String getOpNo() {
			return opNo;
		}

		public String getOpName() {
			return opName;
		}

		public String getOpType() {
			return opType;
		}

		public void seteId(String eId) {
			this.eId = eId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public void setOpNo(String opNo) {
			this.opNo = opNo;
		}

		public void setOpName(String opName) {
			this.opName = opName;
		}

		public void setOpType(String opType) {
			this.opType = opType;
		}

		public List<OrderList> getOrderList() {
			return orderList;
		}

		public void setOrderList(List<OrderList> orderList) {
			this.orderList = orderList;
		}

	}
	
	public class OrderList{
		private String orderNo;

		public String getOrderNo() {
			return orderNo;
		}
		
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		
	}
	
}
