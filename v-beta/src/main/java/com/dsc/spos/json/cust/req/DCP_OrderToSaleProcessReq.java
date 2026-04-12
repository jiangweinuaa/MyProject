package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服務函數：OrderToSaleProcess
 * 服务说明：订单转销售
 * @author jinzma 
 * @since  2019-04-15
 */
public class DCP_OrderToSaleProcessReq extends JsonBasicReq  
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
		private String orderNo;

		private List<goods> goodsList;
		
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

		public List<goods> getGoodsList()
		{
			return goodsList;
		}
		public void setGoodsList(List<goods> goodsList)
		{
			this.goodsList = goodsList;
		}
		
	}

	public class goods
	{
		private String item;
		private BigDecimal qty;

		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}

		public BigDecimal getQty()
		{
			return qty;
		}

		public void setQty(BigDecimal qty)
		{
			this.qty = qty;
		}
	}

}
