package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderDeliveryNoCreateReq extends JsonBasicReq
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
		private List<Order> orderList;

		public List<Order> getOrderList()
		{
			return orderList;
		}

		public void setOrderList(List<Order> orderList)
		{
			this.orderList = orderList;
		}		
	}
	
	public class Order
	{
		private String orderNo;
		private String deliveryType;
		private String deliveryNo;//手工物流单号
        private String delName;//配送员
        private String delTelephone;//配送电话
        private String delId;//配送员
		public String getOrderNo()
		{
			return orderNo;
		}
		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}
		public String getDeliveryType()
		{
			return deliveryType;
		}
		public void setDeliveryType(String deliveryType)
		{
			this.deliveryType = deliveryType;
		}
		public String getDeliveryNo()
		{
			return deliveryNo;
		}
		public void setDeliveryNo(String deliveryNo)
		{
			this.deliveryNo = deliveryNo;
		}

        public String getDelName() {
            return delName;
        }

        public void setDelName(String delName) {
            this.delName = delName;
        }

        public String getDelTelephone() {
            return delTelephone;
        }

        public void setDelTelephone(String delTelephone) {
            this.delTelephone = delTelephone;
        }

        public String getDelId() {
            return delId;
        }

        public void setDelId(String delId) {
            this.delId = delId;
        }
    }
		
}
