package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/**
 * 服務函數：DualPlayCreateDCPReq 說明：双屏播放新增DCP 服务说明：双屏播放新增DCP
 * 
 * @author panjing
 * @since 2016-11-11
 */
public class DCP_DualPlayTemUpdateReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String templateNo;
		private String templateName;
		private String platformType;
		private String shopType;
		private String timeType;
		private String memo;
		private String pollTime;
		private String status;
		private List<level1Shop> shopList;
		private List<level1Time> timeList;
		private List<level1File> fileList;

		public String getTemplateNo() {
			return templateNo;
		}

		public void setTemplateNo(String templateNo) {
			this.templateNo = templateNo;
		}

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getPlatformType() {
			return platformType;
		}

		public void setPlatformType(String platformType) {
			this.platformType = platformType;
		}

		public String getShopType() {
			return shopType;
		}

		public void setShopType(String shopType) {
			this.shopType = shopType;
		}

		public String getTimeType() {
			return timeType;
		}

		public void setTimeType(String timeType) {
			this.timeType = timeType;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getPollTime() {
			return pollTime;
		}

		public void setPollTime(String pollTime) {
			this.pollTime = pollTime;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<level1Shop> getShopList() {
			return shopList;
		}

		public void setShopList(List<level1Shop> shopList) {
			this.shopList = shopList;
		}

		public List<level1Time> getTimeList() {
			return timeList;
		}

		public void setTimeList(List<level1Time> timeList) {
			this.timeList = timeList;
		}

		public List<level1File> getFileList() {
			return fileList;
		}

		public void setFileList(List<level1File> fileList) {
			this.fileList = fileList;
		}
	}
	public class level1Shop {
		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

	}

	public class level1Time {
		private String startTime;
		private String endTime;

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
	}
	public class level1File {
		private String fileName;
		private String priority;
        private String fileType;
		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }
    }

}
