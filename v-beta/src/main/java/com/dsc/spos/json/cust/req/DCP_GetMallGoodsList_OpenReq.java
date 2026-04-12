package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
/**
 * 服务函数：DCP_GetMallGoodsList_Open
 * 服务说明：获取线上商品列表
 * @author jinzma
 * @since  2020-09-27
 */
public class DCP_GetMallGoodsList_OpenReq extends JsonBasicReq{
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
		private String searchString;
		private String shopId;
		private String goodsGroupId;
		private String sort;
		private String queryType;
		private String memberId;
		private String cardNo;
		private String barCode;
		private String miniPrice;
		private String maxPrice;
		private String brand;
		private String orderType;
		private String groupType;
        private String isCollect;
		private List<level2Elm> mallGoodsList;
		private String classType;
		
		public String getClassType() {
			return classType;
		}
		public void setClassType(String classType) {
			this.classType = classType;
		}
		public String getSearchString() {
			return searchString;
		}
		public void setSearchString(String searchString) {
			this.searchString = searchString;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getGoodsGroupId() {
			return goodsGroupId;
		}
		public void setGoodsGroupId(String goodsGroupId) {
			this.goodsGroupId = goodsGroupId;
		}
		public String getSort() {
			return sort;
		}
		public void setSort(String sort) {
			this.sort = sort;
		}
		public String getQueryType() {
			return queryType;
		}
		public void setQueryType(String queryType) {
			this.queryType = queryType;
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
		public List<level2Elm> getMallGoodsList() {
			return mallGoodsList;
		}
		public void setMallGoodsList(List<level2Elm> mallGoodsList) {
			this.mallGoodsList = mallGoodsList;
		}
		public String getBarCode() {
			return barCode;
		}
		public void setBarCode(String barCode) {
			this.barCode = barCode;
		}
		public String getMiniPrice() {
			return miniPrice;
		}
		public void setMiniPrice(String miniPrice) {
			this.miniPrice = miniPrice;
		}
		public String getMaxPrice() {
			return maxPrice;
		}
		public void setMaxPrice(String maxPrice) {
			this.maxPrice = maxPrice;
		}
		public String getBrand() {
			return brand;
		}
		public void setBrand(String brand) {
			this.brand = brand;
		}
		public String getOrderType() {
			return orderType;
		}
		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}
		public String getGroupType() {
			return groupType;
		}
		public void setGroupType(String groupType) {
			this.groupType = groupType;
		}
		public String getIsCollect() {
			return isCollect;
		}
		public void setIsCollect(String isCollect) {
			this.isCollect = isCollect;
		}
	}
	
	public class level2Elm{
		private String mallGoodsId;
		
		public String getMallGoodsId() {
			return mallGoodsId;
		}
		public void setMallGoodsId(String mallGoodsId) {
			this.mallGoodsId = mallGoodsId;
		}
	}
}
