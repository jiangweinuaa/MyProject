package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_ShopGroupDeleteReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{


		private String shopGroupNo;
		private String shopGroupType;
		private String priority;
		private String shopChildren;
		private String childPriority;

		public String getShopGroupNo() {
			return shopGroupNo;
		}

		public void setShopGroupNo(String shopGroupNo) {
			this.shopGroupNo = shopGroupNo;
		}

		public String getShopChildren() {
			return shopChildren;
		}

		public void setShopChildren(String shopChildren) {
			this.shopChildren = shopChildren;
		}

		public String getShopGroupType() {
			return shopGroupType;
		}

		public void setShopGroupType(String shopGroupType) {
			this.shopGroupType = shopGroupType;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}

		public String getChildPriority() {
			return childPriority;
		}

		public void setChildPriority(String childPriority) {
			this.childPriority = childPriority;
		}
	}

}
