package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_JBPTokenRedisReq;
import com.dsc.spos.json.cust.res.DCP_JBPTokenRedisRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_JBPTokenRedis  extends SPosAdvanceService<DCP_JBPTokenRedisReq,DCP_JBPTokenRedisRes> {

	@Override
	protected void processDUID(DCP_JBPTokenRedisReq req, DCP_JBPTokenRedisRes res) throws Exception {
		// TODO Auto-generated method stub
		RedisPosPub redis = new RedisPosPub();
		String redis_key = "JBP_MappingShop";	

		Map<String, String> ordermap = redis.getALLHashMap(redis_key);
		redis.Close();


		for (Map.Entry<String, String> entry : ordermap.entrySet())
		{
			if(entry.getValue()!=null)
			{
				JSONObject obj=new JSONObject(entry.getValue());
				//array.put(obj);
				String eId = obj.get("EID").toString();
				String erpShopNO = obj.get("erpShopNO").toString();
				String erpShopName = obj.get("erpShopName").toString();
				String orderShopNO = obj.get("orderShopNO").toString();
				String orderShopName = obj.get("orderShopName").toString();
				String appAuthToken = obj.get("appAuthToken").toString();
				String businessId = obj.get("businessId").toString();
				String loadDocType = "2";//美团是2
				if (eId == null || eId.length() == 0 || eId == null || eId.length() == 0)
				{
					return;
				}
				erpShopName = getErpShopName(eId, erpShopNO);
				//映射的门店资料保存到数据库 存在就更新，不存在就插入
				DelBean db1 = null;	
				db1 = new DelBean("OC_MAPPINGSHOP");										
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("ORGANIZATIONNO", new DataValue(erpShopNO, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(erpShopNO, Types.VARCHAR));			
				db1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
				db1.addCondition("BUSINESSID", new DataValue(businessId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				String mappingShopNO = eId + "_" + erpShopNO;
				String mappingShopInfo = obj.toString();

				if (erpShopName != null && erpShopName.length() > 255) 
				{
					erpShopName = erpShopName.substring(0, 254);
				}
				if (orderShopName != null && orderShopName.length() > 255) 
				{
					orderShopName = orderShopName.substring(0, 254);
				}
				String[] columns1 = { "EID", "ORGANIZATIONNO", "SHOPID", "LOAD_DOCTYPE", "BUSINESSID", "SHOPNAME",
						"ORDERSHOPNO", "ORDERSHOPNAME", "APPAUTHTOKEN", "MAPPINGSHOPNO", "MAPPINGSHOPINFO", "STATUS" };
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
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
						new DataValue("100", Types.VARCHAR)	
				};

				InsBean ib1 = new InsBean("OC_MAPPINGSHOP", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));

				this.doExecuteDataToDB();	
				HelpTools.writelog_waimai("【JBP门店映射保存成功】"+" 映射后门店编号mappingShopNO:"+mappingShopNO);




			}
		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_JBPTokenRedisReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_JBPTokenRedisReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_JBPTokenRedisReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_JBPTokenRedisReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_JBPTokenRedisReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_JBPTokenRedisReq>(){};
	}

	@Override
	protected DCP_JBPTokenRedisRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_JBPTokenRedisRes();
	}

	private String getErpShopName (String eId,String erpShopNO) throws Exception
	{
		try 
		{
			String sql =" select * from DCP_ORG_lang where EID='"+eId+"' and organizationno='"+erpShopNO+"' and LANG_TYPE='zh_CN'";
			List<Map<String, Object>> shopsName =	this.doQueryData(sql, null);
			if (shopsName != null && shopsName.isEmpty() == false)
			{
				return shopsName.get(0).get("ORG_NAME").toString();		  	
			}

		} 
		catch (Exception e) 
		{

		}

		return "";
	}

}
