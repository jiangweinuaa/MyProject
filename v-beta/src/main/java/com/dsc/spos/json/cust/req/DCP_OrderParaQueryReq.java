package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 门店订单参数查询
 * 
 * @author yuanyy 2019-11-22
 *
 */
public class DCP_OrderParaQueryReq extends JsonBasicReq 
{

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
		private String keyTxt;
		private String businessType; //业务类型 0：外卖饿了么服务商门店绑定,其他业务可不传
		
		public String getBusinessType() {
			return businessType;
		}

		public void setBusinessType(String businessType) {
			this.businessType = businessType;
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
