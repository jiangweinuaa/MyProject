package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_OrderAbnormalQueryRes extends JsonBasicRes
{

	private level1Elm datas;
	public level1Elm getDatas()
	{
		return datas;
	}
	public void setDatas(level1Elm datas)
	{
		this.datas = datas;
	}
	
	public class level1Elm
	{
		private List<abnormal> abnormalList;

		public List<abnormal> getAbnormalList()
		{
			return abnormalList;
		}

		public void setAbnormalList(List<abnormal> abnormalList)
		{
			this.abnormalList = abnormalList;
		}
		
	}
	
	public class abnormal
	{
		private String abnormalType;
		private String abnormalTypeName;
		private String abnormalTime;
		private String memo;
		private String status;
		private List<abnormalDetail> detail;
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
		public List<abnormalDetail> getDetail()
		{
			return detail;
		}
		public void setDetail(List<abnormalDetail> detail)
		{
			this.detail = detail;
		}
		
		
	}
	
	public class abnormalDetail
	{
		private String item;
		private String pluName;		
		private String memo;
		/**
		 * 状态  0：待解决；100：已解决
		 */
		private String status;
		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}
		public String getPluName()
		{
			return pluName;
		}
		public void setPluName(String pluName)
		{
			this.pluName = pluName;
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
		
		
		
	}
	
}
