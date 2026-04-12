package com.dsc.spos.waimai;

import java.util.HashMap;
import java.util.Map;

import com.dsc.spos.scheduler.job.StaticInfo;

import eleme.openapi.sdk.api.entity.shop.OShop;
import eleme.openapi.sdk.api.entity.user.OUser;
import eleme.openapi.sdk.api.enumeration.shop.OShopProperty;
import eleme.openapi.sdk.api.exception.ServiceException;
import eleme.openapi.sdk.api.service.OrderService;
import eleme.openapi.sdk.api.service.ShopService;
import eleme.openapi.sdk.api.service.UserService;
import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.config.ElemeSdkLogger;
import eleme.openapi.sdk.oauth.response.Token;

public class WMELMShopService 
{
	public final static Config config = new Config(StaticInfo.waimaiELMIsSandbox, StaticInfo.waimaiELMAPPKey, StaticInfo.waimaiELMSecret);

	/**
	 * 获取商户账号信息
	 * @param errorMeassge 错误信息
	 * @return
	 * @throws Exception
	 */
	public static OUser getUser(StringBuilder errorMessage) throws Exception
	{		
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
	
			UserService userService = new UserService(config, token);
			config.setLog(new ElemeSdkLogger() {
			
			@Override
			public void info(String message) {
			// TODO Auto-generated method stub
				try {
					HelpTools.writelog_fileName("获取门店req:"+message,"ShopsSaveLocal");
			
			} catch (Exception e) {
			// TODO: handle exception
			}
			
			}
			
			@Override
			public void error(String message) {
			// TODO Auto-generated method stub
				try {
					HelpTools.writelog_fileName("获取门店res:"+message,"ShopsSaveLocal");
			
			} catch (Exception e) {
			// TODO: handle exception
			}
			
			}
		});
			OUser nRet = userService.getUser();		
			return nRet;
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials();
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 查询店铺信息
	 * @param shopId 店铺Id
	 * @param errorMeassge
	 * @return
	 * @throws Exception
	 */
	public static OShop getShop(long shopId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
	
			ShopService shopService = new ShopService(config, token);
			OShop	nRet = shopService.getShop(shopId);		
			return nRet;
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials();
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}
		
	}
	
	/**
	 * 获取门店营业状态 0 未营业，1 正在营业，-1异常
	 * @param shopId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static int getShopStatus(long shopId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return -1;
		}
		try 
		{
	
			ShopService shopService = new ShopService(config, token);
			OShop	nRet = shopService.getShop(shopId);		
			return nRet.getIsOpen();
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials();
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return -1;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return -1;
		}
		
	}
	
	
	/**
	 * 更新店铺基本信息
	 * @param shopId 店铺Id
	 * @param erpShopId 映射的erp门店编号
	 * @param errorMeassge
	 * @return
	 * @throws Exception
	 */
	public static boolean updateShop(long shopId,String erpShopId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}
		try 
		{
				
			ShopService shopService = new ShopService(config, token);
			Map<OShopProperty,Object> properties = new HashMap<OShopProperty,Object>();
			properties.put(OShopProperty.openId,erpShopId);
			shopService.updateShop(shopId, properties);					
			return true;
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials();
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}
	}
	
	
	/**
	 * 更新店铺营业状态 1表示营业，0表示不营业
	 * @param shopId
	 * @param status
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateShopStatus(long shopId,int status, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis();
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}
		try 
		{
				
			ShopService shopService = new ShopService(config, token);
			Map<OShopProperty,Object> properties = new HashMap<OShopProperty,Object>();
			properties.put(OShopProperty.isOpen,status);
			shopService.updateShop(shopId, properties);					
			return true;
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials();
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}
	}
	
	
//region 重载上面的方法
	/**
	 * 根据传入APPKey获取下面的所有门店ID
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OUser getUser(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,StringBuilder errorMessage) throws Exception
	{		
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{			
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			UserService userService = new UserService(configCur, token);	
			configCur.setLog(new ElemeSdkLogger()
			{
				
				@Override
				public void info(String message)
				{
					// TODO Auto-generated method stub
					try {
						HelpTools.writelog_fileName("获取ELM应用下门店:"+message,"ShopsSaveLocal");

					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
				
				@Override
				public void error(String message)
				{
					// TODO Auto-generated method stub
					try {
						HelpTools.writelog_fileName("获取ELM应用下门店error:"+message,"ShopsSaveLocal");

					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
			});
			OUser nRet = userService.getUser();		
			return nRet;
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 根据传入APPKey获取门店详细信息
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param shopId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OShop getShop(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long shopId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return null;
		}
		try 
		{
			
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
	
			ShopService shopService = new ShopService(configCur, token);
			OShop	nRet = shopService.getShop(shopId);		
			return nRet;
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return null;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return null;
		}
		
	}
	
	/**
	 * 获取门店营业状态 0 未营业，1 正在营业，-1异常
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param shopId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static int getShopStatus(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long shopId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return -1;
		}
		try 
		{
			
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
	
			ShopService shopService = new ShopService(configCur, token);
			OShop	nRet = shopService.getShop(shopId);		
			return nRet.getIsOpen();//0 未营业，1 正在营业
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return -1;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return -1;
		}
		
	}
	
	/**
	 * 根据传入APPKey更新门店映射
	 * @param waimaiELMIsSandbox
	 * @param waimaiELMAPPKey
	 * @param waimaiELMSecret
	 * @param waimaiELMAPPName
	 * @param shopId
	 * @param erpShopId
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateShop(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long shopId,String erpShopId, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ShopService shopService = new ShopService(configCur, token);
			Map<OShopProperty,Object> properties = new HashMap<OShopProperty,Object>();
			properties.put(OShopProperty.openId,erpShopId);
			shopService.updateShop(shopId, properties);					
			return true;
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}
	}
	
	
	/**
	 * 更新店铺营业状态 1表示营业，0表示不营业
	 * @param shopId
	 * @param status
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateShopStatus(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,long shopId,int status, StringBuilder errorMessage) throws Exception
	{
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
		if (token == null) 
		{
			errorMessage.append("【获取Token】失败！");
			return false;
		}
		try 
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			ShopService shopService = new ShopService(configCur, token);
			Map<OShopProperty,Object> properties = new HashMap<OShopProperty,Object>();
			properties.put(OShopProperty.isOpen,status);
			shopService.updateShop(shopId, properties);					
			return true;
	
		} 
		catch (ServiceException e) 
		{
			if (e.getCode().equals("UNAUTHORIZED")) 
			{
			  WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret,waimaiELMAPPName);
			  errorMessage.append(e.getMessage() + "，请重试！");
			} 
			else 
			{
				errorMessage.append(e.getMessage()+"("+e.getCode()+")");
			}
			return false;
		} 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			return false;
		}
	}
	
	
	
	
	//endregion
	
	
	
	
}
