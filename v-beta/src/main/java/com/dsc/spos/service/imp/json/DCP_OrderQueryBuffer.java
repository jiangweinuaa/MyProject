package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderQueryBufferReq;
import com.dsc.spos.json.cust.res.DCP_OrderQueryBufferRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderQueryBuffer extends SPosBasicService<DCP_OrderQueryBufferReq, DCP_OrderQueryBufferRes> 
{

	@Override
	protected boolean isVerifyFail(DCP_OrderQueryBufferReq req) throws Exception
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
	protected TypeToken<DCP_OrderQueryBufferReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderQueryBufferReq>(){};
	}

	@Override
	protected DCP_OrderQueryBufferRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderQueryBufferRes();
	}

	@Override
	protected DCP_OrderQueryBufferRes processJson(DCP_OrderQueryBufferReq req) throws Exception
	{
		// TODO Auto-generated method stub
		DCP_OrderQueryBufferRes res = this.getResponse();
		res.setSuccess(true);
		String eId = req.getRequest().geteId();
		String shopNo = req.getRequest().getShopNo();
		DCP_OrderQueryBufferRes.level1Elm datas = res.new level1Elm();
		try
		{
			RedisPosPub redis = new RedisPosPub();
			String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;

			// String redis_key = "WMORDER:99:10001";

			//Map<String, String> ordermap = redis.getALLHashMap(redis_key);
			Map<String, String> ordermap = redis.getALLHashMap_Hscan(redis_key);
			datas.setOrderList(new ArrayList<order>());
			ParseJson pj = new ParseJson();
			
			for (Map.Entry<String, String> entry : ordermap.entrySet()) 
			{
				if (entry.getValue() != null) {
					try {
						String orderJson = entry.getValue();
						order orderModel = pj.jsonToBean(orderJson,
								new TypeToken<order>() {
								});
						// orderList.add(orderModel);
						if(orderModel!=null&&orderModel.getLoadDocType()!=null)
						{
							if(orderModel.getLoadDocType().equals(orderLoadDocType.ELEME)||orderModel.getLoadDocType().equals(orderLoadDocType.MEITUAN)||orderModel.getLoadDocType().equals(orderLoadDocType.JDDJ)||orderModel.getLoadDocType().equals(orderLoadDocType.MTSG)||orderModel.getLoadDocType().equals(orderLoadDocType.DYWM))
							{
								if(orderModel.getShopNo().equals(shopNo)==false)
								{
									HelpTools.writelog_fileName("【获取缓存订单】【外卖类型(1.2.8)订单】【请求门店和实际返回门店不一致】单号orderNO="+orderModel.getOrderNo()+" Redis主键："+redis_key+"  返回res："+orderJson,"waimailogEx");
									continue;
								}
							}
							else
							{

								boolean flag = false;
								if(orderModel.getShopNo()!=null&&orderModel.getShopNo().equals(shopNo))
								{
									flag = true;
								}
								if(orderModel.getMachShopNo()!=null&&orderModel.getMachShopNo().equals(shopNo))
								{
									flag = true;
								}
								if(orderModel.getShippingShopNo()!=null&&orderModel.getShippingShopNo().equals(shopNo))
								{
									flag = true;
								}

								if(flag)
								{

								}
								else
								{
									HelpTools.writelog_fileName("【获取缓存订单】【其他类型订单】【请求门店和实际返回门店(下单、生产、配送)不一致】单号orderNO="+orderModel.getOrderNo()+" Redis主键："+redis_key+"  返回res："+orderJson,"waimailogEx");
									continue;

								}

							}

						}
						datas.getOrderList().add(orderModel);

					} catch (Exception e) {	
						HelpTools.writelog_fileName("【获取缓存订单】【异常】"+e.getMessage()+" 异常订单json:"+entry.getValue()+" Redis主键："+redis_key,"waimailogEx");
						continue;

					}

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
	protected String getQuerySql(DCP_OrderQueryBufferReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}}
