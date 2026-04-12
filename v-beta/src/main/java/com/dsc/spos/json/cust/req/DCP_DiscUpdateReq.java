package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DiscUpdateDCP 說明：触屏折扣修改DCP 服务说明：触屏折扣修改DCP
 * 
 * @author jzma
 * @since 2017-03-03
 */
public class DCP_DiscUpdateReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{

		private List<level1Elm> datas;

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}

	}
	public class level1Elm {
		private String keyValue;
		private String priority;
		private List<level2Elm> shops;

		public String getKeyValue() {
			return keyValue;
		}

		public void setKeyValue(String keyValue) {
			this.keyValue = keyValue;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}

		public List<level2Elm> getShops() {
			return shops;
		}

		public void setShops(List<level2Elm> shops) {
			this.shops = shops;
		}
	}

	public class level2Elm {
		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
	}

}
