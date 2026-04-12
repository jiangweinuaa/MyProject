package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_SupPriceTemplateUpdateReq extends JsonBasicReq
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
		private String templateId;//模板编码
		private String supplierType;//供货机构类型：1-公司2-门店3-配送中心
		private String supplierId;//供货机构编码
		private String receiverType;//收货机构类型：1-公司2-门店3-配送中心
		private String receiverIdRange;//收货机构范围：0-全部机构1-指定机构
		private String memo;//备注
		private String status;//状态：-1未启用100已启用0已禁用
		private List<Range> rangeList;//指定收货机构列表
		private List<TemplateLang> templateName_lang;//
		public String getTemplateId()
		{
			return templateId;
		}
		public void setTemplateId(String templateId)
		{
			this.templateId = templateId;
		}
		public String getSupplierType()
		{
			return supplierType;
		}
		public void setSupplierType(String supplierType)
		{
			this.supplierType = supplierType;
		}
		public String getSupplierId()
		{
			return supplierId;
		}
		public void setSupplierId(String supplierId)
		{
			this.supplierId = supplierId;
		}
		public String getReceiverType()
		{
			return receiverType;
		}
		public void setReceiverType(String receiverType)
		{
			this.receiverType = receiverType;
		}
		public String getReceiverIdRange()
		{
			return receiverIdRange;
		}
		public void setReceiverIdRange(String receiverIdRange)
		{
			this.receiverIdRange = receiverIdRange;
		}
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String status)
		{
			this.status = status;
		}
		public List<Range> getRangeList()
		{
			return rangeList;
		}
		public void setRangeList(List<Range> rangeList)
		{
			this.rangeList = rangeList;
		}
		public List<TemplateLang> getTemplateName_lang()
		{
			return templateName_lang;
		}
		public void setTemplateName_lang(List<TemplateLang> templateName_lang)
		{
			this.templateName_lang = templateName_lang;
		}
		
		
		
	}
	
	public class TemplateLang
	{
		private String langType;//
		private String name;//
		public String getLangType()
		{
			return langType;
		}
		public void setLangType(String langType)
		{
			this.langType = langType;
		}
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}		
	}
	
	public class Range
	{
		private String id;//编码
		private String name;//名称
		public String getId()
		{
			return id;
		}
		public void setId(String id)
		{
			this.id = id;
		}
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}		
	}
	
}
