package com.dsc.spos.waimai.dianwoda;
/**
 * 骑手完成配送前，上游接入方可对针对某个订单增加小费（默认禁用，若要开启可联系点我达运营）。
 * 调用条件
 * 订单状态：点我达骑手离店配送前（已下单、已接单、已到店）
 * 金额限制：总额不超过50元RMB；
 * 多次增加：1.同一个订单order_original_id，本次增加小费总金额会更新（覆盖）上次的小费总金额，且本次金额不可小于上次金额；2.本次增加金额，相比于上次金额，不可大于20元RMB。
 * @author Typingly
 *
 */
public class DianwodaOrderTipParam {

	/**
   * 商户订单编号
   */
  private String order_original_id;
  
  /**
   * 小费总金额，单位：分
   */
  private Long tip;

	public String getOrder_original_id() {
		return order_original_id;
	}

	public void setOrder_original_id(String order_original_id) {
		this.order_original_id = order_original_id;
	}

	public Long getTip() {
		return tip;
	}

	public void setTip(Long tip) {
		this.tip = tip;
	}

	
  
  
}
