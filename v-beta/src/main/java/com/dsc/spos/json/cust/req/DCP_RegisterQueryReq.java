package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_RegisterQueryReq extends JsonBasicReq
{
	  //客户代码
		private String SNumber;
		//客户名称
		private String SName;
	public String getSNumber() {
		return SNumber;
	}
	public void setSNumber(String sNumber) {
		SNumber = sNumber;
	}
	public String getSName() {
		return SName;
	}
	public void setSName(String sName) {
		SName = sName;
	}
		
}
