package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderStatusLogQueryRes  extends JsonRes
{
	private levelDatas datas;

	
	public levelDatas getDatas()
	{
		return datas;
	}

	public void setDatas(levelDatas datas)
	{
		this.datas = datas;
	}

	public class levelDatas
	{
		private String orderNo;
		private String loadDocType;
		private String channelId;
		private String loadDocBillType;
		private String loadDocOrderNo;		
		private List<level1Elm> statusList;
		public String getOrderNo()
		{
			return orderNo;
		}
		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}
		public String getLoadDocType()
		{
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType)
		{
			this.loadDocType = loadDocType;
		}
		public String getChannelId()
		{
			return channelId;
		}
		public void setChannelId(String channelId)
		{
			this.channelId = channelId;
		}
		public String getLoadDocBillType()
		{
			return loadDocBillType;
		}
		public void setLoadDocBillType(String loadDocBillType)
		{
			this.loadDocBillType = loadDocBillType;
		}
		public String getLoadDocOrderNo()
		{
			return loadDocOrderNo;
		}
		public void setLoadDocOrderNo(String loadDocOrderNo)
		{
			this.loadDocOrderNo = loadDocOrderNo;
		}
		public List<level1Elm> getStatusList()
		{
			return statusList;
		}
		public void setStatusList(List<level1Elm> statusList)
		{
			this.statusList = statusList;
		}
		
		
	}
	
	public class level1Elm
	{		
		private String statusType;//状态类型
		private String statusTypeName;//状态名称
		private String status;
		private String statusName;
		private String update_time;//操作时间			
		private String memo;
		private String opNo;
		private String opName;
		public String getStatusType()
		{
			return statusType;
		}
		public void setStatusType(String statusType)
		{
			this.statusType = statusType;
		}
		public String getStatusTypeName()
		{
			return statusTypeName;
		}
		public void setStatusTypeName(String statusTypeName)
		{
			this.statusTypeName = statusTypeName;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		public String getStatusName()
		{
			return statusName;
		}
		public void setStatusName(String statusName)
		{
			this.statusName = statusName;
		}
		public String getUpdate_time()
		{
			return update_time;
		}
		public void setUpdate_time(String update_time)
		{
			this.update_time = update_time;
		}
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
		}
		public String getOpNo()
		{
			return opNo;
		}
		public void setOpNo(String opNo)
		{
			this.opNo = opNo;
		}
		public String getOpName()
		{
			return opName;
		}
		public void setOpName(String opName)
		{
			this.opName = opName;
		}


	}


}
