package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsMappingQueryReq  extends JsonBasicReq 
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String platformType;
		private String keyTxt;
		
		public String getPlatformType() {
			return platformType;
		}

		public void setPlatformType(String platformType) {
			this.platformType = platformType;
		}

		public String getKeyTxt() {
			return keyTxt;
		}

		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
	}




}
