package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_SalePriceTemplateDetailRes extends JsonRes
{

	private List<level1Elm> datas;


	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String templateId;//模板编码
		private String templateType;//模板类型：COMMON-通用模板SPECIAL-专用模板
		private String restrictShop;//适用门店：0-所有门店1-指定门店2-排除门店
		private String restrictChannel;//适用渠道：0-所有渠道1-指定渠道2-排除渠道
		private String restrictAppType;//适用应用：0-所有应用1-指定应用
		private String restrictCardType;//适用卡类型：0-所有卡类型1-指定卡类型2-排除卡类型
		private String memo;//备注
		private String status;//状态：-1未启用100已启用0已禁用
		private String createtime;//创建时间YYYY-MM-DD HH:MI:SS，降序第一列
		private String createopid;//创建人编号
		private String createopname;//创建人名称
		private String lastmoditime;//最后修改时间YYYY-MM-DD HH:MI:SS
		private String lastmodiopid;//最后修改人编码
		private String lastmodiname;//最后修改人名称

		private List<levelTemplate> templateName_lang;		
		private List<levelGoods> priceList;		
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
		
		public String getRestrictShop() {
			return restrictShop;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		public String getRestrictChannel() {
			return restrictChannel;
		}
		public void setRestrictChannel(String restrictChannel) {
			this.restrictChannel = restrictChannel;
		}
		public String getRestrictAppType() {
			return restrictAppType;
		}
		public void setRestrictAppType(String restrictAppType) {
			this.restrictAppType = restrictAppType;
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
		
		
		public String getCreatetime() {
			return createtime;
		}
		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}
		public String getCreateopid() {
			return createopid;
		}
		public void setCreateopid(String createopid) {
			this.createopid = createopid;
		}
		public String getCreateopname() {
			return createopname;
		}
		public void setCreateopname(String createopname) {
			this.createopname = createopname;
		}
		public String getLastmoditime() {
			return lastmoditime;
		}
		public void setLastmoditime(String lastmoditime) {
			this.lastmoditime = lastmoditime;
		}
		public String getLastmodiopid() {
			return lastmodiopid;
		}
		public void setLastmodiopid(String lastmodiopid) {
			this.lastmodiopid = lastmodiopid;
		}
		public String getLastmodiname() {
			return lastmodiname;
		}
		public void setLastmodiname(String lastmodiname) {
			this.lastmodiname = lastmodiname;
		}
		public List<levelTemplate> getTemplateName_lang() {
			return templateName_lang;
		}
		public void setTemplateName_lang(List<levelTemplate> templateName_lang) {
			this.templateName_lang = templateName_lang;
		}
	
		
		public String getRestrictCardType() {
			return restrictCardType;
		}
		public void setRestrictCardType(String restrictCardType) {
			this.restrictCardType = restrictCardType;
		}
		public List<levelGoods> getPriceList() {
			return priceList;
		}
		public void setPriceList(List<levelGoods> priceList) {
			this.priceList = priceList;
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
		private String item ;//商品编码
		private String pluNo ;//商品编码
		private String pluName ;//商品名称
		private String unit ;//单位编码
		private String unitName ;//单位名称
		private String featureNo ;//特征码
		private String featureName ;//特征码名称
		private String price ;//零售价
		private String minPrice ;//最低售价
		private String isDiscount ;//是否可手工打折Y/N
		private String isProm ;//是否可参与促销Y/N
		private String beginDate ;//生效日期YYYY-MM-DD
		private String endDate ;//失效日期YYYY-MM-DD
		private String status ;//状态：-1未启用100已启用 0已禁用
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getFeatureNo() {
			return featureNo;
		}
		public void setFeatureNo(String featureNo) {
			this.featureNo = featureNo;
		}
		public String getFeatureName() {
			return featureName;
		}
		public void setFeatureName(String featureName) {
			this.featureName = featureName;
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
		private String rangeType ;//1-公司2-门店3-渠道4-应用
		private String id ;//编号
		private String name ;//名称
		public String getRangeType() {
			return rangeType;
		}
		public void setRangeType(String rangeType) {
			this.rangeType = rangeType;
		}
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
