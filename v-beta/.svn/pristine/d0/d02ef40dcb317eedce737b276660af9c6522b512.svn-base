package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DualPlayTimeUpdateDCP 說明：双屏播放时间修改DCP 服务说明：双屏播放时间修改DCP
 * 
 * @author panjing
 * @since 2016-11-11
 */
public class DCP_DualPlayTimeUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String dualPlayID;
		private List<level1Elm> datas;

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}

		public String getDualPlayID() {
			return dualPlayID;
		}

		public void setDualPlayID(String dualPlayID) {
			this.dualPlayID = dualPlayID;
		}
	}
	public class level1Elm {
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
