package com.dsc.spos.service.imp.json;

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

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECBatchExpressnoCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECBatchExpressnoCreateRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.logistics.Egs;
import com.dsc.spos.utils.logistics.GreenWorld;
import com.dsc.spos.utils.logistics.Htc;
import com.google.gson.reflect.TypeToken;

//批量取物流单号(支持逆物流取单号)
public class DCP_OrderECBatchExpressnoCreate extends SPosAdvanceService<DCP_OrderECBatchExpressnoCreateReq,DCP_OrderECBatchExpressnoCreateRes> 
{

	Logger logger = LogManager.getLogger(DCP_OrderECBatchExpressnoCreate.class.getName());

	@Override
	protected void processDUID(DCP_OrderECBatchExpressnoCreateReq req, DCP_OrderECBatchExpressnoCreateRes res)
			throws Exception 
	{

		//		
		res.setSuccess(false);
		res.setServiceStatus("100");

		String eShopId=req.getShopId();

		StringBuffer errMsg = new StringBuffer("");

		Egs egs=null;
		Htc htc=null;
		GreenWorld greenworld=null;

		//便利达康的流水号
		int cvsItem=1;

		//月日时分格式
		SimpleDateFormat myTempdf = new SimpleDateFormat("MMddHHmm");
		Calendar myTempcal = Calendar.getInstance();
		String sMMddHHmm=myTempdf.format(myTempcal.getTime());
		//System.out.println(sMMddHHmm);


		String[] ecOrderno=req.getEcOrderNo();

		String sOrdernoMulti="";

		for (int i = 0; i < ecOrderno.length; i++) 
		{
			sOrdernoMulti+=ecOrderno[i]+",";
		}

		if(sOrdernoMulti.length()>0)
		{
			sOrdernoMulti=sOrdernoMulti.substring(0, sOrdernoMulti.length()-1);			
			//
			String[] arrPluno=new String[] {sOrdernoMulti};		

			String sql="select * from OC_order A INNER JOIN ("
					+ PosPub.getFormatSourcePluno(arrPluno)
					+ ") B ON A.ORDERNO=B.PLUNO "
					+ "where A.EID='"+req.geteId()+"' and (A.SHOPID='"+req.getShopId()+"' or A.SHIPPINGSHOP='"+req.getShopId()+"')" ;

			List<Map<String, Object>> getQData= this.doQueryData(sql,null);


			//查询物流厂商API信息
			String sqlLG="select * from OC_logistics  where status='100' and EID='"+req.geteId()+"' ";	
			List<Map<String, Object>> getLGData=this.doQueryData(sqlLG, null);


			if(getQData!=null && getQData.isEmpty()==false)
			{			
				//执行
				List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

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



				//記錄優化一下
				List<String> lstCount=new ArrayList<String>();
				String sendName = "";
				String sendAddress = "";
				String sendPhone = "";
				String sendSuDa5 = "";
				String SenderUnimartShop = "";
				String SenderHilifeShop = "";
				String SenderOkShop = "";
				String SenderFamiShop = "";
				
				String rtnGetshopno="";//记录逆物流的商家设置的收货超商门店
				

				List<Map<String, Object>> getSenderData=null;
				
				for(Map<String, Object> par: getQData)
				{
					String orderno=par.get("ORDERNO").toString();

					//處理訂單以配送門店為歸屬門店
					eShopId=par.get("SHOPID").toString();
					
					//
					lstCount.add(orderno);

					String loadtype=par.get("LOAD_DOCTYPE").toString();					
					String sDeliverytype=par.get("DELIVERYTYPE").toString();
					String sStatus=par.get("STATUS").toString();	
					//物流单号
					String sExpressno=par.get("DELIVERYNO").toString();	
					String address=par.get("ADDRESS").toString();					
					//逆物流单号
					String sRtnExpressno=par.get("GREENWORLD_RTNORDERNO").toString();

					String tempStatusExpressno=sExpressno;
					//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
					if (sStatus.equals("2") || sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7")) 
					{
						tempStatusExpressno=sExpressno;
					}
					else if (sStatus.equals("11")) 
					{
						tempStatusExpressno=sRtnExpressno;
					}

					if (tempStatusExpressno==null || tempStatusExpressno.trim().equals("")) 
					{
						//虾皮的订单有一部分是平台自己发物流处理一下
						if (loadtype.equals("shopee")==false || (loadtype.equals("shopee") && (sDeliverytype==null || sDeliverytype.equals("")|| sDeliverytype.equals("0")|| sDeliverytype.equals("14")))) 
						{
							//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
							//11已完成状态是表示取逆物流单号
							if (sStatus.equals("2") || sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7")|| sStatus.equals("11")) 
							{

								//查找当前门店的寄件人信息		
								//先取门店参数platform_baseset，取不到就取模板参数platform_basesettemp,union会自动根据第一列排序
								String sqlSenderMsg=""
//										+ "select 1 as ID,t.item,t.itemvalue as ITEMVALUE from platform_baseset t where item IN ('SenderName','SenderSuDa5','SenderPhone','SenderAddress','SenderUnimartShop','SenderHilifeShop','SenderOkShop','SenderFamiShop') AND status='100' " 
//										+" and EID = '"+req.geteId()+"' and SHOPID='"+eShopId+"' "
//										+" union "
										+ " select 2 as ID,t.item,t.ITEMVALUE from platform_basesettemp t where item IN ('SenderName','SenderSuDa5','SenderPhone','SenderAddress','SenderUnimartShop','SenderHilifeShop','SenderOkShop','SenderFamiShop') AND status='100' "
										+ " and EID = '"+req.geteId()+"'";

								
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
										String itemValue = map.get("ITEMVALUE").toString();

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
										if(item.equals("SenderUnimartShop")&& SenderUnimartShop.equals(""))
										{
											SenderUnimartShop = itemValue ;
											continue;
										}
										if(item.equals("SenderHilifeShop")&& SenderHilifeShop.equals(""))
										{
											SenderHilifeShop = itemValue ;
											continue;
										}
										if(item.equals("SenderOkShop")&& SenderOkShop.equals(""))
										{
											SenderOkShop = itemValue ;
											continue;
										}
										if(item.equals("SenderFamiShop")&& SenderFamiShop.equals(""))
										{
											SenderFamiShop = itemValue ;
											continue;
										}

									}

								}

								if (req.getLgPlatformNo().equals("htc")) //新竹物流
								{

									if (address==null||address.trim().equals("")) 
									{
										errMsg.append("HTC新竹物流,订单号"+orderno+",地址不能为空，<br/>");
										continue;
									}

									//
									Map<String, Object> map_condition = new HashMap<String, Object>();
									map_condition.put("LGPLATFORMNO", "htc");		
									List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
									if (getQHeader1!=null && getQHeader1.size()>0) 
									{
										String apiUrl=getQHeader1.get(0).get("API_URL").toString();//
										String lgEId=getQHeader1.get(0).get("LGCOMPANYNO").toString();
										String lgPassword=getQHeader1.get(0).get("LGPASSWORD").toString();

										htc=new Htc();

										String receiver=par.get("GETMAN").toString();
										String receiver_phone=par.get("GETMANTEL").toString();
										String pieces=par.get("TOT_QTY").toString();
										if (PosPub.isNumericType(pieces)==false) 
										{
											pieces="1";
										}
										String weight="1";

										String sender_site="";
										String shipdate=par.get("SHIPDATE").toString();
										String paystatus=par.get("PAYSTATUS").toString();
										String payamt=par.get("PAYAMT").toString();
										String totamt=par.get("TOT_AMT").toString();

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

										String memo=par.get("MEMO").toString();

										String productType="001";//温层，商品种类 001 一般;003 冷凍;008 冷藏

										String resbody="";

										if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
										{
											resbody=htc.TransData_Json(apiUrl, lgEId, lgPassword,orderno , receiver, receiver_phone, address, pieces, weight, sender_site, shipdate, collectAmt, memo, productType,"","","");
										}
										else //逆物流
										{
											resbody=htc.TransData_Json(apiUrl, lgEId, lgPassword,"rtn"+orderno , sendName, sendPhone, sendAddress, pieces, weight, sender_site, shipdate, "0", memo, productType,receiver,receiver_phone,address);
										}

										JSONArray jsonres=new JSONArray(resbody);
										for (int a = 0; a < jsonres.length(); a++) 
										{
											if (jsonres.getJSONObject(0).isNull("ErrMsg")) 
											{
												//System.out.println("HTC新竹物流接口TransData_Json调用失败");

												logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】新竹物流htc,接口TransData_Json返回失败,"+resbody+"******\r\n");

												errMsg.append("HTC新竹物流,订单号"+orderno+",接口TransData_Json返回失败，<br/>");
											}
											else 
											{
												String ErrMsg=jsonres.getJSONObject(0).getString("ErrMsg");//错误信息

												String tempStatusDes="";
												
												if (ErrMsg.trim().equals("")==false) 
												{
													//System.out.println("HTC新竹物流接口TransData_Json调用失败=" + ErrMsg);

													logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】新竹物流htc,接口TransData_Json返回失败="+ErrMsg+"******\r\n");

													logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】新竹物流htc,接口TransData_Json返回失败body="+resbody+"******\r\n");

													errMsg.append("HTC新竹物流,订单号"+orderno+",接口TransData_Json返回失败="+ErrMsg+"，<br/>");

													if (jsonres.getJSONObject(0).has("epino")) 
													{
														if(jsonres.getJSONObject(0).isNull("epino")==false)
														{
															String epino=jsonres.getJSONObject(0).getString("epino");//订单编号
															String edelno=jsonres.getJSONObject(0).getString("edelno");//托运单号
															String erstno=jsonres.getJSONObject(0).getString("erstno").trim();//到著站编码

															if (epino.equals(orderno) && ErrMsg.contains("訂單編號重複")) 
															{
																//更新新竹物流单信息
																UptBean ubec=new UptBean("OC_ORDER");
																ubec.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
																ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

																//
																if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
																{
																	ubec.addUpdateValue("DELIVERYNO", new DataValue(edelno, Types.VARCHAR));
																	
																	ubec.addUpdateValue("RECEIVER_SITENO", new DataValue(erstno, Types.VARCHAR));
																	ubec.addUpdateValue("DELIVERYTYPE", new DataValue("15", Types.VARCHAR));
																	ubec.addUpdateValue("WEIGHT", new DataValue(1, Types.FLOAT));
																	ubec.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 

																	tempStatusDes="待配送";
																}
																else //逆物流
																{
																	ubec.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(edelno, Types.VARCHAR));
																	
																	//逆物流货运单表更新
																	UptBean ubsp = new UptBean("OC_SHIPMENT");	
																	//条件
																	ubsp.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
																	ubsp.addCondition("SHOPID",new DataValue(eShopId, Types.VARCHAR));
																	ubsp.addCondition("EC_ORDERNO",new DataValue(orderno, Types.VARCHAR));
																	//值
																	ubsp.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(edelno, Types.VARCHAR));//更新逆物流单号

																	lstData.add(new DataProcessBean(ubsp));	
																	
																	tempStatusDes="逆物流待配送";
																}																

																
																lstData.add(new DataProcessBean(ubec));

																//接單日誌
																DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																		{ 
																				new DataValue(req.geteId(), Types.VARCHAR),
																				new DataValue(eShopId, Types.VARCHAR), // 组织编号=门店编号
																				new DataValue(eShopId, Types.VARCHAR), // 映射后的门店
																				new DataValue(orderno, Types.VARCHAR), //
																				new DataValue(loadtype, Types.VARCHAR), //電商平台
																				new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																				new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
																				new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																				new DataValue(tempStatusDes, Types.VARCHAR), // 状态名称
																				new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																				new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																				new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																				new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																				new DataValue("admin", Types.VARCHAR), //操作員編碼
																				new DataValue("管理員", Types.VARCHAR), //操作員名稱
																				new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																				new DataValue("訂單狀態-->" +tempStatusDes, Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																				new DataValue("100", Types.VARCHAR) 
																		};
																InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
																ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
																lstData.add(new DataProcessBean(ibOrderStatusLog));

															}
														}

													}												
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
													UptBean ubec=new UptBean("OC_ORDER");
													ubec.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
													ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

													
													//
													if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
													{
														ubec.addUpdateValue("DELIVERYNO", new DataValue(edelno, Types.VARCHAR));
														
														tempStatusDes="待配送";
													}
													else //逆物流
													{
														ubec.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(edelno, Types.VARCHAR));
														
														//逆物流货运单表更新
														UptBean ubsp = new UptBean("OC_SHIPMENT");	
														//条件
														ubsp.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
														ubsp.addCondition("SHOPID",new DataValue(eShopId, Types.VARCHAR));
														ubsp.addCondition("EC_ORDERNO",new DataValue(orderno, Types.VARCHAR));
														//值
														ubsp.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(edelno, Types.VARCHAR));//更新逆物流单号

														lstData.add(new DataProcessBean(ubsp));	
														
														tempStatusDes="逆物流待配送";
													}

													ubec.addUpdateValue("RECEIVER_SITENO", new DataValue(erstno, Types.VARCHAR));
													ubec.addUpdateValue("DELIVERYTYPE", new DataValue("15", Types.VARCHAR));
													ubec.addUpdateValue("WEIGHT", new DataValue(1, Types.FLOAT));
													ubec.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 

													lstData.add(new DataProcessBean(ubec));


													//接單日誌
													DataValue[] insValueOrderStatus_LOG = new DataValue[] 
															{ 
																	new DataValue(req.geteId(), Types.VARCHAR),
																	new DataValue(eShopId, Types.VARCHAR), // 组织编号=门店编号
																	new DataValue(eShopId, Types.VARCHAR), // 映射后的门店
																	new DataValue(orderno, Types.VARCHAR), //
																	new DataValue(loadtype, Types.VARCHAR), //電商平台
																	new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																	new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
																	new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																	new DataValue(tempStatusDes, Types.VARCHAR), // 状态名称
																	new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																	new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																	new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																	new DataValue("admin", Types.VARCHAR), //操作員編碼
																	new DataValue("管理員", Types.VARCHAR), //操作員名稱
																	new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																	new DataValue("訂單狀態-->" + tempStatusDes, Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																	new DataValue("100", Types.VARCHAR) 
															};
													InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
													ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
													lstData.add(new DataProcessBean(ibOrderStatusLog));

												}
											}

											break;
										}

									}
									else 
									{
										errMsg.append("货运厂商资料未设定，<br/>");
									}
								}
								else if (req.getLgPlatformNo().equals("egs")) //黑猫宅急便
								{
									if (address==null||address.trim().equals("")) 
									{
										errMsg.append("黑猫宅急便egs,订单号"+orderno+",地址不能为空，<br/>");
										continue;
									}

									//黑猫
									Map<String, Object> map_condition = new HashMap<String, Object>();
									map_condition.put("LGPLATFORMNO", "egs");		
									List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
									if (getQHeader1!=null && getQHeader1.size()>0) 
									{
										String apiUrl=getQHeader1.get(0).get("API_URL").toString();//
										String eId=getQHeader1.get(0).get("EID").toString();
										String customer_id=getQHeader1.get(0).get("CUSTOMERNO").toString();

										//更新栏位							
										String measureNo="0001";//尺寸代码
										String measureName="60cm";//尺寸名称
										String temperateNo="1";//温层代码
										String temperateName="常温";//温层名称
										String expressNo="";//托运单号
										String distanceNo="";//距离代码
										String distanceName="";//距离名称
										String receiveSevenCode="";//7码邮号
										String receiveSite="";//到著站
										String suda5_senderpostcode=PosPub.getPARA_SMS(StaticInfo.dao, eId, req.getShopId(), "SenderSuDa5");
										if (suda5_senderpostcode==null||suda5_senderpostcode.equals("")) 
										{
											suda5_senderpostcode=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "SenderSuDa5");

											if (suda5_senderpostcode==null||suda5_senderpostcode.equals("")) 
											{
												suda5_senderpostcode="00000";
											}											
										}
										
										String receiver=par.get("GETMAN").toString();
										String receiver_phone=par.get("GETMANTEL").toString();		
								
										String delivery_date  = par.get("SHIPDATE").toString();//20190621

										String paystatus=par.get("PAYSTATUS").toString();
										String payamt=par.get("PAYAMT").toString();
										String totamt=par.get("TOT_AMT").toString();

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

										String memo=par.get("MEMO").toString();

										String productType="001";//温层，商品种类 001 一般;003 冷凍;008 冷藏


										//
										String expressBiltype="A";//托运单别，这里先默认处理
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
												
												if (splitStatus.length>1) 
												{
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
														logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs,接口query_suda7_dash返回失败信息="+message+"******\r\n");

														errMsg.append("黑猫宅急便egs,订单号"+orderno+",接口query_suda7_dash返回失败信息="+message+"，<br/>");
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
																		logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs,接口query_distance返回失败信息="+message+"******\r\n");

																		errMsg.append("黑猫宅急便egs,订单号"+orderno+",接口query_distance返回失败信息="+message+"，<br/>");
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
																				logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs,接口query_waybill_id_remain返回失败信息="+message+"******\r\n");

																				errMsg.append("黑猫宅急便egs,订单号"+orderno+",接口query_waybill_id_remain返回失败信息="+message+"，<br/>");
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
																							logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs,接口query_waybill_id_range返回失败信息="+message+"******\r\n");

																							errMsg.append("黑猫宅急便egs,订单号"+orderno+",接口query_waybill_id_range返回失败信息="+message+"，<br/>");
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

																							/*
																							//接口调用成功
																							boolean bCallDeliveryOK=false;
																							
																							if (sStatus.equals("11"))//逆物流
																							{
																								// 建立时间和打印时间传当前系统日期时间
																								SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
																								String sysDate=dfDatetime.format(new Date());
																								String account_id = ""; // 托运单账号
																								String member_no = ""; // 会员编号
																								String taxin = "0"; // 进口关税 海外地区使用   0
																								String insurance = "0"; // 报值金额   0
																								
																								//
																								String egsRes = egs.transfer_waybill(apiUrl, customer_id, expressNo, "rtn"+orderno, sendName, sendAddress, 
																										sendSuDa5, sendPhone, sendPhone, receiver, address, suda5_customerpostcode, receiver_phone, 
																										collectAmt, "百货商品", memo, "0001", "0001", distanceNo, delivery_date, "4", 
																										sysDate, sysDate, account_id, member_no, taxin, insurance);
																								
																								splitStrings=egsRes.split("&");

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
																									}
																									else 
																									{
																										bCallDeliveryOK=true;
																									}

																								}	

																								
																							}	
																							else 
																							{
																								bCallDeliveryOK=true;
																							}
																							
																							//退货调用失败就跳出
																							if (bCallDeliveryOK==false) 
																							{
																								continue;
																							}
																							*/
																							
																							String tempStatusDes="";
																							//
																							if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
																							{
																								//更新黑猫物流单信息
																								UptBean ubec=new UptBean("OC_ORDER");
																								ubec.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
																								ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

																								
																								ubec.addUpdateValue("DELIVERYNO", new DataValue(expressNo, Types.VARCHAR));
																								ubec.addUpdateValue("MEASURENO", new DataValue(measureNo, Types.VARCHAR));
																								ubec.addUpdateValue("MEASURENAME", new DataValue(measureName, Types.VARCHAR));
																								ubec.addUpdateValue("TEMPERATELAYERNO", new DataValue(temperateNo, Types.VARCHAR));
																								ubec.addUpdateValue("TEMPERATELAYERNAME", new DataValue(temperateName, Types.VARCHAR));
																								
																								ubec.addUpdateValue("DISTANCENO", new DataValue(distanceNo, Types.VARCHAR));
																								ubec.addUpdateValue("DISTANCENAME", new DataValue(distanceName, Types.VARCHAR));
																								ubec.addUpdateValue("RECEIVER_FIVECODE", new DataValue(suda5_customerpostcode, Types.VARCHAR));
																								ubec.addUpdateValue("RECEIVER_SEVENCODE", new DataValue(suda7, Types.VARCHAR));
																								ubec.addUpdateValue("RECEIVER_SITENO", new DataValue(receiveSite, Types.VARCHAR));
																								ubec.addUpdateValue("DELIVERYTYPE", new DataValue("9", Types.VARCHAR));
																								ubec.addUpdateValue("EXPRESSBILLTYPE", new DataValue("A", Types.VARCHAR));
																								ubec.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
																								
																								lstData.add(new DataProcessBean(ubec));
																								
																								tempStatusDes="待配送";
																							}
																							else //逆物流
																							{
																								//更新黑猫物流单信息
																								UptBean ubec=new UptBean("OC_ORDER");
																								ubec.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
																								ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

																								ubec.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(expressNo, Types.VARCHAR));
																								
																								lstData.add(new DataProcessBean(ubec));
																								
																								
																								//逆物流货运单表更新
																								UptBean ubsp = new UptBean("OC_SHIPMENT");	
																								//条件
																								ubsp.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
																								ubsp.addCondition("SHOPID",new DataValue(eShopId, Types.VARCHAR));
																								ubsp.addCondition("EC_ORDERNO",new DataValue(orderno, Types.VARCHAR));
																								//值
																								ubsp.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(expressNo, Types.VARCHAR));//更新逆物流单号

																								lstData.add(new DataProcessBean(ubsp));	
																								
																								tempStatusDes="逆物流待配送";
								
																							}		


																							//接單日誌
																							DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																									{ 
																											new DataValue(req.geteId(), Types.VARCHAR),
																											new DataValue(eShopId, Types.VARCHAR), // 组织编号=门店编号
																											new DataValue(eShopId, Types.VARCHAR), // 映射后的门店
																											new DataValue(orderno, Types.VARCHAR), //
																											new DataValue(loadtype, Types.VARCHAR), //電商平台
																											new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																											new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
																											new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																											new DataValue(tempStatusDes, Types.VARCHAR), // 状态名称
																											new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																											new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																											new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																											new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																											new DataValue("admin", Types.VARCHAR), //操作員編碼
																											new DataValue("管理員", Types.VARCHAR), //操作員名稱
																											new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																											new DataValue("訂單狀態-->" +tempStatusDes, Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																											new DataValue("100", Types.VARCHAR) 
																									};
																							InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
																							ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
																							lstData.add(new DataProcessBean(ibOrderStatusLog));

																						}

																					}
																				}
																				else 
																				{
																					logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs,托运单已用完，请联系黑猫重新申请******\r\n");

																					errMsg.append("黑猫宅急便egs,订单号"+orderno+",托运单已用完，请联系黑猫重新申请，<br/>");
																				}

																			}

																		}	


																	}

																}


															}
															else 
															{
																logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs,接口query_suda7_dash返回值不正确="+suda7+"******\r\n");

																errMsg.append("黑猫宅急便egs,订单号"+orderno+",接口query_suda7_dash返回值不正确，地址信息="+suda7+"，<br/>");
															}
														}
														else 
														{
															logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs,接口query_suda7_dash返回值不正确，地址信息="+address+"******\r\n");

															errMsg.append("黑猫宅急便egs,订单号"+orderno+",接口query_suda7_dash返回值不正确，地址信息="+address+"，<br/>");
														}

													}
												}
												else 
												{
													logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs,接口query_suda7_dash返回失败******\r\n");

													errMsg.append("黑猫宅急便egs,订单号"+orderno+",接口query_suda7_dash返回失败，<br/>" + resbody);
												}						

											}	
											else 
											{
												logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs,接口query_suda7_dash返回失败******\r\n");

												errMsg.append("黑猫宅急便egs,订单号"+orderno+",接口query_suda7_dash返回失败，<br/>");
											}

											egs=null;

										}		
										else 
										{
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】黑猫宅急便egs 订单号"+orderno+",托运单别必须是A或B******\r\n");

											errMsg.append("黑猫宅急便egs,订单号"+orderno+",托运单别必须是A或B，<br/>");
										}
									}
									else 
									{
										errMsg.append("货运厂商资料未设定，<br/>");
									}

								}
								else if (req.getLgPlatformNo().equals("cvs")) //便利达康
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
									UptBean ubec=new UptBean("OC_ORDER");
									ubec.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
									ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

									String tempStatusDes="";
									
									//
									if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
									{
										ubec.addUpdateValue("DELIVERYNO", new DataValue(expressno, Types.VARCHAR));
										
										ubec.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 

										tempStatusDes="待配送";
									}
									else //逆物流
									{
										ubec.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(expressno, Types.VARCHAR));
										
										//逆物流货运单表更新
										UptBean ubsp = new UptBean("OC_SHIPMENT");	
										//条件
										ubsp.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
										ubsp.addCondition("SHOPID",new DataValue(eShopId, Types.VARCHAR));
										ubsp.addCondition("EC_ORDERNO",new DataValue(orderno, Types.VARCHAR));
										//值
										ubsp.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(expressno, Types.VARCHAR));//更新逆物流单号

										lstData.add(new DataProcessBean(ubsp));	
										
										tempStatusDes="逆物流待配送";

									}
									
									lstData.add(new DataProcessBean(ubec));

									//接單日誌
									DataValue[] insValueOrderStatus_LOG = new DataValue[] 
											{ 
													new DataValue(req.geteId(), Types.VARCHAR),
													new DataValue(eShopId, Types.VARCHAR), // 组织编号=门店编号
													new DataValue(eShopId, Types.VARCHAR), // 映射后的门店
													new DataValue(orderno, Types.VARCHAR), //
													new DataValue(loadtype, Types.VARCHAR), //電商平台
													new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
													new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
													new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
													new DataValue(tempStatusDes, Types.VARCHAR), // 状态名称
													new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
													new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
													new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
													new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
													new DataValue("admin", Types.VARCHAR), //操作員編碼
													new DataValue("管理員", Types.VARCHAR), //操作員名稱
													new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
													new DataValue("訂單狀態-->" +tempStatusDes, Types.VARCHAR), //類型名稱+"-->"+狀態名稱
													new DataValue("100", Types.VARCHAR) 
											};
									InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
									ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
									lstData.add(new DataProcessBean(ibOrderStatusLog));

								}
								else if (req.getLgPlatformNo().equals("dzt")) //大智通
								{
									Map<String, Object> dztMap=PosPub.getDZT_Expressno(StaticInfo.dao, req.geteId());
									String dzt_expressno=dztMap.get("expressno").toString();
									String dzt_dztno =dztMap.get("dztno").toString();
									String dzt_status=dztMap.get("status").toString();
									String dzt_end=dztMap.get("dztend").toString();

									if (dzt_expressno.equals("")) 
									{
										logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】大智通DZT配送流水号为空******\r\n");

										errMsg.append("大智通DZT配送流水号为空,订单号"+orderno+"，<br/>");
									}
									else 
									{

										//大智通流水号为8码长度
										String expressnoFormat=PosPub.FillStr(dzt_expressno, 8, "0", true);

										//更新便利达康的物流单号
										UptBean ubec=new UptBean("OC_ORDER");
										ubec.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
										ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

										String tempStatusDes="";
										
										//格式化
										if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
										{
											ubec.addUpdateValue("DELIVERYNO", new DataValue(expressnoFormat, Types.VARCHAR));
											ubec.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 

											tempStatusDes="待配送";
										}
										else 
										{
											ubec.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(expressnoFormat, Types.VARCHAR));

											//逆物流货运单表更新
											UptBean ubsp = new UptBean("OC_SHIPMENT");	
											//条件
											ubsp.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
											ubsp.addCondition("SHOPID",new DataValue(eShopId, Types.VARCHAR));
											ubsp.addCondition("EC_ORDERNO",new DataValue(orderno, Types.VARCHAR));
											//值
											ubsp.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(expressnoFormat, Types.VARCHAR));//更新逆物流单号

											lstData.add(new DataProcessBean(ubsp));	
											
											tempStatusDes="逆物流待配送";
										}
										
										lstData.add(new DataProcessBean(ubec));

										//接單日誌
										DataValue[] insValueOrderStatus_LOG = new DataValue[] 
												{ 
														new DataValue(req.geteId(), Types.VARCHAR),
														new DataValue(eShopId, Types.VARCHAR), // 组织编号=门店编号
														new DataValue(eShopId, Types.VARCHAR), // 映射后的门店
														new DataValue(orderno, Types.VARCHAR), //
														new DataValue(loadtype, Types.VARCHAR), //電商平台
														new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
														new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
														new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
														new DataValue(tempStatusDes, Types.VARCHAR), // 状态名称
														new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
														new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
														new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
														new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
														new DataValue("admin", Types.VARCHAR), //操作員編碼
														new DataValue("管理員", Types.VARCHAR), //操作員名稱
														new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
														new DataValue("訂單狀態-->" +tempStatusDes, Types.VARCHAR), //類型名稱+"-->"+狀態名稱
														new DataValue("100", Types.VARCHAR) 
												};
										InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
										ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
										lstData.add(new DataProcessBean(ibOrderStatusLog));


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


										List<DataProcessBean> tempData=new ArrayList<DataProcessBean>();	
										//更新大智通当前使用流水及状态
										ubec=new UptBean("OC_SHIPBOOKDZT");
										ubec.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
										ubec.addCondition("DZTNO", new DataValue(dzt_dztno, Types.VARCHAR));

										ubec.addUpdateValue("DZT_LASTNO", new DataValue(dzt_expressno, Types.VARCHAR));
										ubec.addUpdateValue("STATUS", new DataValue(statusFormat, Types.VARCHAR));
										tempData.add(new DataProcessBean(ubec));

										//这个要先执行，下次还有取流水							
										StaticInfo.dao.useTransactionProcessData(tempData);
									}

								}	
								else if (req.getLgPlatformNo().equals("greenworld")) //綠界
								{


									String receiver=par.get("GETMAN").toString();
									String receiver_phone=par.get("GETMANTEL").toString();									
									String paystatus=par.get("PAYSTATUS").toString();
									String payamt=par.get("PAYAMT").toString();
									String totamt=par.get("TOT_AMT").toString();
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
									String memo=par.get("MEMO").toString();
									String getshopno=par.get("SHIPPINGSHOP").toString();
									String getshopname=par.get("SHIPPINGSHOPNAME").toString();
									String myprovince=par.get("PROVINCE").toString();
									String mycity=par.get("CITY").toString();					

									//
									Map<String, Object> map_condition = new HashMap<String, Object>();
									map_condition.put("LGPLATFORMNO", "greenworld");		
									List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
									if (getQHeader1!=null && getQHeader1.size()>0) 
									{
										String apiUrl=getQHeader1.get(0).get("API_URL").toString();//
										String cvs_Mode=getQHeader1.get(0).get("CVS_MODE").toString();
										String greenworld_MerchantId=getQHeader1.get(0).get("GREENWORLD_MERCHANTID").toString();
										String greenworld_HashKey=getQHeader1.get(0).get("GREENWORLD_HASHKEY").toString();
										String greenworld_HashIv=getQHeader1.get(0).get("GREENWORLD_HASHIV").toString();										
										String greenworld_OurServerUrl=getQHeader1.get(0).get("GREENWORLD_OURSERVERURL").toString();
										String greenworld_OurServerUrlC2C=getQHeader1.get(0).get("GREENWORLD_OURSERVERURL_CTOC").toString();
										String greenworld_MerchantID_B2C=getQHeader1.get(0).get("GREENWORLD_MERCHANTID_BTOC").toString();
										String greenworld_Hashkey_B2C=getQHeader1.get(0).get("GREENWORLD_HASHKEY_BTOC").toString();
										String greenworld_Hashiv_B2C=getQHeader1.get(0).get("GREENWORLD_HASHIV_BTOC").toString();
										//String greenworld_Mapwebsite=getQHeader1.get(0).get("GREENWORLD_MAPWEBSITE").toString();

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
										String MerchantTradeDate=greenworld_Datetime.format(calendar.getTime());

										oneData.put("MerchantTradeDate", MerchantTradeDate);//yyyy/MM/dd HH:mm:ss
										//超商取貨CVS
										oneData.put("LogisticsType", "CVS");

										//訂單金額
										oneData.put("GoodsAmount", totamt);
										
										if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
										{
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

										}
										else //逆物流
										{
											//是否代收貨款
											oneData.put("IsCollection", "N");
											
											//代收貨款金額
											oneData.put("CollectionAmount", "0");//物流子類型為 UNIMARTC2C(7-ELEVEN 超 商交貨便)時，代收金額需要與商品金額 一致。 
										}
										
							
										oneData.put("GoodsName", "百貨商品");
										
										if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
										{
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

										}
										else //逆物流
										{
											//寄件人
											oneData.put("SenderName", receiver);
											

											//收件人電話+886910090053,允許數字+特殊符號；特殊符號僅限()-# 
											//收件人電話886910090053,
											//直接处理掉
											String receiver_cellphone = receiver_phone.replace("+886", "0");
											receiver_cellphone = receiver_cellphone.replace("886", "0");										
											
											//寄件人電話
											oneData.put("SenderPhone",receiver_cellphone);
											
											if (receiver_cellphone.startsWith("09")) 
											{
												//寄件人手機
												oneData.put("SenderCellPhone", receiver_cellphone);
											}
											else 
											{
												//寄件人手機
												oneData.put("SenderCellPhone", "");
											}
											
											//收件人
											oneData.put("ReceiverName",sendName);										

											oneData.put("ReceiverPhone", sendPhone);

											if (sendPhone.startsWith("09")) 
											{
												//收件人手機
												oneData.put("ReceiverCellPhone", sendPhone);
											}
											else 
											{
												//收件人手機,因为必填且09开头，10码，造一个默认的
												oneData.put("ReceiverCellPhone", "0987654321");
											}
											
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
												rtnGetshopno=SenderFamiShop;
											}
											else if (sDeliverytype.equals("7") ||sDeliverytype.equals("16"))//7-11超商 
											{
												oneData.put("LogisticsSubType", "UNIMARTC2C");
												testGetshopno="991182";
												myDeliverytype="16";
												rtnGetshopno=SenderUnimartShop;
											}
											else if (sDeliverytype.equals("10") ||sDeliverytype.equals("18"))//萊而富超商 
											{
												oneData.put("LogisticsSubType", "HILIFEC2C");
												testGetshopno="2001";
												myDeliverytype="18";
												rtnGetshopno=SenderHilifeShop;
											}
											/*
											else if (sDeliverytype.equals("11") ||sDeliverytype.equals("19"))//OK超商 
											{
												oneData.put("LogisticsSubType", "");
											}
											 */
											else 
											{
												errMsg.append("订单号 "+orderno+"，物流類型必須是超商取貨，且綠界暫不支持OK超商，<br/>");
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
												rtnGetshopno=SenderFamiShop;
											}
											else if (sDeliverytype.equals("7") ||sDeliverytype.equals("16"))//7-11超商 
											{
												oneData.put("LogisticsSubType", "UNIMART");
												testGetshopno="991182";
												myDeliverytype="16";
												rtnGetshopno=SenderUnimartShop;
											}
											else if (sDeliverytype.equals("10") ||sDeliverytype.equals("18"))//萊而富超商 
											{
												oneData.put("LogisticsSubType", "HILIFE");
												testGetshopno="2001";
												myDeliverytype="18";
												rtnGetshopno=SenderHilifeShop;
											}
											/*
											else if (sDeliverytype.equals("11") ||sDeliverytype.equals("19"))//OK超商 
											{
												oneData.put("LogisticsSubType", "");
											}
											 */
											else 
											{
												errMsg.append("订单号 "+orderno+"，物流類型必須是超商取貨，且綠界暫不支持OK超商，<br/>");
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
											if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
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
											else 
											{
												//取貨門店												
												oneData.put("ReceiverStoreID",rtnGetshopno);	
											}
					
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

											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******【获取物流单号OrderECBatchExpressnoCreateDCPServiceImp】綠界greenworld失敗:"+resGreenWorld+"******\r\n");

											errMsg.append("订单号"+orderno+",綠界返回失敗,"+resGreenWorld+",<br/>");
										}

										oneData=null;
										//接口返回成功統一處理
										if (bSuccess) 
										{

											//更新便利达康的物流单号
											UptBean ubec=new UptBean("OC_ORDER");
											ubec.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
											ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

											String tempStatusDes="";
											
											//
											if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
											{
												ubec.addUpdateValue("DELIVERYNO", new DataValue(CVSPaymentNo, Types.VARCHAR));
												ubec.addUpdateValue("GREENWORLD_LOGISTICSID", new DataValue(AllPayLogisticsID, Types.VARCHAR));
												ubec.addUpdateValue("GREENWORLD_MERCHANTTRADENO", new DataValue(MerchantTradeNo, Types.VARCHAR));
												ubec.addUpdateValue("GREENWORLD_VALIDNO", new DataValue(CVSValidationNo, Types.VARCHAR));
												ubec.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
												ubec.addUpdateValue("SHIPPINGSHOP", new DataValue(getshopno, Types.VARCHAR));//乐天接单没有取货门店编码
												ubec.addUpdateValue("DELIVERYTYPE", new DataValue(myDeliverytype, Types.VARCHAR));//綠界物流

												tempStatusDes="待配送";
											}
											else 
											{
												ubec.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(CVSPaymentNo, Types.VARCHAR));
												ubec.addUpdateValue("GREENWORLD_RTNLOGISTICSID", new DataValue(AllPayLogisticsID, Types.VARCHAR));
												ubec.addUpdateValue("GREENWORLD_RTNMERCHANTTRADENO", new DataValue(MerchantTradeNo, Types.VARCHAR));
												ubec.addUpdateValue("GREENWORLD_RTNVALIDNO", new DataValue(CVSValidationNo, Types.VARCHAR));
												
												//逆物流货运单表更新
												UptBean ubsp = new UptBean("OC_SHIPMENT");	
												//条件
												ubsp.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
												ubsp.addCondition("SHOPID",new DataValue(eShopId, Types.VARCHAR));
												ubsp.addCondition("EC_ORDERNO",new DataValue(orderno, Types.VARCHAR));
												//值
												ubsp.addUpdateValue("GREENWORLD_RTNORDERNO", new DataValue(CVSPaymentNo, Types.VARCHAR));//更新逆物流单号

												lstData.add(new DataProcessBean(ubsp));
												
												tempStatusDes="逆物流待配送";
											}
											
											lstData.add(new DataProcessBean(ubec));

											//接單日誌
											DataValue[] insValueOrderStatus_LOG = new DataValue[] 
													{ 
															new DataValue(req.geteId(), Types.VARCHAR),
															new DataValue(eShopId, Types.VARCHAR), // 组织编号=门店编号
															new DataValue(eShopId, Types.VARCHAR), // 映射后的门店
															new DataValue(orderno, Types.VARCHAR), //
															new DataValue(loadtype, Types.VARCHAR), //電商平台
															new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
															new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
															new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
															new DataValue(tempStatusDes, Types.VARCHAR), // 状态名称
															new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
															new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
															new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
															new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
															new DataValue("admin", Types.VARCHAR), //操作員編碼
															new DataValue("管理員", Types.VARCHAR), //操作員名稱
															new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
															new DataValue("訂單狀態-->" +tempStatusDes, Types.VARCHAR), //類型名稱+"-->"+狀態名稱
															new DataValue("100", Types.VARCHAR) 
													};
											InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
											ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
											lstData.add(new DataProcessBean(ibOrderStatusLog));	

										}
									}
									else 
									{
										errMsg.append("订单号"+orderno+"，货运厂商资料未设定，<br/>");
									}

								}
								else if (req.getLgPlatformNo().equals("1"))//台湾自配送，前端批量取物流固定写死1=自配送的物流
								{
									//自配送是无需物流接口调用的									
									String tempStatusDes="";
									//
									if (sStatus.equals("2")|| sStatus.equals("4") || sStatus.equals("6") || sStatus.equals("7"))
									{
										//更新黑猫物流单信息
										UptBean ubec=new UptBean("OC_ORDER");
										ubec.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
										ubec.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));

										ubec.addUpdateValue("DELIVERYNO", new DataValue("zps_" +orderno, Types.VARCHAR));
										ubec.addUpdateValue("MEASURENO", new DataValue("", Types.VARCHAR));
										ubec.addUpdateValue("MEASURENAME", new DataValue("", Types.VARCHAR));
										ubec.addUpdateValue("TEMPERATELAYERNO", new DataValue("", Types.VARCHAR));
										ubec.addUpdateValue("TEMPERATELAYERNAME", new DataValue("", Types.VARCHAR));
										
										ubec.addUpdateValue("DISTANCENO", new DataValue("", Types.VARCHAR));
										ubec.addUpdateValue("DISTANCENAME", new DataValue("", Types.VARCHAR));
										ubec.addUpdateValue("RECEIVER_FIVECODE", new DataValue("", Types.VARCHAR));
										ubec.addUpdateValue("RECEIVER_SEVENCODE", new DataValue("", Types.VARCHAR));
										ubec.addUpdateValue("RECEIVER_SITENO", new DataValue("", Types.VARCHAR));
										ubec.addUpdateValue("DELIVERYTYPE", new DataValue("1", Types.VARCHAR));
										ubec.addUpdateValue("EXPRESSBILLTYPE", new DataValue("A", Types.VARCHAR));
										ubec.addUpdateValue("DELIVERYSTUTAS", new DataValue(0, Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
										
										lstData.add(new DataProcessBean(ubec));
										
										tempStatusDes="待配送";
										
										//接單日誌
										DataValue[] insValueOrderStatus_LOG = new DataValue[] 
												{ 
														new DataValue(req.geteId(), Types.VARCHAR),
														new DataValue(eShopId, Types.VARCHAR), // 组织编号=门店编号
														new DataValue(eShopId, Types.VARCHAR), // 映射后的门店
														new DataValue(orderno, Types.VARCHAR), //
														new DataValue(loadtype, Types.VARCHAR), //電商平台
														new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
														new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
														new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
														new DataValue(tempStatusDes, Types.VARCHAR), // 状态名称
														new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
														new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
														new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
														new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
														new DataValue("admin", Types.VARCHAR), //操作員編碼
														new DataValue("管理員", Types.VARCHAR), //操作員名稱
														new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
														new DataValue("訂單狀態-->" +tempStatusDes, Types.VARCHAR), //類型名稱+"-->"+狀態名稱
														new DataValue("100", Types.VARCHAR) 
												};
										InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
										ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
										lstData.add(new DataProcessBean(ibOrderStatusLog));
										
									}
									else 
									{
										errMsg.append("订单号"+orderno+"，此订单状态不允许取物流<br/>");
									}									
								}
								else 
								{
									errMsg.append("订单号"+orderno+"，此物流类型暂不支持<br/>");
								}
							}
							else 
							{
								errMsg.append("订单号"+orderno+"此订单状态不允许取物流单号，<br/>");
							}
						}
						else 
						{
							errMsg.append("虾皮平台已集成，此订单号"+orderno+"不需要再取物流单号，<br/>");
						}
					}
					else 
					{
						errMsg.append("订单号"+orderno+"，已有物流单号<br/>");
					}
				}
				//
				if (getSenderData!=null) 
				{
					getSenderData.clear();
					getSenderData=null;
				}
				

				StaticInfo.dao.useTransactionProcessData(lstData);	

				//有实际成果操作结果
				if (lstData.size()>0) 
				{
					res.setSuccess(true);
				}
			}
			else 
			{
				errMsg.append("订单号不存在，<br/>");
			}

		}
		else 
		{
			errMsg.append("订单号不能为空，<br/>");
		}

		res.setServiceDescription(errMsg.toString());

		egs=null;
		htc=null;
		greenworld=null;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECBatchExpressnoCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECBatchExpressnoCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECBatchExpressnoCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECBatchExpressnoCreateReq req) throws Exception 
	{
		boolean isFail = false;

		StringBuffer errMsg = new StringBuffer("");

		String[] ecOrderno=req.getEcOrderNo();

		if (ecOrderno==null || ecOrderno.length==0) 
		{			
			errMsg.append("订单号不能为空, ");
			isFail = true;
		}

		if (Check.Null(req.getLgPlatformNo())) 
		{
			errMsg.append("货运厂商代码不能为空, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECBatchExpressnoCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_OrderECBatchExpressnoCreateReq>(){};
	}

	@Override
	protected DCP_OrderECBatchExpressnoCreateRes getResponseType() 
	{
		return new DCP_OrderECBatchExpressnoCreateRes();
	}	



}
