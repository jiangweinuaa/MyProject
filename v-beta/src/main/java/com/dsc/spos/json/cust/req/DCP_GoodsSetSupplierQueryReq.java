package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsSetSupplierQueryReq extends JsonBasicReq
{

	private String keyTxt;
	private String status;
	private String rangeType;//供应商适用范围  0.全部 1.门店管理 2.ERP总部
	public String getKeyTxt() {
		return keyTxt;
	}
	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRangeType() {
		return rangeType;
	}
	public void setRangeType(String rangeType) {
		this.rangeType = rangeType;
	}
	
}
