package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_AbnormalOrderProcessReq extends JsonBasicReq
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
		private String eId;
		private String orderNo;
		private String abnormalType;
		private String shipDate;
		
		private String shipStartTime;
		private String shipEndTime;

		private String autoOrderToSale;
		
		//商品列表（abnormalType为goodsNotFound时必传）
		private List<levelGoods> goodsList;

		public String geteId()
		{
			return eId;
		}

		public void seteId(String eId)
		{
			this.eId = eId;
		}

		public String getOrderNo()
		{
			return orderNo;
		}

		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}

		public String getAbnormalType()
		{
			return abnormalType;
		}

		public void setAbnormalType(String abnormalType)
		{
			this.abnormalType = abnormalType;
		}

		public String getShipDate()
		{
			return shipDate;
		}

		public void setShipDate(String shipDate)
		{
			this.shipDate = shipDate;
		}

		public String getShipStartTime()
		{
			return shipStartTime;
		}

		public void setShipStartTime(String shipStartTime)
		{
			this.shipStartTime = shipStartTime;
		}

		public String getShipEndTime()
		{
			return shipEndTime;
		}

		public void setShipEndTime(String shipEndTime)
		{
			this.shipEndTime = shipEndTime;
		}

		public String getAutoOrderToSale() {
			return autoOrderToSale;
		}

		public void setAutoOrderToSale(String autoOrderToSale) {
			this.autoOrderToSale = autoOrderToSale;
		}

		public List<levelGoods> getGoodsList()
		{
			return goodsList;
		}

		public void setGoodsList(List<levelGoods> goodsList)
		{
			this.goodsList = goodsList;
		}
		
		
		
		
	}
	
	public class levelGoods
	{
		private String item;
		private String pluBarcode;
		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}
		public String getPluBarcode()
		{
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode)
		{
			this.pluBarcode = pluBarcode;
		}
		
		
	}

}
