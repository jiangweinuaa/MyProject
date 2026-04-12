package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 平台支付方式映射查询
 * @author yuanyy 2019-04-24
 *
 */
public class DCP_PayMappingQueryReq extends JsonBasicReq {
	
	private levelRequest request;

	public levelRequest getRequest()
	{
		return request;
	}

	public void setRequest(levelRequest request)
	{
		this.request = request;
	}

	public class levelRequest
	{
		private String channelType;
		private String channelId;
		private String keyTxt;

		public String getChannelType() {
			return channelType;
		}

		public void setChannelType(String channelType) {
			this.channelType = channelType;
		}

		public String getChannelId() {
			return channelId;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public String getKeyTxt() {
			return keyTxt;
		}

		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
	}
	
	
	
	
}
