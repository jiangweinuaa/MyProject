package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.waimai.entity.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderRedisProcessReq;
import com.dsc.spos.json.cust.res.DCP_OrderRedisProcessRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

import javax.servlet.jsp.tagext.TryCatchFinally;

public class DCP_OrderRedisProcess extends SPosAdvanceService<DCP_OrderRedisProcessReq,DCP_OrderRedisProcessRes> {

	static String delRedisLogFileName = "deleteRedislog";
	@Override
	protected void processDUID(DCP_OrderRedisProcessReq req, DCP_OrderRedisProcessRes res) throws Exception
	{
		// TODO Auto-generated method stub
		String eId = req.getRequest().geteId();
		String shopId = req.getRequest().getShopId();
		List<String> shopList = new ArrayList<String>();
		if (shopId.toUpperCase().equals("ALL"))
		{
			String sql = "select * from dcp_org where org_form='2' and status='100' and eid='" + eId + "'";
			List<Map<String, Object>> orgMapList = this.doQueryData(sql, null);
			if (orgMapList != null && orgMapList.isEmpty() == false)
			{
				for (Map<String, Object> map : orgMapList)
				{
					String orgNO = map.get("ORGANIZATIONNO").toString();
					shopList.add(orgNO);
				}

				shopList.add("HQ");// 易成有个HQ默认门店
			}
		} else
		{
			shopList.add(shopId);
		}

		if (shopList == null || shopList.isEmpty())
		{
			return;
		}

		for (String string_shopno : shopList)
		{
			String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + string_shopno;
			String redis_key_printError = orderRedisKeyInfo.redis_OrderPrintError + ":" + eId + ":" + string_shopno;
			try
			{
				HelpTools.writelog_fileName("【查询门店Redis开始】门店shop=" + string_shopno + " redis_key：" + redis_key,
						delRedisLogFileName);
				RedisPosPub redis = new RedisPosPub();
                try
                {
                    HelpTools.writelog_fileName("【删除门店打印异常外卖单Redis】开始，门店shop=" + string_shopno + " redis_key：" + redis_key_printError,
                            delRedisLogFileName);
                    redis.DeleteKey(redis_key_printError);
                    HelpTools.writelog_fileName("【删除门店打印异常外卖单Redis】成功，门店shop=" + string_shopno + " redis_key：" + redis_key_printError,
                            delRedisLogFileName);

                }
                catch (Exception e)
                {
                    HelpTools.writelog_fileName("【删除门店打印异常外卖单Redis】异常："+e.getMessage()+"，门店shop=" + string_shopno + " redis_key：" + redis_key_printError,
                            delRedisLogFileName);
                }
				Map<String, String> ordermap = redis.getALLHashMap(redis_key);
				// redis.Close();
				JSONObject jsonobj = new JSONObject();
				List<order> redisOrders = new ArrayList<order>();
				ParseJson pj = new ParseJson();
				for (Map.Entry<String, String> entry : ordermap.entrySet())
				{
					if (entry.getValue() != null)
					{
						try
						{
							String ss = entry.getValue().replace("\"{\"performanceServiceFee\"",
									"\"{\\\"performanceServiceFee\\\"");
							order dcpOrder = pj.jsonToBean(ss, new TypeToken<order>()
							{
							});
							if (dcpOrder != null)
							{
								redisOrders.add(dcpOrder);
							}
							
						} 
						catch (Exception e)
						{
							continue;
						}
						

					}
				}

				pj = null;

				ArrayList<String> delRedisOrderNO = new ArrayList<String>();// 直接删除缓存
				ArrayList<String> needProcessOrderNO = new ArrayList<String>();// 需要对比数据库状态
				if (redisOrders != null && redisOrders.isEmpty() == false)
				{
					HelpTools.writelog_fileName("【查询门店Redis结束】门店shop=" + string_shopno + " redis_key：" + redis_key
							+ " 总记录数：" + redisOrders.size(), delRedisLogFileName);
					for (order redis_onelv1 : redisOrders)
					{
						String redis_status = redis_onelv1.getStatus();
						if (redis_status == null)
						{
							redis_status = "";
						}
						String redis_refundstatus = redis_onelv1.getRefundStatus();
						if (redis_refundstatus == null)
						{
							redis_refundstatus = "";
						}
						String redis_orderno = redis_onelv1.getOrderNo();
						String redis_loadDocType = redis_onelv1.getLoadDocType();
						String redis_shipDate = redis_onelv1.getShipDate();
						if (redis_status.equals("1") || redis_refundstatus.equals("2")
								|| redis_refundstatus.equals("7"))
						{
						    boolean isWaimai = false;
						    if (orderLoadDocType.ELEME.equals(redis_loadDocType)||orderLoadDocType.MEITUAN.equals(redis_loadDocType)||orderLoadDocType.JDDJ.equals(redis_loadDocType)||orderLoadDocType.MTSG.equals(redis_loadDocType)||orderLoadDocType.WAIMAI.equals(redis_loadDocType)||orderLoadDocType.DYWM.equals(redis_loadDocType))
                            {
                                isWaimai = true;
                            }
                            // 未接单的，申请退单，申请部分退单
						    if (redis_status.equals("1")&&isWaimai)
                            {
                                // 微商城的订单默认就是已接单
                                String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                                int shipDate_i = 0;
                                try
                                {
                                    shipDate_i = Integer.parseInt(redis_shipDate);
                                } catch (Exception e)
                                {
                                }
                                int curDate_i = 0;
                                try
                                {
                                    curDate_i = Integer.parseInt(curDate);
                                } catch (Exception e)
                                {
                                }
                                // 配送日期大于等当前日期，先不用删除
                                if (shipDate_i - curDate_i >= 0)
                                {
                                    needProcessOrderNO.add(redis_orderno);
                                } else
                                {
                                    delRedisOrderNO.add(redis_orderno);
                                }
                            }
						    else
                            {

                                needProcessOrderNO.add(redis_orderno);
                            }

						} else
						{
							// 微商城的订单默认就是已接单
							String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
							int shipDate_i = 0;
							try
							{
								shipDate_i = Integer.parseInt(redis_shipDate);
							} catch (Exception e)
							{
							}
							int curDate_i = 0;
							try
							{
								curDate_i = Integer.parseInt(curDate);
							} catch (Exception e)
							{
							}
							// 配送日期大于等当前日期，先不用删除
							if (shipDate_i - curDate_i >= 0)
							{
								needProcessOrderNO.add(redis_orderno);
							} else
							{
								delRedisOrderNO.add(redis_orderno);
							}

						}

					}

					// 需要对比数据库的单号，查询下
					if (needProcessOrderNO.isEmpty() == false)
					{
						List<order> dbOrders = getOrderInfo(eId, needProcessOrderNO);
						if (dbOrders != null && dbOrders.isEmpty() == false)
						{
							// 循环查询订单数据库里状态
							for (order db_onelv1 : dbOrders)
							{
								String db_status = db_onelv1.getStatus();
								if (db_status == null)
								{
									db_status = "";
								}
								String db_refundstatus = db_onelv1.getRefundStatus();
								if (db_refundstatus == null)
								{
									db_refundstatus = "";
								}
								String db_orderno = db_onelv1.getOrderNo();
								String db_loadDocType = db_onelv1.getLoadDocType();
								String db_shipDate = db_onelv1.getShipDate();
								if (db_status.equals("1") || db_refundstatus.equals("2") || db_refundstatus.equals("7"))
								{
									// 数据库里订单状态：未接单的，申请退单，申请部分退单 不能删除缓存
									// needProcessOrderNO.add(redis_orderno);
								} else
								{

									// 微商城数据没有结案的
									String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
									int shipDate_i = 0;
									try
									{
										shipDate_i = Integer.parseInt(db_shipDate);
									} catch (Exception e)
									{
									}
									int curDate_i = 0;
									try
									{
										curDate_i = Integer.parseInt(curDate);
									} catch (Exception e)
									{
									}
									// 配送日期大于等于当前日期，
									if (shipDate_i - curDate_i >= 0)
									{
									    boolean isExistSale = this.isExistSale(db_onelv1.geteId(),db_onelv1.getShopNo(),db_onelv1.getOrderNo());
									    if (isExistSale)
                                        {
                                            HelpTools.writelog_fileName("【配送日期大于等于当前日期,已生成销售单】单号=" + db_orderno,
                                                    delRedisLogFileName);
                                            delRedisOrderNO.add(db_orderno);
                                        }
									    else
                                        {
                                            HelpTools.writelog_fileName("【配送日期大于等于当前日期,先不用删除】单号=" + db_orderno,
                                                    delRedisLogFileName);
                                        }


									} else
									{
										// 配送日期小于当前日期，没有结案的可以删除了
										delRedisOrderNO.add(db_orderno);
									}

								}
							}
						}

					}

					HelpTools.writelog_fileName("【需要删除门店Redis开始】门店shop=" + string_shopno + " redis_key：" + redis_key
							+ " 需要删除总记录数：" + delRedisOrderNO.size(), delRedisLogFileName);
					// 删除需要清理的缓存
					if (delRedisOrderNO.isEmpty() == false)
					{
						RedisPosPub delredis = new RedisPosPub();
						try
						{
							for (String del_orderno : delRedisOrderNO)
							{
								try
								{
									String hash_key = del_orderno;
									HelpTools.writelog_fileName(
											"【删除门店Redis】开始删除  redis_key：" + redis_key + " hash_key：" + del_orderno,
											delRedisLogFileName);
									delredis.DeleteHkey(redis_key, hash_key);
									HelpTools.writelog_fileName(
											"【删除门店Redis】删除成功  redis_key：" + redis_key + " hash_key：" + del_orderno,
											delRedisLogFileName);
								} catch (Exception e)
								{
									continue;
								}

							}

						} catch (Exception e)
						{

						}
						delredis.Close();
					}
				} else
				{
					HelpTools.writelog_fileName("【查询门店Redis】没有资料！门店shop=" + string_shopno + " redis_key：" + redis_key,
							delRedisLogFileName);
				}

			} catch (Exception e)
			{
				HelpTools.writelog_fileName(
						"【查询门店Redis异常】异常：" + e.getMessage() + " 门店shop=" + string_shopno + " redis_key：" + redis_key,
						delRedisLogFileName);
				continue;

			}

		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderRedisProcessReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderRedisProcessReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderRedisProcessReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderRedisProcessReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_OrderRedisProcessReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderRedisProcessReq>(){} ;
	}

	@Override
	protected DCP_OrderRedisProcessRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderRedisProcessRes();
	}
	
	protected List<order> getOrderInfo(String eid,List<String> ordernos) throws Exception
	{
		List<order> res = new ArrayList<order>();
		
		String ordersStr = getString(ordernos);
		String sql = "select * from dcp_order where eid='"+eid+"'";
		sql += " and orderno in ("+ordersStr+")";
		List<Map<String, Object>> mapList = this.doQueryData(sql, null);
		if (mapList != null && mapList.isEmpty() == false)
		{		
			for (Map<String, Object> oneData : mapList) 
			{
				order oneLv1 = new order();
				oneLv1.setGoodsList(new ArrayList<orderGoodsItem>());
				oneLv1.setPay(new ArrayList<orderPay>());
				String orderNO = oneData.get("ORDERNO").toString();
				String shopNO = oneData.get("SHOP").toString(); 
				String status = oneData.get("STATUS").toString();
				String refundStatus = oneData.get("REFUNDSTATUS").toString();
				oneLv1.setStatus(status);
				oneLv1.setRefundStatus(refundStatus);
				oneLv1.setOrderNo(orderNO);
				oneLv1.setShopNo(shopNO);
				oneLv1.setShipDate(oneData.get("SHIPDATE").toString());
				oneLv1.setLoadDocType(oneData.get("LOADDOCTYPE").toString());
				res.add(oneLv1);
		  }
			
			
		}
		return res;
		
	}
	
	protected String getString(List<String> str)
	{
		String str2 = "";

		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		//System.out.println(str2);

		return str2;
	}

	private boolean isExistSale(String eId,String shopId,String orderNo) throws  Exception
    {
        boolean nRet = false;
        try {
            String sql = " select saleNo from dcp_sale where order_id='"+orderNo+"'";
            if (eId!=null&&!eId.isEmpty())
            {
                sql +=" and eid='"+eId+"' ";
            }
            if (shopId!=null&&!shopId.isEmpty())
            {
                sql +=" and shopId='"+shopId+"' ";
            }
            List<Map<String, Object>> mapList = this.doQueryData(sql, null);
            if (mapList!=null&&mapList.isEmpty()==false)
            {
                nRet = true;
            }
        }
        catch (Exception ex)
        {

        }
        return nRet;
    }

}
