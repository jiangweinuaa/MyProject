package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


public class DCP_WeatherQueryRes extends JsonRes
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
		private String SDATE;
		private String CITY;
		private String DISTRICT;
		private String WEEK;
		private String DAY_WEATHER;
		private String NIGHT_WEATHER;
		private String DAY_TEMPERATURE;
		private String NIGHT_TEMPERATURE;
		private String DAY_WIND;
		private String NIGHT_WIND;
		private String DAY_POWER;
		private String NIGHT_POWER;
		public String getSDATE() {
			return SDATE;
		}
		public void setSDATE(String sDATE) {
			SDATE = sDATE;
		}
		public String getCITY() {
			return CITY;
		}
		public void setCITY(String cITY) {
			CITY = cITY;
		}
		public String getDISTRICT() {
			return DISTRICT;
		}
		public void setDISTRICT(String dISTRICT) {
			DISTRICT = dISTRICT;
		}
		public String getWEEK() {
			return WEEK;
		}
		public void setWEEK(String wEEK) {
			WEEK = wEEK;
		}
		public String getDAY_WEATHER() {
			return DAY_WEATHER;
		}
		public void setDAY_WEATHER(String dAY_WEATHER) {
			DAY_WEATHER = dAY_WEATHER;
		}
		public String getNIGHT_WEATHER() {
			return NIGHT_WEATHER;
		}
		public void setNIGHT_WEATHER(String nIGHT_WEATHER) {
			NIGHT_WEATHER = nIGHT_WEATHER;
		}
		public String getDAY_TEMPERATURE() {
			return DAY_TEMPERATURE;
		}
		public void setDAY_TEMPERATURE(String dAY_TEMPERATURE) {
			DAY_TEMPERATURE = dAY_TEMPERATURE;
		}
		public String getNIGHT_TEMPERATURE() {
			return NIGHT_TEMPERATURE;
		}
		public void setNIGHT_TEMPERATURE(String nIGHT_TEMPERATURE) {
			NIGHT_TEMPERATURE = nIGHT_TEMPERATURE;
		}
		public String getDAY_WIND() {
			return DAY_WIND;
		}
		public void setDAY_WIND(String dAY_WIND) {
			DAY_WIND = dAY_WIND;
		}
		public String getNIGHT_WIND() {
			return NIGHT_WIND;
		}
		public void setNIGHT_WIND(String nIGHT_WIND) {
			NIGHT_WIND = nIGHT_WIND;
		}
		public String getDAY_POWER() {
			return DAY_POWER;
		}
		public void setDAY_POWER(String dAY_POWER) {
			DAY_POWER = dAY_POWER;
		}
		public String getNIGHT_POWER() {
			return NIGHT_POWER;
		}
		public void setNIGHT_POWER(String nIGHT_POWER) {
			NIGHT_POWER = nIGHT_POWER;
		}
		
		
		
	}
	
	
	
}
