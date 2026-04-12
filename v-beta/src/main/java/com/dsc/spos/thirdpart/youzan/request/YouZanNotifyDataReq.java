package com.dsc.spos.thirdpart.youzan.request;

/**
 * 3.0 接收有赞 消息推送
 * @author LN 08546
 */
public class YouZanNotifyDataReq {

	public YouZanNotifyDataReq() {
		
	}
	
	
	/**
	 * {"data":"{\"msg\":\"%7B%22delivery_order%22%3A%5B%5D%2C%22order_promotion%22%3A%7B%22item%22%3A%5B%5D%2C%22adjust_fee%22%3A%220.00%22%2C%22order%22%3A%5B%5D%7D%2C%22refund_order%22%3A%5B%5D%2C%22full_order_info%22%3A%7B%22address_info%22%3A%7B%22self_fetch_info%22%3A%22%22%2C%22delivery_address%22%3A%22%E4%BD%99%E6%9D%AD%E8%A1%97%E9%81%93%E6%B9%96%E5%B1%B1%E5%B8%9D%E6%99%AF%E6%B9%BE8-3%EF%BC%88%E4%B8%80%E6%9C%9F%EF%BC%89+%22%2C%22delivery_postal_code%22%3A%22311100%22%2C%22receiver_name%22%3A%22%E4%B8%87%E9%AB%98%E8%83%BD%22%2C%22delivery_province%22%3A%22%E6%B5%99%E6%B1%9F%E7%9C%81%22%2C%22delivery_city%22%3A%22%E6%9D%AD%E5%B7%9E%E5%B8%82%22%2C%22address_extra%22%3A%22%7B%5C%22areaCode%5C%22%3A%5C%22330110%5C%22%2C%5C%22lon%5C%22%3A119.94284313824166%2C%5C%22lat%5C%22%3A30.255021428080497%7D%22%2C%22delivery_district%22%3A%22%E4%BD%99%E6%9D%AD%E5%8C%BA%22%2C%22receiver_tel%22%3A%2213777806504%22%7D%2C%22remark_info%22%3A%7B%22buyer_message%22%3A%22%22%7D%2C%22pay_info%22%3A%7B%22outer_transactions%22%3A%5B%224200001143202106299508139464%22%5D%2C%22post_fee%22%3A%220.01%22%2C%22total_fee%22%3A%220.01%22%2C%22payment%22%3A%220.02%22%2C%22transaction%22%3A%5B%222106291137532621490497%22%5D%7D%2C%22buyer_info%22%3A%7B%22outer_user_id%22%3A%22oHIuuju181j6rPUdfJb9hs1Sb7_4%22%2C%22buyer_phone%22%3A%2213777806504%22%2C%22yz_open_id%22%3A%22cpXKxdn9714928271996825600%22%2C%22fans_type%22%3A9%2C%22buyer_id%22%3A12117343728%2C%22fans_nickname%22%3A%22%E8%80%81%E4%B8%87%E7%9C%9F%E5%B8%85%22%2C%22fans_id%22%3A317712890%7D%2C%22orders%22%3A%5B%7B%22is_cross_border%22%3A%22%22%2C%22outer_item_id%22%3A%22333%22%2C%22discount_price%22%3A%220.01%22%2C%22item_type%22%3A0%2C%22num%22%3A1%2C%22oid%22%3A%222813577170427838537%22%2C%22title%22%3A%22%E9%BC%8E%E6%8D%B7%22%2C%22fenxiao_payment%22%3A%220.00%22%2C%22item_message%22%3A%22%22%2C%22buyer_messages%22%3A%22%22%2C%22cross_border_trade_mode%22%3A%22%22%2C%22is_present%22%3Afalse%2C%22sub_order_no%22%3A%22%22%2C%22price%22%3A%220.01%22%2C%22fenxiao_price%22%3A%220.00%22%2C%22total_fee%22%3A%220.01%22%2C%22alias%22%3A%221y9b57xvlb58u%22%2C%22payment%22%3A%220.01%22%2C%22outer_sku_id%22%3A%22%22%2C%22goods_url%22%3A%22https%3A%2F%2Fh5.youzan.com%2Fv2%2Fshowcase%2Fgoods%3Falias%3D1y9b57xvlb58u%22%2C%22customs_code%22%3A%22%22%2C%22item_id%22%3A984967720%2C%22sku_properties_name%22%3A%22%5B%5D%22%2C%22sku_id%22%3A0%2C%22pic_path%22%3A%22https%3A%2F%2Fimg01.yzcdn.cn%2Fupload_files%2F2021%2F06%2F29%2FFk_yE4BlMP_QA5_X8R9HtExSpwXT.jpg%22%2C%22points_price%22%3A%220%22%7D%5D%2C%22source_info%22%3A%7B%22is_offline_order%22%3Afalse%2C%22book_key%22%3A%22231c952e-317d-4869-80ce-89598d4176ac%22%2C%22source%22%3A%7B%22platform%22%3A%22wx%22%2C%22wx_entrance%22%3A%22direct_buy%22%7D%7D%2C%22order_info%22%3A%7B%22consign_time%22%3A%22%22%2C%22order_extra%22%3A%7B%22is_from_cart%22%3A%22true%22%2C%22is_points_order%22%3A%220%22%7D%2C%22created%22%3A%222021-06-29+11%3A37%3A52%22%2C%22status_str%22%3A%22%E5%B7%B2%E6%94%AF%E4%BB%98%22%2C%22expired_time%22%3A%222021-06-29+12%3A37%3A52%22%2C%22success_time%22%3A%22%22%2C%22type%22%3A0%2C%22confirm_time%22%3A%22%22%2C%22tid%22%3A%22E20210629113750049600089%22%2C%22pay_time%22%3A%222021-06-29+11%3A37%3A57%22%2C%22update_time%22%3A%222021-06-29+11%3A37%3A58%22%2C%22is_retail_order%22%3Afalse%2C%22team_type%22%3A0%2C%22pay_type%22%3A10%2C%22refund_state%22%3A0%2C%22close_type%22%3A0%2C%22order_tags%22%3A%7B%22is_secured_transactions%22%3Atrue%2C%22is_payed%22%3Atrue%7D%2C%22express_type%22%3A0%2C%22status%22%3A%22TRADE_PAID%22%7D%7D%7D\",\"kdt_name\":\"云测试店铺8iL0v\",\"test\":false,\"sign\":\"d2a87565e9028971414bd85d785e7853\",\"sendCount\":1,\"type\":\"trade_TradePaid\",\"version\":1624937878,\"client_id\":\"7ec5aae16aac4b33f4\",\"mode\":1,\"kdt_id\":95908110,\"id\":\"E20210629113750049600089\",\"msg_id\":\"eee4c39e-d69f-4684-bc54-9b26333f898f\",\"root_kdt_id\":95908110,\"status\":\"TRADE_PAID\"}","topic":"trade_TradePaid"}
	 */

	private String topic;
	private Message message;//
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public class Message{
		private String topic;
		private String data;//
		public String getTopic() {
			return topic;
		}
		public void setTopic(String topic) {
			this.topic = topic;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
	}
}
