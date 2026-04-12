package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 商品品类查询	2018-10-15
 * @author yuanyy
 *
 */
public class DCP_GoodsCategoryQueryRes extends JsonRes {
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private String category;
		//private String categoryType;
		private String categoryName;
		private String upCategory;
		private String upCategoryName;
		private String topCategory;
		private String topCategoryName;
		private String downCategoryQty;
		private String categoryLevel;
		private String status;
        private String preFixCode;
		
		private String canSale;//可销售
		private String canFree;//可免单
		private String canStatistics;//可纳入营业额统计
		private String canOrder;//可预订
		private String canReturn;//可销退
		private String canRequire;//可要货
		private String canRequireBack;//可退仓
		private String canProduce;//可制作
		private String canPurchase;//可自采
		private String canWeight;//可称重
		private String canEstimate;//可预估
		private String canMinusSale;//可负库存销售：N-不可 Y-可
		private String clearType;//估清方式：N-不估清 PERIOD-当餐 DAY-当天

		private List<level2Elm> categoryName_lang;
		private List<level1Elm> children;
		
		private String categoryImageUrl; // 分类图片地址
		
		public String getCategoryImageUrl() {
			return categoryImageUrl;
		}
		public void setCategoryImageUrl(String categoryImageUrl) {
			this.categoryImageUrl = categoryImageUrl;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getUpCategory() {
			return upCategory;
		}
		public void setUpCategory(String upCategory) {
			this.upCategory = upCategory;
		}
		public String getUpCategoryName() {
			return upCategoryName;
		}
		public void setUpCategoryName(String upCategoryName) {
			this.upCategoryName = upCategoryName;
		}
		public String getTopCategory() {
			return topCategory;
		}
		public void setTopCategory(String topCategory) {
			this.topCategory = topCategory;
		}
		public String getTopCategoryName() {
			return topCategoryName;
		}
		public void setTopCategoryName(String topCategoryName) {
			this.topCategoryName = topCategoryName;
		}
		public String getDownCategoryQty() {
			return downCategoryQty;
		}
		public void setDownCategoryQty(String downCategoryQty) {
			this.downCategoryQty = downCategoryQty;
		}
		public String getCategoryLevel() {
			return categoryLevel;
		}
		public void setCategoryLevel(String categoryLevel) {
			this.categoryLevel = categoryLevel;
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
		public String getCanStatistics() {
			return canStatistics;
		}
		public void setCanStatistics(String canStatistics) {
			this.canStatistics = canStatistics;
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
		public String getCanRequire() {
			return canRequire;
		}
		public void setCanRequire(String canRequire) {
			this.canRequire = canRequire;
		}
		public String getCanRequireBack() {
			return canRequireBack;
		}
		public void setCanRequireBack(String canRequireBack) {
			this.canRequireBack = canRequireBack;
		}
		public String getCanProduce() {
			return canProduce;
		}
		public void setCanProduce(String canProduce) {
			this.canProduce = canProduce;
		}
		public String getCanPurchase() {
			return canPurchase;
		}
		public void setCanPurchase(String canPurchase) {
			this.canPurchase = canPurchase;
		}
		public String getCanWeight() {
			return canWeight;
		}
		public void setCanWeight(String canWeight) {
			this.canWeight = canWeight;
		}
		public String getCanEstimate() {
			return canEstimate;
		}
		public void setCanEstimate(String canEstimate) {
			this.canEstimate = canEstimate;
		}
		public String getCanMinusSale() {
			return canMinusSale;
		}
		public void setCanMinusSale(String canMinusSale) {
			this.canMinusSale = canMinusSale;
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
		public List<level2Elm> getCategoryName_lang() {
			return categoryName_lang;
		}
		public void setCategoryName_lang(List<level2Elm> categoryName_lang) {
			this.categoryName_lang = categoryName_lang;
		}
		public List<level1Elm> getChildren() {
				return children;
		}
		public void setChildren(List<level1Elm> children) {
			this.children = children;
		}


        public String getPreFixCode() {
            return preFixCode;
        }

        public void setPreFixCode(String preFixCode) {
            this.preFixCode = preFixCode;
        }
    }
	public class level2Elm{
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

}
