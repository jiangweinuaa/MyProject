package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderPlatformGoodsQueryRes extends JsonRes 
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
		private String orderPluNO;
		private String orderPluName;
		private String orderCategoryNO;
		private String orderCategoryName;
		private String categoryNO;
		private String categoryName;
		private String pluNO;
		private String pluName;
		private String orderDescription;//商品描述
		private String orderImageUrl;//商品主图片
		private String orderUnit;//商品单位 
		private String orderPriority;//优先级
		private String resultMapping;//是否映射成功 Y/N
		private String resultMappingDescription;//映射结果描述
		private String materialID1;
		private String materialID2;
		private String materialID3;
		private String materialID4;
		private String materialID5;
		private String materialID6;
		private String materialID7;
		private String materialID8;
		private String materialID9;
		private String materialID10;

		private String material1;
		private String material2;
		private String material3;
		private String material4;
		private String material5;
		private String material6;
		private String material7;
		private String material8;
		private String material9;
		private String material10;	
		private String isAllTimeSell ;
		private String beginDate ;
		private String endDate ;
		private String sellWeek ;
		private String sellTime ;

		private String eId;//job用到
		private String loadDocType;//job用到
		private String shopId;//job用到
		private String orderShopNO;//平台门店ID
		private String spuOnShelf;//是否上架 job用到

		private List<level2Spec> specs;
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
		public String getIsAllTimeSell() {
			return isAllTimeSell;
		}
		public void setIsAllTimeSell(String isAllTimeSell) {
			this.isAllTimeSell = isAllTimeSell;
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
		public String getSellWeek() {
			return sellWeek;
		}
		public void setSellWeek(String sellWeek) {
			this.sellWeek = sellWeek;
		}
		public String getSellTime() {
			return sellTime;
		}
		public void setSellTime(String sellTime) {
			this.sellTime = sellTime;
		}
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
		public String getOrderDescription() {
			return orderDescription;
		}
		public void setOrderDescription(String orderDescription) {
			this.orderDescription = orderDescription;
		}
		public String getOrderImageUrl() {
			return orderImageUrl;
		}
		public void setOrderImageUrl(String orderImageUrl) {
			this.orderImageUrl = orderImageUrl;
		}
		public String getOrderUnit() {
			return orderUnit;
		}
		public void setOrderUnit(String orderUnit) {
			this.orderUnit = orderUnit;
		}
		public List<level2Spec> getSpecs() {
			return specs;
		}
		public void setSpecs(List<level2Spec> specs) {
			this.specs = specs;
		}
		public List<level2Attribute> getAttributes() {
			return attributes;
		}
		public void setAttributes(List<level2Attribute> attributes) {
			this.attributes = attributes;
		}
		public String getOrderPriority() {
			return orderPriority;
		}
		public void setOrderPriority(String orderPriority) {
			this.orderPriority = orderPriority;
		}
		public String getResultMapping() {
			return resultMapping;
		}
		public void setResultMapping(String resultMapping) {
			this.resultMapping = resultMapping;
		}
		public String getResultMappingDescription() {
			return resultMappingDescription;
		}
		public void setResultMappingDescription(String resultMappingDescription) {
			this.resultMappingDescription = resultMappingDescription;
		}
		public String getMaterialID1() {
			return materialID1;
		}
		public void setMaterialID1(String materialID1) {
			this.materialID1 = materialID1;
		}
		public String getMaterialID2() {
			return materialID2;
		}
		public void setMaterialID2(String materialID2) {
			this.materialID2 = materialID2;
		}
		public String getMaterialID3() {
			return materialID3;
		}
		public void setMaterialID3(String materialID3) {
			this.materialID3 = materialID3;
		}
		public String getMaterialID4() {
			return materialID4;
		}
		public void setMaterialID4(String materialID4) {
			this.materialID4 = materialID4;
		}
		public String getMaterialID5() {
			return materialID5;
		}
		public void setMaterialID5(String materialID5) {
			this.materialID5 = materialID5;
		}
		public String getMaterialID6() {
			return materialID6;
		}
		public void setMaterialID6(String materialID6) {
			this.materialID6 = materialID6;
		}
		public String getMaterialID7() {
			return materialID7;
		}
		public void setMaterialID7(String materialID7) {
			this.materialID7 = materialID7;
		}
		public String getMaterialID8() {
			return materialID8;
		}
		public void setMaterialID8(String materialID8) {
			this.materialID8 = materialID8;
		}
		public String getMaterialID9() {
			return materialID9;
		}
		public void setMaterialID9(String materialID9) {
			this.materialID9 = materialID9;
		}
		public String getMaterialID10() {
			return materialID10;
		}
		public void setMaterialID10(String materialID10) {
			this.materialID10 = materialID10;
		}
		public String getMaterial1() {
			return material1;
		}
		public void setMaterial1(String material1) {
			this.material1 = material1;
		}
		public String getMaterial2() {
			return material2;
		}
		public void setMaterial2(String material2) {
			this.material2 = material2;
		}
		public String getMaterial3() {
			return material3;
		}
		public void setMaterial3(String material3) {
			this.material3 = material3;
		}
		public String getMaterial4() {
			return material4;
		}
		public void setMaterial4(String material4) {
			this.material4 = material4;
		}
		public String getMaterial5() {
			return material5;
		}
		public void setMaterial5(String material5) {
			this.material5 = material5;
		}
		public String getMaterial6() {
			return material6;
		}
		public void setMaterial6(String material6) {
			this.material6 = material6;
		}
		public String getMaterial7() {
			return material7;
		}
		public void setMaterial7(String material7) {
			this.material7 = material7;
		}
		public String getMaterial8() {
			return material8;
		}
		public void setMaterial8(String material8) {
			this.material8 = material8;
		}
		public String getMaterial9() {
			return material9;
		}
		public void setMaterial9(String material9) {
			this.material9 = material9;
		}
		public String getMaterial10() {
			return material10;
		}
		public void setMaterial10(String material10) {
			this.material10 = material10;
		}
		public String getLoadDocType() {
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType) {
			this.loadDocType = loadDocType;
		}

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getOrderShopNO() {
			return orderShopNO;
		}
		public void setOrderShopNO(String orderShopNO) {
			this.orderShopNO = orderShopNO;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getSpuOnShelf() {
			return spuOnShelf;
		}
		public void setSpuOnShelf(String spuOnShelf) {
			this.spuOnShelf = spuOnShelf;
		}


	}

	public class level2Spec
	{
		private String orderSpecID;
		private String orderSpecName;
		private String orderPrice;//商品价格		
		private String orderStock;//库存量
		private String ordermaxStock;//库存量
		private String orderPackingFee;//包装费
		private String orderOnShelf;//是否上架
		private String pluBarcode;
		private String pluSpecName;
		private double netWeight;

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
		public String getOrderPrice() {
			return orderPrice;
		}
		public void setOrderPrice(String orderPrice) {
			this.orderPrice = orderPrice;
		}
		public String getOrderStock() {
			return orderStock;
		}
		public void setOrderStock(String orderStock) {
			this.orderStock = orderStock;
		}
		public String getOrderPackingFee() {
			return orderPackingFee;
		}
		public void setOrderPackingFee(String orderPackingFee) {
			this.orderPackingFee = orderPackingFee;
		}
		public String getOrderOnShelf() {
			return orderOnShelf;
		}
		public void setOrderOnShelf(String orderOnShelf) {
			this.orderOnShelf = orderOnShelf;
		}
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}
		public String getPluSpecName() {
			return pluSpecName;
		}
		public void setPluSpecName(String pluSpecName) {
			this.pluSpecName = pluSpecName;
		}
		public double getNetWeight() {
			return netWeight;
		}
		public void setNetWeight(double netWeight) {
			this.netWeight = netWeight;
		}
		public String getOrdermaxStock() {
			return ordermaxStock;
		}
		public void setOrdermaxStock(String ordermaxStock) {
			this.ordermaxStock = ordermaxStock;
		}



	}

	public class level2Attribute
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
