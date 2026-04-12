package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：MainInfoGetDCP
 * 服务说明：首页信息查询
 * @author jinzma 
 * @since  2019-05-20
 */
public class DCP_MainInfoQueryReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{

		private String[] getType ;

		public String[] getGetType() {
			return getType;
		}

		public void setGetType(String[] getType) {
			this.getType = getType;
		}
	}


}
