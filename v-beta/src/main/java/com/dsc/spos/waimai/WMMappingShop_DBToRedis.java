package com.dsc.spos.waimai;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;


public class WMMappingShop_DBToRedis 
{
	private String logFileName = "MappingShopDBToRedis";
	public void SetMappingShopDBToRedis(String loadDocType,String isJBP) throws Exception
	{
		if (HelpTools.elmMappingShopList!=null)
		{
			HelpTools.elmMappingShopList.clear();
		}
		if (HelpTools.mtMappingShopList!=null)
		{
			HelpTools.mtMappingShopList.clear();
		}
		if (HelpTools.jbpMappingShopList!=null)
		{
			HelpTools.jbpMappingShopList.clear();
		}
		if (HelpTools.sgmtMappingShopList!=null)
		{
			HelpTools.sgmtMappingShopList.clear();
		}
		HelpTools.writelog_fileName("【内存中的外卖映射门店】清空下",logFileName);
		String redis_key = "";
		String sql = "select * from  dcp_mappingshop where businessid='2' and shopID<>' ' and shopID is not null   and load_doctype='"+loadDocType+"'";
		 List<Map<String, Object>> getData = null;
		 HelpTools.writelog_fileName("【同步MappingShop到Redis】平台类型="+loadDocType+" 查询SQL="+sql,logFileName);
		try 
		{
			 getData = StaticInfo.dao.executeQuerySQL(sql, null);
			 if(getData==null||getData.isEmpty())
			 {
				 HelpTools.writelog_fileName("【同步MappingShop到Redis】平台类型="+loadDocType+" 查询结果为空",logFileName);
				 return;
			 }
				 		 
		} 
		catch (Exception e)
		{
			HelpTools.writelog_fileName("【同步MappingShop到Redis】平台类型="+loadDocType+" 查询异常:"+e.getMessage(),logFileName);
			return;
	
		}
		
		if(loadDocType.equals(orderLoadDocType.ELEME))
		{
			redis_key = orderRedisKeyInfo.redisKey_elemeMappingshop;
			for (Map<String, Object> map : getData) 
			{
				try 
				{
				//存缓存
					String hash_value = "";
					JSONObject obj = new JSONObject();
					String orderShopNo = map.get("ORDERSHOPNO").toString();
					String eId = map.get("EID").toString();
					String erpShopNo = map.get("SHOPID").toString();
					String channelId = map.get("CHANNELID").toString();
					String mappingShopNo = eId +"_"+erpShopNo;
					
					obj.put("orderShopNo",orderShopNo);
					obj.put("orderShopName", map.get("ORDERSHOPNAME").toString());
					obj.put("erpShopNo", erpShopNo);
					obj.put("erpShopName", map.get("SHOPNAME").toString());
					obj.put("appAuthToken", map.get("APPAUTHTOKEN").toString());
					obj.put("eId", eId);
					obj.put("channelId",channelId);
					obj.put("businessId", "2");//美团聚宝盆才有 默认2代表外卖
					obj.put("appKey", map.get("APPKEY").toString());
					obj.put("appName", map.get("APPNAME").toString());
					obj.put("appSecret", map.get("APPSECRET").toString());
					obj.put("isTest", map.get("ISTEST").toString());
					obj.put("isJbp", "N");
					obj.put("mappingShopNo", mappingShopNo);
					
					hash_value = obj.toString();	
					SaveRedisMappingShop(loadDocType,redis_key, mappingShopNo,hash_value);		//主键是ERP门店编号	
				  SaveRedisMappingShop(loadDocType,redis_key, orderShopNo,hash_value);//主键是外卖平台门店编号
		
				} 
				catch (Exception e) 
				{
					continue;	
				}	
			}
			
			
		}
	  else	if(loadDocType.equals(orderLoadDocType.MEITUAN))
		{
	  	if(isJBP!=null&&isJBP.equals("Y"))
	  	{
	  		redis_key = orderRedisKeyInfo.redisKey_jbpMappingshop;
	  		for (Map<String, Object> map : getData) 
				{
					try 
					{
					//存缓存
						String hash_value = "";
						JSONObject obj = new JSONObject();
											
						String orderShopNo = map.get("ORDERSHOPNO").toString();
						String eId = map.get("EID").toString();
						String erpShopNo = map.get("SHOPID").toString();
						String channelId = map.get("CHANNELID").toString();
						String mappingShopNo = eId +"_"+erpShopNo;
						
						obj.put("orderShopNo",orderShopNo);
						obj.put("orderShopName", map.get("ORDERSHOPNAME").toString());
						obj.put("erpShopNo", erpShopNo);
						obj.put("erpShopName", map.get("SHOPNAME").toString());
						obj.put("appAuthToken", map.get("APPAUTHTOKEN").toString());
						obj.put("eId", eId);
						obj.put("channelId",channelId);
						obj.put("businessId", "2");//美团聚宝盆才有 默认2代表外卖
						obj.put("appKey", map.get("APPKEY").toString());
						obj.put("appName", map.get("APPNAME").toString());
						obj.put("appSecret", map.get("APPSECRET").toString());
						obj.put("isTest", map.get("ISTEST").toString());
						obj.put("isJbp", "Y");
						obj.put("mappingShopNo", mappingShopNo);
						
						
						
						hash_value = obj.toString();	
						SaveRedisMappingShop(loadDocType,redis_key, mappingShopNo,hash_value);		//主键是ERP门店编号					 
			
					} 
					catch (Exception e) 
					{
						continue;	
					}	
				}
	  		
	  	}
	  	else
	  	{
	  		redis_key = orderRedisKeyInfo.redisKey_mtMappingshop;//"MT_MappingShop";
	  		for (Map<String, Object> map : getData) 
				{
					try 
					{
					//存缓存
						String hash_value = "";
						JSONObject obj = new JSONObject();
												
						String orderShopNo = map.get("ORDERSHOPNO").toString();
						String eId = map.get("EID").toString();
						String erpShopNo = map.get("SHOPID").toString();
						String channelId = map.get("CHANNELID").toString();
						String mappingShopNo = eId +"_"+erpShopNo;
						
						obj.put("orderShopNo",orderShopNo);
						obj.put("orderShopName", map.get("ORDERSHOPNAME").toString());
						obj.put("erpShopNo", erpShopNo);
						obj.put("erpShopName", map.get("SHOPNAME").toString());
						obj.put("appAuthToken", map.get("APPAUTHTOKEN").toString());
						obj.put("eId", eId);
						obj.put("channelId",channelId);
						obj.put("businessId", "2");//美团聚宝盆才有 默认2代表外卖
						obj.put("appKey", map.get("APPKEY").toString());
						obj.put("appName", map.get("APPNAME").toString());
						obj.put("appSecret", map.get("APPSECRET").toString());
						obj.put("isTest", map.get("ISTEST").toString());
						obj.put("isJbp", "N");
						obj.put("mappingShopNo", mappingShopNo);
						
						
						
						
						
						hash_value = obj.toString();	
						SaveRedisMappingShop(loadDocType,redis_key, mappingShopNo,hash_value);		//主键是ERP门店编号	
					  SaveRedisMappingShop(loadDocType,redis_key, orderShopNo,hash_value);//主键是外卖平台门店编号
			
					} 
					catch (Exception e) 
					{
						continue;	
					}	
				}
	  	}
			
		}
	  else	if(loadDocType.equals(orderLoadDocType.JDDJ))
		{
	  	redis_key = orderRedisKeyInfo.redisKey_jddjMappingshop;
	  	for (Map<String, Object> map : getData) 
			{
				try 
				{
				//存缓存
					String hash_value = "";
					JSONObject obj = new JSONObject();
									
					String orderShopNo = map.get("ORDERSHOPNO").toString();
					String eId = map.get("EID").toString();
					String erpShopNo = map.get("SHOPID").toString();
					String channelId = map.get("CHANNELID").toString();
					String mappingShopNo = eId +"_"+erpShopNo;
					
					obj.put("orderShopNo",orderShopNo);
					obj.put("orderShopName", map.get("ORDERSHOPNAME").toString());
					obj.put("erpShopNo", erpShopNo);
					obj.put("erpShopName", map.get("SHOPNAME").toString());
					obj.put("appAuthToken", map.get("APPAUTHTOKEN").toString());
					obj.put("eId", eId);
					obj.put("channelId",channelId);
					obj.put("businessId", "2");//美团聚宝盆才有 默认2代表外卖
					obj.put("appKey", map.get("APPKEY").toString());
					obj.put("appName", map.get("APPNAME").toString());
					obj.put("appSecret", map.get("APPSECRET").toString());
					obj.put("isTest", map.get("ISTEST").toString());
					obj.put("isJbp", "N");
					obj.put("mappingShopNo", mappingShopNo);
									
					hash_value = obj.toString();	
					SaveRedisMappingShop(loadDocType,redis_key, mappingShopNo,hash_value);		//主键是ERP门店编号	
				  SaveRedisMappingShop(loadDocType,redis_key, orderShopNo,hash_value);//主键是外卖平台门店编号
		
				} 
				catch (Exception e) 
				{
					continue;	
				}	
			}
			
		}
		else 
		{
		
			return;
	
		}
		
	}
	
	private void SaveRedisMappingShop(String docType,String redis_key, String hash_key, String hash_value) throws Exception
	{		
	  try 
	  {
	  	 HelpTools.writelog_fileName("【开始写缓存MappingShop门店映射创建】"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+hash_value,logFileName);
	  	 RedisPosPub redis = new RedisPosPub();
	  	 boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
	  	 if(isexistHashkey)
	  	 {
	  		
	  		 //redis.DeleteHkey(redis_key, hash_key);//
	  		 HelpTools.writelog_fileName("【MappingShop存在hash_key的缓存】！"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+hash_value,logFileName);
	  	 }
	  	 else
	  	 {
	  		 boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
				 if(nret)
				 {
					 HelpTools.writelog_fileName("【MappingShop写缓存】OK"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+hash_value,logFileName);
				 }
				 else
				 {
					 HelpTools.writelog_fileName("【MappingShop写缓存】Error"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+hash_value,logFileName);
				 }
	  		 
	  	 }
			
			 //redis.Close();
	   } 
	  catch (Exception e) 
	  {
	  	HelpTools.writelog_fileName("【MappingShop写缓存】Exception:"+e.getMessage(),logFileName);	
	  }
	}

}
