package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_SaleOrderQuery_OpenReq extends JsonBasicReq {
	
	private Level1Elm request;

	public Level1Elm getRequest() {
		return request;
	}

	public void setRequest(Level1Elm request) {
		this.request = request;
	}

	public  class Level1Elm {
		
		private String isMember;//是否只查询会员销售单 Y表示是 N或者不填表示不是
		
		private String cardNo;//会员卡号
		
		private String memberId;//会员ID
		
		private String saleNo;//销售单号
		
		private String sOrderTime;//开始时间
		
		private String eOrderTime;//结束时间
		
		private String eid;//企业编号

		private String shopId;//下单门店编码
		
		private String orderChannel;//来源渠道（不传查全部）：POS win版pos，POSANDROID   安卓版POS，WUXIANG 舞像云，XIAOYOU  晓柚，YOUZAN  有赞，
		
		private String isReturn;//是否是退单，Y是退单 N是原单，不传查全部

		private String sort;//企业编号

		public String getIsMember() {
			return isMember;
		}

		public void setIsMember(String isMember) {
			this.isMember = isMember;
		}

		public String getCardNo() {
			return cardNo;
		}

		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}

		public String getMemberId() {
			return memberId;
		}

		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}

		public String getSaleNo() {
			return saleNo;
		}

		public void setSaleNo(String saleNo) {
			this.saleNo = saleNo;
		}

		public String getsOrderTime() {
			return sOrderTime;
		}

		public void setsOrderTime(String sOrderTime) {
			this.sOrderTime = sOrderTime;
		}

		public String geteOrderTime() {
			return eOrderTime;
		}

		public void seteOrderTime(String eOrderTime) {
			this.eOrderTime = eOrderTime;
		}

		public String getEid() {
			return eid;
		}

		public void setEid(String eid) {
			this.eid = eid;
		}

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public String getOrderChannel() {
			return orderChannel;
		}

		public void setOrderChannel(String orderChannel) {
			this.orderChannel = orderChannel;
		}

		public String getIsReturn() {
			return isReturn;
		}

		public void setIsReturn(String isReturn) {
			this.isReturn = isReturn;
		}

		public String getSort() {
			return sort;
		}

		public void setSort(String sort) {
			this.sort = sort;
		}
	}
}
