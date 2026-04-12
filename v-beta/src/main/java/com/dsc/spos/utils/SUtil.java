package com.dsc.spos.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

 

/**
 * 工具类
 * 
 * @param  
 * @return
 */
public class SUtil
{
      //add by 01029 返回模糊查询的sql
	 public static String  RetLikeStr(String a) {    
       if (Check.Null(a)){
    	   return  "'%%^$%%'";  
       }else {
    	   return  "'%%"+a+"%%'";
	   }
 
	 } 
	 
	 public static String  RetTrimStr(String a) {    
	       if (Check.Null(a)){
	    	   return  "";  
	       }else {
	    	   return  "'"+a.trim()+"'";
		   }
	 
		 } 
	 
	 public static String  RetDateCon(String field,String beginDate,String endDate) {  
		 if (StringUtils.isEmpty(beginDate))
				beginDate ="2024-10-01";
			if (StringUtils.isEmpty(endDate))
				endDate ="2064-10-01";
	       if (Check.Null(field)){
	    	   return  "  ";  
	       }else {
	    	   return  " and "+field+"  between to_date('"+beginDate.substring(0,10)+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('"+endDate.substring(0,10)+" 23:59:59','yyyy-mm-dd hh24:mi:ss') ";
		   }
	 
		 } 
	 
	 public static String  RetDateCon8(String field,String beginDate,String endDate) {  
		 if (StringUtils.isEmpty(beginDate))
				beginDate ="20241001";
			if (StringUtils.isEmpty(endDate))
				endDate ="20641001";
	       if (Check.Null(field)){
	    	   return  "  ";  
	       }else {
	    	   return  " and "+ field+" >='"+beginDate+"' and "+ field+" <='"+endDate+"' ";
		   }
	 
		 } 
	 
	 public static boolean EmptyList(List list) {
		if (null == list || list.isEmpty()){
			return true;
		};
	 
		 if (list.size() > 0) {
			 return false;
		 }else {
			 return true;
		}
		
		
	}
	 
	  
};


