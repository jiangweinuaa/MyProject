package com.dsc.spos.waimai.jddj;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import o2o.openplatform.sdk.dto.WebRequestDTO;
import o2o.openplatform.sdk.util.HttpUtil;
import o2o.openplatform.sdk.util.SignUtils;

public class HelpJDDJHttpUtil 
{
	public static String jddjLogFileName = "jddjlog";
	
	public static String sendSimplePostRequest(String url, String jd_param_json) throws Exception
	{			
		/*{
			"code": "0",
			"msg": "操作成功",
			"data": {}
		}*/
		String result = "";		//"code": "0",表示成功，其他均为失败。
		try 
		{
			String appKey = StaticInfo.waimaiJDDJAPPKey;
			String appSecret = StaticInfo.waimaiJDDJSecret;
			String token = StaticInfo.waimaiJDDJToken;
			String isTest = StaticInfo.waimaiJDDJSandbox;;//是否测试
			String sandBoxURL = "https://openapi.jddj.com/mockapi/";//测试环境
			String productURL = "https://openapi.jddj.com/djapi/";//正式环境
			String curURL = productURL;
			if(isTest.equals("Y"))
			{
				curURL = sandBoxURL;
			}
			
			if(!url.startsWith("https"))//兼容下
			{
				url = curURL+url;
			}
		
			String format = "json";
			String v = "1.0";
			String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());  //"2019-01-21 17:10:59";
			
		  // 计算签名实体
			WebRequestDTO webReqeustDTO = new WebRequestDTO();
			webReqeustDTO.setApp_key(appKey);
			webReqeustDTO.setFormat(format);
			//可能没有 应用级参数
			if (jd_param_json != null && jd_param_json.isEmpty() == false && jd_param_json.length() > 0)
			{
				webReqeustDTO.setJd_param_json(jd_param_json);
			}		
			webReqeustDTO.setTimestamp(timestamp);
			webReqeustDTO.setToken(token);
			webReqeustDTO.setV(v);
			
			String sign = SignUtils.getSignByMD5(webReqeustDTO, appSecret);
			////System.out.println("md5 sign:" + sign);
		  // 请求参数实体
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("token", token);
			param.put("app_key", appKey);
			param.put("timestamp", timestamp);
			param.put("sign", sign);
			param.put("format", format);
			param.put("v", v);	
		  //可能没有 应用级参数
			if (jd_param_json != null && jd_param_json.isEmpty() == false && jd_param_json.length() > 0)
			{
				param.put("jd_param_json", jd_param_json);
			}	
			HelpTools.writelog_fileName("【JDDJ接口完整URL】："+url+" 请求内容："+param.toString(), jddjLogFileName);		
			result = HttpUtil.sendSimplePostRequest(url, param);
			//System.out.println(result);
	
		} 
		catch (Exception e) 
		{
			
			result = "{\"code\":\"-1\", \"msg\":\""+e.getMessage()+"\"}";
	  }
			
		return result;
	}
	
	public static OrderInfoDTO GetJDDJOrderByOrderID(String orderNO,StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		
		try 
		{
			
			Long orderId = new Long(orderNO);
			String url = "order/es/query";//查询订单列表接口url
			String jd_json = "{\"orderId\":"+orderId+"}";					
			HelpTools.writelog_fileName("【JDDJ获取订单】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result":{
							         "pageNo": 1,
							         "pageSize": 20,
							         "maxPageSize": 100,
							         "totalCount": 1111,
							         "resultList":[{}]
							}
			}
		  }*/
			//HelpTools.writelog_fileName("【JDDJ获取订单】orderNO="+orderNO+" 返回内容："+responseStr, jddjLogFileName);
			HelpTools.writelog_fileName("【JDDJ获取订单】orderNO="+orderNO+" 返回内容："+responseStr, "jddjJoblog");
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ获取订单】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), "jddjJoblog");
				return null;
		  }
		  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
		  OResult oResult =	JSONObject.toJavaObject(dataObject, OResult.class);		  
		  if (oResult.getCode().equals("0") == false)//业务状态码，0为成功，非0均为失败，其中（1:参数错误，-4为订单中心底层ES中间件服务抖动异常，请重试）
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ获取订单】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), "jddjJoblog");
				return null;
		  }
		  
		  String resultStr = oResult.getResult();
		  
			JSONObject orderObject = JSONObject.parseObject(resultStr);
		  
		  OResultList orderList =	JSONObject.toJavaObject(orderObject, OResultList.class);
		  
			if (orderList.getResultList() == null || orderList.getResultList().size() == 0)
		  {
		  	errorMessage.append("【JDDJ获取订单】失败！orderNO="+orderNO+" 返回订单列表为空！");
		  	HelpTools.writelog_fileName("【JDDJ获取订单】失败！orderNO="+orderNO+" 返回订单列表为空！", "jddjJoblog");
				return null;
		  }
			else
			{
				HelpTools.writelog_fileName("【JDDJ获取订单】成功！orderNO="+orderNO+" 总条数="+orderList.getTotalCount(), "jddjJoblog");
				return orderList.getResultList().get(0);
			}
			
		
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ获取订单异常】orderNO="+orderNO+" 异常内容："+e.getMessage(), "jddjJoblog");
			return null;	
	  }
				
	}

	/**
	 * 确认接单接口
	 * @param orderNO
	 * @param isAgreed
	 * @param operator
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean orderAcceptOperate(String orderNO, Boolean isAgreed,String operator, StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
			
			//Long orderId = new Long(orderNO);
			String url = "ocs/orderAcceptOperate";//商家确认接单接口
			String jd_json = "{\"orderId\":\""+orderNO+"\",\"isAgreed\":"+isAgreed+",\"operator\":\""+operator+"\"}";					
			HelpTools.writelog_fileName("【JDDJ确认接单】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功"					
			}
		  }*/
			HelpTools.writelog_fileName("【JDDJ确认接单】orderNO="+orderNO+" 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ确认接单】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
		  }
		  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
		  OResult oResult =	JSONObject.toJavaObject(dataObject, OResult.class);		  
		  if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ确认接单】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return false;
		  }
		  
		  return true;
		    			
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ确认接单】orderNO="+orderNO+" 异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	/**
	 * 商家审核用户取消申请接口
	 * @param orderNO
	 * @param isAgreed 操作类型 true 同意 false驳回
	 * @param operator
	 * @param remark 操作备注(isAgreed=false时此字段必填，isAgreed=true时可不填)
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean orderCancelOperate(String orderNO, Boolean isAgreed,String operator,String remark, StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
			
			//Long orderId = new Long(orderNO);
			String url = "ocs/orderCancelOperate";//商家审核用户取消申请接口
			if(remark.isEmpty()||remark.length()==0)
			{
				if(isAgreed == false)
				{
					remark = "拒绝取消！";
				}
				else
				{
					remark = "同意取消！";
				}
				
			}
			String jd_json = "{\"orderId\":\""+orderNO+"\",\"isAgreed\":"+isAgreed+",\"operator\":\""+operator+"\",\"remark\":\""+remark+"\"}";			
					
			HelpTools.writelog_fileName("【JDDJ审核用户取消申请接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功"					
			}
		  }*/
			HelpTools.writelog_fileName("【JDDJ审核用户取消申请接口】orderNO="+orderNO+" 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ审核用户取消申请接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
		  }
		  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
		  OResult oResult =	JSONObject.toJavaObject(dataObject, OResult.class);		  
		  if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ审核用户取消申请接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return false;
		  }
		  
		  return true;
		    			
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ审核用户取消申请接口】orderNO="+orderNO+" 异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	/**
	 * 订单妥投接口(商家自送订单模式下，订单由商家配送完成时，调用此接口，变更订单状态)
	 * @param orderNO
	 * @param operPin
	 * @param operTime
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean deliveryEndOrder(String orderNO,String operPin,String operTime, StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
			
			Long orderId = new Long(orderNO);
			String url = "ocs/deliveryEndOrder";//订单妥投接口
			String jd_json = "{\"orderId\":"+orderId+",\"operPin\":"+operPin+",\"operTime\":\""+operTime+"\"}";					
			HelpTools.writelog_fileName("【JDDJ订单妥投】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(orderNO, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result": {
										"orderId": 100001000657300,
										"orderStatus": 33060
									}					
      }
		  }*/
			HelpTools.writelog_fileName("【JDDJ订单妥投】orderNO="+orderNO+" 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ订单妥投】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
		  }
		  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
		  OResult oResult =	JSONObject.toJavaObject(dataObject, OResult.class);		  
		  if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ订单妥投】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return false;
		  }
		  
		  return true;
		    			
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ订单妥投】orderNO="+orderNO+" 异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	/**
	 * 商家确认收到拒收退回（或取消）的商品接口 (拣货完成前，订单被风控；达达配送的订单，顾客拒收订单；两种情况下订单均状态会变为锁定。商家需要调此确认已收到退回的商品，完成订单取消流程。 24小时无响应，自动确认取消订单)
	 * @param orderNO
	 * @param operateTime
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean confirmReceiveGoods(String orderNO,String operateTime, StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
			
			Long orderId = new Long(orderNO);
			//拣货完成前，订单被风控；达达配送的订单，顾客拒收订单；两种情况下订单均状态会变为锁定。商家需要调此确认已收到退回的商品，完成订单取消流程。 24小时无响应，自动确认取消订单
			String url = "order/confirmReceiveGoods";//商家确认收到拒收退回（或取消）的商品接口
			String jd_json = "{\"orderId\":"+orderId+",\"operateTime\":\""+operateTime+"\"}";					
			HelpTools.writelog_fileName("【JDDJ商家确认收到拒收退回（或取消）的商品接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result": {
										"orderId": 100001000657300,
										"orderStatus": 33060
									}					
      }
		  }*/
			HelpTools.writelog_fileName("【JDDJ商家确认收到拒收退回（或取消）的商品接口】orderNO="+orderNO+" 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ商家确认收到拒收退回（或取消）的商品接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
		  }
		  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
		  OResult oResult =	JSONObject.toJavaObject(dataObject, OResult.class);		  
		  if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ商家确认收到拒收退回（或取消）的商品接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return false;
		  }
		  
		  return true;
		    			
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ商家确认收到拒收退回（或取消）的商品接口】orderNO="+orderNO+" 异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	/**
	 * 获取到家门店编码列表接口
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<Long> GetStationsByVenderId(StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{				
			String url = "store/getStationsByVenderId";//获取到家门店编码列表接口
			String jd_json = "";					
			HelpTools.writelog_fileName("【JDDJ获取到家门店编码列表】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result": []
											
      }
		  }*/
			HelpTools.writelog_fileName("【JDDJ获取到家门店编码列表】返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (!oResponse.getCode().equals("0") )//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ获取到家门店编码列表】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
		  }
		  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
			OStationNoList oResult =	JSONObject.toJavaObject(dataObject, OStationNoList.class);		  
		  if (!oResult.getCode().equals("0") )//
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ获取到家门店编码列表】失败："+oResult.getMsg()+" 业务状态码(狗日的京东这个接口比较特殊1代表成功)="+oResult.getCode(), jddjLogFileName);
				return null;
		  }
		  
		  return oResult.getResult();
		    			
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ获取到家门店编码列表】异常："+e.getMessage(), jddjLogFileName);
			return null;	
	  }
	}
	
	/**
	 * 根据到家门店编码查询门店基本信息接口
	 * @param StoreNo
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OStoreInfo getStoreInfoByStationNo(String StoreNo,StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		
		try 
		{				
			String url = "storeapi/getStoreInfoByStationNo";//根据到家门店编码查询门店基本信息接口
			String jd_json = "{\"StoreNo\":\""+StoreNo+"\"}";					
			HelpTools.writelog_fileName("【JDDJ查询门店基本信息】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result":{
							        
							}
			}
		  }*/
			HelpTools.writelog_fileName("【JDDJ查询门店基本信息】StoreNo="+StoreNo+" 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ查询门店基本信息】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
		  }	  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	
			//换一种写法 fastejson有可能会有特殊字符转义的问题
			ParseJson pj=new ParseJson();
			OStoreInfoResult oResult = pj.jsonToBean(data, new TypeToken<OStoreInfoResult>(){});
			pj=null;
			
			//OStoreInfoResult oResult =	JSONObject.toJavaObject(dataObject, OStoreInfoResult.class);		  
		  
			if (oResult.getCode().equals("0") == false)//业务状态码，0为成功，非0均为失败，其中（-4未知错误 ,-3系统错误 ,-2 警告 -1 失败, 0成功 ,1参数错误）
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ查询门店基本信息】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return null;
		  }
		  
			OStoreInfo storeInfo =	oResult.getResult();
		  
			if (storeInfo == null)
		  {
		  	errorMessage.append("【JDDJ查询门店基本信息】失败！StoreNo="+StoreNo+" 返回订单列表为空！");
		  	HelpTools.writelog_fileName("【JDDJ查询门店基本信息】失败！StoreNo="+StoreNo+"  返回订单列表为空！", jddjLogFileName);
				return null;
		  }
			return storeInfo;
					
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ查询门店基本信息异常】StoreNo="+StoreNo+" 异常内容："+e.getMessage(), jddjLogFileName);
			return null;	
	  }
				
	}

	
	
	/**
	 * 修改门店基础信息接口
	 * @param stationNo 京东到家门店编号
	 * @param outSystemId 外部门店编号
	 * @param operator 操作人
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateStoreInfo4Open(String stationNo,String outSystemId,String operator, StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{				
			String url = "store/updateStoreInfo4Open";//修改门店基础信息接口
			String jd_json =  "{\"stationNo\":\""+stationNo+"\",\"outSystemId\":\""+outSystemId+"\",\"operator\":\""+operator+"\"}";				
			HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result": "",
							"data":{}
											
      }
		  }*/
			HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
		  }
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
		  OResult oResult =	JSONObject.toJavaObject(dataObject, OResult.class);		  
		  if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return false;
		  }
		  
		  return true;
		 	    			
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】异常："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
	}
	
	/**
	 * 修改门店开关店信息
	 * @param stationNo 京东到家门店编号
	 * @param outSystemId 外部门店编号
	 * @param operator 操作人
	 * @param optype 操作类型 1开店 2闭店
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean updateStoreStatusInfo4Open(String stationNo,String outSystemId,String operator,String optype, StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{				
			if(operator==null||operator.isEmpty())
			{
				operator = "pos";
			}
			String url = "store/updateStoreInfo4Open";//修改门店基础信息接口
			org.json.JSONObject jsonob=new org.json.JSONObject();
			jsonob.put("stationNo", stationNo);
			//jsonob.put("outSystemId", outSystemId);
			jsonob.put("operator", operator);
			if(optype.equals("1"))
			{
			  jsonob.put("closeStatus", 0);
			}
			else
			{
				jsonob.put("closeStatus", 1);
			}
			
			//String jd_json =  "{\"stationNo\":\""+stationNo+"\",\"outSystemId\":\""+outSystemId+"\",\"operator\":\""+operator+"\"}";				
			String jd_json = jsonob.toString();
			
			HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result": "",
							"data":{}
											
      }
		  }*/
			HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
		  }
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
		  OResult oResult =	JSONObject.toJavaObject(dataObject, OResult.class);		  
		  if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return false;
		  }
		  
		  return true;
		 	    			
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ修改门店基础信息接口】异常："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
	}
	
	/**
	 * 分页获取商品信息
	 * @param pageNo
	 * @param pageSize
	 * @param skuName
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OSkuMainResultList querySkuInfos(int pageNo,int pageSize,String skuName,StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		
		try 
		{				
			String isFilterDel = "0";//是否查询出已删除的上传商品(0代表不查已删除商品,不填则查出全部商品)
			String url = "pms/querySkuInfos";//商家用于查询该商家下已有的商品信息
			String jd_json = "{\"pageNo\":\""+pageNo+"\",\"pageSize\":"+pageSize+",\"isFilterDel\":\""+isFilterDel+"\"";	
			if (skuName != null && skuName.isEmpty() == false && skuName.trim().length() > 0)
			{
				jd_json += ",\"skuName\":\"" + skuName + "\"";
			}
			jd_json += "}";
			HelpTools.writelog_fileName("【JDDJ查询该商家下已有的商品信息】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result":{						         
							         "count": 1111,
							         "resultList":"[{}]" //煞笔玩意 ，返回的list和之前的获取订单信息不一样，不能作为json解析
							}
			}
		  }*/
			HelpTools.writelog_fileName("【JDDJ查询该商家下已有的商品信息】  页码pageNo="+pageNo+" 偏移量pageSize="+pageSize+ " 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ查询该商家下已有的商品信息】异常："+oResponse.getMsg(), jddjLogFileName);
				return null;
		  }	  
			
			String data = oResponse.getData();
			JSONObject dataObject = JSONObject.parseObject(data);	
						
			OSkuMainResult oResult =	JSONObject.toJavaObject(dataObject, OSkuMainResult.class);		  
		  if (oResult.getCode().equals("0") == false)//业务状态码，0为成功，非0均为失败，其中（1:参数错误，-4为订单中心底层ES中间件服务抖动异常，请重试）
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ查询该商家下已有的商品信息】异常："+oResult.getMsg(), jddjLogFileName);
				return null;		  
		  }
		  
			
		  OSkuMainResultList skuMainResult = oResult.getResult();
		  	
			String skuMainListStr = skuMainResult.getResult();
					
		  List<OSkuMain> skuMainList =	JSONObject.parseArray(skuMainListStr,OSkuMain.class);
		  
			if (skuMainList == null || skuMainList.size() == 0)
		  {
		  	errorMessage.append("【JDDJ查询该商家下已有的商品信息】失败！页码pageNo="+pageNo+" 偏移量pageSize="+pageSize+" 总条数="+skuMainResult.getCount());
		  	HelpTools.writelog_fileName("【JDDJ查询该商家下已有的商品信息】为空！页码pageNo="+pageNo+" 偏移量pageSize="+pageSize+" 总条数="+skuMainResult.getCount(), jddjLogFileName);
		  	return null;
		  }
			else
			{
				skuMainResult.setSkuMains(skuMainList);
				HelpTools.writelog_fileName("【JDDJ查询该商家下已有的商品信息】成功！页码pageNo="+pageNo+" 偏移量pageSize="+pageSize+" 总条数="+skuMainResult.getCount(), jddjLogFileName);
				return skuMainResult;
			}
			
					
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ查询门店基本信息异常】 异常内容："+e.getMessage(), jddjLogFileName);
			return null;	
	  }
				
	}
	
	/**
	 * 根据到家商品编码批量更新商家商品编码接口
	 * @param skuInfoListJson 需要更新的商品信息列表(json格式最多50条)
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean batchUpdateOutSkuId(String skuInfoListJson,StringBuilder errorMessage)  throws Exception
	{
		/** 参数skuInfoListJson格式如下
		 * JSONObject body = new JSONObject();
			JSONArray skuInfoListMap = new JSONArray();
			for(level1Elm : req.getDatas())
			{
				JSONObject skuInfoMap_item = new JSONObject();			
				skuInfoMap_item.put("skuId", 1123);//节点值类型Long
				skuInfoMap_item.put("outSkuId", "1222");//节点值类型String
			
				skuInfoListMap.put(skuInfoMap_item);
			}
					
			body.put("skuInfoList", skuInfoListMap);
						
			String skuInfoListJson = body.toString();
		 * 
		 */
		
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{					
			//根据到家商品编码批量更新商家商品编码接口
			String url = "pms/sku/batchUpdateOutSkuId";//根据到家商品编码批量更新商家商品编码接口
			String jd_json = skuInfoListJson;					
			HelpTools.writelog_fileName("【JDDJ根据到家商品编码批量更新商家商品编码接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result": [{
										"detail": "未找到此到家商品编码",
										"skuInfo": {
											"skuId": "2000086904",
											"outskuId": "2000086904"
										}
									}]				
      }
		  }*/
			HelpTools.writelog_fileName("【JDDJ根据到家商品编码批量更新商家商品编码接口】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ根据到家商品编码批量更新商家商品编码接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
		  }
		  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
			OSkuInfoResult oResult =	JSONObject.toJavaObject(dataObject, OSkuInfoResult.class);		  
		  /*List<OSkuInfoResultDetail> oSkuInfoResultDetails = oResult.getResult();
			
			for (int i = 0; i < oSkuInfoResultDetails.size(); i++) 
			{
			
				OSkuInfo skuInfo = oSkuInfoResultDetails.get(i).getSkuInfo();
				//System.out.println(skuInfo.getSkuId()+"-->"+skuInfo.getOutSkuId()+"\n");			
		  }*/
			if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ根据到家商品编码批量更新商家商品编码接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return false;
		  }
		  
		  return true;
		    			
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ根据到家商品编码批量更新商家商品编码接口】 异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	
	/**
	 * 根据到家商品编码和到家门店编码批量查询商品门店价格信息接口
	 * @param stationNo
	 * @param skuIds
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<OPriceInfo> getStationInfoList(String stationNo,List<Long> skuIds,StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{	
		  //根据到家商品编码和到家门店编码批量查询商品门店价格信息接口
			String url = "price/getStationInfoList";//根据到家商品编码和到家门店编码批量查询商品门店价格信息接口
			String skuIds_Str = "";
			
			for (Long item : skuIds) 
			{
				skuIds_Str +=  item+",";		
		  }
			
			if (skuIds_Str.length() > 0)
			{
				skuIds_Str = skuIds_Str.substring(0, skuIds_Str.length()-1);
			}
			
			
			String jd_json = "{\"stationNo\":\""+stationNo+"\",\"skuIds\":["+skuIds_Str+"]}";					
			HelpTools.writelog_fileName("【JDDJ根据到家商品编码和到家门店编码批量查询商品门店价格信息接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*
				{
				"code": "0",
				"msg": "操作成功",
				"data": {
					"code": "0",
					"msg": "操作成功",
					"result": [
						{
							"stationNo": "10000014",
							"price": 2200,
							"pin": "test",
							"venderId": "30298",
							"skuId": 1100546272,
							"vipPrice": 0,
							"promoteVipPrice": 0
						}
					]
				}
			}
			 */
			HelpTools.writelog_fileName("【JDDJ根据到家商品编码和到家门店编码批量查询商品门店价格信息接口】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ根据到家商品编码和到家门店编码批量查询商品门店价格信息接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
		  }
		  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	
			//OPriceInfoResult oResult =	JSONObject.toJavaObject(dataObject, OPriceInfoResult.class);		  	  
			ParseJson pj=new ParseJson();
			OPriceInfoResult oResult =	pj.jsonToBean(data, new TypeToken<OPriceInfoResult>(){});
			pj=null;
			
			if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ根据到家商品编码批量更新商家商品编码接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return null;
		  }
		  
		  return oResult.getResult();		
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ根据到家商品编码和到家门店编码批量查询商品门店价格信息接口】 异常内容："+e.getMessage(), jddjLogFileName);
			return null;
	  }
	}
	
	/**
	 * 订单调整接口
	 * @param orderId
	 * @param operPin
	 * @param remark
	 * @param oaosAdjustDTOList
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static OrderAdjustRespDTO adjustOrder(long orderId,String operPin,String remark,List<OAOSAdjustDTO> oaosAdjustDTOList,StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
		  //订单调整接口
			String url = "orderAdjust/adjustOrder";//订单调整接口
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("orderId", orderId);
			jsonObj.put("operPin", operPin);
			jsonObj.put("remark", remark);
			jsonObj.put("oaosAdjustDTOList", oaosAdjustDTOList);
			
			String jd_json=jsonObj.toJSONString();
			
      HelpTools.writelog_fileName("【JDDJ订单调整接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*
				{
				"code": "0",
				"msg": "操作成功",
				"data": {
					"code": "0",
					"msg": "操作成功",
					"result": [
						{
							"stationNo": "10000014",
							"price": 2200,
							"pin": "test",
							"venderId": "30298",
							"skuId": 1100546272,
							"vipPrice": 0,
							"promoteVipPrice": 0
						}
					]
				}
			}
			 */
			HelpTools.writelog_fileName("【JDDJ订单调整接口】 返回内容："+responseStr, jddjLogFileName);
		
      JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ订单调整接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
		  }
		  
			String data = oResponse.getData();
			ParseJson pj=new ParseJson();
			OrderAdjustRespDTOResult oResult =	pj.jsonToBean(data, new TypeToken<OrderAdjustRespDTOResult>(){});
			pj=null;
			
			if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ订单调整接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return null;
		  }
		  
		  return oResult.getResult();	
			
		
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ订单调整接口】 异常内容："+e.getMessage(), jddjLogFileName);
			return null;
		
	  }
		
		
	}
	
	/**
	 * 拣货完成且众包配送接口
	 * @param orderNO
	 * @param operPin
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static boolean OrderJDZBDelivery(String orderNO,String operPin,StringBuilder errorMessage)  throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{
			if(operPin==null||operPin.isEmpty())
			{
				operPin = "pos";
			}
			//拣货完成且众包配送接口
			String url = "bm/open/api/order/OrderJDZBDelivery";//拣货完成且众包配送接口
			
			org.json.JSONObject jsonob=new org.json.JSONObject();
			jsonob.put("orderId", orderNO);			
			jsonob.put("operator", operPin);		
			String jd_json = jsonob.toString();
							
			HelpTools.writelog_fileName("【JDDJ拣货完成且众包配送接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*{
			"code": "0",
			"msg": "操作成功",
			"data": {
							"code": "0",
							"msg": "操作成功",
							"result": {
										"orderId": 100001000657300,
										"orderStatus": 33060
									}					
      }
		  }*/
			HelpTools.writelog_fileName("【JDDJ拣货完成且众包配送接口】orderNO="+orderNO+" 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ拣货完成且众包配送接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return false;
		  }
		  
			String data = oResponse.getData();
			JSONObject dataObject=JSONObject.parseObject(data);	   
		  OResult oResult =	JSONObject.toJavaObject(dataObject, OResult.class);		  
		  if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ拣货完成且众包配送接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return false;
		  }
		  
		  return true;
		    			
	  } 
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ拣货完成且众包配送接口】orderNO="+orderNO+" 异常内容："+e.getMessage(), jddjLogFileName);
			return false;	
	  }
		
	}
	
	/**
	 * 根据到家商品编码和门店编码批量查询商品库存及可售状态信息接口
	 * @param stationNo
	 * @param skuIds
	 * @param errorMessage
	 * @throws Exception
	 */
	public static List<OQueryStockResponse>	 queryOpenUseable(String stationNo,List<Long> skuIds,StringBuilder errorMessage)  throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{	
		  //根据到家商品编码和门店编码批量查询商品库存及可售状态信息接口
			String url = "stock/queryOpenUseable";//根据到家商品编码和门店编码批量查询商品库存及可售状态信息接口
					
			JSONObject jsobject = new JSONObject();
			com.alibaba.fastjson.JSONArray jsarray = new com.alibaba.fastjson.JSONArray();
			for (Long sku : skuIds) 
			{
				JSONObject skuObj = new JSONObject();
				skuObj.put("stationNo", stationNo);
				skuObj.put("skuId", sku);			
				jsarray.add(skuObj);
		  }
			jsobject.put("listBaseStockCenterRequest", jsarray);
			
			String jd_json=jsobject.toJSONString();
							
			HelpTools.writelog_fileName("【JDDJ根据到家商品编码和门店编码批量查询商品库存及可售状态信息接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*
				{
					"code": "0",
					"msg": "操作成功",
					"data": {
						"isRet": "true",
						"retCode": "0",
						"retMsg": "成功",
						"data": [
							{
								"skuId": "1997342",
								"stationNo": "10001111",
								"usableQty": 6,
								"lockQty": 4,
								"orderQty": 4,
								"vendibility": 0
							}
						]
					}
				}
			 */
			HelpTools.writelog_fileName("【JDDJ根据到家商品编码和门店编码批量查询商品库存及可售状态信息接口】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ根据到家商品编码和门店编码批量查询商品库存及可售状态信息接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
		  }
		  
			String data = oResponse.getData();			
			//OPriceInfoResult oResult =	JSONObject.toJavaObject(dataObject, OPriceInfoResult.class);		  	  
			ParseJson pj=new ParseJson();
			OQueryStockResponseResult oResult =	pj.jsonToBean(data, new TypeToken<OQueryStockResponseResult>(){});
			pj=null;
			
			if (oResult.getRetCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getRetMsg());
		  	HelpTools.writelog_fileName("【JDDJ根据到家商品编码和门店编码批量查询商品库存及可售状态信息接口】失败："+oResult.getRetMsg()+" 业务状态码="+oResult.getRetCode(), jddjLogFileName);
				return null;
		  }
		  
		  return oResult.getData();		
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ根据到家商品编码和门店编码批量查询商品库存及可售状态信息接口】 异常内容："+e.getMessage(), jddjLogFileName);
			return null;
	  }
		
	}
	
	
	/**
	 * 查询商家店内分类信息接口
	 * @throws Exception
	 */
	public static List<OShopCategory> queryCategoriesByOrgCode(StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{	
		  //查询商家店内分类信息接口
			String url = "pms/queryCategoriesByOrgCode";//查询商家店内分类信息接口
					
			JSONObject jsobject = new JSONObject();
			List<String> fields = new ArrayList<String>();
			fields.add("ID");
			fields.add("PID");
			fields.add("SHOP_CATEGORY_LEVEL");
			fields.add("SHOP_CATEGORY_NAME");
			fields.add("SORT");
			jsobject.put("fields", fields);
			
			String jd_json=jsobject.toJSONString();
							
			HelpTools.writelog_fileName("【JDDJ查询商家店内分类信息接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*
				{
					"code": "0",
					"msg": "操作成功",
					"data": {
						"isRet": "true",
						"retCode": "0",
						"retMsg": "成功",
						"data": [
							{
								"skuId": "1997342",
								"stationNo": "10001111",
								"usableQty": 6,
								"lockQty": 4,
								"orderQty": 4,
								"vendibility": 0
							}
						]
					}
				}
			 */
			HelpTools.writelog_fileName("【JDDJ查询商家店内分类信息接口】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ查询商家店内分类信息接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
		  }
		  
			String data = oResponse.getData();			
			//OPriceInfoResult oResult =	JSONObject.toJavaObject(dataObject, OPriceInfoResult.class);		  	  
			ParseJson pj=new ParseJson();
			OShopCategoryResult oResult =	pj.jsonToBean(data, new TypeToken<OShopCategoryResult>(){});
			pj=null;
			
			if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ查询商家店内分类信息接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return null;
		  }
		  
		  return oResult.getResult();		
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ查询商家店内分类信息接口】 异常内容："+e.getMessage(), jddjLogFileName);
			return null;
	  }
		
	}
	
	
	/**
	 * 查询订单应结金额接口
	 * @throws Exception
	 */
	public static OrderShoudSettlementAmount orderShoudSettlementService(String orderNO,StringBuilder errorMessage) throws Exception
	{
		if (errorMessage == null) 
		{
			errorMessage = new StringBuilder();		
	  }
		try 
		{	
		  //查询应结金额接口
			String url = "bill/orderShoudSettlementService";//应结金额接口
					
			JSONObject jsobject = new JSONObject();	
			jsobject.put("orderId", orderNO);
			
			String jd_json=jsobject.toJSONString();
							
			HelpTools.writelog_fileName("【JDDJ查询应结金额接口】接口URL："+url+" 请求内容："+jd_json, jddjLogFileName);
			
			String responseStr = sendSimplePostRequest(url, jd_json);
			/*
				{
					"code": "0",
					"msg": "操作成功",
					"data": {
						"isRet": "true",
						"retCode": "0",
						"retMsg": "成功",
						"data": [
							{
								"skuId": "1997342",
								"stationNo": "10001111",
								"usableQty": 6,
								"lockQty": 4,
								"orderQty": 4,
								"vendibility": 0
							}
						]
					}
				}
			 */
			HelpTools.writelog_fileName("【JDDJ应结金额接口】 返回内容："+responseStr, jddjLogFileName);
			//下面解析开始
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			
			OData oResponse =	JSONObject.toJavaObject(jsonObject, OData.class);
			if (oResponse.getCode().equals("0") == false)//到家平台状态码说明0表示成功，其他均为失败
		  {
				errorMessage.append(oResponse.getMsg());
				HelpTools.writelog_fileName("【JDDJ查询商家店内分类信息接口】失败："+oResponse.getMsg()+" 平台状态码="+oResponse.getCode(), jddjLogFileName);
				return null;
		  }
		  
			String data = oResponse.getData();			
			//OPriceInfoResult oResult =	JSONObject.toJavaObject(dataObject, OPriceInfoResult.class);		  	  
			ParseJson pj=new ParseJson();
			OrderShoudSettlementAmountResult oResult =	pj.jsonToBean(data, new TypeToken<OrderShoudSettlementAmountResult>(){});
			pj=null;
			
			if (oResult.getCode().equals("0") == false)//业务状态码，0 成功，-1 失败，1 参数错误，-3 系统错误
		  {
		  	errorMessage.append(oResult.getMsg());
		  	HelpTools.writelog_fileName("【JDDJ查询商家店内分类信息接口】失败："+oResult.getMsg()+" 业务状态码="+oResult.getCode(), jddjLogFileName);
				return null;
		  }
		  
		  return oResult.getResult();		
		}
		catch (Exception e) 
		{
			errorMessage.append(e.getMessage());
			HelpTools.writelog_fileName("【JDDJ查询商家店内分类信息接口】 异常内容："+e.getMessage(), jddjLogFileName);
			return null;
	  }
		
	}
	
	
}

