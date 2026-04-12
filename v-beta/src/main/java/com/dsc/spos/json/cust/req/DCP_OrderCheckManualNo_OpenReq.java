package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderCheckManualNo_OpenReq extends JsonBasicReq
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
		private String eId;		
		private String ManualNo;//手工单号，一周之内不重复就行

		public String geteId()
		{
			return eId;
		}
		public void seteId(String eId)
		{
			this.eId = eId;
		}
		public String getManualNo()
		{
			return ManualNo;
		}
		public void setManualNo(String manualNo)
		{
			ManualNo = manualNo;
		}		
	}	





}
