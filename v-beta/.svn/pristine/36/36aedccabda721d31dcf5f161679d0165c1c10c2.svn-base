package com.dsc.spos.waimai.dianwoda;
/**
 * 门店确认已出餐
 * 考虑部分门店自身出餐时间不定，门店确认已出餐后，调用接口同步出餐时间，可针对性地调整考核点我达准时率
 * 调用条件：下单成功后即可调用。
 * @author Typingly
 *
 */
public class DianwodaOrderMealdoneParam {

	/**
   * 商户订单编号
   */
  private String order_original_id;
  
  /**
   * 门店确认已出餐时间戳，毫秒级unix-timestamp
   */
  private Long time_meal_ready;

	public String getOrder_original_id() {
		return order_original_id;
	}

	public void setOrder_original_id(String order_original_id) {
		this.order_original_id = order_original_id;
	}

	public Long getTime_meal_ready() {
		return time_meal_ready;
	}

	public void setTime_meal_ready(Long time_meal_ready) {
		this.time_meal_ready = time_meal_ready;
	}
  
  
}
