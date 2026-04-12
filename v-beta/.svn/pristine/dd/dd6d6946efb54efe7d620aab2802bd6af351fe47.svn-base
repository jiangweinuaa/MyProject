package com.dsc.spos.waimai;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.sankuai.meituan.shangou.open.sdk.dto.ReviewAfterSalesInfoDto;
import com.sankuai.meituan.shangou.open.sdk.exception.SgOpenException;
import com.sankuai.meituan.shangou.open.sdk.request.*;
import com.sankuai.meituan.shangou.open.sdk.response.SgOpenResponse;
import com.sankuai.meituan.shangou.open.sdk.domain.SystemParam;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WMSGOrderProcess {
	//public final static SystemParam sysPram = new SystemParam(StaticInfo.waimaiMTAPPID, StaticInfo.waimaiMTSignKey);

  public static boolean orderConfirm(String orderNo,StringBuilder errorMessage)   
  { 
    //成功{"data":"ok"}
  	//失败{"data":"ng","error":{"code":808,"msg":"操作失败,订单已经确认过了"}}
	  String result = "";
	  boolean nRet = false;
  	try 
  	{
		OrderConfirmRequest orderConfirmRequest = new OrderConfirmRequest(getSystemParam());
		orderConfirmRequest.setOrder_id(orderNo);
		SgOpenResponse sgOpenResponse;
		try {
			sgOpenResponse = orderConfirmRequest.doRequest();
		} catch (SgOpenException e) {
			//e.printStackTrace();
			errorMessage.append(e.getMessage());
			return false;
		} catch (Exception e) {
			//e.printStackTrace();
			errorMessage.append(e.getMessage());
			return false;
		}
		//发起请求时的sig，用来联系美团员工排查问题时使用
		//String requestSig = sgOpenResponse.getRequestSig();
		//请求返回的结果，按照官网的接口文档自行解析即可
		String requestResult = sgOpenResponse.getRequestResult();
		//System.out.println(requestResult);
		org.json.JSONObject resObj = new JSONObject(requestResult);
		String data = resObj.optString("data","");
		//{"data":"ok"}
		if ("OK".equalsIgnoreCase(data))
		{
			return true;
		}
		//{"data":"ng","error":{"code":808,"msg":"操作失败,订单已经确认过了"}}
		org.json.JSONObject errorObj =  resObj.getJSONObject("error");
		errorMessage.append(errorObj.optString("msg",""));
		return false;
  	}
  	catch (Exception e) 
  	{
  		
  		errorMessage.append(e.getMessage());
      return false;
  	}
  		 	
   }
	
  public static boolean orderCancel(String orderNo,String reason, String reasonCode, StringBuilder errorMessage) {
	  //成功{"data":"ok"}
	  //失败{"code":错误码, "msg":"错误详情"}
	  String result = "";
	  boolean nRet = false;
	  try {
		  if (reason == null || reason.trim().isEmpty()) {
			  //聚宝盆就是个垃圾，自己调用美团接口，reason，reasonCode 明明可以为空的，反而到这不能为空了
			  reason = "其他原因";
		  }
		  reasonCode = "2007";//理由码 状态值 详见聚宝盆开发文档（补充相关字段说明）
		  OrderCancelRequest orderCancelRequest = new OrderCancelRequest(getSystemParam());
		  orderCancelRequest.setOrder_id(orderNo);
		  SgOpenResponse sgOpenResponse;
		  try {
			  sgOpenResponse = orderCancelRequest.doRequest();
		  } catch (SgOpenException e) {
			  //e.printStackTrace();
			  errorMessage.append(e.getMessage());
			  return false;
		  } catch (Exception e) {
			  //e.printStackTrace();
			  errorMessage.append(e.getMessage());
			  return false;
		  }
		  //发起请求时的sig，用来联系美团员工排查问题时使用
		  //String requestSig = sgOpenResponse.getRequestSig();
		  //请求返回的结果，按照官网的接口文档自行解析即可
		  String requestResult = sgOpenResponse.getRequestResult();
		  //System.out.println(requestResult);
		  org.json.JSONObject resObj = new JSONObject(requestResult);
		  String data = resObj.optString("data", "");
		  //{"data":"ok"}
		  //{"msg":"订单已经取消过了","data":"ok"}
		  if ("OK".equalsIgnoreCase(data)) {
			  return true;
		  }
		  //{"data":"ng","error":{"code":808,"msg":"操作失败,订单已经确认过了"}}
		  org.json.JSONObject errorObj = resObj.getJSONObject("error");
		  errorMessage.append(errorObj.optString("msg", ""));
		  return false;

	  } catch (Exception e) {
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
		OrderRefundAgreeRequest request = new OrderRefundAgreeRequest(getSystemParam());
		request.setOrder_id(orderNo);
		request.setReason(reason);
		SgOpenResponse sgOpenResponse;
		try {
			sgOpenResponse = request.doRequest();
		} catch (SgOpenException e) {
			//e.printStackTrace();
			errorMessage.append(e.getMessage());
			return false;
		} catch (Exception e) {
			//e.printStackTrace();
			errorMessage.append(e.getMessage());
			return false;
		}
		//发起请求时的sig，用来联系美团员工排查问题时使用
		//String requestSig = sgOpenResponse.getRequestSig();
		//请求返回的结果，按照官网的接口文档自行解析即可
		String requestResult = sgOpenResponse.getRequestResult();
		//System.out.println(requestResult);
		org.json.JSONObject resObj = new JSONObject(requestResult);
		String data = resObj.optString("data", "");
		//{"data":"ok"}
		if ("OK".equalsIgnoreCase(data)) {
			return true;
		}
		//{"data":"ng","error":{"code":808,"msg":"操作失败,订单已经确认过了"}}
		org.json.JSONObject errorObj = resObj.getJSONObject("error");
		errorMessage.append(errorObj.optString("msg", ""));
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
  			reason = "商家拒绝退款！";
  		}
		OrderRefundRejectRequest request = new OrderRefundRejectRequest(getSystemParam());
		request.setOrder_id(orderNo);
		request.setReason(reason);
		SgOpenResponse sgOpenResponse;
		try {
			sgOpenResponse = request.doRequest();
		} catch (SgOpenException e) {
			//e.printStackTrace();
			errorMessage.append(e.getMessage());
			return false;
		} catch (Exception e) {
			//e.printStackTrace();
			errorMessage.append(e.getMessage());
			return false;
		}
		//发起请求时的sig，用来联系美团员工排查问题时使用
		//String requestSig = sgOpenResponse.getRequestSig();
		//请求返回的结果，按照官网的接口文档自行解析即可
		String requestResult = sgOpenResponse.getRequestResult();
		//System.out.println(requestResult);
		org.json.JSONObject resObj = new JSONObject(requestResult);
		String data = resObj.optString("data", "");
		//{"data":"ok"}
		if ("OK".equalsIgnoreCase(data)) {
			return true;
		}
		//{"data":"ng","error":{"code":808,"msg":"操作失败,订单已经确认过了"}}
		org.json.JSONObject errorObj = resObj.getJSONObject("error");
		errorMessage.append(errorObj.optString("msg", ""));
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
		OrderBatchPullPhoneNumberRequest request = new OrderBatchPullPhoneNumberRequest(getSystemParam());
		request.setApp_poi_code(app_poi_code);
		request.setOffset(offset);
		request.setLimit(limit);
		SgOpenResponse sgOpenResponse;
		try {
			sgOpenResponse = request.doRequest();
		} catch (SgOpenException e) {
			//e.printStackTrace();
			errorMessage.append(e.getMessage());
			return "";
		} catch (Exception e) {
			//e.printStackTrace();
			errorMessage.append(e.getMessage());
			return "";
		}
		//发起请求时的sig，用来联系美团员工排查问题时使用
		//String requestSig = sgOpenResponse.getRequestSig();
		//请求返回的结果，按照官网的接口文档自行解析即可
		result = sgOpenResponse.getRequestResult();
		//System.out.println(requestResult);
       return result;
      
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
				orderLoadDocType.MTSG);
		if (elmAppKeyList != null && elmAppKeyList.size() > 0)
		{
			appId = elmAppKeyList.get(0).get("APIKEY").toString();
			appSecret = elmAppKeyList.get(0).get("APISECRET").toString();
		}

		com.sankuai.meituan.shangou.open.sdk.domain.SystemParam sysPram = new SystemParam(appId, appSecret);

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
			//result = APIFactory.getOrderAPI().orderPreparationMealComplete(getSystemParam(), orderNo);//直接返回ok
			OrderPreparationMealCompleteRequest request = new OrderPreparationMealCompleteRequest(getSystemParam());
			request.setOrder_id(orderNo);
			SgOpenResponse sgOpenResponse;
			try {
				sgOpenResponse = request.doRequest();
			} catch (SgOpenException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			//发起请求时的sig，用来联系美团员工排查问题时使用
			//String requestSig = sgOpenResponse.getRequestSig();
			//请求返回的结果，按照官网的接口文档自行解析即可
			String requestResult = sgOpenResponse.getRequestResult();
			//System.out.println(requestResult);
			org.json.JSONObject resObj = new JSONObject(requestResult);
			String data = resObj.optString("data", "");
			//{"data":"ok"}
			if ("OK".equalsIgnoreCase(data)) {
				return true;
			}
			//{"data":"ng","error":{"code":808,"msg":"操作失败,订单已经确认过了"}}
			org.json.JSONObject errorObj = resObj.getJSONObject("error");
			errorMessage.append(errorObj.optString("msg", ""));
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
	public static String getOrderDetail(String orderNo,StringBuilder errorMessage) throws Exception {
		String res = "";
		try {
			OrderGetOrderDetailRequest request = new OrderGetOrderDetailRequest(getSystemParam());
			request.setOrder_id(orderNo);
			request.setIs_mt_logistics(0);

			//SystemParam sysPram = new SystemParam("6817", "13f54f83e9059a14f110619762cb4f59");
			/*OrderDetailParam result = APIFactory.getOrderAPI().orderGetOrderDetail(getSystemParam(), orderId, is_mt_logistics);
			Map<String, Object> map = new HashMap<>();
			map = JSON.parseObject(JSON.toJSONString(result), Map.class);
			for (Map.Entry<String, Object> item : map.entrySet()) {
				if (res.isEmpty()) {
					res = item.getKey() + "=" + getURLEncoderString(item.getValue().toString());
				} else {
					res = res + "&" + item.getKey() + "=" + getURLEncoderString(item.getValue().toString());
				}

			}*/
			SgOpenResponse sgOpenResponse;
			try {
				sgOpenResponse = request.doRequest();
			} catch (SgOpenException e) {
				//e.printStackTrace();
				errorMessage.append(e.getMessage());
				return "";
			} catch (Exception e) {
				//e.printStackTrace();
				errorMessage.append(e.getMessage());
				return "";
			}
			//发起请求时的sig，用来联系美团员工排查问题时使用
			//String requestSig = sgOpenResponse.getRequestSig();
			//请求返回的结果，按照官网的接口文档自行解析即可
			String requestResult = sgOpenResponse.getRequestResult();
			//System.out.println(requestResult);
			org.json.JSONObject resObj = new org.json.JSONObject(requestResult);
			String data = resObj.optString("data", "");
			if ("ng".equalsIgnoreCase(data)) {
				return "";
			}
			Map<String, Object> map = new HashMap<>();
			map = com.alibaba.fastjson.JSON.parseObject(data, Map.class);
			for (Map.Entry<String, Object> item : map.entrySet()) {
				if (res.isEmpty()) {
					res = item.getKey() + "=" + getURLEncoderString(item.getValue().toString());
				} else {
					res = res + "&" + item.getKey() + "=" + getURLEncoderString(item.getValue().toString());
				}

			}
			return res;
		}
		catch (Exception e) {
			errorMessage.append(e.getMessage());
			return "";
		}
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

	/**
	 * 售后单（退款/退货退款）审核接口
	 * @param orderNo
	 * @param errorMessage
	 * @return
	 */
	public static boolean orderReviewAfterSales(String orderNo,StringBuilder errorMessage)
	{
		//成功{"data":"ok"}
		//失败{"code":错误码, "msg":"错误详情"}
		String result = "";
		boolean nRet = false;
		try
		{
			Long orderId =new Long(orderNo);
			//result = APIFactory.getOrderAPI().orderPreparationMealComplete(getSystemParam(), orderNo);//直接返回ok
			EcommerceOrderReviewAfterSalesRequest request = new EcommerceOrderReviewAfterSalesRequest(getSystemParam());
			ReviewAfterSalesInfoDto reviewAfterSalesInfoDto = new ReviewAfterSalesInfoDto();
			reviewAfterSalesInfoDto.setWm_order_id_view(orderId);
			reviewAfterSalesInfoDto.setReview_type(1);
			reviewAfterSalesInfoDto.setReject_reason_code(1);
			request.setReviewAfterSalesArgument(reviewAfterSalesInfoDto);

			SgOpenResponse sgOpenResponse;
			try {
				sgOpenResponse = request.doRequest();
			} catch (SgOpenException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			//发起请求时的sig，用来联系美团员工排查问题时使用
			//String requestSig = sgOpenResponse.getRequestSig();
			//请求返回的结果，按照官网的接口文档自行解析即可
			String requestResult = sgOpenResponse.getRequestResult();
			//System.out.println(requestResult);
			org.json.JSONObject resObj = new JSONObject(requestResult);
			String data = resObj.optString("data", "");
			//{"data":"ok"}
			if ("OK".equalsIgnoreCase(data)) {
				return true;
			}
			//{"data":"ng","error":{"code":808,"msg":"操作失败,订单已经确认过了"}}
			org.json.JSONObject errorObj = resObj.getJSONObject("error");
			errorMessage.append(errorObj.optString("msg", ""));
			return false;

		}
		catch (Exception e)
		{

			errorMessage.append(e.getMessage());
			return false;
		}

	}

}
