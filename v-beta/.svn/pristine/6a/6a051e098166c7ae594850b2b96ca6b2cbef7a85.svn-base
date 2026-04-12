package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 会员支付： 调用 CRM ： MemberPay 接口
 * 
 * @author Huawei
 *
 */
public class DCP_MemberPay_OpenReq extends JsonBasicReq {
	private reqJson request;
	private List<levelPayElm> pay;

	public reqJson getRequest() {
		return request;
	}

	public void setRequest(reqJson request) {
		this.request = request;
	}

	public List<levelPayElm> getPay() {
		return pay;
	}

	public void setPay(List<levelPayElm> pay) {
		this.pay = pay;
	}


	public class reqJson {
		private String oEId;
		private String orderNo;
		private String srcBillType;
		private String srcBillNo;
		private String orderAmount;
		private String pointAmount;
		private String memberId;
		private String terminalId;
		private String orgType;
		private String orgId;
		private String inputType;
		private String orderppointget;
		private String cardPayAmount;
		private List<level1Elm> cards;
		private List<level2Elm> coupons;
		private List<level3Elm> payDetail;
		private List<level4Elm> goodsdetail;

		public String getOrderNo() {
			return orderNo;
		}

		public String getoEId() {
			return oEId;
		}

		public void setoEId(String oEId) {
			this.oEId = oEId;
		}

		public String getSrcBillType() {
			return srcBillType;
		}

		public String getSrcBillNo() {
			return srcBillNo;
		}

		public String getOrderAmount() {
			return orderAmount;
		}

		public String getPointAmount() {
			return pointAmount;
		}

		public String getMemberId() {
			return memberId;
		}

		public String getTerminalId() {
			return terminalId;
		}

		public String getOrgType() {
			return orgType;
		}

		public String getOrgId() {
			return orgId;
		}

		public String getInputType() {
			return inputType;
		}

		public String getOrderppointget() {
			return orderppointget;
		}

		public String getCardPayAmount() {
			return cardPayAmount;
		}

		public List<level1Elm> getCards() {
			return cards;
		}

		public List<level2Elm> getCoupons() {
			return coupons;
		}

		public List<level3Elm> getPayDetail() {
			return payDetail;
		}

		public List<level4Elm> getGoodsdetail() {
			return goodsdetail;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		public void setSrcBillType(String srcBillType) {
			this.srcBillType = srcBillType;
		}

		public void setSrcBillNo(String srcBillNo) {
			this.srcBillNo = srcBillNo;
		}

		public void setOrderAmount(String orderAmount) {
			this.orderAmount = orderAmount;
		}

		public void setPointAmount(String pointAmount) {
			this.pointAmount = pointAmount;
		}

		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}

		public void setTerminalId(String terminalId) {
			this.terminalId = terminalId;
		}

		public void setOrgType(String orgType) {
			this.orgType = orgType;
		}

		public void setOrgId(String orgId) {
			this.orgId = orgId;
		}

		public void setInputType(String inputType) {
			this.inputType = inputType;
		}

		public void setOrderppointget(String orderppointget) {
			this.orderppointget = orderppointget;
		}

		public void setCardPayAmount(String cardPayAmount) {
			this.cardPayAmount = cardPayAmount;
		}

		public void setCards(List<level1Elm> cards) {
			this.cards = cards;
		}

		public void setCoupons(List<level2Elm> coupons) {
			this.coupons = coupons;
		}

		public void setPayDetail(List<level3Elm> payDetail) {
			this.payDetail = payDetail;
		}

		public void setGoodsdetail(List<level4Elm> goodsdetail) {
			this.goodsdetail = goodsdetail;
		}
	}

	//cards 卡消费明细节点
	public class level1Elm {
		private String cardNo;
		private String amount;
		private String usePoint;
		private String getPoint;
		public String getCardNo() {
			return cardNo;
		}
		public String getAmount() {
			return amount;
		}
		public String getUsePoint() {
			return usePoint;
		}
		public String getGetPoint() {
			return getPoint;
		}
		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
		public void setUsePoint(String usePoint) {
			this.usePoint = usePoint;
		}
		public void setGetPoint(String getPoint) {
			this.getPoint = getPoint;
		}

	}

	//coupons 券消费明细节点
	public class level2Elm {
		private String couponCode;
		private String couponType;
		private String quantity;
		private String faceAmount;
		private String buyAmount;
		public String getCouponCode() {
			return couponCode;
		}
		public String getCouponType() {
			return couponType;
		}
		public String getQuantity() {
			return quantity;
		}
		public String getFaceAmount() {
			return faceAmount;
		}
		public String getBuyAmount() {
			return buyAmount;
		}
		public void setCouponCode(String couponCode) {
			this.couponCode = couponCode;
		}
		public void setCouponType(String couponType) {
			this.couponType = couponType;
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		public void setFaceAmount(String faceAmount) {
			this.faceAmount = faceAmount;
		}
		public void setBuyAmount(String buyAmount) {
			this.buyAmount = buyAmount;
		}

	}

	//payDetail 节点
	public class level3Elm {
		private String payType;
		private String payName;
		private String payAmount;
		private String nocode;
		private String quantity;
		private String trade_no;
		public String getPayType() {
			return payType;
		}
		public String getPayName() {
			return payName;
		}
		public String getPayAmount() {
			return payAmount;
		}
		public String getNocode() {
			return nocode;
		}
		public String getQuantity() {
			return quantity;
		}
		public String getTrade_no() {
			return trade_no;
		}
		public void setPayType(String payType) {
			this.payType = payType;
		}
		public void setPayName(String payName) {
			this.payName = payName;
		}
		public void setPayAmount(String payAmount) {
			this.payAmount = payAmount;
		}
		public void setNocode(String nocode) {
			this.nocode = nocode;
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		public void setTrade_no(String trade_no) {
			this.trade_no = trade_no;
		}

	}

	// goodsdetail 节点
	public class level4Elm {
		private String goods_category;
		private String goods_id;
		private String goods_name;
		private String price;
		private String quantity;
		private String amount;

		private String allowPoint;
		private String mallGoodsId;
		private String mallGoodsName;
		private String subGoodsId;
		public String getGoods_category() {
			return goods_category;
		}
		public String getGoods_id() {
			return goods_id;
		}
		public String getGoods_name() {
			return goods_name;
		}
		public String getPrice() {
			return price;
		}
		public String getQuantity() {
			return quantity;
		}
		public String getAmount() {
			return amount;
		}
		public String getAllowPoint() {
			return allowPoint;
		}
		public String getMallGoodsId() {
			return mallGoodsId;
		}
		public String getMallGoodsName() {
			return mallGoodsName;
		}
		public String getSubGoodsId() {
			return subGoodsId;
		}
		public void setGoods_category(String goods_category) {
			this.goods_category = goods_category;
		}
		public void setGoods_id(String goods_id) {
			this.goods_id = goods_id;
		}
		public void setGoods_name(String goods_name) {
			this.goods_name = goods_name;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
		public void setAllowPoint(String allowPoint) {
			this.allowPoint = allowPoint;
		}
		public void setMallGoodsId(String mallGoodsId) {
			this.mallGoodsId = mallGoodsId;
		}
		public void setMallGoodsName(String mallGoodsName) {
			this.mallGoodsName = mallGoodsName;
		}
		public void setSubGoodsId(String subGoodsId) {
			this.subGoodsId = subGoodsId;
		}

	}


	public class levelPayElm{
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
		private String payShop;
		private String payLoadDocType;
		private String rcPay;

		public String getItem() {
			return item;
		}
		public String getPayCode() {
			return payCode;
		}
		public String getPayCodeerp() {
			return payCodeerp;
		}
		public String getPayName() {
			return payName;
		}
		public String getCardNO() {
			return cardNO;
		}
		public String getCtType() {
			return ctType;
		}
		public String getPaySernum() {
			return paySernum;
		}
		public String getSerialNO() {
			return serialNO;
		}
		public String getRefNO() {
			return refNO;
		}
		public String getDescore() {
			return descore;
		}
		public String getPay() {
			return pay;
		}
		public String getExtra() {
			return extra;
		}
		public String getChanged() {
			return changed;
		}
		public String getBdate() {
			return bdate;
		}
		public String getIsOrderpay() {
			return isOrderpay;
		}
		public String getIsOnlinePay() {
			return isOnlinePay;
		}
		public String getOrder_PayCode() {
			return order_PayCode;
		}
		public String getPayShop() {
			return payShop;
		}
		public String getPayLoadDocType() {
			return payLoadDocType;
		}
		public String getRcPay() {
			return rcPay;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public void setPayCode(String payCode) {
			this.payCode = payCode;
		}
		public void setPayCodeerp(String payCodeerp) {
			this.payCodeerp = payCodeerp;
		}
		public void setPayName(String payName) {
			this.payName = payName;
		}
		public void setCardNO(String cardNO) {
			this.cardNO = cardNO;
		}
		public void setCtType(String ctType) {
			this.ctType = ctType;
		}
		public void setPaySernum(String paySernum) {
			this.paySernum = paySernum;
		}
		public void setSerialNO(String serialNO) {
			this.serialNO = serialNO;
		}
		public void setRefNO(String refNO) {
			this.refNO = refNO;
		}
		public void setDescore(String descore) {
			this.descore = descore;
		}
		public void setPay(String pay) {
			this.pay = pay;
		}
		public void setExtra(String extra) {
			this.extra = extra;
		}
		public void setChanged(String changed) {
			this.changed = changed;
		}
		public void setBdate(String bdate) {
			this.bdate = bdate;
		}
		public void setIsOrderpay(String isOrderpay) {
			this.isOrderpay = isOrderpay;
		}
		public void setIsOnlinePay(String isOnlinePay) {
			this.isOnlinePay = isOnlinePay;
		}
		public void setOrder_PayCode(String order_PayCode) {
			this.order_PayCode = order_PayCode;
		}
		public void setPayShop(String payShop) {
			this.payShop = payShop;
		}
		public void setPayLoadDocType(String payLoadDocType) {
			this.payLoadDocType = payLoadDocType;
		}
		public void setRcPay(String rcPay) {
			this.rcPay = rcPay;
		}
		public String getTeriminalNO() {
			return teriminalNO;
		}
		public void setTeriminalNO(String teriminalNO) {
			this.teriminalNO = teriminalNO;
		}

	}


}
