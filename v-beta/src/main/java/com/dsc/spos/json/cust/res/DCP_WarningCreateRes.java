package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_WarningCreateRes extends JsonBasicRes {

	private List<level1Elm> datas;
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


	public class level1Elm
	{
		private String billNo;

		public String getBillNo() {
			return billNo;
		}
	
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		
		
	}
}
