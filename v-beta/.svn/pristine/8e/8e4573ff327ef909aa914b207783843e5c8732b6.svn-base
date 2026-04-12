package com.dsc.spos.json.cust.res;
import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：DualPlayGetDCP
 *   說明：双屏播放查询
 * 服务说明：双屏播放查询
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayQueryRes extends JsonRes {

	private List<level1Elm> datas;
	public class level1Elm{
		private String dualPlayID;
		private String platformType;
		private String fileName;
		private String fileUrl; // @Added on 20210120
		private String memo;
		private String shopType;
		private String timeType;
		private String pollTime;
		private String status;
		private List<level2Elm> shops;
		private List<level3Elm> times;

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
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
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
		public List<level3Elm> getTimes() {
			return times;
		}
		public void setTimes(List<level3Elm> times) {
			this.times = times;
		}
		public String getPlatformType() {
			return platformType;
		}
		public void setPlatformType(String platformType) {
			this.platformType = platformType;
		}
		public String getPollTime() {
			return pollTime;
		}
		public void setPollTime(String pollTime) {
			this.pollTime = pollTime;
		}

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
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

	public class level3Elm{
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
