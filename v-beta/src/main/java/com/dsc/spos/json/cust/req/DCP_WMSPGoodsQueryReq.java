package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_WMSPGoodsQueryReq extends JsonBasicReq {

	private level1Elm request;
	public class level1Elm
	{		
		private String keyTxt;// null：全部,order：零售单,point：会员积分,card：储值卡
		private String status;//null：全部,valid：执行中,inValid：暂停中

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
	}
	
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

}
