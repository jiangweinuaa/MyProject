package com.dsc.spos.waimai;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.dsc.spos.waimai.meituanJBP.baseEntityParaReq;
import com.dsc.spos.waimai.meituanJBP.jbpService;
import com.dsc.spos.waimai.meituanJBP.preparationMealCompleteReq;
import org.json.JSONObject;

import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.sankuai.sjst.platform.developer.domain.RequestSysParams;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutOrderCancelRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutOrderConfirmRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutOrderQueryByIdRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutOrderRefundAcceptRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutOrderRefundRejectRequest;

import eleme.openapi.sdk.oauth.response.Token;

public class WMJBPOrderProcess
{
	public static String getToken(String eId,String erpShopNo) throws Exception
	{
		//HelpTools.writelog_waimai("【JBP_MappingShop获取Token】失败！redis_key:"+redis_key+" hash_key:"+hash_key+" "+ e.getMessage());
		String loadDocType = orderLoadDocType.MEITUAN;
		String sql = "select * from DCP_mappingshop where businessid='2' and isjbp='Y' and load_doctype='"+orderLoadDocType.MEITUAN+"' and EID='"+eId+"' and shopID='"+erpShopNo+"' order by tran_time desc";

		try
		{
			HelpTools.writelog_waimai("【从数据库查询JBP门店token】开始，平台类型="+loadDocType+" 门店="+erpShopNo+" 查询sql="+sql);
			List<Map<String, Object>> getData = StaticInfo.dao.executeQuerySQL(sql, null);
			HelpTools.writelog_waimai("【从数据库查询JBP门店token】完成，平台类型="+loadDocType+" 门店="+erpShopNo);
			if(getData!=null&&getData.isEmpty()==false)
			{
				return getData.get(0).get("APPAUTHTOKEN").toString();
			}
			else
			{
				return null;
			}

		}
		catch(Exception e2)
		{
			HelpTools.writelog_waimai("【从数据库查询JBP门店token】异常："+e2.getMessage()+" 平台类型="+loadDocType+" 门店="+erpShopNo+" 查询sql="+sql);
			return null;
		}



	}

	public static boolean orderConfirm(String eId,String erpShopNo,String orderNo,StringBuilder errorMessage)
	{
		//成功{"data":"OK"}
		//失败{"error":{"code":3,"error_type":"signature_error","message":"签名验证失败"}}
		String result = "";
		boolean nRet = false;
		try {
			String appAuthToken = getToken(eId, erpShopNo);
			if (appAuthToken == null || appAuthToken.length() == 0) {
				errorMessage.append("获取eId=" + eId + " shopNo=" + erpShopNo + "Token失败！");
				return false;
			}
			Long orderId = new Long(orderNo);
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimai_digiwin_ISV_MT_signKey, appAuthToken);
			CipCaterTakeoutOrderConfirmRequest request = new CipCaterTakeoutOrderConfirmRequest();
			request.setRequestSysParams(requestSysParams);
			request.setOrderId(orderId);
			try {
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_waimai("【聚宝盆】订单确认请求：eId=" + eId + " shopNo=" + erpShopNo + " getParams：" + request.getParams().toString());
				String resultJson = request.doRequest();
				HelpTools.writelog_waimai("【聚宝盆】订单确认返回：" + resultJson + " 订单号=" + orderNo);
				JSONObject jsonObject = new JSONObject(resultJson);
				try {
					String res = jsonObject.get("data").toString();
					if (res != null && res.toUpperCase().equals("OK")) {
						return true;
					}

				} catch (Exception e) {
					// TODO: handle exception

				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return false;

			} catch (IOException e) {
				// 处理IO异常

				errorMessage.append(e.getMessage());
				return false;
			} catch (URISyntaxException e) {
				// 处理URI语法异常

				errorMessage.append(e.getMessage());
				return false;
			} catch (Exception e) {

				errorMessage.append(e.getMessage());
				return false;
			}
		} catch (Exception e) {

			errorMessage.append(e.getMessage());
			return false;
		}

	}

	public static boolean orderCancel(String eId,String erpShopNo, String orderNo,String reason, String reasonCode, StringBuilder errorMessage)
	{
		//成功{"data":"ok"}
		//失败{"error":{"code":3,"error_type":"signature_error","message":"签名验证失败"}}
		String result = "";
		boolean nRet = false;
		try {
			String appAuthToken = getToken(eId, erpShopNo);
			if (appAuthToken == null || appAuthToken.length() == 0) {
				errorMessage.append("获取eId=" + eId + " shopNo=" + erpShopNo + "Token失败！");
				return false;
			}
			Long orderId = new Long(orderNo);
			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimai_digiwin_ISV_MT_signKey, appAuthToken);
			CipCaterTakeoutOrderCancelRequest request = new CipCaterTakeoutOrderCancelRequest();
			request.setRequestSysParams(requestSysParams);
			request.setOrderId(orderId);
			if (reason == null || reason.length() == 0 || reason.trim().length() == 0) {
				//聚宝盆就是个垃圾，自己调用美团接口，reason，reasonCode 明明可以为空的，反而到这不能为空了
				reason = "其他原因";
			}
			reasonCode = "2007";//理由码 状态值 详见聚宝盆开发文档（补充相关字段说明）
			request.setReason(reason);
			request.setReasonCode(reasonCode);

			try {
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_waimai("【聚宝盆】订单取消请求：eId=" + eId + " shopNo=" + erpShopNo + " getParams：" + request.getParams().toString());
				String resultJson = request.doRequest();
				HelpTools.writelog_waimai("【聚宝盆】订单取消返回：" + resultJson);
				JSONObject jsonObject = new JSONObject(resultJson);
				try {
					String res = jsonObject.get("data").toString();
					if (res != null && res.toUpperCase().equals("OK")) {
						return true;
					}

				} catch (Exception e) {
					// TODO: handle exception

				}
				//这里可以解析error 目前不解析
				errorMessage.append(resultJson);
				return false;

			} catch (IOException e) {
				// 处理IO异常

				errorMessage.append(e.getMessage());
				return false;
			} catch (URISyntaxException e) {
				// 处理URI语法异常

				errorMessage.append(e.getMessage());
				return false;
			} catch (Exception e) {

				errorMessage.append(e.getMessage());
				return false;
			}
		} catch (Exception e) {

			errorMessage.append(e.getMessage());
			return false;
		}

	}

	public static boolean orderRefundAgree(String eId,String erpShopNo, String orderNo,String reason, StringBuilder errorMessage)
  {
    //成功{"data":"ok"}
  	//失败{"error":{"code":3,"error_type":"signature_error","message":"签名验证失败"}}
	  String result = "";
	  boolean nRet = false;
  	try
  	{
			String appAuthToken = getToken(eId, erpShopNo);
			if(appAuthToken==null || appAuthToken.length()==0)
			{
				errorMessage.append("获取eId="+eId+" shopNo="+erpShopNo+"Token失败！");
				return false;
			}
			if (reason == null || reason.length() == 0 || reason.trim().length() == 0)
			{
				reason = "同意退款";
			}
  		Long orderId =new Long(orderNo);
  		RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimai_digiwin_ISV_MT_signKey, appAuthToken);
  		CipCaterTakeoutOrderRefundAcceptRequest request = new CipCaterTakeoutOrderRefundAcceptRequest();
  		request.setRequestSysParams(requestSysParams);
  		request.setOrderId(orderId);
  		request.setReason(reason);

  		try
  		{
  	    // 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
  			HelpTools.writelog_waimai("【聚宝盆】订单同意退款请求：eId=" +eId+" shopNo="+erpShopNo+" getParams："+request.getParams().toString());
  			String resultJson = request.doRequest();
  	    HelpTools.writelog_waimai("【聚宝盆】订单同意退款返回：" + resultJson);
  	    JSONObject jsonObject = new JSONObject(resultJson);
  	    try
  	    {
  	    	String res = jsonObject.get("data").toString();
    	    if(res !=null && res.toUpperCase().equals("OK"))
    	    {
    	    	return true;
    	    }

		    }
  	    catch (Exception e)
  	    {
		      // TODO: handle exception

		    }
  	    //这里可以解析error 目前不解析
  	    errorMessage.append(resultJson);
  	    return false;

  	  }
  		catch (IOException e)
  		{
  	    // 处理IO异常

  			errorMessage.append(e.getMessage());
        return false;
  	  }
  		catch (URISyntaxException e)
  		{
  	    // 处理URI语法异常

  			errorMessage.append(e.getMessage());
        return false;
  	  }
  		catch (Exception e)
    	{

    		errorMessage.append(e.getMessage());
        return false;
    	}
  	}
  	catch (Exception e)
  	{

  		errorMessage.append(e.getMessage());
      return false;
  	}

   }

	public static boolean orderRefundReject(String eId,String erpShopNo, String orderNo,String reason, StringBuilder errorMessage)
  {
    //成功{"data":"ok"}
  	//失败{"error":{"code":3,"error_type":"signature_error","message":"签名验证失败"}}
	  String result = "";
	  boolean nRet = false;
  	try
  	{
			String appAuthToken = getToken(eId, erpShopNo);
			if(appAuthToken==null || appAuthToken.length()==0)
			{
				errorMessage.append("获取eId="+eId+" shopNo="+erpShopNo+"Token失败！");
				return false;
			}
			if (reason == null || reason.length() == 0 || reason.trim().length() == 0)
			{
				reason = "拒绝退款";
			}
  		Long orderId =new Long(orderNo);
  		RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimai_digiwin_ISV_MT_signKey, appAuthToken);
  		CipCaterTakeoutOrderRefundRejectRequest request = new CipCaterTakeoutOrderRefundRejectRequest();
  		request.setRequestSysParams(requestSysParams);
  		request.setOrderId(orderId);
  		request.setReason(reason);
  		try
  		{
  	    // 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
  			HelpTools.writelog_waimai("【聚宝盆】订单拒绝退款请求：eId=" +eId+" shopNo="+erpShopNo+" getParams："+request.getParams().toString());
  			String resultJson = request.doRequest();
  	    HelpTools.writelog_waimai("【聚宝盆】订单拒绝退款返回：" + resultJson);
  	    JSONObject jsonObject = new JSONObject(resultJson);
  	    try
  	    {
  	    	String res = jsonObject.get("data").toString();
    	    if(res !=null && res.toUpperCase().equals("OK"))
    	    {
    	    	return true;
    	    }

		    }
  	    catch (Exception e)
  	    {
		      // TODO: handle exception

		    }
  	    //这里可以解析error 目前不解析
  	    errorMessage.append(resultJson);
  	    return false;

  	  }
  		catch (IOException e)
  		{
  	    // 处理IO异常

  			errorMessage.append(e.getMessage());
        return false;
  	  }
  		catch (URISyntaxException e)
  		{
  	    // 处理URI语法异常

  			errorMessage.append(e.getMessage());
        return false;
  	  }
  		catch (Exception e)
    	{

    		errorMessage.append(e.getMessage());
        return false;
    	}
  	}
  	catch (Exception e)
  	{

  		errorMessage.append(e.getMessage());
      return false;
  	}

   }

	/**
	 * 订单出餐接口(商家确认已完成出餐)
	 * @param eId
	 * @param erpShopNo
	 * @param orderNo
	 * @param errorMessage
	 * @return
	 */
	public static boolean orderPreparationMealComplete(String eId,String erpShopNo,String orderNo,StringBuilder errorMessage)
	{
		String result = "";
		boolean nRet = false;
		try
		{
			String appAuthToken = getToken(eId, erpShopNo);
			if(appAuthToken==null || appAuthToken.length()==0)
			{
				errorMessage.append("获取eId="+eId+" shopNo="+erpShopNo+"Token失败！");
				return false;
			}


			try
			{
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				jbpService jbpReq = new jbpService();
				baseEntityParaReq paraReq = new baseEntityParaReq();
				paraReq.setAppAuthToken(appAuthToken);
				paraReq.setDeveloperId(StaticInfo.waimai_digiwin_ISV_MT_developerId);
				paraReq.setSignKey(StaticInfo.waimai_digiwin_ISV_MT_signKey);
				preparationMealCompleteReq bizReq= new preparationMealCompleteReq();
				bizReq.setOrderId(orderNo);//BJG0019
				StringBuffer error = new StringBuffer("");
				HelpTools.writelog_waimai("【聚宝盆】订单出餐请求：eId=" +eId+" shopNo="+erpShopNo+" orderNo="+orderNo);
				String resultJson = jbpReq.preparationMealComplete(paraReq,bizReq,error);
				HelpTools.writelog_waimai("【聚宝盆】订单出餐返回：" + resultJson+" 订单号="+orderNo);
				JSONObject jsonObject = new JSONObject(resultJson);
				try
				{
					String res = jsonObject.get("data").toString();
					if(res !=null && res.toUpperCase().equals("OK"))
					{
						return true;
					}

				}
				catch (Exception e)
				{
					// TODO: handle exception

				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return false;

			}
			catch (IOException e)
			{
				// 处理IO异常

				errorMessage.append(e.getMessage());
				return false;
			}
			catch (URISyntaxException e)
			{
				// 处理URI语法异常

				errorMessage.append(e.getMessage());
				return false;
			}
			catch (Exception e)
			{

				errorMessage.append(e.getMessage());
				return false;
			}
		}
		catch (Exception e)
		{

			errorMessage.append(e.getMessage());
			return false;
		}

	}

}
