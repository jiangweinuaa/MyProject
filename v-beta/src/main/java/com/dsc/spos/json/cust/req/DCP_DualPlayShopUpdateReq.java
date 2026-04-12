package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DualPlayUpdateDCP 說明：双屏播放门店修改 服务说明：双屏播放门店修改
 * 
 * @author panjing
 * @since 2016-11-11
 */
public class DCP_DualPlayShopUpdateReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String dualPlayID;
		private List<level1Elm> datas;
		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
		public String getDualPlayID() {
			return dualPlayID;
		}
		public void setDualPlayID(String dualPlayID) {
			this.dualPlayID = dualPlayID;
		}

	}

	public class level1Elm {
		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

	}

}
