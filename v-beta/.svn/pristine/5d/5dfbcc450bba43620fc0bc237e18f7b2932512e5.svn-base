package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 服務函數：MemoUpdateDCP
 *    說明：整单备注修改
 * @author jzma 
 * @since  2018-10-31
 */
public class DCP_MemoUpdateReq extends JsonBasicReq  {

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




	public  class level1Elm
	{
		private String item;	
		private String memo;
		private String priority;		
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
		public List<level2Elm> getShops() {
			return shops;
		}
		public void setShops(List<level2Elm> shops) {
			this.shops = shops;
		}

	}
	public  class level2Elm
	{
		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
	}



}
