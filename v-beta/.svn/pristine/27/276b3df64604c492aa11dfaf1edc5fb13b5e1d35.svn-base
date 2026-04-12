package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

public class DCP_PGoodsUpdateReq extends JsonBasicReq
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
		private String pluNo;//套餐商品编码
		private String packageShareType;//分摊方式 FIXEDPRICE 固定单价 FIXEDPERCENT 固定比例 AMTPERCENT 金额比例	
		private String sUnit;//销售单位
		private String price;//组合售价
		private List<GoodsLang> pluName_lang;//商品名称多语言
		private List<ClassGroup> pClassList;//类别组
		
		private List<Org>orgList;
		private List<Shop>shopList;
		private List<PayLimit>payLimitList;
		private String shopRange;
		private String payLimit;
		
		public String getPluNo()
		{
			return pluNo;
		}
		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}
		
		public String getPackageShareType()
		{
			return packageShareType;
		}
		public void setPackageShareType(String packageShareType)
		{
			this.packageShareType = packageShareType;
		}
		public String getsUnit()
		{
			return sUnit;
		}
		public void setsUnit(String sUnit)
		{
			this.sUnit = sUnit;
		}
		public String getPrice()
		{
			return price;
		}
		public void setPrice(String price)
		{
			this.price = price;
		}
		public List<GoodsLang> getPluName_lang()
		{
			return pluName_lang;
		}
		public void setPluName_lang(List<GoodsLang> pluName_lang)
		{
			this.pluName_lang = pluName_lang;
		}
		public List<ClassGroup> getpClassList()
		{
			return pClassList;
		}
		public void setpClassList(List<ClassGroup> pClassList)
		{
			this.pClassList = pClassList;
		}
		
		public List<Org> getOrgList() {
			return orgList;
		}
		
		public void setOrgList(List<Org> orgList) {
			this.orgList = orgList;
		}
		
		public List<Shop> getShopList() {
			return shopList;
		}
		
		public void setShopList(List<Shop> shopList) {
			this.shopList = shopList;
		}
		
		public List<PayLimit> getPayLimitList() {
			return payLimitList;
		}
		
		public void setPayLimitList(List<PayLimit> payLimitList) {
			this.payLimitList = payLimitList;
		}
		
		public String getShopRange() {
			return shopRange;
		}
		
		public void setShopRange(String shopRange) {
			this.shopRange = shopRange;
		}
		
		public String getPayLimit() {
			return payLimit;
		}
		
		public void setPayLimit(String payLimit) {
			this.payLimit = payLimit;
		}
	}
	
	public class GoodsLang
	{
		private String lang_type;
		private String name;
		
		public String getLang_type()
		{
			return lang_type;
		}
		public void setLang_type(String lang_type)
		{
			this.lang_type = lang_type;
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
	
	public class ClassGroup
	{
		private String pclassNo;//分类编码
		private String invoWay;//选择条件1.必选 2.可选
		private String condCount;//数量
		private String minCount; // 可选时最少选择件数，默认0
		private List<subGoods> subGoodsList;
		
		public String getMinCount() {
			return minCount;
		}
		
		public void setMinCount(String minCount) {
			this.minCount = minCount;
		}
		
		public String getPclassNo()
		{
			return pclassNo;
		}
		public void setPclassNo(String pclassNo)
		{
			this.pclassNo = pclassNo;
		}
		public String getInvoWay()
		{
			return invoWay;
		}
		public void setInvoWay(String invoWay)
		{
			this.invoWay = invoWay;
		}
		public String getCondCount()
		{
			return condCount;
		}
		public void setCondCount(String condCount)
		{
			this.condCount = condCount;
		}
		public List<subGoods> getSubGoodsList()
		{
			return subGoodsList;
		}
		public void setSubGoodsList(List<subGoods> subGoodsList)
		{
			this.subGoodsList = subGoodsList;
		}
	}
	
	public class subGoods
	{
		private String detaiPluNo;//子商品编码
		private String detailUnit;//子商品单位
		private String invoWay;//选择条件1.必选 2.可选		
		private String qty;//数量
		private String isSel;//默认选中N
		private String extraAmt;//加价金额
		private String splitPrice;//分摊单价
		private String split;//是否分摊
		private String splitPercent;//分摊比例
		private String priority; // 显示顺序
		
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getDetaiPluNo()
		{
			return detaiPluNo;
		}
		public void setDetaiPluNo(String detaiPluNo)
		{
			this.detaiPluNo = detaiPluNo;
		}
		public String getDetailUnit()
		{
			return detailUnit;
		}
		public void setDetailUnit(String detailUnit)
		{
			this.detailUnit = detailUnit;
		}
		public String getInvoWay()
		{
			return invoWay;
		}
		public void setInvoWay(String invoWay)
		{
			this.invoWay = invoWay;
		}
		public String getQty()
		{
			return qty;
		}
		public void setQty(String qty)
		{
			this.qty = qty;
		}
		public String getIsSel()
		{
			return isSel;
		}
		public void setIsSel(String isSel)
		{
			this.isSel = isSel;
		}
		public String getExtraAmt()
		{
			return extraAmt;
		}
		public void setExtraAmt(String extraAmt)
		{
			this.extraAmt = extraAmt;
		}
		public String getSplitPrice()
		{
			return splitPrice;
		}
		public void setSplitPrice(String splitPrice)
		{
			this.splitPrice = splitPrice;
		}
		public String getSplit()
		{
			return split;
		}
		public void setSplit(String split)
		{
			this.split = split;
		}
		public String getSplitPercent()
		{
			return splitPercent;
		}
		public void setSplitPercent(String splitPercent)
		{
			this.splitPercent = splitPercent;
		}
	}
	
	@Data
	public class Org{
		private String orgNo;
		private String orgName;
	}
	
	@Data
	public class Shop{
		private String shopNo;
		private String shopName;
		private String beginDate;
		private String endDate;
	}
	
	@Data
	public class PayLimit{
		private String payCode;
		private String payName;
        private String payType;
	}
	
	
}
