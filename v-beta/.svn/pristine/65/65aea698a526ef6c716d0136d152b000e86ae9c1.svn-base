package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_MinQtyTemplateQuery
 * 服务说明：商品起售量模板查询
 * @author wangzyc 
 * @since  2020-11-10
 */
public class DCP_MinQtyTemplateQueryReq extends JsonBasicReq{
	
	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
	
	public class level1Elm{
		private String status;// 状态：-1未启用100已启用 0已禁用
		private String keyTxt; // 编码/名称模糊搜索
		private List<level2Elm> shop; // 适用门店
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKeyTxt() {
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt) {
			this.keyTxt = keyTxt;
		}
		public List<level2Elm> getShop() {
			return shop;
		}
		public void setShop(List<level2Elm> shop) {
			this.shop = shop;
		}
		
	}
	
	public class level2Elm{
		private String shopId; // 门店编码

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		
	}

}
