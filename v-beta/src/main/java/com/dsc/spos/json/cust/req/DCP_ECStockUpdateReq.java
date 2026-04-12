package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DCP_ECStockUpdate
 * 服务说明：电商平台库存上下架修改
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockUpdateReq  extends JsonBasicReq {

	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm
	{
		private String ecStockNo;
		private String docType;
		private String opType;
		private String loadDocType;
		private String loadDocNo;
		private String loadDocShop;
		private String totQty;
		private List<level2Elm> goodsDatas;

		public String getEcStockNo() {
			return ecStockNo;
		}
		public void setEcStockNo(String ecStockNo) {
			this.ecStockNo = ecStockNo;
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
		private String pluBarcode;
		private String unit;
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
