package com.dsc.spos.waimai;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;

import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;

import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.oauth.OAuthClient;
import eleme.openapi.sdk.oauth.response.Token;

public class WMELMUtilTools 
{
	public final static Config config = new Config(true,"","");//不在支持配置文件   new Config(StaticInfo.waimaiELMIsSandbox, StaticInfo.waimaiELMAPPKey, StaticInfo.waimaiELMSecret);
 	
	public static Token CurToken = null;
	public static String redis_key = "ELM_Token";
	/**
	 * 饿了么服务商模式token(与userId关联)
	 */
	public static Map<String, String> elmTokenListByUserId = null;
	private static  String logFileName = "ISV_ELMToken";
	//请求获取Token 并且存入缓存
	public static Token GetTokenInClientCredentials() throws Exception
	{
		/*OAuthClient client = new OAuthClient(config);
		Token token = client.getTokenInClientCredentials();
		if (token != null && token.isSuccess())
		{
			CurToken = token;
			int exprieSeconds = new Long(token.getExpires()).intValue();//过期时间秒
			long curTimestamp = Calendar.getInstance().getTimeInMillis()/1000;//当前时间戳，转成秒
			long exprieTimestamp = curTimestamp + exprieSeconds;
			String accesstoken = token.getAccessToken()+"&"+exprieTimestamp;//拼接上过期时间的时间戳
			RedisPosPub redis = new RedisPosPub();		
			boolean isexistHashkey = redis.IsExistStringKey(redis_key);
			if (isexistHashkey) 
			{
				redis.DeleteKey(redis_key);//
				HelpTools.writelog_waimai("【删除存在ELM_Token的缓存】成功！");
			}
			boolean nret = redis.setString(redis_key, accesstoken);
			if (nret) 
			{
				HelpTools.writelog_waimai("【写ELM_Token缓存】成功！"+ accesstoken);
			} 
			else 
			{
				HelpTools.writelog_waimai("【写ELM_Token缓存】失败！"+ accesstoken);
			}			
			boolean nret2 =	redis.setExpire(redis_key,exprieSeconds)	;
			if (nret2) 
			{
				HelpTools.writelog_waimai("【设置ELM_Token缓存过期时间】成功！"+ exprieSeconds);
			} 
			else 
			{
				HelpTools.writelog_waimai("【设置ELM_Token缓存过期时间】失败！"+ exprieSeconds);
			}
			//redis.Close();
		}	
		return token;	*/	
		
		return null;
	}
		
	public static Token GetTokenInRedis() throws Exception
	{
		try 
		{
			Token redis_token = new Token();
			RedisPosPub redis = new RedisPosPub();	
			String accessTokenStr = redis.getString(redis_key);//包含了过期时间戳
			//redis.Close();		
			String accessToken="";
			try 
			{
				String[] ss =accessTokenStr.split("&");
				accessToken = ss[0];
		  } 
			catch (Exception e) 
			{
		
		  }
			
			if(accessToken == null ||accessToken.length()==0)
			{
				redis_token = GetTokenInClientCredentials();
			}	
			else
			{
				redis_token.setAccessToken(accessToken);
			}			
			CurToken = redis_token;
			return redis_token;
		
	  } 
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【获取Token】失败！"+ e.getMessage());
			return null;
	  }
		
		
	}
	
	
	
  //region 重载之前的方法
	
	/**
	 * 根据传入APPKey参数获取相应的token
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @return
	 * @throws Exception
	 */
	public static Token GetTokenInRedis(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName) throws Exception
	{
		try 
		{
			Token redis_token = new Token();
			RedisPosPub redis = new RedisPosPub();
			String CurRedisKey = redis_key + "_" + waimaiELMAPPKey;
			String accessTokenStr = redis.getString(CurRedisKey);//包含了过期时间戳
			//redis.Close();		
			String accessToken="";
			try 
			{
				String[] ss =accessTokenStr.split("&");
				accessToken = ss[0];
		  } 
			catch (Exception e) 
			{
		
		  }
			
			if(accessToken == null ||accessToken.length()==0)
			{
				redis_token = GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
			}	
			else
			{
				redis_token.setAccessToken(accessToken);
			}			
			CurToken = redis_token;
			return redis_token;
		
	  } 
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【获取Token】失败！"+ e.getMessage()+"应用名称："+waimaiELMAPPName+" APPKey："+waimaiELMAPPKey+" APPSecret:"+waimaiELMSecret);
			return null;
	  }
							
	}
	
	/**
	 * 根据传入APPKey获取相应的token
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @return
	 * @throws Exception
	 */
	public static Token GetTokenInClientCredentials(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName) throws Exception
	{
		Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
		OAuthClient client = new OAuthClient(configCur);
		Token token = client.getTokenInClientCredentials();
		if (token != null && token.isSuccess())
		{
			CurToken = token;
			int exprieSeconds = new Long(token.getExpires()).intValue();//过期时间秒
			long curTimestamp = Calendar.getInstance().getTimeInMillis()/1000;//当前时间戳，转成秒
			long exprieTimestamp = curTimestamp + exprieSeconds;
			String accesstoken = token.getAccessToken()+"&"+exprieTimestamp;//拼接上过期时间的时间戳
			String CurRedisKey = redis_key + "_" + waimaiELMAPPKey;
			RedisPosPub redis = new RedisPosPub();		
			boolean isexistHashkey = redis.IsExistStringKey(CurRedisKey);
			if (isexistHashkey) 
			{
				redis.DeleteKey(CurRedisKey);//
				HelpTools.writelog_waimai("【删除存在ELM_Token的缓存】成功！" +" redis_key:"+CurRedisKey);
			}
			boolean nret = redis.setString(CurRedisKey, accesstoken);
			if (nret) 
			{
				HelpTools.writelog_waimai("【写ELM_Token缓存】成功！"+ accesstoken+" redis_key:"+CurRedisKey);
			} 
			else 
			{
				HelpTools.writelog_waimai("【写ELM_Token缓存】失败！"+ accesstoken+" redis_key:"+CurRedisKey);
			}			
			boolean nret2 =	redis.setExpire(CurRedisKey,exprieSeconds)	;
			if (nret2) 
			{
				HelpTools.writelog_waimai("【设置ELM_Token缓存过期时间】成功！"+ exprieSeconds+" redis_key:"+CurRedisKey);
			} 
			else 
			{
				HelpTools.writelog_waimai("【设置ELM_Token缓存过期时间】失败！"+ exprieSeconds+" redis_key:"+CurRedisKey);
			}
			//redis.Close();
		}	
		return token;		
	}
	
	//endregion


	public static Token ISV_GetTokenByUserId(String userId) throws Exception
	{
		try
		{
			Token redis_token = new Token();
			String accessTokenStr = "";//包含了过期时间戳
			if (elmTokenListByUserId!=null)
			{
				accessTokenStr = elmTokenListByUserId.getOrDefault(userId,"").toString();
			}
			long curTimestmap = System.currentTimeMillis()/1000;

			//内存没有，数据中取
			if (accessTokenStr==null||accessTokenStr.trim().isEmpty())
			{
				Map<String,Object> mapToken = getDBTokenByUserId(userId);
				if (mapToken != null && !mapToken.isEmpty())
				{
					String access_token = mapToken.get("ACCESS_TOKEN").toString();
					String expires_in = mapToken.get("ACCESS_TOKEN_EXPIRES_IN").toString();
					accessTokenStr = access_token+"&"+expires_in;
					long token_expires_in = Long.parseLong(expires_in);
					if (token_expires_in>=curTimestmap)
					{
						if (elmTokenListByUserId==null)
						{
							elmTokenListByUserId = new HashMap<>();
						}
						elmTokenListByUserId.put(userId,accessTokenStr);
					}

					redis_token.setAccessToken(access_token);
					return redis_token;
					/*if (token_expires_in>=curTimestmap)
					{

					}
					else
					{
						HelpTools.writelog_fileName("【从客户端数据库获取Token】查询token已经过期,token有效期时间戳="+ expires_in+",当前系统时间戳="+curTimestmap+",商户授权userId："+userId,logFileName);
						//本地token过期了，需要查询服务端
						return null;
					}*/

				}
			}

			String accessToken="";
			long token_expires_in = 0L;
			try
			{
				String[] ss =accessTokenStr.split("&");
				accessToken = ss[0];
				token_expires_in = Long.parseLong(ss[1]);
			}
			catch (Exception e)
			{

			}

			if(accessToken == null ||accessToken.length()==0)
			{
				return null;
				//redis_token = GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
			}
			else
			{
				if (token_expires_in>=curTimestmap)
				{
					redis_token.setAccessToken(accessToken);
					return redis_token;
				}
				else
				{
					HelpTools.writelog_fileName("【从内存中获取Token】token已经过期,【从数据库中查询】token有效期时间戳="+ token_expires_in+",当前系统时间戳="+curTimestmap+",商户授权userId："+userId,logFileName);
					Map<String,Object> mapToken = getDBTokenByUserId(userId);
					if (mapToken != null && !mapToken.isEmpty())
					{
						String access_token = mapToken.get("ACCESS_TOKEN").toString();
						String expires_in = mapToken.get("ACCESS_TOKEN_EXPIRES_IN").toString();
						accessTokenStr = access_token+"&"+expires_in;
						token_expires_in = Long.parseLong(expires_in);
						if (token_expires_in>=curTimestmap)
						{
							if (elmTokenListByUserId==null)
							{
								elmTokenListByUserId = new HashMap<>();
							}
							elmTokenListByUserId.put(userId,accessTokenStr);
						}
						redis_token.setAccessToken(access_token);
						return redis_token;
					}
					else
					{
						return null;
					}

				}


			}



		}
		catch (Exception e)
		{
			HelpTools.writelog_fileName("【获取Token】失败！"+ e.getMessage()+"商户授权userId："+userId,logFileName);
			return null;
		}

	}

	/**
	 * 从数据库中获取token
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> getDBTokenByUserId(String userId) throws Exception
	{
		Map<String,Object> mapToken = new HashMap<>();
		//数据库查询获取
		String sql = " select ACCESS_TOKEN,ACCESS_TOKEN_EXPIRES_IN from DCP_ISVWM_ELM_TOKEN where USERID='"+userId+"'";
		HelpTools.writelog_fileName("【从客户端数据库获取Token】查询sql语句："+ sql+",商户授权userId："+userId,logFileName);
		List<Map<String,Object>> getClientTokenList = StaticInfo.dao.executeQuerySQL(sql,null);
		if (getClientTokenList != null && !getClientTokenList.isEmpty())
		{
			HelpTools.writelog_fileName("【从客户端数据库获取Token】查询完成,商户授权userId："+userId,logFileName);
			mapToken = getClientTokenList.get(0);
			return mapToken;
		}
		else
		{
			HelpTools.writelog_fileName("【从客户端数据库获取Token】查询为空,商户授权userId："+userId,logFileName);
			return null;
		}
	}
}
