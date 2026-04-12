package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_DosageQueryRes extends JsonRes {
		
	private List<level1Elm> datas;
	public class level1Elm{
		private String pfNo;
		
		public String getPfNo() {
			return pfNo;
		}

		public void setPfNo(String pfNo) {
			this.pfNo = pfNo;
		}
		
	}
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	
}
