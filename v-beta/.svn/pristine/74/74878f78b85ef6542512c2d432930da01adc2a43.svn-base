package com.dsc.spos.scheduler.job;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;

import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderQueryRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.dsc.spos.waimai.jddj.HelpTransferOrder;
import com.dsc.spos.waimai.jddj.OrderInfoDTO;
import com.dsc.spos.waimai.jddj.OrderShoudSettlementAmount;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JDDJOrderGet extends InitJob 
{/*
	Logger logger = LogManager.getLogger(JDDJOrderGet.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中
	String jddjLogFileName = "jddjJoblog";

	public JDDJOrderGet()
	{

	}

	public String doExe() throws Exception
	{
		// 返回信息
		String sReturnInfo = "";
		if (StaticInfo.waimaiJDDJAPPKey == null || StaticInfo.waimaiJDDJAPPKey.length() == 0
				|| StaticInfo.waimaiJDDJSecret == null || StaticInfo.waimaiJDDJSecret.length() == 0
				|| StaticInfo.waimaiJDDJToken == null || StaticInfo.waimaiJDDJToken.length() == 0) {
			return sReturnInfo;
		}
		logger.info("\r\n***************JDDJOrderGet同步START****************\r\n");
		HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】同步START！",jddjLogFileName);
		try 
		{
			//此服务是否正在执行中
			if (bRun)
			{		
				logger.info("\r\n*********JDDJOrderGet同步正在执行中,本次调用取消:************\r\n");
				HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】同步正在执行中,本次调用取消！",jddjLogFileName);
				return sReturnInfo;
			}

			bRun=true;//

			RedisPosPub redis = new RedisPosPub();	
				String redis_key = "ELM_Token";
				String accessTokenStr = redis.getString(redis_key);//包含了过期时间戳
				redis.Close();		

			try 
			{
				String sql = this.getQuerySql();

				List<Map<String, Object>> getQData = this.doQueryData(sql, null);
				if (getQData != null && getQData.isEmpty() == false)
				{
					for (Map<String, Object> oneData : getQData)
					{

						try 
						{
							String orderNO = oneData.get("BILLID").toString();
							String orderStatusStr = oneData.get("STATUSID").toString();
							String timestamp = oneData.get("TIMESTAMP").toString();
							String remark = oneData.get("REMARK").toString();
							int orderStatus = Integer.parseInt(orderStatusStr);
							HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】单号orderNO="+orderNO+" 订单状态statusID="+orderStatus,jddjLogFileName);
							StringBuilder error = new StringBuilder();
							//订单状态（20010:锁定，20020:订单取消，20030:订单取消申请，20040:超时未支付系统取消，20060:系统撤销订单，20050:暂停，31000:等待付款，31020:已付款，41000:待处理，32000:等待出库，33040:配送中，33060:已妥投，34000:京东已收款，90000:订单完成）
							if(orderStatus == 41000||orderStatus == 32000)//新的订单消息
							{
								HelpTools.writelog_fileName("【查询新订单开始】单号orderNO="+orderNO,jddjLogFileName);
								OrderInfoDTO orderDTO = HelpJDDJHttpUtil.GetJDDJOrderByOrderID(orderNO, error);							

								if(orderDTO == null)
								{
									HelpTools.writelog_fileName("【查询新订单结束】异常："+error.toString()+" 单号orderNO="+orderNO,jddjLogFileName);
									continue;
								}			
								HelpTools.writelog_fileName("【新订单转成实体类开始】单号orderNO="+orderNO,jddjLogFileName);
								DCP_OrderQueryRes.level1Elm tvOrder = HelpTransferOrder.CoverOrderEntity(orderDTO,error);
								if(tvOrder == null )
								{
									HelpTools.writelog_fileName("【新订单转成实体类开始】异常：" + error.toString() + " 单号orderNO=" + orderNO,jddjLogFileName);
									continue;
								}
								HelpTools.writelog_fileName("【新订单转成实体类成功】单号orderNO="+orderNO,jddjLogFileName);
								//orderStatus = tvOrder.getPlatformStatus();
								String status = tvOrder.getStatus();
								String refundStatus = tvOrder.getRefundStatus();
								if(orderStatus == 32000)
									{
										status = "2";
									}
								tvOrder.setStatus(status);
								tvOrder.setRefundStatus(refundStatus);

								if(this.SaveNewOrder(tvOrder))
								{		
									UpdateMessageProcessStatus(orderNO,orderStatusStr,timestamp);
									if(status.equals("2"))//如果是自动接单的，自动调用拣货完成接口
									{
										try //自动调用拣货完成接口
										{
											HelpTools.writelog_fileName("【新订单已接单状态下自动调用拣货完成接口】开始！单号orderNO="+orderNO,jddjLogFileName);
											StringBuilder errorMeassge = new StringBuilder();
											boolean delivery_result =	HelpJDDJHttpUtil.OrderJDZBDelivery(orderNO,"jddjjob", errorMeassge);
											HelpTools.writelog_fileName("【新订单已接单状态下自动调用拣货完成接口】完成！单号orderNO="+orderNO+" 返回结果："+delivery_result,jddjLogFileName);
										} 
										catch (Exception e) 
										{

											HelpTools.writelog_fileName("【新订单已接单已接单状态下自动调用拣货完成接口】异常："+e.getMessage()+" 单号orderNO="+orderNO,jddjLogFileName);
										}

									}


								}

							}
							else if (orderStatus == 20020 || orderStatus == 20040 || orderStatus == 20060) //订单取消
							{
								String status = "12";
								String refundStatus = "6";
								if(this.UpdateOrder(orderNO, status, refundStatus,""))
								{					  
									UpdateMessageProcessStatus(orderNO,orderStatusStr,timestamp);
								}

							}
							else if (orderStatus == 20030) //订单申请取消
							{
								String status = "2";
								String refundStatus = "2";
								if(this.UpdateOrder(orderNO, status, refundStatus,remark))
								{					  
									UpdateMessageProcessStatus(orderNO,orderStatusStr,timestamp);
								}

							}
							else if (orderStatus == 33060 || orderStatus == 34000 || orderStatus == 90000) 
							{
								String status = "11";
								String refundStatus = "1";
								if(this.UpdateOrder(orderNO, status, refundStatus,""))
								{					  
									UpdateMessageProcessStatus(orderNO,orderStatusStr,timestamp);
								}

							}
							else if (orderStatus == 330901) //(330901:订单支付完成应结;330902:订单调整后应结;330903:订单众包配送转自送后应结;)
							{
								//获取订单的店铺实际收入，更新订单中心数据库
								if(this.UpdateOrderSettlementAmount(orderNO))
								{
									UpdateMessageProcessStatus(orderNO,orderStatusStr,timestamp);
								}


							}
							else //其他状态直接更新
							{		
								HelpTools.writelog_fileName("【其他订单状态直接更新已处理】单号orderNO="+orderNO+" 订单状态statusID="+orderStatus,jddjLogFileName);
								UpdateMessageProcessStatus(orderNO,orderStatusStr,timestamp);								  				
							}

						} 
						catch (Exception e) 
						{
							HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】异常："+e.getMessage(),jddjLogFileName);
							continue;			
						}

					}

				}
				else 
				{
					//
					sReturnInfo="无符合要求的数据！";
					HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】没有需要处理的订单消息！",jddjLogFileName);
					logger.info("\r\n******JDDJOrderGet没有需要获取的订单ID******\r\n");
				}

			} 
			catch (Exception e) 
			{
				logger.error("\r\n******JDDJOrderGet获取订单报错信息" + e.getMessage() + "******\r\n");
				HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】获取订单消息异常："+e.getMessage(),jddjLogFileName);
				sReturnInfo="错误信息:" + e.getMessage();

			}


		} 
		catch (Exception e) 
		{
			logger.error("\r\n***************JDDJOrderGet获取订单异常"+e.getMessage()+"****************\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;//
		}

		logger.info("\r\n***************JDDJOrderGet同步END****************\r\n");

		return sReturnInfo;
	}

	protected String getQuerySql()
	{
		String sql = "select * from OC_order_message where messagetype='1' and process_status='N' order by timestamp";

		return sql;
	}


	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);

	}

	private boolean SaveNewOrder(DCP_OrderQueryRes.level1Elm tvOrder) throws Exception
	{
		boolean result = false;
		try 
		{
			ParseJson pj = new ParseJson();
			String req_str = pj.beanToJson(tvOrder);
			JSONObject req_obj = new JSONObject(req_str);

			String eId = tvOrder.geteId();
			String shopId = tvOrder.getShopId();
			String orderno = tvOrder.getOrderNO();
			String status = tvOrder.getStatus();

			String redis_key = "WMORDER" + "_" + eId + "_" + shopId;								
			String hash_key = orderno;	

			//先写缓存
			SaveOrderRedis(redis_key, hash_key, req_str);

			//region 保存数据库 
			try 
			{
				HelpTools.writelog_fileName("【JDDJ开始保存数据库】 订单号orderNO="+orderno+" 订单状态status="+status,jddjLogFileName);	
				ArrayList<DataProcessBean> DPB = HelpTools.GetInsertOrder(req_obj);
				this.doExecuteDataToDB(DPB);
				result = true;		
			} 
			catch (SQLException e) 
			{
				HelpTools.writelog_fileName("【JDDJ保存数据库执行语句】异常："+e.getMessage()+" 订单号orderNO="+orderno+" 订单状态status="+status,jddjLogFileName);	
			}
			catch (Exception e) 
			{
				HelpTools.writelog_fileName("【JDDJ保存数据库】失败："+e.getMessage()+" 订单号orderNO="+orderno+" 订单状态status="+status,jddjLogFileName);	

			}		  		  	  	
			//endregion

			//region 写日志
			try
			{			
				DCP_OrderStatusLogCreateReq req_log = new DCP_OrderStatusLogCreateReq();
				req_log.setDatas(new ArrayList<DCP_OrderStatusLogCreateReq.level1Elm>());

				//region订单状态
				DCP_OrderStatusLogCreateReq req = new DCP_OrderStatusLogCreateReq();
				DCP_OrderStatusLogCreateReq.level1Elm onelv1 = req.new level1Elm();

				onelv1.setCallback_status("0");
				onelv1.setLoadDocType("8");

				onelv1.setNeed_callback("Y");
				onelv1.setNeed_notify("N");
				onelv1.setoEId(eId);



				String o_opName = "京东到家";

				onelv1.setO_opName(o_opName);
				onelv1.setO_opNO("");
				String oShopId = shopId;
				onelv1.setO_organizationNO(oShopId);
				onelv1.setoShopId(oShopId);
				onelv1.setOrderNO(orderno);
				String statusType = "1";
				String updateStaus = status;

				onelv1.setStatusType(statusType);				 					
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				String memo = "";
				memo += statusTypeName+"-->" + statusName;
				onelv1.setMemo(memo);

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);


				req_log.getDatas().add(onelv1);
				onelv1 = null;

				String req_log_json ="";
				try
				{				  	 
					req_log_json = pj.beanToJson(req_log);
				}
				catch(Exception e)
				{

				}			   			   			  	
				StringBuilder errorMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, req_log_json, errorMessage);
				if(nRet)
				{		  		 
					HelpTools.writelog_waimai("【写表OC_orderStatuslog保存成功】"+" 订单号orderNO:"+orderno);
				}
				else
				{			  		 
					HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+orderno);
				}

				//endregion

			}
			catch (Exception  e)
			{

			}
			//endregion
			
			pj=null;
		} 
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【JDDJ开始插入数据】异常："+e.getMessage(),jddjLogFileName);	
		}

		return result;

	}

	private boolean UpdateOrder(String orderNO,String orderSataus,String refundStatus,String remark) throws Exception
	{
		boolean result = false;
		try 
		{
			String orderDBJson = GetOrderInfo(" ", " ", " ", orderNO, "8");
			if (orderDBJson != null && orderDBJson.isEmpty() == false && orderDBJson.length() > 0)
			{
				JSONObject objDB = new JSONObject(orderDBJson);

				String eId = objDB.get("eId").toString();
				String shopId = objDB.get("shopId").toString();
				String dbStatus =  objDB.get("status").toString();//数据库里面订单状态
				String dbrefundStatus =  objDB.get("refundStatus").toString();
				int dbStatus_i = Integer.parseInt(dbStatus);
				int dbrefundStatus_i = Integer.parseInt(dbrefundStatus);

				int orderSataus_i = Integer.parseInt(orderSataus);
				int refundStatus_i = Integer.parseInt(refundStatus);

				boolean isNeedUpdate = false;//是否需要更新缓存和数据库

				if (dbStatus_i < orderSataus_i)//对比下数据库里面状态
				{
					objDB.put("status", orderSataus);
					isNeedUpdate = true;
				}
				else
				{
					orderSataus = dbStatus;
				}

				if (dbrefundStatus_i < refundStatus_i)//对比下数据库里面状态
				{
					objDB.put("refundStatus", refundStatus);
					isNeedUpdate = true;
				}
				else
				{
					refundStatus = dbrefundStatus;
				}
				String reason = remark;
				if (reason != null && reason.length() > 255) 
				{				
					reason = reason.substring(0, 255);
				}

				objDB.put("refundReason", remark);		
				String req_str = objDB.toString();

				String redis_key = "WMORDER" + "_" + eId + "_" + shopId;								
				String hash_key = orderNO;	

				//先写缓存
				SaveOrderRedis(redis_key, hash_key, req_str);


				if(isNeedUpdate ==false)//不用更新数据库 直接返回
				{
					HelpTools.writelog_fileName("【JDDJ不用更新数据库】与数据库状态一致： 订单号orderNO="+orderNO+" 订单状态status="+orderSataus+" 退单状态refundStatus="+refundStatus,jddjLogFileName);	
					return true;
				}

				//region 更新数据库数据库 
				try 
				{				
					HelpTools.writelog_fileName("【JDDJ更新STATUS开始】 "+" 订单号orderNO:"+orderNO+" 订单状态status="+orderSataus+" 退单状态refundStatus="+refundStatus,jddjLogFileName);
					UptBean ub1 = null;	
					ub1 = new UptBean("OC_ORDER");
					ub1.addUpdateValue("STATUS", new DataValue(orderSataus,Types.VARCHAR));
					ub1.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus,Types.VARCHAR));
					if(refundStatus.equals("2")||refundStatus.equals("7"))
					{
						ub1.addUpdateValue("REFUNDREASON", new DataValue(reason,Types.VARCHAR));
					}

					ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					ub1.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
					ub1.addCondition("LOAD_DOCTYPE", new DataValue("8", Types.VARCHAR));							
					ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
					DPB.add(new DataProcessBean(ub1));		
					this.doExecuteDataToDB(DPB);	
					result = true;	
					HelpTools.writelog_fileName("【JDDJ更新STATUS成功】 "+" 订单号orderNO:"+orderNO+" 订单状态status="+orderSataus+" 退单状态refundStatus="+refundStatus,jddjLogFileName);
				} 
				catch (SQLException e) 
				{
					HelpTools.writelog_fileName("【JDDJ更新数据执行语句】异常："+e.getMessage()+" 订单号orderNO="+orderNO+" 订单状态status="+orderSataus,jddjLogFileName);	
				}
				catch (Exception e) 
				{
					HelpTools.writelog_fileName("【JDDJ更新数据库】失败："+e.getMessage()+" 订单号orderNO="+orderNO+" 订单状态status="+orderSataus,jddjLogFileName);			
				}		  		  	  	
				//endregion

				//region 写日志
				try
				{			
					DCP_OrderStatusLogCreateReq req_log = new DCP_OrderStatusLogCreateReq();
					req_log.setDatas(new ArrayList<DCP_OrderStatusLogCreateReq.level1Elm>());

					//region订单状态
					DCP_OrderStatusLogCreateReq req = new DCP_OrderStatusLogCreateReq();
					DCP_OrderStatusLogCreateReq.level1Elm onelv1 = req.new level1Elm();
					onelv1.setCallback_status("0");
					onelv1.setLoadDocType("8");

					onelv1.setNeed_callback("N");
					onelv1.setNeed_notify("N");
					onelv1.setoEId(eId);



					String o_opName = "京东到家";

					onelv1.setO_opName(o_opName);
					onelv1.setO_opNO("");
					String oShopId = shopId;
					onelv1.setO_organizationNO(oShopId);
					onelv1.setoShopId(oShopId);
					onelv1.setOrderNO(orderNO);
					String statusType = "1";
					String updateStaus = orderSataus;
					if(refundStatus.equals("1"))//没有申请退单,写订单状态
					{
						statusType = "1";//订单状态
						updateStaus = orderSataus;
					}
					else
					{
						statusType = "3";//退单状态
						updateStaus = refundStatus;
					}

					onelv1.setStatusType(statusType);				 					
					onelv1.setStatus(updateStaus);
					StringBuilder statusTypeNameObj = new StringBuilder();
					String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
					String statusTypeName = statusTypeNameObj.toString();
					onelv1.setStatusTypeName(statusTypeName);
					onelv1.setStatusName(statusName);

					String memo = "";
					memo += statusTypeName+"-->" + statusName;
					onelv1.setMemo(memo);

					String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv1.setUpdate_time(updateDatetime);


					req_log.getDatas().add(onelv1);
					onelv1=null ; 

					String req_log_json ="";
					try
					{			
						ParseJson pj = new ParseJson();
						req_log_json = pj.beanToJson(req_log);
						pj=null;
					}
					catch(Exception e)
					{

					}			   			   			  	
					StringBuilder errorMessage = new StringBuilder();
					boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, req_log_json, errorMessage);
					if(nRet)
					{		  		 
						HelpTools.writelog_waimai("【写表OC_orderStatuslog保存成功】"+" 订单号orderNO:"+orderNO);
					}
					else
					{			  		 
						HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+orderNO);
					}

					//endregion

				}
				catch (Exception  e)
				{

				}
				//endregion

			}


		} 
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【JDDJ更新订单】 异常："+e.getMessage()+" 订单号orderNO:"+orderNO+" 订单状态status="+orderSataus+" 退单状态refundStatus="+refundStatus,jddjLogFileName);		
		}

		return result;

	}

	private boolean UpdateOrderSettlementAmount(String orderNO) throws Exception
	{
		boolean result = false;
		try 
		{
			String orderDBJson = GetOrderInfo(" ", " ", " ", orderNO, "8");
			if (orderDBJson != null && orderDBJson.isEmpty() == false && orderDBJson.length() > 0)
			{
				JSONObject objDB = new JSONObject(orderDBJson);

				String eId = objDB.get("eId").toString();
				String shopId = objDB.get("shopId").toString();
				String dbStatus =  objDB.get("status").toString();//数据库里面订单状态
				String dbrefundStatus =  objDB.get("refundStatus").toString();
				String serviceCharge_str =  objDB.get("serviceCharge").toString();//平台服务费
				String incomeAMT_str =  objDB.get("incomeAmt").toString();//店铺实收
				double serviceCharge = 0;
				double incomeAMT = 0;
				try 
				{
					serviceCharge = Double.parseDouble(serviceCharge_str);

				} 
				catch (Exception e) 
				{

				}

				try 
				{
					incomeAMT = Double.parseDouble(incomeAMT_str);		
				} 
				catch (Exception e) 
				{

				}


				//调用订单应结接口
				StringBuilder error = new StringBuilder();
				OrderShoudSettlementAmount orderSettlementAmount = HelpJDDJHttpUtil.orderShoudSettlementService(orderNO,error);
				if(orderSettlementAmount==null)
				{
					if(dbStatus.equals("12"))//已经退单返回的可能没有
					{
						return true;
					}
					else
					{
						return false;
					}


				}



				double incomeAMT_new = (double) orderSettlementAmount.getSettlementAmount()/100;					
				double diff = incomeAMT - incomeAMT_new;//	

				//佣金=货款佣金（包含到家补贴佣金)+餐盒费佣金+运费佣金+保底佣金补差
				int totalCommission = orderSettlementAmount.getGoodsCommission() + orderSettlementAmount.getPackageCommission()
				+ orderSettlementAmount.getFreightCommission() + orderSettlementAmount.getGuaranteedCommission();				
				double totalCommission_d = (double)totalCommission/100;//平台服务费

				serviceCharge = totalCommission_d;

				if(incomeAMT_new<=0)
					return false;
				//region 更新数据库数据库 
				try 
				{				
					HelpTools.writelog_fileName("【JDDJ更新店铺实收INCOMEAMT开始】 "+" 订单号orderNO:"+orderNO+" 订单状态status="+dbStatus+" 退单状态refundStatus="+dbrefundStatus+" 订单原INCOMEAMT="+incomeAMT_str+" 修改后INCOMEAMT="+incomeAMT_new+" 平台服务费SERVICECHARGE="+serviceCharge,jddjLogFileName);
					UptBean ub1 = null;	
					ub1 = new UptBean("OC_ORDER");	

					ub1.addUpdateValue("INCOMEAMT", new DataValue(incomeAMT_new,Types.VARCHAR));
					ub1.addUpdateValue("SERVICECHARGE", new DataValue(serviceCharge,Types.VARCHAR));
					ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					ub1.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
					ub1.addCondition("LOAD_DOCTYPE", new DataValue("8", Types.VARCHAR));							
					ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
					DPB.add(new DataProcessBean(ub1));		
					this.doExecuteDataToDB(DPB);	
					result = true;	
					HelpTools.writelog_fileName("【JDDJ更新店铺实收成功】 "+" 订单号orderNO:"+orderNO+" 订单状态status="+dbStatus+" 退单状态refundStatus="+dbrefundStatus+" 订单原INCOMEAMT="+incomeAMT_str+" 修改后INCOMEAMT="+incomeAMT_new,jddjLogFileName);
				} 
				catch (SQLException e) 
				{
					HelpTools.writelog_fileName("【JDDJ更新数据执行语句】异常："+e.getMessage()+" 订单号orderNO="+orderNO,jddjLogFileName);	
				}
				catch (Exception e) 
				{
					HelpTools.writelog_fileName("【JDDJ更新数据库】失败："+e.getMessage()+" 订单号orderNO="+orderNO,jddjLogFileName);			
				}		  		  	  	
				//endregion

				//region 写日志
				try
				{			
					DCP_OrderStatusLogCreateReq req_log = new DCP_OrderStatusLogCreateReq();
					req_log.setDatas(new ArrayList<DCP_OrderStatusLogCreateReq.level1Elm>());

					//region订单状态
					DCP_OrderStatusLogCreateReq req = new DCP_OrderStatusLogCreateReq();
					DCP_OrderStatusLogCreateReq.level1Elm onelv1 = req.new level1Elm();
					onelv1.setCallback_status("0");
					onelv1.setLoadDocType("8");

					onelv1.setNeed_callback("N");
					onelv1.setNeed_notify("N");
					onelv1.setoEId(eId);



					String o_opName = "京东到家";

					onelv1.setO_opName(o_opName);
					onelv1.setO_opNO("");
					String oShopId = shopId;
					onelv1.setO_organizationNO(oShopId);
					onelv1.setoShopId(oShopId);
					onelv1.setOrderNO(orderNO);
					String statusType = "4";
					String updateStaus = "1";				 
					onelv1.setStatusType(statusType);				 					
					onelv1.setStatus(updateStaus);
					StringBuilder statusTypeNameObj = new StringBuilder();
					String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
					String statusTypeName = statusTypeNameObj.toString();
					onelv1.setStatusTypeName(statusTypeName);
					onelv1.setStatusName(statusName);

					String memo = "";
					memo += statusTypeName+"-->" + statusName+" 店铺实收："+incomeAMT_str+"-->"+incomeAMT_new+" 佣金："+totalCommission_d;
					onelv1.setMemo(memo);

					String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv1.setUpdate_time(updateDatetime);


					req_log.getDatas().add(onelv1);
					onelv1=null;

					String req_log_json ="";
					try
					{			
						ParseJson pj = new ParseJson();
						req_log_json = pj.beanToJson(req_log);
						pj=null;
					}
					catch(Exception e)
					{

					}			   			   			  	
					StringBuilder errorMessage = new StringBuilder();
					boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, req_log_json, errorMessage);
					if(nRet)
					{		  		 
						HelpTools.writelog_waimai("【写表OC_orderStatuslog保存成功】"+" 订单号orderNO:"+orderNO);
					}
					else
					{			  		 
						HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+orderNO);
					}

					//endregion

				}
				catch (Exception  e)
				{

				}
				//endregion

			}


		} 
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【JDDJ更新店铺实收】 异常："+e.getMessage()+" 订单号orderNO:"+orderNO,jddjLogFileName);		
		}

		return result;

	}



	private String GetOrderInfo(String eId,String organizationno,String shopId,String orderNO,String loadDocType) throws Exception
	{
		try 
		{

			String sql = "select * from ( select A.*,B.Item,B.Pluno,B.Plubarcode,B.Pluname,B.SPECNAME,B.ATTRNAME,B.UNIT,B.PRICE,B.QTY,B.Goodsgroup,B.Disc,B.Boxnum,B.BOXPRICE,B.AMT AS DETAILAMT,B.ISMEMO,B.SKUID	";
			sql += " from OC_order A inner join OC_order_detail B on A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND A.SHOPID=B.SHOPID AND A.Orderno=B.Orderno ";
			sql += " where A.Load_Doctype='"+loadDocType+"' and A.ORDERNO='"+orderNO+"'";
			sql += " order by A.Orderno,B.ITEM)";

			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{			
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("ORDERNO", true);	
				condition.put("SHOPID", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
				//单身
				condition.put("ITEM", true);

				List<Map<String, Object>> getOrderGoodsDetail=MapDistinct.getMap(getQDataDetail, condition);

				DCP_OrderQueryRes res = new DCP_OrderQueryRes();
				DCP_OrderQueryRes.level1Elm oneLv1 = res.new level1Elm();
				for (Map<String, Object> oneData : getQHeader) 
				{							
					oneLv1.setGoods(new ArrayList<DCP_OrderQueryRes.level2Elm>());			
					String orderNO_1 = oneData.get("ORDERNO").toString();
					String rShopId = oneData.get("SHOPID").toString();
					oneLv1.setAddress(oneData.get("ADDRESS").toString());
					oneLv1.seteId(oneData.get("EID").toString());
					oneLv1.setContMan(oneData.get("CONTMAN").toString());
					oneLv1.setContTel(oneData.get("CONTTEL").toString());
					oneLv1.setCreateDatetime(oneData.get("CREATE_DATETIME").toString());
					oneLv1.setCustomerNO(oneData.get("CUSTOMERNO").toString());
					oneLv1.setIncomeAmt(oneData.get("INCOMEAMT").toString());
					oneLv1.setInvoiceTitle(oneData.get("INVOICETITLE").toString());
					oneLv1.setInvoiceType(oneData.get("INVOICETYPE").toString());
					oneLv1.setIsBook(oneData.get("ISBOOK").toString());
					oneLv1.setIsInvoice(oneData.get("ISINVOICE").toString());
					oneLv1.setIsShipcompany(oneData.get("ISSHIPCOMPANY").toString());
					oneLv1.setLoadDocType(oneData.get("LOAD_DOCTYPE").toString());
					oneLv1.setMachShopName(oneData.get("MACHSHOPNAME").toString());
					oneLv1.setMachShopNO(oneData.get("MACHSHOP").toString());
					oneLv1.setMemo(oneData.get("MEMO").toString());
					oneLv1.setOrderNO(orderNO);
					oneLv1.setOrganizationNO(oneData.get("ORGANIZATIONNO").toString());
					oneLv1.setPackageFee(oneData.get("PACKAGEFEE").toString());
					oneLv1.setPayAmt(oneData.get("PAYAMT").toString());
					oneLv1.setPayStatus(oneData.get("PAYSTATUS").toString());
					oneLv1.setPlatformDisc(oneData.get("PLATFORM_DISC").toString());
					oneLv1.setsDate(oneData.get("SDATE").toString());
					oneLv1.setSellerDisc(oneData.get("SELLER_DISC").toString());
					oneLv1.setServiceCharge(oneData.get("SERVICECHARGE").toString());
					oneLv1.setShipDate(oneData.get("SHIPDATE").toString());
					oneLv1.setShipFee(oneData.get("SHIPFEE").toString());
					oneLv1.setShippingShopName(oneData.get("SHIPPINGSHOPNAME").toString());
					oneLv1.setShippingShopNO(oneData.get("SHIPPINGSHOP").toString());
					oneLv1.setShipTime(oneData.get("SHIPTIME").toString());
					oneLv1.setShipType(oneData.get("SHIPTYPE").toString());
					oneLv1.setShopName(oneData.get("SHOPNAME").toString());
					oneLv1.setShopId(rShopId);
					oneLv1.setSn(oneData.get("ORDER_SN").toString());
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setRefundStatus(oneData.get("REFUNDSTATUS").toString());
					oneLv1.setsTime(oneData.get("STIME").toString());
					oneLv1.setTaxRegnumber(oneData.get("TAXREGNUMBER").toString());
					oneLv1.setTot_Amt(oneData.get("TOT_AMT").toString());
					oneLv1.setTot_oldAmt(oneData.get("TOT_OLDAMT").toString());
					oneLv1.setTotDisc(oneData.get("TOT_DISC").toString());
					oneLv1.setShopShareDeliveryFee(oneData.get("SHOPSHARESHIPFEE").toString());

					String PartRefundAmt = oneData.get("PARTREFUNDAMT").toString();

					if(PartRefundAmt==null||PartRefundAmt.isEmpty()||PartRefundAmt.trim().length()==0)
					{
						PartRefundAmt = "0";
					}

					oneLv1.setPartRefundAmt(PartRefundAmt);//暂时数据库不存该字段 			
					oneLv1.setMealNumber(oneData.get("MEALNUMBER").toString());
					oneLv1.setManualNO(oneData.get("MANUALNO").toString());
					oneLv1.setGetMan(oneData.get("GETMAN").toString());
					oneLv1.setGetMantel(oneData.get("GETMANTEL").toString());
					oneLv1.setCardNO(oneData.get("CARDNO").toString());
					oneLv1.setSellNO(oneData.get("SELLNO").toString());
					oneLv1.setPointQty(oneData.get("POINTQTY").toString());

					for (Map<String, Object> oneData_detail : getOrderGoodsDetail) 
					{

						DCP_OrderQueryRes.level2Elm oneLv2 = res.new level2Elm();

						oneLv2.setMessages(new ArrayList<DCP_OrderQueryRes.level3Memo>());
						String orderNO_detail = oneData_detail.get("ORDERNO").toString();
						String shopNO_detail = oneData_detail.get("SHOPID").toString();
						String item = oneData_detail.get("ITEM").toString();
						if(orderNO.equals(orderNO_detail)&&rShopId.equals(shopNO_detail))
						{											
							oneLv2.setAmt(oneData_detail.get("DETAILAMT").toString());
							oneLv2.setAttrName(oneData_detail.get("ATTRNAME").toString());
							oneLv2.setBoxNum(oneData_detail.get("BOXNUM").toString());
							oneLv2.setBoxPrice(oneData_detail.get("BOXPRICE").toString());
							oneLv2.setDisc(oneData_detail.get("DISC").toString());
							oneLv2.setGoodsGroup(oneData_detail.get("GOODSGROUP").toString());
							oneLv2.setIsMemo(oneData_detail.get("ISMEMO").toString());
							oneLv2.setItem(oneData_detail.get("ITEM").toString());
							oneLv2.setPluBarcode(oneData_detail.get("PLUBARCODE").toString());
							oneLv2.setPluName(oneData_detail.get("PLUNAME").toString());
							oneLv2.setPluNO(oneData_detail.get("PLUNO").toString());
							oneLv2.setPrice(oneData_detail.get("PRICE").toString());
							oneLv2.setQty(oneData_detail.get("QTY").toString());
							oneLv2.setSpecName(oneData_detail.get("SPECNAME").toString());
							oneLv2.setUnit(oneData_detail.get("UNIT").toString());		
							oneLv2.setSkuID(oneData_detail.get("SKUID").toString());

							oneLv1.getGoods().add(oneLv2);								
						}		

					}

					break;

				}

				ParseJson pj = new ParseJson();
				String	ordermap =pj.beanToJson(oneLv1);
				pj=null;
				oneLv1 = null;
				HelpTools.writelog_fileName("【JDDJ查询订单更新状态】查询数据库返回内容："+ordermap,jddjLogFileName);
				return ordermap;

			}
			else
			{
				return "";
			}

		} 

		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【JDDJ查询订单】orderNO="+orderNO+"查询数据库异常："+e.getMessage(),jddjLogFileName);
			return "";
		}

	}

	private void UpdateMessageProcessStatus(String billID,String statusID,String timestamp) throws Exception
	{
		Map<String, DataValue> values = new HashMap<String, DataValue>();
		DataValue v = new DataValue("Y", Types.VARCHAR);
		values.put("process_status", v);
		DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
		values.put("UPDATE_TIME", v1);

		// condition
		Map<String, DataValue> conditions = new HashMap<String, DataValue>();
		DataValue c1 = new DataValue(billID, Types.VARCHAR);
		conditions.put("BILLID", c1);
		DataValue c2 = new DataValue(statusID, Types.VARCHAR);
		conditions.put("STATUSID", c2);
		DataValue c3 = new DataValue(timestamp, Types.VARCHAR);
		conditions.put("TIMESTAMP", c3);									
		this.doUpdate("OC_order_message", values, conditions);
	}

	private void SaveOrderRedis(String redis_key,String hash_key,String hash_value) throws Exception
	{
		//region 先写缓存
		try 
		{	
			HelpTools.writelog_fileName("【JDDJ开始写缓存】"+"redis_key:"+redis_key+" hash_key:"+hash_key+" hash_value:"+hash_value,jddjLogFileName);	  	
			RedisPosPub redis = new RedisPosPub();
			boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
			if (isexistHashkey) {

				redis.DeleteHkey(redis_key, hash_key);//
				HelpTools.writelog_fileName("【JDDJ删除存在hash_key的缓存】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key,jddjLogFileName);
			}
			boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
			if (nret) {
				HelpTools.writelog_fileName("【JDDJ写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key,jddjLogFileName);
			} else {
				HelpTools.writelog_fileName("【JDDJ写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key,jddjLogFileName);
			}
			redis.Close();
		} 
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【JDDJ写缓存】Exception:"+e.getMessage()+" redis_key:"+redis_key+" hash_key:"+hash_key,jddjLogFileName);	
		}
		//endregion

	}
*/}
