package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

import java.util.List;

/**
 * 服務函數：DualPlayGetDCP
 *   說明：双屏播放查询
 * 服务说明：双屏播放查询
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayTemDetailRes extends JsonBasicRes {

	private level1Elm datas;

	public level1Elm getDatas() {
		return datas;
	}

	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String templateNo;
		private String templateName;
		private String platformType;
		private String shopType;
		private String timeType;
		private String pollTime;
		private String status;
		private String memo;
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

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
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
		private String fileUrl;
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

		public String getFileUrl() {
			return fileUrl;
		}

		public void setFileUrl(String fileUrl) {
			this.fileUrl = fileUrl;
		}

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }
    }
}
