package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：ReasonGet
 *    說明：理由码查询
 * 服务说明：理由码查询
 * @author luoln 
 * @since  2017-03-30
 */
public class DCP_ReasonQueryRes extends JsonRes {
	
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm
	{
		private String bsNo;
		private String bsName;
		private String bsType;
		
		public String getBsName() {
			return bsName;
		}
		public void setBsName(String bsName) {
			this.bsName = bsName;
		}
		public String getBsNo() {
			return bsNo;
		}
		public void setBsNo(String bsNo) {
			this.bsNo = bsNo;
		}
		public String getBsType() {
			return bsType;
		}
		public void setBsType(String bsType) {
			this.bsType = bsType;
		}
	}
}
