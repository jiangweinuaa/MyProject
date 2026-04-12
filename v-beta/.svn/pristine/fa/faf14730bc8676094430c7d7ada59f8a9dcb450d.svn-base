package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_SupPriceTemplateGoodsUpdateReq extends JsonBasicReq
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
		private String templateId;//		
		private List<TemplatePrice> pluList;//
		
		public String getTemplateId()
		{
			return templateId;
		}
		public void setTemplateId(String templateId)
		{
			this.templateId = templateId;
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
		private String item;//项次，不可改
		private String pluNo;//商品编码
		private String pluName;//商品名称
		private String unit;//单位编码
		private String unitName;//单位名称
		private String featureNo;//特征码：非特征商品为一个空格，特征商品为ALL
		private String price;//供货价
		private String beginDate;//生效日期YYYYMMDD
		private String endDate;//失效日期YYYYMMDD
		public String getItem()
		{
			return item;
		}
		public void setItem(String item)
		{
			this.item = item;
		}
		public String getPluNo()
		{
			return pluNo;
		}
		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}
		public String getPluName()
		{
			return pluName;
		}
		public void setPluName(String pluName)
		{
			this.pluName = pluName;
		}
		public String getUnit()
		{
			return unit;
		}
		public void setUnit(String unit)
		{
			this.unit = unit;
		}
		public String getUnitName()
		{
			return unitName;
		}
		public void setUnitName(String unitName)
		{
			this.unitName = unitName;
		}
		public String getFeatureNo()
		{
			return featureNo;
		}
		public void setFeatureNo(String featureNo)
		{
			this.featureNo = featureNo;
		}
		public String getPrice()
		{
			return price;
		}
		public void setPrice(String price)
		{
			this.price = price;
		}
		public String getBeginDate()
		{
			return beginDate;
		}
		public void setBeginDate(String beginDate)
		{
			this.beginDate = beginDate;
		}
		public String getEndDate()
		{
			return endDate;
		}
		public void setEndDate(String endDate)
		{
			this.endDate = endDate;
		}		
	}
	
	
	
}
