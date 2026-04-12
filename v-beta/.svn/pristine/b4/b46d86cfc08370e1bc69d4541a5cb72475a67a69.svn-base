package com.dsc.spos.service.imp.json;

public class DCP_ConversionTimeFormat 
{

	public static String converToDatetime(String datetime) {
		// TODO Auto-generated method stub
		String year = datetime.substring(0,4);
		String month = datetime.substring(4,6);
		String day = datetime.substring(6,8);
		String hour = datetime.substring(8,10);
		String minute = datetime.substring(10,12);
		String second = datetime.substring(12,14);
		String f_datetime = year + "-" + month +"-" + day + " " + hour + ":" + minute + ":" + second;
		return  f_datetime;
	}; 
	public static String converToDate(String date) {
		// TODO Auto-generated method stub
		String year = date.substring(0,4);
		String month = date.substring(4,6);
		String day = date.substring(6,8);
		String f_date = year + "-" + month +"-" + day ;
		return  f_date;
	};
	
	public static String converToTime(String time) {
		// TODO Auto-generated method stub
		String hour = time.substring(0,2);
		String mi = time.substring(2,4);
		String sec = time.substring(4,6);
		String f_time = hour + ":" + mi +":" + sec ;
		return  f_time;
	};
	
}
