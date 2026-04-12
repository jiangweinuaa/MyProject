package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 大智通配送流水查询
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECDZTBookQueryRes extends JsonRes {
	private List<level1Elm> datas;

	public class level1Elm {
		private String dztNo;
	    private String dztDescription;
	    private String status;
	    private String startNo;
	    private String endNo;
	    private String lastNo;
	    private String inputDate;

		public String getDztNo() {
			return dztNo;
		}
		public void setDztNo(String dztNo) {
			this.dztNo = dztNo;
		}
		public String getDztDescription() {
			return dztDescription;
		}
		public void setDztDescription(String dztDescription) {
			this.dztDescription = dztDescription;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getStartNo() {
			return startNo;
		}
		public void setStartNo(String startNo) {
			this.startNo = startNo;
		}
		public String getEndNo() {
			return endNo;
		}
		public void setEndNo(String endNo) {
			this.endNo = endNo;
		}
		public String getLastNo() {
			return lastNo;
		}
		public void setLastNo(String lastNo) {
			this.lastNo = lastNo;
		}
		public String getInputDate() {
			return inputDate;
		}
		public void setInputDate(String inputDate) {
			this.inputDate = inputDate;
		}

		
	}
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
}
