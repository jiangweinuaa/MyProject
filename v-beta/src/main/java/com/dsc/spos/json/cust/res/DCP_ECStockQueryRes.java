package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_ECStockGet
 * 服务说明：电商平台库存上下架查询
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String ecStockNo;
		private String createDate;
		private String createBy;
		private String createByName;
		private String modifyDate;
		private String modifyBy;
		private String modifyByName;
		private String accountDate;
		private String accountBy;
		private String accountByName;
		private String status;
		private String docType;
		private String opType;
		private String loadDocType;
		private String loadDocNo;
		private String loadDocShop;
		private String loadDocShopName;
		private String totQty;
		private List<level2Elm> goodsDatas;
		
		public String getEcStockNo() {
			return ecStockNo;
		}
		public void setEcStockNo(String ecStockNo) {
			this.ecStockNo = ecStockNo;
		}
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
		public String getCreateBy() {
			return createBy;
		}
		public void setCreateBy(String createBy) {
			this.createBy = createBy;
		}
		public String getCreateByName() {
			return createByName;
		}
		public void setCreateByName(String createByName) {
			this.createByName = createByName;
		}
		public String getModifyDate() {
			return modifyDate;
		}
		public void setModifyDate(String modifyDate) {
			this.modifyDate = modifyDate;
		}
		public String getModifyBy() {
			return modifyBy;
		}
		public void setModifyBy(String modifyBy) {
			this.modifyBy = modifyBy;
		}
		public String getModifyByName() {
			return modifyByName;
		}
		public void setModifyByName(String modifyByName) {
			this.modifyByName = modifyByName;
		}
		public String getAccountDate() {
			return accountDate;
		}
		public void setAccountDate(String accountDate) {
			this.accountDate = accountDate;
		}
		public String getAccountBy() {
			return accountBy;
		}
		public void setAccountBy(String accountBy) {
			this.accountBy = accountBy;
		}
		public String getAccountByName() {
			return accountByName;
		}
		public void setAccountByName(String accountByName) {
			this.accountByName = accountByName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getOpType() {
			return opType;
		}
		public void setOpType(String opType) {
			this.opType = opType;
		}
		public String getLoadDocType() {
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType) {
			this.loadDocType = loadDocType;
		}
		public String getLoadDocNo() {
			return loadDocNo;
		}
		public void setLoadDocNo(String loadDocNo) {
			this.loadDocNo = loadDocNo;
		}
		public String getLoadDocShop() {
			return loadDocShop;
		}
		public void setLoadDocShop(String loadDocShop) {
			this.loadDocShop = loadDocShop;
		}
		public String getLoadDocShopName() {
			return loadDocShopName;
		}
		public void setLoadDocShopName(String loadDocShopName) {
			this.loadDocShopName = loadDocShopName;
		}
		public String getTotQty() {
			return totQty;
		}
		public void setTotQty(String totQty) {
			this.totQty = totQty;
		}
		public List<level2Elm> getGoodsDatas() {
			return goodsDatas;
		}
		public void setGoodsDatas(List<level2Elm> goodsDatas) {
			this.goodsDatas = goodsDatas;
		}

	}

	public class level2Elm
	{
		private String item;
		private String pluNo;
		private String pluName;
		private String pluBarcode;
		private String unit;
		private String unitName;
		private String allQty;
		private List<level3Elm> stockDatas;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
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
		public String getPluBarcode() {
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode) {
			this.pluBarcode = pluBarcode;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getAllQty() {
			return allQty;
		}
		public void setAllQty(String allQty) {
			this.allQty = allQty;
		}
		public List<level3Elm> getStockDatas() {
			return stockDatas;
		}
		public void setStockDatas(List<level3Elm> stockDatas) {
			this.stockDatas = stockDatas;
		}

	}

	public class level3Elm
	{
		private String ecPlatformNo;
		private String ecPlatformName;
		private String qty;

		public String getEcPlatformNo() {
			return ecPlatformNo;
		}
		public void setEcPlatformNo(String ecPlatformNo) {
			this.ecPlatformNo = ecPlatformNo;
		}
		public String getEcPlatformName() {
			return ecPlatformName;
		}
		public void setEcPlatformName(String ecPlatformName) {
			this.ecPlatformName = ecPlatformName;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}

	}

}
