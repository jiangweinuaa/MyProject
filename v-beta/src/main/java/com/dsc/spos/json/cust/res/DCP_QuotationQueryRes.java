package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_QuotationQueryRes extends JsonRes {
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm {
		private String quotationRecordNo;
		private String createTime;
		private String shopId;
		private String shopName;
		private String telephone;
		private String shopEmail;
		private String address;
		private String customName;
		private String mobile;
		private String customEmail;
		private String referees;
		private String remark;
		private String tot_Amt;
		private List<level2Elm> quotationGoods;
		public String getQuotationRecordNo() {
			return quotationRecordNo;
		}
		public void setQuotationRecordNo(String quotationRecordNo) {
			this.quotationRecordNo = quotationRecordNo;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getShopEmail() {
			return shopEmail;
		}
		public void setShopEmail(String shopEmail) {
			this.shopEmail = shopEmail;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getCustomName() {
			return customName;
		}
		public void setCustomName(String customName) {
			this.customName = customName;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getCustomEmail() {
			return customEmail;
		}
		public void setCustomEmail(String customEmail) {
			this.customEmail = customEmail;
		}
		public String getReferees() {
			return referees;
		}
		public void setReferees(String referees) {
			this.referees = referees;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		
		public String getTot_Amt() {
			return tot_Amt;
		}
		public void setTot_Amt(String tot_Amt) {
			this.tot_Amt = tot_Amt;
		}
		public List<level2Elm> getQuotationGoods() {
			return quotationGoods;
		}
		public void setQuotationGoods(List<level2Elm> quotationGoods) {
			this.quotationGoods = quotationGoods;
		}
		
	}
	

	public class level2Elm{
		private String pluNo;
		private String pluName;
		private String item;
		private String wunit;
		private String wunitName;
		private String price;
		private String qty;
		private String remark;
//		private String fileName;
		private String amt;
		private String imageFileName;
		
		private String pluBarcode;
		private String specName;
		private String attrName;
		private String unit;
		private String goodsGroup;
		private String isPackage;
		private String pClassNo;
		private String packageType;
		private String packageMitem;
		private String disc;
		
		private List<level2Elm> childGoodsList;
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getWunit() {
			return wunit;
		}
		public void setWunit(String wunit) {
			this.wunit = wunit;
		}
		public String getWunitName() {
			return wunitName;
		}
		public void setWunitName(String wunitName) {
			this.wunitName = wunitName;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
//		public String getFileName() {
//			return fileName;
//		}
//		public void setFileName(String fileName) {
//			this.fileName = fileName;
//		}
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
		public String getImageFileName() {
			return imageFileName;
		}
		public void setImageFileName(String imageFileName) {
			this.imageFileName = imageFileName;
		}
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}
		public String getSpecName() {
			return specName;
		}
		public void setSpecName(String specName) {
			this.specName = specName;
		}
		public String getAttrName() {
			return attrName;
		}
		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getGoodsGroup() {
			return goodsGroup;
		}
		public void setGoodsGroup(String goodsGroup) {
			this.goodsGroup = goodsGroup;
		}
		public String getIsPackage() {
			return isPackage;
		}
		public void setIsPackage(String isPackage) {
			this.isPackage = isPackage;
		}
		public String getpClassNo() {
			return pClassNo;
		}
		public void setpClassNo(String pClassNo) {
			this.pClassNo = pClassNo;
		}
		public String getPackageType() {
			return packageType;
		}
		public void setPackageType(String packageType) {
			this.packageType = packageType;
		}
		public String getPackageMitem() {
			return packageMitem;
		}
		public void setPackageMitem(String packageMitem) {
			this.packageMitem = packageMitem;
		}
		public String getDisc() {
			return disc;
		}
		public void setDisc(String disc) {
			this.disc = disc;
		}
		public List<level2Elm> getChildGoodsList() {
			return childGoodsList;
		}
		public void setChildGoodsList(List<level2Elm> childGoodsList) {
			this.childGoodsList = childGoodsList;
		}
		
		
	}
	
}
