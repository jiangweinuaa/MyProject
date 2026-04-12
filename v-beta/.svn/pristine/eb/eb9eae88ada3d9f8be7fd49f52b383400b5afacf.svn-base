package com.dsc.spos.thirdpart.youzan.request;

/**
 * 交易关闭
 * 买家或卖家取消订单、订单全额退款
 * https://doc.youzanyun.com/doc#/content/MSG/20-交易消息/detail/msg/trade_TradeClose
 */

public class YouZanNotifyTradeCloseReq extends YouZanNotifyBasicReq {

	private String tid;//订单号
	private String update_time;//更新时间
	//关闭类型 0:未关闭;1:过期关闭;2:标记退款;3:订单取消;4:买家取消;5:卖家取消;
	//6:部分退款;10:无法联系上买家;11:买家误拍或重拍了;12:买家无诚意完成交易;
	//13:已通过银行线下汇款;14:已通过同城见面交易;15:已通过货到付款交易;
	//16:已通过网上银行直接汇款;17:已经缺货无法交易;18:扣款失败;
	private String close_type;
	//refund, order closed! 订单退款关单 
	//close by buyer, order canceled! 订单主动取消 
	//order expired closed by task, order canceled! 超时未付款系统关单
	private String close_reason;
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
	public String getClose_type() {
		return close_type;
	}
	public void setClose_type(String close_type) {
		this.close_type = close_type;
	}
	public String getClose_reason() {
		return close_reason;
	}
	public void setClose_reason(String close_reason) {
		this.close_reason = close_reason;
	}

}
