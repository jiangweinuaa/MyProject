package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.Code;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;

/**
 *   查询管易云发货单的物流厂商及物流单号，更新云中台订单
 * 
 * @author 
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EcGuanyiyunDeliverysGet extends InitJob
{

	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(EcGuanyiyunDeliverysGet.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public EcGuanyiyunDeliverysGet()
	{

	}

	public EcGuanyiyunDeliverysGet(String eId,String shopId,String organizationNO, String billNo)
	{
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}


	public String doExe()
	{
		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********管易云订单物流信息查询EcGuanyiyuanDeliverysGet正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-管易云订单物流信息查询EcGuanyiyuanDeliverysGet正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********管易云订单物流信息查询EcGuanyiyuanDeliverysGet定时调用Start:************\r\n");

		try 
		{			
			//查询21管易云物流类型,没有物流单号的单据
			StringBuffer sqlOrder=new StringBuffer("select * from dcp_order a where a.deliverytype=21 and a.autodelivery='Y' and a.DELIVERYSTATUS='-1'  ");

			//根据条件
			if (pBillNo.equals("")==false) 
			{
				sqlOrder.append(" and a.eid='"+pEId+"' and a.orderno='"+pBillNo+"' and a.SHOP='"+pShop+"' ");
			}

			//取21管易云同城配送物流
			String sqlLG = " select * from dcp_outsaleset where DELIVERYTYPE='21' and status='100' " ;	
			List<Map<String, Object>> getLGData=this.doQueryData(sqlLG, null);
			if (getLGData != null && getLGData.isEmpty() == false)
			{
				//
				List<Map<String, Object>> getQData=this.doQueryData(sqlOrder.toString(), null);
				if (getQData != null && getQData.isEmpty() == false)
				{		
					for (Map<String, Object> map : getQData) 
					{			
						String order_eId=map.get("EID").toString();
						String order_shopno=map.get("SHOP").toString();
						String order_load_doctype=map.get("LOADDOCTYPE").toString();
						String order_no=map.get("ORDERNO").toString();
						String order_chanelid=map.get("CHANNELID").toString();
						String machShopId=map.get("MACHSHOP").toString();
						String machShopName=map.get("MACHSHOPNAME").toString();
						String memo=map.get("MEMO").toString();
						String shippingId=map.get("SHIPPINGSHOP").toString();
						String shippingName=map.get("SHIPPINGSHOPNAME").toString();
						String shopId=map.get("SHOP").toString();
						String shopName=map.get("SHOPNAME").toString();

						try 
						{	

							//API地址目前没有字段存，固定的
							String apiUrl=getLGData.get(0).get("APIURL").toString();//"https://v2.api.guanyierp.com/rest/erp_open";
							String appkey=getLGData.get(0).get("APPID").toString();//129151-嘉华客户							
							String secret=getLGData.get(0).get("APPSECRET").toString();//85dd5c3ffa614da3a80c87f8cfdce3bc-嘉华客户	
							String sessionkey=getLGData.get(0).get("APPSIGNKEY").toString();//a03baf70c7324f849ae1a95ec972c30f-嘉华客户

							if (apiUrl.equals("") || appkey.equals("") || sessionkey.equals("")|| secret.equals("")) 
							{
								logger.error("\r\n*********管易云订单物流信息查询EcGuanyiyuanDeliverysGet EID="+order_eId+"物流资料API关键字段有空值************\r\n");
								continue;
							}
							else 
							{
								Map<String, Object> mapOrderA = new LinkedHashMap<String, Object>();
								mapOrderA.put("appkey", appkey);
								mapOrderA.put("sessionkey", sessionkey);
								mapOrderA.put("method", "gy.erp.trade.deliverys.get");
								mapOrderA.put("page_no", "1");
								mapOrderA.put("page_size", "10");
								//mapOrderA.put("code", order_code); //单据编号，新增订单的时候管易返回的,这个是管易的订单号 没屌用 查询条件没有这个栏位
								mapOrderA.put("outer_code", order_no);

								StringBuffer sb=new StringBuffer();
								String resbody=HttpSend.SendGuanyiyuan(apiUrl, secret, "gy.erp.trade.deliverys.get", mapOrderA, order_eId, order_shopno, order_shopno, order_no, sb);

								if (resbody.equals("")==false) 
								{
									JSONObject resjsobject= JSONObject.parseObject(resbody);
									boolean Result=resjsobject.getBoolean("success");
									if (Result) 
									{
										//执行
										List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

										JSONArray deliverys=resjsobject.getJSONArray("deliverys");//
										for (int i = 0; i < deliverys.size(); i++) 
										{												
											String express_no=deliverys.getJSONObject(i).getString("express_no");//快递单号
											String express_code=deliverys.getJSONObject(i).getString("express_code");//快递公司代码
											String express_name=deliverys.getJSONObject(i).getString("express_name");//快递公司名称

											//单号必须有,他们单号与物流公司分配有时间差
											if (express_no==null ||express_no.equals("") || express_no.equals("null")) 
											{
												continue;
											}

											//更新订单管易云物流单信息
											UptBean ubecOrder=new UptBean("DCP_ORDER");
											ubecOrder.addCondition("EID", new DataValue(order_eId, Types.VARCHAR));
											ubecOrder.addCondition("ORDERNO", new DataValue(order_no, Types.VARCHAR));

											ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(express_no, Types.VARCHAR));//物流单号
											ubecOrder.addUpdateValue("SUBDELIVERYCOMPANYNO", new DataValue(express_code, Types.VARCHAR));//物流公司代码
											ubecOrder.addUpdateValue("SUBDELIVERYCOMPANYNAME", new DataValue(express_name, Types.VARCHAR));//物流公司名称
											ubecOrder.addUpdateValue("DELIVERYSTATUS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
											ubecOrder.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											ubecOrder.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
											lstData.add(new DataProcessBean(ubecOrder));

											//写订单日志
											String LogStatus="0";
											orderStatusLog oslog=new orderStatusLog();
											oslog.setCallback_status("N");
											oslog.setChannelId(order_chanelid);
											oslog.setDisplay("1");
											oslog.seteId(order_eId);
											oslog.setLoadDocBillType(order_load_doctype);
											oslog.setLoadDocOrderNo(order_no);
											oslog.setLoadDocType(order_load_doctype);
											oslog.setMachShopName(machShopName);
											oslog.setMachShopNo(machShopId);
											oslog.setMemo("订单状态-->物流已产生发货单("+express_code+express_name+",物流单号="+express_no+")");
											oslog.setNeed_callback("N");
											oslog.setNeed_notify("N");
											oslog.setNotify_status("N");
											oslog.setOpName("admin");
											oslog.setOpNo("admin");
											oslog.setOrderNo(order_no);
											oslog.setShippingShopName(shippingName);
											oslog.setShippingShopNo(shippingId);
											oslog.setShopName(shopName);
											oslog.setShopNo(shopId);
											oslog.setStatus(LogStatus);
											//
											String statusType="2";
											StringBuilder statusTypeName=new StringBuilder();
											String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);			
											oslog.setStatusName(statusName);
											oslog.setStatusType(statusType);
											oslog.setStatusTypeName(statusTypeName.toString());
											oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));			
											InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
											lstData.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));


											//更新微商城状态
											Calendar cal = Calendar.getInstance();   
											String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
											JSONObject js=new JSONObject();
											js.put("serviceId", "DCP_OrderStatusUpdate"); 
											js.put("orderNo", order_no);
											js.put("statusType", "2");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
											js.put("status", "1");//物流状态：0=未配送 1=配送中 2=已配送 3=确认收货
											js.put("description", "物流已产生发货单("+express_code+express_name+",物流单号="+express_no+")");
											js.put("oprId", "admin");
											js.put("orgType", "2");
											js.put("orgId", order_shopno);
											js.put("updateTime", updateTime);

											//物流信息
											JSONArray deliverInfo = new JSONArray();
											JSONObject body = new JSONObject();
											body.put("expressType", "21");
											body.put("expressTypeName", express_name);
											body.put("expressBillNo", express_no);										
											deliverInfo.add(body);
											js.put("deliverInfo", deliverInfo);

											String request = js.toString();
											String result = HttpSend.MicroMarkSend(request, order_eId, "OrderStatusUpdate",order_chanelid);
											body=null;
											deliverInfo=null;
											js=null;
										}						


										StaticInfo.dao.useTransactionProcessData(lstData);	
									}
									else 
									{
										String errorCode=resjsobject.getString("errorCode");//错误代码
										String subErrorCode=resjsobject.getString("subErrorCode");//子错误diam
										String errorDesc=resjsobject.getString("errorDesc");//错误描述
										String subErrorDesc=resjsobject.getString("subErrorDesc");//子错误描述									

										logger.error("\r\n*********管易云订单物流信息查询EcGuanyiyuanDeliverysGet EID="+order_eId+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"调用管易云接口返回错误：errorCode="+errorCode+",subErrorCode="+subErrorCode+",errorDesc="+errorDesc+",subErrorDesc="+subErrorDesc+"************\r\n");
									}
								}
								else 
								{
									String tempError=sb.toString();			
									logger.error("\r\n*********管易云订单物流信息查询EcGuanyiyuanDeliverysGet EID="+order_eId+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"调用管易云接口返回错误："+tempError+"************\r\n");
								}							
								sb.setLength(0);
								sb=null;
							}	
						} 
						catch (Exception e) 
						{
							logger.error("\r\n*********管易云订单物流信息查询EcGuanyiyuanDeliverysGet EID="+order_eId+",SHOP="+order_shopno+",LOADDOCTYPE="+order_load_doctype+",ORDERNO="+order_no+"发生异常错误："+e.getMessage()+"************\r\n");
						}							
					}			
				}
				else 
				{
					logger.info("\r\n*********管易云订单物流信息查询EcGuanyiyuanDeliverysGet定时调用没有需要处理的订单************\r\n");
				}
			}
			else 
			{
				logger.info("\r\n*********管易云订单物流信息查询EcGuanyiyuanDeliverysGet定时调用dcp_outsaleset表中没有管易物流配置:************\r\n");
			}			

		} 
		catch (Exception e) 
		{
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);			

				pw.flush();
				pw.close();			

				errors.flush();
				errors.close();

				logger.error("\r\n******管易云订单物流信息查询EcGuanyiyuanDeliverysGet报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******管易云订单物流信息查询EcGuanyiyuanDeliverysGet报错信息" + e1.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="管易云订单物流信息查询EcGuanyiyuanDeliverysGet错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********管易云订单物流信息查询EcGuanyiyuanDeliverysGet定时调用End:************\r\n");
		}
		return sReturnInfo;
	}

}
