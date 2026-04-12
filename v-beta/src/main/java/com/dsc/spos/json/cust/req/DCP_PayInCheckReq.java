package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PayInCheckReq extends JsonBasicReq
{	
	private String o_eId;
	private String o_shopId;
	private String payInNo;
	private String checkStatus;
	private String comment;
	private String checkOpId;
	private String checkOpName;
	private String checkTime;
	
	public String getO_eId()
	{
		return o_eId;
	}
	public void setO_eId(String o_eId)
	{
		this.o_eId = o_eId;
	}
	public String getO_shopId()
	{
		return o_shopId;
	}
	public void setO_shopId(String o_shopId)
	{
		this.o_shopId = o_shopId;
	}
	public String getPayInNo()
	{
		return payInNo;
	}
	public void setPayInNo(String payInNo)
	{
		this.payInNo = payInNo;
	}
	public String getCheckStatus()
	{
		return checkStatus;
	}
	public void setCheckStatus(String checkStatus)
	{
		this.checkStatus = checkStatus;
	}
	public String getComment()
	{
		return comment;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	public String getCheckOpId()
	{
		return checkOpId;
	}
	public void setCheckOpId(String checkOpId)
	{
		this.checkOpId = checkOpId;
	}
	public String getCheckOpName()
	{
		return checkOpName;
	}
	public void setCheckOpName(String checkOpName)
	{
		this.checkOpName = checkOpName;
	}
	public String getCheckTime()
	{
		return checkTime;
	}
	public void setCheckTime(String checkTime)
	{
		this.checkTime = checkTime;
	} 
	
	
	
}
