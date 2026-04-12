package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_OrderQRCodeQuery_OpenReq extends JsonBasicReq
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
		private String opNo;
		private String opName;
		private String machineNo;
		private String squadNo;
		private String workNo;
		private String shopNo;
		private String orgType;
		private List<level1Elm> orderList;
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
		public String getMachineNo()
		{
			return machineNo;
		}
		public void setMachineNo(String machineNo)
		{
			this.machineNo = machineNo;
		}
		public String getSquadNo()
		{
			return squadNo;
		}
		public void setSquadNo(String squadNo)
		{
			this.squadNo = squadNo;
		}
		public String getWorkNo()
		{
			return workNo;
		}
		public void setWorkNo(String workNo)
		{
			this.workNo = workNo;
		}
		public String getShopNo()
		{
			return shopNo;
		}
		public void setShopNo(String shopNo)
		{
			this.shopNo = shopNo;
		}
		public String getOrgType()
		{
			return orgType;
		}
		public void setOrgType(String orgType)
		{
			this.orgType = orgType;
		}
		public List<level1Elm> getOrderList()
		{
			return orderList;
		}
		public void setOrderList(List<level1Elm> orderList)
		{
			this.orderList = orderList;
		}
		
		
	}
	
	public class level1Elm
	{
		private String orderNo;
		private String loadDocType;
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
		
	}

}
