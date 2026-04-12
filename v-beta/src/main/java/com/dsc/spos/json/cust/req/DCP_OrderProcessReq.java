package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 订单结案处理   OrderProcess 订单转销售  业务
 * @author 24480
 *
 */
public class DCP_OrderProcessReq extends JsonBasicReq {

	private String oEId;
	private String oShopId;
	private String o_opNO;
	private String[] docType;	
	private String orderNO;
	private String status;
	private String refundStatus;
	//是否清理缓存
	private String delRedis;
	//用于微商城结案
	private String delName;//配送/骑手名称
	private String delTelephone;//配送员电话
	private String deliveryNO;//配送单号
	private String requestId;

	private List<level2Pay> pay;

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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
	public String getDelRedis() {
		return delRedis;
	}
	public void setDelRedis(String delRedis) {
		this.delRedis = delRedis;
	}
	public String getDelName() {
		return delName;
	}
	public void setDelName(String delName) {
		this.delName = delName;
	}
	public String getDelTelephone() {
		return delTelephone;
	}
	public void setDelTelephone(String delTelephone) {
		this.delTelephone = delTelephone;
	}
	public String getDeliveryNO() {
		return deliveryNO;
	}
	public void setDeliveryNO(String deliveryNO) {
		this.deliveryNO = deliveryNO;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public List<level2Pay> getPay() {
		return pay;
	}
	public void setPay(List<level2Pay> pay) {
		this.pay = pay;
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
