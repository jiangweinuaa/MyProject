package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderAddShipFeeReq extends JsonBasicReq 
{
	private String docType;
	private String orderNO;
	private String graShipfee;
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getOrderNO() {
		return orderNO;
	}
	public void setOrderNO(String orderNO) {
		this.orderNO = orderNO;
	}
	public String getGraShipfee() {
		return graShipfee;
	}
	public void setGraShipfee(String graShipfee) {
		this.graShipfee = graShipfee;
	}
	
}
