package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服务函数：DCP_GetMallGoodsRecommend_Open
 * 服务说明：获取线上商品推荐
 * @author jinzma 
 * @since  2020-09-27
 */
public class DCP_GetMallGoodsRecommend_OpenReq extends JsonBasicReq{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private level1Elm queryCondition;

		public level1Elm getQueryCondition() {
			return queryCondition;
		}

		public void setQueryCondition(level1Elm queryCondition) {
			this.queryCondition = queryCondition;
		}
	}
	public class level1Elm{
		private String shopId;
		private String memberId;
		private String cardNo;
		private String mallGoodsId;
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
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
		public String getMallGoodsId() {
			return mallGoodsId;
		}
		public void setMallGoodsId(String mallGoodsId) {
			this.mallGoodsId = mallGoodsId;
		}

	}
}
