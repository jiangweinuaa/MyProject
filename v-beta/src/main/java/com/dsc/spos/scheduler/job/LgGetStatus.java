package com.dsc.spos.scheduler.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.FtpUtils;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ec.Shopee;
import com.dsc.spos.utils.logistics.Cvs;
import com.dsc.spos.utils.logistics.Egs;
import com.dsc.spos.utils.logistics.GreenWorld;
import com.dsc.spos.utils.logistics.Htc;
import com.dsc.spos.utils.logistics.SevenEleven;

//物流状态追踪
//获取物流状态
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LgGetStatus extends InitJob
{


	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(LgGetStatus.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中


	public LgGetStatus()
	{

	}	

	public LgGetStatus(String eId,String shopId,String organizationNO, String billNo)
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
			logger.info("\r\n*********【物流状态追踪】正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-【物流状态追踪】正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********【物流状态追踪】定时调用Start:************\r\n");

		//虾皮调用
		Shopee shopee=null;			

		Egs egs=null;
		Htc htc=null;
		Cvs cvs=null;
		SevenEleven se=null;
		GreenWorld greenWorld=null;

		//訂單日誌時間
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String orderStatusLogTimes=dfDatetime.format(cal.getTime());

		try 
		{
			//这里是 1:新建 2：已发货 3：退货 4：换货 5：已安排物流取件 6：已完成
			//lgplatformno=1是自配送，不需要
			String sql="select * from DCP_shipment t where ((t.expressno is not null and t.status=5) or (t.GREENWORLD_RTNORDERNO is not null) ) and t.status='100' and lgplatformno!='1' ";
			List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
			if (sqllist != null && sqllist.isEmpty() == false)
			{

				//插入单头
				String[] columnsTrack = {
						"EID",
						"SHOPID",
						"SHIPMENTNO",
						"LGPLATFORMNO",
						"LGPLATFORMNAME",
						"ITEM",
						"SHIPDATE",
						"SHIPTIME",
						"DESCRIPTION",
						"STATUS",
				};


				//接單日誌
				String[] columnsORDER_STATUSLOG = 
					{ 
							"EID", 
							"ORGANIZATIONNO", 
							"SHOPID", 
							"ORDERNO", 
							"LOAD_DOCTYPE",
							"STATUSTYPE", 
							"STATUSTYPENAME", 
							"STATUS", 
							"STATUSNAME", 
							"NEED_NOTIFY", 
							"NOTIFY_STATUS",
							"NEED_CALLBACK", 
							"CALLBACK_STATUS", 
							"OPNO", 
							"OPNAME", 
							"UPDATE_TIME",
							"MEMO", 
							"STATUS" 
					};


				//标记下载文件成功
				boolean bDownLoadOk=false;
				//黑猫货态
				List<Map<String, Object>> listExpress=null;
				
				int countSize=sqllist.size();
				for (int i = 0; i < countSize; i++) 
				{
					try 
					{
						String lgPlatformNo=sqllist.get(i).get("LGPLATFORMNO").toString();
						String lgPlatformName=sqllist.get(i).get("LGPLATFORMNAME").toString();
						String expressBiltype=sqllist.get(i).get("EXPRESSBILLTYPE").toString();//托运单别
						String address=sqllist.get(i).get("RECEIVER_ADDRESS").toString();//收货地址
						String shipCompany=sqllist.get(i).get("EID").toString();//货运单企业代码
						String shipShop=sqllist.get(i).get("SHOPID").toString();//货运单门店
						String shipmentNo=sqllist.get(i).get("SHIPMENTNO").toString();//货运单号
						String ecPlatformNo=sqllist.get(i).get("ECPLATFORMNO").toString();//电商平台代码
						String ecOrderNo=sqllist.get(i).get("EC_ORDERNO").toString();//电商订单号
						String expressNo=sqllist.get(i).get("EXPRESSNO").toString();//物流单号
						String deliveryType=sqllist.get(i).get("DELIVERYTYPE").toString();//物流类型
						String ecPlatformName=sqllist.get(i).get("ECPLATFORMNAME").toString();//
						String allLogisticsID=sqllist.get(i).get("GREENWORLD_LOGISTICSID").toString();//
						String rtnExpressno=sqllist.get(i).get("GREENWORLD_RTNORDERNO").toString();//逆物流单号
						String rtnallLogisticsID=sqllist.get(i).get("GREENWORLD_RTNLOGISTICSID").toString();//逆物流綠界单号

						//电商平台
						String sqlEC="select * from OC_ECOMMERCE A where A.status='100' and EID='"+shipCompany+"' ";
						List<Map<String, Object>> getDataEC=this.doQueryData(sqlEC, null);

						//查询物流厂商API信息
						String sqlLG="select * from OC_logistics  where status='100' and EID='"+shipCompany+"' ";			
						List<Map<String, Object>> getLGData=this.doQueryData(sqlLG, null);			


						//虾皮已经集成的物流商,直接调用虾皮API接口
						if (ecPlatformNo.equals("shopee") && deliveryType.equals("7")&& deliveryType.equals("8") && deliveryType.equals("9")&& deliveryType.equals("10") && deliveryType.equals("11")&& deliveryType.equals("12")&& deliveryType.equals("13")) 
						{
							shopee=new Shopee();

							//检查电商资料是否存在
							Map<String, Object> map_condition = new HashMap<String, Object>();
							map_condition.put("ECPLATFORMNO", ecPlatformNo);		
							List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getDataEC,map_condition,false);	
							if (getQHeader1!=null && getQHeader1.size()>0) 
							{
								for (Map<String, Object> map : getQHeader1) 
								{
									String apiUrl=map.get("API_URL").toString();
									int partner_id=Integer.parseInt(map.get("PARTNER_ID").toString());
									int shop_id= Integer.parseInt(map.get("STORE_ID").toString());
									String partner_key=map.get("PARTNER_KEY").toString();
									String resbody="";
									if (rtnExpressno.equals("")) 
									{
										resbody=shopee.GetLogisticsMessage(apiUrl, partner_id, partner_key, shop_id, ecOrderNo, expressNo);
									}
									else 
									{
										resbody=shopee.GetLogisticsMessage(apiUrl, partner_id, partner_key, shop_id, ecOrderNo, rtnExpressno);
									}

									JSONObject jsonres = new JSONObject(resbody);
									//
									//String request_id=jsonres.getString("request_id");		
									if(jsonres.has("error"))//错误
									{
										String errorno=jsonres.getString("error");
										String errormsg=jsonres.getString("msg");

										logger.error("\r\n******【物流状态追踪】调用虾皮接口GetLogisticsMessage失败,错误代码="+errorno +"，错误信息="+errormsg+"，电商平台=" + ecPlatformNo + "，货运单="+shipmentNo+"******\r\n");
									}
									else
									{
										String tracking_no=jsonres.getString("tracking_number");

										JSONArray tracking_info=jsonres.getJSONArray("tracking_info");

										//列表SQL
										List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

										//找出之前的物流追踪
										String sqlTrack="select * from OC_SHIPMENT_TRACK where EID='"+shipCompany+"' and SHOPID ='"+shipShop+"' and shipmentno='"+shipmentNo+"' order by item ";			
										List<Map<String, Object>> getLGDataTrack=this.doQueryData(sqlTrack, null);			

										//
										int itmeCount=0;
										if (getLGDataTrack!=null && getLGDataTrack.size()>0) 
										{
											itmeCount=Integer.parseInt(getLGDataTrack.get(getLGDataTrack.size()-1).get("ITEM").toString());
										}

										for (int a = 0; a < tracking_info.length(); a++) 
										{
										
											int ctime=tracking_info.getJSONObject(a).getInt("ctime");
											String description=tracking_info.getJSONObject(a).getString("description");
											String status=tracking_info.getJSONObject(a).getString("status");


											SimpleDateFormat format =  new SimpleDateFormat("yyyyMMdd"); //设置格式
											String sDate=format.format(ctime);  
											format =  new SimpleDateFormat("HHmmss"); //设置格式
											String sTime=format.format(ctime);  


											//
											Map<String, Object> map_conditionTrack = new HashMap<String, Object>();
											map_conditionTrack.put("SHIPDATE", sDate);	
											map_conditionTrack.put("SHIPTIME", sTime);	
											List<Map<String, Object>> getQH=MapDistinct.getWhereMap(getLGDataTrack,map_conditionTrack,false);	
											if (getQH==null || getQH.size()==0) 
											{
												itmeCount+=1;

												DataValue[] insValueTrack =new DataValue[]
														{
																new DataValue(shipCompany, Types.VARCHAR), 	
																new DataValue(shipShop, Types.VARCHAR), 	
																new DataValue(shipmentNo, Types.VARCHAR), 	
																new DataValue(lgPlatformNo, Types.VARCHAR), 	
																new DataValue(lgPlatformName, Types.VARCHAR), 	
																new DataValue(itmeCount, Types.INTEGER), 	
																new DataValue(sDate, Types.VARCHAR), 	
																new DataValue(sTime, Types.VARCHAR), 	
																new DataValue(description, Types.VARCHAR), 	
																new DataValue("100", Types.VARCHAR), 	
														};
												InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
												ibTrack.addValues(insValueTrack);
												//
												lstData.add(new DataProcessBean(ibTrack));		

											}		
											map_conditionTrack=null;
											getQH=null;

											//刪除狀態日誌
											DelBean db1 = new DelBean("OC_ORDER_STATUSLOG");										
											db1.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
											db1.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
											db1.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
											db1.addCondition("UPDATE_TIME", new DataValue(sDate+sTime+"000", Types.VARCHAR));
											//
											lstData.add(new DataProcessBean(db1));

											if (status.equals("PICKED_UP"))//PICKED_UP：待配送
											{
												//接單日誌
												DataValue[] insValueOrderStatus_LOG = new DataValue[] 
														{ 
																new DataValue(shipCompany, Types.VARCHAR),
																new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																new DataValue(ecOrderNo, Types.VARCHAR), //
																new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																new DataValue("待配送", Types.VARCHAR), // 状态名称
																new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																new DataValue("admin", Types.VARCHAR), //操作員編碼
																new DataValue("管理員", Types.VARCHAR), //操作員名稱
																new DataValue(sDate+sTime+"000", Types.VARCHAR), //yyyyMMddHHmmssSSS
																new DataValue("配送狀態-->待配送", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																new DataValue("100", Types.VARCHAR) 
														};
												InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
												ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
												//
												lstData.add(new DataProcessBean(ibOrderStatusLog));
											}
											else if (status.equals("DELIVERED")) //  DELIVERED：配送
											{
												UptBean ubsp = new UptBean("OC_SHIPMENT");	
												//条件
												ubsp.addCondition("EID",new DataValue(shipCompany, Types.VARCHAR));
												ubsp.addCondition("SHOPID",new DataValue(shipShop, Types.VARCHAR));
												ubsp.addCondition("SHIPMENTNO",new DataValue(shipmentNo, Types.VARCHAR));
												//值
												ubsp.addUpdateValue("STATUS", new DataValue(6, Types.INTEGER));//更新 已完成

												lstData.add(new DataProcessBean(ubsp));		

												//接單日誌
												DataValue[] insValueOrderStatus_LOG = new DataValue[] 
														{ 
																new DataValue(shipCompany, Types.VARCHAR),
																new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																new DataValue(ecOrderNo, Types.VARCHAR), //
																new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																new DataValue("11", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																new DataValue("已完成", Types.VARCHAR), // 状态名称
																new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																new DataValue("admin", Types.VARCHAR), //操作員編碼
																new DataValue("管理員", Types.VARCHAR), //操作員名稱
																new DataValue(sDate+sTime+"000", Types.VARCHAR), //yyyyMMddHHmmssSSS
																new DataValue("配送狀態-->已完成", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																new DataValue("100", Types.VARCHAR) 
														};
												InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
												ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
												//
												lstData.add(new DataProcessBean(ibOrderStatusLog));

											}
											else 
											{
												//接單日誌
												DataValue[] insValueOrderStatus_LOG = new DataValue[] 
														{ 
																new DataValue(shipCompany, Types.VARCHAR),
																new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																new DataValue(ecOrderNo, Types.VARCHAR), //
																new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																new DataValue("7", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																new DataValue(description, Types.VARCHAR), // 状态名称
																new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																new DataValue("admin", Types.VARCHAR), //操作員編碼
																new DataValue("管理員", Types.VARCHAR), //操作員名稱
																new DataValue(sDate+sTime+"000", Types.VARCHAR), //yyyyMMddHHmmssSSS
																new DataValue("配送狀態-->" +description , Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																new DataValue("100", Types.VARCHAR) 
														};
												InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
												ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
												//
												lstData.add(new DataProcessBean(ibOrderStatusLog));

											}

											StaticInfo.dao.useTransactionProcessData(lstData);
										}
										getLGDataTrack=null;
									}
								}

							}
							else 
							{
								logger.error("\r\n******【物流状态追踪】电商平台资料未设置，电商平台=" + ecPlatformNo + "，货运单="+shipmentNo+"******\r\n");
							}
							getQHeader1=null;
							map_condition=null;
						}
						else //其他平台，或者是虾皮平台，但是虾皮未集成的物流厂商
						{
							if (lgPlatformNo.equals("htc")) //新竹物流
							{
								//
								Map<String, Object> map_condition = new HashMap<String, Object>();
								map_condition.put("LGPLATFORMNO", "htc");		
								List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
								if (getQHeader1!=null && getQHeader1.size()>0) 
								{
									for (Map<String, Object> map : getQHeader1) 
									{
										String apiUrlTwo=map.get("API_URLTWO").toString();//
										String eId=map.get("EID").toString();
										String privateKey=map.get("PRIVATEKEY").toString();
										String iv=map.get("IV").toString();
										String v=map.get("V").toString();

										//如果新竹物流状态接口地址为空，就不执行接口调用
										if (apiUrlTwo.equals("")) 
										{
											logger.error("\r\n******【物流状态追踪】发货单号shipmentNo=" + shipmentNo + "，新竹物流地址API_URLTWO为空,不执行接口调用******\r\n");
											continue;
										}

										htc=new Htc();

										String[] expressNoList=new String[1];
										expressNoList[0]=expressNo;
										String resbody= htc.GetHtcStatus_Xml(apiUrlTwo, privateKey, iv, v, expressNoList);
										Document document = DocumentHelper.parseText(resbody); //将字符串转为XML


										//找出之前的物流追踪
										String sqlTrack="select * from OC_SHIPMENT_TRACK where eId='"+shipCompany+"' and SHOPID ='"+shipShop+"' and shipmentno='"+shipmentNo+"' order by item ";			
										List<Map<String, Object>> getLGDataTrack=this.doQueryData(sqlTrack, null);			

										//
										int itmeCount=0;
										if (getLGDataTrack!=null && getLGDataTrack.size()>0) 
										{
											itmeCount=Integer.parseInt(getLGDataTrack.get(getLGDataTrack.size()-1).get("ITEM").toString());
										}


										Element rootElt = document.getRootElement(); //获取根节点
										Iterator iter = rootElt.elementIterator("orders"); //获取根节点下的子节点orders 
										while (iter.hasNext()) 
										{
											Element order = (Element) iter.next();
											String ordersid = order.attributeValue("ordersid"); 
											//System.out.println(ordersid);
											//
											Iterator iterOrder=order.elementIterator("order");

											//列表SQL
											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
											int a=0;
											while (iterOrder.hasNext()) 
											{
												Element orderStatus = (Element) iterOrder.next();
												String orderStatusid = orderStatus.attributeValue("orderid"); 
												String orderStatuswrktime = orderStatus.attributeValue("wrktime"); 
												String orderStatusstatus = orderStatus.attributeValue("status"); 
												//System.out.println(orderStatusid);
												//System.out.println(orderStatuswrktime);
												//System.out.println(orderStatusstatus);										

												SimpleDateFormat format =  new SimpleDateFormat("yyyyMMddHHmmss"); //设置格式
												Date sDatetime = format.parse(orderStatuswrktime);
												format =  new SimpleDateFormat("yyyyMMdd"); //设置格式
												String sDate=format.format(sDatetime.getTime());  
												format =  new SimpleDateFormat("HHmmss"); //设置格式
												String sTime=format.format(sDatetime.getTime());  
												//System.out.println(sDate);	
												//System.out.println(sTime);	
												format=null;

												//
												Map<String, Object> map_conditionTrack = new HashMap<String, Object>();
												map_conditionTrack.put("SHIPDATE", sDate);	
												map_conditionTrack.put("SHIPTIME", sTime);	
												List<Map<String, Object>> getQH=MapDistinct.getWhereMap(getLGDataTrack,map_conditionTrack,false);	
												if (getQH==null || getQH.size()==0) 
												{
													itmeCount+=1;

													DataValue[] insValueTrack =new DataValue[]
															{
																	new DataValue(shipCompany, Types.VARCHAR), 	
																	new DataValue(shipShop, Types.VARCHAR), 	
																	new DataValue(shipmentNo, Types.VARCHAR), 	
																	new DataValue(lgPlatformNo, Types.VARCHAR), 	
																	new DataValue(lgPlatformName, Types.VARCHAR), 	
																	new DataValue(a, Types.INTEGER), 	
																	new DataValue(sDate, Types.VARCHAR), 	
																	new DataValue(sTime, Types.VARCHAR), 	
																	new DataValue(orderStatusstatus, Types.VARCHAR), 	
																	new DataValue("100", Types.VARCHAR), 	
															};
													InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
													ibTrack.addValues(insValueTrack);
													//
													lstData.add(new DataProcessBean(ibTrack));	

												}										
												map_conditionTrack=null;
												getQH=null;

												//刪除狀態日誌
												DelBean db1 = new DelBean("OC_ORDER_STATUSLOG");										
												db1.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
												db1.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
												db1.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
												db1.addCondition("UPDATE_TIME", new DataValue(sDate+sTime+"000", Types.VARCHAR));
												//
												lstData.add(new DataProcessBean(db1));

												if (orderStatusstatus.contains("配送中")) 
												{
													//接單日誌
													DataValue[] insValueOrderStatus_LOG = new DataValue[] 
															{ 
																	new DataValue(shipCompany, Types.VARCHAR),
																	new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																	new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																	new DataValue(ecOrderNo, Types.VARCHAR), //
																	new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																	new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																	new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																	new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																	new DataValue("待配送", Types.VARCHAR), // 状态名称
																	new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																	new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																	new DataValue("admin", Types.VARCHAR), //操作員編碼
																	new DataValue("管理員", Types.VARCHAR), //操作員名稱
																	new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																	new DataValue("配送狀態-->待配送", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																	new DataValue("100", Types.VARCHAR) 
															};
													InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
													ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
													//
													lstData.add(new DataProcessBean(ibOrderStatusLog));

												}
												//簽收
												else if (orderStatusstatus.contains("送達")) 
												{					
													UptBean ubsp = new UptBean("OC_SHIPMENT");	
													//条件
													ubsp.addCondition("EID",new DataValue(shipCompany, Types.VARCHAR));
													ubsp.addCondition("SHOPID",new DataValue(shipShop, Types.VARCHAR));
													ubsp.addCondition("SHIPMENTNO",new DataValue(shipmentNo, Types.VARCHAR));
													//值
													ubsp.addUpdateValue("STATUS", new DataValue(6, Types.INTEGER));//更新 已完成

													lstData.add(new DataProcessBean(ubsp));	

													// 更新订单表
													UptBean ubec = new UptBean("OC_ORDER");
													ubec.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
													ubec.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));// 0需调度
													// 1.订单开立
													// 2.已接单
													// 3.已拒单
													// 4.生产接单
													// 5.生产拒单
													// 6.完工入库
													// 7.内部调拨
													// 8.待提货
													// 9.待配送
													// 10.已发货
													// 11.已完成
													// 12.已退单
													// 13.电商已点货
													// 14开始制作
													lstData.add(new DataProcessBean(ubec));
													ubec = null;


													//接單日誌
													DataValue[] insValueOrderStatus_LOG = new DataValue[] 
															{ 
																	new DataValue(shipCompany, Types.VARCHAR),
																	new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																	new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																	new DataValue(ecOrderNo, Types.VARCHAR), //
																	new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																	new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																	new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																	new DataValue("11", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																	new DataValue("已完成", Types.VARCHAR), // 状态名称
																	new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																	new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																	new DataValue("admin", Types.VARCHAR), //操作員編碼
																	new DataValue("管理員", Types.VARCHAR), //操作員名稱
																	new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																	new DataValue("配送狀態-->已完成", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																	new DataValue("100", Types.VARCHAR) 
															};
													InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
													ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
													//
													lstData.add(new DataProcessBean(ibOrderStatusLog));
												}	
												else 
												{
													//接單日誌
													DataValue[] insValueOrderStatus_LOG = new DataValue[] 
															{ 
																	new DataValue(shipCompany, Types.VARCHAR),
																	new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																	new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																	new DataValue(ecOrderNo, Types.VARCHAR), //
																	new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																	new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																	new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																	new DataValue("7", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																	new DataValue(orderStatusstatus, Types.VARCHAR), // 状态名称
																	new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																	new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																	new DataValue("admin", Types.VARCHAR), //操作員編碼
																	new DataValue("管理員", Types.VARCHAR), //操作員名稱
																	new DataValue(sDate+sTime+"000", Types.VARCHAR), //yyyyMMddHHmmssSSS
																	new DataValue("配送狀態-->" +orderStatusstatus , Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																	new DataValue("100", Types.VARCHAR) 
															};
													InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
													ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
													//
													lstData.add(new DataProcessBean(ibOrderStatusLog));

												}

												StaticInfo.dao.useTransactionProcessData(lstData);

											}
										}
										document=null;
										getLGDataTrack=null;
									}

								}
								getQHeader1=null;
								map_condition=null;
							}		
							else if (lgPlatformNo.equals("egs"))//黑猫FTP SOD货况追踪
							{
								//列表SQL
								List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

								//
								Map<String, Object> map_condition = new HashMap<String, Object>();
								map_condition.put("LGPLATFORMNO", "egs");		
								List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
								if (getQHeader1!=null && getQHeader1.size()>0) 
								{
									for (Map<String, Object> map : getQHeader1) 
									{
										//這個FTP地址是 IP 沒有FTP開頭
										String apiUrl=map.get("API_URLTWO").toString();//
										if (apiUrl.toLowerCase().startsWith("ftp://")) 
										{
											apiUrl=apiUrl.substring(6);										
										}

										String eId=map.get("EID").toString();
										String customer_id=map.get("CUSTOMERNO").toString();									
										String ftpuid=map.get("CVS_FTPUID").toString();
										String ftppwd=map.get("CVS_FTPPWD").toString();									

										SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
										String sDate=dfDate.format(cal.getTime());
										dfDate = new SimpleDateFormat("HHmmss");
										String sTime=dfDate.format(cal.getTime());

										//
										dfDate = new SimpleDateFormat("MMddHH");
										String MMddHH=dfDate.format(cal.getTime());
								

										String localpath=System.getProperty("catalina.home")+"\\webapps\\LG\\egs\\download";	

										File file=new File(localpath);
										if (!file.exists()) 
										{
											file.mkdirs();
										}

										//本地备份文件
										File bakfile=new File(localpath+"/bak");
										if (!bakfile.exists()) 
										{
											bakfile.mkdirs();
										}
										bakfile=null;


										//这里是循环，不用每次都去下载
										//文件名无法确定我们就下载所有.SOD文件	
										boolean bRet=false;
										if (bDownLoadOk==false) 
										{
											bRet=downloadFile(apiUrl, 21, ftpuid, ftppwd, "Receive", "", localpath,".SOD");	
										}					
										
										//防止插入失败，文件还在，下次下载失败也可以处理
										boolean bExistFile=false;
										File [] files = file.listFiles();
										for (int Ei = 0; Ei < files.length; Ei++) 
										{
											if (files[Ei].isFile()) 
											{
												bExistFile=true;
												break;
											}
										}
										files=null;
										
										//
										if (bRet||bDownLoadOk||bExistFile) 
										{											
											egs=new Egs();
											
											//
											if (bDownLoadOk==false) 
											{
												//讀取黑貓貨態信息
												listExpress=egs.SOD();
											}											

											egs=null;											
											bDownLoadOk=true;
											//
											Map<String, Object> mapExpressCond = new HashMap<String, Object>();
											mapExpressCond.put("expressno", expressNo);		
											List<Map<String, Object>> getQHeaderExpress=MapDistinct.getWhereMap(listExpress,mapExpressCond,true);	

											if (getQHeaderExpress!=null && getQHeaderExpress.size()>0) 
											{
												//
												String sqlOrderStatusLog="select * from OC_ORDER_STATUSLOG where EID='"+shipCompany+"' and SHOPID='"+shipShop+"' and orderno='"+ecOrderNo+"' ";
												List<Map<String, Object>> getStatusLog=this.doQueryData(sqlOrderStatusLog, null);

												//												
												Calendar calEGS = Calendar.getInstance();// 获得当前时间
												SimpleDateFormat dfDatetimeEGS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
												String orderStatusLogTimesEGS=dfDatetimeEGS.format(calEGS.getTime());
												
												for (Map<String, Object> map2 : getQHeaderExpress) 
												{							
													//毫秒+1
													calEGS.add(Calendar.MILLISECOND, 1);
													dfDatetimeEGS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
													orderStatusLogTimesEGS=dfDatetimeEGS.format(calEGS.getTime());
													
													//貨態描述
													String orderStatusstatus=map2.get("trackDesc").toString();
													String orderStatusstatusID=map2.get("trackId").toString();	
													String orderStatussitename=map2.get("sitename").toString();	
													String orderStatusinputDate=map2.get("inputDate").toString();	

													//过滤是否已经存在此日志记录
													Map<String, Object> mapLogCond = new HashMap<String, Object>();
													
													//黑猫状态竟然还有重复,已完成的，后续竟然还有开立的，妈的什么玩意
													mapLogCond.put("STATUS", "11");		
													List<Map<String, Object>> getQHeaderLog=MapDistinct.getWhereMap(getStatusLog,mapLogCond,false,1);	
													mapLogCond.clear();													
													if (getQHeaderLog==null || getQHeaderLog.size()==0) 
													{
														mapLogCond.put("MEMO", orderStatusstatus);		
														getQHeaderLog=MapDistinct.getWhereMap(getStatusLog,mapLogCond,false,1);	
														mapLogCond.clear();													
														
														if (getQHeaderLog==null || getQHeaderLog.size()==0) 
														{
							

															//貨態編號
															if (orderStatusstatusID.equals("00001")) 
															{
																//接單日誌
																DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																		{ 
																				new DataValue(shipCompany, Types.VARCHAR),
																				new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																				new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																				new DataValue(ecOrderNo, Types.VARCHAR), //
																				new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																				new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																				new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																				new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																				new DataValue("待配送", Types.VARCHAR), // 状态名称
																				new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																				new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																				new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																				new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																				new DataValue("admin", Types.VARCHAR), //操作員編碼
																				new DataValue("管理員", Types.VARCHAR), //操作員名稱
																				new DataValue(orderStatusLogTimesEGS, Types.VARCHAR), //yyyyMMddHHmmssSSS
																				new DataValue("配送狀態-->待配送(" +orderStatusstatusID+ orderStatusstatus+"["+orderStatussitename+orderStatusinputDate+"])", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																				new DataValue("100", Types.VARCHAR) 
																		};
																InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
																ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
																//
																lstData.add(new DataProcessBean(ibOrderStatusLog));

															}
															//簽收
															else if (orderStatusstatusID.equals("00003")) 
															{					
																UptBean ubsp = new UptBean("OC_SHIPMENT");	
																//条件
																ubsp.addCondition("EID",new DataValue(shipCompany, Types.VARCHAR));
																ubsp.addCondition("SHOPID",new DataValue(shipShop, Types.VARCHAR));
																ubsp.addCondition("SHIPMENTNO",new DataValue(shipmentNo, Types.VARCHAR));
																//值
																ubsp.addUpdateValue("STATUS", new DataValue(6, Types.INTEGER));//更新 已完成

																lstData.add(new DataProcessBean(ubsp));	


																// 更新订单表
																UptBean ubec = new UptBean("OC_ORDER");
																ubec.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
																ubec.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));// 0需调度
																// 1.订单开立
																// 2.已接单
																// 3.已拒单
																// 4.生产接单
																// 5.生产拒单
																// 6.完工入库
																// 7.内部调拨
																// 8.待提货
																// 9.待配送
																// 10.已发货
																// 11.已完成
																// 12.已退单
																// 13.电商已点货
																// 14开始制作
																lstData.add(new DataProcessBean(ubec));
																ubec = null;


																//接單日誌
																DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																		{ 
																				new DataValue(shipCompany, Types.VARCHAR),
																				new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																				new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																				new DataValue(ecOrderNo, Types.VARCHAR), //
																				new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																				new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																				new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																				new DataValue("11", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																				new DataValue("已完成", Types.VARCHAR), // 状态名称
																				new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																				new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																				new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																				new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																				new DataValue("admin", Types.VARCHAR), //操作員編碼
																				new DataValue("管理員", Types.VARCHAR), //操作員名稱
																				new DataValue(orderStatusLogTimesEGS, Types.VARCHAR), //yyyyMMddHHmmssSSS
																				new DataValue("配送狀態-->已完成("+orderStatusstatusID+orderStatusstatus+"["+orderStatussitename+orderStatusinputDate+"])", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																				new DataValue("100", Types.VARCHAR) 
																		};
																InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
																ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
																//
																lstData.add(new DataProcessBean(ibOrderStatusLog));
															}	
															else 
															{
																//接單日誌
																DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																		{ 
																				new DataValue(shipCompany, Types.VARCHAR),
																				new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																				new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																				new DataValue(ecOrderNo, Types.VARCHAR), //
																				new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																				new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																				new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																				new DataValue("7", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																				new DataValue(orderStatusstatus, Types.VARCHAR), // 状态名称
																				new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																				new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																				new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																				new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																				new DataValue("admin", Types.VARCHAR), //操作員編碼
																				new DataValue("管理員", Types.VARCHAR), //操作員名稱
																				new DataValue(orderStatusLogTimesEGS, Types.VARCHAR), //yyyyMMddHHmmssSSS
																				new DataValue("配送狀態-->(" +orderStatusstatusID+orderStatusstatus+"["+orderStatussitename+orderStatusinputDate+"])" , Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																				new DataValue("100", Types.VARCHAR) 
																		};
																InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
																ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
																//
																lstData.add(new DataProcessBean(ibOrderStatusLog));

															}
														}
													}	
													else 
													{
														break;//已配送完成的直接跳出去
													}

												}	
											}				

											StaticInfo.dao.useTransactionProcessData(lstData);										

											//
											if (lstData.size()>0) 
											{
												//********本地文件备份处理**********
												String[] names = file.list();																						
												for (int si = 0; si < names.length; si++) 
												{
													File tempFile=new File(localpath + "/" + names[si]);

													//备份文件
													File dstFile=new File(localpath + "/bak/" + names[si]);	

													if (tempFile.isFile()) 
													{
														FileInputStream is = new FileInputStream(tempFile);

														FileOutputStream os = new FileOutputStream(dstFile);

														byte[] buf = new byte[1024];

														while (is.read(buf) != -1) 
														{
															os.write(buf);
														}

														buf=null;

														os.close();
														is.close();

														//删除文件
														tempFile.delete();
													}												
													dstFile=null;												
													tempFile=null;												
												}
											}
								

										}

									}

								}

							}
							else if (lgPlatformNo.equals("cvs")) //便利达康
							{
								//出貨流程：F10(訂單上傳)--F03(大物流驗收檔)--F44(進店即時檔)--F05(取貨完成檔)
								//退貨流程：F61(訂單預退檔)--F84(離店檔)--F07(大物流驗退檔)


								//1.大物流验收,F03档------22:30	-----item=1						
								//2.进店即时档,F44档------每整点10分----item=2
								//3.取货完成档,F05档------22:00-----item=3

								//列表SQL
								List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

								//
								Map<String, Object> map_condition = new HashMap<String, Object>();
								map_condition.put("LGPLATFORMNO", "cvs");		
								List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
								if (getQHeader1!=null && getQHeader1.size()>0) 
								{
									for (Map<String, Object> map : getQHeader1) 
									{
										String apiUrl=map.get("API_URL").toString();//
										String eId=map.get("EID").toString();
										String motherVendorno=map.get("CVS_MOTHERVENDORNO").toString();
										String collectno=map.get("CVS_COLLECTNO").toString();
										String largelogisticsno=map.get("CVS_LARGELOGISTICSNO").toString();
										String ftpuid=map.get("CVS_FTPUID").toString();
										String ftppwd=map.get("CVS_FTPPWD").toString();
										String mapwebsite=map.get("CVS_MAPWEBSITE").toString();
										String ourwebsite=map.get("CVS_OURWEBSITE").toString();


										SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
										String sDate=dfDate.format(cal.getTime());
										dfDate = new SimpleDateFormat("HHmmss");
										String sTime=dfDate.format(cal.getTime());

										//item的3笔记录
										String sqlFXX="SELECT * FROM OC_SHIPMENT_TRACK T WHERE T.EID='"+shipCompany+"' AND T.SHOPID='"+shipShop+"' AND T.SHIPMENTNO='"+shipmentNo+"' AND (ITEM=1 OR ITEM=2 OR ITEM=3 ) ";
										List<Map<String, Object>> getExistFXX=this.doQueryData(sqlFXX, null);		

										cvs=new Cvs();

										FtpUtils ftp=new FtpUtils();
										ftp.setHostname(apiUrl);
										//测试暂时注销
										//ftp.setPort(8821);
										ftp.setUsername(ftpuid);
										ftp.setPassword(ftppwd);										

										//1.大物流验收档
										String fileID="F03";
										String filename=fileID+motherVendorno+"CVS"+sDate+".xml";
										String localpath=System.getProperty("catalina.home")+"\\webapps\\LG\\cvs\\download";	

										boolean bRet=ftp.downloadFile(fileID, filename, localpath);								
										if (bRet) 
										{						
											//1.大物流验收档
											List<Map<String, Object>> lstF03= cvs.F03(localpath);

											Map<String, Object> conditionValues = new HashMap<String, Object>();
											conditionValues.put("f03_expressno", expressNo);	

											List<Map<String, Object>> getDataF03=MapDistinct.getWhereMap(lstF03, conditionValues, false);
											if (getDataF03!=null && getDataF03.size()>0) 
											{
												String f03_expressno=getDataF03.get(0).get("f03_expressno").toString();
												String f03_ecno=getDataF03.get(0).get("f03_ecno").toString();
												String f03_distributeno=getDataF03.get(0).get("f03_distributeno").toString();
												String f03_distributename=getDataF03.get(0).get("f03_distributename").toString();


												conditionValues = new HashMap<String, Object>();
												conditionValues.put("ITEM", 1);
												List<Map<String, Object>> getExistF03=MapDistinct.getWhereMap(getExistFXX, conditionValues, false);	

												//过滤重复插入
												if (getExistF03!=null && getExistF03.size()==0) 
												{
													DataValue[] insValueTrack =new DataValue[]
															{
																	new DataValue(shipCompany, Types.VARCHAR), 	
																	new DataValue(shipShop, Types.VARCHAR), 	
																	new DataValue(shipmentNo, Types.VARCHAR), 	
																	new DataValue(lgPlatformNo, Types.VARCHAR), 	
																	new DataValue(lgPlatformName, Types.VARCHAR), 	
																	new DataValue(1, Types.INTEGER),//F03固定写1
																	new DataValue(sDate, Types.VARCHAR), 	
																	new DataValue(sTime, Types.VARCHAR), 	
																	new DataValue("大物流中心驗收完成", Types.VARCHAR), 	
																	new DataValue("100", Types.VARCHAR), 	
															};
													InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
													ibTrack.addValues(insValueTrack);
													//
													lstData.add(new DataProcessBean(ibTrack));

													//接單日誌
													DataValue[] insValueOrderStatus_LOG = new DataValue[] 
															{ 
																	new DataValue(shipCompany, Types.VARCHAR),
																	new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																	new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																	new DataValue(ecOrderNo, Types.VARCHAR), //
																	new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																	new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																	new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																	new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																	new DataValue("待配送", Types.VARCHAR), // 状态名称
																	new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																	new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																	new DataValue("admin", Types.VARCHAR), //操作員編碼
																	new DataValue("管理員", Types.VARCHAR), //操作員名稱
																	new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																	new DataValue("配送狀態-->物流中心已驗收", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																	new DataValue("100", Types.VARCHAR) 
															};
													InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
													ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
													//
													lstData.add(new DataProcessBean(ibOrderStatusLog));

												}	
												getExistF03=null;
											}
											getDataF03=null;
											conditionValues=null;	
											lstF03=null;
										}

										//2.进店即时档
										fileID="F44";
										filename=fileID+motherVendorno+"CVS"+sDate+".xml";
										localpath=System.getProperty("catalina.home")+"\\webapps\\LG\\cvs\\download";	

										bRet=ftp.downloadFile(fileID, filename, localpath);								
										if (bRet) 
										{						
											//2.进店即时档
											List<Map<String, Object>> lstF44= cvs.F44(localpath);

											Map<String, Object> conditionValues = new HashMap<String, Object>();
											conditionValues.put("f44_expressno", expressNo);	

											List<Map<String, Object>> getDataF44=MapDistinct.getWhereMap(lstF44, conditionValues, false);
											if (getDataF44!=null && getDataF44.size()>0) 
											{
												String f44_expressno=getDataF44.get(0).get("f44_expressno").toString();
												String f44_ecno=getDataF44.get(0).get("f44_ecno").toString();
												String f44_distributeno=getDataF44.get(0).get("f44_distributeno").toString();
												String f44_distributename=getDataF44.get(0).get("f44_distributename").toString();
												String f44_getshopno=getDataF44.get(0).get("f44_getshopno").toString();
												String f44_sdate=getDataF44.get(0).get("f44_sdate").toString();


												conditionValues = new HashMap<String, Object>();
												conditionValues.put("ITEM", 2);
												List<Map<String, Object>> getExistF44=MapDistinct.getWhereMap(getExistFXX, conditionValues, false);	

												//过滤重复插入
												if (getExistF44!=null && getExistF44.size()==0) 
												{
													DataValue[] insValueTrack =new DataValue[]
															{
																	new DataValue(shipCompany, Types.VARCHAR), 	
																	new DataValue(shipShop, Types.VARCHAR), 	
																	new DataValue(shipmentNo, Types.VARCHAR), 	
																	new DataValue(lgPlatformNo, Types.VARCHAR), 	
																	new DataValue(lgPlatformName, Types.VARCHAR), 	
																	new DataValue(2, Types.INTEGER),//F44固定写2
																	new DataValue(sDate, Types.VARCHAR), 	
																	new DataValue(sTime, Types.VARCHAR), 	
																	new DataValue("已到達門店：" + f44_distributename+f44_getshopno , Types.VARCHAR), 	
																	new DataValue("100", Types.VARCHAR), 	
															};
													InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
													ibTrack.addValues(insValueTrack);
													//
													lstData.add(new DataProcessBean(ibTrack));

													//接單日誌
													DataValue[] insValueOrderStatus_LOG = new DataValue[] 
															{ 
																	new DataValue(shipCompany, Types.VARCHAR),
																	new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																	new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																	new DataValue(ecOrderNo, Types.VARCHAR), //
																	new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																	new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																	new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																	new DataValue("8", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																	new DataValue("待提貨", Types.VARCHAR), // 状态名称
																	new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																	new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																	new DataValue("admin", Types.VARCHAR), //操作員編碼
																	new DataValue("管理員", Types.VARCHAR), //操作員名稱
																	new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																	new DataValue("配送狀態-->待提貨", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																	new DataValue("100", Types.VARCHAR) 
															};
													InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
													ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
													//
													lstData.add(new DataProcessBean(ibOrderStatusLog));

												}	
												getExistF44=null;
											}
											getDataF44=null;
											conditionValues=null;	
											lstF44=null;
										}

										//3.取货完成档

										fileID="F05";
										filename=fileID+motherVendorno+"CVS"+sDate+".xml";
										localpath=System.getProperty("catalina.home")+"\\webapps\\LG\\cvs\\download";	

										bRet=ftp.downloadFile(fileID, filename, localpath);								
										if (bRet) 
										{						
											//3.取货完成档,
											List<Map<String, Object>> lstF05= cvs.F05(localpath);

											Map<String, Object> conditionValues = new HashMap<String, Object>();
											conditionValues.put("f05_expressno", expressNo);	

											List<Map<String, Object>> getDataF05=MapDistinct.getWhereMap(lstF05, conditionValues, false);
											if (getDataF05!=null && getDataF05.size()>0) 
											{
												String f05_expressno=getDataF05.get(0).get("f05_expressno").toString();
												String f05_distributeno=getDataF05.get(0).get("f05_distributeno").toString();
												String f05_distributename=getDataF05.get(0).get("f05_distributename").toString();
												String f05_getshopno=getDataF05.get(0).get("f05_getshopno").toString();
												String f05_sdate=getDataF05.get(0).get("f05_sdate").toString();
												String f05_bdate=getDataF05.get(0).get("f05_bdate").toString();

												conditionValues = new HashMap<String, Object>();
												conditionValues.put("ITEM", 3);
												List<Map<String, Object>> getExistF05=MapDistinct.getWhereMap(getExistFXX, conditionValues, false);	

												//过滤重复插入
												if (getExistF05!=null && getExistF05.size()==0) 
												{
													DataValue[] insValueTrack =new DataValue[]
															{
																	new DataValue(shipCompany, Types.VARCHAR), 	
																	new DataValue(shipShop, Types.VARCHAR), 	
																	new DataValue(shipmentNo, Types.VARCHAR), 	
																	new DataValue(lgPlatformNo, Types.VARCHAR), 	
																	new DataValue(lgPlatformName, Types.VARCHAR), 	
																	new DataValue(3, Types.INTEGER),//F05固定写3
																	new DataValue(sDate, Types.VARCHAR), 	
																	new DataValue(sTime, Types.VARCHAR), 	
																	new DataValue("已簽收" , Types.VARCHAR), 	
																	new DataValue("100", Types.VARCHAR), 	
															};
													InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
													ibTrack.addValues(insValueTrack);
													//
													lstData.add(new DataProcessBean(ibTrack));

													//***更新物流已完成
													UptBean ubsp = new UptBean("OC_SHIPMENT");	
													//条件
													ubsp.addCondition("EID",new DataValue(shipCompany, Types.VARCHAR));
													ubsp.addCondition("SHOPID",new DataValue(shipShop, Types.VARCHAR));
													ubsp.addCondition("SHIPMENTNO",new DataValue(shipmentNo, Types.VARCHAR));
													//值
													ubsp.addUpdateValue("STATUS", new DataValue(6, Types.INTEGER));//更新 已完成

													lstData.add(new DataProcessBean(ubsp));	

													// 更新订单表
													UptBean ubec = new UptBean("OC_ORDER");
													ubec.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
													ubec.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));// 0需调度
													// 1.订单开立
													// 2.已接单
													// 3.已拒单
													// 4.生产接单
													// 5.生产拒单
													// 6.完工入库
													// 7.内部调拨
													// 8.待提货
													// 9.待配送
													// 10.已发货
													// 11.已完成
													// 12.已退单
													// 13.电商已点货
													// 14开始制作
													lstData.add(new DataProcessBean(ubec));
													ubec = null;

													//接單日誌
													DataValue[] insValueOrderStatus_LOG = new DataValue[] 
															{ 
																	new DataValue(shipCompany, Types.VARCHAR),
																	new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																	new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																	new DataValue(ecOrderNo, Types.VARCHAR), //
																	new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																	new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																	new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																	new DataValue("11", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																	new DataValue("已完成", Types.VARCHAR), // 状态名称
																	new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																	new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																	new DataValue("admin", Types.VARCHAR), //操作員編碼
																	new DataValue("管理員", Types.VARCHAR), //操作員名稱
																	new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																	new DataValue("配送狀態-->已完成", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																	new DataValue("100", Types.VARCHAR) 
															};
													InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
													ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
													//
													lstData.add(new DataProcessBean(ibOrderStatusLog));

												}	
												getExistF05=null;
											}
											getDataF05=null;
											conditionValues=null;	
											lstF05=null;
										}
										getExistFXX=null;
										ftp=null;
									}

								}
								else 
								{
									logger.error("\r\n******【物流状态追踪】发货单号shipmentNo=" + shipmentNo + "，便利达康基础资料为空,不执行接口调用******\r\n");
								}
								getQHeader1=null;
								map_condition=null;

								//执行
								StaticInfo.dao.useTransactionProcessData(lstData);
							}
							else if (lgPlatformNo.equals("dzt")) //大智通
							{
								//出貨流程：SIN(訂單上傳)--FILEOK/SRP(訂單回復檔)--ETA(出貨通知)--EIN(物流中心驗收)--PPS(到店檔)--OL(商品代收檔)
								//退貨流程：ERT(預定退貨通知)--PPS(離店資料)--EDR(物流退貨驗收通知)--EVR(廠退資料)

								//1.订单出货通知,ETA档-----------item=1      ------08:40、12:50、13:45
								//2.大物流进货验收,EIN档--------item=2      ------06:00
								//3.商品到店，PPS档 -------item=3      ------06:00 起至 23:00 每隔一小時一次
								//4.取货完成后，代收，OL档-----item=4      ------10:00
								//
								//列表SQL
								List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

								Map<String, Object> map_condition = new HashMap<String, Object>();
								map_condition.put("LGPLATFORMNO", "dzt");		
								List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
								if (getQHeader1!=null && getQHeader1.size()>0) 
								{
									for (Map<String, Object> map : getQHeader1) 
									{
										String apiUrl=map.get("API_URL").toString();//
										String eId=map.get("EID").toString();
										String motherVendorno=map.get("DZT_MOTHERVENDORNO").toString();
										String sonVendorno=map.get("DZT_SONVENDORNO").toString();
										String ftpuid=map.get("DZT_FTPUID").toString();
										String ftppwd=map.get("DZT_FTPPWD").toString();
										String mapwebsite=map.get("DZT_MAPWEBSITE").toString();

										SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
										String sDate=dfDate.format(cal.getTime());
										dfDate = new SimpleDateFormat("HHmmss");
										String sTime=dfDate.format(cal.getTime());

										//item的4笔记录
										String sqlFXX="SELECT * FROM OC_SHIPMENT_TRACK T WHERE T.EID='"+shipCompany+"' AND T.SHOPID='"+shipShop+"' AND T.SHIPMENTNO='"+shipmentNo+"' AND (ITEM=1 OR ITEM=2 OR ITEM=3 OR ITEM=4 ) ";
										List<Map<String, Object>> getExistFXX=this.doQueryData(sqlFXX, null);		

										se=new SevenEleven();

										FtpUtils ftp=new FtpUtils();
										ftp.setHostname(apiUrl);
										//默认端口
										ftp.setPort(21);
										ftp.setUsername(ftpuid);
										ftp.setPassword(ftppwd);
										
										//1.订单出货通知,ETA档
										String fileID="ETA";

										String filename=motherVendorno+sonVendorno+sDate;
										String localpath=System.getProperty("catalina.home")+"\\webapps\\LG\\dzt\\download";						

										//获取所以文件
										List<String> ftpFiles=ftp.getFileInfo(fileID,filename);
										for (int fj = 0; fj < ftpFiles.size(); fj++) 
										{									
											filename= ftpFiles.get(fj);
											File tempFile = new File(localpath+"\\"+filename);
											if (tempFile.exists()==false) 
											{
												boolean bRet=ftp.downloadFile(fileID, filename, localpath);								
												if (bRet) 
												{						
													//1.订单出货通知,ETA档
													List<Map<String, Object>> lstETA= se.ETA(localpath);

													Map<String, Object> conditionValues = new HashMap<String, Object>();
													conditionValues.put("eta_expressno", expressNo);	

													List<Map<String, Object>> getDataETA=MapDistinct.getWhereMap(lstETA, conditionValues, false);
													if (getDataETA!=null && getDataETA.size()>0) 
													{
														String eta_expressno=getDataETA.get(0).get("eta_expressno").toString();
														String eta_replycode=getDataETA.get(0).get("eta_replycode").toString();
														String eta_replyname=getDataETA.get(0).get("eta_replyname").toString();
														//描述
														String eta_status="";
														if (eta_replycode.equals("02001") || eta_replycode.equals("02002")) 
														{
															eta_status="大智通ETA出貨通知-成功";
														}
														else 
														{
															eta_status="大智通ETA出貨通知-失敗，" + eta_replyname;
														}												

														conditionValues = new HashMap<String, Object>();
														conditionValues.put("ITEM", 1);
														List<Map<String, Object>> getExistETA=MapDistinct.getWhereMap(getExistFXX, conditionValues, false);	

														//过滤重复插入
														if (getExistETA!=null && getExistETA.size()==0) 
														{
															DataValue[] insValueTrack =new DataValue[]
																	{
																			new DataValue(shipCompany, Types.VARCHAR), 	
																			new DataValue(shipShop, Types.VARCHAR), 	
																			new DataValue(shipmentNo, Types.VARCHAR), 	
																			new DataValue(lgPlatformNo, Types.VARCHAR), 	
																			new DataValue(lgPlatformName, Types.VARCHAR), 	
																			new DataValue(1, Types.INTEGER),//ETA固定写1 	
																			new DataValue(sDate, Types.VARCHAR), 	
																			new DataValue(sTime, Types.VARCHAR), 	
																			new DataValue(eta_status, Types.VARCHAR), 	
																			new DataValue("100", Types.VARCHAR), 	
																	};
															InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
															ibTrack.addValues(insValueTrack);
															//
															lstData.add(new DataProcessBean(ibTrack));

															//接單日誌
															DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																	{ 
																			new DataValue(shipCompany, Types.VARCHAR),
																			new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																			new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																			new DataValue(ecOrderNo, Types.VARCHAR), //
																			new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																			new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																			new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																			new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																			new DataValue("待配送", Types.VARCHAR), // 状态名称
																			new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																			new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																			new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																			new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																			new DataValue("admin", Types.VARCHAR), //操作員編碼
																			new DataValue("管理員", Types.VARCHAR), //操作員名稱
																			new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																			new DataValue("配送狀態-->物流已出貨通知", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																			new DataValue("100", Types.VARCHAR) 
																	};
															InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
															ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
															//
															lstData.add(new DataProcessBean(ibOrderStatusLog));

														}	
														getExistETA=null;
													}
													getDataETA=null;
													conditionValues=null;	
													lstETA=null;
												}		
											}
										}


										//2.大物流进货验收,EIN档
										fileID="EIN";

										filename=motherVendorno+sonVendorno+sDate;
										localpath=System.getProperty("catalina.home")+"\\webapps\\LG\\dzt\\download";						

										//获取文件
										ftpFiles=ftp.getFileInfo(fileID,filename);
										for (int fj = 0; fj < ftpFiles.size(); fj++) 
										{									
											filename= ftpFiles.get(fj);
											File tempFile = new File(localpath+"\\"+filename);
											if (tempFile.exists()==false) 
											{
												boolean bRet=ftp.downloadFile(fileID, filename, localpath);								
												if (bRet) 
												{						
													//2.大物流进货验收,EIN档
													List<Map<String, Object>> lstEIN= se.EIN(localpath);

													Map<String, Object> conditionValues = new HashMap<String, Object>();
													conditionValues.put("eta_expressno", expressNo);	

													List<Map<String, Object>> getDataEIN=MapDistinct.getWhereMap(lstEIN, conditionValues, false);
													if (getDataEIN!=null && getDataEIN.size()>0) 
													{
														String ein_expressno=getDataEIN.get(0).get("ein_expressno").toString();

														//验收日期
														String ein_sdate=getDataEIN.get(0).get("ein_sdate").toString();
														if (ein_sdate.length()==10) 
														{
															ein_sdate=ein_sdate.substring(0,4) +ein_sdate.substring(5,7) + ein_sdate.substring(8,10);
														}
														else 
														{
															ein_sdate=sDate;
														}
														//00:成功
														String ein_status=getDataEIN.get(0).get("ein_status").toString();
														String ein_statusname=getDataEIN.get(0).get("ein_statusname").toString();
														//描述
														String ein_statusDecr="";
														if (ein_status.equals("00")) 
														{
															ein_statusDecr="大智通EIN大物流中心驗收-成功";
														}
														else 
														{
															ein_statusDecr="大智通EIN大物流中心驗收-失敗，" + ein_statusname;
														}												

														conditionValues = new HashMap<String, Object>();
														conditionValues.put("ITEM", 2);
														List<Map<String, Object>> getExistEIN=MapDistinct.getWhereMap(getExistFXX, conditionValues, false);	

														//过滤重复插入
														if (getExistEIN!=null && getExistEIN.size()==0) 
														{
															DataValue[] insValueTrack =new DataValue[]
																	{
																			new DataValue(shipCompany, Types.VARCHAR), 	
																			new DataValue(shipShop, Types.VARCHAR), 	
																			new DataValue(shipmentNo, Types.VARCHAR), 	
																			new DataValue(lgPlatformNo, Types.VARCHAR), 	
																			new DataValue(lgPlatformName, Types.VARCHAR), 	
																			new DataValue(2, Types.INTEGER),//EIN固定写2
																			new DataValue(ein_sdate, Types.VARCHAR), 	
																			new DataValue(sTime, Types.VARCHAR), 	
																			new DataValue(ein_statusDecr, Types.VARCHAR), 	
																			new DataValue("100", Types.VARCHAR), 	
																	};
															InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
															ibTrack.addValues(insValueTrack);
															//
															lstData.add(new DataProcessBean(ibTrack));

															//接單日誌
															DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																	{ 
																			new DataValue(shipCompany, Types.VARCHAR),
																			new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																			new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																			new DataValue(ecOrderNo, Types.VARCHAR), //
																			new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																			new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																			new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																			new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																			new DataValue("待配送", Types.VARCHAR), // 状态名称
																			new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																			new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																			new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																			new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																			new DataValue("admin", Types.VARCHAR), //操作員編碼
																			new DataValue("管理員", Types.VARCHAR), //操作員名稱
																			new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																			new DataValue("配送狀態-->物流中心已驗收", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																			new DataValue("100", Types.VARCHAR) 
																	};
															InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
															ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
															//
															lstData.add(new DataProcessBean(ibOrderStatusLog));

														}	
														getExistEIN=null;
													}
													getDataEIN=null;
													conditionValues=null;	
													lstEIN=null;
												}		
											}
										}


										//3.商品到店，PPS档 
										fileID="PPS";

										filename=motherVendorno+sonVendorno+sDate;
										localpath=System.getProperty("catalina.home")+"\\webapps\\LG\\dzt\\download";						

										//获取文件
										ftpFiles=ftp.getFileInfo(fileID,filename);
										for (int fj = 0; fj < ftpFiles.size(); fj++) 
										{									
											filename= ftpFiles.get(fj);
											File tempFile = new File(localpath+"\\"+filename);
											if (tempFile.exists()==false) 
											{
												boolean bRet=ftp.downloadFile(fileID, filename, localpath);								
												if (bRet) 
												{						
													//2.大物流进货验收,EIN档
													List<Map<String, Object>> lstPPS= se.PPS(localpath);

													Map<String, Object> conditionValues = new HashMap<String, Object>();
													conditionValues.put("pps_expressno", expressNo);	

													List<Map<String, Object>> getDataPPS=MapDistinct.getWhereMap(lstPPS, conditionValues, false);
													if (getDataPPS!=null && getDataPPS.size()>0) 
													{
														String pps_expressno=getDataPPS.get(0).get("pps_expressno").toString();												
														String pps_sdate=getDataPPS.get(0).get("pps_sdate").toString();	
														String pps_stime=getDataPPS.get(0).get("pps_stime").toString();	
														String pps_sgetshopno=getDataPPS.get(0).get("pps_sgetshopno").toString();	
														String pps_status=getDataPPS.get(0).get("pps_status").toString();	
														String pps_statusname=getDataPPS.get(0).get("pps_statusname").toString();												

														//描述
														String ein_statusDecr="";
														if (pps_status.equals("101")||pps_status.equals("102")) 
														{
															ein_statusDecr="大智通PPS商品配達門店-成功";
														}
														else 
														{
															ein_statusDecr="大智通PPS商品配達門店-失敗，" + pps_statusname;
														}												

														conditionValues = new HashMap<String, Object>();
														conditionValues.put("ITEM", 3);
														List<Map<String, Object>> getExistPPS=MapDistinct.getWhereMap(getExistFXX, conditionValues, false);	

														//过滤重复插入
														if (getExistPPS!=null && getExistPPS.size()==0) 
														{
															DataValue[] insValueTrack =new DataValue[]
																	{
																			new DataValue(shipCompany, Types.VARCHAR), 	
																			new DataValue(shipShop, Types.VARCHAR), 	
																			new DataValue(shipmentNo, Types.VARCHAR), 	
																			new DataValue(lgPlatformNo, Types.VARCHAR), 	
																			new DataValue(lgPlatformName, Types.VARCHAR), 	
																			new DataValue(3, Types.INTEGER),//PPS固定写3
																			new DataValue(pps_sdate, Types.VARCHAR), 	
																			new DataValue(pps_stime, Types.VARCHAR), 	
																			new DataValue(ein_statusDecr, Types.VARCHAR), 	
																			new DataValue("100", Types.VARCHAR), 	
																	};
															InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
															ibTrack.addValues(insValueTrack);
															//
															lstData.add(new DataProcessBean(ibTrack));

															//接單日誌
															DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																	{ 
																			new DataValue(shipCompany, Types.VARCHAR),
																			new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																			new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																			new DataValue(ecOrderNo, Types.VARCHAR), //
																			new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																			new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																			new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																			new DataValue("8", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																			new DataValue("待提貨", Types.VARCHAR), // 状态名称
																			new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																			new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																			new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																			new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																			new DataValue("admin", Types.VARCHAR), //操作員編碼
																			new DataValue("管理員", Types.VARCHAR), //操作員名稱
																			new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																			new DataValue("配送狀態-->待提貨", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																			new DataValue("100", Types.VARCHAR) 
																	};
															InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
															ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
															//
															lstData.add(new DataProcessBean(ibOrderStatusLog));

														}	
														getExistPPS=null;
													}
													getDataPPS=null;
													conditionValues=null;	
													lstPPS=null;
												}		
											}
										}


										//4.取货完成后，代收，OL档
										fileID="OL";

										filename=fileID+motherVendorno+sonVendorno+"0."+sDate;
										localpath=System.getProperty("catalina.home")+"\\webapps\\LG\\dzt\\download";

										File tempFile = new File(localpath+"\\"+filename);
										if (tempFile.exists()==false) 
										{
											boolean bRet=ftp.downloadFile(fileID, filename, localpath);								
											if (bRet) 
											{						
												//4.取货完成后，代收，OL档
												List<Map<String, Object>> lstOL= se.OL(localpath);

												Map<String, Object> conditionValues = new HashMap<String, Object>();
												conditionValues.put("ol_expressno", expressNo);	

												List<Map<String, Object>> getDataOL=MapDistinct.getWhereMap(lstOL, conditionValues, false);
												if (getDataOL!=null && getDataOL.size()>0) 
												{
													String ol_expressno=getDataOL.get(0).get("ol_expressno").toString();												
													String ol_servicetype=getDataOL.get(0).get("ol_servicetype").toString();	
													String ol_amt=getDataOL.get(0).get("ol_amt").toString();	
													String ol_collectshopno=getDataOL.get(0).get("ol_collectshopno").toString();	
													String ol_exchangeno=getDataOL.get(0).get("ol_exchangeno").toString();	
													String ol_posmachine=getDataOL.get(0).get("ol_posmachine").toString();
													String ol_sdate=getDataOL.get(0).get("ol_sdate").toString();
													String ol_stime=getDataOL.get(0).get("ol_stime").toString();
													String ol_collectno=getDataOL.get(0).get("ol_collectno").toString();
													String ol_collectpartno=getDataOL.get(0).get("ol_collectpartno").toString();
													String ol_collectdate=getDataOL.get(0).get("ol_collectdate").toString();

													conditionValues = new HashMap<String, Object>();
													conditionValues.put("ITEM", 4);
													List<Map<String, Object>> getExistOL=MapDistinct.getWhereMap(getExistFXX, conditionValues, false);	

													//过滤重复插入
													if (getExistOL!=null && getExistOL.size()==0) 
													{
														DataValue[] insValueTrack =new DataValue[]
																{
																		new DataValue(shipCompany, Types.VARCHAR), 	
																		new DataValue(shipShop, Types.VARCHAR), 	
																		new DataValue(shipmentNo, Types.VARCHAR), 	
																		new DataValue(lgPlatformNo, Types.VARCHAR), 	
																		new DataValue(lgPlatformName, Types.VARCHAR), 	
																		new DataValue(4, Types.INTEGER),//OL固定写4
																		new DataValue(ol_sdate, Types.VARCHAR), 	
																		new DataValue(ol_stime, Types.VARCHAR), 	
																		new DataValue("已簽收", Types.VARCHAR), 	
																		new DataValue("100", Types.VARCHAR), 	
																};
														InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
														ibTrack.addValues(insValueTrack);
														//
														lstData.add(new DataProcessBean(ibTrack));


														//***更新物流已完成
														UptBean ubsp = new UptBean("OC_SHIPMENT");	
														//条件
														ubsp.addCondition("EID",new DataValue(shipCompany, Types.VARCHAR));
														ubsp.addCondition("SHOPID",new DataValue(shipShop, Types.VARCHAR));
														ubsp.addCondition("SHIPMENTNO",new DataValue(shipmentNo, Types.VARCHAR));
														//值
														ubsp.addUpdateValue("STATUS", new DataValue(6, Types.INTEGER));//更新 已完成

														lstData.add(new DataProcessBean(ubsp));	

														// 更新订单表
														UptBean ubec = new UptBean("OC_ORDER");
														ubec.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
														ubec.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));// 0需调度
														// 1.订单开立
														// 2.已接单
														// 3.已拒单
														// 4.生产接单
														// 5.生产拒单
														// 6.完工入库
														// 7.内部调拨
														// 8.待提货
														// 9.待配送
														// 10.已发货
														// 11.已完成
														// 12.已退单
														// 13.电商已点货
														// 14开始制作
														lstData.add(new DataProcessBean(ubec));
														ubec = null;


														//接單日誌
														DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																{ 
																		new DataValue(shipCompany, Types.VARCHAR),
																		new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																		new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																		new DataValue(ecOrderNo, Types.VARCHAR), //
																		new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
																		new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																		new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
																		new DataValue("11", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																		new DataValue("已完成", Types.VARCHAR), // 状态名称
																		new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																		new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																		new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																		new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																		new DataValue("admin", Types.VARCHAR), //操作員編碼
																		new DataValue("管理員", Types.VARCHAR), //操作員名稱
																		new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																		new DataValue("配送狀態-->已完成", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																		new DataValue("100", Types.VARCHAR) 
																};
														InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
														ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
														//
														lstData.add(new DataProcessBean(ibOrderStatusLog));

													}	
													getExistOL=null;
												}
												getDataOL=null;
												conditionValues=null;	
												lstOL=null;
											}		
										}

										ftp=null;
									}

								}
								getQHeader1=null;
								map_condition=null;

								//执行
								StaticInfo.dao.useTransactionProcessData(lstData);
							}
							else if (lgPlatformNo.equals("greenworld")) //绿界
							{
								//
								Map<String, Object> map_condition = new HashMap<String, Object>();
								map_condition.put("LGPLATFORMNO", "greenworld");		
								List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
								if (getQHeader1!=null && getQHeader1.size()>0) 
								{
									for (Map<String, Object> map : getQHeader1) 
									{
										String apiUrl=map.get("API_URL").toString();//
										String cvs_Mode=map.get("CVS_MODE").toString();
										String MerchantID="";
										String HashKey="";
										String HashIV="";

										if (cvs_Mode.equals("1")) 
										{
											MerchantID=map.get("GREENWORLD_MERCHANTID").toString();
											HashKey=map.get("GREENWORLD_HASHKEY").toString();
											HashIV=map.get("GREENWORLD_HASHIV").toString();
										}
										else 
										{
											MerchantID=map.get("GREENWORLD_MERCHANTID_BTOC").toString();
											HashKey=map.get("GREENWORLD_HASHKEY_BTOC").toString();
											HashIV=map.get("GREENWORLD_HASHIV_BTOC").toString();
										}								

										greenWorld=new GreenWorld();						

										String resBody="";
										if (rtnExpressno.equals("")) 
										{
											resBody=greenWorld.QueryLogisticsTradeInfo(apiUrl, MerchantID, HashKey, HashIV, allLogisticsID);
										}
										else 
										{
											resBody=greenWorld.QueryLogisticsTradeInfo(apiUrl, MerchantID, HashKey, HashIV, rtnallLogisticsID);
										}								

										String MerchantTradeNo="";
										String LogisticsStatus="";
										String gwExpressno="";

										String descriptionMemo="";

										if (resBody.contains("AllPayLogisticsID=")) 
										{
											String urlPara=resBody;

											String[] splitStr=urlPara.split("&");			
											for (int a = 0; a < splitStr.length; a++) 
											{
												String[] sTempStr=splitStr[a].split("=");

												if (sTempStr.length>1) 
												{
													if (sTempStr[0].equals("MerchantTradeNo")) 
													{
														MerchantTradeNo=sTempStr[1];
													}
													else if (sTempStr[0].equals("LogisticsStatus")) 
													{
														LogisticsStatus=sTempStr[1];
													}
													else if (sTempStr[0].equals("ShipmentNo")) 
													{
														gwExpressno=sTempStr[1];
													}
													else if (sTempStr[0].equals("BookingNote")) 
													{
														gwExpressno=sTempStr[1];
													}											
												}
											}

											//
											String sqlStatusCode="select * from DCP_shipment_statuscode where STATUSCODE='"+LogisticsStatus+"' ";
											List<Map<String, Object>> getDataStatus=this.doQueryData(sqlStatusCode, null);
											if (getDataStatus != null && getDataStatus.isEmpty() == false)
											{
												descriptionMemo=getDataStatus.get(0).get("STATUSNAME").toString();											
											}
											getDataStatus=null;


											String sqlTrack="select count(*) over() num,t.item,t.description from DCP_shipment_track t "
													+ "where t.shipmentno='"+shipmentNo+"' "
													+ "and t.EID='"+shipCompany+"' "
													+ "and t.SHOPID='"+shipShop+"' ";

											int num=1; 
											List<Map<String, Object>> getDataTrack=this.doQueryData(sqlTrack, null);
											if (getDataTrack != null && getDataTrack.isEmpty() == false)
											{
												Map<String, Object> map_condHQ = new HashMap<String, Object>();
												map_condHQ.put("DESCRIPTION", LogisticsStatus+descriptionMemo);	

												List<Map<String, Object>> getQH=MapDistinct.getWhereMap(getDataTrack,map_condHQ,false);	
												if (getQH!=null && getQH.size()>0) 
												{
													continue;//此物料状态已存在
												}

												getQH=null;
												num=Integer.parseInt(getDataTrack.get(0).get("NUM").toString())+1; 										
											}
											getDataTrack=null;

											SimpleDateFormat format =  new SimpleDateFormat("yyyyMMdd"); //设置格式
											String sDate=format.format(cal.getTime());  
											format =  new SimpleDateFormat("HHmmss"); //设置格式
											String sTime=format.format(cal.getTime());  
											//System.out.println(sDate);	
											//System.out.println(sTime);	
											format=null;

											//列表SQL
											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

											DataValue[] insValueTrack =new DataValue[]
													{
															new DataValue(shipCompany, Types.VARCHAR), 	
															new DataValue(shipShop, Types.VARCHAR), 	
															new DataValue(shipmentNo, Types.VARCHAR), 	
															new DataValue(lgPlatformNo, Types.VARCHAR), 	
															new DataValue(lgPlatformName, Types.VARCHAR), 	
															new DataValue(num, Types.INTEGER), 	
															new DataValue(sDate, Types.VARCHAR), 	
															new DataValue(sTime, Types.VARCHAR), 	
															new DataValue(LogisticsStatus+descriptionMemo, Types.VARCHAR), 	
															new DataValue("100", Types.VARCHAR), 	
													};
											InsBean ibTrack = new InsBean("OC_SHIPMENT_TRACK", columnsTrack);
											ibTrack.addValues(insValueTrack);
											//
											lstData.add(new DataProcessBean(ibTrack));		

											//接單日誌
											DataValue[] insValueOrderStatus_LOG = new DataValue[] 
													{ 
															new DataValue(shipCompany, Types.VARCHAR),
															new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
															new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
															new DataValue(ecOrderNo, Types.VARCHAR), //
															new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
															new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
															new DataValue("配送狀態", Types.VARCHAR), // 状态类型名称
															new DataValue(LogisticsStatus, Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
															new DataValue(descriptionMemo, Types.VARCHAR), // 状态名称
															new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
															new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
															new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
															new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
															new DataValue("admin", Types.VARCHAR), //操作員編碼
															new DataValue("管理員", Types.VARCHAR), //操作員名稱
															new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
															new DataValue("配送狀態-->" +LogisticsStatus+descriptionMemo, Types.VARCHAR), //類型名稱+"-->"+狀態名稱
															new DataValue("100", Types.VARCHAR) 
													};
											InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
											ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
											//
											lstData.add(new DataProcessBean(ibOrderStatusLog));

											//簽收
											if (LogisticsStatus.equals("2067")||LogisticsStatus.equals("3022")) 
											{					
												UptBean ubsp = new UptBean("OC_SHIPMENT");	
												//条件
												ubsp.addCondition("EID",new DataValue(shipCompany, Types.VARCHAR));
												ubsp.addCondition("SHOPID",new DataValue(shipShop, Types.VARCHAR));
												ubsp.addCondition("SHIPMENTNO",new DataValue(shipmentNo, Types.VARCHAR));
												//值
												ubsp.addUpdateValue("STATUS", new DataValue(6, Types.INTEGER));//更新 已完成

												lstData.add(new DataProcessBean(ubsp));		

												// 更新订单表
												UptBean ubec = new UptBean("OC_ORDER");
												ubec.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
												ubec.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));// 0需调度
												// 1.订单开立
												// 2.已接单
												// 3.已拒单
												// 4.生产接单
												// 5.生产拒单
												// 6.完工入库
												// 7.内部调拨
												// 8.待提货
												// 9.待配送
												// 10.已发货
												// 11.已完成
												// 12.已退单
												// 13.电商已点货
												// 14开始制作
												lstData.add(new DataProcessBean(ubec));
												ubec = null;

											}										


											StaticInfo.dao.useTransactionProcessData(lstData);



										}
									}


								}
								getQHeader1=null;
								map_condition=null;														
							}
							else 
							{

							}
						}
						getDataEC=null;
						getLGData=null;

					} 
					catch (Exception e) 
					{

					}
				}
			}
			sqllist=null;
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

				logger.error("\r\n******【物流状态追踪】报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******【物流状态追踪】报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			shopee=null;

			htc=null;
			egs=null;
			cvs=null;
			se=null;

			bRun=false;//
			logger.info("\r\n*********【物流状态追踪】定时调用End:************\r\n");
		}
		return sReturnInfo;		

	}


	/** * 下载文件 * 
	 * @param pathname FTP服务器文件目录 * 
	 * @param filename 文件名称 * 
	 * @param localpath 下载后的文件路径 * 
	 * @param suffixName 后缀名.xls .xlsx
	 * @return */
	private  boolean downloadFile(String hostname,Integer port,String username,String password, String pathname, String filename, String localpath,String... suffixName)
	{ 
		boolean flag = false; 
		OutputStream os=null;
		
		try 
		{
			FTPClient client = new FTPClient();
			client.connect(hostname, port); //连接ftp服务器
			boolean bloginOk=client.login(username, password); //登录ftp服务器
			
			int replyCode = client.getReplyCode(); //是否成功登录服务器
			
			logger.error("\r\nLgGetStatus下载文件,ftp登录login=" +bloginOk + ",服务器响应码replyCode=" +replyCode);
			
			if(!FTPReply.isPositiveCompletion(replyCode))
			{
				logger.error("\r\nLgGetStatus下载文件失败:hostname=" + hostname+",port=" +port +",username="+username+",password="+password);
			}
			else 
			{
				String LOCAL_CHARSET = "GBK";
				// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
				if (FTPReply.isPositiveCompletion(client.sendCommand("OPTS UTF8", "ON"))) 
				{
					LOCAL_CHARSET = "UTF-8";
				}
				client.setControlEncoding(LOCAL_CHARSET);
				client.enterLocalPassiveMode();// 设置被动模式
				client.setFileType(FTPClient.BINARY_FILE_TYPE);// 设置传输的模式

				//切换FTP目录 
				client.changeWorkingDirectory(pathname); 

				//被动模式
				client.enterLocalPassiveMode();

				FTPFile[] ftpFiles = client.listFiles(); 

				List<String> Listsuffix=new ArrayList<String>();
				if (filename.equals("")) 
				{
					for (int iis = 0; iis < suffixName.length; iis++) 
					{
						Listsuffix.add(suffixName[iis]);
					}				
				}			

				for(FTPFile file : ftpFiles)
				{ 
					//下载全部文件
					if (filename.equals("")) 
					{
						if (Listsuffix.size()==0) 
						{
							File localFile = new File(localpath + "/" + file.getName()); 
												
							File bakFile = new File(localpath + "/bak/" + file.getName()); 
							//备份文件
							if (!localFile.exists() && !bakFile.exists()) 
							{
								os = new FileOutputStream(localFile); 		
								String encodeName=new String(file.getName().getBytes("UTF-8"),"iso-8859-1");
								boolean bok=client.retrieveFile(encodeName, os); 						
								if (bok==false) 
								{
									encodeName=new String(file.getName().getBytes("GBK"),"iso-8859-1");
									bok=client.retrieveFile(encodeName, os); 
								}	
								os.close(); 
							}
							bakFile=null;
							
						}
						else 
						{
							int iPos=file.getName().lastIndexOf(".");
							String fileSuffix=file.getName().substring(iPos);
							if (Listsuffix.contains(fileSuffix)) 
							{
								File localFile = new File(localpath + "/" + file.getName()); 
																
								File bakFile = new File(localpath + "/bak/" + file.getName()); 
								//备份文件
								if (!localFile.exists() && !bakFile.exists()) 
								{
									os = new FileOutputStream(localFile); 
									String encodeName=new String(file.getName().getBytes("UTF-8"),"iso-8859-1");
									boolean bok=client.retrieveFile(encodeName, os); 						
									if (bok==false) 
									{
										encodeName=new String(file.getName().getBytes("GBK"),"iso-8859-1");
										bok=client.retrieveFile(encodeName, os); 
									}
									os.close(); 
								}
								bakFile=null;
								
							}					
						}					
					}
					else 
					{
						if(filename.equalsIgnoreCase(file.getName()))
						{ 
							File localFile = new File(localpath + "/" + file.getName()); 
														
							File bakFile = new File(localpath + "/bak/" + file.getName()); 
							//备份文件
							if (!localFile.exists() && !bakFile.exists()) 
							{
								os = new FileOutputStream(localFile); 
								String encodeName=new String(file.getName().getBytes("UTF-8"),"iso-8859-1");
								boolean bok=client.retrieveFile(encodeName, os); 						
								if (bok==false) 
								{
									encodeName=new String(file.getName().getBytes("GBK"),"iso-8859-1");
									bok=client.retrieveFile(encodeName, os); 
								}
								os.close(); 
							}	
							bakFile=null;
							
						} 
					}				
				} 
				
				client.logout(); 
			}
			
			if(client.isConnected())
			{ 
				try
				{
					client.disconnect();
				}
				catch(IOException e)
				{

				}
			} 
			if(null != os)
			{
				try 
				{
					os.close();
				} 
				catch (IOException e) 
				{

				} 
			} 
			
			flag = true; 

		} 
		catch (Exception e) 
		{ 
			logger.error("\r\nLgGetStatus下载文件失败:" + e.getMessage());
		} 
		finally
		{ 
			/*
			if(ftpClient.isConnected())
			{ 
				try
				{
					ftpClient.disconnect();
				}
				catch(IOException e)
				{

				}
			} 
			if(null != os)
			{
				try 
				{
					os.close();
				} 
				catch (IOException e) 
				{

				} 
			} 
			*/
		} 
		return flag; 
	}



}
