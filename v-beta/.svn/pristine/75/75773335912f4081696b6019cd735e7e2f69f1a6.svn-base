package com.dsc.spos.waimai;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.waimai.model.WMJBPQueryListByEPoiId;
import com.dsc.spos.waimai.model.WMJBPQueryPoiInfo;
import com.google.gson.reflect.TypeToken;
import com.sankuai.sjst.platform.developer.domain.RequestSysParams;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutPoiCloseRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutPoiInfoQueryRequest;
import com.sankuai.sjst.platform.developer.request.CipCaterTakeoutPoiOpenRequest;

public class WMJBPShopService 
{
	
	/**
	 * 设置营业
	 * @param companyNo
	 * @param erpShopNo
	 * @param errorMessage
	 * @return
	 */
	public static boolean setShopOpen(String companyNo,String erpShopNo,StringBuilder errorMessage)
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;

			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);
			CipCaterTakeoutPoiOpenRequest request = new CipCaterTakeoutPoiOpenRequest();
			request.setRequestSysParams(requestSysParams);		
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_waimai("【聚宝盆】设置营业请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString());		
				String resultJson = request.doRequest();
				HelpTools.writelog_waimai("【聚宝盆】设置营业返回：" + resultJson);
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
	
	/**
	 * 设置门店休息
	 * @param companyNo
	 * @param erpShopNo
	 * @param errorMessage
	 * @return
	 */
	public static boolean setShopClose(String companyNo,String erpShopNo,StringBuilder errorMessage)
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;

			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);
			CipCaterTakeoutPoiCloseRequest request = new CipCaterTakeoutPoiCloseRequest();
			request.setRequestSysParams(requestSysParams);		
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_waimai("【聚宝盆】设置闭店请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString());		
				String resultJson = request.doRequest();
				HelpTools.writelog_waimai("【聚宝盆】设置闭店返回：" + resultJson);
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
	
	
	/**
	 * 查询门店是否营业
	 * @param companyNo
	 * @param erpShopNo
	 * @param errorMessage
	 * @return
	 */
	public static int getShopStatus(String companyNo,String erpShopNo,StringBuilder errorMessage)
	{
		try 
		{
			String appAuthToken = WMJBPOrderProcess.getToken(companyNo, erpShopNo);
			String ePoiId = companyNo + "_" + erpShopNo;

			RequestSysParams requestSysParams = new RequestSysParams(StaticInfo.waimaiMTSignKey, appAuthToken);
			CipCaterTakeoutPoiInfoQueryRequest request = new CipCaterTakeoutPoiInfoQueryRequest();
			request.setRequestSysParams(requestSysParams);	
			request.setEPoiIds(ePoiId);
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				HelpTools.writelog_waimai("【聚宝盆】查询门店信息请求：companyNo=" +companyNo+" shopNo="+erpShopNo+" getParams："+request.getParams().toString());		
				String resultJson = request.doRequest();
				HelpTools.writelog_waimai("【聚宝盆】查询门店信息返回：" + resultJson);
				try 
				{
					ParseJson pj = new ParseJson();
					WMJBPQueryPoiInfo resserver=pj.jsonToBean(resultJson, new TypeToken<WMJBPQueryPoiInfo>(){});
					pj=null;
					
				 if (resserver.getData() != null && resserver.getData().isEmpty() == false)
				 {
					 int status =  resserver.getData().get(0).getIsOpen();//1可配送 3休息中 0-未上线
					 if(status==1)
					 {
						return 1;
					 }
						
					 else
					 {
						 return 0;
					 }				 
				 }
					
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				//这里可以解析error
				errorMessage.append(resultJson);
				return -1;

			} 
			catch (IOException e) 
			{
				// 处理IO异常
				
				errorMessage.append(e.getMessage());
				return -1;
			} 
			catch (URISyntaxException e) 
			{
				// 处理URI语法异常   
				
				errorMessage.append(e.getMessage());
				return -1;
			}
			catch (Exception e) 
			{
				
				errorMessage.append(e.getMessage());
				return -1;
			}  
		} 
		catch (Exception e) 
		{
			
			errorMessage.append(e.getMessage());
			return -1;
		}
		
	}
	

}
