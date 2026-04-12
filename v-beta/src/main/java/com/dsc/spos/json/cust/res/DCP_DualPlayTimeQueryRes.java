package com.dsc.spos.json.cust.res;
import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_ParaSetQueryRes.level1Elm;

/**
 * 服務函數：DualPlayTimeGetDCP
 *   說明：双屏播放查询
 * 服务说明：双屏播放查询
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayTimeQueryRes extends JsonRes {

	
	private List<level1Elm> datas;
	public class level1Elm{
		private String item;
		private String lbDate;
		private String leDate;
		private String lbTime;
		private String leTime;
		private String status;

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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	}

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}	
}
