package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderShipping_OpenReq extends JsonBasicReq
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
		private String opType;
		private String[] orderList;		
		private String bdate;
        private String opShopId;//新加字段当前操作门店

        private String machineNo;//机台编号（POS渠道必传）
        private String squadNo;//班别编号（POS渠道必传）
        private String workNo;//班号（POS渠道必传）
        private String opNo;//用户编号（POS渠道必传）
        private String opName;
		
		public String geteId()
		{
			return eId;
		}
		public void seteId(String eId)
		{
			this.eId = eId;
		}
		public String getOpType()
		{
			return opType;
		}
		public void setOpType(String opType)
		{
			this.opType = opType;
		}
		public String[] getOrderList()
		{
			return orderList;
		}
		public void setOrderList(String[] orderList)
		{
			this.orderList = orderList;
		}
		public String getBdate()
		{
			return bdate;
		}
		public void setBdate(String bdate)
		{
			this.bdate = bdate;
		}

        public String getOpShopId()
        {
            return opShopId;
        }

        public void setOpShopId(String opShopId)
        {
            this.opShopId = opShopId;
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

        public String getOpNo()
        {
            return opNo;
        }

        public void setOpNo(String opNo)
        {
            this.opNo = opNo;
        }

        public String getOpName() {
            return opName;
        }

        public void setOpName(String opName) {
            this.opName = opName;
        }
    }
	
	public class Order
	{
		private String orderNo;

		public String getOrderNo()
		{
			return orderNo;
		}

		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}		
	}
	
	
}
