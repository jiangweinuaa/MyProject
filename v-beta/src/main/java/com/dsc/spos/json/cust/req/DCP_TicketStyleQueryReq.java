package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DCP_TicketStyleDefQuery
 * 服务说明：企业小票类型查询
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketStyleQueryReq extends JsonBasicReq{
private level1Elm request;
	
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private String rangeType; // 机构类型：RETAIL-云中台 SHOP-门店管理 POS-POS机
		private String ticketType; // 小票类型编码
		private String keyTxt; // 匹配样式编码或名称
		public String getRangeType() {
			return rangeType;
		}
		public void setRangeType(String rangeType) {
			this.rangeType = rangeType;
		}
		public String getTicketType() {
			return ticketType;
		}
		public void setTicketType(String ticketType) {
			this.ticketType = ticketType;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		
		
	}
}
