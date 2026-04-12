package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_GoodsTemplateDetailRes extends JsonRes
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

		private String templateId ;//模板编码
		private String templateType ;//模板类型：COMMON-通用模板SPECIAL-专用模板
		
		private String restrictShop ;//适用门店：0-所有门店1-指定门店2-排除门店
		private String restrictChannel ;//适用渠道：0-所有渠道1-指定渠道2-排除渠道
		private String restrictAppType ;//适用应用：0-所有应用1-指定应用
		private String memo ;//备注
		private String status;//状态：-1未启用100已启用0已禁用		
		private String createtime;//创建时间YYYY-MM-DD HH:MI:SS		
		private String createopid;//创建人编号		
		private String createopname;//创建人名称	
		private String lastmoditime;//最后修改时间YYYY-MM-DD HH:MI:SS		
		private String lastmodiopid;//最后修改人编码		
		private String lastmodiname;//最后修改人名称	

		private List<levelTemplate> templateName_lang;		
		private List<levelGoods> goodsList;		
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
		public List<levelGoods> getGoodsList() {
			return goodsList;
		}
		public void setGoodsList(List<levelGoods> goodsList) {
			this.goodsList = goodsList;
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
		private String supplierType ;//供货机构类型：FACTORY-工厂；SUPPLIER-供应商
		private String supplierId ;//供货机构编码
		private String warningQty ;//警戒库存量
		private String safeQty ;//安全库存量
		private String canSale ;//是否可销售Y/N
		private String canFree ;//是否可免单Y/N
		private String canOrder ;//是否可预订Y/N
		private String canReturn ;//是否可退货Y/N
		private String canPurchase ;//是否可采购Y/N
		private String taxCode ;//税别编码
		private String canRequire ;//是否可要货Y/N
		private String minQty ;//最小要货量
		private String maxQty ;//最大要货量
		private String multiQty ;//要货倍量
		private String canRequireBack ;//是否可退仓Y/N
		private String isAutoSubtract ;//是否自动扣料Y/N
		private String canEstimate ;//可预估Y/N
		private String clearType ;//估清方式N-不估清 PERIOD-按餐段估清 DAY-按天估清
		private String status ;//是否有效：0-无效100有效
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getSupplierType() {
			return supplierType;
		}
		public void setSupplierType(String supplierType) {
			this.supplierType = supplierType;
		}
		public String getSupplierId() {
			return supplierId;
		}
		public void setSupplierId(String supplierId) {
			this.supplierId = supplierId;
		}
		public String getWarningQty() {
			return warningQty;
		}
		public void setWarningQty(String warningQty) {
			this.warningQty = warningQty;
		}
		public String getSafeQty() {
			return safeQty;
		}
		public void setSafeQty(String safeQty) {
			this.safeQty = safeQty;
		}
		public String getCanSale() {
			return canSale;
		}
		public void setCanSale(String canSale) {
			this.canSale = canSale;
		}
		public String getCanFree() {
			return canFree;
		}
		public void setCanFree(String canFree) {
			this.canFree = canFree;
		}
		public String getCanOrder() {
			return canOrder;
		}
		public void setCanOrder(String canOrder) {
			this.canOrder = canOrder;
		}
		public String getCanReturn() {
			return canReturn;
		}
		public void setCanReturn(String canReturn) {
			this.canReturn = canReturn;
		}
		public String getCanPurchase() {
			return canPurchase;
		}
		public void setCanPurchase(String canPurchase) {
			this.canPurchase = canPurchase;
		}
		public String getTaxCode() {
			return taxCode;
		}
		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}
		public String getCanRequire() {
			return canRequire;
		}
		public void setCanRequire(String canRequire) {
			this.canRequire = canRequire;
		}
		public String getMinQty() {
			return minQty;
		}
		public void setMinQty(String minQty) {
			this.minQty = minQty;
		}
		public String getMaxQty() {
			return maxQty;
		}
		public void setMaxQty(String maxQty) {
			this.maxQty = maxQty;
		}
		public String getMultiQty() {
			return multiQty;
		}
		public void setMultiQty(String multiQty) {
			this.multiQty = multiQty;
		}
		public String getCanRequireBack() {
			return canRequireBack;
		}
		public void setCanRequireBack(String canRequireBack) {
			this.canRequireBack = canRequireBack;
		}
		public String getIsAutoSubtract() {
			return isAutoSubtract;
		}
		public void setIsAutoSubtract(String isAutoSubtract) {
			this.isAutoSubtract = isAutoSubtract;
		}
		public String getCanEstimate() {
			return canEstimate;
		}
		public void setCanEstimate(String canEstimate) {
			this.canEstimate = canEstimate;
		}
		public String getClearType() {
			return clearType;
		}
		public void setClearType(String clearType) {
			this.clearType = clearType;
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
