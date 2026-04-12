package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服务函数：DCP_GetAllGroup_Open
 * 服务说明：获取全部线上商品分组
 * @author jinzma 
 * @since  2020-09-25
 */
public class DCP_GetAllGroup_OpenReq extends JsonBasicReq{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String shopId;
		private String classType;
		public String getClassType() {
			return classType;
		}
		public void setClassType(String classType) {
			this.classType = classType;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

	}
}
