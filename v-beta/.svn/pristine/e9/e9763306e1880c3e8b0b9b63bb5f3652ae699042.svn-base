package com.dsc.spos.waimai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.sankuai.meituan.waimai.opensdk.exception.ApiOpException;
import com.sankuai.meituan.waimai.opensdk.exception.ApiSysException;
import com.sankuai.meituan.waimai.opensdk.factory.APIFactory;
import com.sankuai.meituan.waimai.opensdk.vo.PoiParam;
import com.sankuai.meituan.waimai.opensdk.vo.SystemParam;

public class WMMTShopService 
{
	//public final static SystemParam sysPram = new SystemParam(StaticInfo.waimaiMTAPPID, StaticInfo.waimaiMTSignKey);
	public static String mtLogFileName = "MTShopServicelog";
	/**
	 * 设置营业
	 * @param companyNo
	 * @param erpShopNo
	 * @param errorMessage
	 * @return
	 */
	public static boolean setShopOpen(String app_poi_code,StringBuilder errorMessage)
	{
		try 
		{			
			String ePoiId = app_poi_code;
			String resultJson = "";
		
			try 
			{						
				resultJson =	APIFactory.getPoiAPI().poiOpen(getSystemParam(), ePoiId);						
				try 
				{			
					if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
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
	public static boolean setShopClose(String app_poi_code,StringBuilder errorMessage)
	{
		try 
		{			
			String ePoiId = app_poi_code;
			String resultJson = "";
			try 
			{			
				resultJson =	APIFactory.getPoiAPI().poiClose(getSystemParam(), ePoiId);			
				try 
				{				
					if(resultJson !=null && resultJson.toUpperCase().equals("OK"))
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
	public static int getShopStatus(String app_poi_code,StringBuilder errorMessage)
	{
		try 
		{
			String ePoiId = app_poi_code;
			try 
			{			
				// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
				//HelpTools.writelog_waimai("【MT】查询门店信息请求：companyNo=" +companyNo+" shopNo="+erpShopNo);		
				List<PoiParam> poiInfos = APIFactory.getPoiAPI().poiMget(getSystemParam(), ePoiId);
				//HelpTools.writelog_waimai("【MT】查询门店信息返回：" + resultJson);
				try 
				{				
				 if (poiInfos != null && poiInfos.isEmpty() == false)
				 {
					 int status =  poiInfos.get(0).getOpen_level();//1可配送 3休息中 0-未上线
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
					errorMessage.append(e.getMessage());
				}
				//这里可以解析error				
				return -1;

			} 
			catch (ApiOpException e) 
	  	{
	       
	       errorMessage.append(e.getMsg());
	       return -1;
	    }
	  	catch (ApiSysException e) 
	  	{ 		 
	              
	       errorMessage.append(e.getExceptionEnum().getMsg());    
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
	
	
	/**
	 * 批量获取门店的app_poi_code，包括正式门店和审核中的门店
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<String> getShopIds(StringBuilder errorMessage) throws Exception
	{
		try 
		{
			HelpTools.writelog_fileName("【MT】批量获取门店的app_poi_code开始",mtLogFileName);				
			String resultJson = APIFactory.getPoiAPI().poiGetIds(getSystemParam());	
			//返回的内容 ["123","456"]
			try 
			{			
				JSONArray jsonArray = new JSONArray(resultJson);
				List<String> shopIds =  new ArrayList<String>();
				for(int i = 0;i<jsonArray.length();i++)
				{
					shopIds.add(jsonArray.get(i).toString());
				}
		
		    return shopIds;
				
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				 
	       errorMessage.append(e.getMessage());
	       return null;
			}
	
		} 
		catch (ApiOpException e) 
  	{
       
       errorMessage.append(e.getMsg());
       return null;
    }
  	catch (ApiSysException e) 
  	{ 		 
              
       errorMessage.append(e.getExceptionEnum().getMsg());    
       return null;
  	}
       
		catch (Exception e)
		{
			errorMessage.append(e.getMessage());
			return null;
	
		}
		
		
		
	}
	

	/**
	 * 批量获取门店的所有详细信息
	 * @param shopList
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	public static List<PoiParam> getShopIdsInfo(List<String> shopList,StringBuilder errorMessage ) throws Exception
	{
		try 
		{			
			// 发送请求，接收Json ，所有Request都有doRequest方法直接调用即可
			String app_poi_codes = "";
			if(shopList==null||shopList.size()==0)
			{
				errorMessage.append("请求的门店列表为空！");
				return null;
			}
			for (String item_shop : shopList) 
			{
				app_poi_codes +=item_shop+",";	
		  }
			
			app_poi_codes = app_poi_codes.substring(0,app_poi_codes.length()-1);
			HelpTools.writelog_fileName("【MT】查询门店信息请求：companyNo=" +app_poi_codes,mtLogFileName);		
			List<PoiParam> poiInfos = APIFactory.getPoiAPI().poiMget(getSystemParam(), app_poi_codes);
					
			return poiInfos;

		} 
		catch (ApiOpException e) 
  	{
       
       errorMessage.append(e.getMsg());
       return null;
    }
  	catch (ApiSysException e) 
  	{ 		 
              
       errorMessage.append(e.getExceptionEnum().getMsg());    
       return null;
       
    } 	
  	catch (Exception e) 
  	{
  		
  		errorMessage.append(e.getMessage());
      return null;
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
	
}
