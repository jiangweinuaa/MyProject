package com.dsc.spos.json.cust.res;


import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * POS 查询生产计划
 * @author yuanyy 2019-10-29
 *
 */
public class DCP_PosPlanQueryRes extends JsonRes {
	private String bDate;
	
	private List<level1Elm> datas;

	public String getbDate() {
		return bDate;
	}

	public void setbDate(String bDate) {
		this.bDate = bDate;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		
		private String fType;
		private String fNo;
		private String fName;
		private String beginTime;
		private String endTime;
		private String predictAmt;
		
		private List<level2Elm> datas;

		public String getfType() {
			return fType;
		}

		public void setfType(String fType) {
			this.fType = fType;
		}

		public String getfNo() {
			return fNo;
		}

		public void setfNo(String fNo) {
			this.fNo = fNo;
		}

		public String getfName() {
			return fName;
		}

		public void setfName(String fName) {
			this.fName = fName;
		}

		public String getBeginTime() {
			return beginTime;
		}

		public void setBeginTime(String beginTime) {
			this.beginTime = beginTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public String getPredictAmt() {
			return predictAmt;
		}

		public void setPredictAmt(String predictAmt) {
			this.predictAmt = predictAmt;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
		
	}
	
	public class level2Elm {
		
		private String item;
		private String qty;
		private String pluNo;
		private String pluName;
		private String pUnit;
		private String price;
		private String distriPrice;
		private String amt;
		private String distriAmt;
		
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getpUnit() {
			return pUnit;
		}
		public void setpUnit(String pUnit) {
			this.pUnit = pUnit;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getDistriPrice() {
			return distriPrice;
		}
		public void setDistriPrice(String distriPrice) {
			this.distriPrice = distriPrice;
		}
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
		public String getDistriAmt() {
			return distriAmt;
		}
		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
		}
		
	}
	
}
