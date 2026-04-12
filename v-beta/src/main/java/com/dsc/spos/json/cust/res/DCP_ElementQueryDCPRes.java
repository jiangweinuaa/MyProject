package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 影响因素查询
 * @author yuanyy 2019-10-21
 *
 */
public class DCP_ElementQueryDCPRes extends JsonRes {
	private List<level1Elm> datas;

	public class level1Elm {
		private String e_Type;
		private String eNo;
		private String eName;
		private String eRatio;
		private String status;

		public String getE_Type() {
			return e_Type;
		}
		public void setE_Type(String e_Type) {
			this.e_Type = e_Type;
		}
		public String geteNo() {
			return eNo;
		}
		public void seteNo(String eNo) {
			this.eNo = eNo;
		}
		public String geteName() {
			return eName;
		}
		public void seteName(String eName) {
			this.eName = eName;
		}
		public String geteRatio() {
			return eRatio;
		}
		public void seteRatio(String eRatio) {
			this.eRatio = eRatio;
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
