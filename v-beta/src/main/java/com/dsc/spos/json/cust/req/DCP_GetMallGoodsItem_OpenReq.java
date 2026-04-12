package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_GetMallGoodsItem_Open
 * 服务说明：获取线上商品子商品
 * @author jinzma
 * @since  2020-10-12
 */
public class DCP_GetMallGoodsItem_OpenReq extends JsonBasicReq{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	
	public class levelElm{
		private String mallGoodsId;
		private String shopId;
		private String memberId;
		private String cardNo;
		private String promCategory;
		private String promNo;
		private String cardTypeId;
		
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getMallGoodsId() {
			return mallGoodsId;
		}
		public void setMallGoodsId(String mallGoodsId) {
			this.mallGoodsId = mallGoodsId;
		}
		public String getMemberId() {
			return memberId;
		}
		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}
		public String getCardNo() {
			return cardNo;
		}
		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}
		public String getPromCategory() {
			return promCategory;
		}
		public void setPromCategory(String promCategory) {
			this.promCategory = promCategory;
		}
		public String getPromNo() {
			return promNo;
		}
		public void setPromNo(String promNo) {
			this.promNo = promNo;
		}
		public String getCardTypeId() {
			return cardTypeId;
		}
		public void setCardTypeId(String cardTypeId) {
			this.cardTypeId = cardTypeId;
		}
	}
}
