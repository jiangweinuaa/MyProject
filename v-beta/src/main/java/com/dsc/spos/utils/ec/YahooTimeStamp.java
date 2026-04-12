package com.dsc.spos.utils.ec;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.utils.HttpSend;

public class YahooTimeStamp {
	/**
	 * 获取时间戳
	 * @param apiUrl
	 * @return
	 */
	public static String getTimeStamp(String apiUrl){
		
		// 地址：  https://tw.ews.mall.yahooapis.com/stauth/v1/echo?Format=json 或 xml
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="echo??Format=json";
		}
		else			
		{
			apiUrl+="/echo??Format=json";
		}
		
		String resbody="";
		String timeStamp = "";
		try
		{
			resbody = HttpSend.Sendhttp("get", null, apiUrl);
//			String s1 = resbody.replace("\"","\\\"");
			JSONObject jsonObj = JSONObject.parseObject(resbody);
			String res = jsonObj.getString("Response").toString();
			
			JSONObject jsonObj2 = JSONObject.parseObject(res);
			timeStamp = jsonObj2.getString("TimeStamp").toString();
			
		}
		catch (Exception ex) 
		{		
			//System.out.println("YaHoo 获取时间戳 出现错误！");
			//System.out.println(ex.getMessage().toString());
		}		
		return timeStamp;
	}
}
