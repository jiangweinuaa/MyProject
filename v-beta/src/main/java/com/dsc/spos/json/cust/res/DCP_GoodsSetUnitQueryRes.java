package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_GoodsSetUnitQueryRes extends JsonRes
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
		private String UNIT;
		private String UNIT_NAME;
		private String eId;
		private String status;
		public String getUNIT() {
			return UNIT;
		}
		public void setUNIT(String uNIT) {
			UNIT = uNIT;
		}
		public String getUNIT_NAME() {
			return UNIT_NAME;
		}
		public void setUNIT_NAME(String uNIT_NAME) {
			UNIT_NAME = uNIT_NAME;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}





	}

}
