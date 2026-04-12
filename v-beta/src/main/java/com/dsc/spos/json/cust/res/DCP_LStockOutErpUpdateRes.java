package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_LStockOutErpUpdateRes extends JsonBasicRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String doc_no;
		private String org_no;
		public String getDoc_no() {
			return doc_no;
		}
		public void setDoc_no(String doc_no) {
			this.doc_no = doc_no;
		}
		public String getOrg_no() {
			return org_no;
		}
		public void setOrg_no(String org_no) {
			this.org_no = org_no;
		}
		
	}

}
