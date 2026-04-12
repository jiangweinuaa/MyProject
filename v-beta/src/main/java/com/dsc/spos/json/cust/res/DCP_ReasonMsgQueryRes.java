package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 原因码查询 ReasonMsgGet 2018-09-19
 * @author yuanyy
 *
 */
public class DCP_ReasonMsgQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm {
		private String bsNo;
		private String bsName;
		private String bsType;
		private String status;

		private List<level2Elm> datas;
		public String getBsNo() {
			return bsNo;
		}
		public void setBsNo(String bsNo) {
			this.bsNo = bsNo;
		}

		public String getBsName() {
			return bsName;
		}

		public void setBsName(String bsName) {
			this.bsName = bsName;
		}

		public String getBsType() {
			return bsType;
		}

		public void setBsType(String bsType) {
			this.bsType = bsType;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}


	}

	public class level2Elm{

		private String bsNo;
		private String bsName;
		private String bsType;
		private String langType;
		private String status;
		public String getBsNo() {
			return bsNo;
		}
		public void setBsNo(String bsNo) {
			this.bsNo = bsNo;
		}
		public String getBsName() {
			return bsName;
		}
		public void setBsName(String bsName) {
			this.bsName = bsName;
		}
		public String getBsType() {
			return bsType;
		}
		public void setBsType(String bsType) {
			this.bsType = bsType;
		}
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	}


}
