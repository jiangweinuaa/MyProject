package com.dsc.spos.waimai.dianwoda;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.waimai.HelpTools;


public class DianwodaService {

	public static String dwdLogFileName = "dianwodalog";
	
	/**
	 * 创建订单
	 * @param appkey
	 * @param app_secret
	 * @param access_token
	 * @param IsSandBox
	 * @param param
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static DianwodaOrderCreateResult OrderCreate(String appkey, String app_secret, String access_token,String IsSandBox,DianwodaOrderCreateParam param, StringBuilder errorMessage)  throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
		
			String api = "dianwoda.order.create";
			if(param==null)
			{
				errorMessage.append("请求内容为空！");
				return null;
			}
			//region 必传字段检查
			boolean IsNeedRefParm = false;
			if(param.getOrder_original_id()==null||param.getOrder_original_id().trim().isEmpty())
			{
				IsNeedRefParm = true;
				errorMessage.append("订单号order_original_id不能为空，");
			}
			if(param.getOrder_create_time()==null)
			{
				IsNeedRefParm = true;
				errorMessage.append("订单创建时间戳order_create_time不能为空，");
			}
			
			if(IsNeedRefParm)
			{
				return null;
			}
			//endregion
			
			//测试环境 联调时，order_original_id请加上需要加上'${appkey}_'前缀
			//seller_id请加上'${appkey}_'前缀
			if(IsSandBox!=null&&IsSandBox.equals("Y"))
			{
				param.setOrder_original_id(appkey+"_"+param.getOrder_original_id());
				param.setSeller_id(appkey+"_"+param.getSeller_id());
				
				param.setCity_code("330100");
				param.setSeller_lat(30.2764454);
				param.setSeller_lng(120.111227);
				param.setConsignee_lat(30.2764454);
				param.setConsignee_lng(120.111227);
				
			}
			
			String parm_json = JSON.toJSONString(param);
			HelpTools.writelog_fileName("【开始调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id(), dwdLogFileName);
			String result = DianwodaHttpClientUtil.sendPostRequest(appkey, app_secret, access_token, IsSandBox, api, parm_json);
			HelpTools.writelog_fileName("【结束调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id()+" 返回："+result, dwdLogFileName);
			DianwodaClientResponse clientRes = JSON.parseObject(result,DianwodaClientResponse.class);
	    String res_code = clientRes.getCode();
	    if(res_code.equals("success")==false)
	    {
	    	errorMessage.append(clientRes.getMessage());
	    	if(clientRes.getSub_message()!=null)
	    	{
	    		errorMessage.append("("+clientRes.getSub_message()+")")
;	    	}
	    	return null;
	    }
	    String res_data= clientRes.getData();
	    
	    DianwodaOrderCreateResult dataObj = JSON.parseObject(res_data, DianwodaOrderCreateResult.class);
	   
	    return dataObj;
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			errorMessage.append(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 取消订单 （上游商户可以调用此接口，撤销未开始配送的订单需求）
	 * 调用条件：订单状态：点我达骑手离店配送前（已下单、已接单、已到店）
	 * @param appkey
	 * @param app_secret
	 * @param access_token
	 * @param IsSandBox
	 * @param param
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean OrderCancel(String appkey, String app_secret, String access_token,String IsSandBox,DianwodaOrderCancelParam param, StringBuilder errorMessage)  throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
		
			String api = "dianwoda.order.cancel";
			if(param==null)
			{
				errorMessage.append("请求内容为空！");
				return false;
			}
		  //region 必传字段检查
			boolean IsNeedRefParm = false;
			if(param.getOrder_original_id()==null||param.getOrder_original_id().trim().isEmpty())
			{
				IsNeedRefParm = true;
				errorMessage.append("订单号order_original_id不能为空，");
			}
			if(param.getCancel_type()==null)
			{
				IsNeedRefParm = true;
				errorMessage.append("取消类型cancel_type不能为空，");
			}
			if(param.getCancel_reason()==null||param.getCancel_reason().trim().isEmpty())
			{
				IsNeedRefParm = true;
				errorMessage.append("取消原因cancel_reason不能为空，");
			}
			
			if(IsNeedRefParm)
			{
				return false;
			}
			//endregion
			
		  //测试环境 联调时，order_original_id请加上需要加上  ${appkey}_ 前缀
			if(IsSandBox!=null&&IsSandBox.equals("Y"))
			{
				param.setOrder_original_id(appkey+"_"+param.getOrder_original_id());
			}
			
			String parm_json = JSON.toJSONString(param);
			HelpTools.writelog_fileName("【开始调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id(), dwdLogFileName);
			String result = DianwodaHttpClientUtil.sendPostRequest(appkey, app_secret, access_token, IsSandBox, api, parm_json);
			HelpTools.writelog_fileName("【结束调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id()+" 返回："+result, dwdLogFileName);
			DianwodaClientResponse clientRes = JSON.parseObject(result,DianwodaClientResponse.class);
	    String res_code = clientRes.getCode();
	    if(res_code.equals("success"))
	    {
	    	return true;
	    	
	    }
	    else
	    {    	
	    	errorMessage.append(clientRes.getMessage());
	    	if(clientRes.getSub_message()!=null)
	    	{
	    		errorMessage.append("("+clientRes.getSub_message()+")")
;	    	}
	    	return false;
	    }   
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			errorMessage.append(e.getMessage());
		}
		return false;
	}
	
	/**
	 * 门店确认已出餐
	 * 调用条件：下单成功后即可调用
	 * @param appkey
	 * @param app_secret
	 * @param access_token
	 * @param IsSandBox
	 * @param param
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean OrderMealdone(String appkey, String app_secret, String access_token,String IsSandBox,DianwodaOrderMealdoneParam param, StringBuilder errorMessage)  throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
		
			String api = "dianwoda.order.mealdone";
			if(param==null)
			{
				errorMessage.append("请求内容为空！");
				return false;
			}
		  //region 必传字段检查
			boolean IsNeedRefParm = false;
			if(param.getOrder_original_id()==null||param.getOrder_original_id().trim().isEmpty())
			{
				IsNeedRefParm = true;
				errorMessage.append("订单号order_original_id不能为空，");
			}
			if(param.getTime_meal_ready()==null)
			{
				IsNeedRefParm = true;
				errorMessage.append("确认已出餐时间戳time_meal_ready不能为空，");
			}
			
			
			if(IsNeedRefParm)
			{
				return false;
			}
			//endregion
			
			 //测试环境 联调时，order_original_id请加上需要加上  ${appkey}_ 前缀
			if(IsSandBox!=null&&IsSandBox.equals("Y"))
			{
				param.setOrder_original_id(appkey+"_"+param.getOrder_original_id());
			}
			
			String parm_json = JSON.toJSONString(param);
			HelpTools.writelog_fileName("【开始调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id(), dwdLogFileName);
			String result = DianwodaHttpClientUtil.sendPostRequest(appkey, app_secret, access_token, IsSandBox, api, parm_json);
			HelpTools.writelog_fileName("【结束调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id()+" 返回："+result, dwdLogFileName);
			DianwodaClientResponse clientRes = JSON.parseObject(result,DianwodaClientResponse.class);
	    String res_code = clientRes.getCode();
	    if(res_code.equals("success"))
	    {
	    	return true;
	    	
	    }
	    else
	    {    	
	    	errorMessage.append(clientRes.getMessage());
	    	if(clientRes.getSub_message()!=null)
	    	{
	    		errorMessage.append("("+clientRes.getSub_message()+")")
;	    	}
	    	return false;
	    }   
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			errorMessage.append(e.getMessage());
		}
		return false;
	}
	
	/**
	 * 骑手完成配送前，上游接入方可对针对某个订单增加小费（默认禁用，若要开启可联系点我达运营）。
	 * 调用条件
	 * 订单状态：点我达骑手离店配送前（已下单、已接单、已到店）；
	 * 金额限制：总额不超过50元RMB；
	 * 多次增加：1.同一个订单order_original_id，本次增加小费总金额会更新（覆盖）上次的小费总金额，且本次金额不可小于上次金额；2.本次增加金额，相比于上次金额，不可大于20元RMB。
	 * @param appkey
	 * @param app_secret
	 * @param access_token
	 * @param IsSandBox
	 * @param param
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean OrderTip(String appkey, String app_secret, String access_token,String IsSandBox,DianwodaOrderTipParam param, StringBuilder errorMessage)  throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
		
			String api = "dianwoda.order.tip";
			if(param==null)
			{
				errorMessage.append("请求内容为空！");
				return false;
			}
		  //region 必传字段检查
			boolean IsNeedRefParm = false;
			if(param.getOrder_original_id()==null||param.getOrder_original_id().trim().isEmpty())
			{
				IsNeedRefParm = true;
				errorMessage.append("订单号order_original_id不能为空，");
			}
			if(param.getTip()==null)
			{
				IsNeedRefParm = true;
				errorMessage.append("小费总金额tip不能为空，");
			}
			
			
			if(IsNeedRefParm)
			{
				return false;
			}
			//endregion
			
			 //测试环境 联调时，order_original_id请加上需要加上  ${appkey}_ 前缀
			if(IsSandBox!=null&&IsSandBox.equals("Y"))
			{
				param.setOrder_original_id(appkey+"_"+param.getOrder_original_id());
			}
			
			String parm_json = JSON.toJSONString(param);
			HelpTools.writelog_fileName("【开始调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id(), dwdLogFileName);
			String result = DianwodaHttpClientUtil.sendPostRequest(appkey, app_secret, access_token, IsSandBox, api, parm_json);
			HelpTools.writelog_fileName("【结束调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id()+" 返回："+result, dwdLogFileName);
			DianwodaClientResponse clientRes = JSON.parseObject(result,DianwodaClientResponse.class);
	    String res_code = clientRes.getCode();
	    if(res_code.equals("success"))
	    {
	    	return true;
	    	
	    }
	    else
	    {    	
	    	errorMessage.append(clientRes.getMessage());
	    	if(clientRes.getSub_message()!=null)
	    	{
	    		errorMessage.append("("+clientRes.getSub_message()+")")
;	    	}
	    	return false;
	    }   
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			errorMessage.append(e.getMessage());
		}
		return false;
	}
	
	/**
	 * 订单运费预估
	 * @param appkey
	 * @param app_secret
	 * @param access_token
	 * @param IsSandBox
	 * @param param
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static DianwodaOrderCostEstimateResult OrderCostEstimate(String appkey, String app_secret, String access_token,String IsSandBox,DianwodaOrderCostEstimateParam param, StringBuilder errorMessage)  throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
		
			String api = "dianwoda.order.cost.estimate";
			if(param==null)
			{
				errorMessage.append("请求内容为空！");
				return null;
			}
			//region 必传字段检查
			boolean IsNeedRefParm = false;
			if(param.getCity_code()==null||param.getCity_code().trim().isEmpty())
			{
				IsNeedRefParm = true;
				errorMessage.append("行政区划代码city_code不能为空，");
			}
			if(param.getSeller_id()==null||param.getSeller_id().trim().isEmpty())
			{
				IsNeedRefParm = true;
				errorMessage.append("门店编号seller_id不能为空，");
			}
			
			if(IsNeedRefParm)
			{
				return null;
			}
			//endregion
			
			String parm_json = JSON.toJSONString(param);
			HelpTools.writelog_fileName("【开始调用dianwoda接口】:"+api, dwdLogFileName);
			String result = DianwodaHttpClientUtil.sendPostRequest(appkey, app_secret, access_token, IsSandBox, api, parm_json);
			HelpTools.writelog_fileName("【结束调用dianwoda接口】:"+api+" 返回："+result, dwdLogFileName);
			DianwodaClientResponse clientRes = JSON.parseObject(result,DianwodaClientResponse.class);
	    String res_code = clientRes.getCode();
	    if(res_code.equals("success")==false)
	    {
	    	errorMessage.append(clientRes.getMessage());
	    	if(clientRes.getSub_message()!=null)
	    	{
	    		errorMessage.append("("+clientRes.getSub_message()+")")
;	    	}
	    	return null;
	    }
	    String res_data= clientRes.getData();
	    
	    DianwodaOrderCostEstimateResult dataObj = JSON.parseObject(res_data, DianwodaOrderCostEstimateResult.class);
	   
	    return dataObj;
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			errorMessage.append(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 修改订单备注信息
	 * 修改方式为覆盖更新原有订单备注。
	 * 调用条件
	 * 订单状态：订单终态（完成、异常、取消）前可以调用。
	 * @param appkey
	 * @param app_secret
	 * @param access_token
	 * @param IsSandBox
	 * @param param
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean OrderRemarkUpdate(String appkey, String app_secret, String access_token,String IsSandBox,DianwodaOrderRemarkUpdateParam param, StringBuilder errorMessage)  throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
		
			String api = "dianwoda.order.remark.update";
			if(param==null)
			{
				errorMessage.append("请求内容为空！");
				return false;
			}
		  //region 必传字段检查
			boolean IsNeedRefParm = false;
			if(param.getOrder_original_id()==null||param.getOrder_original_id().trim().isEmpty())
			{
				IsNeedRefParm = true;
				errorMessage.append("订单号order_original_id不能为空，");
			}
			if(param.getOrder_info_content()==null||param.getOrder_info_content().trim().isEmpty())
			{
				IsNeedRefParm = true;
				errorMessage.append("新订单备注order_info_content不能为空，");
			}
			
		
			if(IsNeedRefParm)
			{
				return false;
			}
			//endregion
			
			 //测试环境 联调时，order_original_id请加上需要加上  ${appkey}_ 前缀
			if(IsSandBox!=null&&IsSandBox.equals("Y"))
			{
				param.setOrder_original_id(appkey+"_"+param.getOrder_original_id());
			}
			
			String parm_json = JSON.toJSONString(param);
			HelpTools.writelog_fileName("【开始调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id(), dwdLogFileName);
			String result = DianwodaHttpClientUtil.sendPostRequest(appkey, app_secret, access_token, IsSandBox, api, parm_json);
			HelpTools.writelog_fileName("【结束调用dianwoda接口】:"+api+" 订单号:"+param.getOrder_original_id()+" 返回："+result, dwdLogFileName);
			DianwodaClientResponse clientRes = JSON.parseObject(result,DianwodaClientResponse.class);
	    String res_code = clientRes.getCode();
	    if(res_code.equals("success"))
	    {
	    	return true;
	    	
	    }
	    else
	    {    	
	    	errorMessage.append(clientRes.getMessage());
	    	if(clientRes.getSub_message()!=null)
	    	{
	    		errorMessage.append("("+clientRes.getSub_message()+")")
;	    	}
	    	return false;
	    }   
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			errorMessage.append(e.getMessage());
		}
		return false;
	}
	
	
	
}
