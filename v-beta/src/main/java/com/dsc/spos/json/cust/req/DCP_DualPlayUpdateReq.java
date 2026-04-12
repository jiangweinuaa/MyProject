package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DualPlayUpdateDCP 說明：双屏播放修改DCP 服务说明：双屏播放修改DCP
 * 
 * @author panjing
 * @since 2016-11-11
 */
public class DCP_DualPlayUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String dualPlayID;
		private String platformType;
		private String fileName;
		private String shopType;
		private String timeType;
		private String memo;
		private String status;
		private String pollTime;
		private String fileData;// base64
		private List<level1Elm> shops;
		private List<level2Elm> times;

		public String getDualPlayID() {
			return dualPlayID;
		}

		public void setDualPlayID(String dualPlayID) {
			this.dualPlayID = dualPlayID;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
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

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getPollTime() {
			return pollTime;
		}

		public void setPollTime(String pollTime) {
			this.pollTime = pollTime;
		}

		public String getFileData() {
			return fileData;
		}

		public void setFileData(String fileData) {
			this.fileData = fileData;
		}

		public List<level1Elm> getShops() {
			return shops;
		}

		public void setShops(List<level1Elm> shops) {
			this.shops = shops;
		}

		public List<level2Elm> getTimes() {
			return times;
		}

		public void setTimes(List<level2Elm> times) {
			this.times = times;
		}

		public String getPlatformType() {
			return platformType;
		}

		public void setPlatformType(String platformType) {
			this.platformType = platformType;
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

	public class level2Elm {
		private String item;
		private String lbDate;
		private String leDate;
		private String lbTime;
		private String leTime;

		public String getItem() {
			return item;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getLbDate() {
			return lbDate;
		}

		public void setLbDate(String lbDate) {
			this.lbDate = lbDate;
		}

		public String getLeDate() {
			return leDate;
		}

		public void setLeDate(String leDate) {
			this.leDate = leDate;
		}

		public String getLbTime() {
			return lbTime;
		}

		public void setLbTime(String lbTime) {
			this.lbTime = lbTime;
		}

		public String getLeTime() {
			return leTime;
		}

		public void setLeTime(String leTime) {
			this.leTime = leTime;
		}
	}

}
