package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 计算千元用量
 * @author yuanyy 2019-08-13 
 *
 */
public class DCP_DosageOldQueryReq extends JsonBasicReq {

	private String beginDate;
	private String endDate;
	private String avgSaleAmt;
	private String saleAmt;
	private String totSaleAmt;
	private String modifRatio;
	
	private String pfWeatherRatio;
	private String pfMatterRatio;
	private String pfHolidayRatio;
	
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getAvgSaleAmt() {
		return avgSaleAmt;
	}
	public void setAvgSaleAmt(String avgSaleAmt) {
		this.avgSaleAmt = avgSaleAmt;
	}
	public String getTotSaleAmt() {
		return totSaleAmt;
	}
	public void setTotSaleAmt(String totSaleAmt) {
		this.totSaleAmt = totSaleAmt;
	}
	public String getSaleAmt() {
		return saleAmt;
	}
	public void setSaleAmt(String saleAmt) {
		this.saleAmt = saleAmt;
	}
	public String getModifRatio() {
		return modifRatio;
	}
	public void setModifRatio(String modifRatio) {
		this.modifRatio = modifRatio;
	}
	public String getPfWeatherRatio() {
		return pfWeatherRatio;
	}
	public void setPfWeatherRatio(String pfWeatherRatio) {
		this.pfWeatherRatio = pfWeatherRatio;
	}
	public String getPfMatterRatio() {
		return pfMatterRatio;
	}
	public void setPfMatterRatio(String pfMatterRatio) {
		this.pfMatterRatio = pfMatterRatio;
	}
	public String getPfHolidayRatio() {
		return pfHolidayRatio;
	}
	public void setPfHolidayRatio(String pfHolidayRatio) {
		this.pfHolidayRatio = pfHolidayRatio;
	}

	
}
