package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：MachineGetDCP
 * 服务说明：机台信息查询DCP
 * @author jzma 
 * @since  2018-11-01
 */
public class DCP_MachineQueryReq extends JsonBasicReq {
	
	private levelReq request;
	
	public levelReq getRequest() {
		return request;
	}
	public void setRequest(levelReq request) {
		this.request = request;
	}
	
	public class levelReq{
		
		private String keyTxt ;
		private String shopId ;
		private String regflg ;
		
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public String getRegflg() {
			return regflg;
		}
		public void setRegflg(String regflg) {
			this.regflg = regflg;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

	}
	
}
