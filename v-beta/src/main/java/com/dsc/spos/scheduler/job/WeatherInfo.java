package com.dsc.spos.scheduler.job;


import java.sql.Types;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;


@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WeatherInfo extends InitJob 
{
	Logger logger = LogManager.getLogger(WeatherInfo.class.getName());
	//一天执行一次
	static boolean bFirst=true;

	static String sDate="";

	public WeatherInfo()
	{


	}


	public String doExe() 
	{
		logger.info("\r\n***************统一获取天气预报功能START****************\r\n");

		//返回信息
		String sReturnInfo="";

		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");		

		//日期 
		String eDate=dfDate.format(cal.getTime());

		//时间
		SimpleDateFormat dfTime=new SimpleDateFormat("HHmmss");
		String mySysTime = dfTime.format(cal.getTime());			

		if(mySysTime.compareTo("003000")>=0 && mySysTime.compareTo("004000")<0) 
		{			
			//又过一天
			if(sDate.equals(PosPub.GetStringDate(eDate, -1)))
			{				
				bFirst=true;
			}

			//一天执行一次
			if(bFirst==false)
			{				
				return sReturnInfo;
			}				

			sDate=eDate;

			logger.error("\r\n***************统一获取天气预报功能执行日期：" + eDate+"****************\r\n");

			//一天执行一次
			bFirst=false;

			try 
			{
				//1.查询门店信息表中城市和区县字段不为空的门店和门店对应的城市气象代码
				//2.如果气象城市代码adcode没有值，调用SendDistrict获取气象城市代码adcode;否则调用根据adcode调用SendWeatherInfo获取天气
				//3.将获取的天气信息更新到DCP_AREAWEATHER(有则更新，无则插入)		
				
				String sql = "SELECT a.eId, a.organizationno SHOPID, a.city AS city_name, "
						+ " a.COUNTY AS district_name, b.adcode FROM DCP_ORG a "
						+ " LEFT JOIN DCP_SHOPINFO_WEATHERCODE b "
						+ " ON a.eId = b.eId AND a.organizationno = b.SHOPID WHERE a.STATUS='100' "
						+ " AND a.city IS NOT NULL ORDER BY a.eId, a.organizationno ";
				
				String[] conditionValues = {}; // 查詢條件
				List<Map<String, Object>> getQDataEdate = StaticInfo.dao.executeQuerySQL(sql, conditionValues);
				if (getQDataEdate != null || getQDataEdate.isEmpty() == false ) 
				{
					//已完成店数
					int eDateOKshopCount=0;

					for (Map<String, Object> par : getQDataEdate) 
					{
						Thread.sleep(1000);  

						String json="";

						try 
						{							

							String adcode="";
							String adcityname="";//城市名称
							String addistrictname="";//区县名称
							String jsonData="";
							JSONObject jsonObject=null;
							String status="";

							String parAdcode=par.get("ADCODE").toString();
							if(parAdcode.equals(""))
							{
								//获取adcode

								//这里有一种情况省级市(重庆、北京、天津、上海)
								String shopCityname=par.get("CITY_NAME").toString();
								if (shopCityname.contains("北京市")) 
								{
									shopCityname="北京";
								}
								if (shopCityname.contains("上海市")) 
								{
									shopCityname="上海";
								}
								if (shopCityname.contains("重庆市")) 
								{
									shopCityname="重庆";
								}
								if (shopCityname.contains("天津市")) 
								{
									shopCityname="天津";
								}

								String shopDistrictname=par.get("DISTRICT_NAME").toString();

								jsonData=HttpSend.SendDistrict(shopCityname);

								if(jsonData.equals(""))
								{			
									//获取城市编码adcode失败
									logger.info("\r\n***********定时统一获取天气预报:EID=" + par.get("EID").toString() + "SHOPID=" +par.get("SHOPID").toString() +"ORGANIZATIONNO="+ par.get("ORGANIZATIONNO").toString() + "\r\n HttpSend.SendDistrict获取城市编码adcode失败！\r\n");

									continue;
								}


								jsonObject = new JSONObject(jsonData);

								status=jsonObject.get("status").toString();

								if(status.equals("1"))//0表示失败；1表示成功
								{			
									//地市级districts
									JSONArray jsonArrayCity = jsonObject.getJSONArray("districts");
									if (jsonArrayCity.length() > 0) 
									{
										for (int a = 0; a < jsonArrayCity.length(); a++) 
										{		           
											JSONObject object = jsonArrayCity.getJSONObject(a);
											String city_adcode = object.optString("adcode");//320500			
											String city_name = object.optString("name");//苏州市
											String city_level = object.optString("level");//苏州市,这里有一种情况省级市(重庆、北京、天津、上海)


											//因为关键字是模糊查询，所以返回结果可能很多，我们只要地市级city
											if(city_level.equals("city") && city_name.contains(shopCityname))
											{				
												adcode=city_adcode;

												adcityname=city_name;

												Boolean bExistdistrict=false;
												//区县级districts
												JSONArray jsonArraydistricts = object.getJSONArray("districts");
												if (jsonArrayCity.length() > 0) 
												{					
													for (int b = 0; b < jsonArraydistricts.length(); b++) 
													{
														String district_adcode = jsonArraydistricts.getJSONObject(b).optString("adcode");//320508			
														String district_name = jsonArraydistricts.getJSONObject(b).optString("name");//姑苏区

														if(district_name.contains(shopDistrictname))
														{									
															adcode=district_adcode;		

															addistrictname=district_name;

															bExistdistrict=true;

															break;//找到跳出
														}								
													}							
												}			

												if (bExistdistrict) 
												{
													break;
												}
											}
										}		
										
										
										//处理DCP_SHOPINFO_WEATHERCODE
										//插入
										String[] columnsCODE = 
											{
													"SHOPID", "EID","ADCODE","ADCITY_NAME","ADDISTRICT_NAME"
											};
										DataValue[] insValueCODE = null;

										insValueCODE = new DataValue[]
												{
														new DataValue(par.get("SHOPID").toString(), Types.VARCHAR), 
														new DataValue(par.get("EID").toString(), Types.VARCHAR), 
														new DataValue(adcode, Types.VARCHAR), 
														new DataValue(adcityname, Types.VARCHAR), 
														new DataValue(addistrictname, Types.VARCHAR)

												};

										StaticInfo.dao.insert("DCP_SHOPINFO_WEATHERCODE", columnsCODE, insValueCODE);	
										
									}			
								}										
							}
							else
							{								
								adcode=par.get("ADCODE").toString();
								adcityname=par.get("CITY_NAME").toString();//城市名称
								addistrictname=par.get("DISTRICT_NAME").toString();//区县名称
							}

							//获取天气信息
							if(adcode.equals("")==false)
							{			
								jsonData=HttpSend.SendWeatherInfo(adcode, adcityname,"GAODEMAP");

								if(jsonData.equals(""))
								{			
									//获取天气信息失败
									logger.info("\r\n***********定时统一获取天气预报:EID=" + par.get("EID").toString() + "SHOPID=" +par.get("SHOPID").toString() +"ORGANIZATIONNO="+ par.get("ORGANIZATIONNO").toString() + "\r\n HttpSend.SendWeatherInfo获取天气信息失败！\r\n");

									continue;										
								}

								jsonObject = new JSONObject(jsonData);

								status=jsonObject.get("status").toString();
								if(status.equals("1"))//0表示失败；1表示成功
								{
									//预报
									JSONArray jsonArrayForecasts = jsonObject.getJSONArray("forecasts");

									if (jsonArrayForecasts.length() > 0) 
									{
										JSONArray jsonArrayCasts = jsonArrayForecasts.getJSONObject(0).getJSONArray("casts");

										if (jsonArrayCasts.length() > 0) 
										{						
											for (int c = 0; c < jsonArrayCasts.length(); c++) 
											{
												String cast_date = jsonArrayCasts.getJSONObject(c).optString("date");//2018-05-09
												String cast_week = jsonArrayCasts.getJSONObject(c).optString("week");//3
												String cast_dayweather = jsonArrayCasts.getJSONObject(c).optString("dayweather");//晴
												String cast_nightweather = jsonArrayCasts.getJSONObject(c).optString("nightweather");//多云
												String cast_daytemp = jsonArrayCasts.getJSONObject(c).optString("daytemp");//25
												String cast_nighttemp = jsonArrayCasts.getJSONObject(c).optString("nighttemp");//15
												String cast_daywind = jsonArrayCasts.getJSONObject(c).optString("daywind");//东北
												String cast_nightwind = jsonArrayCasts.getJSONObject(c).optString("nightwind");//东
												String cast_daypower = jsonArrayCasts.getJSONObject(c).optString("daypower");//≤3
												String cast_nightpower = jsonArrayCasts.getJSONObject(c).optString("nightpower");//≤3

												cast_date=cast_date.substring(0,4) + cast_date.substring(5,7) + cast_date.substring(8,10);

												String sql_weather="select sdate from DCP_AREAWEATHER where sdate='"+cast_date+"' and city='"+par.get("CITY_NAME").toString()+"' and district='"+par.get("DISTRICT_NAME").toString()+"' ";

												List<Map<String, Object>> getQWeather = StaticInfo.dao.executeQuerySQL(sql_weather, null);
												if (getQWeather != null && getQWeather.isEmpty()) 
												{														
													//插入
													String[] columns1 = 
														{
																"SDATE", "CITY","DISTRICT","WEEK","DAY_WEATHER", "NIGHT_WEATHER",  "DAY_TEMPERATURE", 
																"NIGHT_TEMPERATURE","DAY_WIND","NIGHT_WIND","DAY_POWER", "NIGHT_POWER"
														};
													DataValue[] insValue1 = null;

													insValue1 = new DataValue[]
															{
																	new DataValue(cast_date, Types.VARCHAR), 
																	new DataValue(par.get("CITY_NAME").toString(), Types.VARCHAR), 
																	new DataValue(par.get("DISTRICT_NAME").toString(), Types.VARCHAR), 
																	new DataValue(cast_week, Types.VARCHAR), 
																	new DataValue(cast_dayweather, Types.VARCHAR),
																	new DataValue(cast_nightweather, Types.VARCHAR),
																	new DataValue(cast_daytemp, Types.VARCHAR),
																	new DataValue(cast_nighttemp, Types.VARCHAR), 
																	new DataValue(cast_daywind, Types.VARCHAR), 
																	new DataValue(cast_nightwind, Types.VARCHAR),
																	new DataValue(cast_daypower, Types.VARCHAR), 
																	new DataValue(cast_nightpower, Types.VARCHAR)
															};

													StaticInfo.dao.insert("DCP_AREAWEATHER", columns1, insValue1);	
												}
												else 
												{														
													//更新
													// values
													Map<String, DataValue> values = new HashMap<String, DataValue>();
													values.put("WEEK", new DataValue(cast_week, Types.VARCHAR));
													values.put("DAY_WEATHER", new DataValue(cast_dayweather, Types.VARCHAR));
													values.put("NIGHT_WEATHER", new DataValue(cast_nightweather, Types.VARCHAR));
													values.put("DAY_TEMPERATURE", new DataValue(cast_daytemp, Types.VARCHAR));
													values.put("NIGHT_TEMPERATURE", new DataValue(cast_nighttemp, Types.VARCHAR));
													values.put("DAY_WIND", new DataValue(cast_daywind, Types.VARCHAR));
													values.put("NIGHT_WIND", new DataValue(cast_nightwind, Types.VARCHAR));
													values.put("DAY_POWER", new DataValue(cast_daypower, Types.VARCHAR));
													values.put("NIGHT_POWER", new DataValue(cast_nightpower, Types.VARCHAR));

													// condition
													Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
													DataValue c1 = new DataValue(cast_date, Types.VARCHAR);
													conditions.put("SDATE", c1);
													DataValue c2 = new DataValue(par.get("CITY_NAME").toString(), Types.VARCHAR);
													conditions.put("CITY", c2);			
													DataValue c3 = new DataValue(par.get("DISTRICT_NAME").toString(), Types.VARCHAR);
													conditions.put("DISTRICT", c3);		

													StaticInfo.dao.update("DCP_AREAWEATHER", values, conditions);	
												}													
											}						

										}											
									}										
								}									
							}	



						} 
						catch (Exception e) 
						{
							logger.error("\r\n***********定时统一获取天气预报单个门店异常:EID=" + par.get("EID").toString() + "SHOPID=" +par.get("SHOPID").toString() +"ORGANIZATIONNO="+ par.get("ORGANIZATIONNO").toString() + "\r\n Json=" + json + "\r\n"+ e.getMessage());
						}
						finally 
						{
							eDateOKshopCount+=1;
							logger.error("\r\n***********定时统一获取天气预报完成门店总数:" + eDateOKshopCount +"\r\n");
						}						
					}
				}
			} 
			catch (Exception e) 
			{
				logger.error("\r\n***********统一获取天气预报功能发生异常:" + e.getMessage());
			}  
		}		

		logger.info("\r\n***************统一获取天气预报功能END****************\r\n");

		return sReturnInfo;	

	}


}
