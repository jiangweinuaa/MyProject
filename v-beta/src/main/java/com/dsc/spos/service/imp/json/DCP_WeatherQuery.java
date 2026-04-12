package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.dsc.spos.json.cust.req.DCP_WeatherQueryReq;
import com.dsc.spos.json.cust.res.DCP_WeatherQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_WeatherQuery extends SPosBasicService<DCP_WeatherQueryReq, DCP_WeatherQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_WeatherQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    
	    //必传值不为空
	    String Select_mode = req.getSelect_mode();
	    
	    if(Check.Null(Select_mode))
	    {
	    	errCt++;
	    	errMsg.append("日期选择模式Select_mode不可为空值, ");
			isFail = true;
	    }
	    
	    if (Select_mode.equals("0")) //
	    {
			if(req.getWeatherDate()==null || req.getWeatherDate().length<1)
			{				
				errCt++;
		    	errMsg.append("日期WeatherDate不可为空值, ");
				isFail = true;
			}
		}
	    else
	    {
	    	if(Check.Null(req.getBeginDate()))
		    {
		    	errCt++;
		    	errMsg.append("日期BeginDate不可为空值, ");
				isFail = true;
		    }
	    	
	    	if(Check.Null(req.getEndDate()))
		    {
		    	errCt++;
		    	errMsg.append("日期EndDate不可为空值, ");
				isFail = true;
		    }	    	
	    }
	    
	    
	    if (isFail)
	    {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_WeatherQueryReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_WeatherQueryReq>(){};
	}

	@Override
	protected DCP_WeatherQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_WeatherQueryRes();
	}

	@Override
	protected DCP_WeatherQueryRes processJson(DCP_WeatherQueryReq req) throws Exception 
	{
		
		//查詢資料
		DCP_WeatherQueryRes res = null;
		res = this.getResponse();
		
		 //查詢資料
	    String sql = this.getQuerySql(req);
	    
	    List<Map<String, Object>> getQData1 = this.doQueryData(sql, null);
	    
	    res.setDatas(new ArrayList<DCP_WeatherQueryRes.level1Elm>());
	    
	    if (getQData1 != null && getQData1.isEmpty() == false) // 有資料，取得詳細內容
	    { 
	      	for (Map<String, Object> oneData1 : getQData1) 
	      	{
	      		DCP_WeatherQueryRes.level1Elm oneLv1 = res.new level1Elm();
        		
				String cITY = oneData1.get("CITY").toString();
				String dAY_POWER   = oneData1.get("DAY_POWER").toString();
				String dAY_TEMPERATURE = oneData1.get("DAY_TEMPERATURE").toString();
				String dAY_WEATHER = oneData1.get("DAY_WEATHER").toString();
				String dAY_WIND = oneData1.get("DAY_WIND").toString();
				String dISTRICT = oneData1.get("DISTRICT").toString();
				String nIGHT_POWER = oneData1.get("NIGHT_POWER").toString();
				String nIGHT_TEMPERATURE = oneData1.get("NIGHT_TEMPERATURE").toString();
				String nIGHT_WEATHER = oneData1.get("NIGHT_WEATHER").toString();
				String nIGHT_WIND = oneData1.get("NIGHT_WIND").toString();
				String sDATE = oneData1.get("SDATE").toString();
				String wEEK = oneData1.get("WEEK").toString();
				
				oneLv1.setCITY(cITY);
				oneLv1.setDAY_POWER(dAY_POWER);
				oneLv1.setDAY_TEMPERATURE(dAY_TEMPERATURE);
				oneLv1.setDAY_WEATHER(dAY_WEATHER);
				oneLv1.setDAY_WIND(dAY_WIND);
				oneLv1.setDISTRICT(dISTRICT);
				oneLv1.setNIGHT_POWER(nIGHT_POWER);
				oneLv1.setNIGHT_TEMPERATURE(nIGHT_TEMPERATURE);
				oneLv1.setNIGHT_WEATHER(nIGHT_WEATHER);
				oneLv1.setNIGHT_WIND(nIGHT_WIND);
				oneLv1.setSDATE(sDATE);
				oneLv1.setWEEK(wEEK);
				
				res.getDatas().add(oneLv1);
				
				oneLv1=null;
        	}
	    }
	   		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * 查询出天气信息
	 */
	@Override
	protected String getQuerySql(DCP_WeatherQueryReq req) throws Exception 
	{
		
		String multi_Date="";
		
		if (req.getSelect_mode().equals("0")) //取数组getWeatherDate
		{
			String[] weatherdate= req.getWeatherDate();
			for (String str : weatherdate)
			{				
				multi_Date = multi_Date + "'" +str + "',";
			}
		}
		else
		{
			SimpleDateFormat difsimDate=new SimpleDateFormat("yyyyMMdd");
		    java.util.Date date1=difsimDate.parse(req.getBeginDate());
		    java.util.Date date2=difsimDate.parse(req.getEndDate());
		    int difdate=PosPub.differentDaysByMillisecond(date1, date2);
		    
		    for (int i = 0; i <= difdate; i++) 
		    {
		    	multi_Date = multi_Date + "'" +PosPub.GetStringDate(req.getBeginDate(),i)  + "',";
			}		    
		}
		
		if (multi_Date.length()>0)
		{
			multi_Date=multi_Date.substring(0,multi_Date.length()-1);
		}		
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		
		sqlbuf.append("select * from DCP_areaweather "
				+ "where city='"+req.getCITY()+"' and district='"+req.getDISTRICT()+"' "
				+ "and sdate in ("+multi_Date+")");
		
		sql = sqlbuf.toString();
		return sql;
	}	
	
	

}
