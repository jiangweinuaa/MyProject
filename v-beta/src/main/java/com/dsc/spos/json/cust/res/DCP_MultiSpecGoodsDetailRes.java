package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsCreateReq.attr;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsCreateReq.attrValue;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsCreateReq.masterPluName_lang;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsCreateReq.subGoods;

public class DCP_MultiSpecGoodsDetailRes extends JsonBasicRes {
	
	private goodsSetDetail datas;
	
	public goodsSetDetail getDatas() {
		return datas;
	}
	public void setDatas(goodsSetDetail datas) {
		this.datas = datas;
	}

	public class goodsSetDetail
	{
		private String masterPluNo;
		private String masterPluName;
		private List<masterPluName_lang> masterPluName_lang;
		private String memo;
		private String status;
		private String attrGroupId;
		private String attrGroupName;
		private String minPrice;
		private String maxPrice;
		private List<attr> attrList;
		private List<subGoods> subGoodsList;
		public String getMasterPluNo() {
			return masterPluNo;
		}
		public void setMasterPluNo(String masterPluNo) {
			this.masterPluNo = masterPluNo;
		}
		public List<masterPluName_lang> getMasterPluName_lang() {
			return masterPluName_lang;
		}
		public void setMasterPluName_lang(List<masterPluName_lang> masterPluName_lang) {
			this.masterPluName_lang = masterPluName_lang;
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
		public List<attr> getAttrList() {
			return attrList;
		}
		public void setAttrList(List<attr> attrList) {
			this.attrList = attrList;
		}
		public List<subGoods> getSubGoodsList() {
			return subGoodsList;
		}
		public void setSubGoodsList(List<subGoods> subGoodsList) {
			this.subGoodsList = subGoodsList;
		}
		public String getMasterPluName() {
			return masterPluName;
		}
		public void setMasterPluName(String masterPluName) {
			this.masterPluName = masterPluName;
		}
		public String getMinPrice() {
			return minPrice;
		}
		public void setMinPrice(String minPrice) {
			this.minPrice = minPrice;
		}
		public String getMaxPrice() {
			return maxPrice;
		}
		public void setMaxPrice(String maxPrice) {
			this.maxPrice = maxPrice;
		}
				
		
	}
	
	public class masterPluName_lang
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
	
	public class attr
	{
		private String attrId;
		private String attrName;
		private String sortId;
		private List<attrValue> attrValueList;
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
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public List<attrValue> getAttrValueList() {
			return attrValueList;
		}
		public void setAttrValueList(List<attrValue> attrValueList) {
			this.attrValueList = attrValueList;
		}
					
	}
	
	public class attrValue
	{
		private String attrValueId;
		private String attrValueName;
		private String status;
		
		public String getAttrValueId() {
			return attrValueId;
		}
		public void setAttrValueId(String attrValueId) {
			this.attrValueId = attrValueId;
		}
		public String getAttrValueName() {
			return attrValueName;
		}
		public void setAttrValueName(String attrValueName) {
			this.attrValueName = attrValueName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}
	
	public class subGoods
	{
		private String pluNo;
		private String pluName;
		private String unit;	
		private String price;
		private String unitName;
		private String featureNo;
		private String featureName;		
		private String attrId1;
		private String attrValueId1;
		private String attrId2;
		private String attrValueId2;
		private String attrId3;
		private String attrValueId3;
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
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
		public String getAttrId1() {
			return attrId1;
		}
		public void setAttrId1(String attrId1) {
			this.attrId1 = attrId1;
		}
		public String getAttrValueId1() {
			return attrValueId1;
		}
		public void setAttrValueId1(String attrValueId1) {
			this.attrValueId1 = attrValueId1;
		}
		public String getAttrId2() {
			return attrId2;
		}
		public void setAttrId2(String attrId2) {
			this.attrId2 = attrId2;
		}
		public String getAttrValueId2() {
			return attrValueId2;
		}
		public void setAttrValueId2(String attrValueId2) {
			this.attrValueId2 = attrValueId2;
		}
		public String getAttrId3() {
			return attrId3;
		}
		public void setAttrId3(String attrId3) {
			this.attrId3 = attrId3;
		}
		public String getAttrValueId3() {
			return attrValueId3;
		}
		public void setAttrValueId3(String attrValueId3) {
			this.attrValueId3 = attrValueId3;
		}
		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}

		
		
	}

}
