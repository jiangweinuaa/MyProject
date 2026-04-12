package com.dsc.spos.waimai.dada;

import java.util.List;

public class DadaAddOrderModel 
{
	private String shop_no;
	private String origin_id;
	private String city_code;
	private double cargo_price;
	/**
	 * 是否需要垫付 1:是 0:否 (垫付订单金额，非运费)
	 */
	private int is_prepay = 0;
	private String receiver_name;
	private String receiver_address;
	private double receiver_lat;
	private double receiver_lng;
	private String receiver_phone;	
	private String callback;	
	
	
	/**
	 * 预约发单时间（预约时间unix时间戳(10位),精确到分;整分钟为间隔，并且需要至少提前5分钟预约，可以支持未来3天内的订单发预约单。）
	 */
	private int delay_publish_time;
	private String info;
	/**
	 * 订单商品类型：食品小吃-1,饮料-2,鲜花绿植-3,文印票务-8,便利店-9,水果生鲜-13,同城电商-19, 医药-20,蛋糕-21,酒品-24,小商品市场-25,服装-26,汽修零配-27,数码家电-28,小龙虾-29,个人-50,火锅-51,个护美妆-53、母婴-55,家居家纺-57,手机-59,家装-61,其他-5
	 */
	private int cargo_type = 1;
	/**
	 * 订单重量（单位：Kg）
	 */
	private double cargo_weight = 1.0;
	/**
	 * 订单商品数量
	 */
	private int cargo_num;
	
	private List<product> product_list;
	
	public String getShop_no()
	{
		return shop_no;
	}
	public void setShop_no(String shop_no)
	{
		this.shop_no = shop_no;
	}
	public void setCargo_price(double cargo_price) {
		this.cargo_price = cargo_price;
	}
	public void setReceiver_lat(double receiver_lat) {
		this.receiver_lat = receiver_lat;
	}
	public void setReceiver_lng(double receiver_lng) {
		this.receiver_lng = receiver_lng;
	}
	public void setOrigin_id(String origin_id) {
		this.origin_id = origin_id;
	}
	public String getOrigin_id() {
		return origin_id;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}
	public String getCity_code() {
		return city_code;
	}

	public void setCargo_price(Double cargo_price) {
		this.cargo_price = cargo_price;
	}
	public double getCargo_price() {
		return cargo_price;
	}

	public void setIs_prepay(int is_prepay) {
		this.is_prepay = is_prepay;
	}
	public int getIs_prepay() {
		return is_prepay;
	}

	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getReceiver_name() {
		return receiver_name;
	}

	public void setReceiver_address(String receiver_address) {
		this.receiver_address = receiver_address;
	}
	public String getReceiver_address() {
		return receiver_address;
	}

	public void setReceiver_lat(Double receiver_lat) {
		this.receiver_lat = receiver_lat;
	}
	public double getReceiver_lat() {
		return receiver_lat;
	}

	public void setReceiver_lng(Double receiver_lng) {
		this.receiver_lng = receiver_lng;
	}
	public double getReceiver_lng() {
		return receiver_lng;
	}

	public void setReceiver_phone(String receiver_phone) {
		this.receiver_phone = receiver_phone;
	}
	public String getReceiver_phone() {
		return receiver_phone;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getCallback() {
		return callback;
	}
	public int getDelay_publish_time()
	{
		return delay_publish_time;
	}
	public void setDelay_publish_time(int delay_publish_time)
	{
		this.delay_publish_time = delay_publish_time;
	}
	public String getInfo()
	{
		return info;
	}
	public void setInfo(String info)
	{
		this.info = info;
	}
	public int getCargo_type()
	{
		return cargo_type;
	}
	public void setCargo_type(int cargo_type)
	{
		this.cargo_type = cargo_type;
	}
	public double getCargo_weight()
	{
		return cargo_weight;
	}
	public void setCargo_weight(double cargo_weight)
	{
		this.cargo_weight = cargo_weight;
	}
	public int getCargo_num()
	{
		return cargo_num;
	}
	public void setCargo_num(int cargo_num)
	{
		this.cargo_num = cargo_num;
	}
	
	public List<product> getProduct_list()
	{
		return product_list;
	}
	public void setProduct_list(List<product> product_list)
	{
		this.product_list = product_list;
	}

	public static class product
	{
		private String sku_name;
		private String src_product_no;
		private double count;
		private String unit;
		public String getSku_name()
		{
			return sku_name;
		}
		public void setSku_name(String sku_name)
		{
			this.sku_name = sku_name;
		}
		public String getSrc_product_no()
		{
			return src_product_no;
		}
		public void setSrc_product_no(String src_product_no)
		{
			this.src_product_no = src_product_no;
		}
		
		public double getCount()
		{
			return count;
		}
		public void setCount(double count)
		{
			this.count = count;
		}
		public String getUnit()
		{
			return unit;
		}
		public void setUnit(String unit)
		{
			this.unit = unit;
		}
		
		
		
	}

}
