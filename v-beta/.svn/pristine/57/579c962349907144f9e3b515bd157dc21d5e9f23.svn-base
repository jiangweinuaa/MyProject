package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;
/**
 * 服务函数：DCP_GetGoodsGroup_Open
 * 服务说明：获取线上商品分组信息(含商品）
 * @author jinzma 
 * @since  2020-09-10
 */
public class DCP_GetGoodsGroup_OpenReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	
	public class levelElm{
    private String shopId;
		private String memberId;
		private String cardNo;
		private List<level1Elm>queryCondition;

		private String miniPrice;   // 最低价
		private String maxPrice;   // 最高价
		private String brand;   // 品牌
		private String searchString;   // 搜索(支持宝贝名称)
        private String orderType;   // 排序类型
        private String classType;//CLASSTYPE 分类类型：POS ONLINE,不传时默认查 ONLINE


        public String getClassType() {
			return classType;
		}

		public void setClassType(String classType) {
			this.classType = classType;
		}

		public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
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
		public List<level1Elm> getQueryCondition() {
			return queryCondition;
		}
		public void setQueryCondition(List<level1Elm> queryCondition) {
			this.queryCondition = queryCondition;
		}
		
	}
	public class level1Elm{
		private String groupId;
		private String goodsQty;

		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getGoodsQty() {
			return goodsQty;
		}
		public void setGoodsQty(String goodsQty) {
			this.goodsQty = goodsQty;
		}
	}
}
