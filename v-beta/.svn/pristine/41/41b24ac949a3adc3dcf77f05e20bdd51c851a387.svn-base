package com.dsc.spos.waimai;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.waimai.WuXiangOderReturn.ReturnOrderSub;
import com.google.gson.reflect.TypeToken;

import eleme.openapi.sdk.api.entity.message.OMessage;

import eleme.openapi.ws.sdk.utils.JacksonUtils;

public class WuXiangReturnService extends SWaimaiBasicService
{


	@Override
	public String execute(String json) throws Exception 
	{		
		//String res_json = HelpTools.GetJBPResponse(json);
		String res_json = json;
	  if(res_json ==null ||res_json.length()==0)
	  {
	  	return null;
	  }
		Map<String, Object> res = new HashMap<String, Object>();
	  this.processDUID(res_json, res);
		
		return null;	
	}

	@Override
	protected void processDUID(String message, Map<String, Object> res) throws Exception 
	{
		try		
		{
			//**************开始处理消息，自己写***********
			HelpTools.writelog_waimai("【收到舞像退款的消息内容】"+message);								
			ParseJson pj = new ParseJson();
			
			WuXiangOderReturn curreginfo=pj.jsonToBean(message, new TypeToken<WuXiangOderReturn>(){});
			pj=null;
			
			
			String orderNO = curreginfo.getOrderNo();	
			String shopId = curreginfo.getSub_store_code();	
			String status = "2";//必须要设置，只有已接单的才能退
			String refundstatus = "2";//订单退单状态说明：1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功
			List<ReturnOrderSub> orderRefundGoods = new ArrayList<ReturnOrderSub>();
				
			//更新缓存
			String response_string = UpdateOrderProcess(shopId, orderNO, status, refundstatus,orderRefundGoods);
		  //存数据库			
			SaveOrder(response_string);
			
			return ;
		}
		catch (Exception e) 
		{
			
			try 
			{
				
				HelpTools.writelog_waimai("舞像退款的消息内容异常】"+e.getMessage());			
		  } 
			catch (Exception e2) 
			{
		
		  }
		  	
			return ;			
		}		
	
	
	}


	//更新缓存
	private String UpdateOrderProcess(String shopid,String orderno, String status, String refundStatus,List<ReturnOrderSub> orderRefundGoods) throws Exception
	{	
		String Response_json = "";
		try 
		{/*								
			String companyno = "99";
			String erpshopno = shopid;						
			OrderGetRes.level1Elm model = new OrderGetRes().new level1Elm();
	  	model.setCompanyNO(companyno);
	  	model.setShopNO(erpshopno);
	  	model.setOrganizationNO(erpshopno);
	  	model.setOrderNO(orderno);
	  	model.setLoadDocType("7");
	  	model.setStatus(status);
	  	model.setRefundStatus(refundStatus);
	  	model.setGoods(new ArrayList<OrderGetRes.level2Elm>());	
	  	
	  	ParseJson pj = new ParseJson();	  	
	  	String	ordermap =pj.beanToJson(model);		
	  	pj=null;
	  	
	  	Response_json = ordermap;
 
		  RedisPosPub redis = new RedisPosPub();
		  String redis_key = "WMORDER" + "_" + companyno + "_" + erpshopno;
		  String hash_key = orderno;
			if (erpshopno != null && erpshopno.trim().length() > 0)
		  {
				boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
				if (isexistHashkey) 
				{			
				  redis.DeleteHkey(redis_key, hash_key);//
				  HelpTools.writelog_waimai("【舞象删除存在hash_key的缓存ELM】成功！"+" redis_key:"+redis_key+" hash_key:"+hash_key);
				}
				HelpTools.writelog_waimai("【舞象开始写缓存】"+" redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+Response_json);
				boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
				if (nret) 
				{
				  HelpTools.writelog_waimai("【舞象写缓存】OK"+" redis_key:"+redis_key+" hash_key:"+hash_key);
				} 
				else 
				{
				  HelpTools.writelog_waimai("【舞象写缓存】Error"+" redis_key:"+redis_key+" hash_key:"+hash_key);
				}
		  	
		  }	  
			//redis.Close();			
		  return Response_json;
	  */} 
		catch (Exception e) 
		{			
			HelpTools.writelog_waimai("舞象更新缓存中饿了么内容异常！"+e.getMessage()+" OrderNO:"+orderno);				
	  }
		return Response_json;
		
	}
	
	//存数据库
	private void SaveOrder(String req) throws Exception
	{
		try 
		{
			if(this.dao == null)
			{			
			  //this.dao = SPosDbPoolDAOImp.getDao();			  
			}
			JSONObject obj = new JSONObject(req);
			String status = obj.get("status").toString();
			String refundStatus = obj.get("refundStatus").toString();
			String orderNO = obj.get("orderNO").toString();
			String companyNO = obj.get("companyNO").toString();	
			String shopNO = obj.get("shopNO").toString();
			String organizationNO = shopNO;
			String loadDocType = obj.get("loadDocType").toString();
			
			boolean IsExistOrder = IsExistOrder(companyNO, organizationNO, shopNO, orderNO,loadDocType);
			
			if(IsExistOrder)//存在就Update
			{
				UptBean ub1 = null;	
				ub1 = new UptBean("TV_ORDER");
				ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
				
				ub1.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus,Types.VARCHAR));
				
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											
				ub1.addCondition("COMPANYNO", new DataValue(companyNO, Types.VARCHAR));
				ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
				ub1.addCondition("SHOP", new DataValue(shopNO, Types.VARCHAR));
				ub1.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
				ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));							
		    this.addProcessData(new DataProcessBean(ub1));
				
				this.doExecuteDataToDB();	
				HelpTools.writelog_waimai("【舞象更新STATUS、REFUNDSTATUS成功】"+" 订单号orderNO:"+orderNO+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
										
			}			
			else
			{
				HelpTools.writelog_waimai("【舞象退款更新的单据不存在】"+" 订单号orderNO:"+orderNO+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);			
			}
		} 
		catch (SQLException e) 
		{
			HelpTools.writelog_waimai("【舞象退单更新数据库执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
	  }
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【舞象退单更新数据库执行语句】异常："+e.getMessage() + "\r\n req请求内容:" + req);		
	  }
	}

	
	private void DeleteRedis(String redis_key,String hash_key) throws Exception
	{
		try 
		{			
			RedisPosPub redis = new RedisPosPub();
			HelpTools.writelog_waimai("【开始删除缓存】"+" redis_key:"+redis_key+" hash_key:"+hash_key);
			redis.DeleteHkey(redis_key, hash_key);//
  		HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！"+" redis_key:"+redis_key+" hash_key:"+hash_key);
  		//redis.Close();
	
    } 
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【删除存在hash_key的缓存】异常"+e.getMessage()+" redis_key:"+redis_key+" hash_key:"+hash_key);
    }
	}
	
	 /**
   * 将json对象中包含的null和JSONNull属性修改成""
   *
   * @param jsonObj
   */
  public static JSONObject filterNull(JSONObject jsonObj)  throws Exception {
      Iterator<String> it = jsonObj.keys();
      Object obj = null;
      String key = null;
      while (it.hasNext()) {
          key = it.next();
          obj = jsonObj.get(key);
          if (obj instanceof JSONObject) {
              filterNull((JSONObject) obj);
          }
          if (obj instanceof JSONArray) {
              JSONArray objArr = (JSONArray) obj;
              for (int i = 0; i < objArr.length(); i++) {
                  filterNull(objArr.getJSONObject(i));
              }
          }
          if (obj == null) {
              jsonObj.put(key, "");
          }
          if (obj.equals(null)) {
              jsonObj.put(key, "");
          }
      }
      return jsonObj;
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
	
	
	private boolean IsExistOrder(String companyno,String organizationno,String shopNO,String orderNO,String loadDocType) throws Exception
	{
    boolean nRet = false;
		
		String sql = "select * from tv_order where COMPANYNO='" + companyno + "' and ORGANIZATIONNO='" + organizationno + "'";
		sql += " and SHOP='" + shopNO + "' and ORDERNO='" + orderNO + "' and LOAD_DOCTYPE='" + loadDocType + "'";
		
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql,null);
		
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			nRet = true;
		}
		return nRet;
					
	}
	
	private String GetOrderInfo(String companyno,String organizationno,String shopNO,String orderNO,String loadDocType) throws Exception
	{
		return "";
	}
	
	
	
}
