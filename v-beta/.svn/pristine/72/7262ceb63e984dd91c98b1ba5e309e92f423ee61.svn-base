package com.dsc.spos.thirdpart.youzan.request;

import com.dsc.spos.thirdpart.youzan.YouZanRefundReasonEnum;
import com.dsc.spos.utils.Check;

/**
 * 退款或退货消息
 * https://doc.youzanyun.com/doc#/content/MSG/20-交易退款/detail/msg/trade_refund_RefundSuccess
 */

public class YouZanNotifyRefundSuccessReq extends YouZanNotifyBasicReq {

	private String tid;//订单号
	private String update_time;//更新时间
	private String refund_type;//REFUND_ONLY 仅退款 REFUND_AND_RETURN 退货退款
	private String refund_reason;//原因
	private String refund_reason_des;//原因描述
	private String oids;//交易明细id
	private String refund_id;//退款id
	private String refunded_fee;//退款金额
	public String getRefund_reason_des() {
		return refund_reason_des;
	}
	public void setRefund_reason_des(String refund_reason_des) {
		this.refund_reason_des = refund_reason_des;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getRefund_type() {
		return refund_type;
	}
	public void setRefund_type(String refund_type) {
		this.refund_type = refund_type;
	}
	public String getRefund_reason() {
		return refund_reason;
	}
	public void setRefund_reason(String refund_reason) {
		this.refund_reason = refund_reason;
		if(Check.Null(this.refund_reason_des) && !Check.Null(this.refund_reason)){
			this.refund_reason_des = YouZanRefundReasonEnum.getName(this.refund_reason);
		}
	}
	public String getOids() {
		return oids;
	}
	public void setOids(String oids) {
		this.oids = oids;
	}
	public String getRefund_id() {
		return refund_id;
	}
	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}
	public String getRefunded_fee() {
		return refunded_fee;
	}
	public void setRefunded_fee(String refunded_fee) {
		this.refunded_fee = refunded_fee;
	}

	
	

}
