package com.dsc.spos.waimai;

import com.dsc.spos.scheduler.job.StaticInfo;

import eleme.openapi.sdk.api.enumeration.order.OInvalidateType;
import eleme.openapi.sdk.api.exception.ServiceException;
import eleme.openapi.sdk.api.service.OrderService;
import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.oauth.response.Token;

public class WMELMOrderProcess 
{
	public final static Config config = new Config(StaticInfo.waimaiELMIsSandbox, StaticInfo.waimaiELMAPPKey, StaticInfo.waimaiELMSecret);

	 public static boolean orderConfirm(String orderNo,StringBuilder errorMessage) throws Exception
	 {
		 boolean nRet = false;
		 Token token = WMELMUtilTools.GetTokenInRedis();
		 if(token == null)
		 {
			 return false;
		 }			
		 try 
		 {
			 OrderService orderService = new OrderService(config, token);
			 orderService.confirmOrderLite(orderNo);
			 return true;
		
	   } 
		 catch (ServiceException e)
		 {
			 if("UNAUTHORIZED".equals(e.getCode()))
			 {
				 WMELMUtilTools.GetTokenInClientCredentials();
				 errorMessage.append(e.getMessage()+"，请重试！");
			 }
			 else
			 {
				 errorMessage.append(e.getMessage());
			 }
			 return false;
		 }
		 catch (Exception e) 
		 {
			 errorMessage.append(e.getMessage());
			 return false;
	   }
					 	
	 }
	
	 public static boolean orderCancel(String orderNo,String reason, String reasonCode, StringBuilder errorMessage) throws Exception
	 {
		 boolean nRet = false;
		 Token token = WMELMUtilTools.GetTokenInRedis();
		 if(token == null)
		 {
			 return false;
		 }			
		 try 
		 {
			 OrderService orderService = new OrderService(config, token);
			 orderService.cancelOrderLite(orderNo, OInvalidateType.others, reason);
			 return true;
		
	   } 
		 catch (ServiceException e)
		 {
			 if("UNAUTHORIZED".equals(e.getCode()))
			 {
				 WMELMUtilTools.GetTokenInClientCredentials();
				 errorMessage.append(e.getMessage()+"，请重试！");
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
	 
	 public static boolean orderRefundAgree(String orderNo,String reason,StringBuilder errorMessage) throws Exception
	 {
		 boolean nRet = false;
		 Token token = WMELMUtilTools.GetTokenInRedis();
		 if(token == null)
		 {
			 return false;
		 }			
		 try 
		 {
			 OrderService orderService = new OrderService(config, token);
			 orderService.agreeRefundLite(orderNo);
			 return true;
		
	   } 
		 catch (ServiceException e)
		 {
			 if("UNAUTHORIZED".equals(e.getCode()))
			 {
				 WMELMUtilTools.GetTokenInClientCredentials();
				 errorMessage.append(e.getMessage()+"，请重试！");
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
	 
	 public static boolean orderRefundReject(String orderNo,String reason, StringBuilder errorMessage) throws Exception
	 {
		 boolean nRet = false;
		 Token token = WMELMUtilTools.GetTokenInRedis();
		 if(token == null)
		 {
			 return false;
		 }			
		 try 
		 {
			 OrderService orderService = new OrderService(config, token);
			 if (reason == null || reason.length() == 0 || reason.trim().length() == 0)
				{
					reason = "不同意退款";
				}
			 orderService.disagreeRefundLite(orderNo, reason);
			 return true;		
	   } 
		 catch (ServiceException e)
		 {
			 if("UNAUTHORIZED".equals(e.getCode()))
			 {
				 WMELMUtilTools.GetTokenInClientCredentials();
				 errorMessage.append(e.getMessage()+"，请重试！");
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

	public static boolean orderPrepared(String orderNo,StringBuilder errorMessage) throws Exception
	{
		boolean nRet = false;
		Token token = WMELMUtilTools.GetTokenInRedis();
		if(token == null)
		{
			return false;
		}
		try
		{
			OrderService orderService = new OrderService(config, token);
			orderService.setOrderPrepared(orderNo);
			return true;

		}
		catch (ServiceException e)
		{
			if("UNAUTHORIZED".equals(e.getCode()))
			{
				WMELMUtilTools.GetTokenInClientCredentials();
				errorMessage.append(e.getMessage()+"，请重试！");
			}
			else
			{
				errorMessage.append(e.getMessage());
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
	 public static boolean orderConfirm(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,StringBuilder errorMessage) throws Exception
	 {
		 boolean nRet = false;
		 Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
		 if(token == null)
		 {
			 return false;
		 }			
		 try 
		 {
			 Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			 OrderService orderService = new OrderService(configCur, token);
			 orderService.confirmOrderLite(orderNo);
			 return true;
		
	   } 
		 catch (ServiceException e)
		 {
			 if("UNAUTHORIZED".equals(e.getCode()))
			 {
				 WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				 errorMessage.append(e.getMessage()+"，请重试！");
			 }
			 else
			 {
				 errorMessage.append(e.getMessage());
			 }
			 return false;
		 }
		 catch (Exception e) 
		 {
			 errorMessage.append(e.getMessage());
			 return false;
	   }
					 	
	 }
	
	 public static boolean orderCancel(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,String reason, String reasonCode, StringBuilder errorMessage) throws Exception
	 {
		 boolean nRet = false;
		 Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
		 if(token == null)
		 {
			 return false;
		 }			
		 try 
		 {
			 Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			 OrderService orderService = new OrderService(configCur, token);
			 orderService.cancelOrderLite(orderNo, OInvalidateType.others, reason);
			 return true;
		
	   } 
		 catch (ServiceException e)
		 {
			 if("UNAUTHORIZED".equals(e.getCode()))
			 {
				 WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				 errorMessage.append(e.getMessage()+"，请重试！");
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
	 
	 public static boolean orderRefundAgree(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,String reason,StringBuilder errorMessage) throws Exception
	 {
		 boolean nRet = false;
		 Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
		 if(token == null)
		 {
			 return false;
		 }			
		 try 
		 {
			 Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			 OrderService orderService = new OrderService(configCur, token);
			 orderService.agreeRefundLite(orderNo);
			 return true;
		
	   } 
		 catch (ServiceException e)
		 {
			 if("UNAUTHORIZED".equals(e.getCode()))
			 {
				 WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				 errorMessage.append(e.getMessage()+"，请重试！");
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
	 
	 public static boolean orderRefundReject(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,String reason, StringBuilder errorMessage) throws Exception
	 {
		 boolean nRet = false;
		 Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
		 if(token == null)
		 {
			 return false;
		 }			
		 try 
		 {
			 if (reason == null || reason.length() == 0 || reason.trim().length() == 0)
				{
					reason = "不同意退款";
				}
			 Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			 OrderService orderService = new OrderService(configCur, token);
			 orderService.disagreeRefundLite(orderNo, reason);
			 return true;		
	   } 
		 catch (ServiceException e)
		 {
			 if("UNAUTHORIZED".equals(e.getCode()))
			 {
				 WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				 errorMessage.append(e.getMessage()+"，请重试！");
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

	public static boolean orderPrepared(boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,StringBuilder errorMessage) throws Exception
	{
		boolean nRet = false;
		Token token = WMELMUtilTools.GetTokenInRedis(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
		if(token == null)
		{
			return false;
		}
		try
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			OrderService orderService = new OrderService(configCur, token);
			orderService.setOrderPrepared(orderNo);
			return true;

		}
		catch (ServiceException e)
		{
			if("UNAUTHORIZED".equals(e.getCode()))
			{
				WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				errorMessage.append(e.getMessage()+"，请重试！");
			}
			else
			{
				errorMessage.append(e.getMessage());
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

	//region 服务商模式-重载上面的方法
	public static boolean orderConfirm(String userId,boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,StringBuilder errorMessage) throws Exception
	{
		boolean nRet = false;
		Token token = WMELMUtilTools.ISV_GetTokenByUserId(userId);
		if(token == null)
		{
			return false;
		}
		try
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			OrderService orderService = new OrderService(configCur, token);
			orderService.confirmOrderLite(orderNo);
			return true;

		}
		catch (ServiceException e)
		{
			if("UNAUTHORIZED".equals(e.getCode()))
			{
				//WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				errorMessage.append(e.getMessage()+"，请重试！");
			}
			else
			{
				errorMessage.append(e.getMessage());
			}
			return false;
		}
		catch (Exception e)
		{
			errorMessage.append(e.getMessage());
			return false;
		}

	}

	public static boolean orderCancel(String userId,boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,String reason, String reasonCode, StringBuilder errorMessage) throws Exception
	{
		boolean nRet = false;
		Token token = WMELMUtilTools.ISV_GetTokenByUserId(userId);
		if(token == null)
		{
			return false;
		}
		try
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			OrderService orderService = new OrderService(configCur, token);
			orderService.cancelOrderLite(orderNo, OInvalidateType.others, reason);
			return true;

		}
		catch (ServiceException e)
		{
			if("UNAUTHORIZED".equals(e.getCode()))
			{
				//WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				errorMessage.append(e.getMessage()+"，请重试！");
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

	public static boolean orderRefundAgree(String userId,boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,String reason,StringBuilder errorMessage) throws Exception
	{
		boolean nRet = false;
		Token token = WMELMUtilTools.ISV_GetTokenByUserId(userId);
		if(token == null)
		{
			return false;
		}
		try
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			OrderService orderService = new OrderService(configCur, token);
			orderService.agreeRefundLite(orderNo);
			return true;

		}
		catch (ServiceException e)
		{
			if("UNAUTHORIZED".equals(e.getCode()))
			{
				//WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				errorMessage.append(e.getMessage()+"，请重试！");
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

	public static boolean orderRefundReject(String userId,boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,String reason, StringBuilder errorMessage) throws Exception
	{
		boolean nRet = false;
		Token token = WMELMUtilTools.ISV_GetTokenByUserId(userId);
		if(token == null)
		{
			return false;
		}
		try
		{
			if (reason == null || reason.length() == 0 || reason.trim().length() == 0)
			{
				reason = "不同意退款";
			}
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			OrderService orderService = new OrderService(configCur, token);
			orderService.disagreeRefundLite(orderNo, reason);
			return true;
		}
		catch (ServiceException e)
		{
			if("UNAUTHORIZED".equals(e.getCode()))
			{
				//WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				errorMessage.append(e.getMessage()+"，请重试！");
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

	public static boolean orderPrepared(String userId,boolean waimaiELMIsSandbox,String waimaiELMAPPKey,String waimaiELMSecret,String waimaiELMAPPName,String orderNo,StringBuilder errorMessage) throws Exception
	{
		boolean nRet = false;
		Token token = WMELMUtilTools.ISV_GetTokenByUserId(userId);
		if(token == null)
		{
			return false;
		}
		try
		{
			Config configCur = new Config(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret);
			OrderService orderService = new OrderService(configCur, token);
			orderService.setOrderPrepared(orderNo);
			return true;

		}
		catch (ServiceException e)
		{
			if("UNAUTHORIZED".equals(e.getCode()))
			{
				//WMELMUtilTools.GetTokenInClientCredentials(waimaiELMIsSandbox, waimaiELMAPPKey, waimaiELMSecret, waimaiELMAPPName);
				errorMessage.append(e.getMessage()+"，请重试！");
			}
			else
			{
				errorMessage.append(e.getMessage());
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
