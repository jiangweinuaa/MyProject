package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderBasicSettingUpdateReq extends JsonBasicReq
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
		private String settingNo;
		private String settingValue;
		public String getSettingNo()
		{
			return settingNo;
		}
		public void setSettingNo(String settingNo)
		{
			this.settingNo = settingNo;
		}
		public String getSettingValue()
		{
			return settingValue;
		}
		public void setSettingValue(String settingValue)
		{
			this.settingValue = settingValue;
		}
		
		

	}

}
