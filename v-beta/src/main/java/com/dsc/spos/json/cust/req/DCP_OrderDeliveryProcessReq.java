package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderDeliveryProcessReq extends JsonBasicReq {

	private String oEId;//企业编号
	private String o_opNO;//操作员编号
	private String o_opName;//操作员名称
	private String deliveryType;//物流类型        20-点我达
	private String opType;//操作类型 0-快递下单、1-快递撤销
	private String IsAfterSale;//是否售后处理
	private List<level1Elm> datas;


	public String getoEId() {
		return oEId;
	}
	public void setoEId(String oEId) {
		this.oEId = oEId;
	}
	public String getO_opNO() {
		return o_opNO;
	}
	public void setO_opNO(String o_opNO) {
		this.o_opNO = o_opNO;
	}	
	public String getO_opName() {
		return o_opName;
	}
	public void setO_opName(String o_opName) {
		this.o_opName = o_opName;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getOpType() {
		return opType;
	}
	public void setOpType(String opType) {
		this.opType = opType;
	}

	public String getIsAfterSale() {
		return IsAfterSale;
	}
	public void setIsAfterSale(String isAfterSale) {
		IsAfterSale = isAfterSale;
	}
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public  class level1Elm
	{
		private String orderNO;//订单号
		private String docType;//订单来源类型
		private String reason;//取消时必传
		public String getOrderNO() {
			return orderNO;
		}
		public void setOrderNO(String orderNO) {
			this.orderNO = orderNO;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}

	}



}
