package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_CategoryControlQueryRes extends JsonBasicRes {
	
	private level1Elm datas;
	
	public level1Elm getDatas() {
		return datas;
	}
	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String category;	
		private String categoryType;
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
		
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getCategoryType() {
			return categoryType;
		}
		public void setCategoryType(String categoryType) {
			this.categoryType = categoryType;
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
			
		
		
		
	}
	

}
