package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：OrderCategorySync
 * 服务说明：外卖分类同步
 * @author jinzma 
 * @since  2019-03-12
 */
public class DCP_OrderCategorySyncReq extends JsonBasicReq  {

	private String[] loadDocType ;
	private String operType ;
	private List<level1categoryElm> categorydatas;
	private List<level1shopsElm> shopsdatas;

	public String[] getLoadDocType() {
		return loadDocType;
	}

	public void setLoadDocType(String[] loadDocType) {
		this.loadDocType = loadDocType;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public List<level1shopsElm> getShopsdatas() {
		return shopsdatas;
	}

	public void setShopsdatas(List<level1shopsElm> shopsdatas) {
		this.shopsdatas = shopsdatas;
	}

	public List<level1categoryElm> getCategorydatas() {
		return categorydatas;
	}

	public void setCategorydatas(List<level1categoryElm> categorydatas) {
		this.categorydatas = categorydatas;
	}

	public  class level1categoryElm
	{
		private String categoryNO ;
		private String categoryName;
		private String priority;
		private String newCategoryName;

		public String getCategoryNO() {
			return categoryNO;
		}

		public void setCategoryNO(String categoryNO) {
			this.categoryNO = categoryNO;
		}

		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}

		public String getNewCategoryName() {
			return newCategoryName;
		}

		public void setNewCategoryName(String newCategoryName) {
			this.newCategoryName = newCategoryName;
		}
	}

	public  class level1shopsElm
	{
		private String erpShopNO ;
		private String orderShopNO;
		private String orderShopName;

		public String getOrderShopNO() {
			return orderShopNO;
		}
		public void setOrderShopNO(String orderShopNO) {
			this.orderShopNO = orderShopNO;
		}
		public String getOrderShopName() {
			return orderShopName;
		}
		public void setOrderShopName(String orderShopName) {
			this.orderShopName = orderShopName;
		}
		public String getErpShopNO() {
			return erpShopNO;
		}
		public void setErpShopNO(String erpShopNO) {
			this.erpShopNO = erpShopNO;
		}


	}



}
