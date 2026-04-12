package com.dsc.spos.thirdpart.youzan.request;

import java.util.List;

import com.dsc.spos.thirdpart.youzan.YouZanPayTypeEnum;


/**
 * 买家付款完成创建消息
 * 触发场景
 * 买家付款且主订单状态为「等待商家发货」时触发 对于周期购订单，买家付款且主订单状态为「周期购待发货」 对于多人拼团订单，需要拼团成功，主订单状态为「等待商家发货」时触发
 * https://doc.youzanyun.com/doc#/content/MSG/20-交易消息/detail/msg/trade_TradeBuyerPay
 */

public class YouZanNotifyTradeBuyerPayReq extends YouZanNotifyBasicReq {

	private List<DeliveryOrder> delivery_order;//交易物流信息结构体
	private OrderPromotion order_promotion;//交易优惠信息结构体
	private List<RefundOrder> refund_order;//交易退款信息结构体
	private FullOrderInfo full_order_info=new FullOrderInfo();//交易基础信息结构体
	private QrInfo qr_info;//订单扫码收银信息
	
	public FullOrderInfo getFull_order_info() {
		return full_order_info;
	}

	public void setFull_order_info(FullOrderInfo full_order_info) {
		this.full_order_info = full_order_info;
	}

	public List<DeliveryOrder> getDelivery_order() {
		return delivery_order;
	}

	public void setDelivery_order(List<DeliveryOrder> delivery_order) {
		this.delivery_order = delivery_order;
	}

	public OrderPromotion getOrder_promotion() {
		return order_promotion;
	}

	public void setOrder_promotion(OrderPromotion order_promotion) {
		this.order_promotion = order_promotion;
	}

	public List<RefundOrder> getRefund_order() {
		return refund_order;
	}

	public void setRefund_order(List<RefundOrder> refund_order) {
		this.refund_order = refund_order;
	}

	public QrInfo getQr_info() {
		return qr_info;
	}

	public void setQr_info(QrInfo qr_info) {
		this.qr_info = qr_info;
	}

	/**
	 * 交易物流信息结构体
	 */
	public class DeliveryOrder{
		private String pk_id;//包裹id
		private String express_state;//物流状态 0:待发货; 1:已发货
		private String express_type;//物流类型 0:手动发货; 1:系统自动发货
		private List<Oid> oids;//物流类型 0:手动发货; 1:系统自动发货
		
		public String getPk_id() {
			return pk_id;
		}

		public void setPk_id(String pk_id) {
			this.pk_id = pk_id;
		}

		public String getExpress_state() {
			return express_state;
		}

		public void setExpress_state(String express_state) {
			this.express_state = express_state;
		}

		public String getExpress_type() {
			return express_type;
		}

		public void setExpress_type(String express_type) {
			this.express_type = express_type;
		}


		public List<Oid> getOids() {
			return oids;
		}

		public void setOids(List<Oid> oids) {
			this.oids = oids;
		}


		/**
		 * 交易明细发货信息
		 */
		public class Oid{
			private String oid;//交易明细号

			public String getOid() {
				return oid;
			}

			public void setOid(String oid) {
				this.oid = oid;
			}
		}
	}
	
	/**
	 * 交易优惠信息结构体
	 */
	public class OrderPromotion{
		private String adjust_fee;//订单改价金额
		private String item_discount_fee;//商品优惠总金额
		private String order_discount_fee;//订单优惠总金额
		
		private List<Item> item;//商品级优惠明细结构体
		private List<Order> order;//订单级优惠明细结构体
		public String getAdjust_fee() {
			return adjust_fee;
		}
		public void setAdjust_fee(String adjust_fee) {
			this.adjust_fee = adjust_fee;
		}
		public String getItem_discount_fee() {
			return item_discount_fee;
		}
		public void setItem_discount_fee(String item_discount_fee) {
			this.item_discount_fee = item_discount_fee;
		}
		public String getOrder_discount_fee() {
			return order_discount_fee;
		}
		public void setOrder_discount_fee(String order_discount_fee) {
			this.order_discount_fee = order_discount_fee;
		}
		public List<Item> getItem() {
			return item;
		}
		public void setItem(List<Item> item) {
			this.item = item;
		}
		public List<Order> getOrder() {
			return order;
		}
		public void setOrder(List<Order> order) {
			this.order = order;
		}
		/**
		 * 商品级优惠明细结构体
		 */
		public class Item{
			private String item_id;//商品id
			private String is_present;//是否赠品
			private String sku_id;//规格id
			private String oid;//交易明细id
			private Promotions promotions;//优惠明细结构体
			public String getItem_id() {
				return item_id;
			}
			public void setItem_id(String item_id) {
				this.item_id = item_id;
			}
			public String getIs_present() {
				return is_present;
			}
			public void setIs_present(String is_present) {
				this.is_present = is_present;
			}
			public String getSku_id() {
				return sku_id;
			}
			public void setSku_id(String sku_id) {
				this.sku_id = sku_id;
			}
			public String getOid() {
				return oid;
			}
			public void setOid(String oid) {
				this.oid = oid;
			}
			public Promotions getPromotions() {
				return promotions;
			}
			public void setPromotions(Promotions promotions) {
				this.promotions = promotions;
			}
			/**
			 * 优惠明细结构体
			 */
			public class Promotions{
				private String decrease;//优惠金额
				private String promotion_type;//优惠类型 枚举待补充
				private String promotion_type_name;//优惠类型描述-满减送
				private String promotion_title;//优惠别名-满减送活动
				private String promotion_type_id;//优惠类型id
				public String getDecrease() {
					return decrease;
				}
				public void setDecrease(String decrease) {
					this.decrease = decrease;
				}
				public String getPromotion_type() {
					return promotion_type;
				}
				public void setPromotion_type(String promotion_type) {
					this.promotion_type = promotion_type;
				}
				public String getPromotion_type_name() {
					return promotion_type_name;
				}
				public void setPromotion_type_name(String promotion_type_name) {
					this.promotion_type_name = promotion_type_name;
				}
				public String getPromotion_title() {
					return promotion_title;
				}
				public void setPromotion_title(String promotion_title) {
					this.promotion_title = promotion_title;
				}
				public String getPromotion_type_id() {
					return promotion_type_id;
				}
				public void setPromotion_type_id(String promotion_type_id) {
					this.promotion_type_id = promotion_type_id;
				}
			}
		}
		/**
		 * 订单级优惠明细结构体
		 */
		public class Order{
			private String promotion_type;//优惠类型 枚举待补充
			private String promotion_id;//优惠id
			private String coupon_id;//优惠券/码编号
			private String promotion_type_id;//优惠类型id
			private String promotion_type_name;//优惠类型描述
			private String promotion_condition;//优惠描述
			private String sub_promotion_type;//优惠子类型 card 优惠券 code 优惠码
			private String promotion_title;//优惠别名-满减送活动
			private String discount_fee;//优惠金额
			private String promotion_content;//优惠活动别名-满减送XX
			public String getPromotion_type() {
				return promotion_type;
			}
			public void setPromotion_type(String promotion_type) {
				this.promotion_type = promotion_type;
			}
			public String getPromotion_id() {
				return promotion_id;
			}
			public void setPromotion_id(String promotion_id) {
				this.promotion_id = promotion_id;
			}
			public String getCoupon_id() {
				return coupon_id;
			}
			public void setCoupon_id(String coupon_id) {
				this.coupon_id = coupon_id;
			}
			public String getPromotion_type_id() {
				return promotion_type_id;
			}
			public void setPromotion_type_id(String promotion_type_id) {
				this.promotion_type_id = promotion_type_id;
			}
			public String getPromotion_type_name() {
				return promotion_type_name;
			}
			public void setPromotion_type_name(String promotion_type_name) {
				this.promotion_type_name = promotion_type_name;
			}
			public String getPromotion_condition() {
				return promotion_condition;
			}
			public void setPromotion_condition(String promotion_condition) {
				this.promotion_condition = promotion_condition;
			}
			public String getSub_promotion_type() {
				return sub_promotion_type;
			}
			public void setSub_promotion_type(String sub_promotion_type) {
				this.sub_promotion_type = sub_promotion_type;
			}
			public String getPromotion_title() {
				return promotion_title;
			}
			public void setPromotion_title(String promotion_title) {
				this.promotion_title = promotion_title;
			}
			public String getDiscount_fee() {
				return discount_fee;
			}
			public void setDiscount_fee(String discount_fee) {
				this.discount_fee = discount_fee;
			}
			public String getPromotion_content() {
				return promotion_content;
			}
			public void setPromotion_content(String promotion_content) {
				this.promotion_content = promotion_content;
			}
		}
	}
	
	/**
	 * 交易退款信息结构体
	 */
	public class RefundOrder{
		
	}
	
	/**
	 * 交易基础信息结构体
	 */
	public class FullOrderInfo{
		private List<Orders> orders;//订单明细结构体  即商品列表
		private OrderInfo order_info=new OrderInfo();//交易明细详情 即订单单身
		private AddressInfo address_info;//订单收货地址信息结构体
		private RemarkInfo remark_info;//订单标记信息结构体
		private BuyerInfo buyer_info;//订单买家信息结构体
		private SourceInfo source_info;//订单来源
		private PayInfo pay_info;//交易支付信息结构体
		private ChildInfo child_info;//交易送礼子单
		private InvoiceInfo invoice_info;//交易送礼子单
		
		public InvoiceInfo getInvoice_info() {
			return invoice_info;
		}
		public void setInvoice_info(InvoiceInfo invoice_info) {
			this.invoice_info = invoice_info;
		}
		public List<Orders> getOrders() {
			return orders;
		}
		public void setOrders(List<Orders> orders) {
			this.orders = orders;
		}
		public OrderInfo getOrder_info() {
			return order_info;
		}
		public void setOrder_info(OrderInfo order_info) {
			this.order_info = order_info;
		}
		public AddressInfo getAddress_info() {
			return address_info;
		}
		public void setAddress_info(AddressInfo address_info) {
			this.address_info = address_info;
		}
		
		public RemarkInfo getRemark_info() {
			return remark_info;
		}
		public void setRemark_info(RemarkInfo remark_info) {
			this.remark_info = remark_info;
		}
		public BuyerInfo getBuyer_info() {
			return buyer_info;
		}
		public void setBuyer_info(BuyerInfo buyer_info) {
			this.buyer_info = buyer_info;
		}
		public SourceInfo getSource_info() {
			return source_info;
		}
		public void setSource_info(SourceInfo source_info) {
			this.source_info = source_info;
		}
		public PayInfo getPay_info() {
			return pay_info;
		}
		public void setPay_info(PayInfo pay_info) {
			this.pay_info = pay_info;
		}
		public ChildInfo getChild_info() {
			return child_info;
		}
		public void setChild_info(ChildInfo child_info) {
			this.child_info = child_info;
		}

		/**
		 * 订单明细结构体
		 * 即商品列表
		 */
		public class Orders{
			private String title;//商品名称
			private String num;//商品数量
			private String price;//单商品原价
			private String payment;//商品最终均摊价
			private String item_type;//订单类型 0:普通类型商品; 1:拍卖商品; 5:餐饮商品; 10:分销商品; 20:会员卡商品; 21:礼品卡商品; 23:有赞会议商品; 24:周期购; 30:收银台商品; 31:知识付费商品; 35:酒店商品; 40:普通服务类商品; 182:普通虚拟商品; 183:电子卡券商品; 201:外部会员卡商品; 202:外部直接收款商品; 203:外部普通商品; 205:mock不存在商品; 206:小程序二维码
			private String outer_item_id;//商品编码
			private String outer_sku_id;//商家编码
			private String pic_path;//商品图片地址
			private String sku_properties_name;//规格信息（无规格商品为空）
			private String buyer_messages;//商品留言（返回格式不统一，建议使用item_message）
			private String alias;//商品别名
			private String points_price;//商品积分价（非积分商品则为0）
			private String oid;//订单明细id
			private String sku_id;//规格id（无规格商品也会有值）
			private String is_present;//是否赠品
			private String total_fee;//商品优惠后总价
			private String item_id;//商品id
			private String goods_url;//商品详情链接
			private String discount_price;//单商品现价，减去了商品的优惠金额
			private String item_message;//商品留言。如果有留言，统一为json格式（因商家留言内容的多样性，具体分json对象格式，如：{"手机":"159xxxxxxxx","姓名":"张三"} 和json数组格式，如：["159xxxxxxxx","张三"] 两种，请开发者注意做好兼容)；如果没有留言，为空字符串
			private String sub_order_no;//报关单号
			private String tax_total;//税费
			private String freight;//运杂费
			private String customs_code;//海淘口岸编码
			private String cross_border_trade_mode;//海淘商品贸易模式
			private String is_cross_border;//是否海淘订单, 1是海淘
			private String discount;//非现金抵扣金额
			private String fenxiao_price;//分销价格
			private String fenxiao_payment;//分销实付金额
			private String fenxiao_freight;//分销运杂费
			private String fenxiao_discount;//分销非现金抵扣金额
			private String fenxiao_tax_total;//分销税费
			private String fenxiao_discount_price;//分销商品金额
			public String getTitle() {
				return title;
			}
			public void setTitle(String title) {
				this.title = title;
			}
			public String getNum() {
				return num;
			}
			public void setNum(String num) {
				this.num = num;
			}
			public String getPrice() {
				return price;
			}
			public void setPrice(String price) {
				this.price = price;
			}
			public String getPayment() {
				return payment;
			}
			public void setPayment(String payment) {
				this.payment = payment;
			}
			public String getItem_type() {
				return item_type;
			}
			public void setItem_type(String item_type) {
				this.item_type = item_type;
			}
			public String getOuter_item_id() {
				return outer_item_id;
			}
			public void setOuter_item_id(String outer_item_id) {
				this.outer_item_id = outer_item_id;
			}
			public String getOuter_sku_id() {
				return outer_sku_id;
			}
			public void setOuter_sku_id(String outer_sku_id) {
				this.outer_sku_id = outer_sku_id;
			}
			public String getPic_path() {
				return pic_path;
			}
			public void setPic_path(String pic_path) {
				this.pic_path = pic_path;
			}
			public String getSku_properties_name() {
				return sku_properties_name;
			}
			public void setSku_properties_name(String sku_properties_name) {
				this.sku_properties_name = sku_properties_name;
			}
			public String getBuyer_messages() {
				return buyer_messages;
			}
			public void setBuyer_messages(String buyer_messages) {
				this.buyer_messages = buyer_messages;
			}
			public String getAlias() {
				return alias;
			}
			public void setAlias(String alias) {
				this.alias = alias;
			}
			public String getPoints_price() {
				return points_price;
			}
			public void setPoints_price(String points_price) {
				this.points_price = points_price;
			}
			public String getOid() {
				return oid;
			}
			public void setOid(String oid) {
				this.oid = oid;
			}
			public String getSku_id() {
				return sku_id;
			}
			public void setSku_id(String sku_id) {
				this.sku_id = sku_id;
			}
			public String getIs_present() {
				return is_present;
			}
			public void setIs_present(String is_present) {
				this.is_present = is_present;
			}
			public String getTotal_fee() {
				return total_fee;
			}
			public void setTotal_fee(String total_fee) {
				this.total_fee = total_fee;
			}
			public String getItem_id() {
				return item_id;
			}
			public void setItem_id(String item_id) {
				this.item_id = item_id;
			}
			public String getGoods_url() {
				return goods_url;
			}
			public void setGoods_url(String goods_url) {
				this.goods_url = goods_url;
			}
			public String getDiscount_price() {
				return discount_price;
			}
			public void setDiscount_price(String discount_price) {
				this.discount_price = discount_price;
			}
			public String getItem_message() {
				return item_message;
			}
			public void setItem_message(String item_message) {
				this.item_message = item_message;
			}
			public String getSub_order_no() {
				return sub_order_no;
			}
			public void setSub_order_no(String sub_order_no) {
				this.sub_order_no = sub_order_no;
			}
			public String getTax_total() {
				return tax_total;
			}
			public void setTax_total(String tax_total) {
				this.tax_total = tax_total;
			}
			public String getFreight() {
				return freight;
			}
			public void setFreight(String freight) {
				this.freight = freight;
			}
			public String getCustoms_code() {
				return customs_code;
			}
			public void setCustoms_code(String customs_code) {
				this.customs_code = customs_code;
			}
			public String getCross_border_trade_mode() {
				return cross_border_trade_mode;
			}
			public void setCross_border_trade_mode(String cross_border_trade_mode) {
				this.cross_border_trade_mode = cross_border_trade_mode;
			}
			public String getIs_cross_border() {
				return is_cross_border;
			}
			public void setIs_cross_border(String is_cross_border) {
				this.is_cross_border = is_cross_border;
			}
			public String getDiscount() {
				return discount;
			}
			public void setDiscount(String discount) {
				this.discount = discount;
			}
			public String getFenxiao_price() {
				return fenxiao_price;
			}
			public void setFenxiao_price(String fenxiao_price) {
				this.fenxiao_price = fenxiao_price;
			}
			public String getFenxiao_payment() {
				return fenxiao_payment;
			}
			public void setFenxiao_payment(String fenxiao_payment) {
				this.fenxiao_payment = fenxiao_payment;
			}
			public String getFenxiao_freight() {
				return fenxiao_freight;
			}
			public void setFenxiao_freight(String fenxiao_freight) {
				this.fenxiao_freight = fenxiao_freight;
			}
			public String getFenxiao_discount() {
				return fenxiao_discount;
			}
			public void setFenxiao_discount(String fenxiao_discount) {
				this.fenxiao_discount = fenxiao_discount;
			}
			public String getFenxiao_tax_total() {
				return fenxiao_tax_total;
			}
			public void setFenxiao_tax_total(String fenxiao_tax_total) {
				this.fenxiao_tax_total = fenxiao_tax_total;
			}
			public String getFenxiao_discount_price() {
				return fenxiao_discount_price;
			}
			public void setFenxiao_discount_price(String fenxiao_discount_price) {
				this.fenxiao_discount_price = fenxiao_discount_price;
			}
		}
		
		/**
		 * 交易明细详情 即订单单身
		 */
		public class OrderInfo{
			private String tid;//订单号
			private String status_str;//主订单状态描述-已付款
			/**
			 * 主订单状态
			 * WAIT_BUYER_PAY （等待买家付款）； 
			 * TRADE_PAID（订单已支付 ）； 
			 * WAIT_CONFIRM（待确认，包含待成团、待接单等等。即：买家已付款，等待成团或等待接单）；
			 * WAIT_SELLER_SEND_GOODS（等待卖家发货，即：买家已付款）； 
			 * WAIT_BUYER_CONFIRM_GOODS (等待买家确认收货，即：卖家已发货) ；
			 * TRADE_SUCCESS（买家已签收以及订单成功）； 
			 * TRADE_CLOSED（交易关闭）；
			 * PS：TRADE_PAID状态仅代表当前订单已支付成功，表示瞬时状态，稍后会自动修改成后面的状态。如果不关心此状态请再次请求详情接口获取下一个状态。
			 */
			private String status;//
			 

			/**
			 * 主订单类型
			 * 0:普通订单; 
			 * 1:送礼订单; 
			 * 2:代付; 
			 * 3:分销采购单; 
			 * 4:赠品; 
			 * 5:心愿单; 
			 * 6:二维码订单; 
			 * 7:合并付货款; 
			 * 8:1分钱实名认证; 
			 * 9:品鉴; 
			 * 10:拼团; 
			 * 15:返利; 35:酒店; 40:外卖; 41:堂食点餐; 46:外卖买单; 
			 * 51:全员开店; 52:保证金; 61:线下收银台订单; 71:美业预约单; 
			 * 72:美业服务单; 75:知识付费; 81:礼品卡; 100:批发
			 */
			private String type;//
			/**
			 * 支付类型 
			 * 0:默认值,未支付; 
			 * 1:微信自有支付; 
			 * 2:支付宝wap; 3:支付宝wap; 
			 * 5:财付通; 7:代付; 8:联动优势; 
			 * 9:货到付款; 
			 * 10:大账号代销; 11:受理模式; 12:百付宝; 
			 * 13:sdk支付; 14:合并付货款; 15:赠品; 
			 * 16:优惠兑换; 17:自动付货款; 18:爱学贷; 
			 * 19:微信wap; 20:微信红包支付; 
			 * 21:返利; 22:ump红包; 24:易宝支付; 
			 * 25:储值卡; 
			 * 27:qq支付; 28:有赞E卡支付; 
			 * 29:微信条码; 
			 * 30:支付宝条码; 33:礼品卡支付; 
			 * 35:会员余额; 
			 * 72:微信扫码二维码支付; 100:代收账户; 
			 * 300:储值账户; 400:保证金账户; 101:收款码; 
			 * 102:微信; 103:支付宝; 104:刷卡; 
			 * 105:二维码台卡; 106:储值卡; 107:有赞E卡; 
			 * 110:标记收款-自有微信支付; 111:标记收款-自有支付宝; 
			 * 112:标记收款-自有POS刷卡; 113:通联刷卡支付; 200:记账账户; 201:现金
			 */
			private String pay_type;//
			private String pay_type_name;//
			/**
			 * 关闭类型 
			 * 0:未关闭; 1:过期关闭; 2:标记退款; 3:订单取消; 
			 * 4:买家取消; 5:卖家取消; 6:部分退款; 10:无法联系上买家; 
			 * 11:买家误拍或重拍了; 12:买家无诚意完成交易; 13:已通过银行线下汇款; 
			 * 14:已通过同城见面交易; 15:已通过货到付款交易; 
			 * 16:已通过网上银行直接汇款; 17:已经缺货无法交易
			 */
			private String close_type;
			private String expired_time;//订单过期时间（未付款将自动关单）-2018-01-01 00:00:00
			/**
			 * 退款状态 0:未退款; 1:部分退款中; 2:部分退款成功; 11:全额退款中; 12:全额退款成功
			 */
			private String express_type;
			/**
			 * 店铺类型 0:微商城; 1:微小店; 2:爱学贷微商城; 3:批发店铺; 4:批发商城; 5:外卖; 6:美业; 7:超级门店; 8:收银; 9:收银加微商城; 10:零售总部; 99:有赞开放平台平台型应用创建的店铺
			 */
			private String team_type;
			
			private String consign_time;//订单发货时间（当所有商品发货后才会更新）-2018-01-01 00:00:00
			private String update_time;//订单更新时间-2018-01-01 00:00:00
			private String offline_id;//网点id
			private String created;//订单创建时间-2018-01-01 00:00:00
			private String pay_time;//订单支付时间-2018-01-01 00:00:00
			private String confirm_time;//订单确认时间（多人拼团成团）-2018-01-01 00:00:00
			private String is_retail_order;//是否零售订单-false
			private String success_time;//订单成功时间-2018-01-01 00:00:00
			private OrderExtra order_extra;//订单扩展字段
			private OrderTags order_tags;//订单信息打标
			public String getPay_type_name() {
				return pay_type_name;
			}
			public void setPay_type_name(String pay_type_name) {
//				this.pay_type_name=pay_type_name;
			}
			public OrderExtra getOrder_extra() {
				return order_extra;
			}
			public void setOrder_extra(OrderExtra order_extra) {
				this.order_extra = order_extra;
			}
			public OrderTags getOrder_tags() {
				return order_tags;
			}
			public void setOrder_tags(OrderTags order_tags) {
				this.order_tags = order_tags;
			}
			public String getTid() {
				return tid;
			}
			public void setTid(String tid) {
				this.tid = tid;
			}
			public String getStatus_str() {
				return status_str;
			}
			public void setStatus_str(String status_str) {
				this.status_str = status_str;
			}
			public String getStatus() {
				return status;
			}
			public void setStatus(String status) {
				this.status = status;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getPay_type() {
				return pay_type;
			}
			public void setPay_type(String pay_type) {
				String payTypeName="";
				if(pay_type!=null&&pay_type.trim().length()>0){
					payTypeName=YouZanPayTypeEnum.getName(pay_type);
					if(payTypeName==null||payTypeName.trim().length()<1){
						payTypeName=pay_type;
					}
					this.pay_type_name=payTypeName;
				}
				this.pay_type = pay_type;
			}
			public String getClose_type() {
				return close_type;
			}
			public void setClose_type(String close_type) {
				this.close_type = close_type;
			}
			public String getExpired_time() {
				return expired_time;
			}
			public void setExpired_time(String expired_time) {
				this.expired_time = expired_time;
			}
			public String getExpress_type() {
				return express_type;
			}
			public void setExpress_type(String express_type) {
				this.express_type = express_type;
			}
			public String getTeam_type() {
				return team_type;
			}
			public void setTeam_type(String team_type) {
				this.team_type = team_type;
			}
			public String getConsign_time() {
				return consign_time;
			}
			public void setConsign_time(String consign_time) {
				this.consign_time = consign_time;
			}
			public String getUpdate_time() {
				return update_time;
			}
			public void setUpdate_time(String update_time) {
				this.update_time = update_time;
			}
			public String getOffline_id() {
				return offline_id;
			}
			public void setOffline_id(String offline_id) {
				this.offline_id = offline_id;
			}
			public String getCreated() {
				return created;
			}
			public void setCreated(String created) {
				this.created = created;
			}
			public String getPay_time() {
				return pay_time;
			}
			public void setPay_time(String pay_time) {
				this.pay_time = pay_time;
			}
			public String getConfirm_time() {
				return confirm_time;
			}
			public void setConfirm_time(String confirm_time) {
				this.confirm_time = confirm_time;
			}
			public String getIs_retail_order() {
				return is_retail_order;
			}
			public void setIs_retail_order(String is_retail_order) {
				this.is_retail_order = is_retail_order;
			}
			public String getSuccess_time() {
				return success_time;
			}
			public void setSuccess_time(String success_time) {
				this.success_time = success_time;
			}
			
			/**
			 * 订单扩展字段
			 */
			public class OrderExtra{
				private String is_from_cart;//是否来自购物车 是：true 不是：false
				private String cashier_id;//收银员id
				private String cashier_name;//收银员名字
				private String invoice_title;//发票抬头
				private String settle_time;//结算时间-1525844042082
				private String is_parent_order;//是否父单(分销合并订单) 是：1 其他：null
				private String is_sub_order;//是否子单(分销买家订单) 是：1 其他：null
				private String fx_order_no;//分销单订单号
				private String fx_kdt_id;//分销店铺id
				private String parent_order_no;//父单号
				private String purchase_order_no;//采购单号
				private String dept_id;//美业分店id
				private String create_device_id;//下单设备号
				private String is_points_order;//是否是积分订单
				private String id_card_number;//海淘身份证信息
				private String retail_pick_up_code;//零售特有字段
				private String retail_site_no;//零售特有字段
				private String id_card_name;//海淘身份证信息
				private String daogou;//导购员信息-{"name":"张三","id":"9408645"}
				private String fx_inner_transaction_no;//分销单外部支付流水号
				private String fx_outer_transaction_no;//分销单内部支付流水号
				private String merchant_customized_special_order;//ISV打标信息
				public String getIs_from_cart() {
					return is_from_cart;
				}
				public void setIs_from_cart(String is_from_cart) {
					this.is_from_cart = is_from_cart;
				}
				public String getCashier_id() {
					return cashier_id;
				}
				public void setCashier_id(String cashier_id) {
					this.cashier_id = cashier_id;
				}
				public String getCashier_name() {
					return cashier_name;
				}
				public void setCashier_name(String cashier_name) {
					this.cashier_name = cashier_name;
				}
				public String getInvoice_title() {
					return invoice_title;
				}
				public void setInvoice_title(String invoice_title) {
					this.invoice_title = invoice_title;
				}
				public String getSettle_time() {
					return settle_time;
				}
				public void setSettle_time(String settle_time) {
					this.settle_time = settle_time;
				}
				public String getIs_parent_order() {
					return is_parent_order;
				}
				public void setIs_parent_order(String is_parent_order) {
					this.is_parent_order = is_parent_order;
				}
				public String getIs_sub_order() {
					return is_sub_order;
				}
				public void setIs_sub_order(String is_sub_order) {
					this.is_sub_order = is_sub_order;
				}
				public String getFx_order_no() {
					return fx_order_no;
				}
				public void setFx_order_no(String fx_order_no) {
					this.fx_order_no = fx_order_no;
				}
				public String getFx_kdt_id() {
					return fx_kdt_id;
				}
				public void setFx_kdt_id(String fx_kdt_id) {
					this.fx_kdt_id = fx_kdt_id;
				}
				public String getParent_order_no() {
					return parent_order_no;
				}
				public void setParent_order_no(String parent_order_no) {
					this.parent_order_no = parent_order_no;
				}
				public String getPurchase_order_no() {
					return purchase_order_no;
				}
				public void setPurchase_order_no(String purchase_order_no) {
					this.purchase_order_no = purchase_order_no;
				}
				public String getDept_id() {
					return dept_id;
				}
				public void setDept_id(String dept_id) {
					this.dept_id = dept_id;
				}
				public String getCreate_device_id() {
					return create_device_id;
				}
				public void setCreate_device_id(String create_device_id) {
					this.create_device_id = create_device_id;
				}
				public String getIs_points_order() {
					return is_points_order;
				}
				public void setIs_points_order(String is_points_order) {
					this.is_points_order = is_points_order;
				}
				public String getId_card_number() {
					return id_card_number;
				}
				public void setId_card_number(String id_card_number) {
					this.id_card_number = id_card_number;
				}
				public String getRetail_pick_up_code() {
					return retail_pick_up_code;
				}
				public void setRetail_pick_up_code(String retail_pick_up_code) {
					this.retail_pick_up_code = retail_pick_up_code;
				}
				public String getRetail_site_no() {
					return retail_site_no;
				}
				public void setRetail_site_no(String retail_site_no) {
					this.retail_site_no = retail_site_no;
				}
				public String getId_card_name() {
					return id_card_name;
				}
				public void setId_card_name(String id_card_name) {
					this.id_card_name = id_card_name;
				}
				public String getDaogou() {
					return daogou;
				}
				public void setDaogou(String daogou) {
					this.daogou = daogou;
				}
				public String getFx_inner_transaction_no() {
					return fx_inner_transaction_no;
				}
				public void setFx_inner_transaction_no(String fx_inner_transaction_no) {
					this.fx_inner_transaction_no = fx_inner_transaction_no;
				}
				public String getFx_outer_transaction_no() {
					return fx_outer_transaction_no;
				}
				public void setFx_outer_transaction_no(String fx_outer_transaction_no) {
					this.fx_outer_transaction_no = fx_outer_transaction_no;
				}
				public String getMerchant_customized_special_order() {
					return merchant_customized_special_order;
				}
				public void setMerchant_customized_special_order(String merchant_customized_special_order) {
					this.merchant_customized_special_order = merchant_customized_special_order;
				}
			}
			/**
			 * 订单信息打标
			 */
			public class OrderTags{
			}
		}
		
		/**
		 * 订单收货地址信息结构体
		 */
		public class AddressInfo{
			private String receiver_name;//收货人姓名
			private String receiver_tel;//收货人手机号
			private String delivery_province;//省
			private String delivery_city;//市
			private String delivery_district;//区
			private String delivery_address;//详细地址
			private String delivery_postal_code;//邮政编码
			private String address_extra;//地址扩展信息（经纬度信息）-	{ln:23.43232,lat:9879.3443243}
			private String delivery_start_time;//同城送预计送达时间-开始时间 非同城送以及没有开启定时达的订单不返回-2018-01-01 00:00:00
			private String delivery_end_time;//同城送预计送达时间-结束时间 非同城送以及没有开启定时达的订单不返回-2018-01-01 00:00:00
			private String self_fetch_info;//到店自提信息 json格式-{}
			public String getReceiver_name() {
				return receiver_name;
			}
			public void setReceiver_name(String receiver_name) {
				this.receiver_name = receiver_name;
			}
			public String getReceiver_tel() {
				return receiver_tel;
			}
			public void setReceiver_tel(String receiver_tel) {
				this.receiver_tel = receiver_tel;
			}
			public String getDelivery_province() {
				return delivery_province;
			}
			public void setDelivery_province(String delivery_province) {
				this.delivery_province = delivery_province;
			}
			public String getDelivery_city() {
				return delivery_city;
			}
			public void setDelivery_city(String delivery_city) {
				this.delivery_city = delivery_city;
			}
			public String getDelivery_district() {
				return delivery_district;
			}
			public void setDelivery_district(String delivery_district) {
				this.delivery_district = delivery_district;
			}
			public String getDelivery_address() {
				return delivery_address;
			}
			public void setDelivery_address(String delivery_address) {
				this.delivery_address = delivery_address;
			}
			public String getDelivery_postal_code() {
				return delivery_postal_code;
			}
			public void setDelivery_postal_code(String delivery_postal_code) {
				this.delivery_postal_code = delivery_postal_code;
			}
			public String getAddress_extra() {
				return address_extra;
			}
			public void setAddress_extra(String address_extra) {
				this.address_extra = address_extra;
			}
			public String getDelivery_start_time() {
				return delivery_start_time;
			}
			public void setDelivery_start_time(String delivery_start_time) {
				this.delivery_start_time = delivery_start_time;
			}
			public String getDelivery_end_time() {
				return delivery_end_time;
			}
			public void setDelivery_end_time(String delivery_end_time) {
				this.delivery_end_time = delivery_end_time;
			}
			public String getSelf_fetch_info() {
				return self_fetch_info;
			}
			public void setSelf_fetch_info(String self_fetch_info) {
				this.self_fetch_info = self_fetch_info;
			}
		}
		
		/**
		 * 订单买家信息结构体
		 */
		public class BuyerInfo{
			private String buyer_id;//买家id
			private String fans_id;//粉丝id
			private String fans_type;//粉丝类型 1:自有粉丝; 9:代销粉丝
			private String fans_nickname;//粉丝昵称
			private String buyer_phone;//买家手机号
			private String outer_user_id;//外部用户ID
			private String yz_open_id;//有赞用户id，用户在有赞的唯一id。推荐使用-3hf8Fb8I596768747084054528
			public String getBuyer_id() {
				return buyer_id;
			}
			public void setBuyer_id(String buyer_id) {
				this.buyer_id = buyer_id;
			}
			public String getFans_id() {
				return fans_id;
			}
			public void setFans_id(String fans_id) {
				this.fans_id = fans_id;
			}
			public String getFans_type() {
				return fans_type;
			}
			public void setFans_type(String fans_type) {
				this.fans_type = fans_type;
			}
			public String getFans_nickname() {
				return fans_nickname;
			}
			public void setFans_nickname(String fans_nickname) {
				this.fans_nickname = fans_nickname;
			}
			public String getBuyer_phone() {
				return buyer_phone;
			}
			public void setBuyer_phone(String buyer_phone) {
				this.buyer_phone = buyer_phone;
			}
			public String getOuter_user_id() {
				return outer_user_id;
			}
			public void setOuter_user_id(String outer_user_id) {
				this.outer_user_id = outer_user_id;
			}
			public String getYz_open_id() {
				return yz_open_id;
			}
			public void setYz_open_id(String yz_open_id) {
				this.yz_open_id = yz_open_id;
			}
		}
		/**
		 * 订单来源
		 */
		public class SourceInfo{
			private String is_offline_order;//是否来自线下订单
			private String book_key;//下单唯一标识-H7jg6gdn73kLLdg75b3ns28cyemsYkdb
			private Source source;
			public String getIs_offline_order() {
				return is_offline_order;
			}
			public void setIs_offline_order(String is_offline_order) {
				this.is_offline_order = is_offline_order;
			}
			public String getBook_key() {
				return book_key;
			}
			public void setBook_key(String book_key) {
				this.book_key = book_key;
			}
			public Source getSource() {
				return source;
			}
			public void setSource(Source source) {
				this.source = source;
			}
			/**
			 * 订单来源平台
			 */
			public class Source{
				/**
				 * 平台 wx:微信; merchant_3rd:商家自有app; buyer_v:买家版; 
				 * browser:系统浏览器; alipay:支付宝;qq:腾讯QQ; wb:微博; other:其他
				 */
				private String platform;
				/**
				 * 微信平台细分 
				 * wx_gzh:微信公众号; yzdh:有赞大号; merchant_xcx:商家小程序; yzdh_xcx:有赞大号小程序; direct_buy:直接购买
				 */
				private String wx_entrance;
				public String getPlatform() {
					return platform;
				}
				public void setPlatform(String platform) {
					this.platform = platform;
				}
				public String getWx_entrance() {
					return wx_entrance;
				}
				public void setWx_entrance(String wx_entrance) {
					this.wx_entrance = wx_entrance;
				}
			}
		}
		/**
		 * 交易支付信息结构体
		 */
		public class PayInfo{
			private String payment;//最终支付价格 payment=orders.payment的总和-110.99
			private String post_fee;//邮费-10.00
			private String outer_transactions;//外部支付单号-[]
			private String total_fee;//优惠前商品总价-100.99
			private List<String> transaction;//有赞支付流水号-[]
			public String getPayment() {
				return payment;
			}
			public void setPayment(String payment) {
				this.payment = payment;
			}
			public String getPost_fee() {
				return post_fee;
			}
			public void setPost_fee(String post_fee) {
				this.post_fee = post_fee;
			}
			public String getOuter_transactions() {
				return outer_transactions;
			}
			public void setOuter_transactions(String outer_transactions) {
				this.outer_transactions = outer_transactions;
			}
			public String getTotal_fee() {
				return total_fee;
			}
			public void setTotal_fee(String total_fee) {
				this.total_fee = total_fee;
			}
			public List<String> getTransaction() {
				return transaction;
			}
			public void setTransaction(List<String> transaction) {
				this.transaction = transaction;
			}
		}
		/**
		 * 交易送礼子单
		 */
		public class ChildInfo{
			private String gift_no;//送礼编号
			private String gift_sign;
			public String getGift_no() {
				return gift_no;
			}
			public void setGift_no(String gift_no) {
				this.gift_no = gift_no;
			}
			public String getGift_sign() {
				return gift_sign;
			}
			public void setGift_sign(String gift_sign) {
				this.gift_sign = gift_sign;
			}//送礼标记
		}
		
		/**
		 * 订单标记信息结构体
		 */
		public class RemarkInfo{
			private String star;//订单标星等级 0-5
			private String trade_memo;//订单商家备注
			private String buyer_message;//订单买家留言
			public String getStar() {
				return star;
			}
			public void setStar(String star) {
				this.star = star;
			}
			public String getTrade_memo() {
				return trade_memo;
			}
			public void setTrade_memo(String trade_memo) {
				this.trade_memo = trade_memo;
			}
			public String getBuyer_message() {
				return buyer_message;
			}
			public void setBuyer_message(String buyer_message) {
				this.buyer_message = buyer_message;
			}
		}
		
		//发票信息
		public class InvoiceInfo{
			private String user_name;//发票抬头
			private String taxpayer_id;//税号
			private String raise_type;//抬头类型（个人/企业） * personal 个人 * enterprise 企业
			private String invoice_detail_type;//* 发票内容（明细/分类） * itemCategory 商品类别 * itemDetail 商品明细
			public String getUser_name() {
				return user_name;
			}
			public void setUser_name(String user_name) {
				this.user_name = user_name;
			}
			public String getTaxpayer_id() {
				return taxpayer_id;
			}
			public void setTaxpayer_id(String taxpayer_id) {
				this.taxpayer_id = taxpayer_id;
			}
			public String getRaise_type() {
				return raise_type;
			}
			public void setRaise_type(String raise_type) {
				this.raise_type = raise_type;
			}
			public String getInvoice_detail_type() {
				return invoice_detail_type;
			}
			public void setInvoice_detail_type(String invoice_detail_type) {
				this.invoice_detail_type = invoice_detail_type;
			}
			
		}
		
	}
	
	/**
	 * 订单扫码收银信息
	 */
	public class QrInfo{
		private String qr_id;//收款码ID
		private String qr_pay_id;//订单付款ID
		private String qr_name;//收款码名称-XX收款码
		public String getQr_id() {
			return qr_id;
		}
		public void setQr_id(String qr_id) {
			this.qr_id = qr_id;
		}
		public String getQr_pay_id() {
			return qr_pay_id;
		}
		public void setQr_pay_id(String qr_pay_id) {
			this.qr_pay_id = qr_pay_id;
		}
		public String getQr_name() {
			return qr_name;
		}
		public void setQr_name(String qr_name) {
			this.qr_name = qr_name;
		}
	}
	

}
