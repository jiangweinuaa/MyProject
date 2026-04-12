package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_OrderChargeAmtUpdate_OpenReq extends JsonBasicReq 
{
	private String requestId;
	private level1Elm request;

	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public  class level1Elm
	{
		private String eId;
		private String companyId;
		private String shopId;
		private String opNo;
		private String opName;
		private String orderNo;
		private String load_docType;
		private String chargeGetAmt;
		private List<level2Good> goods;
		private List<level2Pay> pay;

		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getCompanyId() {
			return companyId;
		}
		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getOpNo() {
			return opNo;
		}
		public void setOpNo(String opNo) {
			this.opNo = opNo;
		}
		public String getOpName() {
			return opName;
		}
		public void setOpName(String opName) {
			this.opName = opName;
		}
		public String getOrderNo() {
			return orderNo;
		}
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		public String getLoad_docType() {
			return load_docType;
		}
		public void setLoad_docType(String load_docType) {
			this.load_docType = load_docType;
		}
		public List<level2Good> getGoods() {
			return goods;
		}
		public void setGoods(List<level2Good> goods) {
			this.goods = goods;
		}
		public List<level2Pay> getPay() {
			return pay;
		}
		public void setPay(List<level2Pay> pay) {
			this.pay = pay;
		}
		public String getChargeGetAmt() {
			return chargeGetAmt;
		}
		public void setChargeGetAmt(String chargeGetAmt) {
			this.chargeGetAmt = chargeGetAmt;
		}



	}

	public  class level2Good
	{
		private String item;
		private String pluBarcode;
		private String pluName;
		private String specName;
		private String attrName;
		private String unit;
		private String qty;
		private String price;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
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
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}



	}

	public  class level2Pay
	{
		private String item;
		private String payCode;
		private String payCodeerp;
		private String payName;
		private String cardNO;
		private String ctType;
		private String paySernum;
		private String serialNO;
		private String refNO;
		private String teriminalNO;
		private String descore;
		private String pay;
		private String extra;
		private String changed;
		private String bdate;
		private String isOrderpay;

		private String isOnlinePay;
		private String order_PayCode;
		private String rcPay;

		private String payShop;//订金付款门店编号
		private String payLoadDocType;//订金付款来源类型


		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getPayCode() {
			return payCode;
		}
		public void setPayCode(String payCode) {
			this.payCode = payCode;
		}
		public String getPayCodeerp() {
			return payCodeerp;
		}
		public void setPayCodeerp(String payCodeerp) {
			this.payCodeerp = payCodeerp;
		}
		public String getPayName() {
			return payName;
		}
		public void setPayName(String payName) {
			this.payName = payName;
		}
		public String getCardNO() {
			return cardNO;
		}
		public void setCardNO(String cardNO) {
			this.cardNO = cardNO;
		}
		public String getCtType() {
			return ctType;
		}
		public void setCtType(String ctType) {
			this.ctType = ctType;
		}
		public String getPaySernum() {
			return paySernum;
		}
		public void setPaySernum(String paySernum) {
			this.paySernum = paySernum;
		}
		public String getSerialNO() {
			return serialNO;
		}
		public void setSerialNO(String serialNO) {
			this.serialNO = serialNO;
		}
		public String getRefNO() {
			return refNO;
		}
		public void setRefNO(String refNO) {
			this.refNO = refNO;
		}
		public String getTeriminalNO() {
			return teriminalNO;
		}
		public void setTeriminalNO(String teriminalNO) {
			this.teriminalNO = teriminalNO;
		}
		public String getDescore() {
			return descore;
		}
		public void setDescore(String descore) {
			this.descore = descore;
		}
		public String getPay() {
			return pay;
		}
		public void setPay(String pay) {
			this.pay = pay;
		}
		public String getExtra() {
			return extra;
		}
		public void setExtra(String extra) {
			this.extra = extra;
		}
		public String getChanged() {
			return changed;
		}
		public void setChanged(String changed) {
			this.changed = changed;
		}
		public String getBdate() {
			return bdate;
		}
		public void setBdate(String bdate) {
			this.bdate = bdate;
		}
		public String getIsOrderpay() {
			return isOrderpay;
		}
		public void setIsOrderpay(String isOrderpay) {
			this.isOrderpay = isOrderpay;
		}
		public String getIsOnlinePay() {
			return isOnlinePay;
		}
		public void setIsOnlinePay(String isOnlinePay) {
			this.isOnlinePay = isOnlinePay;
		}
		public String getOrder_PayCode() {
			return order_PayCode;
		}
		public void setOrder_PayCode(String order_PayCode) {
			this.order_PayCode = order_PayCode;
		}
		public String getRcPay() {
			return rcPay;
		}
		public void setRcPay(String rcPay) {
			this.rcPay = rcPay;
		}
		public String getPayShop() {
			return payShop;
		}
		public void setPayShop(String payShop) {
			this.payShop = payShop;
		}
		public String getPayLoadDocType() {
			return payLoadDocType;
		}
		public void setPayLoadDocType(String payLoadDocType) {
			this.payLoadDocType = payLoadDocType;
		}

	}
}
