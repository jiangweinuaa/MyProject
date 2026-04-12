package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PaymentShopQueryReq extends JsonBasicReq
{

	private String funcNO;
    private String payCode;
    private String payCodePOS;
    
	public String getFuncNO() {
		return funcNO;
	}

	public void setFuncNO(String funcNO) {
		this.funcNO = funcNO;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getPayCodePOS() {
		return payCodePOS;
	}

	public void setPayCodePOS(String payCodePOS) {
		this.payCodePOS = payCodePOS;
	}
	
	
}
