package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OtherDeliveryOrderRemindQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OtherDeliveryOrderRemindQuery_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OtherDeliveryOrderRemindQuery_OpenRes.level2Order;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

public class DCP_OtherDeliveryOrderRemindQuery_Open extends SPosBasicService<DCP_OtherDeliveryOrderRemindQuery_OpenReq, DCP_OtherDeliveryOrderRemindQuery_OpenRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OtherDeliveryOrderRemindQuery_OpenReq req) throws Exception
	{
		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().geteId())) 
		{
			errCt++;
			errMsg.append("企业编号eId不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getRequest().getShopNo())) 
		{
			errCt++;
			errMsg.append("当前门店shopNo不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OtherDeliveryOrderRemindQuery_OpenReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OtherDeliveryOrderRemindQuery_OpenReq>(){};
	}

	@Override
	protected DCP_OtherDeliveryOrderRemindQuery_OpenRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OtherDeliveryOrderRemindQuery_OpenRes();
	}

	@Override
	protected DCP_OtherDeliveryOrderRemindQuery_OpenRes processJson(DCP_OtherDeliveryOrderRemindQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		DCP_OtherDeliveryOrderRemindQuery_OpenRes res = this.getResponse();
		res.setSuccess(true);
		String eId = req.getRequest().geteId();
		String shopNo = req.getRequest().getShopNo();
		DCP_OtherDeliveryOrderRemindQuery_OpenRes.level1Elm datas = res.new level1Elm();
		try
		{
			RedisPosPub redis = new RedisPosPub();
			String redis_key = orderRedisKeyInfo.redis_OrderNotify + ":" + eId + ":" + shopNo;

			// String redis_key = "WMORDER:99:10001";

			//Map<String, String> ordermap = redis.getALLHashMap(redis_key);
			Map<String, String> ordermap = redis.getALLHashMap_Hscan(redis_key);
			datas.setDeliveryList(new ArrayList<>());
			ParseJson pj = new ParseJson();
			ArrayList<String> hashKeyList = new ArrayList<String>();
			for (Map.Entry<String, String> entry : ordermap.entrySet()) 
			{
				if (entry.getValue() != null) {
					try {
						String orderJson = entry.getValue();
						level2Order orderModel = pj.jsonToBean(orderJson,
								new TypeToken<level2Order>() {
								});
						datas.getDeliveryList().add(orderModel);
						hashKeyList.add(entry.getKey());

					} catch (Exception e) {	
						HelpTools.writelog_fileName("【获取物流下单后未及时接单通知POS缓存】【异常】"+e.getMessage()+" 异常订单json:"+entry.getValue()+" Redis主键："+redis_key,"waimailogEx");
						continue;

					}

				}
			}

			//删除 刚才取到的key
			if(hashKeyList!=null&&hashKeyList.size()>0)
			{
				try
				{
					String[] fields = new String[hashKeyList.size()];
					fields = hashKeyList.toArray(fields);

					RedisPosPub redis_del = new RedisPosPub();
					redis_del.DeleteHighkey(redis_key, fields);

				}
				catch (Exception e)
				{


				}
			}
			
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceDescription(e.getMessage());
		}
		
		
		
		
		res.setDatas(datas);
				
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OtherDeliveryOrderRemindQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}}
