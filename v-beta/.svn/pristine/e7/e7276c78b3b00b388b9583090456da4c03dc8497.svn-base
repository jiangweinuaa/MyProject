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
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ec.Rakuten;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EcRakuten extends InitJob
{

	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(EcRakuten.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public EcRakuten()
	{

	}

	public EcRakuten(String eId,String shopId,String organizationNO, String billNo)
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
			logger.info("\r\n*********乐天订单抓取EcRakuten正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-乐天订单抓取EcRakuten正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********乐天订单抓取EcRakuten定时调用Start:************\r\n");

		boolean more=true;//可能有很多订单

		//乐天调用
		Rakuten rt=new Rakuten();	

		try
		{		
			//取第一笔
			String sql="select * from OC_ECOMMERCE A where A.ECPLATFORMNO='letian' and A.status='100' ";	
			List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
			if (sqllist != null && sqllist.isEmpty() == false)
			{
				for (Map<String, Object> map : sqllist) 
				{
					String apiUrl=map.get("API_URL").toString();
					String licensekey=map.get("LTLICENSEKEY").toString();
					String secretkey= map.get("LTSECRETKEY").toString();
					String shopUrl=map.get("LTSHOPURL").toString();
					String lastUpdatetime=map.get("LT_LASTUPDATETIME").toString();
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
					String sqlTvMappingPay="select * from OC_mappingpayment t where t.load_doctype='letian' and t.status='100' and t.eid='"+eId+"' ";


					//上次拉取订单的时间戳
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

					//今天的0点0分0秒
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					Date zero = calendar.getTime();		

					SimpleDateFormat sdfRT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					String sDzero=sdfRT.format(zero);			
					//System.out.println(sDzero);	
					String createdAfter=sDzero;

					if(lastUpdatetime.equals("")==false)
					{					
						//上次拉取订单的时间戳
						SimpleDateFormat sdfpp = new SimpleDateFormat("yyyyMMddHHmmss");
						Date tempdatepp = sdfpp.parse(lastUpdatetime);
						sdfpp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

						//固定减8小时处理,虾皮平台问题订单时间是8小时之前的					
						Calendar rt_Cal = Calendar.getInstance();  
						rt_Cal.setTime(tempdatepp);
						rt_Cal.add(Calendar.HOUR, -8);//					
						createdAfter=sdfpp.format(rt_Cal.getTime());

						//createdAfter=sdfpp.format(tempdatepp);		
						//System.out.println(createdAfter);									
					}				

					calendar = Calendar.getInstance();
					SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyyMMdd");
					String sysDate=dfDatetime.format(calendar.getTime());
					dfDatetime = new SimpleDateFormat("HHmmss");
					String sysTime=dfDatetime.format(calendar.getTime());

					//訂單日誌時間
					dfDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
					String orderStatusLogTimes=dfDatetime.format(calendar.getTime());

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

					//结束日期
					sdfRT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

					//固定减8小时处理,虾皮平台问题订单时间是8小时之前的					
					Calendar rt_Cal_A = Calendar.getInstance();				
					rt_Cal_A.add(Calendar.HOUR, -8);//				
					String sNowRT=sdfRT.format(rt_Cal_A.getTime());

					//String sNowRT=sdfRT.format(calendar.getTime());	

					//System.out.println(sNowRT);	
					String createdBefore=sNowRT;

					int pageIndex=0;
					int position=0;

					while (more)
					{
						String resbody=rt.Order_POST(apiUrl, licensekey, secretkey, shopUrl, createdAfter, createdBefore, pageIndex);

						JSONObject jsonres = new JSONObject(resbody);

						if (jsonres.has("errors"))
						{
							more=false;

							JSONObject jsonErr = jsonres.getJSONObject("errors");						
							//常见错误
							JSONArray common = jsonErr.getJSONArray("common");						
							for (int ei = 0; ei < common.length(); ei++) 
							{
								int errorCode=common.getJSONObject(ei).getInt("errorCode");//错误代码
								String shortMessage=common.getJSONObject(ei).getString("shortMessage");//简短描述
								String longMessage=common.getJSONObject(ei).getString("longMessage");//详细错误
								//System.out.println(errorCode);
								//System.out.println(shortMessage);
								//System.out.println(longMessage);

								logger.error("\r\n******乐天订单抓取EcRakuten报错信息errorCode=" + errorCode+",shortMessage=" + shortMessage+",longMessage"+longMessage + "******\r\n");
							}						
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
							
							
							//處理已取消的訂單,這裡不要影響正常的接單功能
							try 
							{
								String resbodyCancel=rt.Order_POST_Cancel(apiUrl, licensekey, secretkey, shopUrl, createdAfter, createdBefore, 0);

								JSONObject jsonresCancel = new JSONObject(resbodyCancel);
								if (jsonresCancel.has("errors"))
								{
									JSONObject jsonErrCancel = jsonresCancel.getJSONObject("errors");						
									//常见错误
									JSONArray commonCancel = jsonErrCancel.getJSONArray("common");						
									for (int fi = 0; fi < commonCancel.length(); fi++) 
									{
										int errorCodeCancel=commonCancel.getJSONObject(fi).getInt("errorCode");//错误代码
										String shortMessageCancel=commonCancel.getJSONObject(fi).getString("shortMessage");//简短描述
										String longMessageCancel=commonCancel.getJSONObject(fi).getString("longMessage");//详细错误
										//System.out.println(errorCodeCancel);
										//System.out.println(shortMessageCancel);
										//System.out.println(longMessageCancel);

										logger.error("\r\n******乐天订单取消抓取EcRakuten报错信息errorCode=" + errorCodeCancel+",shortMessage=" + shortMessageCancel+",longMessage"+longMessageCancel + "******\r\n");
									}			
								}
								else 
								{
									//订单总笔数
									int totalCountCancel=jsonresCancel.getInt("totalCount");
									//System.out.println("订单取消总笔数:" + totalCountCancel);
									//订单
									JSONArray ordersCancel = jsonresCancel.getJSONArray("orders");

									if (ordersCancel.length()>0) 
									{			
										//取消订单SQL
										List<DataProcessBean> lstCancelData=new ArrayList<DataProcessBean>();	

										for (int i = 0; i < ordersCancel.length(); i++) 
										{
											//订单号,格式： [7位數 字]-[YYMMDD][10位數字]
											String orderNumberCancel=ordersCancel.getJSONObject(i).getString("orderNumber");
											//System.out.println("取消订单號:" + orderNumberCancel);

											//
											UptBean ubec=new UptBean("OC_ORDER");
											ubec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
											ubec.addCondition("SHOPID", new DataValue(inShop, Types.VARCHAR));
											ubec.addCondition("ORDERNO", new DataValue(orderNumberCancel, Types.VARCHAR));
											ubec.addCondition("LOAD_DOCTYPE", new DataValue("letian", Types.VARCHAR));

											ubec.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
											ubec.addUpdateValue("REFUNDSTATUS", new DataValue("6", Types.VARCHAR));//1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功 7.用户申请部分退款 8.拒绝部分退款 9 部分退款失败 10.部分退款成功

											lstCancelData.add(new DataProcessBean(ubec));	

											//接單日誌
											DataValue[] insValueOrderStatus_LOG = new DataValue[] 
													{ 
															new DataValue(eId, Types.VARCHAR),
															new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
															new DataValue(inShop, Types.VARCHAR), // 映射后的门店
															new DataValue(orderNumberCancel, Types.VARCHAR), //
															new DataValue("letian", Types.VARCHAR), //電商平台
															new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
															new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
															new DataValue("12", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
															new DataValue("訂單已取消", Types.VARCHAR), // 状态名称
															new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
															new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
															new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
															new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
															new DataValue("admin", Types.VARCHAR), //操作員編碼
															new DataValue("管理員", Types.VARCHAR), //操作員名稱
															new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
															new DataValue("訂單狀態-->訂單已取消", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
															new DataValue("100", Types.VARCHAR) 
													};
											InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
											ibOrderStatusLog.addValues(insValueOrderStatus_LOG);

											lstCancelData.add(new DataProcessBean(ibOrderStatusLog));	

											//批处理执行**********************************************
											//****************************************************
											StaticInfo.dao.useTransactionProcessData(lstCancelData);
										}				

									}		

								}

							} 
							catch (Exception ex) 
							{
								logger.error("\r\n******乐天订单取消抓取EcRakuten报错信息"+ex.getMessage() + "******\r\n");
							}

							//订单总笔数
							int totalCount=jsonres.getInt("totalCount");
							//System.out.println("订单总笔数:" + totalCount);

							if ((position+50)<totalCount) 
							{
								more=true;

								pageIndex+=1;
							}
							else 
							{
								more =false;
							}

							//
							position+=50;


							//订单
							JSONArray orders = jsonres.getJSONArray("orders");

							if (orders.length()>0) 
							{
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
										"ORIGINAL_DELIVERYNO",//之前借用生產備註PROMEMO，我寫成訂單GUID：98f269de-1664-4bd6-bd40-5eae3b728ae7
										"SHIP_ORDERPACKAGEID",
										"ORDERSHOP",
										"ORDERSHOPNAME",
										
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

								};

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

								};




								String inMultiOrderno="";
								for (int i = 0; i < orders.length(); i++) 
								{
									//订单号,格式： [7位數 字]-[YYMMDD][10位數字]
									String orderNumber=orders.getJSONObject(i).getString("orderNumber");
									//System.out.println("订单號:" + orderNumber);

									inMultiOrderno=inMultiOrderno + "'" + orderNumber+"',";
								}
								if(inMultiOrderno.equals("")==false)
								{
									inMultiOrderno=inMultiOrderno.substring(0,inMultiOrderno.length()-1);
								}		

								//列表SQL
								List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		

								//企业编码注意===需要替换
								String sqlExistOrdernoString="SELECT * FROM OC_ORDER T WHERE T.EID='"+eId+"' AND T.ORDERNO IN("+inMultiOrderno+")";
								List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sqlExistOrdernoString, null);

								//查询付款方式映射
								List<Map<String, Object>> sqllistPaymapping=this.doQueryData(sqlTvMappingPay, null);

								//电商库存处理
								JSONObject jsonECStock=new JSONObject();
								JSONArray ecHeaderArr=new JSONArray();									
								JSONArray ecDetailArr = new JSONArray(); 

								for (int i = 0; i < orders.length(); i++) 
								{
									//订单号,格式： [7位數 字]-[YYMMDD][10位數字]
									String orderNumber=orders.getJSONObject(i).getString("orderNumber");
									//System.out.println("订单號:" + orderNumber);

									//检查订单是否存在
									Map<String, Object> map_condition = new HashMap<String, Object>();
									map_condition.put("ORDERNO", orderNumber);		
									List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getQData,map_condition,false);	
									if (getQHeader1!=null && getQHeader1.size()>0) 
									{
										continue;
									}

									//guid 用于促销查询
									String orderId=orders.getJSONObject(i).getString("orderId");
									String rorderStatus=orders.getJSONObject(i).getString("orderStatus");//订单状态

									int cancelReason=0;
									if (jsonres.has("cancelReason")) 
									{
										cancelReason=orders.getJSONObject(i).getInt("cancelReason");//取消原因代码					
									}

									//订单总金额(含配送费)
									int orderTotal=orders.getJSONObject(i).getInt("orderTotal");
									//商品总金额sum(unitPrice*quantity)
									int itemTotalBeforeDiscount=orders.getJSONObject(i).getInt("itemTotalBeforeDiscount");
									//商品总折扣额 sum(discount)
									int discountTotal=orders.getJSONObject(i).getInt("discountTotal");
									//订单最后修改日期
									String lastModifiedDate=orders.getJSONObject(i).getString("lastModifiedDate");
									//订单日期,2019-06-25T09:09:00.000Z	
									String orderDate=orders.getJSONObject(i).getString("orderDate");

									SimpleDateFormat sdfOrderdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
									Date tempdatepp = sdfOrderdate.parse(orderDate);
									sdfOrderdate = new SimpleDateFormat("yyyyMMddHHmmss");
									String sCreate_time=sdfOrderdate.format(tempdatepp);		
									//System.out.println(sCreate_time);	


									//订单完成日期	
									String completionDate="";
									if (jsonres.has("completionDate")) 
									{
										completionDate=orders.getJSONObject(i).getString("completionDate");
									}
									//顾客姓名			
									String buyerName=orders.getJSONObject(i).getString("buyerName");
									//顾客电话
									String buyerPhoneNumber=orders.getJSONObject(i).getString("buyerPhoneNumber");
									//店家备注
									String merchantMemo="";
									if (orders.getJSONObject(i).has("merchantMemo")) 
									{
										merchantMemo=orders.getJSONObject(i).getString("merchantMemo");
									}

									//顧客在結帳時輸入的訊息或備註 
									String shopperComment="";
									if (orders.getJSONObject(i).has("shopperComment")) 
									{
										String sOutComment=orders.getJSONObject(i).getString("shopperComment");

										JSONObject objShopperComment=new JSONObject(sOutComment);
										if (objShopperComment.has("shopper_comment")) 
										{
											shopperComment=objShopperComment.getString("shopper_comment");
										}
										else 
										{
											shopperComment="";
										}
									}

									//會員等級，R1
									String buyerMemberRank="";
									if (orders.getJSONObject(i).has("buyerMemberRank")) 
									{
										buyerMemberRank=orders.getJSONObject(i).getString("buyerMemberRank");
									}

									//促销活动
									if (orders.getJSONObject(i).has("campaigns")) 
									{
										JSONArray campaigns = orders.getJSONObject(i).getJSONArray("campaigns");						
										for (int a = 0; a < campaigns.length(); a++) 
										{
											String campaignName=campaigns.getJSONObject(a).getString("campaignName");//活动名称
											String campaignReferenceId=campaigns.getJSONObject(a).getString("campaignReferenceId");//活动编码
											String campaignType=campaigns.getJSONObject(a).getString("campaignType");//活动类型
											//活动url
											String campaignURL="";
											if(campaigns.getJSONObject(a).has("campaignURL"))
											{
												campaignURL=campaigns.getJSONObject(a).getString("campaignURL");	
											}							
											//
											JSONObject campaignInfo = campaigns.getJSONObject(a).getJSONObject("campaignInfo");
											String discountType=campaignInfo.getString("discountType");//折扣类型
											int discountValue=campaignInfo.getInt("discountValue");//折扣值

											String couponCode="";
											if (jsonres.has("couponCode")) 
											{
												couponCode=campaignInfo.getString("couponCode");//折扣券代码
											}

										}
									}


									double tot_qty=0;
									double tot_disc=0;
									double tot_OldAMT=0;
									double tot_AMT=0;

									//找不到商品的异常
									String sExceptionStatus="N";
									String sExceptionMemo="";

									//电商库存处理
									JSONObject ecHeader = new JSONObject();
									ecHeader.put("EID", eId);								
									ecHeader.put("ecStockNO", orderNumber);//单号
									ecHeader.put("docType", "1");//库存异动方向：0入  1出
									ecHeader.put("opNo", "admin");
									ecHeader.put("loadType", "2");//来源类型： 0手动上下架 1调拨收货 2订单 3调拨出库 
									ecHeader.put("loadDocno", "");//来源单号
									ecHeader.put("loadDocShop", "");
									
									
									//商品明细
									if (orders.getJSONObject(i).has("orderItems")) 
									{
										JSONArray orderItems = orders.getJSONObject(i).getJSONArray("orderItems");						
										for (int a = 0; a < orderItems.length(); a++) 
										{
											//名称zh_TW/en_us
											JSONObject name = orderItems.getJSONObject(a).getJSONObject("name");
											//商品名称
											String item_name="";
											if (name.has("zh_TW")) 
											{
												item_name=name.getString("zh_TW");
											}
											//baseSKU編號 ，电商品号
											String baseSku=orderItems.getJSONObject(a).getString("baseSku");
											//sku的規格 ,品号
											String sku=orderItems.getJSONObject(a).getString("sku");

											//
											String pluno="";											
											if (sku.equals("")) 
											{
												pluno=baseSku;
											}
											else 
											{
												pluno=sku;
											}

											String specName="";
											//规格属性variantAttributes
											if (orderItems.getJSONObject(a).has("variantAttributes")) 
											{
												JSONObject variantAttributes = orderItems.getJSONObject(a).getJSONObject("variantAttributes");
												//这个是可以定义的attributeName(1-5)
												//顏色，尺寸 ，款式，口味，支撐架
												String attrColor="";//顏色
												String attrSize="";//尺寸
												String attrStyle="";//款式
												String attrFlavor="";//口味
												String attrCarriage="";//支撐架								
												if (variantAttributes.has("zh_TW")) 
												{
													JSONObject attr=variantAttributes.getJSONObject("zh_TW");
													if(attr.has("顏色"))
													{
														attrColor=attr.getString("顏色");
														specName+="顏色:"+attrColor;
													}
													if(attr.has("尺寸"))
													{
														attrSize=attr.getString("尺寸");
														specName+="尺寸:"+attrColor;
													}
													if(attr.has("款式"))
													{
														attrStyle=attr.getString("款式");
														specName+="款式:"+attrColor;
													}
													if(attr.has("口味"))
													{
														attrFlavor=attr.getString("口味");
														specName+="口味:"+attrColor;
													}
													if(attr.has("支撐架"))
													{
														attrCarriage=attr.getString("支撐架");
														specName+="支撐架:"+attrColor;
													}
												}
											}

											//訂單商品識別碼 guid
											String orderItemId=orderItems.getJSONObject(a).getString("orderItemId");
											//商品单价
											String unitPrice=orderItems.getJSONObject(a).getString("unitPrice");
											//商品数量
											String quantity=orderItems.getJSONObject(a).getString("quantity");
											//商品折扣
											String discount=orderItems.getJSONObject(a).getString("discount");
											//商品成交金额,(unitPrice*quantity)-discount
											String itemTotal=orderItems.getJSONObject(a).getString("itemTotal");

											double disc=Double.valueOf(discount);

											tot_qty+=Double.valueOf(quantity);
											tot_disc+=disc;
											tot_OldAMT+=(Double.valueOf(itemTotal) + disc);
											tot_AMT+=Double.valueOf(itemTotal);

											//
											String sExeptionSql="select a.pluNo ,  a.sunit, b.plubarcode  from DCP_GOODS a "
													+ " left join DCP_BARCODE b on a.EID = b.EID and a.pluNo = b.pluNo and b.STATUS = '100' "
													+ " where a.EID = '"+eId+"'  "
													+ " and a.pluNo = '"+pluno+"' "
													+ " and a.STATUS = '100' ";

											List<Map<String, Object>> sqlExceptionList=this.doQueryData(sExeptionSql, null);
											if (sqlExceptionList == null || sqlExceptionList.isEmpty())
											{			
												//只需一次异常赋值
												if (sExceptionStatus.equals("Y")==false) 
												{
													sExceptionStatus="Y";
												}

												sExceptionMemo+=pluno+"__("+ item_name+"),\r\n";												
											}
											sqlExceptionList=null;


											DataValue[] insValueOrderDetail =new DataValue[]
													{
															new DataValue(eId, Types.VARCHAR), 
															new DataValue(inShop, Types.VARCHAR), 
															new DataValue(orderNumber, Types.VARCHAR), 
															new DataValue("letian", Types.VARCHAR), 
															new DataValue(a+1, Types.INTEGER), //item
															new DataValue(pluno, Types.VARCHAR), //pluno
															new DataValue(pluno, Types.VARCHAR), //plubarcode
															new DataValue(item_name, Types.VARCHAR), //pluname
															new DataValue(specName, Types.VARCHAR), //specname
															new DataValue("", Types.VARCHAR), //unit
															new DataValue(unitPrice, Types.DOUBLE),//price 
															new DataValue(quantity, Types.DOUBLE), //qty
															new DataValue(disc, Types.DOUBLE),//折扣 
															new DataValue(itemTotal, Types.DOUBLE), //amt
															new DataValue("100", Types.VARCHAR), 
															new DataValue("", Types.VARCHAR), //order_sn
															new DataValue("", Types.VARCHAR), //ecpluno
													};

											InsBean ibOrderDetail = new InsBean("OC_ORDER_DETAIL", columnsOrderDetail);
											ibOrderDetail.addValues(insValueOrderDetail);
											lstData.add(new DataProcessBean(ibOrderDetail));	

											//电商库存处理
											JSONObject ecDetail = new JSONObject();
											ecDetail.put("loadDocItem", "1");//来源项次
											ecDetail.put("ecPlatformNO", "letian");//电商平台
											ecHeader.put("shopId", inShop);
											ecDetail.put("pluNO", pluno);
											ecDetail.put("pluBarcode", pluno);
											ecDetail.put("unit", " ");
											ecDetail.put("qty", quantity);
											ecDetailArr.put(ecDetail);
											
										}
									}


									String deliverytype="";//配送商类型
									String getshop="";
									String getshopName="";
									int pickupWay=2;//1：超取 2：宅配
									String expressBiltype="A";//A：一般託運單 B：代收託運單        黑猫宅急便用

									//1.外卖平台配送 2.门店配送(台湾显示为宅配) 3.顾客自提 5 总部配送 6.超商
									String shiptype="";
									//包裹编号
									String orderPackageId="";
									//收件人姓名	
									String name="";
									//收件人电话
									String phoneNumber="";
									//收件人地区/国家代码
									String countryCode="";
									//收件人地址1,六工門市 基隆市七七堵區工建路1之22號1之23號1樓
									String address1="";
									//收件人邮号
									String postalCode="";
									//收件人省份代码
									String stateCode="";
									//收件人省份名称
									String stateName="";
									//收件人城市代码
									String cityCode="";
									//收件人城市名称	
									String cityName="";
									//收件人地址2
									String address2="";

									//配送方式，台灣本島各縣市
									String shippingMethod="";
									//配送状态
									String shippingStatus="";

									//配送日期
									String shippingDate="";
									String shippingtime="";

									//运单号
									String trackingNumber="";
									//配送费			
									String shippingFee="";

									//******物流商*****
									//預設配送方式：黑貓宅急便、7-11門市 取貨、全家門市取貨
									//非預設配送方式：新竹物流,中華郵政, 嘉里大榮,統一速達,宅配通 
									String carrierName="";

									//配送
									if (orders.getJSONObject(i).has("shipping")) 
									{
										JSONObject shipping=orders.getJSONObject(i).getJSONObject("shipping");

										orderPackageId=shipping.getString("orderPackageId");

										//商品配送地址
										JSONObject deliveryAddress=shipping.getJSONObject("deliveryAddress");
										name=deliveryAddress.getString("name");																
										phoneNumber=deliveryAddress.getString("phoneNumber");									
										countryCode=deliveryAddress.getString("countryCode");									
										address1=deliveryAddress.getString("address1");									
										if (deliveryAddress.has("postalCode")) 
										{
											postalCode=deliveryAddress.getString("postalCode");
										}									
										if (deliveryAddress.has("stateCode")) 
										{
											stateCode=deliveryAddress.getString("stateCode");
										}

										if (deliveryAddress.has("stateName")) 
										{
											stateName=deliveryAddress.getString("stateName");
										}						

										if (deliveryAddress.has("cityCode")) 
										{
											cityCode=deliveryAddress.getString("cityCode");
										}

										if (deliveryAddress.has("cityName")) 
										{
											cityName=deliveryAddress.getString("cityName");
										}

										if (deliveryAddress.has("address2")) 
										{
											address2=deliveryAddress.getString("address2");
										}
										
										//这个不一定有值
										if (shipping.has("storeId")) 
										{
											getshop=shipping.getString("storeId").trim();
										}

										//发票配送地址
										if (shipping.has("invoiceAddress")) 
										{
											JSONObject invoiceAddress=shipping.getJSONObject("invoiceAddress");

											String ivname=invoiceAddress.getString("name");//收件人姓名						
											String ivphoneNumber=invoiceAddress.getString("phoneNumber");//收件人电话						
											String ivcountryCode=invoiceAddress.getString("countryCode");//收件人地区/国家代码
											String ivaddress1=invoiceAddress.getString("address1");//收件人地址1
											String ivpostalCode="";
											if (invoiceAddress.has("postalCode")) 
											{
												postalCode=invoiceAddress.getString("postalCode");//收件人邮号
											}						

											String ivstateCode="";
											if (invoiceAddress.has("stateCode")) 
											{
												stateCode=invoiceAddress.getString("stateCode");//收件人省份代码
											}

											String ivstateName="";
											if (invoiceAddress.has("stateName")) 
											{
												stateName=invoiceAddress.getString("stateName");//收件人省份名称
											}						

											String ivcityCode="";
											if (invoiceAddress.has("cityCode")) 
											{
												cityCode=invoiceAddress.getString("cityCode");//收件人城市代码
											}

											String ivcityName="";
											if (invoiceAddress.has("cityName")) 
											{
												cityName=invoiceAddress.getString("cityName");//收件人城市名称	
											}

											String ivaddress2="";
											if (invoiceAddress.has("address2")) 
											{
												address2=invoiceAddress.getString("address2");//收件人地址2
											}

										}

										//配送方式，台灣本島各縣市
										shippingMethod=shipping.getString("shippingMethod");
										//配送状态
										shippingStatus=shipping.getString("shippingStatus");

										if (shipping.has("carrierName")) 
										{
											carrierName=shipping.getString("carrierName");										
										}
										//System.out.println("物流商:" + carrierName);


										if (shipping.has("trackingNumber")) 
										{
											trackingNumber=shipping.getString("trackingNumber");
										}
										//System.out.println("运单号:" + trackingNumber);


										if (shipping.has("shippingDate")) 
										{
											shippingDate=shipping.getString("shippingDate");

											SimpleDateFormat sdfshippingDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
											Date tempdateShip = sdfshippingDate.parse(shippingDate);
											sdfshippingDate = new SimpleDateFormat("yyyyMMdd");
											shippingDate=sdfshippingDate.format(tempdateShip);	

											sdfshippingDate = new SimpleDateFormat("HHmmss");
											shippingtime=sdfshippingDate.format(tempdateShip);	

											//System.out.println(shippingDate);	
											//System.out.println(shippingtime);	
										}

										if (shipping.has("shippingFee")) 
										{
											shippingFee=shipping.getString("shippingFee");
										}						

									}								

									//乐天目前只有7-11和全家
									if (shippingMethod.contains("黑貓")) 
									{
										deliverytype="9";
										pickupWay=2;											
									}
									else if (shippingMethod.contains("7-11")) 
									{
										deliverytype="7";
										pickupWay=1;

										String[] splitFullStrings=address1.split(" ");
										getshopName=splitFullStrings[0];											
										//System.out.println(getshop+" , "+getshopName);
										
										//乐天商城没有门店编号
										if (getshop.equals("")) 
										{
											getshop=HttpSend.Send711Shopno(getshopName);
										}
									}
									else if (shippingMethod.contains("全家")) 
									{
										deliverytype="8";
										pickupWay=1;

										String[] splitFullStrings=address1.split(" ");
										getshopName=splitFullStrings[0];											
										//System.out.println(getshop+" , "+getshopName);
										
										//乐天商城没有门店编号
										if (getshop.equals("")) 
										{
											getshop=HttpSend.SendCVSShopno(stateName, cityName, getshopName);
										}
									}		
									else if (shippingMethod.contains("萊而富")) 
									{
										deliverytype="10";
										pickupWay=1;

										String[] splitFullStrings=address1.split(" ");
										getshopName=splitFullStrings[0];											
										//System.out.println(getshop+" , "+getshopName);
										
										//乐天商城没有门店编号
										if (getshop.equals("")) 
										{
											getshop=HttpSend.SendCVSShopno(stateName, cityName, getshopName);
										}
									}		
									else if (shippingMethod.contains("OK")) 
									{
										deliverytype="11";
										pickupWay=1;

										String[] splitFullStrings=address1.split(" ");
										getshopName=splitFullStrings[0];											
										//System.out.println(getshop+" , "+getshopName);
										
										//乐天商城没有门店编号
										if (getshop.equals("")) 
										{
											getshop=HttpSend.SendCVSShopno(stateName, cityName, getshopName);
										}
									}		
									else if (shippingMethod.contains("新竹")) 
									{
										deliverytype="15";
										pickupWay=2;
									}		
									else if (shippingMethod.contains("中華郵政")) 
									{
										deliverytype="13";
										pickupWay=2;
									}	
									else 
									{
										//
										deliverytype="14";//暂不支持
										pickupWay=2;
									}
									
									

									if (pickupWay==2) 
									{
										shiptype="2";
									}
									else 
									{
										shiptype="6";
									}

									//对应的ERP支付方式
									String erp_paycode="";
									String erp_payName="";
									String erp_paycodeERP="";

									//付款方式编码GUID
									String orderPaymentId="";

									//付款方式名称，信用卡付款
									String paymentMethod="";
									//付款状态，Paid
									String paymentStatus="";
									//使用点数
									String pointAmount="";
									//付款金额
									String payAmount="";
									//付款日期
									String paymentDate="";
									//付款手續費
									String paymentFee="";	

									//付款信息
									if (orders.getJSONObject(i).has("payment")) 
									{
										JSONObject payment=orders.getJSONObject(i).getJSONObject("payment");

										orderPaymentId=payment.getString("orderPaymentId");									
										paymentMethod=payment.getString("paymentMethod");

										//过滤付款方式映射
										map_condition = new HashMap<String, Object>();
										map_condition.put("ORDER_PAYCODE",paymentMethod );//这里采用包含的关系,因为那些信用卡和银行太多了		
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

										//付款状态，Paid
										paymentStatus=payment.getString("paymentStatus");
										//使用点数
										pointAmount=payment.getString("pointAmount");						

										if (payment.has("payAmount")) 
										{
											payAmount=payment.getString("payAmount");				
										}	

										if (payment.has("paymentDate")) 
										{
											paymentDate=payment.getString("paymentDate");
										}						

										if (payment.has("paymentFee")) 
										{
											paymentFee=payment.getString("paymentFee");			
										}									

									}

									//发票状态，Not issued未開發票，
									String eInvoicestatus="";
									//发票号码
									String eInvoicenumber="";
									//发票类型 DONATE捐贈， DUPLICATE二聯， TRIPLICATE，三聯
									String eInvoicetype="";
									//发票载具ID
									String eInvoiceCarrierId="";
									//发票载具类型 RAKUTEN_MEMBER
									String eInvoiceCarrierType="";
									//发票开了日期
									String eInvoiceIssuedDate="";
									//发票最后可作废日期
									String eInvoiceDueDate="";
									//买方码
									String eInvoiceBuyerBAN="";
									//买方Email
									String eInvoiceBuyerEmail="";
									//社会福利码
									String eInvoiceSocialWelfareBAN="";
									//社会福利名称
									String eInvoiceSocialWelfareName="";
									//强制性津贴
									boolean eInvoiceForcedAllowance=false;

									//電子发票信息
									if (orders.getJSONObject(i).has("eInvoice")) 
									{
										JSONObject eInvoice=orders.getJSONObject(i).getJSONObject("eInvoice");
										//发票状态，Not issued未開發票，
										eInvoicestatus=eInvoice.getString("status");

										if(eInvoice.has("number"))
										{
											eInvoicenumber=eInvoice.getString("number");
										}

										if(eInvoice.has("type"))
										{
											eInvoicetype=eInvoice.getString("type");
										}

										if(eInvoice.has("carrierId"))
										{
											eInvoiceCarrierId=eInvoice.getString("carrierId");
										}

										if(eInvoice.has("carrierType"))
										{
											eInvoiceCarrierType=eInvoice.getString("carrierType");
										}

										if(eInvoice.has("issuedDate"))
										{
											eInvoiceIssuedDate=eInvoice.getString("issuedDate");
										}

										if(eInvoice.has("dueDate"))
										{
											eInvoiceDueDate=eInvoice.getString("dueDate");
										}

										if(eInvoice.has("buyerBAN"))
										{
											eInvoiceBuyerBAN=eInvoice.getString("buyerBAN");
										}

										if(eInvoice.has("buyerEmail"))
										{
											eInvoiceBuyerEmail=eInvoice.getString("buyerEmail");
										}

										if(eInvoice.has("socialWelfareBAN"))
										{
											eInvoiceSocialWelfareBAN=eInvoice.getString("socialWelfareBAN");
										}

										if(eInvoice.has("socialWelfareName"))
										{
											eInvoiceSocialWelfareName=eInvoice.getString("socialWelfareName");
										}

										if(eInvoice.has("forcedAllowance"))
										{
											eInvoiceForcedAllowance=eInvoice.getBoolean("forcedAllowance");
										}
									}


									String payStatuString="3";
									double payAmt=0;								
									if(paymentMethod.contains("貨到付款"))
									{
										payStatuString="1";
										payAmt=0;
									}
									else 
									{
										payStatuString="3";
										payAmt=tot_AMT+Double.valueOf(shippingFee);
									}

									String address="";
									if (pickupWay==2)//2：宅配
									{
										address=stateName+cityName+address1;
									}
									else 
									{
										address=address1;
									}

									//插入订单表
									if (getQHeader1==null || getQHeader1.size()==0) 
									{
										DataValue[] insValueOrder =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 								
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(inShopName, Types.VARCHAR), 
														new DataValue(orderNumber, Types.VARCHAR), 
														new DataValue("letian", Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR),
														new DataValue(shiptype, Types.VARCHAR),////1.外卖平台配送 2.门店配送(台湾显示为宅配) 3.顾客自提 5 总部配送 6.超商
														new DataValue(name, Types.VARCHAR), 
														new DataValue(phoneNumber, Types.VARCHAR), 
														new DataValue(address, Types.VARCHAR), //配送地址
														new DataValue(shippingDate, Types.VARCHAR), //配送日期
														new DataValue(shippingtime, Types.VARCHAR), 	//配送时间						
														new DataValue("1", Types.VARCHAR), //配送时段		
														new DataValue(shopperComment, Types.VARCHAR), 
														new DataValue(sCreate_time, Types.VARCHAR), 
														new DataValue("", Types.VARCHAR),//交易流水号 		
														new DataValue(orderTotal, Types.DOUBLE), //原价金额
														new DataValue(tot_AMT+Double.valueOf(shippingFee), Types.DOUBLE),//用户实际支付金额 								
														new DataValue(tot_qty, Types.DOUBLE), //总数量
														new DataValue(tot_AMT+Double.valueOf(shippingFee), Types.DOUBLE),//商家实收金额
														new DataValue(0, Types.DOUBLE),//平台服务费
														new DataValue(tot_disc, Types.DOUBLE), //总折扣
														new DataValue(tot_disc, Types.DOUBLE), //商户优惠总额
														new DataValue(0, Types.DOUBLE), //平台优惠总额
														new DataValue(Double.valueOf(shippingFee), Types.DOUBLE), //预计运费
														new DataValue(Double.valueOf(shippingFee), Types.DOUBLE), //总配送费
														new DataValue(0, Types.DOUBLE), //配送费减免
														new DataValue("2", Types.VARCHAR), //状态
														new DataValue(sysDate, Types.VARCHAR), 								
														new DataValue(sysTime, Types.VARCHAR), 
														new DataValue("100", Types.VARCHAR), 
														new DataValue(name, Types.VARCHAR), //收货人
														new DataValue(phoneNumber, Types.VARCHAR),//收货人电话 
														new DataValue("", Types.VARCHAR),//收货人email 								
														new DataValue("", Types.VARCHAR),//会员卡
														new DataValue("", Types.VARCHAR),//会员姓名
														new DataValue(stateName, Types.VARCHAR), 
														new DataValue(cityName, Types.VARCHAR), 
														new DataValue("", Types.VARCHAR), 								
														new DataValue("", Types.VARCHAR), 
														new DataValue(2, Types.INTEGER), //币别 1:人民币 2：台币
														new DataValue(pickupWay, Types.INTEGER), //取货类型
														new DataValue(payStatuString, Types.VARCHAR),//货到付款
														new DataValue(payAmt, Types.DOUBLE),
														new DataValue(deliverytype, Types.VARCHAR),//物流类型
														new DataValue(expressBiltype, Types.VARCHAR),//物流单类型
														new DataValue("", Types.VARCHAR),//虾皮用
														new DataValue("", Types.VARCHAR),//虾皮用
														new DataValue("", Types.VARCHAR),//虾皮用
														new DataValue("", Types.VARCHAR),//虾皮用
														new DataValue("", Types.VARCHAR),//虾皮用
														new DataValue(inEccustomerno, Types.VARCHAR),//归属客户
														new DataValue(inInvoice, Types.VARCHAR),//是否开发票
														new DataValue(sExceptionStatus, Types.VARCHAR),//商品异常标记
														new DataValue(sExceptionMemo, Types.VARCHAR),//异常商品记录
														new DataValue(orderId, Types.VARCHAR),
														new DataValue(orderPackageId, Types.VARCHAR),
														new DataValue(getshop, Types.VARCHAR),
														new DataValue(getshopName, Types.VARCHAR)
												};

										InsBean ibOrder = new InsBean("OC_ORDER", columnsOrder);
										ibOrder.addValues(insValueOrder);
										lstData.add(new DataProcessBean(ibOrder));									


										//订单折扣
										DataValue[] insValueOrderAgio =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(orderNumber, Types.VARCHAR), 
														new DataValue(inEccustomerno, Types.VARCHAR), //归属客户
														new DataValue(1, Types.INTEGER), //ITME
														new DataValue("樂天折扣", Types.VARCHAR), //活动名称
														new DataValue(tot_disc, Types.DOUBLE), //总折扣额
														new DataValue(tot_disc, Types.DOUBLE), //商家承担额
														new DataValue(0, Types.DOUBLE), //平台承担额
														new DataValue(sysDate, Types.VARCHAR), //系统日期
														new DataValue(sysTime, Types.VARCHAR), //系统时间
														new DataValue("100", Types.VARCHAR), 
														new DataValue("letian", Types.VARCHAR), //平台代码
												};

										InsBean ibOrderAgio = new InsBean("OC_ORDER_AGIO", columnsOrderAgio);
										ibOrderAgio.addValues(insValueOrderAgio);
										lstData.add(new DataProcessBean(ibOrderAgio));											

										//订单付款
										DataValue[] insValueOrderPay =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(orderNumber, Types.VARCHAR), 
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
														new DataValue(tot_AMT+Double.valueOf(shippingFee) , Types.DOUBLE), //pay
														new DataValue(0, Types.DOUBLE), //extra
														new DataValue(0, Types.DOUBLE), //changed
														new DataValue(sysDate, Types.VARCHAR), //bdate
														new DataValue("N", Types.VARCHAR), //isorderpay
														new DataValue("letian", Types.VARCHAR), //load_doctype
														new DataValue(paymentMethod, Types.VARCHAR), //order_paycode
														new DataValue("N", Types.VARCHAR), //isonlinepay
														new DataValue("0", Types.DOUBLE), //rcpay
														new DataValue("100", Types.VARCHAR), //STATUS
												};

										InsBean ibOrderPay = new InsBean("OC_ORDER_PAY", columnsOrderPay);
										ibOrderPay.addValues(insValueOrderPay);
										lstData.add(new DataProcessBean(ibOrderPay));

										//接單日誌
										DataValue[] insValueOrderStatus_LOG = new DataValue[] 
												{ 
														new DataValue(eId, Types.VARCHAR),
														new DataValue(inShop, Types.VARCHAR), // 组织编号=门店编号
														new DataValue(inShop, Types.VARCHAR), // 映射后的门店
														new DataValue(orderNumber, Types.VARCHAR), //
														new DataValue("letian", Types.VARCHAR), //電商平台
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
										InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
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
								
								//
								UptBean ubec=new UptBean("OC_ECOMMERCE");
								ubec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								ubec.addCondition("ECPLATFORMNO", new DataValue("letian", Types.VARCHAR));

								ubec.addUpdateValue("LT_LASTUPDATETIME", new DataValue(sysDate+sysTime, Types.VARCHAR));

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
				logger.error("\r\n*********乐天订单抓取EcRakuten API资料未设置:************\r\n");
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
				
				logger.error("\r\n******乐天订单抓取EcRakuten报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******乐天订单抓取EcRakuten报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			more=false;//

			rt=null;

			bRun=false;//
			logger.info("\r\n*********乐天订单抓取EcRakuten定时调用End:************\r\n");
		}
		return sReturnInfo;

	}




}
