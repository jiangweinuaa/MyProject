package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 商品流水记录查询
 * @author yuanyy 
 *	
 */
public class DCP_PluSaleFlowQueryReq extends JsonBasicReq {
	private level1Elm request;
//	private String timestamp;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
//	public String getTimestamp() {
//		return timestamp;
//	}
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}

	public class level1Elm
	{
		private String pluNo;
		private String guQingNo;
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getGuQingNo() {
			return guQingNo;
		}
		public void setGuQingNo(String guQingNo) {
			this.guQingNo = guQingNo;
		}
		
	}
	
	
	
	
	
}
