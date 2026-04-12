package com.dsc.spos.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	private final static DateUtils du = new DateUtils();
	
	private DateUtils() {}
	
	public static DateUtils getInstance() {
		return du;
	}
	
	public Date parse(String date, String format) {
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (ParseException e) {

		}
		return null;
	}
	
	public String format(Date date, String format) {
		if (date == null) {
			return "";
		}
		return new SimpleDateFormat(format).format(date);
	}
}
