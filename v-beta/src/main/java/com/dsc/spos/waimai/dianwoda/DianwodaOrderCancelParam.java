package com.dsc.spos.waimai.dianwoda;

public class DianwodaOrderCancelParam {

  /**
   * 渠道订单编号
   */
  private String order_original_id;

  /**
   * 取消类型:
   *
   * 0：用户取消；
   *
   * 1 ：商家取消；
   *
   * 2 ：客服取消；
   *
   * 3 ：系统取消；
   */
  private Integer cancel_type;

  /**
   * 取消订单原因
   */
  private String cancel_reason;

	public String getOrder_original_id() {
		return order_original_id;
	}

	public void setOrder_original_id(String order_original_id) {
		this.order_original_id = order_original_id;
	}

	public Integer getCancel_type() {
		return cancel_type;
	}

	public void setCancel_type(Integer cancel_type) {
		this.cancel_type = cancel_type;
	}

	public String getCancel_reason() {
		return cancel_reason;
	}

	public void setCancel_reason(String cancel_reason) {
		this.cancel_reason = cancel_reason;
	}
  
  
}
