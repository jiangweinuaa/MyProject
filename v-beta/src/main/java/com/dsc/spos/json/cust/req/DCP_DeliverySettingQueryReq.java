package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 货运厂商查询
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_DeliverySettingQueryReq extends JsonBasicReq 
{

	private levelRequest request;		

	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String keyTxt;
		private String deliveryType;
		private String status;
		public String getKeyTxt()
		{
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt)
		{
			this.keyTxt = keyTxt;
		}
		public String getDeliveryType()
		{
			return deliveryType;
		}
		public void setDeliveryType(String deliveryType)
		{
			this.deliveryType = deliveryType;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}

	}





}
