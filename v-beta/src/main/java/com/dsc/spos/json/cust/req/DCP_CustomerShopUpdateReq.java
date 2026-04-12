package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_CustomerQueryReq.levelRequest;

public class DCP_CustomerShopUpdateReq extends JsonBasicReq
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
		private String customerNo;
		private List<Range> rangeList;

		public String getCustomerNo()
		{
			return customerNo;
		}
		public void setCustomerNo(String customerNo)
		{
			this.customerNo = customerNo;
		}
		public List<Range> getRangeList()
		{
			return rangeList;
		}
		public void setRangeList(List<Range> rangeList)
		{
			this.rangeList = rangeList;
		}


	}

	public  class Range
	{
		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
	}

}
