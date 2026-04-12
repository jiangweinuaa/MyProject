package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DCP_TicketStyleDetailQuery
 * 服务说明：企业小票详情查询
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketStyleDetailQueryReq extends JsonBasicReq{
	
	private level1Elm request; 
	
	
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String styleId; //小票样式模板

		public String getStyleId() {
			return styleId;
		}

		public void setStyleId(String styleId) {
			this.styleId = styleId;
		}
		
	}

}
