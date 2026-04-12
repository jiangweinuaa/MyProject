package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.cust.res.DCP_OrderToSaleProcess_OpenRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.thirdpart.qimai.QiMaiService;
import com.dsc.spos.thirdpart.xiaoyou.xiaoyouService;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_OrderStatusUpdateERPReq;
import com.dsc.spos.json.cust.res.DCP_OrderStatusUpdateERPRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

import javax.print.DocFlavor;

/**
 * 服务函数：DCP_OrderStatusUpdate
 * 服务说明：订单状态修改
 * @author jinzma 
 * @since  2020-10-30
 */
public class DCP_OrderStatusUpdateERP extends SPosAdvanceService<DCP_OrderStatusUpdateERPReq,DCP_OrderStatusUpdateERPRes >{
	Logger logger = LogManager.getLogger(DCP_AdjustCreate.class.getName());
	@Override
	protected void processDUID(DCP_OrderStatusUpdateERPReq req, DCP_OrderStatusUpdateERPRes res) throws Exception {
		// TODO 自动生成的方法存根
		logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********订单状态修改start*************\r\n");
		String logStartStr = "【调用DCP_OrderStatusUpdateERP接口】";
		String fileName="";
		try 
		{
            
			ParseJson pj = new ParseJson();
			String reqJson = pj.beanToJson(req);			
			HelpTools.writelog_waimai(logStartStr + "请求json=" + reqJson);
			String eId = req.geteId();
			String orderNo = req.getOrderNo();
			String opNo = req.getOpNO();
			String opName = req.getOpName();
			if (opName==null||opName.isEmpty())
			{
				opName = "ERP操作人员";
			}
			String status = req.getStatus();
			String deliveryStatus = req.getDeliveryStatus();
			String productStatus = req.getProductStatus(); 
			String delName = req.getDelName();
			String delTelephone = req.getDelTelephone();
			String deliveryNo = req.getDeliveryNo();
			String deliveryType = req.getDeliveryType();
			String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			String expressTypeName = "中国快递";//物流公司名称
			String expressTypeCode = "";//物流公司代码
			String delMemo = "";//配送备注
			

			String sql = " select * from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"' ";
			HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+"  查询语句："+sql);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData != null && getQData.isEmpty() == false)
			{
				String orderStatus = getQData.get(0).get("STATUS").toString();
				String loadDocType = getQData.get(0).getOrDefault("LOADDOCTYPE","").toString();
				String channelId = getQData.get(0).getOrDefault("CHANNELID","").toString();
				String refundSourceBillNo= getQData.get(0).getOrDefault("REFUNDSOURCEBILLNO","").toString();
				delMemo = getQData.get(0).getOrDefault("DELMEMO","").toString();
				boolean isXiaoYou = false;//是不是晓柚渠道订单
				/*********晓柚渠道逻辑*************/
				if (orderLoadDocType.XIAOYOU.equals(loadDocType))
				{
					isXiaoYou = true;
				}
				
				if(orderLoadDocType.YOUZAN.equals(loadDocType)){
	            	fileName=this.getClass().getSimpleName()+"_"+orderLoadDocType.YOUZAN;
	            }else if(orderLoadDocType.XIAOYOU.equals(loadDocType)){
	            	fileName=this.getClass().getSimpleName()+"_"+orderLoadDocType.XIAOYOU;
	            }if(orderLoadDocType.QIMAI.equals(loadDocType)){
	            	fileName=this.getClass().getSimpleName()+"_"+orderLoadDocType.QIMAI;
	            }
	            
	            Log(fileName, com.alibaba.fastjson.JSON.toJSONString(req));

				if (orderStatus.equals("12"))
				{
					if (Check.Null(status))
					{

					}
					else
					{
						if (!Check.Null(status)&&"12".equals(status))
						{
							//这种可以继续修改，只要不是修改订单状态
						}
						else
						{
							logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******订单状态修改失败," + "订单单号："+orderNo+"" + "******\r\n");
							HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+"已退单，不允许修改 ！");
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"订单单号："+orderNo+"已退单，不允许修改 ！");
						}
					}


				}
				if (orderStatus.equals("3"))
				{
					if (Check.Null(status))
					{

					}
					else
					{
						logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******订单状态修改失败," + "订单单号："+orderNo+"已取消，不允许修改 ！" + "******\r\n");
						HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+"已取消，不允许修改 ！");
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"订单单号："+orderNo+"已取消，不允许修改 ！");
					}

				}
				if (orderStatus.equals("11"))
				{
					if (Check.Null(status))
					{

					}
					else
					{
						if (!Check.Null(status)&&("11".equals(status)||"12".equals(status)))
						{
							//这种可以继续修改，
						}
						else
						{
							logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******订单状态修改失败," + "订单单号："+orderNo+"已完成，不允许修改 ！" + "******\r\n");
							HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+"已完成，不允许修改 ！");
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"订单单号："+orderNo+"已完成，不允许修改 ！");
						}
					}


				}
				String shop = getQData.get(0).getOrDefault("SHOP","").toString();
				String shopName = getQData.get(0).getOrDefault("SHOPNAME","").toString();
				String shop_mach = getQData.get(0).getOrDefault("MACHSHOP","").toString();
				String shop_shipping = getQData.get(0).getOrDefault("SHIPPINGSHOP","").toString();
				String shipType = getQData.get(0).getOrDefault("SHIPTYPE","").toString();//配送方式(1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送)
				boolean isChangeStatus = false;//订单状态 10转成11
				String changeStatus = status;
				String changeStatusMemo = "";
				//总部配方式，订单渠道非QIMAI，XIAOYOU，YOUZAN渠道的订单，erp给订单状态为10的时候，中台的订单状态全部改成11，但是不需要订转销；
				if ("5".equals(shipType)&&"10".equals(status))
				{
					if (orderLoadDocType.QIMAI.equals(loadDocType)||orderLoadDocType.YOUZAN.equals(loadDocType)||orderLoadDocType.XIAOYOU.equals(loadDocType))
					{

					}
					else
					{
						isChangeStatus = true;
						changeStatus = "11";
						changeStatusMemo = "<br>订单状态已发货转成已完成";
					}

				}

				////修改订单单头
				UptBean ub = new UptBean("DCP_ORDER");
				if (!Check.Null(status))
				{
					if (isChangeStatus)
					{
						ub.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));
					}
					else
					{
						ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
					}

				}
				if (!Check.Null(productStatus))
				{
					ub.addUpdateValue("PRODUCTSTATUS", new DataValue(productStatus, Types.VARCHAR));
				}
				if (!Check.Null(deliveryStatus))
				{
					ub.addUpdateValue("DELIVERYSTATUS", new DataValue(deliveryStatus, Types.VARCHAR));
					ub.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
					ub.addUpdateValue("DELTELEPHONE", new DataValue(delTelephone, Types.VARCHAR));
					if (deliveryNo!=null&&!deliveryNo.trim().isEmpty())
					{
						ub.addUpdateValue("DELIVERYNO", new DataValue(deliveryNo, Types.VARCHAR));
					}
					if (deliveryType!=null&&!deliveryType.trim().isEmpty())
					{
						ub.addUpdateValue("DELIVERYTYPE", new DataValue(deliveryType, Types.VARCHAR));
						ub.addUpdateValue("SUBDELIVERYCOMPANYNO", new DataValue(deliveryType, Types.VARCHAR));
						if (orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.LINE.equals(loadDocType))
						{
							//  注释：物流公司名称到配置表DCP_DELIVERYTYPE查询，用物流公司编码和DELIVERYTYPE=4，把名称查询出来后
							//如果不在表中，则物流公司名称 给一个默认名称“中国快递”；
							try
							{
								String sql_kuaidi100 = " select * from DCP_DELIVERYTYPE where DELIVERYTYPE='4' and DELIVERYNO='"+deliveryType+"' ";
								List<Map<String, Object>> getQData_kuaidi100 = this.doQueryData(sql_kuaidi100, null);
								if (getQData_kuaidi100!=null&&getQData_kuaidi100.isEmpty()==false)
								{
									expressTypeName = getQData_kuaidi100.get(0).getOrDefault("DELIVERYNAME","").toString();
									ub.addUpdateValue("SUBDELIVERYCOMPANYNAME", new DataValue(expressTypeName, Types.VARCHAR));
								}
								if (expressTypeName==null||expressTypeName.trim().isEmpty())
								{
									expressTypeName = "中国快递";
								}
							}
							catch (Exception e)
							{

							}

						}

					}


				}
	            ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub));

				if ("11".equals(status)||"11".equals(changeStatus))
				{
					if (!Check.Null(shop_mach)&&!Check.Null(shop_shipping))
					{
						boolean flag = false;
						sql = "";
						sql = " select * from dcp_org where eid='"+eId+"' and organizationno in ('"+shop_mach+"','"+shop_shipping+"') ";
						HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+",查询生产和配送门店是不是总部："+sql);
						List<Map<String, Object>> getOrgList = this.doQueryData(sql, null);
						if (getOrgList!=null&&getOrgList.isEmpty()==false)
						{
							for (Map<String, Object> oneData : getOrgList)
							{
								String ORG_FORM = oneData.getOrDefault("ORG_FORM","").toString();
								if ("0".equals(ORG_FORM))
								{
									flag = true;
								}
								else
								{
									flag = false;
									break;
								}
							}
						}
						if (!flag)
						{
							HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+",生产和配送门店不全是总部,无须调用积分!");
						}
						else
						{
							HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+",生产和配送门店是总部,检查是否需要调用积分!");
							StringBuffer errorMess = new StringBuffer("");
							boolean nRet = this.UpdateMemberPoint(eId,orderNo,opNo,opName,"1",shop_shipping,errorMess);
							if (!nRet)
							{
								//写日志
								List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
								//订单状态日志
								orderStatusLog onelv1 = new orderStatusLog();
								onelv1.setLoadDocType(getQData.get(0).get("LOADDOCTYPE").toString());
								onelv1.setChannelId(getQData.get(0).get("CHANNELID").toString());
								onelv1.setLoadDocBillType("");
								onelv1.setLoadDocOrderNo("");
								onelv1.seteId(eId);
								onelv1.setOpName(opName);
								onelv1.setOpNo(opNo);
								onelv1.setShopNo(shop);
								onelv1.setOrderNo(orderNo);
								onelv1.setMachShopNo("");
								onelv1.setShippingShopNo("");

								onelv1.setStatusType("99");
								onelv1.setStatus("99");

								onelv1.setStatusTypeName("其他状态");
								onelv1.setStatusName("其他");
								String memo = "调用会员积分失败:"+errorMess.toString();

								onelv1.setMemo(memo);
								onelv1.setDisplay("0");

								onelv1.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

								orderStatusLogList.add(onelv1);

								StringBuilder errorStatusLogMessage = new StringBuilder();
								HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
							}

						}



					}

				}


				////插入订单日志表   (调用康忽悠的方法)
				///订单状态变更		
				if (!Check.Null(status)) 
				{
					orderStatusLog orderStatusLog = new orderStatusLog();
					orderStatusLog.seteId(eId);
					orderStatusLog.setShopNo(getQData.get(0).get("SHOP").toString());
					orderStatusLog.setShopName(getQData.get(0).get("SHOPNAME").toString());
					orderStatusLog.setOpNo(opNo);
					orderStatusLog.setOpName(opName);
					orderStatusLog.setOrderNo(orderNo);
					orderStatusLog.setLoadDocType(getQData.get(0).get("LOADDOCTYPE").toString());
					orderStatusLog.setChannelId(getQData.get(0).get("CHANNELID").toString());
					orderStatusLog.setLoadDocBillType(getQData.get(0).get("LOADDOCBILLTYPE").toString());
					orderStatusLog.setLoadDocOrderNo(getQData.get(0).get("LOADDOCORDERNO").toString());
					orderStatusLog.setStatusType("1");
					orderStatusLog.setStatus(status);
					if (isChangeStatus)
					{
						orderStatusLog.setStatus("11");
					}
					StringBuilder statusTypeName = new StringBuilder();
					String statusName = HelpTools.GetOrderStatusName("1", status, statusTypeName);
					if (isChangeStatus)
					{
						statusName = HelpTools.GetOrderStatusName("1", "11", statusTypeName);
					}
					orderStatusLog.setStatusTypeName(statusTypeName.toString());
					orderStatusLog.setStatusName(statusName);
					orderStatusLog.setDisplay("0");  //1:对外给买家看的 否则写0
					orderStatusLog.setMemo("ERP调用"+changeStatusMemo);
					orderStatusLog.setUpdate_time(update_time);
					InsBean ib = HelpTools.InsertOrderStatusLog(orderStatusLog);
					this.addProcessData(new DataProcessBean(ib));	
				}

				///物流状态变更
				if (!Check.Null(deliveryStatus))  
				{
					orderStatusLog orderStatusLog = new orderStatusLog();
					orderStatusLog.seteId(eId);
					orderStatusLog.setShopNo(getQData.get(0).get("SHOP").toString());
					orderStatusLog.setShopName(getQData.get(0).get("SHOPNAME").toString());
					orderStatusLog.setOpNo(opNo);
					orderStatusLog.setOpName(opName);
					orderStatusLog.setOrderNo(orderNo);
					orderStatusLog.setLoadDocType(getQData.get(0).get("LOADDOCTYPE").toString());
					orderStatusLog.setChannelId(getQData.get(0).get("CHANNELID").toString());
					orderStatusLog.setLoadDocBillType(getQData.get(0).get("LOADDOCBILLTYPE").toString());
					orderStatusLog.setLoadDocOrderNo(getQData.get(0).get("LOADDOCORDERNO").toString());
					orderStatusLog.setStatusType("2");
					orderStatusLog.setStatus(deliveryStatus);
					StringBuilder statusTypeName = new StringBuilder();
					String statusName = HelpTools.GetOrderStatusName("2", deliveryStatus, statusTypeName);
					orderStatusLog.setStatusTypeName(statusTypeName.toString());
					orderStatusLog.setStatusName(statusName);
					orderStatusLog.setDisplay("0");  //1:对外给买家看的 否则写0
					StringBuffer memoBuffer = new StringBuffer("");
					memoBuffer.append("ERP调用");
					if (deliveryNo!=null&&!deliveryNo.trim().isEmpty())
					{
						memoBuffer.append("<br>物流单号:"+deliveryNo);
					}
					if (deliveryType!=null&&!deliveryType.trim().isEmpty())
					{
						memoBuffer.append("<br>物流类型:"+deliveryType);
					}
					if (delName!=null&&!delName.trim().isEmpty())
					{
						memoBuffer.append("<br>配送人员:"+delName);
					}
					if (delTelephone!=null&&!delTelephone.trim().isEmpty())
					{
						memoBuffer.append("<br>配送电话:"+delTelephone);
					}
					orderStatusLog.setMemo(memoBuffer.toString());
					orderStatusLog.setUpdate_time(update_time);
					InsBean ib = HelpTools.InsertOrderStatusLog(orderStatusLog);
					this.addProcessData(new DataProcessBean(ib));	
				}

				////生产状态变更
				if (!Check.Null(productStatus))
				{
					orderStatusLog orderStatusLog = new orderStatusLog();
					orderStatusLog.seteId(eId);
					orderStatusLog.setShopNo(getQData.get(0).get("SHOP").toString());
					orderStatusLog.setShopName(getQData.get(0).get("SHOPNAME").toString());
					orderStatusLog.setOpNo(opNo);
					orderStatusLog.setOpName(opName);
					orderStatusLog.setOrderNo(orderNo);
					orderStatusLog.setLoadDocType(getQData.get(0).get("LOADDOCTYPE").toString());
					orderStatusLog.setChannelId(getQData.get(0).get("CHANNELID").toString());
					orderStatusLog.setLoadDocBillType(getQData.get(0).get("LOADDOCBILLTYPE").toString());
					orderStatusLog.setLoadDocOrderNo(getQData.get(0).get("LOADDOCORDERNO").toString());
					orderStatusLog.setStatusType("4");
					orderStatusLog.setStatus(productStatus);
					StringBuilder statusTypeName = new StringBuilder();
					String statusName = HelpTools.GetOrderStatusName("4", productStatus, statusTypeName);
					orderStatusLog.setStatusTypeName(statusTypeName.toString());
					orderStatusLog.setStatusName(statusName);
					orderStatusLog.setDisplay("0");  //1:对外给买家看的 否则写0
					orderStatusLog.setMemo("ERP调用");
					orderStatusLog.setUpdate_time(update_time);
					InsBean ib = HelpTools.InsertOrderStatusLog(orderStatusLog);
					this.addProcessData(new DataProcessBean(ib));	
				}

				this.doExecuteDataToDB();
				HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+"订单状态修改 ！");
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

				//region xiaoyou渠道类型逻辑
				if (isXiaoYou)
				{
					if (!Check.Null(status)&&"10".equals(status))
					{
						//region ERP已发货，通知晓柚
						boolean nRet = false;
						String errorMessage = "";
						List<Map<String, Object>> xiaoYouAppKeyList = PosPub.getWaimaiAppConfig(StaticInfo.dao, eId,
								orderLoadDocType.XIAOYOU);
						if (xiaoYouAppKeyList != null && xiaoYouAppKeyList.size() > 0)
						{
							Map<String, Object> setMap = xiaoYouAppKeyList.get(0);
							for (Map<String, Object> par : xiaoYouAppKeyList)
							{
								if (channelId.equals(par.get("CHANNELID").toString()))
								{
									setMap = par;
									break;
								}
							}
							String sqlOrderDetail="select * from dcp_order_detail where eid='"+eId+"' and orderno='"+orderNo+"' ";
							List<Map<String, Object>> getDetailDatas=this.doQueryData(sqlOrderDetail, null);

							try
							{
								xiaoyouService xiaoyou = new xiaoyouService();
								Map<String, Object> orderMap = getQData.get(0);
								if (deliveryNo!=null&&!deliveryNo.trim().isEmpty())
								{
									orderMap.put("DELIVERYNO",deliveryNo);
								}
								if (deliveryType!=null&&!deliveryType.trim().isEmpty())
								{
									orderMap.put("DELIVERYTYPE",deliveryType);
								}
								String res_xiaoyou = xiaoyou.sendWeshopOrder(setMap,orderNo,getQData.get(0),getDetailDatas);
								JSONObject resJson = JSONObject.parseObject(res_xiaoyou);
								String resCode = resJson.get("status").toString();
								if ("1".equals(resCode))
								{
									nRet = true;
									errorMessage = "发货通知晓柚成功！";
								}
								else
								{
									nRet = false;
									errorMessage = "发货通知晓柚失败:"+resJson.getOrDefault("msg","").toString();
								}

							}
							catch (Exception e)
							{
								nRet = false;
								errorMessage ="调用晓柚接口异常:"+e.getMessage();
							}

						}
						else
						{
							nRet = false;
							errorMessage ="晓柚对接渠道参数apiKey,apiSecret没有设置！";
						}

						if (!nRet)
						{
							res.setSuccess(nRet);
							res.setServiceStatus("100");
							res.setServiceDescription("DCP更新发货状态成功，但是通知晓柚失败:"+errorMessage);
						}

						//写订单日志
						List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
						orderStatusLog oslog=new orderStatusLog();
						oslog.setCallback_status("N");
						oslog.setChannelId(channelId);
						oslog.setDisplay("0");
						oslog.seteId(eId);
						oslog.setLoadDocType(loadDocType);
						oslog.setMachShopName("");
						oslog.setMachShopNo("");
						oslog.setNeed_callback("N");
						oslog.setNeed_notify("N");
						oslog.setNotify_status("N");
						oslog.setOpName(opName);
						oslog.setOpNo(opNo);
						oslog.setOrderNo(orderNo);
						oslog.setShippingShopName("");
						oslog.setShippingShopNo("");
						oslog.setShopName(shopName);
						oslog.setShopNo(shop);

						//
						oslog.setMemo(errorMessage);
						String statusType="99";
						if (nRet)
						{
							oslog.setStatus("99");
						}
						else
						{
							statusType="997";
							oslog.setStatus("997");
						}

						StringBuilder statusTypeName=new StringBuilder("其他状态");
						String statusName="其他状态";
						oslog.setStatusName(statusName);
						oslog.setStatusType(statusType);
						oslog.setStatusTypeName(statusTypeName.toString());
						oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
						orderStatusLogList.add(oslog);
						StringBuilder errorMessage_log = new StringBuilder();
						boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage_log);
						//endregion
					}

					if (!Check.Null(deliveryStatus)&&"-3".equals(deliveryStatus))
					{
						//region ERP退货入库，通知晓柚
						boolean nRet = false;
						String errorMessage = "";
						List<Map<String, Object>> xiaoYouAppKeyList = PosPub.getWaimaiAppConfig(StaticInfo.dao, eId,
								orderLoadDocType.XIAOYOU);
						if (xiaoYouAppKeyList != null && xiaoYouAppKeyList.size() > 0)
						{
							Map<String, Object> setMap = xiaoYouAppKeyList.get(0);
							for (Map<String, Object> par : xiaoYouAppKeyList)
							{
								if (channelId.equals(par.get("CHANNELID").toString()))
								{
									setMap = par;
									break;
								}
							}
							try
							{
								xiaoyouService xiaoyou = new xiaoyouService();
								String res_xiaoyou = xiaoyou.addRefundAction(setMap,orderNo);
								JSONObject resJson = JSONObject.parseObject(res_xiaoyou);
								String resCode = resJson.get("status").toString();
								if ("1".equals(resCode))
								{
									nRet = true;
									errorMessage = "退货入库通知晓柚成功！";
								}
								else
								{
									nRet = false;
									errorMessage = "退货入库晓柚失败:"+resJson.getOrDefault("msg","").toString();
								}

							}
							catch (Exception e)
							{
								nRet = false;
								errorMessage ="调用晓柚接口异常:"+e.getMessage();
							}






						}
						else
						{
							nRet = false;
							errorMessage ="晓柚对接渠道参数apiKey,apiSecret没有设置！";
						}

						if (!nRet)
						{
							res.setSuccess(nRet);
							res.setServiceStatus("100");
							res.setServiceDescription("DCP更新退货入库状态成功，但是通知晓柚失败:"+errorMessage);
						}

						//写订单日志
						List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
						orderStatusLog oslog=new orderStatusLog();
						oslog.setCallback_status("N");
						oslog.setChannelId(channelId);
						oslog.setDisplay("0");
						oslog.seteId(eId);
						oslog.setLoadDocType(loadDocType);
						oslog.setMachShopName("");
						oslog.setMachShopNo("");
						oslog.setNeed_callback("N");
						oslog.setNeed_notify("N");
						oslog.setNotify_status("N");
						oslog.setOpName(opName);
						oslog.setOpNo(opNo);
						oslog.setOrderNo(orderNo);
						oslog.setShippingShopName("");
						oslog.setShippingShopNo("");
						oslog.setShopName(shopName);
						oslog.setShopNo(shop);

						//
						oslog.setMemo(errorMessage);
						String statusType="99";
						if (nRet)
						{
							oslog.setStatus("99");
						}
						else
						{
							statusType="996";
							oslog.setStatus("996");
						}

						StringBuilder statusTypeName=new StringBuilder("其他状态");
						String statusName="其他状态";
						oslog.setStatusName(statusName);
						oslog.setStatusType(statusType);
						oslog.setStatusTypeName(statusTypeName.toString());
						oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
						orderStatusLogList.add(oslog);
						//写下原单的订单历程
						if (!orderNo.equals(refundSourceBillNo))
						{
							orderStatusLog oslog2=new orderStatusLog();
							oslog2.setCallback_status("N");
							oslog2.setChannelId(channelId);
							oslog2.setDisplay("0");
							oslog2.seteId(eId);
							oslog2.setLoadDocType(loadDocType);
							oslog2.setMachShopName("");
							oslog2.setMachShopNo("");
							oslog2.setNeed_callback("N");
							oslog2.setNeed_notify("N");
							oslog2.setNotify_status("N");
							oslog2.setOpName(opName);
							oslog2.setOpNo(opNo);
							oslog2.setOrderNo(refundSourceBillNo);
							oslog2.setShippingShopName("");
							oslog2.setShippingShopNo("");
							oslog2.setShopName(shopName);
							oslog2.setShopNo(shop);

							//
							oslog2.setMemo(errorMessage);
							statusType="99";
							if (nRet)
							{
								oslog2.setStatus("99");
							}
							else
							{
								statusType="996";
								oslog2.setStatus("996");
							}

							statusTypeName=new StringBuilder("其他状态");
							statusName="其他状态";
							oslog2.setStatusName(statusName);
							oslog2.setStatusType(statusType);
							oslog2.setStatusTypeName(statusTypeName.toString());
							oslog2.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
							orderStatusLogList.add(oslog2);
						}
						StringBuilder errorMessage_log = new StringBuilder();
						boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage_log);
						//endregion
					}
				}
				//endregion

				//企迈订单
				if (orderLoadDocType.QIMAI.equals(loadDocType)){
					//10-已发货
					if ("10".equals(status)){
						try{
							if(deliveryNo!=null&&deliveryNo.length()>0&&deliveryType!=null&&deliveryType.length()>0){
								Map<String, Object> otherMap = new HashMap<String, Object>();
								otherMap.put("deliveryNo", deliveryNo);
								otherMap.put("deliveryType", deliveryType);
								otherMap.put("docType", "ERP");
								JsonBasicRes thisRes=new JsonBasicRes();
								thisRes=QiMaiService.getInstance().localDelivery(getQData.get(0), otherMap);
								if(!thisRes.isSuccess()){
									res.setSuccess(false);
									res.setServiceStatus("100");
									res.setServiceDescription("操作失败：[QIMAI]"+thisRes.getServiceDescription());
								}
							}
						}catch (Exception e) { 
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("操作失败：[QIMAI]"+e.getMessage());
						}
					}
				}
				else if (orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.LINE.equals(loadDocType))
				{
					//手机商城
					if (!Check.Null(deliveryStatus))
					{
						String deliveryStatus_crm = "0";//0=未配送;1=配送中;2=已配送;3=确认收货
						String description = "未配送";
						if ("-1".equals(deliveryStatus)||"0".equals(deliveryStatus)||"1".equals(deliveryStatus)||"2".equals(deliveryStatus)||"6".equals(deliveryStatus)||"7".equals(deliveryStatus)||"8".equals(deliveryStatus))
						{
							deliveryStatus_crm = "1";
							description = "配送中";
						}
						else if ("9".equals(deliveryStatus))
						{
							//消费者七天未取件
							deliveryStatus_crm = "2";
							description = "已配送";
						}
						else if ("3".equals(deliveryStatus))
						{
							//用户签收
							deliveryStatus_crm = "3";
							description = "确认收货";
						}
						boolean nRet_Crm = false;
						StringBuffer nRet_error = new StringBuffer("");
						try
						{
							org.json.JSONObject js=new org.json.JSONObject();
							js.put("serviceId", "OrderStatusUpdate");
							js.put("orderNo", orderNo);
							js.put("statusType", "2");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
							js.put("status", deliveryStatus_crm);//交易状态 3=已接单 4=已发货 5=已收货未回款 6=交易成功（确认收货，货款两清） 7=交易关闭（取消订单、全额退款、超时未支付、拒绝接单）
							js.put("description", description);
							js.put("oprId", opNo);
							js.put("orgType", "2");
							js.put("orgId", shop);
							js.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

							org.json.JSONArray deliverInfo = new org.json.JSONArray();
							org.json.JSONObject body = new org.json.JSONObject();
							//body.put("expressType", "2");
							body.put("expressTypeCode", deliveryType);
							body.put("expressTypeName", expressTypeName);
							body.put("expressBillNo", deliveryNo);
							body.put("deliverPerson", delName);
							body.put("deliverPhone", delTelephone);
							body.put("remark", delMemo);
							deliverInfo.put(body);

							js.put("deliverInfo", deliverInfo);

							String req_crm = js.toString();
							String result = HttpSend.MicroMarkSend(req_crm, eId, "OrderStatusUpdate",channelId);
							org.json.JSONObject json = new org.json.JSONObject(result);
							String success = json.get("success").toString();
							if(success.equals("true"))
							{
								nRet_Crm = true;

							}
							else
							{
								String serviceDescription = json.get("serviceDescription").toString();
								nRet_error.append(serviceDescription);
							}
						}
						catch (Exception e)
						{
							nRet_error.append(e.getMessage());
						}

						//写订单日志
						List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
						orderStatusLog oslog=new orderStatusLog();
						oslog.setCallback_status("N");
						oslog.setChannelId(channelId);
						oslog.setDisplay("0");
						oslog.seteId(eId);
						oslog.setLoadDocType(loadDocType);
						oslog.setMachShopName("");
						oslog.setMachShopNo("");
						oslog.setNeed_callback("N");
						oslog.setNeed_notify("N");
						oslog.setNotify_status("N");
						oslog.setOpName(opName);
						oslog.setOpNo(opNo);
						oslog.setOrderNo(orderNo);
						oslog.setShippingShopName("");
						oslog.setShippingShopNo("");
						oslog.setShopName(shopName);
						oslog.setShopNo(shop);

						//
						oslog.setMemo("");
						String statusType="99";
						if (nRet_Crm)
						{
							oslog.setStatus("99");
							oslog.setMemo("通知商城成功");
						}
						else
						{
							statusType="996";
							oslog.setStatus("996");
							oslog.setMemo("通知商城失败:"+nRet_error.toString());
						}

						StringBuilder statusTypeName=new StringBuilder("其他状态");
						String statusName="其他状态";
						oslog.setStatusName(statusName);
						oslog.setStatusType(statusType);
						oslog.setStatusTypeName(statusTypeName.toString());
						oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
						orderStatusLogList.add(oslog);

						StringBuilder errorMessage_log = new StringBuilder();
						boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage_log);

					}

					if (!Check.Null(status))
					{
						String description = "订单状态变更";
						StringBuilder statusTypeName = new StringBuilder();
						String statusName = HelpTools.GetOrderStatusName("1", status, statusTypeName);
						description = "ERP调用订单状态变更："+statusName;
						boolean nRet_Crm = false;
						StringBuffer nRet_error = new StringBuffer("");
						try
						{
							org.json.JSONObject js=new org.json.JSONObject();
							js.put("serviceId", "OrderStatusUpdate");
							js.put("orderNo", orderNo);
							js.put("statusType", "3");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
							js.put("status", "");//交易状态 3=已接单 4=已发货 5=已收货未回款 6=交易成功（确认收货，货款两清） 7=交易关闭（取消订单、全额退款、超时未支付、拒绝接单）
							js.put("description", description);
							js.put("oprId", opNo);
							js.put("orgType", "2");
							js.put("orgId", shop);
							js.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

							String req_crm = js.toString();
							HttpSend.MicroMarkSend(req_crm, eId, "OrderStatusUpdate",channelId);

						}
						catch (Exception e)
						{
							nRet_error.append(e.getMessage());
						}

					}

				}
					

				logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********订单状态修改end*************\r\n");
			}
			else
			{
				logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******订单状态修改失败," + "订单单号："+orderNo+"不存在 ！" + "******\r\n");
				HelpTools.writelog_waimai(logStartStr+"单号orderNo="+orderNo+",该订单不存在！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"订单单号："+orderNo+"不存在 ！");
			}

		} catch (Exception e) {
			// TODO: handle exception
			StringWriter errors = new StringWriter();
			PrintWriter pw=new PrintWriter(errors);
			e.printStackTrace(pw);

			pw.flush();
			pw.close();

			errors.flush();
			errors.close();
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******订单状态修改失败," + errors.toString() + "******\r\n");
			HelpTools.writelog_waimai(logStartStr+"异常："+errors.toString());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}finally{
        	Log(fileName, com.alibaba.fastjson.JSON.toJSONString(res));
        }

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderStatusUpdateERPReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderStatusUpdateERPReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderStatusUpdateERPReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderStatusUpdateERPReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String status = req.getStatus();
		String deliveryStatus = req.getDeliveryStatus();
		String productStatus = req.getProductStatus(); 

		if (Check.Null(req.geteId())) 
		{
			errMsg.append("企业编号不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(req.getOrderNo())) 
		{
			errMsg.append("订单单号不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(status) && Check.Null(deliveryStatus) && Check.Null(productStatus))
		{
			errMsg.append("订单修改状态不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;

	}

	@Override
	protected TypeToken<DCP_OrderStatusUpdateERPReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderStatusUpdateERPReq>(){};
	}

	@Override
	protected DCP_OrderStatusUpdateERPRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderStatusUpdateERPRes();
	}

	private  boolean UpdateMemberPoint(String eId,String orderNo,String opNo,String opName,String orgType,String orgId,StringBuffer errorMessage) throws  Exception
	{
		boolean nRet = false;
		String logStartStr = "【调用DCP_OrderStatusUpdateERP接口】";
		order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao, eId, "", orderNo);
		if (dcpOrder==null)
		{
			return false;
		}
		if (orgType==null||orgType.isEmpty())
		{
			orgType = "1";
		}
		if (orgId==null||orgId.isEmpty())
		{
			orgId = dcpOrder.getShippingShopNo();
		}
		try
		{
			

			//获得积分
			BigDecimal getPoint=new BigDecimal(0);
			//尾款处理,这个只是记录付款
			com.alibaba.fastjson.JSONArray payslistArray=new com.alibaba.fastjson.JSONArray();
			//这里才会扣款
			com.alibaba.fastjson.JSONArray cardlistArray=new com.alibaba.fastjson.JSONArray();
			//券列表
			com.alibaba.fastjson.JSONArray couponlistArray=new com.alibaba.fastjson.JSONArray();

			String memberOrderno= PosPub.getGUID(false);//调用积分memberpay的orderno

			//积分卡
			List<String> listCardno=new ArrayList<>();

			for (orderPay pay : dcpOrder.getPay())
			{
				String funcno=pay.getFuncNo();
				String cardno=pay.getCardNo();

				BigDecimal p_pay=new BigDecimal(pay.getPay());
				BigDecimal p_changed=new BigDecimal(pay.getChanged());
				BigDecimal p_extra=new BigDecimal(pay.getExtra());

				if ("301".equals(funcno))
				{
					//pay-changed-extra累加起来
					BigDecimal p_realpay=p_pay.subtract(p_changed).subtract(p_extra);

					com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
					tempPay.put("payType",pay.getPayType());//收款方式代号
					tempPay.put("payName",pay.getPayName());//收款方式名称
					tempPay.put("payAmount",p_realpay);//付款金额
					tempPay.put("noCode",cardno);//卡号
					tempPay.put("isCardPay",1);//
					payslistArray.add(tempPay);

					//
					com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
					tempCard.put("cardNo",cardno);
					tempCard.put("amount","0");//0只处理积分
					tempCard.put("getPoint","0");
					cardlistArray.add(tempCard);

					//
					listCardno.add(cardno);
				}
			}



			if (Check.Null(dcpOrder.getMemberId())==false ||  Check.Null(dcpOrder.getCardNo())==false || payslistArray.size()>0 || cardlistArray.size()>0 || couponlistArray.size()>0)
			{
				//卡号为空不用传了
				if (dcpOrder.getCardNo().equals("")==false && listCardno.contains(dcpOrder.getCardNo())==false)
				{
					//
					com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
					tempCard.put("cardNo",dcpOrder.getCardNo());
					tempCard.put("amount","0");//只处理积分
					tempCard.put("getPoint","0");
					cardlistArray.add(tempCard);
				}

				String Yc_Url="";
				String Yc_Key="";
				String Yc_Sign_Key="";

				String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + eId + "'" +
						" AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
				List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
				if (result != null && result.size() > 0)
				{
					for (Map<String, Object> map : result)
					{
						//内部服务调用形式的才取数据库，外部的用传进来的接口帐号
						if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode"))
						{
							Yc_Key = map.get("ITEMVALUE").toString();
						}
						if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserKey"))
						{
							Yc_Sign_Key = map.get("ITEMVALUE").toString();
						}
					}
				}
				Yc_Url=PosPub.getCRM_INNER_URL(eId);
				if(Yc_Url.trim().equals("") || Yc_Key.trim().equals("") ||Yc_Sign_Key.trim().equals(""))
				{
					errorMessage.append("CrmUrl、apiUserCode、userKey接口参数未设置无法调用积分");
					HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，CrmUrl、apiUserCode、userKey接口参数未设置无法调用积分!");
					return  false;
					//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "CrmUrl、apiUserCode、userKey移动支付接口参数未设置!");
				}

				com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				com.alibaba.fastjson.JSONArray goodslistArray=new com.alibaba.fastjson.JSONArray();
				for (orderGoodsItem detail : dcpOrder.getGoodsList())
				{
					com.alibaba.fastjson.JSONObject goods=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
					goods.put("goods_id",detail.getPluBarcode());
					goods.put("goods_name",detail.getPluName());
					goods.put("price",detail.getPrice());
					goods.put("quantity",detail.getQty());
					goods.put("amount",detail.getAmt());
					goods.put("allowPoint","1");
					goodslistArray.add(goods);
				}

				reqheader.put("orderNo", memberOrderno);//需唯一
				reqheader.put("businessType", "2");//业务类型0.其他1.订单下订2.订单提货3.零售支付
				reqheader.put("srcBillType", "订转销");//实际业务单别
				reqheader.put("srcBillNo", orderNo);//实际业务单号
				reqheader.put("orderAmount", dcpOrder.getTot_Amt());//
				reqheader.put("pointAmount", dcpOrder.getTot_Amt());//
				reqheader.put("memberId",dcpOrder.getMemberId() );//
				reqheader.put("orgType", orgType);//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
				reqheader.put("orgId", orgId);//订转销取操作门店
				reqheader.put("oprId", opNo);//
				reqheader.put("goodsdetail", goodslistArray);
				reqheader.put("cards", cardlistArray);
				reqheader.put("coupons", couponlistArray);
				reqheader.put("payDetail", payslistArray);

				//
				String req_sign=reqheader.toString() + Yc_Sign_Key;

				req_sign= DigestUtils.md5Hex(req_sign);

				//
				signheader.put("key", Yc_Key);//
				signheader.put("sign", req_sign);//md5

				payReq.put("serviceId", "MemberPay");

				payReq.put("request", reqheader);
				payReq.put("sign", signheader);


				String str = payReq.toString();
				HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，调用会员积分接口MemberPay请求req:"+str);
				//PosPub.WriteETLJOBLog("会员积分接口MemberPay请求内容："+str +"\r\n");

				String	resbody = "";

				//编码处理
				str= URLEncoder.encode(str,"UTF-8");

				resbody= HttpSend.Sendcom(str, Yc_Url);

				//PosPub.WriteETLJOBLog("会员积分接口MemberPay返回："+resbody +"\r\n");
				HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，调用会员积分接口MemberPay返回res:"+resbody);



				if (resbody.equals("")==false)
				{
					com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);

					String serviceDescription=jsonres.get("serviceDescription").toString();
					String serviceStatus=jsonres.get("serviceStatus").toString();

					//单号重复,直接查询积分
					if (serviceStatus.equals("900"))
					{
						reqheader.clear();
						signheader.clear();
						payReq.clear();
						str = "";

						reqheader.put("orderNo", memberOrderno);
						req_sign=reqheader.toString() + Yc_Sign_Key;
						req_sign=DigestUtils.md5Hex(req_sign);

						//
						signheader.put("key", Yc_Key);//
						signheader.put("sign", req_sign);//md5

						payReq.put("serviceId", "MemberPayQuery");

						payReq.put("request", reqheader);
						payReq.put("sign", signheader);

						str = payReq.toString();

						//PosPub.WriteETLJOBLog("会员积分接口MemberPayQuery请求内容："+str +"\r\n");
						HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，会员积分接口MemberPayQuery请求req:"+str);
						//编码处理
						str=URLEncoder.encode(str,"UTF-8");

						resbody=HttpSend.Sendcom(str, Yc_Url);

						//PosPub.WriteETLJOBLog("会员积分接口MemberPayQuery返回："+resbody +"\r\n");
						HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，会员积分接口MemberPayQuery返回res:"+resbody);

						if (resbody.equals("")==false)
						{
							jsonres = JSON.parseObject(resbody);

							serviceDescription=jsonres.get("serviceDescription").toString();
							serviceStatus=jsonres.get("serviceStatus").toString();
							if (jsonres.get("success").toString().equals("true"))
							{
								com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
								for (int pi = 0; pi < cardsList.size(); pi++)
								{
									nRet = true;
									//多张卡累加积分
									getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));
								}
							}
							else
							{
								errorMessage.append("调用会员积分查询接口MemberPayQuery失败:" +serviceDescription );
								HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，调用会员积分查询接口MemberPayQuery失败:"+serviceDescription);
								return false;
							}
						}
						else
						{
							errorMessage.append("调用会员积分查询接口MemberPayQuery失败:返回为空");
							HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，调用会员积分查询接口MemberPayQuery失败:返回为空");
							return false;
						}
					}
					else
					{
						if (jsonres.get("success").toString().equals("true"))
						{
							com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
							for (int pi = 0; pi < cardsList.size(); pi++)
							{
								nRet = true;
								//多张卡累加积分
								getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));
							}
						}
						else
						{
							errorMessage.append("调用会员积分接口MemberPay失败:" + serviceDescription);
							HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，调用会员积分接口MemberPay失败:"+ serviceDescription);
							return false;
						}
					}
				}
				else
				{
					errorMessage.append("调用会员积分接口MemberPay失败:返回为空");
					HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，调用会员积分接口MemberPay失败:返回为空");
					return false;
				}



			}
			else
			{
				HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，没有会员相关信息,无需调用积分!");
				return  true;
			}

			if (nRet)
			{
				//写下日志
				//写日志
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				//订单状态日志
				orderStatusLog onelv1 = new orderStatusLog();
				onelv1.setLoadDocType(dcpOrder.getLoadDocType());
				onelv1.setChannelId(dcpOrder.getChannelId());
				onelv1.setLoadDocBillType("");
				onelv1.setLoadDocOrderNo("");
				onelv1.seteId(eId);
				onelv1.setOpName(opName);
				onelv1.setOpNo(opNo);
				onelv1.setShopNo(dcpOrder.getShopNo());
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo("");
				onelv1.setShippingShopNo("");

				onelv1.setStatusType("99");
				onelv1.setStatus("99");

				onelv1.setStatusTypeName("其他状态");
				onelv1.setStatusName("其他");
				String memo = "调用会员积分成功";
				memo +="<br>本次获得积分:"+getPoint.setScale(2, RoundingMode.HALF_UP);

				onelv1.setMemo(memo);
				onelv1.setDisplay("0");

				onelv1.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

				orderStatusLogList.add(onelv1);

				StringBuilder errorStatusLogMessage = new StringBuilder();
				HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);

			}


		}
		catch (Exception e)
		{
			errorMessage.append("调用会员积分异常:"+e.getMessage());
			HelpTools.writelog_waimai(logStartStr+"，单号orderNo="+orderNo+"，调用会员积分异常:"+e.getMessage());
			return  false;
		}
		return nRet;
	}
	
	public void Log(String fileName,String log) throws Exception {
    	if(fileName!=null&&fileName.trim().length()>0){
    		HelpTools.writelog_fileName(log, fileName);
    	}
    }

}
