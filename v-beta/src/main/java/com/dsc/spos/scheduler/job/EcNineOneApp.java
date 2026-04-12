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
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ec.NineOneApp;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EcNineOneApp extends InitJob
{

	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(EcShopee.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public EcNineOneApp()
	{

	}

	public EcNineOneApp(String eId,String shopId,String organizationNO, String billNo)
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
			logger.info("\r\n*********91App订单抓取EcNineOneApp正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-91App订单抓取EcNineOneApp正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********91App订单抓取EcNineOneApp定时调用Start:************\r\n");

		boolean more=true;//可能有很多订单

		Date now = new Date();
		SimpleDateFormat sdfapp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String sNow=sdfapp.format(now);			
		//System.out.println(sNow);			

		sdfapp = new SimpleDateFormat("yyyyMMddHHmmss");
		String sLastuptime=sdfapp.format(now);		

		//91app调用
		NineOneApp noapp=new NineOneApp();		

		try
		{		

			//取第一笔
			String sql="select * from OC_ECOMMERCE A where A.ECPLATFORMNO='91app' and A.status='100' ";

			List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
			if (sqllist != null && sqllist.isEmpty() == false)
			{
				for (Map<String, Object> map : sqllist) 
				{
					String apiUrl=map.get("API_URL").toString();
					String token=map.get("TOKEN").toString();
					String appkey= map.get("APPKEY").toString();
					String saltkey=map.get("SALTKEY").toString();
					int shopsn=Integer.parseInt(map.get("SHOPSN").toString());
					String lastUpdatetime=map.get("JY_LASTUPDATETIME").toString();
					String eId=map.get("EID").toString();
					String inShop=map.get("SHOPID").toString();
					String inWarehouse=map.get("WAREHOUSE").toString();
					String inEccustomerno=map.get("ECCUSTOMERNO").toString();

					String inShopName="";
					
					//上次拉取订单的时间戳
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

					//订单付款方式映射
					String sqlTvMappingPay="select * from OC_mappingpayment t where t.load_doctype='91app' and t.status='100' and t.eid='"+eId+"' ";


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

					//插入单头
					String[] columnsOrder = {
							"EID",
							"SHOPID",
							"SHOPNAME",
							"ORDERNO",
							"LOAD_DOCTYPE",
							"SHIPPINGSHOP",
							"SHIPPINGSHOPNAME",
							"SHIPTYPE",////1.外卖平台配送 2.门店配送(台湾显示为宅配) 3.顾客自提 5 总部配送 6.超商
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
							"SHIPFEE",
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
							"ECCUSTOMERNO",//电商归属客户代号
							"ORDERSHOP",//
							"ORDERSHOPNAME",//
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
							"ORDER_SN",
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
							"ECPLUNO",

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


					int position=0;

					while (more)
					{
						String resbody=noapp.GetSalesOrder(apiUrl, token, appkey, saltkey, shopsn, lastUpdatetime,sNow, position);

						JSONObject jsonres = new JSONObject(resbody);

						String sTMCode="";
						String sTSCode="";

						String rStatus=jsonres.getString("Status");

						if(rStatus.equals("Failure"))//失败
						{
							String ErrorMessage=jsonres.getString("ErrorMessage");

							//System.out.println("错误信息:"+ErrorMessage);

							logger.error("\r\n******91App订单抓取EcNineOneApp报错信息error=" + ErrorMessage + "******\r\n");
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
							
							//查询付款方式映射
							List<Map<String, Object>> sqllistPaymapping=this.doQueryData(sqlTvMappingPay, null);

							//				
							JSONObject data = jsonres.getJSONObject("Data");
							//总记录数
							int TotalCount= data.getInt("TotalCount");

							if ((position+50)<TotalCount) 
							{
								more=true;
							}
							else 
							{
								more =false;
							}

							//
							position+=50;

							String inMultiTMno="";
							String inMultiOrderno="";
							JSONArray List =data.getJSONArray("List");
							for(int t=0;t<List.length();t++)
							{
								String TMCode=List.getJSONObject(t).getString("TMCode");//主單編號
								String TSCode=List.getJSONObject(t).getString("TSCode");//訂單編號 

								//System.out.println(TMCode+":"+TSCode);

								inMultiTMno=inMultiTMno + "'" + TMCode+"',";
								inMultiOrderno=inMultiOrderno + "'" + TSCode+"',";							
							}

							//列表SQL
							List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		

							//企业编码注意===需要替换
							String sqlExistOrdernoString="SELECT * FROM OC_ORDER T WHERE T.EID='"+eId+"' AND T.ORDERNO IN("+inMultiOrderno+") AND T.ORDER_SN IN("+inMultiTMno+") ";
							List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sqlExistOrdernoString, null);

							//电商库存处理
							JSONObject jsonECStock=new JSONObject();
							JSONArray ecHeaderArr=new JSONArray();									
							JSONArray ecDetailArr = new JSONArray(); 
							
							for(int p=0;p<List.length();p++)
							{
								String TMCode=List.getJSONObject(p).getString("TMCode");//主單編號
								String TSCode=List.getJSONObject(p).getString("TSCode");//訂單編號 					

								//检查订单是否存在
								Map<String, Object> map_condition = new HashMap<String, Object>();
								map_condition.put("ORDER_SN", TMCode);		
								map_condition.put("ORDERNO", TSCode);		
								List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getQData,map_condition,false);	
								if (getQHeader1!=null && getQHeader1.size()>0) 
								{
									continue;
								}

								//订单明细
								resbody="";
								resbody=noapp.GetSalesOrderDetail(apiUrl, token, appkey, saltkey, shopsn, TMCode, TSCode);

								jsonres = new JSONObject(resbody);

								rStatus=jsonres.getString("Status");

								if(rStatus.equals("Failure"))//失败
								{
									String ErrorMessage=jsonres.getString("ErrorMessage");

									//System.out.println("错误信息:"+ErrorMessage);
								}
								else
								{
									//单据单头信息
									String OrderMemo="";
									String OrderReceiverName="";// 收件人姓名 
									String OrderReceiverMobile="";// 收件人電話 
									String OrderReceiverZipCode="";//收件人郵遞區號 
									String OrderReceiverCity="";// 收件人縣市 
									String OrderReceiverDistrict="";// 收件人鄉鎮市區 
									String OrderReceiverAddress="";// 收件人地址 
									String OrderReceiverStoreId="";// 超商取貨的門市編號 
									String OrderReceiverStoreName="";// 超商取貨的門市名稱 
									String TMShippingFee="";//主單(TM)運費 
									double Price=0;// 商品單價 
									int Qty=0;//商品數量 
									double TotalPrice=0;// 商品總金額(單價*數量) 
									double TotalDiscount=0;// 訂單總折扣金額 
									double TotalPayment=0;// 訂單實際付款金額 
									double tot_qty=0;
									long VipMemberId=0;//91APP VIP 會員序號  								
									String OuterMemberCode="";//客戶會員編號 (貴公司的會員編號) 如未使用會員模組，此欄位會回覆 null 
									int pickupWay=1;
									String shiptype="";//
									String payStatus="";
									double payAmt=0;			
									String deliverytype="";//物流类型
									String expressBiltype="A";//A：一般託運單 B：代收託運單        黑猫宅急便用

									//付款方式 -信用卡一次付款：CreditCardOnce -信用卡分期付款：CreditCardInstallment 
									//-超商取貨付款：StorePay -ATM 付款：ATM -貨到付款：CashOnDelivery - LINE Pay：LinePay
									String OrderPayType="";

									//对应的ERP支付方式
									String erp_paycode="";
									String erp_payName="";
									String erp_paycodeERP="";




									//				
									data = jsonres.getJSONObject("Data");
									//总记录数
									//int TotalCount= data.getInt("TotalCount");

				
									
									//电商库存处理
									JSONObject ecHeader = new JSONObject();
									ecHeader.put("eId", eId);
									ecHeader.put("ecStockNO", TSCode);//单号
									ecHeader.put("docType", "1");//库存异动方向：0入  1出
									ecHeader.put("opNo", "admin");
									ecHeader.put("loadType", "2");//来源类型： 0手动上下架 1调拨收货 2订单 3调拨出库 
									ecHeader.put("loadDocno", "");//来源单号
									ecHeader.put("loadDocShop", "");
									
									List =data.getJSONArray("List");
									for(int i=0;i<List.length();i++)
									{
										String dTMCode=List.getJSONObject(i).getString("TMCode");//主單編號
										String dTSCode=List.getJSONObject(i).getString("TSCode");//訂單編號 

										OrderPayType=List.getJSONObject(i).getString("OrderPayType");// 

										long OrderShippingTypeId=List.getJSONObject(i).getLong("OrderShippingTypeId");//物流商编码 

										//配送方式 -宅配(含離島宅配) :Home -超商取貨付款 : StoreCashOnDelivery -付款後超商取貨 :StorePickup -付款後門市自取 : LocationPickup 
										//-貨到付款：CashOnDelivery -海外宅配：Oversea 
										String OrderDeliverType=List.getJSONObject(i).getString("OrderDeliverType");// 									

										//通路商 -全家：Family -7-11：SevenEleven 
										String DistributorDef=""; 
										if (List.getJSONObject(i).isNull("DistributorDef")==false) 
										{
											DistributorDef=List.getJSONObject(i).getString("DistributorDef");// 
										}

										if (OrderDeliverType.equals("Home")||OrderDeliverType.equals("CashOnDelivery")||OrderDeliverType.equals("Oversea")) 
										{
											pickupWay=2;
											deliverytype="14";
										}
										else 
										{
											pickupWay=1;

											if (DistributorDef.equals("Family")) 
											{
												deliverytype="8";
											}
											else if (DistributorDef.equals("SevenEleven")) 
											{
												deliverytype="7";//7-11
											}
											else 
											{
												deliverytype="";
											}				

										}


										if (pickupWay==2) 
										{
											shiptype="2";
										}
										else 
										{
											shiptype="6";
										}


										//溫層類別 -常溫：Normal -冷藏：Refrigerator -冷凍：Freezer
										String TemperatureTypeDef=List.getJSONObject(i).getString("TemperatureTypeDef");// 
										TMShippingFee=List.getJSONObject(i).getString("TMShippingFee");//主單(TM)運費 
										String OrderDateTime=List.getJSONObject(i).getString("OrderDateTime");//訂單轉單2013-08-30T17:30:00
										//訂單狀態 - 已取消：Cancel - 已完成：Finish - 付款確認中：WaitingToCreditCheck - 已成立：WaitingToShipping 
										//- 已確認待出貨：ConfirmedToShipping - 待付款：WaitingToPay
										String OrderStatus=List.getJSONObject(i).getString("OrderStatus");// 
										String OrderStatusUpdatedDateTime=List.getJSONObject(i).getString("OrderStatusUpdatedDateTime");// 訂單狀態日 2013-08-30T17:30:00 
										String SalesOrderConfirmDateTime=List.getJSONObject(i).getString("SalesOrderConfirmDateTime");// 訂單確認日期 2013-09-01T17:30:00 
										//訂單交期別 - 一般 : 1   -預購(指定出貨日) : 2   - 訂製 : 3   - 客約 : 4   - 預購(指定工作天)：6 
										String OrderShippingType=List.getJSONObject(i).getString("OrderShippingType");// 
										String OrderExpectShippingDate=List.getJSONObject(i).getString("OrderExpectShippingDate");// 訂單預計出貨日 2013-09-04T17:30:00
										String OrderSource=List.getJSONObject(i).getString("OrderSource");//訂單來源 iOSApp 
										String MemberCode=List.getJSONObject(i).getString("MemberCode");// 會員編號 
										VipMemberId=List.getJSONObject(i).getLong("VipMemberId");//91APP VIP 會員序號  
										//客戶會員編號 (貴公司的會員編號) 如未使用會員模組，此欄位會回覆 null 
										if (List.getJSONObject(i).isNull("OuterMemberCode")==false) 
										{
											OuterMemberCode=List.getJSONObject(i).getString("OuterMemberCode");// 
										}
										OrderReceiverName=List.getJSONObject(i).getString("OrderReceiverName");// 收件人姓名 
										OrderReceiverMobile=List.getJSONObject(i).getString("OrderReceiverMobile");// 收件人電話 
										OrderReceiverZipCode=List.getJSONObject(i).getString("OrderReceiverZipCode");//收件人郵遞區號 
										OrderReceiverCity=List.getJSONObject(i).getString("OrderReceiverCity");// 收件人縣市 
										OrderReceiverDistrict=List.getJSONObject(i).getString("OrderReceiverDistrict");// 收件人鄉鎮市區 
										OrderReceiverAddress=List.getJSONObject(i).getString("OrderReceiverAddress");// 收件人地址 
										OrderReceiverStoreId=List.getJSONObject(i).getString("OrderReceiverStoreId");// 超商取貨的門市編號 
										OrderReceiverStoreName=List.getJSONObject(i).getString("OrderReceiverStoreName");// 超商取貨的門市名稱 
										String ShippingOrderCode=List.getJSONObject(i).getString("ShippingOrderCode");//貨運單配送編號  
										//消費者備註 ※請注意：商店須設定使用訂單備註，訂購流 程中才會顯示消費者填寫的欄位 

										if (List.getJSONObject(i).isNull("OrderMemo")==false) 
										{
											OrderMemo=List.getJSONObject(i).getString("OrderMemo");// 
										}
										String OrderSupplierNote=List.getJSONObject(i).getString("OrderSupplierNote");// 客戶備註 
										long SkuId=List.getJSONObject(i).getLong("SkuId");// 商品選項(SKU)編號 
										String SkuName=List.getJSONObject(i).getString("SkuName");// 商品選項名稱
										String OuterId="";
										if (List.getJSONObject(i).isNull("OuterId")==false) 
										{
											OuterId=List.getJSONObject(i).getString("OuterId");//商品料號 -若為空白則顯示 null  
										}
										//用於顯示一般商品的 TS 單是否有帶活動贈品 或商品贈品，任一條件符合有即顯示 true，
										//無則顯示 false，如為活動贈品或商品贈品的 TS 則顯示 false 
										boolean HasGift=List.getJSONObject(i).getBoolean("HasGift");// 
										Price=List.getJSONObject(i).getDouble("Price");// 商品單價 
										Qty=List.getJSONObject(i).getInt("Qty");//商品數量 
										tot_qty+=Qty;

										TotalPrice=List.getJSONObject(i).getDouble("TotalPrice");// 商品總金額(單價*數量) 
										TotalDiscount=List.getJSONObject(i).getDouble("TotalDiscount");// 訂單總折扣金額 
										TotalPayment=List.getJSONObject(i).getDouble("TotalPayment");// 訂單實際付款金額 

										//付款方式 -信用卡一次付款：CreditCardOnce -信用卡分期付款：CreditCardInstallment 
										//-超商取貨付款：StorePay -ATM 付款：ATM -貨到付款：CashOnDelivery - LINE Pay：LinePay
										if (OrderPayType.equals("StorePay") && OrderPayType.equals("CashOnDelivery")) 
										{
											payStatus="1";
											payAmt=0;
										}
										else 
										{
											payStatus="3";
											payAmt=TotalPayment;
										}

										JSONArray PromotionIdList=List.getJSONObject(i).getJSONArray("PromotionIdList");// 
										for (int a = 0; a < PromotionIdList.length(); a++) 
										{
											long ID=PromotionIdList.getLong(a);
											//System.out.println("ID:"+ID);
										}
										double PromotionDiscount=List.getJSONObject(i).getDouble("PromotionDiscount");//折扣活動折扣金額 					
										long ECouponId=List.getJSONObject(i).getLong("ECouponId");//折價券活動序號 
										double ECouponDiscount=List.getJSONObject(i).getDouble("ECouponDiscount");//折價券折扣金額 
										//贈品屬性，若為滿額 is 贈會顯示(活動贈品)， 若為買就送會顯示(商品贈品)，一般商品則顯 示”“ 
										String ProductAttribute=List.getJSONObject(i).getString("ProductAttribute");//
										String SalePageGiftGroupCode=List.getJSONObject(i).getString("SalePageGiftGroupCode");//商品贈品關連代碼      
										//溫層商品空間保留編號 -未開放(請忽略此欄位) 
										//String TemperatureReservedNo=List.getJSONObject(i).getString("TemperatureReservedNo");// 
										//取消代號: 
										//1    價格比較貴 
										//2    衝動購買 
										//3    不想等太久 
										//4    其他 
										//5    商店取消 
										//6    消費者未取貨 
										//7    門市閉店 
										//8    訂單繳費過期
										String CancelOrderSlaveCauseDef=List.getJSONObject(i).getString("CancelOrderSlaveCauseDef");// 
										String CancelOrderSlaveCauseDefDesc=List.getJSONObject(i).getString("CancelOrderSlaveCauseDefDesc");//取消原因  價格比較貴 
										String ShippingOrderSlaveStatusCasue=List.getJSONObject(i).getString("ShippingOrderSlaveStatusCasue");//驗退原因說明  門市關轉 
										//是否為加價購商品  true:是 false:否 
										//(註：此欄位目前皆先回傳為 false) 
										String IsExtra=List.getJSONObject(i).getString("IsExtra");// 
										String OrderReceiverCountryCode=List.getJSONObject(i).getString("OrderReceiverCountryCode");// 收件人電話國碼 
										String OrderReceiverCountry=List.getJSONObject(i).getString("OrderReceiverCountry");//收件國家/地區 
										double LoyaltyPointDiscount=List.getJSONObject(i).getDouble("LoyaltyPointDiscount");//點數折抵金額  
										double LoyaltyPoint=List.getJSONObject(i).getDouble("LoyaltyPoint");// 折抵點數 

										//System.out.println(dTMCode+":"+dTSCode);


										DataValue[] insValueOrderDetail =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(TSCode, Types.VARCHAR), 
														new DataValue(TMCode, Types.VARCHAR), 
														new DataValue("91app", Types.VARCHAR), 
														new DataValue(i+1, Types.INTEGER), 
														new DataValue(SkuId, Types.VARCHAR), 
														new DataValue(SkuId, Types.VARCHAR), 
														new DataValue(SkuName, Types.VARCHAR), 
														new DataValue("", Types.VARCHAR), //规格名称
														new DataValue("", Types.VARCHAR), //单位
														new DataValue(Price, Types.DOUBLE), 
														new DataValue(Qty, Types.DOUBLE), 
														new DataValue(TotalDiscount, Types.DOUBLE),//折扣 
														new DataValue(TotalCount-TotalDiscount, Types.DOUBLE), 
														new DataValue("100", Types.VARCHAR), 
														new DataValue("", Types.VARCHAR), 
														new DataValue(OuterId, Types.VARCHAR), 
												};

										InsBean ibOrderDetail = new InsBean("OC_ORDER_DETAIL", columnsOrderDetail);
										ibOrderDetail.addValues(insValueOrderDetail);
										lstData.add(new DataProcessBean(ibOrderDetail));

										
										//电商库存处理
										JSONObject ecDetail = new JSONObject();
										ecDetail.put("loadDocItem", "1");//来源项次
										ecDetail.put("ecPlatformNO", "91app");//电商平台
										ecHeader.put("shopId", inShop);
										ecDetail.put("pluNO", SkuId);
										ecDetail.put("pluBarcode", SkuId);
										ecDetail.put("unit", " ");
										ecDetail.put("qty", Integer.toString(Qty));
										ecDetailArr.put(ecDetail);
									}

									//插入订单表
									if (getQHeader1==null || getQHeader1.size()==0) 
									{
										DataValue[] insValueOrder =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 								
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(inShopName, Types.VARCHAR), 
														new DataValue(TSCode, Types.VARCHAR), 
														new DataValue("91app", Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(inShopName, Types.VARCHAR), 
														new DataValue(shiptype, Types.VARCHAR), 
														new DataValue(OrderReceiverName, Types.VARCHAR), //订购人
														new DataValue(OrderReceiverMobile, Types.VARCHAR), //订购人电话
														new DataValue(OrderReceiverAddress, Types.VARCHAR), //配送地址
														new DataValue("", Types.VARCHAR), //配送日期
														new DataValue("", Types.VARCHAR), //配送时间								
														new DataValue("1", Types.VARCHAR), //配送时段
														new DataValue(OrderMemo, Types.VARCHAR), //备注
														new DataValue(sysDate+sysTime, Types.VARCHAR), //下单时间
														new DataValue(TMCode, Types.VARCHAR),//交易流水号 		
														new DataValue(TotalCount, Types.DOUBLE), //原单金额
														new DataValue(TotalPayment, Types.DOUBLE), 	//用户实际支付金额							
														new DataValue(tot_qty, Types.DOUBLE), //总数量
														new DataValue(TotalPayment, Types.DOUBLE), //商家实收金额
														new DataValue(0, Types.DOUBLE), //平台服务费
														new DataValue(TotalDiscount, Types.DOUBLE), //总折扣
														new DataValue(TMShippingFee, Types.DOUBLE), //预计运费
														new DataValue("2", Types.VARCHAR), //订单状态
														new DataValue(sysDate, Types.VARCHAR), //系统日期								
														new DataValue(sysTime, Types.VARCHAR),//系统时间 
														new DataValue("100", Types.VARCHAR), //状态
														new DataValue(OrderReceiverName, Types.VARCHAR), //收货人
														new DataValue(OrderReceiverMobile, Types.VARCHAR), //收货人电话
														new DataValue("", Types.VARCHAR),//收货人email 								
														new DataValue(VipMemberId, Types.VARCHAR),//会员卡
														new DataValue("", Types.VARCHAR),//会员姓名
														new DataValue("", Types.VARCHAR), //省份
														new DataValue(OrderReceiverCity, Types.VARCHAR), //城市
														new DataValue(OrderReceiverDistrict, Types.VARCHAR), //区县								
														new DataValue("", Types.VARCHAR), //乡镇街道
														new DataValue(2, Types.INTEGER), //币别 1:人民币 2：台币
														new DataValue(pickupWay, Types.INTEGER), //取货类型
														new DataValue(payStatus, Types.VARCHAR),//货到付款标记 1.未支付 2.部分支付 3.付清
														new DataValue(payAmt, Types.DOUBLE),//已付金额
														new DataValue(deliverytype, Types.VARCHAR),//物流类型
														new DataValue(expressBiltype, Types.VARCHAR),//
														new DataValue(inEccustomerno, Types.VARCHAR),
														new DataValue(OrderReceiverStoreId, Types.VARCHAR), 
														new DataValue(OrderReceiverStoreName, Types.VARCHAR) 
												};

										InsBean ibOrder = new InsBean("OC_ORDER", columnsOrder);
										ibOrder.addValues(insValueOrder);
										lstData.add(new DataProcessBean(ibOrder));

								

										//订单折扣
										DataValue[] insValueOrderAgio =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(TSCode, Types.VARCHAR), 
														new DataValue(inEccustomerno, Types.VARCHAR), //归属客户
														new DataValue(1, Types.INTEGER), //ITME
														new DataValue("91App折扣", Types.VARCHAR), //活动名称
														new DataValue(TotalDiscount, Types.DOUBLE), //总折扣额
														new DataValue(TotalDiscount, Types.DOUBLE), //商家承担额
														new DataValue(0, Types.DOUBLE), //平台承担额
														new DataValue(sysDate, Types.VARCHAR), //系统日期
														new DataValue(sysTime, Types.VARCHAR), //系统时间
														new DataValue("100", Types.VARCHAR), 
														new DataValue("91app", Types.VARCHAR), //平台代码
												};

										InsBean ibOrderAgio = new InsBean("OC_ORDER_AGIO", columnsOrderAgio);
										ibOrderAgio.addValues(insValueOrderAgio);
										lstData.add(new DataProcessBean(ibOrderAgio));		


										//过滤付款方式映射
										map_condition = new HashMap<String, Object>();
										map_condition.put("ORDER_PAYCODE",OrderPayType);		
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

										//订单付款
										DataValue[] insValueOrderPay =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(TSCode, Types.VARCHAR), 
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
														new DataValue(TotalPayment, Types.DOUBLE), //pay
														new DataValue(0, Types.DOUBLE), //extra
														new DataValue(0, Types.DOUBLE), //changed
														new DataValue(sysDate, Types.VARCHAR), //bdate
														new DataValue("N", Types.VARCHAR), //isorderpay
														new DataValue("91app", Types.VARCHAR), //load_doctype
														new DataValue(OrderPayType, Types.VARCHAR), //order_paycode
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
														new DataValue(TSCode, Types.VARCHAR), //
														new DataValue("91app", Types.VARCHAR), //電商平台
														new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
														new DataValue("订单状态", Types.VARCHAR), // 状态类型名称
														new DataValue("2", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
														new DataValue("已接單", Types.VARCHAR), // 状态名称
														new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
														new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
														new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
														new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
														new DataValue("admin", Types.VARCHAR), //操作員編碼
														new DataValue("管理員", Types.VARCHAR), //操作員名稱
														new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
														new DataValue("订单状态-->已接單", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
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
								
							}

							//
							UptBean ubec=new UptBean("OC_ECOMMERCE");
							ubec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ubec.addCondition("ECPLATFORMNO", new DataValue("91app", Types.VARCHAR));

							ubec.addUpdateValue("JY_LASTUPDATETIME", new DataValue(sLastuptime, Types.VARCHAR));

							lstData.add(new DataProcessBean(ubec));		

							//批处理执行**********************************************
							//****************************************************
							StaticInfo.dao.useTransactionProcessData(lstData);

							getQData=null;

						}

					}
				}

			}
			else				
			{
				logger.error("\r\n*********91App订单抓取EcNineOneApp API资料未设置:************\r\n");
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

				logger.error("\r\n******91App订单抓取EcNineOneApp报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******91App订单抓取EcNineOneApp报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			more=false;//

			noapp=null;

			bRun=false;//
			logger.info("\r\n*********91App订单抓取EcNineOneApp定时调用End:************\r\n");
		}
		return sReturnInfo;

	}



}
