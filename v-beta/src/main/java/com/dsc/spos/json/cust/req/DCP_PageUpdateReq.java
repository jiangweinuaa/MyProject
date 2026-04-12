package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_PageUpdateReq extends JsonBasicReq
{	
	private String type;
	private List<level2ElmShop> shops;
	private List<level2ElmPage> datas;

	public  class level2ElmShop
	{
		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

	}

	public  class level2ElmPage
	{
		private String pageID;
		private String pageType;
		private String pageIndex;
		private String pageName;
		private String status;

		public String getPageID() {
			return pageID;
		}
		public void setPageID(String pageID) {
			this.pageID = pageID;
		}

		public String getPageType() {
			return pageType;
		}
		public void setPageType(String pageType) {
			this.pageType = pageType;
		}

		public String getPageIndex() {
			return pageIndex;
		}
		public void setPageIndex(String pageIndex) {
			this.pageIndex = pageIndex;
		}

		public String getPageName() {
			return pageName;
		}
		public void setPageName(String pageName) {
			this.pageName = pageName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public List<level2ElmShop> getShops() {
		return shops;
	}
	public void setShops(List<level2ElmShop> shops) {
		this.shops = shops;
	}

	public List<level2ElmPage> getDatas() {
		return datas;
	}
	public void setDatas(List<level2ElmPage> datas) {
		this.datas = datas;
	}
}
