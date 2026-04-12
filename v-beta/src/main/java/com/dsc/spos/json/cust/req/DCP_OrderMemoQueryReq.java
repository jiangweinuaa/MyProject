package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderMemoQueryReq extends JsonBasicReq {

	private String oEId ;
	private String oShopId;
	private String[] docType;
	private String orderNO;
	private String item;
	private String pluNO;

	public String getoEId() {
		return oEId;
	}
	public void setoEId(String oEId) {
		this.oEId = oEId;
	}
	public String getoShopId() {
		return oShopId;
	}
	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}
	public String[] getDocType() {
		return docType;
	}
	public void setDocType(String[] docType) {
		this.docType = docType;
	}
	public String getOrderNO() {
		return orderNO;
	}
	public void setOrderNO(String orderNO) {
		this.orderNO = orderNO;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getPluNO() {
		return pluNO;
	}
	public void setPluNO(String pluNO) {
		this.pluNO = pluNO;
	}

}
