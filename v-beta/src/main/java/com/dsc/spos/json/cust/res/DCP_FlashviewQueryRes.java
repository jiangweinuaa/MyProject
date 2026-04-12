package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 轮播广告查询
 * @author yuanyy
 *
 */
public class DCP_FlashviewQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm{
		private String flashviewId;
		private String fileName;
		private String linkUrl;
		private String shopType;
		private String status;
		private String priority;
		private List<level2Elm> shops;
		public String getFlashviewId() {
			return flashviewId;
		}
		public void setFlashviewId(String flashviewId) {
			this.flashviewId = flashviewId;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getLinkUrl() {
			return linkUrl;
		}
		public void setLinkUrl(String linkUrl) {
			this.linkUrl = linkUrl;
		}
		public String getShopType() {
			return shopType;
		}
		public void setShopType(String shopType) {
			this.shopType = shopType;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
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


}
