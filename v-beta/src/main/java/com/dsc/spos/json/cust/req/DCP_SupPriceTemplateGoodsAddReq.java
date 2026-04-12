package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
public class DCP_SupPriceTemplateGoodsAddReq extends JsonBasicReq
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
		private String templateId ;//商品模板
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
		private String type ;//CATEGORY-商品分类；GOODS-商品；
		private String id ;//编码
		private String discRate ;//售价折扣率：存储100=100%
		private String beginDate ;//生效日期YYYY-MM-DD
		private String endDate ;//失效日期YYYY-MM-DD


		public String getType()
		{
			return type;
		}
		public void setType(String type)
		{
			this.type = type;
		}
		public String getId()
		{
			return id;
		}
		public void setId(String id)
		{
			this.id = id;
		}
		
		
		public String getDiscRate()
		{
			return discRate;
		}
		public void setDiscRate(String discRate)
		{
			this.discRate = discRate;
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
