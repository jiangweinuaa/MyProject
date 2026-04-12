package com.dsc.spos.waimai.dianwoda;

public class DianwodaOrderCostEstimateParam {

  //----------------------------- 时效信息 ------------------------------------
  /**
   * 是否预约到店
   *
   * （1：是；0：否）
   */
  private Integer order_is_arrive_reserve;
  
  /**
   * 是否预约送达
   *
   * （1：是；0：否）
   */
  private Integer order_is_reserve;
  
  
  //----------------------------- 商家信息 ------------------------------------
  /**
   * 行政区划代码；详见1.4 行政区划代码查询
   */
  private String city_code;

  /**
   * 商家编号
   */
  private String seller_id;

  /**
   * 商家店铺名称
   */
  private String seller_name;

  /**
   * 商家联系方式
   */
  private String seller_mobile;

  /**
   * 商家文字地址（四级详细地址）
   */
  private String seller_address;

  /**
   * 商家纬度坐标.(坐标系为高德地图坐标系，又称火星坐标).（单位：度）
   */
  private Double seller_lat;

  /**
   * 商家经度坐标.(坐标系为高德地图坐标系，又称火星坐标).（单位：度）
   */
  private Double seller_lng;
  
  //----------------------------- 客人信息 ------------------------------------
 
  /**
   * 收货人地址（四级详细地址）
   */
  private String consignee_address;

  /**
   * 收货人纬度坐标.(坐标系为高德地图坐标系，又称火星坐标).（单位：度）
   */
  private Double consignee_lat;

  /**
   * 收货人经度坐标.(坐标系为高德地图坐标系，又称火星坐标).（单位：度）
   */
  private Double consignee_lng;
  
  
  //----------------------------- 订单货品信息 ------------------------------------
  /**
   * 订单商品类型
   *
   * 详见附录 - 商品类型编码；默认传00   08	鲜花，0801：盒装花，0802：裸花
   */
  private String cargo_type;

  /**
   * 订单商品重量，单位：克
   *
   * 如果无，默认传0
   */
  private Integer cargo_weight;

	public Integer getOrder_is_arrive_reserve() {
		return order_is_arrive_reserve;
	}

	public void setOrder_is_arrive_reserve(Integer order_is_arrive_reserve) {
		this.order_is_arrive_reserve = order_is_arrive_reserve;
	}

	public Integer getOrder_is_reserve() {
		return order_is_reserve;
	}

	public void setOrder_is_reserve(Integer order_is_reserve) {
		this.order_is_reserve = order_is_reserve;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public String getSeller_name() {
		return seller_name;
	}

	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}

	public String getSeller_mobile() {
		return seller_mobile;
	}

	public void setSeller_mobile(String seller_mobile) {
		this.seller_mobile = seller_mobile;
	}

	public String getSeller_address() {
		return seller_address;
	}

	public void setSeller_address(String seller_address) {
		this.seller_address = seller_address;
	}

	public Double getSeller_lat() {
		return seller_lat;
	}

	public void setSeller_lat(Double seller_lat) {
		this.seller_lat = seller_lat;
	}

	public Double getSeller_lng() {
		return seller_lng;
	}

	public void setSeller_lng(Double seller_lng) {
		this.seller_lng = seller_lng;
	}

	public String getConsignee_address() {
		return consignee_address;
	}

	public void setConsignee_address(String consignee_address) {
		this.consignee_address = consignee_address;
	}

	public Double getConsignee_lat() {
		return consignee_lat;
	}

	public void setConsignee_lat(Double consignee_lat) {
		this.consignee_lat = consignee_lat;
	}

	public Double getConsignee_lng() {
		return consignee_lng;
	}

	public void setConsignee_lng(Double consignee_lng) {
		this.consignee_lng = consignee_lng;
	}

	public String getCargo_type() {
		return cargo_type;
	}

	public void setCargo_type(String cargo_type) {
		this.cargo_type = cargo_type;
	}

	public Integer getCargo_weight() {
		return cargo_weight;
	}

	public void setCargo_weight(Integer cargo_weight) {
		this.cargo_weight = cargo_weight;
	}
  
  
  
}
