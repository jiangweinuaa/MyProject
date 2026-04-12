package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_StuffGoodsAddReq extends JsonBasicReq
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
		private String stuffId;
		private List<levelStuffGoods> pluList ;//
		public List<levelStuffGoods> getPluList()
		{
			return pluList;
		}

		public void setPluList(List<levelStuffGoods> pluList)
		{
			this.pluList = pluList;
		}

		public String getStuffId()
		{
			return stuffId;
		}

		public void setStuffId(String stuffId)
		{
			this.stuffId = stuffId;
		}
			
		
		
		
	}
	
	public class levelStuffGoods
	{
		private String pluNo ;//商品编码	

		public String getPluNo()
		{
			return pluNo;
		}

		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}

		
				
	}
	
	
}
