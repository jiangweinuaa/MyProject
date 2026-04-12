package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

/**
 * 计划报单详情查询
 * @author yuanyy
 *	
 */
public class DCP_PFOrderDetailRes extends JsonBasicRes{
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String dateReferType;
		private String adjustWay;
		private String adjustValue;
		
		private List<level2Elm> pluList;
		private List<level3Elm> forecastList;
		
		public String getDateReferType() {
			return dateReferType;
		}
		public void setDateReferType(String dateReferType) {
			this.dateReferType = dateReferType;
		}
		public String getAdjustWay() {
			return adjustWay;
		}
		public String getAdjustValue() {
			return adjustValue;
		}
		public void setAdjustWay(String adjustWay) {
			this.adjustWay = adjustWay;
		}
		public void setAdjustValue(String adjustValue) {
			this.adjustValue = adjustValue;
		}
		public List<level2Elm> getPluList() {
			return pluList;
		}
		public void setPluList(List<level2Elm> pluList) {
			this.pluList = pluList;
		}
		public List<level3Elm> getForecastList() {
			return forecastList;
		}
		public void setForecastList(List<level3Elm> forecastList) {
			this.forecastList = forecastList;
		}
		
	}
	
	public class level2Elm{
		private String pluNo;
		private String pluName;
		private String fileName;
		private String pUnit;
		private String pUnitName;
		private String udLength;
		private String guQingType;
		private String kQty;
		private String kAmt;
		private String price;
		private String preSaleQty;
		private String qty;
		private String kAdjAmt;
		private List<level4Elm> mealData;
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
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getpUnit() {
			return pUnit;
		}
		public void setpUnit(String pUnit) {
			this.pUnit = pUnit;
		}
		public String getUdLength() {
			return udLength;
		}
		public void setUdLength(String udLength) {
			this.udLength = udLength;
		}
		public String getpUnitName() {
			return pUnitName;
		}
		public void setpUnitName(String pUnitName) {
			this.pUnitName = pUnitName;
		}
		public String getGuQingType() {
			return guQingType;
		}
		public void setGuQingType(String guQingType) {
			this.guQingType = guQingType;
		}
		public String getkQty() {
			return kQty;
		}
		public void setkQty(String kQty) {
			this.kQty = kQty;
		}
		public String getkAmt() {
			return kAmt;
		}
		public void setkAmt(String kAmt) {
			this.kAmt = kAmt;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getPreSaleQty() {
			return preSaleQty;
		}
		public void setPreSaleQty(String preSaleQty) {
			this.preSaleQty = preSaleQty;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getkAdjAmt() {
			return kAdjAmt;
		}
		public void setkAdjAmt(String kAdjAmt) {
			this.kAdjAmt = kAdjAmt;
		}
		public List<level4Elm> getMealData() {
			return mealData;
		}
		public void setMealData(List<level4Elm> mealData) {
			this.mealData = mealData;
		}
		
	}
	
	
	public class level4Elm{
		private String dtNo;
		private String dtName;
		private String beginTime;
		private String endTime;
		private String kQty;
		private String lastSaleTime;
		private String qty;
		public String getDtNo() {
			return dtNo;
		}
		public void setDtNo(String dtNo) {
			this.dtNo = dtNo;
		}
		public String getDtName() {
			return dtName;
		}
		public void setDtName(String dtName) {
			this.dtName = dtName;
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
		public String getkQty() {
			return kQty;
		}
		public void setkQty(String kQty) {
			this.kQty = kQty;
		}
		public String getLastSaleTime() {
			return lastSaleTime;
		}
		public void setLastSaleTime(String lastSaleTime) {
			this.lastSaleTime = lastSaleTime;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		
	}
	
	public class level3Elm{
		private String eDate;
		private String dataType;
		private String item;
		private String beginDate;
		private String endDate;
		private String dayAmt;
		private String pfDay;
		private String city;
		private String pfHoliday;
		private String lunarPfHoliday;
		private String lunar;
		private String pfHolidayNo;
		private String pfWeather;
		private String pfWeatherNo;
		private String climate;
		private String lowClimate;
		private String highClimate;
		private String saleAmt;
		private String dinnerAmt;
		private String tableAmt;
		private String selected;
		public String geteDate() {
			return eDate;
		}
		public void seteDate(String eDate) {
			this.eDate = eDate;
		}
		public String getDataType() {
			return dataType;
		}
		public void setDataType(String dataType) {
			this.dataType = dataType;
		}
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
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
		public String getDayAmt() {
			return dayAmt;
		}
		public void setDayAmt(String dayAmt) {
			this.dayAmt = dayAmt;
		}
		public String getPfDay() {
			return pfDay;
		}
		public void setPfDay(String pfDay) {
			this.pfDay = pfDay;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getPfHoliday() {
			return pfHoliday;
		}
		public void setPfHoliday(String pfHoliday) {
			this.pfHoliday = pfHoliday;
		}
		public String getLunarPfHoliday() {
			return lunarPfHoliday;
		}
		public void setLunarPfHoliday(String lunarPfHoliday) {
			this.lunarPfHoliday = lunarPfHoliday;
		}
		public String getLunar() {
			return lunar;
		}
		public void setLunar(String lunar) {
			this.lunar = lunar;
		}
		public String getPfHolidayNo() {
			return pfHolidayNo;
		}
		public void setPfHolidayNo(String pfHolidayNo) {
			this.pfHolidayNo = pfHolidayNo;
		}
		public String getPfWeather() {
			return pfWeather;
		}
		public void setPfWeather(String pfWeather) {
			this.pfWeather = pfWeather;
		}
		public String getPfWeatherNo() {
			return pfWeatherNo;
		}
		public void setPfWeatherNo(String pfWeatherNo) {
			this.pfWeatherNo = pfWeatherNo;
		}
		public String getClimate() {
			return climate;
		}
		public void setClimate(String climate) {
			this.climate = climate;
		}
		public String getLowClimate() {
			return lowClimate;
		}
		public void setLowClimate(String lowClimate) {
			this.lowClimate = lowClimate;
		}
		public String getHighClimate() {
			return highClimate;
		}
		public void setHighClimate(String highClimate) {
			this.highClimate = highClimate;
		}
		public String getSaleAmt() {
			return saleAmt;
		}
		public void setSaleAmt(String saleAmt) {
			this.saleAmt = saleAmt;
		}
		public String getDinnerAmt() {
			return dinnerAmt;
		}
		public void setDinnerAmt(String dinnerAmt) {
			this.dinnerAmt = dinnerAmt;
		}
		public String getTableAmt() {
			return tableAmt;
		}
		public void setTableAmt(String tableAmt) {
			this.tableAmt = tableAmt;
		}
		public String getSelected() {
			return selected;
		}
		public void setSelected(String selected) {
			this.selected = selected;
		}
		
	}
	
	
}
