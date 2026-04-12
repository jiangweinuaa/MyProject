package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

public class DCP_GoodsOnlineDetailRes  extends JsonRes {
	
	private levelElm datas;
	public levelElm getDatas() {
		return datas;
	}
	
	public void setDatas(levelElm datas) {
		this.datas = datas;
	}
	
	public class levelElm
	{
		private String pluNo;
		private String pluName;
		private String pluType;//商品类型：NORMAL-普通商品 FEATURE-特征码商品 PACKAGE -套餐商品MULTISPEC-多规格商品
		private String stockDisplay;//页面是否显示库存0-否1-是
		private String attrGroupId;//商品属性分组编码
		private String attrGroupName;//商品属性分组编码
		private String shopPickUp;//是否支持自提0-否1-是
		private String cityDeliver;//是否支持同城配送0-否1-是
		private String expressDeliver;//是否支持全国快递0-否1-是	
		private String freightFree;//是否包邮0-否1-是
		private String freightTemplateId;//运费模板编码
		private String preSale;//是否预订，需提前预订0-否1-是
		private String deliveryDateType;//发货时机类型1：付款成功后发货2：指定日期发货
		private String deliveryDateType2;//发货时间类型1：小时 2：天
		private String deliveryDateValue;//付款后%S天后发货
		private String deliveryDate;//预计发货日期
		private String onShelfAuto;//是否自动下架0-否1-是	
		private String onShelfDate;//上架日期
		private String onShelfTime;//上架时间
		private String offShelfAuto;//是否自动下架0-否1-是
		private String offShelfDate;//下架日期  自动时不可空，YYYY-MM-DD
		private String offShelfTime;//下架时间 自动时不可空,HH:MI:SS
		private String memo;
		private String status;//状态：-1未上架100-已上架0-已下架
		private String sortId;
		private String restrictShop;//适用门店：0-所有门店1-指定门店2-排除门店
		private String restrictChannel;//适用渠道：0-所有渠道1-指定渠道2-排除渠道
		private String restrictAppType;//适用应用：0-所有应用1-指定应用
		private String displayName;//商品显示名称
		private List<displayNameLang> displayName_lang;//商品显示名称
		private List<simpleDescription> simpleDescription_lang;
		private String simpleDescription;
		private List<shareDescription> shareDescription_lang;
		private String shareDescription;
		private List<classMemu> classList;//隶属菜单
		private List<refClassMemu> refClassList;//关联推荐菜单
		private List<range> rangeList;//适用范围
		private List<intro> introList;//商品介绍
		private List<msgKind> msgKindList;//商品留言
		private List<Tags> tags;
		
		
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
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
		public String getPluType() {
			return pluType;
		}
		public void setPluType(String pluType) {
			this.pluType = pluType;
		}
		public String getStockDisplay() {
			return stockDisplay;
		}
		public void setStockDisplay(String stockDisplay) {
			this.stockDisplay = stockDisplay;
		}
		public String getAttrGroupId() {
			return attrGroupId;
		}
		public void setAttrGroupId(String attrGroupId) {
			this.attrGroupId = attrGroupId;
		}
		public String getAttrGroupName() {
			return attrGroupName;
		}
		public void setAttrGroupName(String attrGroupName) {
			this.attrGroupName = attrGroupName;
		}
		public String getShopPickUp() {
			return shopPickUp;
		}
		public void setShopPickUp(String shopPickUp) {
			this.shopPickUp = shopPickUp;
		}
		public String getCityDeliver() {
			return cityDeliver;
		}
		public void setCityDeliver(String cityDeliver) {
			this.cityDeliver = cityDeliver;
		}
		public String getExpressDeliver() {
			return expressDeliver;
		}
		public void setExpressDeliver(String expressDeliver) {
			this.expressDeliver = expressDeliver;
		}
		public String getFreightFree() {
			return freightFree;
		}
		public void setFreightFree(String freightFree) {
			this.freightFree = freightFree;
		}
		public String getFreightTemplateId() {
			return freightTemplateId;
		}
		public void setFreightTemplateId(String freightTemplateId) {
			this.freightTemplateId = freightTemplateId;
		}
		public String getPreSale() {
			return preSale;
		}
		public void setPreSale(String preSale) {
			this.preSale = preSale;
		}
		public String getDeliveryDateType() {
			return deliveryDateType;
		}
		public void setDeliveryDateType(String deliveryDateType) {
			this.deliveryDateType = deliveryDateType;
		}
		public String getDeliveryDateType2() {
			return deliveryDateType2;
		}
		public void setDeliveryDateType2(String deliveryDateType2) {
			this.deliveryDateType2 = deliveryDateType2;
		}
		public String getDeliveryDateValue() {
			return deliveryDateValue;
		}
		public void setDeliveryDateValue(String deliveryDateValue) {
			this.deliveryDateValue = deliveryDateValue;
		}
		public String getDeliveryDate() {
			return deliveryDate;
		}
		public void setDeliveryDate(String deliveryDate) {
			this.deliveryDate = deliveryDate;
		}
		public String getOnShelfAuto() {
			return onShelfAuto;
		}
		public void setOnShelfAuto(String onShelfAuto) {
			this.onShelfAuto = onShelfAuto;
		}
		public String getOnShelfDate() {
			return onShelfDate;
		}
		public void setOnShelfDate(String onShelfDate) {
			this.onShelfDate = onShelfDate;
		}
		public String getOnShelfTime() {
			return onShelfTime;
		}
		public void setOnShelfTime(String onShelfTime) {
			this.onShelfTime = onShelfTime;
		}
		public String getOffShelfAuto() {
			return offShelfAuto;
		}
		public void setOffShelfAuto(String offShelfAuto) {
			this.offShelfAuto = offShelfAuto;
		}
		public String getOffShelfDate() {
			return offShelfDate;
		}
		public void setOffShelfDate(String offShelfDate) {
			this.offShelfDate = offShelfDate;
		}
		public String getOffShelfTime() {
			return offShelfTime;
		}
		public void setOffShelfTime(String offShelfTime) {
			this.offShelfTime = offShelfTime;
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
		public List<classMemu> getClassList() {
			return classList;
		}
		public void setClassList(List<classMemu> classList) {
			this.classList = classList;
		}
		public List<refClassMemu> getRefClassList() {
			return refClassList;
		}
		public void setRefClassList(List<refClassMemu> refClassList) {
			this.refClassList = refClassList;
		}
		public List<range> getRangeList() {
			return rangeList;
		}
		public void setRangeList(List<range> rangeList) {
			this.rangeList = rangeList;
		}
		public List<intro> getIntroList() {
			return introList;
		}
		public void setIntroList(List<intro> introList) {
			this.introList = introList;
		}
		public List<msgKind> getMsgKindList() {
			return msgKindList;
		}
		public void setMsgKindList(List<msgKind> msgKindList) {
			this.msgKindList = msgKindList;
		}
		public List<simpleDescription> getSimpleDescription_lang() {
			return simpleDescription_lang;
		}
		public void setSimpleDescription_lang(List<simpleDescription> simpleDescription_lang) {
			this.simpleDescription_lang = simpleDescription_lang;
		}
		public List<shareDescription> getShareDescription_lang() {
			return shareDescription_lang;
		}
		public void setShareDescription_lang(List<shareDescription> shareDescription_lang) {
			this.shareDescription_lang = shareDescription_lang;
		}
		public List<displayNameLang> getDisplayName_lang() {
			return displayName_lang;
		}
		public void setDisplayName_lang(List<displayNameLang> displayName_lang) {
			this.displayName_lang = displayName_lang;
		}
		public String getDisplayName()
		{
			return displayName;
		}
		public void setDisplayName(String displayName)
		{
			this.displayName = displayName;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public String getSimpleDescription()
		{
			return simpleDescription;
		}
		public void setSimpleDescription(String simpleDescription)
		{
			this.simpleDescription = simpleDescription;
		}
		public String getShareDescription()
		{
			return shareDescription;
		}
		public void setShareDescription(String shareDescription)
		{
			this.shareDescription = shareDescription;
		}
		public List<Tags> getTags() {
			return tags;
		}
		public void setTags(List<Tags> tags) {
			this.tags = tags;
		}
	}
	
	public class displayNameLang
	{
		private String langType;
		private String name;
		
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
	
	public class simpleDescription
	{
		private String langType;
		private String name;
		
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
	
	public class shareDescription
	{
		private String langType;
		private String name;
		
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
	
	public class classMemu
	{
		private String classNo;//菜单编码
		private String className;//菜单名称
		public String getClassNo() {
			return classNo;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		
	}
	
	public class refClassMemu
	{
		private String classNo;//菜单编码
		private String className;//菜单名称
		public String getClassNo() {
			return classNo;
		}
		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		
	}
	
	public class intro
	{
		private String attrId;//属性编码
		private String attrName;//属性名称
		private String intro;//商品介绍
		public String getAttrId() {
			return attrId;
		}
		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}
		public String getAttrName() {
			return attrName;
		}
		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}
		public String getIntro() {
			return intro;
		}
		public void setIntro(String intro) {
			this.intro = intro;
		}
		
	}
	
	public class msgKind
	{
		private String msgKindId;//留言项编码
		private String need;//是否必须0-否1-是
		public String getMsgKindId() {
			return msgKindId;
		}
		public void setMsgKindId(String msgKindId) {
			this.msgKindId = msgKindId;
		}
		public String getNeed() {
			return need;
		}
		public void setNeed(String need) {
			this.need = need;
		}
		
	}
	
	public class range
	{
		private String rangeType;//1-公司2-门店3-渠道4-应用
		private String id;
		private String name;
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
	
	public class Tags{
		private String onliLaSorting;
		private String tagNo;
		private String tagName;
		
		public String getOnliLaSorting() {
			return onliLaSorting;
		}
		public void setOnliLaSorting(String onliLaSorting) {
			this.onliLaSorting = onliLaSorting;
		}
		public String getTagNo() {
			return tagNo;
		}
		public void setTagNo(String tagNo) {
			this.tagNo = tagNo;
		}
		public String getTagName() {
			return tagName;
		}
		public void setTagName(String tagName) {
			this.tagName = tagName;
		}
	}
}
