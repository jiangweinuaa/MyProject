package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_ReceivingOrgQueryRes extends JsonRes
{
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String orgNo;
		private String orgName;
		private String orgForm;

		public String getOrgNo() {
			return orgNo;
		}
		public void setOrgNo(String orgNo) {
			this.orgNo = orgNo;
		}
		public String getOrgName() {
			return orgName;
		}
		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}
		public String getOrgForm() {
			return orgForm;
		}
		public void setOrgForm(String orgForm) {
			this.orgForm = orgForm;
		}
	}

}
