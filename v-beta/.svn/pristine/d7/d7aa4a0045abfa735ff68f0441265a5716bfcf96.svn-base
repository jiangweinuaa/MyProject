package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 同期销售
 */
public class DCP_PeriodSaleQtyReq extends JsonBasicReq {

	private levelReq request;
	
	public levelReq getRequest() {
		return request;
	}

	public void setRequest(levelReq request) {
		this.request = request;
	}

	public class levelReq{
		private String rDate   ;//需求日期
		
		private List<level1Elm> datas; 
		
		public String getrDate() {
			return rDate;
		}

		public void setrDate(String rDate) {
			this.rDate = rDate;
		}

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}

	public  class level1Elm
	{
		private String pluNo;
		private String punit;
		public String getPluNo() {
			return pluNo;
		}
		public String getPunit() {
			return punit;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public void setPunit(String punit) {
			this.punit = punit;
		}
	}
}
