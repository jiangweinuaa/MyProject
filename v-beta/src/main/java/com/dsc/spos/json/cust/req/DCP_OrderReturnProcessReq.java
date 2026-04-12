package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 订单退订处理
 * @author yuanyy
 *
 */
public class DCP_OrderReturnProcessReq extends JsonBasicReq {
	private String oEId;
	private String oShopId;
	private String o_opNO;
	private String[] docType;	
	private String orderNO;
	private String reason;
	private String reasonCode;
	private String refundReason;
	private String refundReasonDesc;
	//是否原路退回
	private String refund;
	private String requestId;

	private List<level1Elm> payDetail;

	public String getoEId() {
		return oEId;
	}
	public void setoEId(String oEId) {
		this.oEId = oEId;
	}
	public String getoShopId() {
		return oShopId;
	}
	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}
	public String getO_opNO() {
		return o_opNO;
	}
	public void setO_opNO(String o_opNO) {
		this.o_opNO = o_opNO;
	}
	public String[] getDocType() {
		return docType;
	}
	public void setDocType(String[] docType) {
		this.docType = docType;
	}
	public String getOrderNO() {
		return orderNO;
	}
	public void setOrderNO(String orderNO) {
		this.orderNO = orderNO;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	public String getRefund() {
		return refund;
	}
	public void setRefund(String refund) {
		this.refund = refund;
	}

	public List<level1Elm> getPayDetail() {
		return payDetail;
	}
	public void setPayDetail(List<level1Elm> payDetail) {
		this.payDetail = payDetail;
	}

	public String getRefundReason() {
		return refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public String getRefundReasonDesc() {
		return refundReasonDesc;
	}
	public void setRefundReasonDesc(String refundReasonDesc) {
		this.refundReasonDesc = refundReasonDesc;
	}

	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public  class level1Elm
	{
		private String payCode;
		private String payName;

		private String payCodeerp;
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

		public String getPayCode() {
			return payCode;
		}
		public void setPayCode(String payCode) {
			this.payCode = payCode;
		}
		public String getPayName() {
			return payName;
		}
		public void setPayName(String payName) {
			this.payName = payName;
		}
		public String getPayCodeerp() {
			return payCodeerp;
		}
		public void setPayCodeerp(String payCodeerp) {
			this.payCodeerp = payCodeerp;
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
