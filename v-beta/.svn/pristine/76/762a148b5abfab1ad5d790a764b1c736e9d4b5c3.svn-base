package com.dsc.spos.thirdpart.youzan.response;

import com.dsc.spos.thirdpart.youzan.YouZanPayTypeEnum;


/**
 * 买家付款完成创建消息
 * 触发场景
 * 买家付款且主订单状态为「等待商家发货」时触发 对于周期购订单，买家付款且主订单状态为「周期购待发货」 对于多人拼团订单，需要拼团成功，主订单状态为「等待商家发货」时触发
 * https://doc.youzanyun.com/doc#/content/MSG/20-交易消息/detail/msg/trade_TradeBuyerPay
 */

public class YouZanTradeGetRes extends YouZanBasicRes  {

	private FullOrderInfo full_order_info=new FullOrderInfo();//交易基础信息结构体
	
	public FullOrderInfo getFull_order_info() {
		return full_order_info;
	}

	public void setFull_order_info(FullOrderInfo full_order_info) {
		this.full_order_info = full_order_info;
	}


	
	/**
	 * 交易基础信息结构体
	 */
	public class FullOrderInfo{
		private OrderInfo order_info=new OrderInfo();//交易明细详情 即订单单身
		
		public OrderInfo getOrder_info() {
			return order_info;
		}

		public void setOrder_info(OrderInfo order_info) {
			this.order_info = order_info;
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
			private String selffetch_code;//自提码
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
			public String getSelffetch_code() {
				return selffetch_code;
			}
			public void setSelffetch_code(String selffetch_code) {
				this.selffetch_code = selffetch_code;
			}
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
		
	}

}
