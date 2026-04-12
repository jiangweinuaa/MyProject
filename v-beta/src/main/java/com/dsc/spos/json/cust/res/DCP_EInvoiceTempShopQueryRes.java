package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_EInvoiceTempShopQueryRes extends JsonBasicRes {

	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String shopId;
		private String shopName;

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}		

	}
}
