package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_ReserveItemsCreateReq.level2Elm;

public class DCP_CustomerPOrderDeleteReq extends JsonBasicReq {

	private level1Elm request;
	public class level1Elm
	{			
		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
		private String eId;
		private String shopId;
	    private List<level2Elm> datas;
	    
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		
	}
	
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
	public class level2Elm
	{
		private String pOrderNo;

		public String getpOrderNo() {
			return pOrderNo;
		}

		public void setpOrderNo(String pOrderNo) {
			this.pOrderNo = pOrderNo;
		}
		
	}
}
