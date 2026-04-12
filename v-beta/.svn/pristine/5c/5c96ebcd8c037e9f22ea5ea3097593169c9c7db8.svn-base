package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_SupPriceTemplateGoodsDeleteReq extends JsonBasicReq
{
	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
	public class level1Elm
	{
		private String templateId;//商品模板
		private String isAllGoods;//是否全部商品Y/N		
		private List<TemplatePrice> pluList;
		
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
		public List<TemplatePrice> getPluList()
		{
			return pluList;
		}
		public void setPluList(List<TemplatePrice> pluList)
		{
			this.pluList = pluList;
		}		
	}

	public class TemplatePrice
	{		
		private String item;//项次

		public String getItem()
		{
			return item;
		}

		public void setItem(String item)
		{
			this.item = item;
		}		
	}
	
	
}
