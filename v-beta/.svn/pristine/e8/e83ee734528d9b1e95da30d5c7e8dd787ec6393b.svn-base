package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.logistics.Egs;
import com.dsc.spos.utils.logistics.GreenWorld;
import com.dsc.spos.utils.logistics.Htc;

//申请物流单号
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LgGetExpressNo extends InitJob
{
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";
	String pLgplatformno="";

	Logger logger = LogManager.getLogger(FeeCreate.class.getName());
	static boolean bRun=false;//标记此服务是否正在执行中
	public LgGetExpressNo()
	{

	}

	public LgGetExpressNo(String eId,String shopId,String organizationNO, String billNo,String platformno)
	{
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
		pLgplatformno=platformno;
	}


	public String doExe()
	{
		//返回信息
		String sReturnInfo="";
		
		StringBuffer sb=new StringBuffer();
		
		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********【獲取物流單號】正在執行中,本次調用取消：************\r\n");

			sb.append("定時傳輸任務-【獲取物流單號】正在執行中！");
			sReturnInfo=sb.toString();
			
			sb.setLength(0);
			sb=null;
			
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********【獲取物流單號】定時調用Start:************\r\n");

		Egs egs=null;
		Htc htc=null;
		GreenWorld greenworld=null;

		//便利达康的流水号
		int cvsItem=1;


		try 
		{
			//月日时分格式
			SimpleDateFormat myTempdf = new SimpleDateFormat("MMddHHmm");
			Calendar myTempcal = Calendar.getInstance();
			String sMMddHHmm=myTempdf.format(myTempcal.getTime());
			//这里是要过滤掉那些电商已经集成过的物流商(虾皮)
			String sql="select * from DCP_shipment t "
					+ "LEFT JOIN OC_ORDER OC_ORDER  ON t.EID = OC_ORDER.EID AND t.SHOPID = OC_ORDER.SHOPID  AND t.ec_OrderNo = OC_ORDER.orderNo "
					+ "where (t.expressno='' or t.expressno is null) and t.status=1 "
					+ "and ( "
					+ "(t.deliverytype =0 or t.deliverytype =14 or t.deliverytype is null and ECPLATFORMNO='shopee')  "
					+ "or "
					+ "ECPLATFORMNO<>'shopee' "
					+ ") ";

			//根据条件
			if (pBillNo.equals("")==false) 
			{
				sql+=" and t.EID='"+pEId+"' and t.shipmentno='"+pBillNo+"' and t.SHOPID='"+pShop+"' ";
			}

			List<Map<String, Object>> sqllist=this.doQueryData(sql, null);

			//查询物流厂商API信息
			String sqlLG="select * from OC_logistics  where status='100'";		

			//根据条件
			if (pBillNo.equals("")==false) 
			{
				sqlLG+=" and EID='"+pEId+"' and LGPLATFORMNO='"+pLgplatformno+"' ";
			}
			List<Map<String, Object>> getLGData=this.doQueryData(sqlLG, null);
			if (sqllist != null && sqllist.isEmpty() == false)
			{
				int countSize=sqllist.size();

				//記錄優化一下
				List<String> lstCount=new ArrayList<String>();
				String sendName = "";
				String sendAddress = "";
				String sendPhone = "";
				String sendSuDa5 = "";


				//訂單日誌時間
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat sysDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String orderStatusLogTimes=sysDatetime.format(calendar.getTime());

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

				for (int i = 0; i < countSize; i++) 
				{
					String lgPlatformNo=sqllist.get(i).get("LGPLATFORMNO").toString();
					String expressBiltype=sqllist.get(i).get("EXPRESSBILLTYPE").toString();//托运单别
					String address=sqllist.get(i).get("RECEIVER_ADDRESS").toString();//收货地址
					String shipCompany=sqllist.get(i).get("EID").toString();//货运单企业代码
					String shipShop=sqllist.get(i).get("SHOPID").toString();//货运单门店
					String shipmentNo=sqllist.get(i).get("SHIPMENTNO").toString();//货运单号
					String orderno=sqllist.get(i).get("EC_ORDERNO").toString();//訂單號
					String sDeliverytype=sqllist.get(i).get("DELIVERYTYPE").toString();
					String loadtype=sqllist.get(i).get("ECPLATFORMNO").toString();

					//
					lstCount.add(orderno);

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
								String apiUrl=map.get("API_URL").toString();//
								String eId=map.get("EID").toString();
								String lgCompanyno=map.get("LGCOMPANYNO").toString();
								String lgPassword=map.get("LGPASSWORD").toString();

								htc=new Htc();

								String receiver=sqllist.get(i).get("RECEIVER").toString();
								String receiver_phone=sqllist.get(i).get("RECEIVER_MOBILE").toString();
								String pieces=sqllist.get(i).get("PIECES").toString();
								if (PosPub.isNumericType(pieces)==false) 
								{
									pieces="1";
								}
								String weight=sqllist.get(i).get("WEIGHT").toString();
								if (PosPub.isNumericType(weight)==false) 
								{
									weight="1";
								}
								BigDecimal bd=new BigDecimal(weight); 
								if (bd.compareTo(BigDecimal.valueOf(0.0))<=0) 
								{
									weight="1";
								}

								String sender_site=sqllist.get(i).get("SENDER_SITENO").toString();
								String shipdate=sqllist.get(i).get("SHIPDATE").toString();
								String collectAmt=sqllist.get(i).get("COLLECTAMT").toString();
								if (PosPub.isNumericType(collectAmt)==false) 
								{
									collectAmt="0";
								}

								String memo=sqllist.get(i).get("MEMO").toString();
								int temperatelayno=Integer.parseInt(sqllist.get(i).get("TEMPERATELAYERNO").toString());


								String productType="001";
								if (temperatelayno==2) 
								{
									productType="008";
								}
								else if (temperatelayno==3) 
								{
									productType="003";
								}
								else 
								{
									productType="001";
								}

								String resbody=htc.TransData_Json(apiUrl, lgCompanyno, lgPassword, shipmentNo, receiver, receiver_phone, address, pieces, weight, sender_site, shipdate, collectAmt, memo, productType,"","","");

								JSONArray jsonres=new JSONArray(resbody);
								for (int a = 0; a < jsonres.length(); a++) 
								{
									if (jsonres.getJSONObject(0).isNull("ErrMsg")) 
									{
										//System.out.println("HTC新竹物流接口TransData_Json调用失败");

										logger.error("\r\n******【獲取物流單號】orderno=" +orderno+",EID="+lgCompanyno+"Password="+lgPassword+" 新竹物流htc,接口TransData_Json返回失敗******\r\n");
										
										sb.append("******【獲取物流單號】orderno=" +orderno+",EID="+lgCompanyno+"Password="+lgPassword+"新竹物流htc,接口TransData_Json返回失败******");										
									}
									else 
									{
										String ErrMsg=jsonres.getJSONObject(0).getString("ErrMsg");//错误信息

										if (ErrMsg.trim().equals("")==false) 
										{
											//System.out.println("HTC新竹物流接口TransData_Json调用失败=" + ErrMsg);

											logger.error("\r\n******【獲取物流單號】orderno=" +orderno+",EID="+lgCompanyno+"Password="+lgPassword+"新竹物流htc,接口TransData_Json返回失败="+ErrMsg+"******\r\n");
											
											sb.append("******【獲取物流單號】orderno=" +orderno+",EID="+lgCompanyno+"Password="+lgPassword+"新竹物流htc,接口TransData_Json返回失敗="+ErrMsg+"******");
										}
										else 
										{
											String Num=jsonres.getJSONObject(0).getString("Num");//序号
											String success=jsonres.getJSONObject(0).getString("success");//新增 Y 修改 R 失敗 
											String edelno=jsonres.getJSONObject(0).getString("edelno");//托运单号
											String epino=jsonres.getJSONObject(0).getString("epino");//订单编号
											String erstno=jsonres.getJSONObject(0).getString("erstno").trim();//到著站编码
											String eqamt=jsonres.getJSONObject(0).getString("eqamt").trim();//重量
											//String image=jsonres.getJSONObject(0).getString("image");//托运单图片字符串
											////System.out.println(image);

											//更新新竹物流单信息
											UptBean ubec=new UptBean("OC_SHIPMENT");
											ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
											ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
											ubec.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR));

											//
											ubec.addUpdateValue("EXPRESSNO", new DataValue(edelno, Types.VARCHAR));
											ubec.addUpdateValue("RECEIVER_SITENO", new DataValue(erstno, Types.VARCHAR));

											//执行
											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
											lstData.add(new DataProcessBean(ubec));


											//更新新竹物流单信息
											UptBean ubecOrder=new UptBean("OC_ORDER");
											ubecOrder.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
											ubecOrder.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
											ubecOrder.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));
											//
											ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(edelno, Types.VARCHAR));
											ubecOrder.addUpdateValue("RECEIVER_SITENO", new DataValue(erstno, Types.VARCHAR));
											ubecOrder.addUpdateValue("DELIVERYTYPE", new DataValue("15", Types.VARCHAR));
											ubecOrder.addUpdateValue("WEIGHT", new DataValue(1, Types.FLOAT));
											ubecOrder.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 

											lstData.add(new DataProcessBean(ubecOrder));

											//接單日誌
											DataValue[] insValueOrderStatus_LOG = new DataValue[] 
													{ 
															new DataValue(shipCompany, Types.VARCHAR),
															new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
															new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
															new DataValue(orderno, Types.VARCHAR), //
															new DataValue(loadtype, Types.VARCHAR), //電商平台
															new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
															new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
															new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
															new DataValue("待配送", Types.VARCHAR), // 状态名称
															new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
															new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
															new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
															new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
															new DataValue("admin", Types.VARCHAR), //操作員編碼
															new DataValue("管理員", Types.VARCHAR), //操作員名稱
															new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
															new DataValue("訂單狀態-->待配送", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
															new DataValue("100", Types.VARCHAR) 
													};
											InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
											ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
											lstData.add(new DataProcessBean(ibOrderStatusLog));	
											
											StaticInfo.dao.useTransactionProcessData(lstData);

										}
									}

									break;
								}

								htc=null;
							}
		
						}

					}
					else if (lgPlatformNo.equals("egs")) //黑猫宅急便
					{
						//黑猫
						Map<String, Object> map_condition = new HashMap<String, Object>();
						map_condition.put("LGPLATFORMNO", "egs");		
						List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
						if (getQHeader1!=null && getQHeader1.size()>0) 
						{
							for (Map<String, Object> map : getQHeader1) 
							{
								String apiUrl=map.get("API_URL").toString();//
								String eId=map.get("EID").toString();
								String customer_id=map.get("CUSTOMERNO").toString();

								//更新栏位							
								String expressNo="";//托运单号
								String distanceNo="";//距离代码
								String distanceName="";//距离名称
								String receiveSevenCode="";//7码邮号
								String receiveSite="";//到著站
								String suda5_senderpostcode=PosPub.getPARA_SMS(StaticInfo.dao, eId, shipShop, "SenderSuDa5");
								if (suda5_senderpostcode==null ||suda5_senderpostcode.equals("")) 
								{

									suda5_senderpostcode=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "SenderSuDa5");

									if (suda5_senderpostcode==null ||suda5_senderpostcode.equals("")) 
									{
										suda5_senderpostcode="00000";
									}

								}

								//
								if(expressBiltype.equals("A") || expressBiltype.equals("B"))
								{
									//
									egs=new Egs();

									String[] addresslist=new  String[1];
									addresslist[0]=address;
									String resbody=egs.query_suda7_dash(apiUrl, addresslist);

									String[] splitStrings=resbody.split("&");
									if (splitStrings.length>0) 
									{
										//OK|ERROR
										String[] splitStatus=splitStrings[0].split("=");
										String status=splitStatus[1];

										if (status.equals("ERROR")) 
										{
											//message错误信息
											String[] splitMessage=splitStrings[1].split("=");
											String message=splitMessage[1];
											message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
											message = message.replaceAll("\\+", "%2B");  //+
											message = URLDecoder.decode(message, "UTF-8");	
											//System.out.println(message);
											logger.error("\r\n******【獲取物流單號】orderno="+orderno+",黑猫宅急便egs,接口query_suda7_dash返回失败信息="+message+"******\r\n");
										
											sb.append("******【獲取物流單號】orderno="+orderno+",黑猫宅急便egs,接口query_suda7_dash返回失败信息="+message+"******");											
										}
										else 
										{
											//suda7
											String[] splitSuda7=splitStrings[1].split("=");

											if (splitSuda7.length>1) 
											{
												String suda7=splitSuda7[1];
												//33-330-42
												//System.out.println(suda7);
												if (suda7.length()==9) 
												{
													receiveSite=suda7.substring(0,2);											
													receiveSevenCode=suda7.substring(3);

													//收件地址5码邮号
													String suda5_customerpostcode=suda7.substring(3,6)+suda7.substring(7,9);
													if (suda5_customerpostcode.equals("")) 
													{
														suda5_customerpostcode="99999";
													}

													resbody="";
													String[] suda5_senderpostcodelist=new String[1];
													suda5_senderpostcodelist[0]=suda5_senderpostcode;

													String[] suda5_customerpostcodelist=new String[1];
													suda5_customerpostcodelist[0]=suda5_customerpostcode;

													resbody=egs.query_distance(apiUrl, suda5_senderpostcodelist, suda5_customerpostcodelist);

													splitStrings=resbody.split("&");

													if (splitStrings.length>0) 
													{
														//OK|ERROR
														splitStatus=splitStrings[0].split("=");
														status=splitStatus[1];

														if (status.equals("ERROR")) 
														{
															//message错误信息
															String[] splitMessage=splitStrings[1].split("=");
															String message=splitMessage[1];
															message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
															message = message.replaceAll("\\+", "%2B");  //+
															message = URLDecoder.decode(message, "UTF-8");	
															//System.out.println(message);
															logger.error("\r\n******【獲取物流單號】orderno="+orderno+",黑猫宅急便egs,接口query_distance返回失败信息="+message+"******\r\n");
														    sb.append("******【獲取物流單號】orderno="+orderno+",黑猫宅急便egs,接口query_distance返回失败信息="+message+"******");
														}
														else 
														{
															//distance
															String[] splitDistance=splitStrings[1].split("=");
															distanceNo=splitDistance[1];
															//System.out.println(distanceNo);
															if (distanceNo.equals("00")) 
															{
																distanceName="同縣市";
															}
															else if (distanceNo.equals("01")) 
															{
																distanceName="外縣市";
															}
															else if (distanceNo.equals("02")) 
															{
																distanceName="離島";
															}
															else 
															{
																distanceName="";
															}

															resbody="";
															//检查货运单剩余数量
															resbody=egs.query_waybill_id_remain(apiUrl, customer_id, expressBiltype);
															splitStrings=resbody.split("&");

															if (splitStrings.length>0) 
															{
																//OK|ERROR
																splitStatus=splitStrings[0].split("=");
																status=splitStatus[1];

																if (status.equals("ERROR")) 
																{
																	//message错误信息
																	String[] splitMessage=splitStrings[1].split("=");
																	String message=splitMessage[1];
																	message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
																	message = message.replaceAll("\\+", "%2B");  //+
																	message = URLDecoder.decode(message, "UTF-8");	
																	//System.out.println(message);
																	logger.error("\r\n******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_waybill_id_remain返回失败信息="+message+"******\r\n");
																    sb.append("******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_waybill_id_remain返回失败信息="+message+"******");
																}
																else 
																{
																	//waybill_id_remain
																	String[] splitWaybill_id_remain=splitStrings[1].split("=");
																	String waybill_id_remain=splitWaybill_id_remain[1];
																	//System.out.println(waybill_id_remain);

																	//waybill_type
																	//String[] splitWaybill_type=splitStrings[2].split("=");
																	//String rwaybill_type=splitWaybill_type[1];
																	////System.out.println(rwaybill_type);		
																	int countRemain=Integer.parseInt(waybill_id_remain);
																	if (countRemain>0) 
																	{
																		resbody="";
																		//申请1个托运单号
																		resbody=egs.query_waybill_id_range(apiUrl, customer_id, expressBiltype, 1);

																		splitStrings=resbody.split("&");

																		if (splitStrings.length>0) 
																		{
																			//OK|ERROR
																			splitStatus=splitStrings[0].split("=");
																			status=splitStatus[1];

																			if (status.equals("ERROR")) 
																			{
																				//message错误信息
																				String[] splitMessage=splitStrings[1].split("=");
																				String message=splitMessage[1];
																				message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
																				message = message.replaceAll("\\+", "%2B");  //+
																				message = URLDecoder.decode(message, "UTF-8");	
																				//System.out.println(message);
																				logger.error("\r\n******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_waybill_id_range返回失败信息="+message+"******\r\n");
																			    sb.append("******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_waybill_id_range返回失败信息="+message+"******");
																			}
																			else 
																			{
																				//waybill_type
																				//String[] splitWaybill_type=splitStrings[1].split("=");
																				//String rwaybill_type=splitWaybill_type[1];
																				////System.out.println(rwaybill_type);

																				//waybill_id
																				String[] splitwaybill_id=splitStrings[2].split("=");
																				expressNo=splitwaybill_id[1];
																				//System.out.println(expressNo);			

																				//更新黑猫物流单信息
																				UptBean ubec=new UptBean("OC_SHIPMENT");
																				ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
																				ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
																				ubec.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR));

																				//
																				ubec.addUpdateValue("EXPRESSNO", new DataValue(expressNo, Types.VARCHAR));
																				ubec.addUpdateValue("DISTANCENO", new DataValue(distanceNo, Types.VARCHAR));
																				ubec.addUpdateValue("DISTANCENAME", new DataValue(distanceName, Types.VARCHAR));
																				ubec.addUpdateValue("RECEIVER_FIVECODE", new DataValue(suda5_customerpostcode, Types.VARCHAR));
																				ubec.addUpdateValue("RECEIVER_SEVENCODE", new DataValue(suda7, Types.VARCHAR));
																				ubec.addUpdateValue("RECEIVER_SITENO", new DataValue(receiveSite, Types.VARCHAR));

																				//执行
																				List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
																				lstData.add(new DataProcessBean(ubec));

																				//更新黑猫物流单信息
																				UptBean ubecOrder=new UptBean("OC_ORDER");
																				ubecOrder.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
																				ubecOrder.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
																				ubecOrder.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

																				//
																				ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(expressNo, Types.VARCHAR));
																				ubecOrder.addUpdateValue("DISTANCENO", new DataValue(distanceNo, Types.VARCHAR));
																				ubecOrder.addUpdateValue("DISTANCENAME", new DataValue(distanceName, Types.VARCHAR));
																				ubecOrder.addUpdateValue("RECEIVER_FIVECODE", new DataValue(suda5_customerpostcode, Types.VARCHAR));
																				ubecOrder.addUpdateValue("RECEIVER_SEVENCODE", new DataValue(suda7, Types.VARCHAR));
																				ubecOrder.addUpdateValue("RECEIVER_SITENO", new DataValue(receiveSite, Types.VARCHAR));
																				ubecOrder.addUpdateValue("DELIVERYTYPE", new DataValue("9", Types.VARCHAR));					
																				ubecOrder.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
																				lstData.add(new DataProcessBean(ubecOrder));

																				//接單日誌
																				DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																						{ 
																								new DataValue(shipCompany, Types.VARCHAR),
																								new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
																								new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
																								new DataValue(orderno, Types.VARCHAR), //
																								new DataValue(loadtype, Types.VARCHAR), //電商平台
																								new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																								new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
																								new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																								new DataValue("待配送", Types.VARCHAR), // 状态名称
																								new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																								new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																								new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																								new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																								new DataValue("admin", Types.VARCHAR), //操作員編碼
																								new DataValue("管理員", Types.VARCHAR), //操作員名稱
																								new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																								new DataValue("訂單狀態-->待配送", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																								new DataValue("100", Types.VARCHAR) 
																						};
																				InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
																				ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
																				lstData.add(new DataProcessBean(ibOrderStatusLog));	

																				StaticInfo.dao.useTransactionProcessData(lstData);

																			}

																		}
																	}
																	else 
																	{
																		logger.error("\r\n******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,托运单已用完，请联系黑猫从新申请******\r\n");
																		sb.append("******【獲取物流單號】黑猫宅急便egs,托运单已用完，请联系黑猫从新申请******");
																	}

																}

															}	


														}

													}


												}
												else 
												{
													logger.error("\r\n******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_suda7_dash返回值不正确="+suda7+"******\r\n");
													sb.append("******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_suda7_dash返回值不正确="+suda7+"******");
												}
											}
											else 
											{
												logger.error("\r\n******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_suda7_dash返回值不正确，地址信息="+address+"******\r\n");
												sb.append("******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_suda7_dash返回值不正确，地址信息="+address+"******");
											}

										}

									}	
									else 
									{
										logger.error("\r\n******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_suda7_dash返回失败******\r\n");
									    sb.append("******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs,接口query_suda7_dash返回失败******");
									}

									egs=null;

								}		
								else 
								{
									logger.error("\r\n******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs 产生的货运单,托运单别必须是A或B******\r\n");
									sb.append("******【獲取物流單號】orderno="+orderno+",customer_id="+customer_id+",黑猫宅急便egs 产生的货运单,托运单别必须是A或B******");
								}
							}
						
						}
						else 
						{
							logger.error("\r\n******【獲取物流單號】orderno="+orderno+",黑猫宅急便egs货运厂商资料未设置******\r\n");
							sb.append("******【獲取物流單號】orderno="+orderno+",黑猫宅急便egs货运厂商资料未设置******");
						}

					}
					else if (lgPlatformNo.equals("sfexpress")) //顺丰速达
					{

					}
					else if (lgPlatformNo.equals("cvs")) //便利达康
					{

						//累加
						cvsItem=+1;

						//越界处理
						if (cvsItem>999) 
						{
							//分钟增加1
							Date dateTemp= myTempdf.parse(sMMddHHmm);
							myTempcal.setTime(dateTemp);
							//+-分钟
							myTempcal.add(Calendar.MINUTE, 1);
							sMMddHHmm=myTempdf.format(myTempcal.getTime());
							//System.out.println(sMMddHHmm);

							//
							cvsItem=1;
						}

						//不足3位长度
						String sNo=PosPub.FillStr(cvsItem+"", 3, "0", true);

						//8码MMddHHmm+3码流水号=配送编码
						String expressno=sMMddHHmm+sNo;

						//更新便利达康的物流单号
						UptBean ubec=new UptBean("OC_SHIPMENT");
						ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
						ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
						ubec.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR));

						//
						ubec.addUpdateValue("EXPRESSNO", new DataValue(expressno, Types.VARCHAR));

						//执行
						List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
						lstData.add(new DataProcessBean(ubec));

						//更新便利达康的物流单号
						UptBean ubecOrder=new UptBean("OC_ORDER");
						ubecOrder.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
						ubecOrder.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
						ubecOrder.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

						//
						ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(expressno, Types.VARCHAR));
						ubecOrder.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
						lstData.add(new DataProcessBean(ubecOrder));

						//接單日誌
						DataValue[] insValueOrderStatus_LOG = new DataValue[] 
								{ 
										new DataValue(shipCompany, Types.VARCHAR),
										new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
										new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
										new DataValue(orderno, Types.VARCHAR), //
										new DataValue(loadtype, Types.VARCHAR), //電商平台
										new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
										new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
										new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
										new DataValue("待配送", Types.VARCHAR), // 状态名称
										new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
										new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
										new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
										new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
										new DataValue("admin", Types.VARCHAR), //操作員編碼
										new DataValue("管理員", Types.VARCHAR), //操作員名稱
										new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
										new DataValue("訂單狀態-->待配送", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
										new DataValue("100", Types.VARCHAR) 
								};
						InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
						ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
						lstData.add(new DataProcessBean(ibOrderStatusLog));	
						
						StaticInfo.dao.useTransactionProcessData(lstData);

					}
					else if (lgPlatformNo.equals("dzt")) //大智通
					{
						Map<String, Object> dztMap=PosPub.getDZT_Expressno(StaticInfo.dao, shipCompany);
						String dzt_expressno=dztMap.get("expressno").toString();
						String dzt_dztno =dztMap.get("dztno").toString();
						String dzt_status=dztMap.get("status").toString();
						String dzt_end=dztMap.get("dztend").toString();

						if (dzt_expressno.equals("")) 
						{
							logger.error("\r\n******【獲取物流單號】orderno="+orderno+",大智通DZT配送流水号为空******\r\n");
							sb.append("******【獲取物流單號】orderno="+orderno+",大智通DZT配送流水号为空******");
						}
						else 
						{
							List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

							//大智通流水号为8码长度
							String expressnoFormat=PosPub.FillStr(dzt_expressno, 8, "0", true);

							//更新便利达康的物流单号
							UptBean ubec=new UptBean("OC_SHIPMENT");
							ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
							ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
							ubec.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR));

							//格式化
							ubec.addUpdateValue("EXPRESSNO", new DataValue(expressnoFormat, Types.VARCHAR));
							lstData.add(new DataProcessBean(ubec));


							//更新便利达康的物流单号
							UptBean ubecOrder=new UptBean("OC_ORDER");
							ubecOrder.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
							ubecOrder.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
							ubecOrder.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

							//格式化
							ubecOrder.addUpdateValue("DELIVERYNO", new DataValue(expressnoFormat, Types.VARCHAR));
							ubecOrder.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
							lstData.add(new DataProcessBean(ubecOrder));


							//更新状态
							String statusFormat="";

							//如果之前未使用状态
							if (dzt_status.equals("1")) 
							{
								statusFormat="2";//使用中
							}
							else 
							{
								//如果之前使用中状态
								if (dzt_end.equals(dzt_expressno)) 
								{
									statusFormat="3";//使用完
								}
								else 
								{
									statusFormat="2";//使用中
								}
							}

							//更新大智通当前使用流水及状态
							ubec=new UptBean("OC_SHIPBOOKDZT");
							ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
							ubec.addCondition("DZTNO", new DataValue(dzt_dztno, Types.VARCHAR));

							ubec.addUpdateValue("DZT_LASTNO", new DataValue(dzt_expressno, Types.VARCHAR));
							ubec.addUpdateValue("STATUS", new DataValue(statusFormat, Types.VARCHAR));
							lstData.add(new DataProcessBean(ubec));
							
							//接單日誌
							DataValue[] insValueOrderStatus_LOG = new DataValue[] 
									{ 
											new DataValue(shipCompany, Types.VARCHAR),
											new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
											new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
											new DataValue(orderno, Types.VARCHAR), //
											new DataValue(loadtype, Types.VARCHAR), //電商平台
											new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
											new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
											new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
											new DataValue("待配送", Types.VARCHAR), // 状态名称
											new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
											new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
											new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
											new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
											new DataValue("admin", Types.VARCHAR), //操作員編碼
											new DataValue("管理員", Types.VARCHAR), //操作員名稱
											new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
											new DataValue("訂單狀態-->待配送", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
											new DataValue("100", Types.VARCHAR) 
									};
							InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
							ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
							lstData.add(new DataProcessBean(ibOrderStatusLog));	

							//执行							
							StaticInfo.dao.useTransactionProcessData(lstData);
						}
					}
					else if (lgPlatformNo.equals("greenworld")) //綠界
					{
						//查找当前门店的寄件人信息		
						//先取门店参数platform_baseset，取不到就取模板参数platform_basesettemp,union会自动根据第一列排序
						String sqlSenderMsg="select 1 as ID,t.item,t.itemvalue as def from platform_baseset t where item IN ('SenderName','SenderSuDa5','SenderPhone','SenderAddress') AND STATUS='100' " 
								+" and EID = '"+shipCompany+"' and SHOPID='"+shipShop+"' "
								+" union "
								+ " select 2 as ID,t.item,t.def from platform_basesettemp t where item IN ('SenderName','SenderSuDa5','SenderPhone','SenderAddress') AND STATUS='100' "
								+ " and EID = '"+shipShop+"'";

						List<Map<String, Object>> getSenderData=null;
						//查一次就可以了
						if (lstCount.size()==1) 
						{
							getSenderData=this.doQueryData(sqlSenderMsg, null);
						}									

						if(getSenderData.isEmpty() == false)
						{
							for (Map<String, Object> map : getSenderData) 
							{
								String item = map.get("ITEM").toString();
								String itemValue = map.get("DEF").toString();

								if(item.equals("SenderName")&& sendName.equals(""))
								{
									sendName = itemValue ;
									continue;
								}
								if(item.equals("SenderAddress")&& sendAddress.equals(""))
								{
									sendAddress = itemValue ;
									continue;
								}
								if(item.equals("SenderPhone")&& sendPhone.equals(""))
								{
									sendPhone = itemValue ;
									continue;
								}
								if(item.equals("SenderSuDa5")&& sendSuDa5.equals(""))
								{
									sendSuDa5 = itemValue ;
									continue;
								}

							}

						}
						getSenderData=null;

						String receiver=sqllist.get(i).get("GETMAN").toString();
						String receiver_phone=sqllist.get(i).get("GETMANTEL").toString();									
						String paystatus=sqllist.get(i).get("PAYSTATUS").toString();
						String payamt=sqllist.get(i).get("PAYAMT").toString();
						String totamt=sqllist.get(i).get("TOT_AMT").toString();
						String collectAmt="0";
						if (paystatus.equals("3")) 
						{
							collectAmt="0";
						}
						else 
						{
							BigDecimal bdTotamt=new BigDecimal(totamt);
							BigDecimal bdPayamt=new BigDecimal(payamt);

							collectAmt=bdTotamt.subtract(bdPayamt) +"";
						}
						String memo=sqllist.get(i).get("MEMO").toString();
						String getshopno=sqllist.get(i).get("SHIPPINGSHOP").toString();
						String getshopname=sqllist.get(i).get("SHIPPINGSHOPNAME").toString();
						String myprovince=sqllist.get(i).get("PROVINCE").toString();
						String mycity=sqllist.get(i).get("CITY").toString();					

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
								String greenworld_MerchantId=map.get("GREENWORLD_MERCHANTID").toString();
								String greenworld_HashKey=map.get("GREENWORLD_HASHKEY").toString();
								String greenworld_HashIv=map.get("GREENWORLD_HASHIV").toString();										
								String greenworld_OurServerUrl=map.get("GREENWORLD_OURSERVERURL").toString();
								String greenworld_OurServerUrlC2C=map.get("GREENWORLD_OURSERVERURL_CTOC").toString();
								String greenworld_MerchantID_B2C=map.get("GREENWORLD_MERCHANTID_BTOC").toString();
								String greenworld_Hashkey_B2C=map.get("GREENWORLD_HASHKEY_BTOC").toString();
								String greenworld_Hashiv_B2C=map.get("GREENWORLD_HASHIV_BTOC").toString();
								//String greenworld_Mapwebsite=map.get("GREENWORLD_MAPWEBSITE").toString();

								greenworld=new GreenWorld();

								//目前物流狀態
								String RtnCode="";
								//物流狀態說明
								String RtnMsg="";
								//綠界物流編號
								String AllPayLogisticsID="";
								//宅配托運單號
								String BookingNote="";
								//C2C寄貨編號
								String CVSPaymentNo="";
								//C2C 7-11驗證碼
								String CVSValidationNo="";
								//綠界交易單號
								String MerchantTradeNo="";

								//接口成功標記
								boolean bSuccess=false;

								Map<String, Object> oneData=new HashMap<String, Object>();
								//物流單產生時間
								SimpleDateFormat greenworld_Datetime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								String MerchantTradeDate=greenworld_Datetime.format(myTempcal.getTime());

								oneData.put("MerchantTradeDate", MerchantTradeDate);//yyyy/MM/dd HH:mm:ss
								//超商取貨CVS
								oneData.put("LogisticsType", "CVS");

								//訂單金額
								oneData.put("GoodsAmount", totamt);
								if (paystatus.equals("3")) 
								{
									//是否代收貨款
									oneData.put("IsCollection", "N");
								}
								else 
								{
									//是否代收貨款
									oneData.put("IsCollection", "Y");
								}

								//代收貨款金額
								oneData.put("CollectionAmount", collectAmt);//物流子類型為 UNIMARTC2C(7-ELEVEN 超 商交貨便)時，代收金額需要與商品金額 一致。 

								oneData.put("GoodsName", "百貨商品");
								//寄件人
								oneData.put("SenderName", sendName);
								//寄件人電話
								oneData.put("SenderPhone",sendPhone);

								if (sendPhone.startsWith("09")) 
								{
									//寄件人手機
									oneData.put("SenderCellPhone", sendPhone);
								}
								else 
								{
									//寄件人手機
									oneData.put("SenderCellPhone", "");
								}


								//收件人
								oneData.put("ReceiverName",receiver);										

								//收件人電話+886910090053,允許數字+特殊符號；特殊符號僅限()-# 
								//收件人電話886910090053,
								//直接处理掉
								String receiver_cellphone = receiver_phone.replace("+886", "0");
								receiver_cellphone = receiver_cellphone.replace("886", "0");										

								oneData.put("ReceiverPhone", receiver_cellphone);

								if (receiver_cellphone.startsWith("09")) 
								{
									//收件人手機
									oneData.put("ReceiverCellPhone", receiver_cellphone);
								}
								else 
								{
									//收件人手機,因为必填且09开头，10码，造一个默认的
									oneData.put("ReceiverCellPhone", "0987654321");
								}
								//收件人email
								oneData.put("ReceiverEmail","");
								//訂單備註
								oneData.put("Remark", memo);

								String merchantID="";
								String hashkey="";
								String hashiv="";

								String testGetshopno="";
								//成功後數據庫更新成綠界超商類型
								String myDeliverytype="";

								if (cvs_Mode.equals("1")) //1:C2C店到店模式
								{					
									merchantID=greenworld_MerchantId;
									hashkey=greenworld_HashKey;
									hashiv=greenworld_HashIv;

									//---C2C--- FAMIC2C:全家店到店 UNIMARTC2C:7-ELEVEN 超商交貨便 HILIFEC2C:萊爾富店到店 
									if (sDeliverytype.equals("8") ||sDeliverytype.equals("17"))//全家超商 
									{
										oneData.put("LogisticsSubType", "FAMIC2C");
										testGetshopno="001779";
										myDeliverytype="17";
									}
									else if (sDeliverytype.equals("7") ||sDeliverytype.equals("16"))//7-11超商 
									{
										oneData.put("LogisticsSubType", "UNIMARTC2C");
										testGetshopno="991182";
										myDeliverytype="16";
									}
									else if (sDeliverytype.equals("10") ||sDeliverytype.equals("18"))//萊而富超商 
									{
										oneData.put("LogisticsSubType", "HILIFEC2C");
										testGetshopno="2001";
										myDeliverytype="18";
									}
									/*
									else if (sDeliverytype.equals("11") ||sDeliverytype.equals("19"))//OK超商 
									{
										oneData.put("LogisticsSubType", "");
									}
									 */
									else 
									{
										logger.error("\r\n******【獲取物流單號】綠界greenworld,订单号 "+orderno+"，物流類型必須是超商取貨，且綠界暫不支持OK超商\r\n");
                                        sb.append("******【獲取物流單號】綠界greenworld,订单号 "+orderno+"，物流類型必須是超商取貨，且綠界暫不支持OK超商");
                                        
										greenworld=null;
										oneData=null;
										continue;//跳出
									}											

								}
								else //2：B2C大物流中心模式
								{
									merchantID=greenworld_MerchantID_B2C;
									hashkey=greenworld_Hashkey_B2C;
									hashiv=greenworld_Hashiv_B2C;		

									//---B2C--- FAMI:全家 UNIMART:7-ELEVEN 超商 HILIFE:萊爾富 
									if (sDeliverytype.equals("8") ||sDeliverytype.equals("17"))//全家超商 
									{
										oneData.put("LogisticsSubType", "FAMI");
										testGetshopno="001779";
										myDeliverytype="17";
									}
									else if (sDeliverytype.equals("7") ||sDeliverytype.equals("16"))//7-11超商 
									{
										oneData.put("LogisticsSubType", "UNIMART");
										testGetshopno="991182";
										myDeliverytype="16";
									}
									else if (sDeliverytype.equals("10") ||sDeliverytype.equals("18"))//萊而富超商 
									{
										oneData.put("LogisticsSubType", "HILIFE");
										testGetshopno="2001";
										myDeliverytype="18";
									}
									/*
									else if (sDeliverytype.equals("11") ||sDeliverytype.equals("19"))//OK超商 
									{
										oneData.put("LogisticsSubType", "");
									}
									 */
									else 
									{
										logger.error("\r\n******【獲取物流單號】綠界greenworld,订单号 "+orderno+"，物流類型必須是超商取貨，且綠界暫不支持OK超商\r\n");
										sb.append("******【獲取物流單號】綠界greenworld,订单号 "+orderno+"，物流類型必須是超商取貨，且綠界暫不支持OK超商");
																				
										greenworld=null;
										oneData=null;
										continue;//跳出
									}
								}

								if (greenworld_MerchantId.equals("2000933")|| greenworld_MerchantId.equals("2000132"))//測試賬號數據處理
								{
									//取貨門店
									////測試環境請使用以下門市進行測試，7-ELEVEN 超商：991182、全家：001779、萊爾富：2001。 
									oneData.put("ReceiverStoreID",testGetshopno);	
								}
								else 
								{
									//处理取货门店为空(之前乐天规格是没有取货门店编号的)
									if (getshopno.equals("")) 
									{
										if (sDeliverytype.equals("7") ||sDeliverytype.equals("16"))//7-11超商 
										{
											getshopno=HttpSend.Send711Shopno(getshopname);
										}
										else 
										{
											getshopno=HttpSend.SendCVSShopno(myprovince, mycity, getshopname);
										}												
									}

									//取貨門店												
									oneData.put("ReceiverStoreID",getshopno);	
								}	

								String resGreenWorld=greenworld.ExpressCreate_CVS(apiUrl, merchantID, hashkey, hashiv, greenworld_OurServerUrl, greenworld_OurServerUrlC2C, oneData);

								if (resGreenWorld.startsWith("1|")) 
								{
									String urlPara=resGreenWorld.substring(2);

									String[] splitStr=urlPara.split("&");			
									for (int a = 0; a < splitStr.length; a++) 
									{
										String[] sTempStr=splitStr[a].split("=");

										if (sTempStr.length>1) 
										{
											if (sTempStr[0].equals("AllPayLogisticsID")) 
											{
												AllPayLogisticsID=sTempStr[1];
											}
											else if (sTempStr[0].equals("BookingNote")) 
											{
												BookingNote=sTempStr[1];
											}
											else if (sTempStr[0].equals("RtnCode")) 
											{
												RtnCode=sTempStr[1];
											}
											else if (sTempStr[0].equals("RtnMsg")) 
											{
												RtnMsg=sTempStr[1];
											}
											else if (sTempStr[0].equals("CVSPaymentNo")) 
											{
												CVSPaymentNo=sTempStr[1];
											}
											else if (sTempStr[0].equals("CVSValidationNo")) 
											{
												CVSValidationNo=sTempStr[1];
											}	
											else if (sTempStr[0].equals("MerchantTradeNo")) 
											{
												MerchantTradeNo=sTempStr[1];
											}		

										}

									}	

									bSuccess=true;
								}
								else 
								{
									bSuccess=false;

									//返回失敗:0| ErrorMessage 

									logger.error("\r\n******【獲取物流單號】綠界greenworld,订单号 "+orderno+"，綠界返回失敗,"+resGreenWorld+",\r\n");
									sb.append("******【獲取物流單號】綠界greenworld,订单号 "+orderno+"，綠界返回失敗,"+resGreenWorld+",");
								}

								oneData=null;
								//接口返回成功統一處理
								if (bSuccess) 
								{

									//执行
									List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

									//更新物流单号
									UptBean ubec=new UptBean("OC_ORDER");
									ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
									ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
									ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

									//
									ubec.addUpdateValue("DELIVERYNO", new DataValue(CVSPaymentNo, Types.VARCHAR));
									ubec.addUpdateValue("GREENWORLD_LOGISTICSID", new DataValue(AllPayLogisticsID, Types.VARCHAR));
									ubec.addUpdateValue("GREENWORLD_MERCHANTTRADENO", new DataValue(MerchantTradeNo, Types.VARCHAR));
									ubec.addUpdateValue("GREENWORLD_VALIDNO", new DataValue(CVSValidationNo, Types.VARCHAR));
									ubec.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
									
									ubec.addUpdateValue("SHIPPINGSHOP", new DataValue(getshopno, Types.VARCHAR));//乐天接单没有取货门店编码
									ubec.addUpdateValue("DELIVERYTYPE", new DataValue(myDeliverytype, Types.VARCHAR));//綠界物流

									lstData.add(new DataProcessBean(ubec));
									
									//更新物流单信息
									ubec=new UptBean("OC_SHIPMENT");
									ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
									ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
									ubec.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR));

									//
									ubec.addUpdateValue("EXPRESSNO", new DataValue(CVSPaymentNo, Types.VARCHAR));
									ubec.addUpdateValue("GREENWORLD_LOGISTICSID", new DataValue(AllPayLogisticsID, Types.VARCHAR));
									ubec.addUpdateValue("GREENWORLD_MERCHANTTRADENO", new DataValue(MerchantTradeNo, Types.VARCHAR));
									ubec.addUpdateValue("GREENWORLD_VALIDNO", new DataValue(CVSValidationNo, Types.VARCHAR));

									lstData.add(new DataProcessBean(ubec));
									

									//接單日誌
									DataValue[] insValueOrderStatus_LOG = new DataValue[] 
											{ 
													new DataValue(shipCompany, Types.VARCHAR),
													new DataValue(shipShop, Types.VARCHAR), // 组织编号=门店编号
													new DataValue(shipShop, Types.VARCHAR), // 映射后的门店
													new DataValue(orderno, Types.VARCHAR), //
													new DataValue(loadtype, Types.VARCHAR), //電商平台
													new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
													new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
													new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
													new DataValue("待配送", Types.VARCHAR), // 状态名称
													new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
													new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
													new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
													new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
													new DataValue("admin", Types.VARCHAR), //操作員編碼
													new DataValue("管理員", Types.VARCHAR), //操作員名稱
													new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
													new DataValue("訂單狀態-->待配送", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
													new DataValue("100", Types.VARCHAR) 
											};
									InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
									ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
									lstData.add(new DataProcessBean(ibOrderStatusLog));	

									//执行							
									StaticInfo.dao.useTransactionProcessData(lstData);

								}
							}
					
						}
						else 
						{
							logger.error("\r\n******【獲取物流單號】綠界greenworld,订单号 "+orderno+"，货运厂商资料未设定\r\n");
							sb.append("******【獲取物流單號】綠界greenworld,订单号 "+orderno+"，货运厂商资料未设定");
						}

					}
					else if (lgPlatformNo.equals("mingjie")) //大物流十公斤以上
					{

					}
					else if (lgPlatformNo.equals("chinapost")) //中华邮政
					{

					}
					else 
					{

					}

				}

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

				logger.error("\r\n******【獲取物流單號】报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				sb.append("******【獲取物流單號】报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******");
				
				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******【獲取物流單號】报错信息" + e.getMessage() + "******\r\n");
				sb.append("******【獲取物流單號】报错信息" + e.getMessage() + "******");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			egs=null;
			htc=null;

			bRun=false;//
			logger.info("\r\n*********【獲取物流單號】定时调用End:************\r\n");
		}
		
		//
		sReturnInfo=sb.toString();
		sb.setLength(0);
		sb=null;
		
		return sReturnInfo;		

	}



}
