package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderProSchedUpdateReq extends JsonBasicReq
{
	private String type;
	private String optype;
	private List<level1Elm> datas;
	
	public  class level1Elm
	{
		private String orderNO;
		private String docType;
		private String machShop;
		private String deliveryType;
		private String isinPrice;
		
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

	public String getMachShop() {
	return machShop;
	}

	public void setMachShop(String machShop) {
	this.machShop = machShop;
	}

	public String getDeliveryType() {
	return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
	this.deliveryType = deliveryType;
	}

	public String getIsinPrice() {
	return isinPrice;
	}

	public void setIsinPrice(String isinPrice) {
	this.isinPrice = isinPrice;
	}
		
	}

	public String getOptype() {
	return optype;
	}
	public void setOptype(String optype) {
	this.optype = optype;
	}
	public String getType() {
	return type;
	}
	public void setType(String type) {
	this.type = type;
	}
	public List<level1Elm> getDatas() {
	return datas;
	}
	public void setDatas(List<level1Elm> datas) {
	this.datas = datas;
	}
	
}
