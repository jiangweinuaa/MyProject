package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
/**
 * 服務函數：MemoGetDCP
 *   說明：整单备注查询DCP
 * @author Jinzma 
 * @since  2018-10-31
 */
public class DCP_MemoQueryRes extends JsonRes {

	private List<level1Elm> datas;
	public class level1Elm{
		private String item;
		private String memo;
		private String priority;
		private String status;

		private List<level2Elm> shops;
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public List<level2Elm> getShops() {
			return shops;
		}
		public void setShops(List<level2Elm> shops) {
			this.shops = shops;
		}


	}
	public class level2Elm{
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

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}



}