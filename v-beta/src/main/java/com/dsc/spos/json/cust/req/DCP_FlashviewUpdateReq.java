package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 轮播广告
 * @author yuanyy
 *
 */
public class DCP_FlashviewUpdateReq extends JsonBasicReq {
//	private String timeStamp;
	private level1Elm request;
//	public String getTimeStamp() {
//		return timeStamp;
//	}
//	public void setTimeStamp(String timeStamp) {
//		this.timeStamp = timeStamp;
//	}
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
	
	public class level1Elm{
		private String flashviewId;
		private String fileName;
		private String fileData;
		
		private String linkUrl;
		private String shopType;
		private String status;
		private String priority;
		private String toPriority;
		
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

		public String getFileData() {
			return fileData;
		}

		public void setFileData(String fileData) {
			this.fileData = fileData;
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

		public String getToPriority() {
			return toPriority;
		}

		public void setToPriority(String toPriority) {
			this.toPriority = toPriority;
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

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		
		
	}
	
}
