package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;
/**
 * 同期销量查询
 *
 */
public class DCP_PeriodSaleQtyRes extends JsonBasicRes {
	
	private List<level1Elm> datas; 
	
	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm
	{
		private String pluNo;
        private String punit;
		private String preDaySaleQty;//昨日销量（已转换为要货单位）
		private String lastPeriodSaleQty;//上期销量
		private String periodSaleQty;//同期销量
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPunit() {
			return punit;
		}
		public void setPunit(String punit) {
			this.punit = punit;
		}
		public String getPreDaySaleQty() {
			return preDaySaleQty;
		}
		public void setPreDaySaleQty(String preDaySaleQty) {
			this.preDaySaleQty = preDaySaleQty;
		}
		public String getLastPeriodSaleQty() {
			return lastPeriodSaleQty;
		}
		public void setLastPeriodSaleQty(String lastPeriodSaleQty) {
			this.lastPeriodSaleQty = lastPeriodSaleQty;
		}
		public String getPeriodSaleQty() {
			return periodSaleQty;
		}
		public void setPeriodSaleQty(String periodSaleQty) {
			this.periodSaleQty = periodSaleQty;
		} 
	}
	
}
