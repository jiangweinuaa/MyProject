package com.dsc.spos.waimai.dianwoda;

/**
 * 修改订单备注信息；修改方式为覆盖更新原有订单备注
 * @author Typingly
 *
 */
public class DianwodaOrderRemarkUpdateParam {

  /**
   * 商户订单编号
   */
  private String order_original_id;
  
  /**
   * 新订单备注
   */
  private String order_info_content;

	public String getOrder_original_id() {
		return order_original_id;
	}

	public void setOrder_original_id(String order_original_id) {
		this.order_original_id = order_original_id;
	}

	public String getOrder_info_content() {
		return order_info_content;
	}

	public void setOrder_info_content(String order_info_content) {
		this.order_info_content = order_info_content;
	}
  
  
}
