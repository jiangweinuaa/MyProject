package com.dsc.spos.waimai;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.redis.RedisPosPub;

public class WMJDDJTokenService extends SWaimaiBasicService {

	@Override
	public String execute(String json) throws Exception {
	// TODO Auto-generated method stub
	String res_json = json; //HelpTools.GetJBPTokenResponse(json);
	GetJDDJTokenResponse(res_json);
	
	if(res_json ==null ||res_json.length()==0)
  {
  	return null;
  }
  Map<String, Object> res = new HashMap<String, Object>();
  //this.processDUID(res_json, res);
  
  return null;
	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			JSONObject obj = new JSONObject(req);
			String companyNO = obj.get("companyNO").toString();
			String erpShopNO = obj.get("erpShopNO").toString();
			String erpShopName = obj.get("erpShopName").toString();
			String orderShopNO = obj.get("orderShopNO").toString();
			String orderShopName = obj.get("orderShopName").toString();
			String appAuthToken = obj.get("appAuthToken").toString();
			String businessId = obj.get("businessId").toString();
			String loadDocType = "2";//美团是2
			if (companyNO == null || companyNO.length() == 0 || companyNO == null || companyNO.length() == 0)
			{
				return;
			}
			
			//映射的门店资料保存到数据库 存在就更新，不存在就插入
			DelBean db1 = null;	
			db1 = new DelBean("TV_MAPPINGSHOP");										
			db1.addCondition("COMPANYNO", new DataValue(companyNO, Types.VARCHAR));
			db1.addCondition("ORGANIZATIONNO", new DataValue(erpShopNO, Types.VARCHAR));
			db1.addCondition("SHOP", new DataValue(erpShopNO, Types.VARCHAR));			
			db1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
			db1.addCondition("BUSINESSID", new DataValue(businessId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
						
			String mappingShopNO = companyNO + "_" + erpShopNO;
			String mappingShopInfo = obj.toString();
			
			if (erpShopName != null && erpShopName.length() > 255) 
			{
				erpShopName = erpShopName.substring(0, 254);
			}
			if (orderShopName != null && orderShopName.length() > 255) 
			{
				orderShopName = orderShopName.substring(0, 254);
			}
			String[] columns1 = { "COMPANYNO", "ORGANIZATIONNO", "SHOP", "LOAD_DOCTYPE", "BUSINESSID", "SHOPNAME",
					"ORDERSHOPNO", "ORDERSHOPNAME", "APPAUTHTOKEN", "MAPPINGSHOPNO", "MAPPINGSHOPINFO", "CNFFLG" };
			DataValue[] insValue1 = null;
				
			insValue1 = new DataValue[]{
						new DataValue(companyNO, Types.VARCHAR),
						new DataValue(erpShopNO, Types.VARCHAR),//组织编号=门店编号
						new DataValue(erpShopNO, Types.VARCHAR),//ERP门店
						new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
						new DataValue(businessId, Types.VARCHAR),//1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
						new DataValue(erpShopName, Types.VARCHAR),//ERP门店名称
						new DataValue(orderShopNO, Types.VARCHAR),//外卖平台门店ID
						new DataValue(orderShopName, Types.VARCHAR),//外卖平台门店名称
						new DataValue(appAuthToken, Types.VARCHAR),//token 
						new DataValue(mappingShopNO, Types.VARCHAR),//缓存里面的key（99_10001）
						new DataValue(mappingShopInfo, Types.VARCHAR),//缓存里面的value(json格式)					
						new DataValue("Y", Types.VARCHAR)	
				};

			InsBean ib1 = new InsBean("TV_MAPPINGSHOP", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1));
			
			this.doExecuteDataToDB();	
			HelpTools.writelog_waimai("【JBP门店映射保存成功】"+" 映射后门店编号mappingShopNO:"+mappingShopNO);
		
	  } 
		catch (SQLException e) 
		{
			HelpTools.writelog_waimai("【JBP门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
	  }
		catch (Exception e) 
		{
		  // TODO: handle exception
			HelpTools.writelog_waimai("【JBP门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
	  }
	
	}

	@Override
	protected List<InsBean> prepareInsertData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}
	
	public  String GetJDDJTokenResponse(String responseStr) throws Exception
	{
		if (responseStr==null || responseStr.length()==0) 
		{
			//writelog_waimaiException("美团外卖发送的请求为空！");
			return null;		
	  }		
		HelpTools.writelog_waimai("【JDDJ回传Token消息转码前】"+responseStr);
		String	responseStr_1 = HelpTools.getURLDecoderString(responseStr);//一次转码 （获取的是：%E6%B5%8B%E8%AF%95）
		//writelog_waimai("【美团URL转码后1】"+responseStr_1);
		String responseStr_2 = HelpTools.getURLDecoderString(responseStr_1);//二次转码（获取为 中文）
		//writelog_waimai("【聚宝盆门店绑定后Token回传消息转码前转码后2】"+responseStr_2);
		Calendar cal =Calendar.getInstance();
		Date date = cal.getTime();
		
		RedisPosPub redis = new RedisPosPub();
		String redis_key = "JDDJ_Token";
		String hash_key = String.valueOf(date.getTime());
		boolean nret = redis.setHashMap(redis_key, hash_key, responseStr_2);
		
		return responseStr_2;
		
		
		/*String[] MTResquest = responseStr_2.split("&");
		if(MTResquest==null || MTResquest.length ==0)
		{
			//writelog_waimai("聚宝盆门店绑定后Token回传消息发送的请求格式有误！");
			return null;
		}
		
		Map<String, String> map_MTResquest = new HashMap<String, String>();
		
		for (String string_mt : MTResquest) 
		{
			try 
			{				
				String[] ss = string_mt.split("=");
			  map_MTResquest.put(ss[0], ss[1]);					
			} 
			catch (Exception e) 
			{
				// TODO: handle exception		
				continue;			
			}		  					
	  }
		
		try 
		{
			String appAuthToken = map_MTResquest.get("appAuthToken");//门店绑定的授权token，将来的门店业务操作必须要传
			String ePoiId = map_MTResquest.get("ePoiId");//门店绑定时，传入的ERP厂商分配给门店的唯一标识 99_10001
			String poiId = map_MTResquest.get("poiId");//美团门店id
			String poiName = map_MTResquest.get("poiName");//美团门店名称
			String shopno_poi = map_MTResquest.get("ePoiId").toString();
			String businessId = map_MTResquest.get("businessId").toString();//1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
			String companyno = "99";
			String erpshopno = "";
			try 
		  {
			 String[] app_poi_codes = shopno_poi.split("_");
			 if (app_poi_codes != null && app_poi_codes.length >= 2)
			 {
				 companyno = app_poi_codes[0];
				 erpshopno = app_poi_codes[1];
			 }			 
		
	    } 
			catch (Exception e) 
			{
				 		 			 			 
			}			
			String Response_json = "";
			JSONObject obj = new JSONObject();
			if (poiId == null)
			{
				poiId = "";
			}
			if (poiName == null)
			{
				poiName = "";
			}
			if (appAuthToken == null)
			{
				appAuthToken = "";
			}
			if (businessId == null)
			{
				businessId = "";
			}
			obj.put("companyNO", companyno);
			obj.put("orderShopNO", poiId);
			obj.put("orderShopName", poiName);
			obj.put("erpShopNO", erpshopno);
			obj.put("erpShopName", "");
			obj.put("appAuthToken", appAuthToken);
			obj.put("businessId", businessId);
			
			Response_json = obj.toString();		 
	  	//Token存缓存
			String redis_key = "JBP_MappingShop";	
			if(businessId.equals("2"))
			{
				redis_key = "JBP_MappingShop";
			}
			else
			{
				redis_key = "JBP_MappingShop" + businessId;
			}
			String hash_key = ePoiId;
			writelog_waimai("【开始写缓存JBP_MappingShop门店映射】"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+Response_json);
		  try 
		  {
		  	 
		  	 RedisPosPub redis = new RedisPosPub();
		  	 boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
		  	 if(isexistHashkey)
		  	 {
		  		 
		  		 redis.DeleteHkey(redis_key, hash_key);//
		  		 writelog_waimai("【删除存在hash_key的缓存】成功！"+"redis_key:"+redis_key+" hash_key:"+hash_key);
		  	 }
				 boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
				 if(nret)
				 {
					writelog_waimai("【写缓存】OK"+" redis_key:"+redis_key+" hash_key:"+hash_key);
				 }
				 else
				 {
					writelog_waimai("【写缓存】Error"+" redis_key:"+redis_key+" hash_key:"+hash_key);
				 }
				 //redis.Close();
			
		   } 
		  catch (Exception e) 
		  {
		  	 writelog_waimai("【写缓存】Exception:"+e.getMessage()+" redis_key:"+redis_key+" hash_key:"+hash_key);	
		  }
		  	
			return Response_json;
			
	  } 
		catch (Exception e) 
		{
			//writelog_waimai("聚宝盆门店绑定后Token回传消息发送的请求格式有误！");
			return null;		
	  }		
	  */
	}
	

}
