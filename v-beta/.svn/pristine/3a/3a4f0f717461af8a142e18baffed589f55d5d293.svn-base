package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateCreateReq.RangeList;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateCreateReq.TemplateName_Lang;

public class DCP_SalePriceTemplateUpdateReq extends JsonBasicReq
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
		private String templateId ;
		private String templateType ;
		private String restrictChannel ;
		private String channelId ;
		private String memo ;
		private String status;		
		
		private List<levelTemplate> templateName_lang;		
		private List<levelRange> rangeList;
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		public String getTemplateType() {
			return templateType;
		}
		public void setTemplateType(String templateType) {
			this.templateType = templateType;
		}
		
		public String getRestrictChannel() {
			return restrictChannel;
		}
		public void setRestrictChannel(String restrictChannel) {
			this.restrictChannel = restrictChannel;
		}		
		
		public String getChannelId()
		{
			return channelId;
		}
		public void setChannelId(String channelId)
		{
			this.channelId = channelId;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public List<levelTemplate> getTemplateName_lang() {
			return templateName_lang;
		}
		public void setTemplateName_lang(List<levelTemplate> templateName_lang) {
			this.templateName_lang = templateName_lang;
		}
		
		public List<levelRange> getRangeList() {
			return rangeList;
		}
		public void setRangeList(List<levelRange> rangeList) {
			this.rangeList = rangeList;
		}
		
		
		
	}	
	
	public class levelTemplate
	{
		private String langType ;
		private String name ;
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	public class levelGoods
	{
		private String pluNo ;//商品编码
		private String unit ;//单位编码
		private String featureNo ;//特征码
		private String price ;//零售价
		private String minPrice ;//最低售价
		private String isDiscount ;//是否可手工打折Y/N
		private String isProm ;//是否可参与促销Y/N
		private String beginDate ;//生效日期YYYY-MM-DD
		private String endDate ;//失效日期YYYY-MM-DD
		private String status ;//状态：-1未启用100已启用 0已禁用
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getMinPrice() {
			return minPrice;
		}
		public void setMinPrice(String minPrice) {
			this.minPrice = minPrice;
		}
		public String getIsDiscount() {
			return isDiscount;
		}
		public void setIsDiscount(String isDiscount) {
			this.isDiscount = isDiscount;
		}
		public String getIsProm() {
			return isProm;
		}
		public void setIsProm(String isProm) {
			this.isProm = isProm;
		}
		public String getBeginDate() {
			return beginDate;
		}
		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}		
	}
	
	public class levelRange
	{
		private String id ;//编号
		private String name ;//名称
	
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		
		
	}
	
}
