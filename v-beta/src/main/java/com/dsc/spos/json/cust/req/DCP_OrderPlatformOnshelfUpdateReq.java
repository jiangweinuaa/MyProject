package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
/**
 * 服务函数：OrderPlatformOnshelfUpdate
 * 服务说明：第三方商品上下架更新
 * @author jinzma	 
 * @since  2019-03-14
 */
public class DCP_OrderPlatformOnshelfUpdateReq  extends JsonBasicReq  {

	private String isOnshelf;
	private String loadDocType;
	private String orderShopNO;
	private String oShopId;
	private List<level1goodsElm> goodsdatas;

	public String getIsOnshelf() {
		return isOnshelf;
	}
	public void setIsOnshelf(String isOnshelf) {
		this.isOnshelf = isOnshelf;
	}
	public String getLoadDocType() {
		return loadDocType;
	}
	public void setLoadDocType(String loadDocType) {
		this.loadDocType = loadDocType;
	}
	public String getOrderShopNO() {
		return orderShopNO;
	}
	public void setOrderShopNO(String orderShopNO) {
		this.orderShopNO = orderShopNO;
	}
	public List<level1goodsElm> getGoodsdatas() {
		return goodsdatas;
	}
	public void setGoodsdatas(List<level1goodsElm> goodsdatas) {
		this.goodsdatas = goodsdatas;
	}

	public String getoShopId() {
		return oShopId;
	}
	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}


	public  class level1goodsElm
	{
		private String orderPluNO;
		private String orderPluName;
		private String pluNO;
		private String pluName;
		private String categoryNO;
		private String categoryNAME; 
		private String orderCategoryNO;
		private String orderCategoryNAME;    
		private List<level2specElm> specDatas;

		public String getOrderPluNO() {
			return orderPluNO;
		}

		public void setOrderPluNO(String orderPluNO) {
			this.orderPluNO = orderPluNO;
		}

		public String getPluNO() {
			return pluNO;
		}

		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
		}

		public String getPluName() {
			return pluName;
		}

		public void setPluName(String pluName) {
			this.pluName = pluName;
		}

		public String getCategoryNO() {
			return categoryNO;
		}

		public void setCategoryNO(String categoryNO) {
			this.categoryNO = categoryNO;
		}

		public String getCategoryNAME() {
			return categoryNAME;
		}

		public void setCategoryNAME(String categoryNAME) {
			this.categoryNAME = categoryNAME;
		}

		public String getOrderPluName() {
			return orderPluName;
		}

		public void setOrderPluName(String orderPluName) {
			this.orderPluName = orderPluName;
		}

		public String getOrderCategoryNO() {
			return orderCategoryNO;
		}

		public void setOrderCategoryNO(String orderCategoryNO) {
			this.orderCategoryNO = orderCategoryNO;
		}

		public String getOrderCategoryNAME() {
			return orderCategoryNAME;
		}

		public void setOrderCategoryNAME(String orderCategoryNAME) {
			this.orderCategoryNAME = orderCategoryNAME;
		}

		public List<level2specElm> getSpecDatas() {
			return specDatas;
		}

		public void setSpecDatas(List<level2specElm> specDatas) {
			this.specDatas = specDatas;
		}

	}
	public  class level2specElm
	{
		private String specNO;
		private String specName;
		private String orderSpecNO;
		private String orderSpecName;
		private String price;
		private String stockQty;

		public String getSpecNO() {
			return specNO;
		}
		public void setSpecNO(String specNO) {
			this.specNO = specNO;
		}
		public String getSpecName() {
			return specName;
		}
		public void setSpecName(String specName) {
			this.specName = specName;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getStockQty() {
			return stockQty;
		}
		public void setStockQty(String stockQty) {
			this.stockQty = stockQty;
		}
		public String getOrderSpecNO() {
			return orderSpecNO;
		}
		public void setOrderSpecNO(String orderSpecNO) {
			this.orderSpecNO = orderSpecNO;
		}
		public String getOrderSpecName() {
			return orderSpecName;
		}
		public void setOrderSpecName(String orderSpecName) {
			this.orderSpecName = orderSpecName;
		}

	}


}
