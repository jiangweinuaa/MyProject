package com.dsc.spos.waimai;

import com.dsc.spos.dao.*;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderGoodsItem;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class WMSGService extends SWaimaiBasicService {

	private Map<String, Object> req = null;

	String messageType ="";

	public WMSGService()//默认，防止更新兼容
	{
		this.messageType = "1";
	}

	public WMSGService(String messType)
	{
		this.messageType = messType;
	}

	@Override
	public String execute(String json) throws Exception {
		String res_json = null;
		if(messageType.equals("1"))//订单推送消息
		{
			res_json = HelpTools.GetSGMTResponse(json);
		}
		else if (messageType.equals("2")) //取消类型消息
		{
			res_json = HelpTools.GetSGMTCancelResponse(json);
		}
		else if (messageType.equals("3")) //退单类型消息
		{
			res_json = HelpTools.GetSGMTRefundResponse(json);
		}
		else 
		{

		}

		if(res_json ==null ||res_json.length()==0)
		{
			return null;
		}
		Map<String, Object> res = new HashMap<String, Object>();
		this.processDUID(res_json, res);

		return null;
	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception 
	{		
		try 
		{
			//JSONObject obj = new JSONObject(req);
			ParseJson pj = new ParseJson();
			order dcpOrder = pj.jsonToBean(req, new TypeToken<order>(){});
			String orderstatus = dcpOrder.getStatus();// 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单	
			String orderNo = dcpOrder.getOrderNo();			
			String refundStatus = dcpOrder.getRefundStatus();			
			String eId = dcpOrder.geteId();		
			String shopNo = dcpOrder.getShopNo();			
			String loadDocType = dcpOrder.getLoadDocType();
			String channelId = dcpOrder.getChannelId();
			if(orderstatus !=null)
			{
				if(orderstatus.equals("1"))//插入
				{
					StringBuffer errorMessage = new StringBuffer();
					List<order> orderList = new ArrayList<order>();					
					orderList.add(dcpOrder);
					
					ArrayList<DataProcessBean> DPB = HelpTools.GetInsertOrderCreat(orderList, errorMessage,null);					
					if (DPB != null && DPB.size() > 0)
					{
						for (DataProcessBean dataProcessBean : DPB) 
						{
							this.addProcessData(dataProcessBean);			
						}					
						//this.doExecuteDataToDB();
						//HelpTools.writelog_waimai("【JBP保存数据库成功】"+" 订单号orderNO:"+orderNO);

						try 
						{
							this.doExecuteDataToDB();
							HelpTools.writelog_waimai("【MT保存数据库成功】"+" 订单号orderNo:"+orderNo);
							//商品资料异常
							HelpTools.waimaiOrderAbnormalSave(dcpOrder, errorMessage);

						} 
						catch (Exception e) 
						{
							this.pData.clear();						
						}

						//订单追踪  1、订单创建时间  2、订单接单时间  3、订单拒单时间  4、生产接单时间  5、完工入库时间  6、订单调拨时间  7、订单配送时间  8、订单完成时间 9、订单退订时间
						//InsertOrderTrack(obj.get("companyNO").toString(),obj.get("shopNO").toString(),orderNO,"2","1");
						//region 写订单日志
						// 写订单日志
						List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
						orderStatusLog onelv1 = new orderStatusLog();
						onelv1.setLoadDocType(loadDocType);
						onelv1.setChannelId(dcpOrder.getChannelId());
						onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
						onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
						onelv1.seteId(eId);
						String opNO = "";
						String o_opName = "美团用户";
						
						

						onelv1.setOpName(o_opName);
						onelv1.setOpNo(opNO);				
						onelv1.setShopNo(shopNo);
						onelv1.setOrderNo(orderNo);
						onelv1.setMachShopNo(dcpOrder.getMachShopNo());
						onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
						String statusType = "";
						String updateStaus = orderstatus;
						statusType = "1";// 订单状态				
						onelv1.setStatusType(statusType);
						onelv1.setStatus(updateStaus);
						StringBuilder statusTypeNameObj = new StringBuilder();
						String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
						String statusTypeName = statusTypeNameObj.toString();
						onelv1.setStatusTypeName(statusTypeName);
						onelv1.setStatusName(statusName);

						String memo = "";
						memo += statusName;
						onelv1.setMemo(memo);
						onelv1.setDisplay("1");

						String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						onelv1.setUpdate_time(updateDatetime);

						orderStatusLogList.add(onelv1);
						
						StringBuilder errorStatusLogMessage = new StringBuilder();
						boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
						if (nRet) {
							HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNo:" + orderNo);
						} else {
							HelpTools.writelog_waimai(
									"【写表tv_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNo:" + orderNo);
						}
						this.pData.clear();
																
						//endregion



					}

				}			
				else//更新 数据库状态
				{
					HelpTools.writelog_waimai("【MT开始更新数据库】"+" 订单号orderNO:"+orderNo + " 订单状态status=" + orderstatus+" 退单状态refundStatus="+refundStatus);
					UpdateOrderStatus(req);
				}

			}


		} 
		catch (SQLException e) 
		{
			this.pData.clear();
			HelpTools.writelog_waimai("【MT执行语句】异常："+e.getMessage()+ "\r\n req请求内容:" + req);		
		}
		catch (Exception e) 
		{
			this.pData.clear();
			HelpTools.writelog_waimai("【MT执行语句】异常："+e.getMessage()+ "\r\n req请求内容:" + req);		
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

	private void UpdateOrderStatus(String req) throws Exception
	{
		try 
		{
			String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			ParseJson pj = new ParseJson();
			order dcpOrder = pj.jsonToBean(req, new TypeToken<order>(){});
			
			String status = dcpOrder.getStatus();// 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单	
			String orderNo = dcpOrder.getOrderNo();			
			String refundStatus = dcpOrder.getRefundStatus();			
			String eId = dcpOrder.geteId();		
			String shopNo = dcpOrder.getShopNo();			
			String loadDocType = dcpOrder.getLoadDocType();
			String channelId = dcpOrder.getChannelId();
				
			double partRefundAmt = dcpOrder.getRefundAmt();
			

			String reason = dcpOrder.getRefundReason();
			

			if (reason != null && reason.length() > 255) 
			{				
				reason = reason.substring(0, 255);
			}
			if (shopNo != null && shopNo.trim().length() > 0)
			{
				if (status.equals("3") || status.equals("12"))
				{
					HelpTools.setWaiMaiOrderToSaleOrRefundRedisLock("1",eId,orderNo);
				}
			}

			Map<String, Object> orderHead = new HashMap<String, Object>();
			boolean IsExistOrder = IsExistOrder(eId, orderNo,orderHead);
			String status_DB ="";
			String refundStatus_DB ="";
			try 
			{
				status_DB =orderHead.get("STATUS").toString();
				refundStatus_DB =orderHead.get("REFUNDSTATUS").toString();	
			} 
			catch (Exception e) 
			{

			}

			if(IsExistOrder)//存在就Update
			{
				if(refundStatus.equals("2")||refundStatus.equals("7"))//申请了退款，或者部分退款
				{
					try 
					{
						/*String status_DB =orderHead.get("STATUS").toString();
						String refundStatus_DB =orderHead.get("REFUNDSTATUS").toString();*/
						if(refundStatus.equals("2"))
						{
							if(refundStatus_DB.equals("6")||refundStatus_DB.equals("3")||refundStatus_DB.equals("5"))
							{
								//已经处理了的退款，删除缓存
								HelpTools.writelog_waimai("【MT退单重复推送退单申请】"+" 订单号orderNo:"+orderNo+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
								String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;							 
								String hash_key = orderNo;
								//DeleteRedis(redis_key,hash_key);
								if(refundStatus_DB.equals("6"))//这时候 不能直接删缓存，否则pos收不到退单消息
								{
									//obj.put("status", "12");
									//obj.put("refundStatus", refundStatus_DB);
									dcpOrder.setStatus("12");
									dcpOrder.setRefundStatus(refundStatus_DB);
									if (dcpOrder.getGoodsList()!=null&&dcpOrder.getGoodsList().isEmpty()==false)
									{
										for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
										{
											goodsItem.setrQty(goodsItem.getQty());
										}
										HelpTools.writelog_waimai("【外卖取消或退单状态时】更新订单商品明细缓存里面rqty");
									}
									String hash_value = pj.beanToJson(dcpOrder);
									WriteRedis(redis_key, hash_key, hash_value);
									
								}
								else
								{
									DeleteRedis(redis_key,hash_key);
								}
								return;
							}

						}
						else
						{
							if(refundStatus_DB.equals("10")||refundStatus_DB.equals("8")||refundStatus_DB.equals("9"))
							{
								//已经处理了的部分退款，删除缓存
								HelpTools.writelog_waimai("【MT部分退单重复推送退单申请】"+" 订单号orderNo:"+orderNo+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
								String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
								String hash_key = orderNo;
								//DeleteRedis(redis_key,hash_key);
								if(refundStatus_DB.equals("10"))//这时候 不能直接删缓存，否则pos收不到退单消息 // 数据库可能还没及时更新，所以没有部分退的商品
								{										
									/*obj.put("refundStatus", refundStatus_DB);
									
									String hash_value = obj.toString();
									WriteRedis(redis_key, hash_key, hash_value);*/
									
								}
								else
								{
									DeleteRedis(redis_key,hash_key);
								}
								return;
							}

						}

					} 
					catch (Exception e) 
					{

					}

				}

				UptBean ub1 = null;	
				ub1 = new UptBean("DCP_ORDER");
				ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));				
				//部分退单，最后还推送已完成订单refundStatus=1，如果数据库已经是退单成功状态,refundStatus就不更新了
				if(refundStatus_DB!=null&&refundStatus_DB.equals("10"))//
				{

				}
				else
				{
					if(refundStatus.equals("7"))//如果数据库已经是部分退单成功的状态，不能更新 申请部分退单的状态
					{
						ub1.addCondition("REFUNDSTATUS", new DataValue("10", Types.VARCHAR,DataExpression.NE));
					}
					
					ub1.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus,Types.VARCHAR));
				}


				if(refundStatus.equals("2")||refundStatus.equals("7"))
				{
					HelpTools.writelog_fileName("【MT】申请退款更新数据，单号orderNo="+orderNo+" 退货原因refundReason="+reason, "refunReasonLog");
					HelpTools.writelog_waimai("【MT】申请退款更新数据，单号orderNo="+orderNo+" 退货原因refundReason="+reason);
					ub1.addUpdateValue("REFUNDREASON", new DataValue(reason,Types.VARCHAR));
				}

				if(refundStatus.equals("10"))
				{
					ub1.addUpdateValue("REFUNDAMT", new DataValue(partRefundAmt,Types.VARCHAR));
					ub1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(partRefundAmt,Types.VARCHAR));
					ub1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(partRefundAmt,Types.VARCHAR));
					ub1.addUpdateValue("REFUNDREASON", new DataValue(reason,Types.VARCHAR));
					ub1.addUpdateValue("LASTREFUNDTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				}			
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				if(status.equals("11")||status.equals("3")||status.equals("12"))
				{
					ub1.addUpdateValue("COMPLETE_DATETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					if(status.equals("3")||status.equals("12"))
					{
						ub1.addUpdateValue("LASTREFUNDTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
						if (dcpOrder.getGoodsList()!=null&&dcpOrder.getGoodsList().isEmpty()==false)
						{
							for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
							{
								goodsItem.setrQty(goodsItem.getQty());
							}
							HelpTools.writelog_waimai("【外卖取消或退单状态时】更新订单商品明细缓存里面rqty");
						}
					}
				}
				
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));				
				ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				
				/*if(status.equals("12")==false)//如果不是已退单的状态，不能更新 已退单的状态
				{
					ub1.addCondition("STATUS", new DataValue("12", Types.VARCHAR,DataExpression.NE));
				}*/
				
				
				
				this.addProcessData(new DataProcessBean(ub1));

				this.doExecuteDataToDB();	
				HelpTools.writelog_waimai("【MT更新STATUS成功】"+" 订单号orderNO:"+orderNo+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
				
				if(status.equals("12"))
				{
					//强制写一次缓存
					
					String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
					String hash_key = orderNo;
					String hash_value = pj.beanToJson(dcpOrder);
					HelpTools.writelog_waimai("【整单退单成功强制写一次缓存】"+" redis_key:"+redis_key+" hash_key:"+hash_key);
					WriteRedis(redis_key, hash_key, hash_value);
				}
				
				//同意部分退款后，再插入退款的明细商品 不用放在同一个事务里面
				if(refundStatus.equals("10"))
				{
					HelpTools.writelog_waimai("【MT部分退单新增单身开始】"+" 订单号orderNo:"+orderNo+" 订单状态status="+status);
					boolean isExist = false;
					String curDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					//获取下最大的单身item+1 由于之前已经查询了数据库了，
					//int maxPartRefundGoodsItem = GetMaxOrderDetailItem(companyNO, organizationNO, shopNO, orderNO,loadDocType);
					try 
					{
						
						StringBuffer error_partRefundGoods = new StringBuffer();
						List<orderGoodsItem> goodsList_partRefund = HelpTools.waimaiPartRefundGoodsProcess(dcpOrder, error_partRefundGoods);//真正的转化出来的部分退单单身(包含套餐商品处理)
												
						//更新原单
						for (orderGoodsItem goodsItem : goodsList_partRefund) 
						{
							try 
							{
												
								String oItem = goodsItem.getoItem();//原单对应的商品项次
																
								double qty = goodsItem.getQty();
																
								//先简单处理
								String execsql = "update DCP_ORDER_detail set rqty="+qty+""
										+ " where rownum =1 and eid='"+eId+"' and orderno='"+orderNo+"' and item="+oItem+"  and qty>="+qty;
								
								HelpTools.writelog_waimai("【部分退单成功后】更新原单单身已退数量sql="+execsql+",订单号orderNo="+orderNo+",原单项次item="+oItem);
								ExecBean exBean = new ExecBean(execsql);
								this.addProcessData(new DataProcessBean(exBean));																							
								isExist = true;
										
							} 
							catch (Exception e) 
							{
								HelpTools.writelog_waimai("【开始保存数据库】添加部分退单单身异常："+e.getMessage()+"(eid="+eId+" shopno="+shopNo+" orderNo="+orderNo+")");
								continue;
							}			
						}	
										
						if(isExist)
						{
							this.doExecuteDataToDB();
							HelpTools.writelog_waimai("【MT部分退单单身添加执行语句成功】"+" 订单号orderNo:"+orderNo+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
							StringBuffer error_partRefund = new StringBuffer();
							HelpTools.waimaiOrderPartRefundProcess(dcpOrder, goodsList_partRefund, "", error_partRefund);
						}
						else
						{
							HelpTools.writelog_waimai("【MT部分退单单身为空！】"+" 订单号orderNo:"+orderNo+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
						}
						//强制写一次缓存
											
						String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
						String hash_key = orderNo;
						String hash_value = pj.beanToJson(dcpOrder);
						HelpTools.writelog_waimai("【部分退单成功强制写一次缓存】"+" redis_key:"+redis_key+" hash_key:"+hash_key);
						WriteRedis(redis_key, hash_key, hash_value);

					}
					catch (SQLException e) 
					{
						this.pData.clear();
						HelpTools.writelog_waimai("【MT部分退单新增单身执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
					}
					catch (Exception e) 
					{
						this.pData.clear();
						// TODO: handle exception
						HelpTools.writelog_waimai("【MT部分退单新增单身执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
					}
				}
				
				
				//推送取消或者退单消息时，执行下。
				if(status.equals("3")||status.equals("12"))
				{
					StringBuffer RefundOrCancelError = new StringBuffer();
					HelpTools.OrderRefundOrCancelProcess(dcpOrder, sdate, RefundOrCancelError);
				}
				
				

			}
			else
			{
				HelpTools.writelog_waimai("【MT更新的单据不存在】开始插入到数据库"+" 订单号orderNo:"+orderNo+" 订单状态status="+status);
							
				StringBuffer errorMessage = new StringBuffer();
				List<order> orderList = new ArrayList<order>();					
				orderList.add(dcpOrder);
				ArrayList<DataProcessBean> DPB = HelpTools.GetInsertOrderCreat(orderList, errorMessage,null);
				
				if (DPB != null && DPB.size() > 0)
				{
					try
					{
						for (DataProcessBean dataProcessBean : DPB)
						{
							this.addProcessData(dataProcessBean);
						}
						this.doExecuteDataToDB();
						HelpTools.writelog_waimai("【MT更新的单据不存在】插入数据库成功"+" 订单号orderNo:"+orderNo+" 订单状态status="+status);

						//商品资料异常
						HelpTools.waimaiOrderAbnormalSave(dcpOrder, errorMessage);
					}
					catch ( Exception e)
					{
						this.pData.clear();
						HelpTools.writelog_waimai("【MT更新的单据不存在】插入到数据库异常:"+e.getMessage()+",订单号orderNo:"+orderNo+" 订单状态status="+status);
						//如果插入失败了，可能是在三方外卖APP上开启了自动接单，先推送接单，后推送开立。
						//这时候更新下即可
						if ("2".equals(status))
						{
							UptBean ub1 = null;
							ub1 = new UptBean("DCP_ORDER");
							ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
							ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
							ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

							ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
							ub1.addCondition("STATUS", new DataValue("1",Types.VARCHAR));//只更新开立的时候，其他不能更新
							this.addProcessData(new DataProcessBean(ub1));

							this.doExecuteDataToDB();
							HelpTools.writelog_waimai("【MT更新STATUS成功】"+" 订单号orderNO:"+orderNo+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);

						}

					}

				}

			}


			//region写订单日志
			try
			{						
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				orderStatusLog onelv1 = new orderStatusLog();
				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(dcpOrder.getChannelId());
				onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
				onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
				onelv1.seteId(eId);
				String opNO = "";
				String o_opName = "美团用户";
				
				onelv1.setOpName(o_opName);
				onelv1.setOpNo(opNO);				
				onelv1.setShopNo(shopNo);
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo(dcpOrder.getMachShopNo());
				onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
				String statusType = "";
				String updateStaus = status;
				statusType = "1";// 订单状态				
				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				String memo = "";
				memo += statusName;
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);

				//orderStatusLogList.add(onelv1); //这里需要注意（先不添加，没有申请退单在添加，因为申请退单之前的订单状态已经存了)，
												
				// 如果有申请退单
				if (refundStatus.equals("2") || refundStatus.equals("7")|| refundStatus.equals("10"))
				{
					orderStatusLog onelv2 = new orderStatusLog();
					onelv2.setLoadDocType(loadDocType);
					onelv2.setChannelId(dcpOrder.getChannelId());
					onelv2.setLoadDocBillType(dcpOrder.getLoadDocBillType());
					onelv2.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
					onelv2.seteId(eId);				
					onelv2.setOpName(o_opName);
					onelv2.setOpNo(opNO);				
					onelv2.setShopNo(shopNo);
					onelv2.setOrderNo(orderNo);
					onelv2.setMachShopNo(dcpOrder.getMachShopNo());
					onelv2.setShippingShopNo(dcpOrder.getShippingShopNo());
				
					statusType = "3";// 退单状态	
					updateStaus = refundStatus;
					onelv2.setStatusType(statusType);
					onelv2.setStatus(updateStaus);
					statusTypeNameObj = new StringBuilder();
					statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
					statusTypeName = statusTypeNameObj.toString();
					onelv2.setStatusTypeName(statusTypeName);
					onelv2.setStatusName(statusName);

					memo = "";
					memo += statusName;
					onelv2.setMemo(memo);
					onelv2.setDisplay("1");

					updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv2.setUpdate_time(updateDatetime);
					
					orderStatusLogList.add(onelv2);

					
				} 
				else
				{
					orderStatusLogList.add(onelv1);
				}

				StringBuilder errorStatusLogMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
				if (nRet) {
					HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNo:" + orderNo);
				} else {
					HelpTools.writelog_waimai(
							"【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + orderNo);
				}
			


			}
			catch (Exception e)
			{

			}
			//endregion


			//清除缓存
			/*boolean IsDeleteRedis = false;
			if(status.equals("3")||status.equals("12"))
			{
				IsDeleteRedis = true;
			}
			else if(status.equals("11"))
			{
				if(refundStatus.equals("2")||refundStatus.equals("4")||refundStatus.equals("7"))
				{

				}
				else
				{
					IsDeleteRedis = true;
				}
			}
			if(IsDeleteRedis)
			{
				String hash_key = orderNO;
				String redis_key =  "WMORDER" + "_" + companyNO + "_" + shopNO;;
				DeleteRedis(redis_key, hash_key);
			}*/


		}
		catch (SQLException e) 
		{
			this.pData.clear();
			HelpTools.writelog_waimai("【JBP执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
		}
		catch (Exception e) 
		{
			this.pData.clear();
			HelpTools.writelog_waimai("【JBP执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
		}		

	}
	
	/**
	 * 查询下订单是否存在，返回订单状态信息
	 * @param eid
	 * @param orderNO
	 * @param orderHead
	 * @return
	 * @throws Exception
	 */
	private boolean IsExistOrder(String eid,  String orderNO,Map<String, Object> orderHead) throws Exception
	{
		boolean nRet = false;

		String sql = "select * from DCP_order ";
		sql += " where ORDERNO='" + orderNO + "' and EID='" + eid + "'";
		
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			nRet = true;
			String status = getQDataDetail.get(0).get("STATUS").toString();
			String refundStatus = getQDataDetail.get(0).get("REFUNDSTATUS").toString();
			String shop = getQDataDetail.get(0).get("SHOP").toString();
			String channelId = getQDataDetail.get(0).get("CHANNELID").toString();
			orderHead.put("STATUS", status);
			orderHead.put("REFUNDSTATUS", refundStatus);
			orderHead.put("SHOP", shop);
			orderHead.put("CHANNELID", channelId);
			
		}
		return nRet;

	}

	private void DeleteRedis(String redis_key,String hash_key) throws Exception
	{
		try 
		{			
			RedisPosPub redis = new RedisPosPub();
			HelpTools.writelog_waimai("【开始删除缓存】"+" redis_key:"+redis_key+",hash_key:"+hash_key);
			redis.DeleteHkey(redis_key, hash_key);//
			HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！"+" redis_key:"+redis_key+",hash_key:"+hash_key);
			//redis.Close();

		} 
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【删除存在hash_key的缓存】异常"+e.getMessage()+",redis_key:"+redis_key+",hash_key:"+hash_key);
		}
	}
	
	private void WriteRedis(String redis_key,String hash_key,String hash_value ) throws Exception
	{
		try 
		{			
			RedisPosPub redis = new RedisPosPub();
			HelpTools.writelog_waimai("【开始写缓存】"+" redis_key:"+redis_key+",hash_key:"+hash_key+",hash_value:"+hash_value);
			//redis.DeleteHkey(redis_key, hash_key);//
			boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
			if (nret) {
				HelpTools.writelog_waimai(
						"【MT写缓存】OK" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
			} else {
				HelpTools.writelog_waimai(
						"【MT写缓存】Error" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
			}
			//redis.Close();		

		} 
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【开始写缓存】异常"+e.getMessage()+" redis_key:"+redis_key+" hash_key:"+hash_key);
		}
	}

}
