package com.dsc.spos.scheduler.job;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.sql.Insert;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.WMELMUtilTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ELMTokenRefresh extends InitJob  
{
	Logger logger = LogManager.getLogger(ELMTokenRefresh.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public ELMTokenRefresh()
	{

	}

	public String doExe() throws Exception
	{
		// 返回信息
		String sReturnInfo = "";
		//查询所有APPKEY
		boolean isHasOtherAppKey = false;//是否存在多个饿了么应用APPKey
		List<Map<String, Object>> elmAppKeyList =	PosPub.getWaimaiAppConfig(StaticInfo.dao, "", orderLoadDocType.ELEME);
		if (elmAppKeyList != null && elmAppKeyList.isEmpty() == false)
		{
			isHasOtherAppKey = true;
		}

		if (isHasOtherAppKey == false)
		{
			if (StaticInfo.waimaiELMAPPKey == null || StaticInfo.waimaiELMAPPKey.length() == 0 || StaticInfo.waimaiELMSecret == null || StaticInfo.waimaiELMSecret.length() == 0) 
			{					
				return sReturnInfo;			
			}

		}

		logger.info("\r\n***************ELM_Token同步START****************\r\n");

		try 
		{
			//此服务是否正在执行中
			if (bRun)
			{		
				logger.info("\r\n*********ELM_Token同步正在执行中,本次调用取消:************\r\n");
				return sReturnInfo;
			}

			bRun=true;//
			boolean isFail = false;
			if(isHasOtherAppKey == false)//老的 
			{
				try 
				{
					RedisPosPub redis = new RedisPosPub();	
					String redis_key = "ELM_Token";
					String accessTokenStr = redis.getString(redis_key);//包含了过期时间戳
					redis.Close();		
					String accessToken="";
					long tokenTimestamp = 0;
					try 
					{
						String[] ss =accessTokenStr.split("&");
						accessToken = ss[0];
						if(ss.length>1)
						{					
							try 
							{
								tokenTimestamp = Long.parseLong(ss[1]);			
							} 
							catch (Exception e) 
							{
								tokenTimestamp = 0;			      
							}
						}
					} 
					catch (Exception e) 
					{

					}
					//缓存中Token为空，获取下Token
					if(accessToken.isEmpty()||accessToken.length()==0)
					{
						WMELMUtilTools.GetTokenInClientCredentials();
					}
					else//判断过期时间
					{
						long curTimestamp = Calendar.getInstance().getTimeInMillis()/1000;//当前时间戳，转成秒
						//定时器执行的间隔
						long job_time = 1800;//1800秒 目前写死吧，30分钟更新下
						//Token有效期-当前时间 小于等于 定时器时间间隔那么就更新

						if(tokenTimestamp-curTimestamp<=job_time)
						{
							WMELMUtilTools.GetTokenInClientCredentials();
						}
						else
						{
							sReturnInfo = "ELM_Token同步没有过期不用更新";
							logger.info("\r\n***************ELM_Token同步没有过期不用更新****************\r\n");
						}

					}
		
				} 
				catch (Exception e) 
				{
					InsertWSLOG.insert_JOBLOG(" "," ", "ELMTokenRefresh", "饿了么外卖取TOKEN", e.getMessage());
					isFail=true;	
				}
				

			}
			else//新的 多APPKey
			{			
				for (Map<String, Object> map : elmAppKeyList) 
				{
					try 
					{
						String elmAPPKey = map.get("APIKEY").toString();
						String elmAPPSecret = map.get("APISECRET").toString();
						String elmAPPName = "";//map.get("APPNAME").toString();
						String elmIsTest = map.get("ISTEST").toString();
						boolean elmIsSandbox = false;
						if (elmIsTest != null && elmIsTest.equals("Y"))
						{
							elmIsSandbox = true;
						}
						RedisPosPub redis = new RedisPosPub();	
						String redis_key = "ELM_Token" + "_" + elmAPPKey;
						String accessTokenStr = redis.getString(redis_key);//包含了过期时间戳
						redis.Close();		
						String accessToken="";
						long tokenTimestamp = 0;
						try 
						{
							String[] ss =accessTokenStr.split("&");
							accessToken = ss[0];
							if(ss.length>1)
							{					
								try 
								{
									tokenTimestamp = Long.parseLong(ss[1]);			
								} 
								catch (Exception e) 
								{
									tokenTimestamp = 0;			      
								}
							}
						} 
						catch (Exception e) 
						{

						}
						//缓存中Token为空，获取下Token
						if(accessToken.isEmpty()||accessToken.length()==0)
						{
							WMELMUtilTools.GetTokenInClientCredentials(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName);
						}
						else//判断过期时间
						{
							long curTimestamp = Calendar.getInstance().getTimeInMillis()/1000;//当前时间戳，转成秒
							//定时器执行的间隔
							long job_time = 1800;//1800秒 目前写死吧，30分钟更新下
							//Token有效期-当前时间 小于等于 定时器时间间隔那么就更新

							if(tokenTimestamp-curTimestamp<=job_time)
							{
								WMELMUtilTools.GetTokenInClientCredentials(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName);
							}
							else
							{
								sReturnInfo = "ELM_Token同步没有过期不用更新";
								logger.info("\r\n***************ELM_Token同步没有过期不用更新****************\r\n");
							}
						}
					} 
					catch (Exception e) 
					{
						//保存JOB异常日志  BY JZMA 20190527
						InsertWSLOG.insert_JOBLOG(" "," ", "ELMTokenRefresh", "饿了么外卖取TOKEN", e.getMessage());
						isFail=true;
						continue;							
					}
				}
			
			}
			
			if (!isFail)
			{
				//删除JOB异常日志    BY JZMA 20190527
				InsertWSLOG.delete_JOBLOG(" "," ", "ELMTokenRefresh");
			}
		} 
		catch (Exception e) 
		{
			logger.error("\r\n***************ELM_Token同步异常"+e.getMessage()+"****************\r\n");
			InsertWSLOG.insert_JOBLOG(" "," ", "ELMTokenRefresh", "饿了么外卖取TOKEN", e.getMessage());
		}
		finally 
		{
			bRun=false;//
		}

		logger.info("\r\n***************ELM_Token同步END****************\r\n");

		return sReturnInfo;
	}

}
