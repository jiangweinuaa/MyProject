package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderCheckAgreeOrRejectReq;
import com.dsc.spos.json.cust.req.DCP_OrderRefundReq;
import com.dsc.spos.json.cust.res.DCP_OrderCheckAgreeOrRejectRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderCheckAgreeOrReject extends SPosAdvanceService<DCP_OrderCheckAgreeOrRejectReq, DCP_OrderCheckAgreeOrRejectRes>
{

	@Override
	protected void processDUID(DCP_OrderCheckAgreeOrRejectReq req, DCP_OrderCheckAgreeOrRejectRes res) throws Exception
	{
		String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String stime = new SimpleDateFormat("HHmmss").format(new Date());
		String sDateTime =new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

		/************* 必传的节点 ******************/
		String eId = req.geteId();// token的eId
		// 1：同意,2：拒绝
		String opType = req.getRequest().getOpType();
		
		if(opType.equals("1")||opType.equals("2"))
		{
			
		}
		else
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "操作类型opType="+opType+"暂不支持！");
		}

		String refundBdate = req.getRequest().getRefundBdate();
		if(refundBdate==null||refundBdate.isEmpty())
		{
			refundBdate = sdate;
		}
		String refundDatetime = req.getRequest().getRefundDatetime();
		if(refundDatetime==null||refundDatetime.isEmpty())
		{
			refundDatetime = sDateTime;
		}
		String opNo = req.getOpNO();
		String opName = req.getOpName();

		String refundReason = req.getRequest().getRefundReason();
		if (refundReason == null)
		{
			refundReason = "";
		}

        String machineNo = "";
        String workNo = "";
        String squadNo = "";
		ParseJson pj = new ParseJson();

		DCP_OrderCheckAgreeOrRejectRes.responseDatas datas = res.new responseDatas();
		datas.setErrorOrderList(new ArrayList<DCP_OrderCheckAgreeOrRejectRes.level1Elm>());
		boolean sucess = true;//所有都成功，才返回true
		for (DCP_OrderCheckAgreeOrRejectReq.level1Elm map : req.getRequest().getOrderList())
		{
			this.pData.clear();//删除前一次SQL，防止之前没有执行
			DCP_OrderCheckAgreeOrRejectRes.level1Elm oneLv1 = res.new level1Elm();
			String orderNo = map.getOrderNo();
			String errorDesc = "";
			String sql = "select * from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"' ";
			HelpTools.writelog_waimai("批量【调用DCP_OrderCheckAgreeOrReject接口】循环查询订单sql=" +sql+" 单号orderNo="+orderNo);
			List<Map<String, Object>> getHead = this.doQueryData(sql, null);
			if(getHead==null||getHead.isEmpty())
			{
				errorDesc = "订单不存在！";
				sucess = false;
				HelpTools.writelog_waimai("批量【调用DCP_OrderCheckAgreeOrReject接口】订单不存在！" +sql+" 单号orderNo="+orderNo);
				oneLv1.setErrorDesc(errorDesc);
				oneLv1.setOrderNo(orderNo);
				datas.getErrorOrderList().add(oneLv1);
                //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "订单不存在,订单号:"+orderNo);
				continue;
			}
			String status = getHead.get(0).get("STATUS").toString();
			String channelId = getHead.get(0).get("CHANNELID").toString();
			String shopId = getHead.get(0).get("SHOP").toString();
			String loadDocType = getHead.get(0).get("LOADDOCTYPE").toString();
			String loadDocBillType = getHead.get(0).get("LOADDOCBILLTYPE").toString();
			String loadDocOrderNo = getHead.get(0).get("LOADDOCORDERNO").toString();
			String statusType = "1";
			StringBuilder statusTypeNameSB = new StringBuilder();
			String statusName = HelpTools.GetOrderStatusName(statusType, status, statusTypeNameSB);
			if(status.equals("3")||status.equals("11")||status.equals("12"))
			{
				
				errorDesc = "订单状态"+statusName;											
				sucess = false;
				HelpTools.writelog_waimai("批量【调用DCP_OrderCheckAgreeOrReject接口】异常：" +errorDesc+" 单号orderNo="+orderNo);
				oneLv1.setErrorDesc(errorDesc);
				oneLv1.setOrderNo(orderNo);
				datas.getErrorOrderList().add(oneLv1);
                //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorDesc+"，不能审单,订单号:"+orderNo);
				continue;
			}
			
			if(opType.equals("1"))
			{
				if (status.equals("0")==false)
				{
					errorDesc = "订单状态"+statusName+"，非待审核状态，不能同意审单";
					sucess = false;
					HelpTools.writelog_waimai("批量【调用DCP_OrderCheckAgreeOrReject接口】异常：" +errorDesc+" 单号orderNo="+orderNo);
					oneLv1.setErrorDesc(errorDesc);
					oneLv1.setOrderNo(orderNo);
					datas.getErrorOrderList().add(oneLv1);
                    //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorDesc+",订单号:"+orderNo);
					continue;
				}

                if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
                {
                    String canModify = getHead.get(0).getOrDefault("CANMODIFY","").toString();
                    if ("Y".equals(canModify))
                    {
                        //订单信息未完整，请先到pos完成信息补录！
                        errorDesc = "订单信息未完整(请先到pos完成信息补录)，不能同意审单";
                        sucess = false;
                        HelpTools.writelog_waimai("批量【调用DCP_OrderCheckAgreeOrReject接口】异常：" +errorDesc+" 单号orderNo="+orderNo);
                        oneLv1.setErrorDesc(errorDesc);
                        oneLv1.setOrderNo(orderNo);
                        datas.getErrorOrderList().add(oneLv1);
                        //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, errorDesc+",订单号:"+orderNo);
                        continue;
                    }
                }
				
				UptBean up1 = new UptBean("DCP_ORDER");
				up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				up1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));

				// 更新updatetime
				up1.addUpdateValue("UPDATE_TIME",
						new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				up1.addUpdateValue("TRAN_TIME",
						new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				try
				{
					up1.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
					this.addProcessData(new DataProcessBean(up1));
					this.doExecuteDataToDB();
				} 
				catch (Exception e)
				{
					this.pData.clear();
					HelpTools.writelog_waimai("【调用DCP_OrderCheckAgreeOrReject接口】【更新数据库】异常:" + e.getMessage()+" 单号orderNo="+orderNo);
					errorDesc = "【更新数据库】异常:"+ e.getMessage();
					sucess = false;			
					oneLv1.setErrorDesc(errorDesc);
					oneLv1.setOrderNo(orderNo);
					datas.getErrorOrderList().add(oneLv1);					
					continue;
				}
				
				//写日志
				try
				{
					List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
					orderStatusLog onelv1 = new orderStatusLog();
					onelv1.setLoadDocType(loadDocType);
					onelv1.setChannelId(channelId);
					onelv1.setLoadDocBillType(loadDocBillType);
					onelv1.setLoadDocOrderNo(loadDocOrderNo);
					onelv1.seteId(eId);
					onelv1.setOpName(opName);
					onelv1.setOpNo(opNo);
					onelv1.setShopNo(shopId);
					onelv1.setOrderNo(orderNo);
					onelv1.setMachShopNo("");
					onelv1.setShippingShopNo("");
					
					String updateStaus = "1";
					
					onelv1.setStatusType(statusType);
					onelv1.setStatus(updateStaus);
					StringBuilder statusTypeNameObj = new StringBuilder();
					statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
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
					if (nRet)
					{
						HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
					} else
					{
						HelpTools.writelog_waimai(
								"【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
					}
					this.pData.clear();
					// endregion

				} catch (Exception e)
				{
					this.pData.clear();

				}

				//写下缓存
				try
				{
					order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao,eId,loadDocType,orderNo);
					String shopNo = dcpOrder.getShopNo();
					String machShopNo = dcpOrder.getMachShopNo();
					String shippingShopNo = dcpOrder.getShippingShopNo();
					HelpTools.writelog_waimai("订单号orderNo="+orderNo+",【配送门店】shippingShopNo="+shippingShopNo+",下订门店shopNo="+shopNo+",生产门店machShopNo="+machShopNo);
					String Response_json = pj.beanToJson(dcpOrder);
					String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shippingShopNo;
					// String hash_key = orderid + "&" + orderStatus;
					String hash_key = orderNo;
					RedisPosPub redis = new RedisPosPub();
					if (shippingShopNo!=null&&!shippingShopNo.trim().isEmpty())
					{
						HelpTools.writelog_waimai(
								"【配送门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
						boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
						if (nret) {
							HelpTools.writelog_waimai("【配送门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
						} else {
							HelpTools.writelog_waimai("【配送门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
						}
					}

					//下订门店与配送门店不一致
					if(shopNo!=null&&shopNo.trim().isEmpty()==false&&shopNo.equals(shippingShopNo)==false)
					{
						redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;

						HelpTools.writelog_waimai(
								"【下订门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);

						boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
						if (nret) {
							HelpTools.writelog_waimai("【下订门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
						} else {
							HelpTools.writelog_waimai("【下订门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
						}

					}

					//生产门店与其他2个门店不一致
					if(machShopNo!=null&&machShopNo.trim().isEmpty()==false&&machShopNo.equals(shippingShopNo)==false&&machShopNo.equals(shopNo)==false)
					{
						redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + machShopNo;

						HelpTools.writelog_waimai(
								"【生产门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
						boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
						if (nret) {
							HelpTools.writelog_waimai("【生产门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
						} else {
							HelpTools.writelog_waimai("【生产门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
						}

					}

                }
				catch (Exception e)
                {

                }
				
			}
			else
			{
				//订单状态给3.已拒单 ，需要生成一笔退订单，回写原单的商品已退数量以及单头的已退金额
				
				
				boolean otherChannelRes = true;//退款调用相应渠道接口，成功标识
				StringBuffer otherChannelError = new StringBuffer("");
				
				if (loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.LINE))//商城的需要调用退款
				{
					
					try
					{
	  					
							String refundReasonCode_wechat = "4";//退单原因  不传默认0-未知、1-未付款自主取消、2-超时未支付、3-商家拒单、4-用户申请退单
							String refundReasonDesc = refundReason;//退单原因描述
							if(refundReasonDesc==null||refundReasonDesc.trim().isEmpty())
							{
								refundReasonDesc = "";
							}
							JSONObject objReq = new JSONObject();
							objReq.put("orderNo", orderNo);
							objReq.put("refund", 1);
							objReq.put("refundReason", refundReasonCode_wechat);
							objReq.put("refundReasonDesc", refundReasonDesc);

							String request = objReq.toString();														
							String microMarkServiceName = "OrderRefund";
														
							String result = HttpSend.MicroMarkSend(request, eId, microMarkServiceName,channelId);
							JSONObject json = new JSONObject(result);
							try 
							{

								String success = json.get("success").toString();					
								String serviceDescription = json.get("serviceDescription").toString();
								if(success.equals("true")|| serviceDescription.equals("訂單異常或已退款")||serviceDescription.equals("订单异常或已退款"))
								{
									
								}
								else
								{
									otherChannelRes = false;
									otherChannelError.append(serviceDescription);
								}
							} 
							catch (Exception e) 
							{
								otherChannelRes = false;
								otherChannelError.append(e.getMessage());
							}						
					} 
					catch (Exception e)
					{
						// TODO: handle exception
						otherChannelRes = false;
						otherChannelError.append(e.getMessage());
					}
				}
				else
				{
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "渠道类型" + loadDocType + "暂不支持,审核拒单");
				}
				
				
				if(!otherChannelRes)
				{					
					errorDesc = "【调用渠道类型="+loadDocType+"退款接口】返回异常:"+otherChannelError.toString();
					sucess = false;			
					oneLv1.setErrorDesc(errorDesc);
					oneLv1.setOrderNo(orderNo);
					datas.getErrorOrderList().add(oneLv1);		
					HelpTools.writelog_waimai("【调用DCP_OrderCheckAgreeOrReject接口】" + errorDesc+" 单号orderNo="+orderNo);
					continue;					
				}
				
				
				UptBean up1 = new UptBean("DCP_ORDER");
				up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
				up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));
				
				//更新updatetime
				up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				up1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
				this.addProcessData(new DataProcessBean(up1));
				// 更新单身已退数量
				String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='" + eId
						+ "' and orderno='" + orderNo + "' ";
				ExecBean exSale = new ExecBean(execsql);
				this.addProcessData(new DataProcessBean(exSale));
				
				//生成退订单
				List<DataProcessBean> DPB_returnOrder = new ArrayList<DataProcessBean>();//记录 退订单的sql
				DCP_OrderRefund orderRefund = new DCP_OrderRefund();
				boolean isHashReturn = this.isReturn(eId, orderNo);
				if(isHashReturn==false)
				{
					//传null表示全退
					DPB_returnOrder = orderRefund.getReturnOrderSql(this.dao,eId, refundBdate,loadDocType, orderNo,"",machineNo,workNo,squadNo,null,null,null,null);
				}
				String logMemo = "";

				if(DPB_returnOrder!=null&&DPB_returnOrder.isEmpty()==false)
				{
					logMemo += "<br>生成退订单单号："+"RE"+orderNo;
					for (DataProcessBean bean : DPB_returnOrder)
					{
						this.addProcessData(bean);
					}
				}
				
				//库存解锁				
				DCP_OrderRefundReq req_orderRefund = new DCP_OrderRefundReq();
				
				DCP_OrderRefundReq.levelRequest req_orderRefund_request = req_orderRefund.new levelRequest();
				req_orderRefund.setToken(req.getToken());
				req_orderRefund_request.seteId(eId);
				req_orderRefund_request.setChannelId(channelId);
				req_orderRefund_request.setLoadDocType(loadDocType);
				req_orderRefund_request.setOrderNo(orderNo);
				req_orderRefund_request.setRefundBdate(refundBdate);
				req_orderRefund.setRequest(req_orderRefund_request);
				StringBuffer unLockStockError = new StringBuffer();			
				boolean unLockStockFlag = orderRefund.dcpStockUnlock(this.dao,req_orderRefund, unLockStockError);
				if(unLockStockFlag)
				{
					logMemo += "<br>库存解锁成功";
				}						

				try
				{
					this.doExecuteDataToDB();
					

					
					//写日志
					try
					{
						List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
						orderStatusLog onelv1 = new orderStatusLog();
						onelv1.setLoadDocType(loadDocType);
						onelv1.setChannelId(channelId);
						onelv1.setLoadDocBillType(loadDocBillType);
						onelv1.setLoadDocOrderNo(loadDocOrderNo);
						onelv1.seteId(eId);
						onelv1.setOpName(opName);
						onelv1.setOpNo(opNo);
						onelv1.setShopNo(shopId);
						onelv1.setOrderNo(orderNo);
						onelv1.setMachShopNo("");
						onelv1.setShippingShopNo("");
						
						String updateStaus = "3";
						
						onelv1.setStatusType(statusType);
						onelv1.setStatus(updateStaus);
						StringBuilder statusTypeNameObj = new StringBuilder();
						statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
						String statusTypeName = statusTypeNameObj.toString();
						onelv1.setStatusTypeName(statusTypeName);
						onelv1.setStatusName(statusName);

						String memo = "";
						memo += statusName;
						memo += logMemo;
						onelv1.setMemo(memo);
						onelv1.setDisplay("1");

						String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						onelv1.setUpdate_time(updateDatetime);

						orderStatusLogList.add(onelv1);

						StringBuilder errorStatusLogMessage = new StringBuilder();
						boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
						if (nRet)
						{
							HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
						} else
						{
							HelpTools.writelog_waimai(
									"【写表tv_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
						}
						this.pData.clear();
						// endregion

					} 
					catch (Exception e)
					{
						this.pData.clear();

					}
										
				} 
				catch (Exception e)
				{
					this.pData.clear();
					HelpTools.writelog_waimai("【调用DCP_OrderCheckAgreeOrReject接口】【更新数据库】异常:" + e.getMessage()+" 单号orderNo="+orderNo);
					errorDesc = "【更新数据库】异常:"+ e.getMessage();
					sucess = false;			
					oneLv1.setErrorDesc(errorDesc);
					oneLv1.setOrderNo(orderNo);
					datas.getErrorOrderList().add(oneLv1);					
					continue;
				}				
																		
			}
			
			
		}

		if (!sucess)
		{
			res.setSuccess(false);
			res.setServiceDescription("存在审核失败的订单!");
			res.setServiceStatus("100");
			res.setDatas(datas);
			if (datas.getErrorOrderList()!=null&&!datas.getErrorOrderList().isEmpty())
			{
				String error = "";
				for (DCP_OrderCheckAgreeOrRejectRes.level1Elm par : datas.getErrorOrderList())
				{
					error +="<br>单号"+par.getOrderNo()+""+par.getErrorDesc();
				}
				res.setServiceDescription("存在审核失败的订单!"+error);
			}

		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderCheckAgreeOrRejectReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderCheckAgreeOrRejectReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderCheckAgreeOrRejectReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderCheckAgreeOrRejectReq req) throws Exception
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
		if (Check.Null(req.getRequest().getOpType())) 
		{
			errCt++;
			errMsg.append("操作类型 opType不可为空值, ");
			isFail = true;
		} 
		
		if (req.getRequest().getOrderList()==null||req.getRequest().getOrderList().isEmpty()) 
		{
			errCt++;
			errMsg.append("订单单号列表 orderList不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderCheckAgreeOrRejectReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderCheckAgreeOrRejectReq>(){};
	}

	@Override
	protected DCP_OrderCheckAgreeOrRejectRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderCheckAgreeOrRejectRes();
	}

	/**
	 * 判断该订单号有没有生成过退订单（暂不考虑原订单退多笔）
	 * @param eId
	 * @param sourceOrderNo
	 * @return
	 * @throws Exception
	 */
	private boolean isReturn(String eId,String sourceOrderNo) throws Exception
	{
		String orderNo = "RE"+sourceOrderNo;
		
		String sql= "select * from dcp_order where  eid='"+eId+"' and orderno='"+orderNo+"' ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if(getQData!=null&&getQData.isEmpty()==false)
		{
			return true;
		}			
		return false;
	}
	
}
