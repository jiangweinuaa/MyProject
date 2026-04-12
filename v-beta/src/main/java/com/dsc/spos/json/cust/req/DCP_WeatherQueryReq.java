package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_WeatherQueryReq extends JsonBasicReq
{

	private String select_mode;//日期类型    0：取weatherDate日期数组    1：取beginDate和endDate
	
	private String[] weatherDate;//日期数组
	
	private String beginDate;//开始日期
	
	private String endDate;//结束日期

	public String getSelect_mode() {
		return select_mode;
	}

	public void setSelect_mode(String select_mode) {
		this.select_mode = select_mode;
	}

	public String[] getWeatherDate() {
		return weatherDate;
	}

	public void setWeatherDate(String[] weatherDate) {
		this.weatherDate = weatherDate;
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
	
	
	
	
	
	
	
}
