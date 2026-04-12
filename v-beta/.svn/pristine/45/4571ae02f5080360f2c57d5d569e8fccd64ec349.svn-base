package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 低消适用门店查询
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_MiniChargeShopUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String miniChargeNo;
		private List<level1Elm> datas;

		public String getMiniChargeNo() {
			return miniChargeNo;
		}
		public void setMiniChargeNo(String miniChargeNo) {
			this.miniChargeNo = miniChargeNo;
		}
		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}
	public  class level1Elm{
		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

	}
}
