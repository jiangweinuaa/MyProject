package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_FlavorGoodsQueryReq extends JsonBasicReq
{

	private level1Elm request;	
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}


	public class level1Elm
	{
		private String flavorId;//
		private String status;//
		private String keyTxt;//
		public String getFlavorId() {
			return flavorId;
		}
		public void setFlavorId(String flavorId) {
			this.flavorId = flavorId;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt()
		{
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt)
		{
			this.keyTxt = keyTxt;
		}
		



	}
}
