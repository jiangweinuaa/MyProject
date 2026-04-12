package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderMappingShopQueryNewReq;
import com.dsc.spos.json.cust.res.DCP_OrderMappingShopQueryNewRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMELMShopService;
import com.dsc.spos.waimai.WMMappingShopModel;
import com.google.gson.reflect.TypeToken;

import eleme.openapi.sdk.api.entity.shop.OShop;
import eleme.openapi.sdk.api.entity.user.OAuthorizedShop;
import eleme.openapi.sdk.api.entity.user.OUser;

public class DCP_OrderMappingShopQueryNew extends SPosAdvanceService<DCP_OrderMappingShopQueryNewReq,DCP_OrderMappingShopQueryNewRes> {

	@Override
	protected void processDUID(DCP_OrderMappingShopQueryNewReq req, DCP_OrderMappingShopQueryNewRes res) throws Exception {
	// TODO Auto-generated method stub
		
		res.setDatas(new ArrayList<DCP_OrderMappingShopQueryNewRes.level1Elm>());
		String keyText = req.getKeyTxt();
		String docType = req.getDocType();
		String eId = req.geteId();
		String langType = req.getLangType();
		String businessId = req.getBusinessId();
		if(docType.equals("1"))
		{
			res.setDeveloperId(StaticInfo.waimaiELMAPPKey);//饿了么key
			res.setSignKey(StaticInfo.waimaiELMSecret);//饿了么secret
			try 
			{
				//查询所有APPKEY
				List<Map<String, Object>> elmAppKeyList =	PosPub.getWaimaiAppConfig(this.dao, eId, docType);
			  //查询ERP所有门店信息
			  List<WMMappingShopModel> shopList = getErpShop(eId, langType);
			  //
			  //List<Map<String, Object>> existMappingShops = this.getMappingShopFromDB(docType);
				if (elmAppKeyList == null || elmAppKeyList.isEmpty() == true)//为空还是走原来的配置文件
				{
					StringBuilder errorMeassge = new StringBuilder();
				  OUser elmUser =	WMELMShopService.getUser(errorMeassge);
				  if(elmUser == null)
				  {
				  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMeassge.toString());
				  }
				  List<OAuthorizedShop> authorizedShops = elmUser.getAuthorizedShops();
				  if(authorizedShops == null || authorizedShops.size() == 0)
				  {
				  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该商户账号下授权店铺列表为空！");
				  }	  
				  
					if(shopList == null || shopList.size()==0)
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "ERP没有维护门店！");
					}
				  for (OAuthorizedShop oAuthorizedShop : authorizedShops) 
				  {
				  	DCP_OrderMappingShopQueryNewRes.level1Elm oneLv1 = res.new level1Elm();
				  	StringBuilder errorMeassge2 = new StringBuilder();
				  	long shopId = oAuthorizedShop.getId();
				  	String shopName = oAuthorizedShop.getName();
						if (keyText != null && keyText.length() > 0)
				  	{
				  		boolean isContain = shopName.contains(keyText);
				  		if(!isContain)
				  			continue;			
				  	}		  	
				  	String erpShopNO = "";
				  	String erpShopName = "";
				  	try 
				    {
				      OShop oshop =	WMELMShopService.getShop(shopId, errorMeassge2);
				      if(oshop != null )		     
				      {
				      	
				      	String openId = oshop.getOpenId();//正确映射的格式是 99_10001	否则返回空	      	
				      	try 
				      	{
						       String[] ss = openId.split("_");
						       if (ss != null && ss.length >= 2)
									 {
						      	 erpShopNO = ss[1];
									 }			      
					      } 
				      	catch (Exception e) 
				      	{
				      		erpShopNO = "";			
					      }		      		      		      	
				      }
				      //查询ERP的门店名称
				      for (WMMappingShopModel oneData : shopList) 
				      {
				      	if(oneData.getErpShopNo().equals(erpShopNO))
				      	{
				      		erpShopName = oneData.getErpShopName();
				      		break;
				      	}
				      }
				      
				      oneLv1.setOrderShopName(shopName);
			      	oneLv1.setOrderShopNO(String.valueOf(shopId));
			      	oneLv1.setErpShopNO(erpShopNO);
			      	oneLv1.setAppAuthToken("");
			      	oneLv1.setErpShopName(erpShopName);
			      	res.getDatas().add(oneLv1);
				    } 
				    catch (Exception e) 
				    {
				    	oneLv1.setOrderShopName(shopName);
			      	oneLv1.setOrderShopNO(String.valueOf(shopId));
			      	oneLv1.setErpShopNO(erpShopNO);
			      	oneLv1.setAppAuthToken("");
			      	oneLv1.setErpShopName(erpShopName);
			      	res.getDatas().add(oneLv1);	      	
				    	continue;			
				    }	  	
				  	
				  }	  

					
				}
				else 
				{
					boolean IsHasOUser = false;//是否有总店账号
					boolean IsHasOAuthorizedShop = false;//是否有管辖门店
					int endIndex = elmAppKeyList.size() -1;
					for (int i = 0; i < elmAppKeyList.size(); i++) 
					{
						try 
						{
							Map<String, Object> map = elmAppKeyList.get(i);
							String elmAPPKey = map.get("APPKEY").toString();
							String elmAPPSecret = map.get("APPSECRET").toString();
							String elmAPPName = map.get("APPNAME").toString();
							String elmIsTest = map.get("ISTEST").toString();
							boolean elmIsSandbox = false;
							if (elmIsTest != null && elmIsTest.equals("Y"))
							{
								elmIsSandbox = true;
							}
							
							StringBuilder errorMeassge = new StringBuilder();
						  OUser elmUser =	WMELMShopService.getUser(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,errorMeassge);
						  if(elmUser == null)
						  {
						  	/*throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMeassge.toString());*/						  	
						  	if (i == endIndex && IsHasOUser == false)
						  	{
						  		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMeassge.toString());
						  	}
						  	else
						  	{
						  		continue;
						  	}
						  	
						  }
						  IsHasOUser = true;
						  List<OAuthorizedShop> authorizedShops = elmUser.getAuthorizedShops();
						  if(authorizedShops == null || authorizedShops.size() == 0)
						  {
								if (i == endIndex && IsHasOAuthorizedShop == false)
								{
									throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该商户账号下授权店铺列表为空！");
								}
						  	else
						  	{
						  		continue;
						  	}
						  	
						  }	  
						  IsHasOAuthorizedShop = true;
						  				  
							if(shopList == null || shopList.size()==0)
							{
								throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "ERP没有维护门店！");
							}
						  for (OAuthorizedShop oAuthorizedShop : authorizedShops) 
						  {
						  	DCP_OrderMappingShopQueryNewRes.level1Elm oneLv1 = res.new level1Elm();
						  	oneLv1.setIsTest(elmIsTest);
						  	oneLv1.setAppKey(elmAPPKey);
						  	oneLv1.setAppSecret(elmAPPSecret);
						  	oneLv1.setAppName(elmAPPName);
						  	StringBuilder errorMeassge2 = new StringBuilder();
						  	long shopId = oAuthorizedShop.getId();
						  	String shopName = oAuthorizedShop.getName();
								if (keyText != null && keyText.length() > 0)
						  	{
						  		boolean isContain = shopName.contains(keyText);
						  		if(!isContain)
						  			continue;			
						  	}		  	
						  	String erpShopNO = "";
						  	String erpShopName = "";
						  	try 
						    {
						      OShop oshop = WMELMShopService.getShop(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,shopId, errorMeassge2);
						      if(oshop != null )		     
						      {
						      	//erpShopName = oshop.getAddressText();
						      	String openId = oshop.getOpenId();//正确映射的格式是 99_10001	否则返回空	      	
						      	try 
						      	{
								       String[] ss = openId.split("_");
								       if (ss != null && ss.length >= 2)
											 {
								      	 erpShopNO = ss[1];
											 }			      
							      } 
						      	catch (Exception e) 
						      	{
						      		erpShopNO = "";			
							      }		      		      		      	
						      }
						      //查询ERP的门店名称
						      for (WMMappingShopModel oneData : shopList) 
						      {
						      	if(oneData.getErpShopNo().equals(erpShopNO))
						      	{
						      		erpShopName = oneData.getErpShopName();
						      		break;
						      	}
						      }
						      
						      oneLv1.setOrderShopName(shopName);
					      	oneLv1.setOrderShopNO(String.valueOf(shopId));
					      	oneLv1.setErpShopNO(erpShopNO);
					      	oneLv1.setAppAuthToken("");
					      	oneLv1.setErpShopName(erpShopName);
					      	res.getDatas().add(oneLv1);
						    } 
						    catch (Exception e) 
						    {
						    	oneLv1.setOrderShopName(shopName);
					      	oneLv1.setOrderShopNO(String.valueOf(shopId));
					      	oneLv1.setErpShopNO(erpShopNO);
					      	oneLv1.setAppAuthToken("");
					      	oneLv1.setErpShopName(erpShopName);
					      	res.getDatas().add(oneLv1);	      	
						    	continue;			
						    }	  	
						  	
						  }	  
																	
			      } 
						catch (Exception e) 
						{
							continue;
				
			      }
			
			    }
			
		    }
							
		  } 
			catch (Exception e) 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		  }
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
			return;			
		}
		else
		{
			res.setDeveloperId(StaticInfo.waimaiMTAPPID);//美团 聚宝盆developerId
			res.setSignKey(StaticInfo.waimaiMTSignKey);//美团 聚宝盆signKey
			try 
			{
				//查询所有门店信息
				List<WMMappingShopModel> shopList = getErpShop(eId, langType);
				if(shopList == null || shopList.size()==0)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "ERP没有维护门店！");
				}
				//获取已经映射完成的门店
				List<WMMappingShopModel> mappingShopModels = getMappingShop(businessId);
				
				for (WMMappingShopModel oneData : shopList) 
				{
					DCP_OrderMappingShopQueryNewRes.level1Elm oneLv1 = res.new level1Elm();
					String erpShopNO = oneData.getErpShopNo();
					String erpShopName = oneData.getErpShopName();
					
					if (keyText != null && keyText.length() > 0)
			  	{
			  		boolean isContain = erpShopName.contains(keyText);//门店名称是否包含
			  		if(!isContain)
			  		{
			  			isContain = erpShopNO.contains(keyText);//门店编号是否包含
			  		}		  		
			  		if(!isContain)
			  		{
			  			continue;	
			  		}
			  	}
					
					
					String orderShopNO = "";
					String orderShopName = "";
					String appAuthToken = "";				
					if (mappingShopModels != null && mappingShopModels.size() > 0)
					{
						for (WMMappingShopModel wmMappingShopModel : mappingShopModels) 
						{
							String erpShopNO_mapping = wmMappingShopModel.getErpShopNo();
							if(erpShopNO.equals(erpShopNO_mapping))
							{
								orderShopNO = wmMappingShopModel.getOrderShopNo();
								orderShopName = wmMappingShopModel.getOrderShopName();							
								appAuthToken = wmMappingShopModel.getAppAuthToken();	
								break;
							}																					
			      }
					}
					oneLv1.setOrderShopNO(orderShopNO);
					oneLv1.setOrderShopName(orderShopName);
					oneLv1.setErpShopNO(erpShopNO);
					oneLv1.setAppAuthToken(appAuthToken);
					oneLv1.setErpShopName(erpShopName);
					res.getDatas().add(oneLv1);	
					oneLv1 =null;
		    }
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功！");	
		
		  } 
			catch (Exception e) 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		  }
		}
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderMappingShopQueryNewReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderMappingShopQueryNewReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderMappingShopQueryNewReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderMappingShopQueryNewReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
					
		if(req.getDocType()==null)
		{
			errCt++;
			errMsg.append("平台类型不可为空值, ");
			isFail = true;
		}
			
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
    
		return isFail;
	
	}

	@Override
	protected TypeToken<DCP_OrderMappingShopQueryNewReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderMappingShopQueryNewReq>(){};
	}

	@Override
	protected DCP_OrderMappingShopQueryNewRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderMappingShopQueryNewRes() ;
	}
	
	private List<WMMappingShopModel> getErpShop(String eId,String langType) throws Exception
	{
		String sql = "select * from (";		
		sql += "SELECT distinct A.ORGANIZATIONNO as SHOPID,B.ORG_NAME AS SHOPNAME FROM DCP_ORG A LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.status='100' AND B.LANG_TYPE='"+langType+"'";
		sql += "WHERE A.ORG_FORM='2' AND A.status='100' AND A.EID='"+eId+"'";
		sql += ")";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		List<WMMappingShopModel> shopList = new ArrayList<WMMappingShopModel>();
		if (getQData != null && getQData.isEmpty() == false)
		{
			
			
			for (Map<String, Object> map : getQData) 
			{
				WMMappingShopModel oneLv1 = new WMMappingShopModel();				
				String shopId = map.get("SHOPID").toString();
				String shopName = map.get("SHOPNAME").toString();
				oneLv1.setErpShopNo(shopId);
				oneLv1.setErpShopName(shopName);
				shopList.add(oneLv1);				
		  }
			return shopList;
			
		}
		return null;
	}
	
	private List<WMMappingShopModel> getMappingShop(String businessType) throws Exception
	{
		List<WMMappingShopModel> shopList = new ArrayList<WMMappingShopModel>();
		//从缓存获取
		try 
		{
			RedisPosPub redis = new RedisPosPub();
			String redis_key = "JBP_MappingShop";//
			if(businessType!=null&&businessType.trim().length()>0)
			{
				if(businessType.equals("2")==false)
				{
					redis_key ="JBP_MappingShop"+businessType;
				}
			}
			
			Map<String, String> mapShopList = redis.getALLHashMap(redis_key);
			redis.Close();
			for (Map.Entry<String, String> entry : mapShopList.entrySet())
			{
				try 
				{
					if (entry.getValue() != null) 
					{
						JSONObject obj = new JSONObject(entry.getValue());
						WMMappingShopModel mappingShop = new WMMappingShopModel();
						if (!obj.isNull("orderShopNo")) {
							mappingShop.setOrderShopNo(obj.get("orderShopNo").toString());
						}
						if (!obj.isNull("orderShopName")) {
							mappingShop.setOrderShopName(obj.get("orderShopName").toString());
						}
						if (!obj.isNull("erpShopNo")) {
							mappingShop.setErpShopNo(obj.get("erpShopNo").toString());
						}
						if (!obj.isNull("erpShopName")) {
							mappingShop.setErpShopName(obj.get("erpShopName").toString());
						}
						if (!obj.isNull("appAuthToken")) {
							mappingShop.setAppAuthToken(obj.get("appAuthToken").toString());
						}
						shopList.add(mappingShop);		
					}
			
		    } 
				catch (Exception e) 
				{
					continue;		
		    }
				 
			}
		
	  } 
		catch (Exception e) 
		{
		 // TODO: handle exception
			return shopList;
	  }
						
		return shopList;
		
		
	}
	
	private List<Map<String, Object>> getMappingShopFromDB(String loadDocType) throws Exception
	{
		String sql = " select * from OC_MAPPINGSHOP where LOAD_DOCTYPE='"+loadDocType+"'";
		HelpTools.writelog_waimai("【ELM映射查询已经映射过的门店】查询语句："+sql);
		List<Map<String, Object>> getHeader = this.doQueryData(sql, null);
		return getHeader;
		
	}
	
	private OShop getMappingShopOpenId(List<Map<String, Object>> MappingShops,long shopId)
	{
		if(MappingShops==null||MappingShops.size()==0)
		{
			return null;
		}
		OShop oshop = null;
		for (Map<String, Object> map : MappingShops) 
		{
			if(map.get("ORDERSHOPNO").toString().equals(Long.toString(shopId)))
			{		
				oshop = new OShop();
				oshop.setId(shopId);
				oshop.setOpenId(map.get("MAPPINGSHOPNO").toString());
				oshop.setAddressText(map.get("SHOPNAME").toString());
				break;
			}
		
	  }
		return oshop;
		
	}

}
