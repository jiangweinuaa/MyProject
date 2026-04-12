package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderPlatformMappingGoodsCreateReq extends JsonBasicReq 
{

	private String docType;
	private String erpShopNO;
	private List<level1Elm> datas;

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getErpShopNO() {
		return erpShopNO;
	}

	public void setErpShopNO(String erpShopNO) {
		this.erpShopNO = erpShopNO;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	public  class level1Elm
	{
		private String orderPluNO;
		private String orderPluName;
		private String orderCategoryNO;
		private String orderCategoryName;
		private String pluBarcode;
		private String pluNO;
		private String pluName;
		private String category;
		private String categoryName;

		private String result;//为了更新映射前的商品
		private List<level2Elm> specs;
		private List<level2Elm> datas;
		private List<level2Attribute> attributes;
		public String getOrderPluNO() {
			return orderPluNO;
		}
		public void setOrderPluNO(String orderPluNO) {
			this.orderPluNO = orderPluNO;
		}
		public String getOrderPluName() {
			return orderPluName;
		}
		public void setOrderPluName(String orderPluName) {
			this.orderPluName = orderPluName;
		}	
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}

		public String getOrderCategoryNO() {
			return orderCategoryNO;
		}
		public void setOrderCategoryNO(String orderCategoryNO) {
			this.orderCategoryNO = orderCategoryNO;
		}
		public String getOrderCategoryName() {
			return orderCategoryName;
		}
		public void setOrderCategoryName(String orderCategoryName) {
			this.orderCategoryName = orderCategoryName;
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
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public List<level2Elm> getSpecs() {
			return specs;
		}
		public void setSpecs(List<level2Elm> specs) {
			this.specs = specs;
		}
		public List<level2Attribute> getAttributes() {
			return attributes;
		}
		public void setAttributes(List<level2Attribute> attributes) {
			this.attributes = attributes;
		}
		

	}

	public  class level2Elm
	{
		private String orderSpecID;
		private String orderSpecName;
		private String pluBarcode;
		private String pluName;
		private String pluSpecName;

		public String getOrderSpecID() {
			return orderSpecID;
		}
		public void setOrderSpecID(String orderSpecID) {
			this.orderSpecID = orderSpecID;
		}
		public String getOrderSpecName() {
			return orderSpecName;
		}
		public void setOrderSpecName(String orderSpecName) {
			this.orderSpecName = orderSpecName;
		}
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getPluSpecName() {
			return pluSpecName;
		}
		public void setPluSpecName(String pluSpecName) {
			this.pluSpecName = pluSpecName;
		}	

	}

	public  class level2Attribute
	{
		private String name;
		private List<String> details;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getDetails() {
			return details;
		}
	
		public void setDetails(List<String> details) {
			this.details = details;
		}


	}

}
