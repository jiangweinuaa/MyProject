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
import java.util.List;
import java.util.Map;


import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ec.Shopee;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EcShopee extends InitJob
{

	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(EcShopee.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public EcShopee()
	{

	}

	public EcShopee(String eId,String shopId,String organizationNO, String billNo)
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
			logger.info("\r\n*********虾皮订单抓取EcShopee正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-虾皮订单抓取EcShopee正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********虾皮订单抓取EcShopee定时调用Start:************\r\n");

		boolean more=true;//可能有很多订单

		//虾皮调用
		Shopee shopee=new Shopee();			

		try
		{		

			
			//取第一笔
			String sql="select * from DCP_ECOMMERCE A where A.ECPLATFORMNO='shopee' and A.status='100' ";

			List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
			if (sqllist != null && sqllist.isEmpty() == false)
			{
				for (Map<String, Object> map : sqllist) 
				{
					String apiUrl=map.get("API_URL").toString();
					int partner_id=Integer.parseInt(map.get("PARTNER_ID").toString());
					int shop_id= Integer.parseInt(map.get("STORE_ID").toString());
					String partner_key=map.get("PARTNER_KEY").toString();
					String lastUpdatetime=map.get("LASTUPDATETIME").toString();
					String eId=map.get("EID").toString();
					String inShop=map.get("SHOPID").toString();
					String inWarehouse=map.get("WAREHOUSE").toString();
					String inEccustomerno=map.get("ECCUSTOMERNO").toString();
					String inInvoice=map.get("ISINVOICE").toString();

					String inShopName="";
					
								
					
					//谁在有的表里面干的主键
					if (inEccustomerno.equals("")) 
					{
						inEccustomerno=" ";
					}

					//订单付款方式映射
					String sqlTvMappingPay="select * from DCP_mappingpayment t where t.load_doctype='shopee' and t.status='100' and t.eid='"+eId+"' ";


					//上次拉取订单的时间戳
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

					//今天的0点0分0秒
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					Date zero = calendar.getTime();

					long lastTimestamp=zero.getTime()/1000;

					if(lastUpdatetime.equals("")==false)
					{					
						Date tempdate = sdf.parse(lastUpdatetime);
						lastTimestamp = tempdate.getTime()/1000;					
					}				

					calendar = Calendar.getInstance();
					SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyyMMdd");
					String sysDate=dfDatetime.format(calendar.getTime());
					dfDatetime = new SimpleDateFormat("HHmmss");
					String sysTime=dfDatetime.format(calendar.getTime());

					//訂單日誌時間
					dfDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
					String orderStatusLogTimes=dfDatetime.format(calendar.getTime());


					long lEndTimestamp=System.currentTimeMillis();

					//设置格式
					SimpleDateFormat format =  new SimpleDateFormat("yyyyMMddHHmmss"); 
					String timeText=format.format(lEndTimestamp);

					//处理
					lEndTimestamp=lEndTimestamp/1000;

					//页码
					int pageindex=0;

					while (more)
					{
						String resbody=shopee.GetOrdersByStatus(apiUrl, partner_id, partner_key, shop_id, pageindex,lastTimestamp,lEndTimestamp);

						JSONObject jsonres = new JSONObject(resbody);

						if(jsonres.has("error"))//错误
						{
							more=false;

							String errorno=jsonres.getString("error");
							String errormsg=jsonres.getString("msg");

							logger.error("\r\n******虾皮订单抓取EcShopee报错信息error=" + errorno+",msg=" + errormsg + "******\r\n");
						}
						else
						{
							
									
							//通过配置文件读取
							String langtype="zh_CN";
							List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
							if(lstProd!=null&&!lstProd.isEmpty())
							{
								langtype=lstProd.get(0).getHostLang().getValue();
							}
							//取一下门店名称
							String sqlOrg="select * from DCP_ORG_lang where eid='"+eId+"' and organizationno='"+inShop+"' and status='100' and lang_type='"+langtype+"' ";
							List<Map<String, Object>> sqlGetORG=this.doQueryData(sqlOrg, null);
							if (sqlGetORG != null && sqlGetORG.isEmpty() == false)
							{
								inShopName=sqlGetORG.get(0).get("ORG_NAME").toString();
							}
							
							
							more= jsonres.getBoolean("more");//是否还有更多页

							if (more) 
							{
								pageindex+=1;
							}

							String request_id=jsonres.getString("request_id");//错误追踪ID

							//订单单头列表Object []
							JSONArray orders = jsonres.getJSONArray("orders");

							String[] ordersn_list=new String[orders.length()];

							String inMultiOrderno="";
							for(int i=0;i<orders.length();i++)
							{
								String ordersn=orders.getJSONObject(i).getString("ordersn");//订单号
								String order_status=orders.getJSONObject(i).getString("order_status");//订单状态READY_TO_SHIP
								long update_time=orders.getJSONObject(i).getLong("update_time");//更新时间
								//System.out.println(ordersn);

								SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");							
								Date date = new Date(update_time*1000);							
								String sdate = simpleDateFormat.format(date);							
								simpleDateFormat = new SimpleDateFormat("HHmmss");		
								String stime = simpleDateFormat.format(date);	

								inMultiOrderno=inMultiOrderno + "'" + ordersn+"',";
								ordersn_list[i]=ordersn;
							}

							if(inMultiOrderno.equals("")==false)
							{
								inMultiOrderno=inMultiOrderno.substring(0,inMultiOrderno.length()-1);
							}				


							//有订单
							if(orders.length()>0)
							{							
								//列表SQL
								List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		

								//企业编码注意===需要替换
								String sqlExistOrdernoString="SELECT * FROM DCP_ORDER T WHERE T.eid='"+eId+"' AND T.ORDERNO IN("+inMultiOrderno+")";
								List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sqlExistOrdernoString, null);

								//查询付款方式映射
								List<Map<String, Object>> sqllistPaymapping=this.doQueryData(sqlTvMappingPay, null);

								//50个订单发送一次
								int a=ordersn_list.length/50;

								if(ordersn_list.length%50==0)//防止整除情况
								{
									a=a-1;
								}


								//插入单头
								String[] columnsOrder = {
										"EID",
										"SHOPID",
										"SHOPNAME",
										"ORDERNO",
										"LOAD_DOCTYPE",
										"SHIPPINGSHOP",
										"SHIPPINGSHOPNAME",
										"SHIPTYPE",//1.外卖平台配送 2.门店配送(台湾显示为宅配) 3.顾客自提 5 总部配送 6.超商
										"CONTMAN",
										"CONTTEL",
										"ADDRESS",
										"SHIPDATE",
										"SHIPTIME",
										"SHIPHOURTYPE",
										"MEMO",
										"CREATE_DATETIME",
										"ORDER_SN",
										"TOT_OLDAMT",
										"TOT_AMT",
										"TOT_QTY",
										"INCOMEAMT",
										"SERVICECHARGE",
										"TOT_DISC",
										"SELLER_DISC",
										"PLATFORM_DISC",
										"SHIPFEE",
										"TOTSHIPFEE",
										"RSHIPFEE",
										"STATUS",
										"SDATE",
										"STIME",
										"STATUS",
										"GETMAN",
										"GETMANTEL",
										"GETMANEMAIL",
										"CARDNO",
										"MEMBERNAME",
										"PROVINCE",
										"CITY",
										"COUNTY",
										"STREET",
										"CURRENCYNO",
										"PICKUPWAY",
										"PAYSTATUS",
										"PAYAMT",
										"DELIVERYTYPE",
										"EXPRESSBILLTYPE",//托运单单别 黑猫需要
										"SHOPEE_MODE",
										"SHOPEE_ADDRESS_ID",
										"SHOPEE_PICKUP_TIME_ID",
										"SHOPEE_BRANCH_ID",
										"SHOPEE_SENDER_REAL_NAME",
										"ECCUSTOMERNO",//电商归属客户代号
										"ISINVOICE",
										"EXCEPTIONSTATUS",//商品异常状态(找不到商品)
										"EXCEPTIONMEMO",//异常商品备注
										"ORDERSHOP",//超商门店编码
										"ORDERSHOPNAME",//超商门店名称
                                        "BDATE",
                                        "PARTITION_DATE",//分区字段，值同BDATE
                                        "UPDATE_TIME",
                                        "TRAN_TIME"
								};

								//插入1笔总折扣
								String[] columnsOrderAgio = {
										"EID",
										"SHOPID",
										"ORDERNO",									
										"CUSTOMERNO",
										"ITEM",
										"PROMNAME",
										"AGIOAMT",
										"SELLER_DISC",
										"PLATFORM_DISC",
										"SDATE",
										"STIME",								
										"STATUS",
										"LOAD_DOCTYPE",
                                        "PARTITION_DATE",//分区字段，值同BDATE

								};


								//插入付款方式
								String[] columnsOrderPay = {
										"EID",
										"SHOPID",
										"ORDERNO",									
										"CUSTOMERNO",
										"ITEM",
										"PAYCODE",
										"PAYCODEERP",
										"PAYNAME",
										"CARDNO",
										"CTTYPE",
										"PAYSERNUM",
										"SERIALNO",
										"REFNO",
										"TERIMINALNO",
										"DESCORE",
										"PAY",
										"EXTRA",
										"CHANGED",
										"BDATE",
										"ISORDERPAY",
										"LOAD_DOCTYPE",
										"ORDER_PAYCODE",
										"ISONLINEPAY",
										"RCPAY",									
										"STATUS",
                                        "PARTITION_DATE",//分区字段，值同BDATE
                                        "UPDATE_TIME",
                                        "TRAN_TIME"
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


								for(int i=0;i<a+1;i++)
								{								
									int len=0;
									if(ordersn_list.length>50)
									{
										len=ordersn_list.length-(a*50);
									}
									else
									{
										len=ordersn_list.length;
									}

									String[] tempArr=new String[len];

									System.arraycopy(ordersn_list, i*50, tempArr, 0,len);

									//订单明细
									resbody="";
									resbody=shopee.GetOrderDetails(apiUrl, partner_id, partner_key, shop_id, tempArr);

									JSONObject jsonresDetail = new JSONObject(resbody);

									//找到的订单
									if(jsonresDetail.has("orders"))
									{
										//电商库存处理
										JSONObject jsonECStock=new JSONObject();
										JSONArray ecHeaderArr=new JSONArray();									
										JSONArray ecDetailArr = new JSONArray(); 
										
										JSONArray ordersDetail = jsonresDetail.getJSONArray("orders");
										for(int b=0;b<ordersDetail.length();b++)
										{
											//订单号
											String ordersn=ordersDetail.getJSONObject(b).getString("ordersn");

											//电商库存处理
											JSONObject ecHeader = new JSONObject();
											ecHeader.put("eId", eId);
											ecHeader.put("ecStockNO", ordersn);//单号
											ecHeader.put("docType", "1");//库存异动方向：0入  1出
											ecHeader.put("opNo", "admin");
											ecHeader.put("loadType", "2");//来源类型： 0手动上下架 1调拨收货 2订单 3调拨出库 
											ecHeader.put("loadDocno", "");//来源单号
											ecHeader.put("loadDocShop", "");
											
											//检查订单是否存在
											Map<String, Object> map_condition = new HashMap<String, Object>();
											map_condition.put("ORDERNO", ordersn);		
											List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getQData,map_condition,false);	
											if (getQHeader1!=null && getQHeader1.size()>0) 
											{
												continue;
											}

											//******获取发货资料模式******
											//*************************************
											int shopee_mode=1;
											String shopee_address_id="";
											String shopee_pickup_time_id="";
											String shopee_branch_id="";
											String shopee_sender_real_name="";

											resbody="";
											resbody=shopee.GetLogisticInfo(apiUrl, partner_id, partner_key, shop_id, ordersn);

											JSONObject jsonLogiRes = new JSONObject(resbody);

											//jsonLogiRes
											request_id=jsonLogiRes.getString("request_id");		
											if(jsonLogiRes.has("error"))//错误
											{
												String errorno=jsonLogiRes.getString("error");
												String errormsg=jsonLogiRes.getString("msg");
												logger.error("\r\n*********虾皮订单抓取EcShopee 订单号="+ordersn+",接口:GetLogisticInfo,错误代码="+errorno +",错误信息="+errormsg+"************\r\n");
											}
											else
											{
												//这个栏位指定使用什么栏位去发货
												JSONObject info_needed=jsonLogiRes.getJSONObject("info_needed");	

												if(info_needed.has("pickup"))//存在就使用此方式
												{
													shopee_mode=1;
													/*
													JSONArray bpickup=info_needed.getJSONArray("pickup");//第1种方式
													for(int c=0;c<bpickup.length();c++)
													{
														String ckeyName=bpickup.getString(c);
														if(ckeyName.equals("address_id"))
														{

														}
														if(ckeyName.equals("pickup_time_id"))
														{

														}					

													}
													 */
												}

												if(info_needed.has("dropoff"))//存在就使用此方式
												{
													shopee_mode=2;

													JSONArray bdropoff=info_needed.getJSONArray("dropoff");//第2种方式					
													for(int c=0;c<bdropoff.length();c++)
													{
														String ckeyName=bdropoff.getString(c);
														if(ckeyName.equals("branch_id"))
														{
															//使用第2中方式dropoff的branch_id去发货

														}
														if(ckeyName.equals("sender_real_name"))
														{
															//使用寄件人姓名去发货											
															shopee_sender_real_name=PosPub.getPARA_SMS(StaticInfo.dao, eId, inShop, "SenderName");

															if (shopee_sender_real_name==null || shopee_sender_real_name.equals("")) 
															{
																shopee_sender_real_name=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "SenderName");
															}
														}
														if(ckeyName.equals("tracking_no"))
														{
															//使用托运单号去发货
														}						

														break;
													}

												}

												if(info_needed.has("non_integrated"))//存在就使用此方式
												{
													shopee_mode=3;
													/*
													JSONArray non_integrated=info_needed.getJSONArray("non_integrated");//第3种方式,已有快递单号==手工填单
													for(int c=0;c<non_integrated.length();c++)
													{
														String ckeyName=non_integrated.getString(c);
														if(ckeyName.equals("tracking_no"))
														{

														}

													}
													 */
												}

												//第1种方式pickup
												JSONObject pickup = jsonLogiRes.getJSONObject("pickup");
												//这个对象可能是空
												if (pickup.length()>0)
												{
													JSONArray address_list = pickup.getJSONArray("address_list");			
													for(int k=0;k<address_list.length();k++)
													{
														long address_id=address_list.getJSONObject(k).getLong("address_id");
														String country=address_list.getJSONObject(k).getString("country");
														String state=address_list.getJSONObject(k).getString("state");
														String city=address_list.getJSONObject(k).getString("city");
														String address=address_list.getJSONObject(k).getString("address");
														String zipcode=address_list.getJSONObject(k).getString("zipcode");
														String district=address_list.getJSONObject(k).getString("district");
														String town=address_list.getJSONObject(k).getString("town");

														//地址代码
														shopee_address_id=Long.toString(address_id);

														//取货时间列表
														Object listArray = new JSONTokener(address_list.getJSONObject(k).optString("time_slot_list")).nextValue();

														if (listArray instanceof JSONArray)
														{
															JSONArray time_slot_list=address_list.getJSONObject(k).getJSONArray("time_slot_list");
															for (int d = 0; d < time_slot_list.length(); d++) 
															{
																String pickup_time_id= time_slot_list.getJSONObject(d).getString("pickup_time_id");//取货ID
																long date= time_slot_list.getJSONObject(d).getLong("date");//取货日期

																//System.out.println(pickup_time_id);
																//System.out.println(date);

																//时间槽代码
																shopee_pickup_time_id=pickup_time_id;

																break;
															}

														}
														else 
														{


															//错误
															JSONObject time_slot_list=address_list.getJSONObject(k).getJSONObject("time_slot_list");
															if (time_slot_list.has("error")) 
															{
																String msg= time_slot_list.getString("msg");//msg
																String error= time_slot_list.getString("error");//error											

																//System.out.println(msg);							
																//System.out.println(error);

																logger.error("\r\n*********虾皮订单抓取EcShopee 订单号="+ordersn+",接口:GetLogisticInfo,错误代码="+error +",错误信息="+msg+"************\r\n");

															}									

														}

													}	

												}


												//第2种方式dropoff
												JSONObject dropoff=jsonLogiRes.getJSONObject("dropoff");
												//这个对象可能是空
												if (dropoff.length()>0)
												{
													JSONArray branch_list=dropoff.getJSONArray("branch_list");
													for(int p=0;p<branch_list.length();p++)
													{
														long branch_id=branch_list.getJSONObject(p).getLong("branch_id");
														String country=branch_list.getJSONObject(p).getString("country");
														String state=branch_list.getJSONObject(p).getString("state");
														String city=branch_list.getJSONObject(p).getString("city");
														String address=branch_list.getJSONObject(p).getString("address");
														String zipcode=branch_list.getJSONObject(p).getString("zipcode");
														String district=branch_list.getJSONObject(p).getString("district");
														String town=branch_list.getJSONObject(p).getString("town");

														//发货分支代码
														shopee_branch_id=Long.toString(branch_id);
														break;
													}
												}

											}
											jsonLogiRes=null;


											String country=ordersDetail.getJSONObject(b).getString("country");//国家，2码
											String currency=ordersDetail.getJSONObject(b).getString("currency");//货币单位，3码
											boolean cod=ordersDetail.getJSONObject(b).getBoolean("cod");//是否货到付款

											String tracking_no="";//托运单号
											if (ordersDetail.getJSONObject(b).has("tracking_no")) 
											{
												if (ordersDetail.getJSONObject(b).isNull("tracking_no")) 
												{
													tracking_no="";
												}
												else 
												{
													tracking_no=ordersDetail.getJSONObject(b).getString("tracking_no");//托运单号
												}
											}


											int days_to_ship=ordersDetail.getJSONObject(b).getInt("days_to_ship");//卖家几天发货
											double estimated_shipping_fee=ordersDetail.getJSONObject(b).getDouble("estimated_shipping_fee");//预计运费		
											//这个字段是没值的,要到实际发货后才可能有值
											//double actual_shipping_cost=ordersDetail.getJSONObject(b).getDouble("actual_shipping_cost");//货运成本
											double total_amount=ordersDetail.getJSONObject(b).getDouble("total_amount");//买家实际付款金额
											double escrow_amount=ordersDetail.getJSONObject(b).getDouble("escrow_amount");//卖家实际收到金额
											String order_status=ordersDetail.getJSONObject(b).getString("order_status");//订单状态
											String shipping_carrier=ordersDetail.getJSONObject(b).getString("shipping_carrier");//买家选择的物流商
											String payment_method=ordersDetail.getJSONObject(b).getString("payment_method");//买家付款方式

											//对应的ERP支付方式
											String erp_paycode="";
											String erp_payName="";
											String erp_paycodeERP="";

											//过滤付款方式映射
											map_condition = new HashMap<String, Object>();
											map_condition.put("ORDER_PAYCODE",payment_method );//这里采用包含的关系,因为那些信用卡和银行太多了		
											List<Map<String, Object>> getQPAY=MapDistinct.getWhereMap(sqllistPaymapping,map_condition,false,2);	
											if (getQPAY==null || getQPAY.size()==0) 
											{
												map_condition = new HashMap<String, Object>();
												map_condition.put("ORDER_PAYCODE", "ALL");		
												getQPAY=MapDistinct.getWhereMap(sqllistPaymapping,map_condition,false);	
												if (getQPAY!=null && getQPAY.size()>0) 
												{
													erp_paycode=getQPAY.get(0).get("PAYCODE").toString();
													erp_payName=getQPAY.get(0).get("PAYNAME").toString();
													erp_paycodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
												}
											}
											else 
											{
												erp_paycode=getQPAY.get(0).get("PAYCODE").toString();
												erp_payName=getQPAY.get(0).get("PAYNAME").toString();
												erp_paycodeERP=getQPAY.get(0).get("PAYCODEERP").toString();
											}

											boolean goods_to_declare=ordersDetail.getJSONObject(b).getBoolean("goods_to_declare");//海关申报，trackingNo产生后才准确
											String message_to_seller=ordersDetail.getJSONObject(b).getString("message_to_seller");//买家留言
											String note=ordersDetail.getJSONObject(b).getString("note");//卖家的便签备注
											long note_update_time=ordersDetail.getJSONObject(b).getLong("note_update_time");//卖家便签备注时间
											long create_time=ordersDetail.getJSONObject(b).getLong("create_time");//
											create_time=create_time*1000L;

											long update_time=ordersDetail.getJSONObject(b).getLong("update_time");//
											long ship_by_date=ordersDetail.getJSONObject(b).getLong("ship_by_date");//配送日期
											ship_by_date=ship_by_date*1000L;

											//转换
											Date date = new Date(ship_by_date);
											SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
											String sShip_by_date=simpleDateFormat.format(date);

											//转换
											date = new Date(create_time);
											simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
											String sCreate_time=simpleDateFormat.format(date);


											//这个字段是没值的,要到实际付完款后才可能有值
											//int pay_time=ordersDetail.getJSONObject(b).getInt("pay_time");//付款时间
											String dropshipper=ordersDetail.getJSONObject(b).getString("dropshipper");//印尼用
											//String credit_card_number=ordersDetail.getJSONObject(b).getString("credit_card_number");//信用卡号，后4码
											String buyer_username=ordersDetail.getJSONObject(b).getString("buyer_username");//买家用户名
											String dropshipper_phone=ordersDetail.getJSONObject(b).getString("dropshipper_phone");//印尼用				

											//收货地址
											JSONObject recipient_address=ordersDetail.getJSONObject(b).getJSONObject("recipient_address");
											String name=recipient_address.getString("name");//姓名
											String phone=recipient_address.getString("phone");//电话
											//根据不同的国家选择性栏位
											String town="";
											if(recipient_address.has("town"))
											{
												town=recipient_address.getString("town");//乡镇
											}
											String district="";
											if(recipient_address.has("district"))
											{
												district=recipient_address.getString("district");//区县
											}
											String city="";
											if(recipient_address.has("city"))
											{
												city=recipient_address.getString("city");//城市
											}
											String state="";
											if(recipient_address.has("state"))
											{
												state=recipient_address.getString("state");//省份
											}

											String rcountry="";
											if(recipient_address.has("country"))
											{
												rcountry=recipient_address.getString("country");//国家，2码
											}

											String zipcode="";
											if(recipient_address.has("zipcode"))
											{
												zipcode=recipient_address.getString("zipcode");//邮编
											}

											String full_address=recipient_address.getString("full_address");//详细地址



											String deliverytype="";//配送商类型
											String getshop="";
											String getshopName="";
											int pickupWay=2;//1：超取 2：宅配
											String expressBiltype="";

											//
											if (shipping_carrier.contains("全家")) 
											{
												deliverytype="8";
												pickupWay=1;

												String[] splitFullStrings=full_address.split(" ");
												getshopName=splitFullStrings[0];
												getshop=splitFullStrings[2].substring(3);
												//System.out.println(getshop+" , "+getshopName);

											}
											else if (shipping_carrier.contains("黑貓")) 
											{
												deliverytype="9";
												pickupWay=2;
												if (cod) 
												{
													expressBiltype="B";
												}
												else 
												{
													expressBiltype="A";
												}											
											}
											else if (shipping_carrier.contains("7-11")) 
											{
												deliverytype="7";
												pickupWay=1;

												String[] splitFullStrings=full_address.split(" ");
												getshopName=splitFullStrings[0]+splitFullStrings[1];
												getshop=splitFullStrings[3].substring(2);
												//System.out.println(getshop+" , "+getshopName);

											}
											else if (shipping_carrier.contains("Hi-Life") || shipping_carrier.contains("萊爾富")) 
											{
												deliverytype="10";
												pickupWay=1;

												String[] splitFullStrings=full_address.split(" ");
												getshopName=splitFullStrings[0];
												getshop=splitFullStrings[2];
												//System.out.println(getshop+" , "+getshopName);
											}
											else if (shipping_carrier.contains("OK Mart")) 
											{
												deliverytype="11";
												pickupWay=1;

												String[] splitFullStrings=full_address.split(" ");
												getshopName=splitFullStrings[0];
												getshop=splitFullStrings[2].substring(1);
												//System.out.println(getshop+" , "+getshopName);
											}
											else if (shipping_carrier.contains("mingjie")) 
											{
												deliverytype="12";
												pickupWay=2;
											}
											else if (shipping_carrier.contains("中華郵政")) 
											{
												deliverytype="13";
												pickupWay=2;
											}
											else if (shipping_carrier.contains("賣家宅配")) 
											{
												deliverytype="14";
												pickupWay=2;
											}
											else if (shipping_carrier.contains("新竹")) 
											{
												deliverytype="15";
												pickupWay=2;
											}
											else 
											{
												//
												deliverytype="";//暂不支持
												pickupWay=2;
											}

											String shiptype="";
											if (pickupWay==2) 
											{
												shiptype="2";
											}
											else 
											{
												shiptype="6";
											}

											double tot_qty=0;
											double tot_disc=0;
											double tot_OldAMT=0;
											double tot_AMT=0;

											//找不到商品的异常
											String sExceptionStatus="N";
											StringBuffer sExceptionMemo=new StringBuffer("");

											//插入明细
											String[] columnsOrderDetail = {
													"EID",
													"SHOPID",
													"ORDERNO",
													"LOAD_DOCTYPE",
													"ITEM",
													"PLUNO",
													"PLUBARCODE",
													"PLUNAME",
													"SPECNAME",
													"UNIT",
													"PRICE",
													"QTY",
													"DISC",
													"AMT",
													"STATUS",
													"ORDER_SN",
													"ECPLUNO",
                                                    "PARTITION_DATE",//分区字段，值同BDATE
											};

											//明细
											JSONArray items=ordersDetail.getJSONObject(b).getJSONArray("items");
											for(int j=0;j<items.length();j++)
											{
												long item_id=items.getJSONObject(j).getLong("item_id");//平台商品编码
												String item_name=items.getJSONObject(j).getString("item_name");//商品名称
												//商家品号pluno
												String item_sku=items.getJSONObject(j).getString("item_sku");//商品SKU
												long variation_id=items.getJSONObject(j).getLong("variation_id");//规格编码
												String variation_name=items.getJSONObject(j).getString("variation_name");//规格名称
												String variation_sku=items.getJSONObject(j).getString("variation_sku");//规格sku
												//数量
												int variation_quantity_purchased=items.getJSONObject(j).getInt("variation_quantity_purchased");//购买此规格数量
												//原单价
												double variation_original_price=items.getJSONObject(j).getDouble("variation_original_price");//购买此规格的原价
												//成交单价
												double variation_discounted_price=items.getJSONObject(j).getDouble("variation_discounted_price");//折扣价
												boolean is_wholesale=items.getJSONObject(j).getBoolean("is_wholesale");//买方是否以批发价购买
												double weight=items.getJSONObject(j).getDouble("weight");//重量
												boolean is_add_on_deal=items.getJSONObject(j).getBoolean("is_add_on_deal");//是否附属品false
												boolean is_main_item=items.getJSONObject(j).getBoolean("is_main_item");//是否主商品false
												int add_on_deal_id=items.getJSONObject(j).getInt("add_on_deal_id");//附属品ID 0

												double disc=variation_original_price-variation_discounted_price;

												tot_qty+=variation_quantity_purchased;
												tot_disc+=(disc*variation_quantity_purchased);
												tot_OldAMT+=variation_original_price*variation_quantity_purchased;
												tot_AMT+=variation_discounted_price*variation_quantity_purchased;

												//
												String pluno="";											
												if (variation_sku.equals("")) 
												{
													pluno=item_sku;
												}
												else 
												{
													pluno=variation_sku;
												}

												//
												String sExeptionSql="select a.pluNo ,  a.sunit, b.plubarcode  from DCP_GOODS a "
														+ " left join DCP_BARCODE b on a.eId = b.eId and a.pluNo = b.pluNo and b.STATUS='100' "
														+ " where a.eId = '"+eId+"'  "
														+ " and a.pluNo = '"+pluno+"' "
														+ " and a.STATUS='100' ";

												List<Map<String, Object>> sqlExceptionList=this.doQueryData(sExeptionSql, null);
												if (sqlExceptionList == null || sqlExceptionList.isEmpty())
												{			
													//只需一次异常赋值
													if (sExceptionStatus.equals("Y")==false) 
													{
														sExceptionStatus="Y";
													}

													sExceptionMemo.append(pluno+"__("+ item_name+"),\r\n");
												}
												sqlExceptionList=null;


												DataValue[] insValueOrderDetail =new DataValue[]
														{
																new DataValue(eId, Types.VARCHAR), 
																new DataValue(inShop, Types.VARCHAR), 
																new DataValue(ordersn, Types.VARCHAR), 
																new DataValue("shopee", Types.VARCHAR), 
																new DataValue(j+1, Types.INTEGER), //item
																new DataValue(pluno, Types.VARCHAR), //pluno
																new DataValue(pluno, Types.VARCHAR), //plubarcode
																new DataValue(item_name, Types.VARCHAR), //pluname
																new DataValue(variation_name, Types.VARCHAR), //specname
																new DataValue("", Types.VARCHAR), //unit
																new DataValue(variation_original_price, Types.DOUBLE),//price 
																new DataValue(variation_quantity_purchased, Types.DOUBLE), //qty
																new DataValue(disc, Types.DOUBLE),//折扣 
																new DataValue(variation_quantity_purchased*variation_discounted_price, Types.DOUBLE), //amt
																new DataValue("100", Types.VARCHAR), 
																new DataValue("", Types.VARCHAR), //order_sn
																new DataValue(item_id, Types.VARCHAR), //ecpluno
                                                                new DataValue(sysDate, Types.NUMERIC),//分区字段
														};

												InsBean ibOrderDetail = new InsBean("DCP_ORDER_DETAIL", columnsOrderDetail);//分区字段已处理
												ibOrderDetail.addValues(insValueOrderDetail);
												lstData.add(new DataProcessBean(ibOrderDetail));	

												
												//电商库存处理
												JSONObject ecDetail = new JSONObject();
												ecDetail.put("ecPlatformNO", "shopee");//电商平台
												ecDetail.put("loadDocItem", "1");//来源项次
												ecHeader.put("shopId", inShop);
												ecDetail.put("pluNO", pluno);
												ecDetail.put("pluBarcode", pluno);
												ecDetail.put("unit", " ");
												ecDetail.put("qty", Integer.toString(variation_quantity_purchased));
												ecDetailArr.put(ecDetail);
												
											}


											String payStatuString="";
											double payAmt=0;
											if(cod)
											{
												payStatuString="1";
												payAmt=0;
											}
											else 
											{
												payStatuString="3";
												payAmt=tot_AMT+estimated_shipping_fee;
											}


											//插入订单表
											if (getQHeader1==null || getQHeader1.size()==0) 
											{
												DataValue[] insValueOrder =new DataValue[]
														{
																new DataValue(eId, Types.VARCHAR), 								
																new DataValue(inShop, Types.VARCHAR), 
																new DataValue(inShopName, Types.VARCHAR),
																new DataValue(ordersn, Types.VARCHAR), 
																new DataValue("shopee", Types.VARCHAR), 
																new DataValue(inShop, Types.VARCHAR), 
																new DataValue(inShopName, Types.VARCHAR),
																new DataValue(shiptype, Types.VARCHAR),////1.外卖平台配送 2.门店配送(台湾显示为宅配) 3.顾客自提 5 总部配送 6.超商
																new DataValue(name, Types.VARCHAR), 
																new DataValue(phone, Types.VARCHAR), 
																new DataValue(full_address, Types.VARCHAR), //配送地址
																new DataValue(sShip_by_date, Types.VARCHAR), //配送日期
																new DataValue("", Types.VARCHAR), 	//配送时间						
																new DataValue("1", Types.VARCHAR), //配送时段		
																new DataValue(message_to_seller+"【卖家实收金额："+total_amount +"】", Types.VARCHAR), 
																new DataValue(sCreate_time, Types.VARCHAR), 
																new DataValue("", Types.VARCHAR),//交易流水号 		
																new DataValue(tot_OldAMT+estimated_shipping_fee, Types.DOUBLE), //原价金额
																new DataValue(tot_AMT+estimated_shipping_fee, Types.DOUBLE),//用户实际支付金额 								
																new DataValue(tot_qty, Types.DOUBLE), //总数量
																new DataValue(escrow_amount, Types.DOUBLE),//商家实收金额
																new DataValue(0, Types.DOUBLE),//平台服务费
																new DataValue(tot_disc, Types.DOUBLE), //总折扣
																new DataValue(tot_disc, Types.DOUBLE), //商户优惠总额
																new DataValue(0, Types.DOUBLE), //平台优惠总额
																new DataValue(estimated_shipping_fee, Types.DOUBLE), //预计运费
																new DataValue(estimated_shipping_fee, Types.DOUBLE), //总配送费
																new DataValue(0, Types.DOUBLE), //配送费减免
																new DataValue("2", Types.VARCHAR), //状态
																new DataValue(sysDate, Types.VARCHAR), 								
																new DataValue(sysTime, Types.VARCHAR), 
																new DataValue("100", Types.VARCHAR), 
																new DataValue(name, Types.VARCHAR), //收货人
																new DataValue(phone, Types.VARCHAR),//收货人电话 
																new DataValue("", Types.VARCHAR),//收货人email 								
																new DataValue("", Types.VARCHAR),//会员卡
																new DataValue("", Types.VARCHAR),//会员姓名
																new DataValue(state, Types.VARCHAR), 
																new DataValue(city, Types.VARCHAR), 
																new DataValue(district, Types.VARCHAR), 								
																new DataValue(town, Types.VARCHAR), 
																new DataValue(2, Types.INTEGER), //币别 1:人民币 2：台币
																new DataValue(pickupWay, Types.INTEGER), //取货类型
																new DataValue(payStatuString, Types.VARCHAR),//货到付款
																new DataValue(payAmt, Types.DOUBLE),
																new DataValue(deliverytype, Types.VARCHAR),//物流类型
																new DataValue(expressBiltype, Types.VARCHAR),//物流单类型
																new DataValue(shopee_mode, Types.VARCHAR),//虾皮用
																new DataValue(shopee_address_id, Types.VARCHAR),//虾皮用
																new DataValue(shopee_pickup_time_id, Types.VARCHAR),//虾皮用
																new DataValue(shopee_branch_id, Types.VARCHAR),//虾皮用
																new DataValue(shopee_sender_real_name, Types.VARCHAR),//虾皮用
																new DataValue(inEccustomerno, Types.VARCHAR),//归属客户
																new DataValue(inInvoice, Types.VARCHAR),//是否开发票
																new DataValue(sExceptionStatus, Types.VARCHAR),//商品异常标记
																new DataValue(sExceptionMemo.toString(), Types.VARCHAR),//异常商品记录
																new DataValue(getshop, Types.VARCHAR),//超商门店编码
																new DataValue(getshopName, Types.VARCHAR),//超商门店名称
                                                                new DataValue(sysDate, Types.VARCHAR),//bdate
                                                                new DataValue(sysDate, Types.NUMERIC),//分区字段
                        										new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        										new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
														};

												InsBean ibOrder = new InsBean("DCP_ORDER", columnsOrder);//分区字段已处理
												ibOrder.addValues(insValueOrder);
												lstData.add(new DataProcessBean(ibOrder));									


												//订单折扣
												DataValue[] insValueOrderAgio =new DataValue[]
														{
																new DataValue(eId, Types.VARCHAR), 
																new DataValue(inShop, Types.VARCHAR), 
																new DataValue(ordersn, Types.VARCHAR), 
																new DataValue(inEccustomerno, Types.VARCHAR), //归属客户
																new DataValue(1, Types.INTEGER), //ITME
																new DataValue("虾皮折扣", Types.VARCHAR), //活动名称
																new DataValue(tot_disc, Types.DOUBLE), //总折扣额
																new DataValue(tot_disc, Types.DOUBLE), //商家承担额
																new DataValue(0, Types.DOUBLE), //平台承担额
																new DataValue(sysDate, Types.VARCHAR), //系统日期
																new DataValue(sysTime, Types.VARCHAR), //系统时间
																new DataValue("100", Types.VARCHAR), 
																new DataValue("shopee", Types.VARCHAR), //平台代码
                                                                new DataValue(sysDate, Types.NUMERIC),//分区字段
														};

												InsBean ibOrderAgio = new InsBean("DCP_ORDER_AGIO", columnsOrderAgio);//分区字段已处理
												ibOrderAgio.addValues(insValueOrderAgio);
												lstData.add(new DataProcessBean(ibOrderAgio));											

												//订单付款
												DataValue[] insValueOrderPay =new DataValue[]
														{
																new DataValue(eId, Types.VARCHAR), 
																new DataValue(inShop, Types.VARCHAR), 
																new DataValue(ordersn, Types.VARCHAR), 
																new DataValue(inEccustomerno, Types.VARCHAR), //归属客户
																new DataValue(1, Types.INTEGER), //ITME
																new DataValue(erp_paycode, Types.VARCHAR), //paycode
																new DataValue(erp_paycodeERP, Types.VARCHAR), //paycodeerp
																new DataValue(erp_payName, Types.VARCHAR), //paycodename
																new DataValue("", Types.VARCHAR), //cardno
																new DataValue("", Types.VARCHAR), //cttype
																new DataValue("", Types.VARCHAR), //paysernum
																new DataValue("", Types.VARCHAR), //serialno
																new DataValue("", Types.VARCHAR), //refno
																new DataValue("", Types.VARCHAR), //teriminalno
																new DataValue(0, Types.DOUBLE), //descore
																new DataValue(tot_AMT+estimated_shipping_fee, Types.DOUBLE), //pay
																new DataValue(0, Types.DOUBLE), //extra
																new DataValue(0, Types.DOUBLE), //changed
																new DataValue(sysDate, Types.VARCHAR), //bdate
																new DataValue("N", Types.VARCHAR), //isorderpay
																new DataValue("shopee", Types.VARCHAR), //load_doctype
																new DataValue(payment_method, Types.VARCHAR), //order_paycode
																new DataValue("N", Types.VARCHAR), //isonlinepay
																new DataValue("0", Types.DOUBLE), //rcpay
																new DataValue("100", Types.VARCHAR), //STATUS
                                                                new DataValue(sysDate, Types.NUMERIC),//分区字段
                        										new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        										new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
														};

												InsBean ibOrderPay = new InsBean("DCP_ORDER_PAY", columnsOrderPay);//分区字段已处理
												ibOrderPay.addValues(insValueOrderPay);
												lstData.add(new DataProcessBean(ibOrderPay));	

												//接單日誌
												DataValue[] insValueOrderStatus_LOG = new DataValue[] 
														{ 
																new DataValue(eId, Types.VARCHAR),
																new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
																new DataValue(inShop, Types.VARCHAR), // 映射后的门店
																new DataValue(ordersn, Types.VARCHAR), //
																new DataValue("shopee", Types.VARCHAR), //電商平台
																new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
																new DataValue("2", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																new DataValue("已接單", Types.VARCHAR), // 状态名称
																new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																new DataValue("admin", Types.VARCHAR), //操作員編碼
																new DataValue("管理員", Types.VARCHAR), //操作員名稱
																new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																new DataValue("訂單狀態-->已接單", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																new DataValue("100", Types.VARCHAR) 
														};
												InsBean ibOrderStatusLog = new InsBean("DCP_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
												ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
												lstData.add(new DataProcessBean(ibOrderStatusLog));	
												
											}								

											//电商库存处理
											ecHeader.put("detail", ecDetailArr);
											ecHeaderArr.put(ecHeader);									
										}									
										
										//电商库存处理
										jsonECStock.put("datas", ecHeaderArr);									
										lstData.addAll(PosPub.UpdateEC_Stock(StaticInfo.dao, jsonECStock.toString()));
										
										ecDetailArr=null;
										ecHeaderArr=null;
										jsonECStock=null;
										
										
									}		
									else 
									{
										//找不到的订单
										if(jsonres.has("errors"))
										{
											//找不到的订单
											JSONArray errors = jsonres.getJSONArray("errors");
											for(int c=0;c<errors.length();c++)
											{
												String errorOrderSN=errors.getString(c);
												//System.out.println(errorOrderSN);
												logger.error("\r\n*********虾皮订单抓取EcShopee 订单号="+errorOrderSN+"找不到:GetOrderDetails************\r\n");
												break;
											}

										}
									}								

									String request_idDetail=jsonres.getString("request_id");//错误追踪ID
								}


								//
								UptBean ubec=new UptBean("DCP_ECOMMERCE");
								ubec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								ubec.addCondition("ECPLATFORMNO", new DataValue("shopee", Types.VARCHAR));

								ubec.addUpdateValue("LASTUPDATETIME", new DataValue(timeText, Types.VARCHAR));

								lstData.add(new DataProcessBean(ubec));		

								//批处理执行**********************************************
								//****************************************************
								StaticInfo.dao.useTransactionProcessData(lstData);

								getQData=null;
							}

						}

						jsonres=null;
					}
				}
			}
			else				
			{
				logger.error("\r\n*********虾皮订单抓取EcShopee API资料未设置:************\r\n");
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

				logger.error("\r\n******虾皮订单抓取EcShopee报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******虾皮订单抓取EcShopee报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			more=false;//

			shopee=null;

			bRun=false;//
			logger.info("\r\n*********虾皮订单抓取EcShopee定时调用End:************\r\n");
		}
		return sReturnInfo;

	}





}
