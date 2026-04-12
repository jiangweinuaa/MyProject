package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务OrderStatementUpdate ,对账更新
 * @author 08546
 */
public class DCP_OrderStatementUpdateReq extends JsonBasicReq 
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		/**
		 * 
		 */
		private List<Data> datas;

		public List<Data> getDatas() {
			return datas;
		}

		public void setDatas(List<Data> datas) {
			this.datas = datas;
		}
	}
	public class Data
	{
		private String eId;
		private String shopId;
		private String thirdShop;
		private String orderNo;
		private String orderType;
		private String thirdType;
		private String diversityReason;

		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getThirdShop() {
			return thirdShop;
		}
		public void setThirdShop(String thirdShop) {
			this.thirdShop = thirdShop;
		}
		public String getOrderNo() {
			return orderNo;
		}
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		public String getOrderType() {
			return orderType;
		}
		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}
		public String getThirdType() {
			return thirdType;
		}
		public void setThirdType(String thirdType) {
			this.thirdType = thirdType;
		}
		public String getDiversityReason() {
			return diversityReason;
		}
		public void setDiversityReason(String diversityReason) {
			this.diversityReason = diversityReason;
		}
	}

}
