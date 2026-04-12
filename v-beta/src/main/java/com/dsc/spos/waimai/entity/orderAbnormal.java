package com.dsc.spos.waimai.entity;

import java.io.Serializable;
import java.util.List;

public class orderAbnormal implements Serializable
{
	private String abnormalType;
	private String abnormalTypeName;
	private String abnormalTime;
	private String memo;
	private String status;
	private List<orderAbnormalDetail> detail;
	public String getAbnormalType()
	{
		return abnormalType;
	}
	public void setAbnormalType(String abnormalType)
	{
		this.abnormalType = abnormalType;
	}
	public String getAbnormalTypeName()
	{
		return abnormalTypeName;
	}
	public void setAbnormalTypeName(String abnormalTypeName)
	{
		this.abnormalTypeName = abnormalTypeName;
	}
	public String getAbnormalTime()
	{
		return abnormalTime;
	}
	public void setAbnormalTime(String abnormalTime)
	{
		this.abnormalTime = abnormalTime;
	}
	public String getMemo()
	{
		return memo;
	}
	public void setMemo(String memo)
	{
		this.memo = memo;
	}
	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public List<orderAbnormalDetail> getDetail()
	{
		return detail;
	}
	public void setDetail(List<orderAbnormalDetail> detail)
	{
		this.detail = detail;
	}
	
	

}
