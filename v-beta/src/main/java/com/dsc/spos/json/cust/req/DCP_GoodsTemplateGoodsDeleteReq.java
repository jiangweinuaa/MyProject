package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsTemplateGoodsDeleteReq extends JsonBasicReq
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
		private String templateId;
		private String isAllGoods;
		private List<levelPlu> pluList;//
		public String getTemplateId()
		{
			return templateId;
		}
		public void setTemplateId(String templateId)
		{
			this.templateId = templateId;
		}
		public String getIsAllGoods()
		{
			return isAllGoods;
		}
		public void setIsAllGoods(String isAllGoods)
		{
			this.isAllGoods = isAllGoods;
		}
		public List<levelPlu> getPluList()
		{
			return pluList;
		}
		public void setPluList(List<levelPlu> pluList)
		{
			this.pluList = pluList;
		}
		
		

			
	}
	
	public class levelPlu
	{
		private String pluNo;//模板编码

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
