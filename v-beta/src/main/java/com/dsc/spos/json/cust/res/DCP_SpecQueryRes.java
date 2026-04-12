package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 规格查询 2018-10-15	
 * @author yuanyy
 *
 */
public class DCP_SpecQueryRes extends JsonRes {
	
	private List<level1Elm> datas;
	
	public class level1Elm{
		private String specNo;
		private String specName;
		private String status;
		
		public String getSpecNo()
		{
			return specNo;
		}
		public void setSpecNo(String specNo)
		{
			this.specNo = specNo;
		}
		public String getSpecName() {
			return specName;
		}
		public void setSpecName(String specName) {
			this.specName = specName;
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
