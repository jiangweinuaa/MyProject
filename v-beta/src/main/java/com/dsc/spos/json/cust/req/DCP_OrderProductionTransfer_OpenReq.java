package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 订单生产内部调拨
 *
 */
public class DCP_OrderProductionTransfer_OpenReq extends JsonBasicReq {

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
		private String packerId;
		private String packerName;
		private String packerTelephone;
		private String delId;
		private String delName;
		private String delTelephone;
		
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

		public String getPackerId() {
			return packerId;
		}

		public void setPackerId(String packerId) {
			this.packerId = packerId;
		}

		public String getPackerName() {
			return packerName;
		}

		public void setPackerName(String packerName) {
			this.packerName = packerName;
		}

		public String getPackerTelephone() {
			return packerTelephone;
		}

		public void setPackerTelephone(String packerTelephone) {
			this.packerTelephone = packerTelephone;
		}

		public String getDelId() {
			return delId;
		}

		public void setDelId(String delId) {
			this.delId = delId;
		}

		public String getDelName() {
			return delName;
		}

		public void setDelName(String delName) {
			this.delName = delName;
		}

		public String getDelTelephone() {
			return delTelephone;
		}

		public void setDelTelephone(String delTelephone) {
			this.delTelephone = delTelephone;
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
