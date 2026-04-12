package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderDeliveryNoCreate_OpenRes extends JsonRes
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
		private List<Order> errorOrderList;

		public List<Order> getErrorOrderList()
		{
			return errorOrderList;
		}

		public void setErrorOrderList(List<Order> errorOrderList)
		{
			this.errorOrderList = errorOrderList;
		}		
	}

	public class Order
	{
		private String orderNo;
		private String errorDesc;

		public String getOrderNo()
		{
			return orderNo;
		}

		public void setOrderNo(String orderNo)
		{
			this.orderNo = orderNo;
		}

		public String getErrorDesc()
		{
			return errorDesc;
		}

		public void setErrorDesc(String errorDesc)
		{
			this.errorDesc = errorDesc;
		}		
		
		
	}
}
