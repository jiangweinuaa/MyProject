package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_FlavorGoodsDeleteReq extends JsonBasicReq
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
		private List<levelFlavorGoods> pluList ;//
				
		public List<levelFlavorGoods> getPluList() {
			return pluList;
		}
		public void setPluList(List<levelFlavorGoods> pluList) {
			this.pluList = pluList;
		}
		
		
	}
	
	public class levelFlavorGoods
	{
		private String flavorId ;//口味编码
		private String pluNo ;//商品编码	

		public String getPluNo() {
			return pluNo;
		}

		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}

		public String getFlavorId()
		{
			return flavorId;
		}

		public void setFlavorId(String flavorId)
		{
			this.flavorId = flavorId;
		}

		
		
	}
	
	
}
