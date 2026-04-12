package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderPlatformMappingGoodsUpdateReq  extends JsonBasicReq  {

	private String docType;
	private String erpShopNO;
	private String orderPluNO;
	private String orderSpecNO;
	private String newPluNO;	
	private String newPluName;
	private String newSpecNO;
	private String newSpecName;
	
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getErpShopNO() {
		return erpShopNO;
	}
	public void setErpShopNO(String erpShopNO) {
		this.erpShopNO = erpShopNO;
	}
	public String getOrderPluNO() {
		return orderPluNO;
	}
	public void setOrderPluNO(String orderPluNO) {
		this.orderPluNO = orderPluNO;
	}
	public String getOrderSpecNO() {
		return orderSpecNO;
	}
	public void setOrderSpecNO(String orderSpecNO) {
		this.orderSpecNO = orderSpecNO;
	}
	public String getNewPluNO() {
		return newPluNO;
	}
	public void setNewPluNO(String newPluNO) {
		this.newPluNO = newPluNO;
	}
	public String getNewPluName() {
		return newPluName;
	}
	public void setNewPluName(String newPluName) {
		this.newPluName = newPluName;
	}
	public String getNewSpecNO() {
		return newSpecNO;
	}
	public void setNewSpecNO(String newSpecNO) {
		this.newSpecNO = newSpecNO;
	}
	public String getNewSpecName() {
		return newSpecName;
	}
	public void setNewSpecName(String newSpecName) {
		this.newSpecName = newSpecName;
	}




}
