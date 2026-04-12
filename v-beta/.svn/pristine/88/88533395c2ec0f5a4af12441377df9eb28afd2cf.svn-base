package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：OrgGet
 *    說明：选取组织查询
 * 服务说明：选取组织查询
 * @author luoln 
 * @since  2017-03-06
 */
public class DCP_OrgQueryReq extends JsonBasicReq{

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

    @Data
	public class levelElm{
		private String keyTxt;
		private String getType;
		private String status;
		private String isCorp;
		private String employeeId;//用户编码

	}	
}
