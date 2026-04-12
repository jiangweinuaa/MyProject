package com.dsc.spos.waimai.dianwoda;

import java.util.List;

public class DianwodaOrderCreateParam {
	
	 //----------------------------- 订单基本信息 ------------------------------------
  /**
   * 渠道订单编号
   */
  private String order_original_id;

  /**
   * 渠道订单创建时间戳，毫秒级unix-timestamp
   */
  private Long order_create_time;

  /**
   * 订单备注
   */
  private String order_remark;

  /**
   * 订单流水号
   */
  private String serial_id;

  /**
   * 用于面单号识别，便于骑手后续扫码拣货，无字符格式限制，长度要求32位以内
   */
  private String waybillno;

  //----------------------------- 时效信息 ------------------------------------
  /**
   * 是否预约到店
   *
   * （1：是；0：否）
   */
  private Integer order_is_arrive_reserve;

  /**
   * 用户预约到店开始时间，毫秒级unix-timestamp
   */
  private Long time_reserve_arrive_start;

  /**
   * 用户预约到店结束时间，毫秒级unix-timestamp
   */
  private Long time_reserve_arrive_end;

  /**
   * 是否预约送达
   *
   * （1：是；0：否）
   */
  private Integer order_is_reserve;

  /**
   * 用户预约送达开始时间，毫秒级unix-timestamp
   */
  private Long time_reserve_deliver_start;

  /**
   * 用户预约送达结束时间，毫秒级unix-timestamp
   */
  private Long time_reserve_deliver_end;

  //----------------------------- 金额信息 ------------------------------------
  /**
   * 订单金额(分)
   */
  private Long order_price;

  //----------------------------- 商家信息 ------------------------------------
  /**
   * 行政区划代码；详见1.5 行政区划代码查询
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
   * 收货人姓名
   */
  private String consignee_name;

  /**
   * 收货人手机号码
   */
  private String consignee_mobile;

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
   * 详见附录 - 商品类型编码；默认传00
   */
  private String cargo_type;

  /**
   * 订单商品重量，单位：克
   *
   * 如果无，默认传0
   */
  private Integer cargo_weight;

  /**
   * 商品件数
   *
   * 默认传1；
   */
  private Integer cargo_num;

  /**
   * 商品信息
   */
  private List<DianwodaOrderCreateItem> items;

	public String getOrder_original_id() {
		return order_original_id;
	}

	public void setOrder_original_id(String order_original_id) {
		this.order_original_id = order_original_id;
	}

	public Long getOrder_create_time() {
		return order_create_time;
	}

	public void setOrder_create_time(Long order_create_time) {
		this.order_create_time = order_create_time;
	}

	public String getOrder_remark() {
		return order_remark;
	}

	public void setOrder_remark(String order_remark) {
		this.order_remark = order_remark;
	}

	public String getSerial_id() {
		return serial_id;
	}

	public void setSerial_id(String serial_id) {
		this.serial_id = serial_id;
	}

	public String getWaybillno() {
		return waybillno;
	}

	public void setWaybillno(String waybillno) {
		this.waybillno = waybillno;
	}

	public Integer getOrder_is_arrive_reserve() {
		return order_is_arrive_reserve;
	}

	public void setOrder_is_arrive_reserve(Integer order_is_arrive_reserve) {
		this.order_is_arrive_reserve = order_is_arrive_reserve;
	}

	public Long getTime_reserve_arrive_start() {
		return time_reserve_arrive_start;
	}

	public void setTime_reserve_arrive_start(Long time_reserve_arrive_start) {
		this.time_reserve_arrive_start = time_reserve_arrive_start;
	}

	public Long getTime_reserve_arrive_end() {
		return time_reserve_arrive_end;
	}

	public void setTime_reserve_arrive_end(Long time_reserve_arrive_end) {
		this.time_reserve_arrive_end = time_reserve_arrive_end;
	}

	public Integer getOrder_is_reserve() {
		return order_is_reserve;
	}

	public void setOrder_is_reserve(Integer order_is_reserve) {
		this.order_is_reserve = order_is_reserve;
	}

	public Long getTime_reserve_deliver_start() {
		return time_reserve_deliver_start;
	}

	public void setTime_reserve_deliver_start(Long time_reserve_deliver_start) {
		this.time_reserve_deliver_start = time_reserve_deliver_start;
	}

	public Long getTime_reserve_deliver_end() {
		return time_reserve_deliver_end;
	}

	public void setTime_reserve_deliver_end(Long time_reserve_deliver_end) {
		this.time_reserve_deliver_end = time_reserve_deliver_end;
	}

	public Long getOrder_price() {
		return order_price;
	}

	public void setOrder_price(Long order_price) {
		this.order_price = order_price;
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

	public String getConsignee_name() {
		return consignee_name;
	}

	public void setConsignee_name(String consignee_name) {
		this.consignee_name = consignee_name;
	}

	public String getConsignee_mobile() {
		return consignee_mobile;
	}

	public void setConsignee_mobile(String consignee_mobile) {
		this.consignee_mobile = consignee_mobile;
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

	public Integer getCargo_num() {
		return cargo_num;
	}

	public void setCargo_num(Integer cargo_num) {
		this.cargo_num = cargo_num;
	}

	public List<DianwodaOrderCreateItem> getItems() {
		return items;
	}

	public void setItems(List<DianwodaOrderCreateItem> items) {
		this.items = items;
	}
  
  
  

}
