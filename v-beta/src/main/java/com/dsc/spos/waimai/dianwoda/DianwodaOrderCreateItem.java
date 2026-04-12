package com.dsc.spos.waimai.dianwoda;

public class DianwodaOrderCreateItem {
	
	/**
   * 商品名称
   */
  private String item_name;

  /**
   * 商品单位
   */
  private String unit;

  /**
   * 商品数量
   */
  private Integer quantity;

  /**
   * 单位原价(分)
   *
   * 如果无，默认传0
   */
  private Long unit_price;

  /**
   * 单位折扣价(分)
   *
   * 若无折扣则传原价
   */
  private Long discount_price;

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Long getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(Long unit_price) {
		this.unit_price = unit_price;
	}

	public Long getDiscount_price() {
		return discount_price;
	}

	public void setDiscount_price(Long discount_price) {
		this.discount_price = discount_price;
	}
  
  

}
