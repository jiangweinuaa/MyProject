package com.dsc.spos.waimai;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sankuai.meituan.waimai.opensdk.vo.OrderDetailParam;
import org.json.JSONObject;

import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.sankuai.meituan.waimai.opensdk.exception.ApiOpException;
import com.sankuai.meituan.waimai.opensdk.exception.ApiSysException;
import com.sankuai.meituan.waimai.opensdk.factory.APIFactory;
import com.sankuai.meituan.waimai.opensdk.vo.SystemParam;

public class WMMTOrderProcess {
	//public final static SystemParam sysPram = new SystemParam(StaticInfo.waimaiMTAPPID, StaticInfo.waimaiMTSignKey);

  public static boolean orderConfirm(String orderNo,StringBuilder errorMessage)   
  { 
    //成功{"data":"ok"}
  	//失败{"code":错误码, "msg":"错误详情"}
	  String result = "";
	  boolean nRet = false;
  	try 
  	{		
  		Long orderId =new Long(orderNo); 			
      result = APIFactory.getOrderAPI().orderConfirm(getSystemParam(), orderId);//直接返回ok
      return true;
      
  	} 
  	catch (ApiOpException e) 
  	{
       
       errorMessage.append(e.getMsg());
       return false;
    }
  	catch (ApiSysException e) 
  	{ 		 
              
       errorMessage.append(e.getExceptionEnum().getMsg());    
       return false;
       
    } 	
  	catch (Exception e) 
  	{
  		
  		errorMessage.append(e.getMessage());
      return false;
  	}
  		 	
   }
	
  public static boolean orderCancel(String orderNo,String reason, String reasonCode, StringBuilder errorMessage)
  {
  //成功{"data":"ok"}
  	//失败{"code":错误码, "msg":"错误详情"}
	  String result = "";
	  boolean nRet = false;
  	try 
  	{		
  		if (reason == null || reason.length() == 0 || reason.trim().length() == 0)
  		{
  			//聚宝盆就是个垃圾，自己调用美团接口，reason，reasonCode 明明可以为空的，反而到这不能为空了
				reason = "其他原因";
  		}
			reasonCode = "2007";//理由码 状态值 详见聚宝盆开发文档（补充相关字段说明）
  		Long orderId =new Long(orderNo); 			
      result = APIFactory.getOrderAPI().orderCancel(getSystemParam(), orderId, reason, reasonCode);
      return true;
      
  	} 
  	catch (ApiOpException e) 
  	{
       
       errorMessage.append(e.getMsg());
       return false;
    }
  	catch (ApiSysException e) 
  	{ 		 
              
       errorMessage.append(e.getExceptionEnum().getMsg());    
       return false;
       
    }	
  	catch (Exception e) 
  	{
  		
  		errorMessage.append(e.getMessage());
      return false;
  	}
  	 	
  }
  
  public static boolean orderRefundAgree(String orderNo,String reason, StringBuilder errorMessage)
  {
  //成功{"data":"ok"}
  	//失败{"code":错误码, "msg":"错误详情"}
	  String result = "";
	  boolean nRet = false;
  	try 
  	{		
  		if(reason==null||reason.trim().isEmpty())
  		{
  			reason = "商家同意退单！";
  		}
  		Long orderId =new Long(orderNo); 			
      result = APIFactory.getOrderAPI().orderRefundAgree(getSystemParam(), orderId, reason);
      return true;
      
  	} 
  	catch (ApiOpException e) 
  	{
       
       errorMessage.append(e.getMsg());
       return false;
    }
  	catch (ApiSysException e) 
  	{ 		 
              
       errorMessage.append(e.getExceptionEnum().getMsg());    
       return false;
       
    }	
  	catch (Exception e) 
  	{
  		
  		errorMessage.append(e.getMessage());
      return false;
  	}
  	 	
  }
  
  public static boolean orderRefundReject(String orderNo,String reason, StringBuilder errorMessage)
  {
  //成功{"data":"ok"}
  	//失败{"code":错误码, "msg":"错误详情"}
	  String result = "";
	  boolean nRet = false;
  	try 
  	{		
  		if(reason==null||reason.trim().isEmpty())
  		{
  			reason = "商家拒绝退单！";
  		}
  		Long orderId =new Long(orderNo); 			
      result = APIFactory.getOrderAPI().orderRefundReject(getSystemParam(), orderId, reason);
      return true;
      
  	} 
  	catch (ApiOpException e) 
  	{
       
       errorMessage.append(e.getMsg());
       return false;
    }
  	catch (ApiSysException e) 
  	{ 		 
              
       errorMessage.append(e.getExceptionEnum().getMsg());    
       return false;
       
    }	
  	catch (Exception e) 
  	{
  		
  		errorMessage.append(e.getMessage());
      return false;
  	} 	  
  	
  }
  
  public static String orderBatchPullPhoneNumber(String app_poi_code, Integer offset, Integer limit, StringBuilder errorMessage)
  {
  //成功{"data":"ok"}
  	//失败{"code":错误码, "msg":"错误详情"}
	  String result = "";
  	try 
  	{		 				
      result = APIFactory.getOrderAPI().orderBatchPullPhoneNumber(getSystemParam(), app_poi_code, offset, limit);
      return result;
      
  	} 
  	catch (ApiOpException e) 
  	{
       
       errorMessage.append(e.getMsg());
       return errorMessage.toString();
    }
  	catch (ApiSysException e) 
  	{ 		 
              
       errorMessage.append(e.getExceptionEnum().getMsg());    
       return errorMessage.toString();
       
    }	
  	catch (Exception e) 
  	{
  		
  		errorMessage.append(e.getMessage());
      return errorMessage.toString();
  	}
  	 	
  }
  
  private static SystemParam getSystemParam() throws Exception
  {
		String appId = "";
		String appSecret = "";
		String eId = "";
		List<Map<String, Object>> elmAppKeyList = PosPub.getWaimaiAppConfig(StaticInfo.dao, eId,
				orderLoadDocType.MEITUAN);
		if (elmAppKeyList != null && elmAppKeyList.size() > 0)
		{
			appId = elmAppKeyList.get(0).get("APIKEY").toString();
			appSecret = elmAppKeyList.get(0).get("APISECRET").toString();
		} else
		{
			appId = StaticInfo.waimaiMTAPPID;
			appSecret = StaticInfo.waimaiMTSignKey;
		}

		SystemParam sysPram = new SystemParam(appId, appSecret);

		return sysPram;
	}

	/**
	 * 商家确认已出餐接口
	 * @param orderNo
	 * @param errorMessage
	 * @return
	 */
	public static boolean orderPreparationMealComplete(String orderNo,StringBuilder errorMessage)
	{
		//成功{"data":"ok"}
		//失败{"code":错误码, "msg":"错误详情"}
		String result = "";
		boolean nRet = false;
		try
		{
			//Long orderId =new Long(orderNo);
			result = APIFactory.getOrderAPI().orderPreparationMealComplete(getSystemParam(), orderNo);//直接返回ok
			return true;

		}
		catch (ApiOpException e)
		{

			errorMessage.append(e.getMsg());
			return false;
		}
		catch (ApiSysException e)
		{

			errorMessage.append(e.getExceptionEnum().getMsg());
			return false;

		}
		catch (Exception e)
		{

			errorMessage.append(e.getMessage());
			return false;
		}

	}

	/**
	 * 查询订单详细信息
	 * @param orderNo
	 * @param errorMessage
	 * @return
	 */
	public static String getOrderDetail(String orderNo,StringBuilder errorMessage) {
		String res = "";
		try
		{
			Long orderId = new Long(orderNo);
			long is_mt_logistics = new Long("0");
			//SystemParam sysPram = new SystemParam("6817", "13f54f83e9059a14f110619762cb4f59");
			OrderDetailParam result = APIFactory.getOrderAPI().orderGetOrderDetail(getSystemParam(), orderId, is_mt_logistics);
			Map<String, Object> map = new HashMap<>();
			map = JSON.parseObject(JSON.toJSONString(result), Map.class);
			for (Map.Entry<String, Object> item : map.entrySet()) {
				if (res.isEmpty()) {
					res = item.getKey() + "=" + getURLEncoderString(item.getValue().toString());
				} else {
					res = res + "&" + item.getKey() + "=" + getURLEncoderString(item.getValue().toString());
				}

			}

		} catch (ApiOpException e) {

			errorMessage.append(e.getMsg());
			return res;
		} catch (ApiSysException e) {

			errorMessage.append(e.getExceptionEnum().getMsg());
			return res;

		} catch (Exception e) {
			errorMessage.append(e.getMessage());
		}
		return res;
	}


	private static String getURLEncoderString(String str) {
		String result = "";
		if (null == str) {
			return "";
		}
		try {
			result = java.net.URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {

		}
		return result;
	}

}
