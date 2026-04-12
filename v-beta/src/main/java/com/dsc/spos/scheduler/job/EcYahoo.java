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
import com.dsc.spos.utils.ec.Yahoo;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EcYahoo extends InitJob {


	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(EcYahoo.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public EcYahoo()
	{

	}

	public EcYahoo(String eId,String shopId,String organizationNO, String billNo)
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
			logger.info("\r\n*********雅虎订单抓取EcYahoo正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-雅虎订单抓取EcYahoo正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********雅虎订单抓取EcYahoo定时调用Start:************\r\n");

		boolean more=true; //可能有很多订单

		//雅虎调用
		Yahoo Yahoo=new Yahoo();			

		try
		{		

			//取第一笔
			String sql="select a.* from OC_ECOMMERCE A  "
					+ " where A.ECPLATFORMNO='yahoosuper' and A.status='100' ";			

			List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
			if (sqllist != null && sqllist.isEmpty() == false)
			{
				for (Map<String, Object> map : sqllist) 
				{
					String apiUrl = map.get("API_URL").toString();
					String lastUpdatetime=map.get("YAHOO_LASTUPDATETIME").toString();
					String eId=map.get("EID").toString();
					String inShop=map.get("SHOPID").toString();
					String inWarehouse=map.get("WAREHOUSE").toString();
					String inEccustomerno=map.get("ECCUSTOMERNO").toString();
					String isInvoice=map.get("ISINVOICE").toString();
					String currencyNo =map.get("CURRENCYNO").toString(); 
					
					String inShopName="";
					
					//谁在有的表里面干的主键
					if (inEccustomerno.equals("")) 
					{
						inEccustomerno=" ";
					}

					//订单付款方式映射
					String sqlTvMappingPay="select * from OC_mappingpayment t where t.load_doctype='Yahoo' and t.status='100' and t.eId='"+eId+"' ";
					
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

					// yahoo 的时间格式是 2019-01-01 12:00:00  ，
					// 如果是第一次拉取订单 或 拉取订单时出现异常， 导致update 上次拉取时间没有更新， 就从当前日期的00:00:00 开始拉取订单
					calendar = Calendar.getInstance();
					SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyy-MM-dd");
					String sysDate=dfDatetime.format(calendar.getTime());
					dfDatetime = new SimpleDateFormat("HHmmss");
					String sysTime=dfDatetime.format(calendar.getTime());

					long lEndTimestamp=System.currentTimeMillis();

					//设置格式
					SimpleDateFormat format =  new SimpleDateFormat("yyyyMMddHHmmss"); 
					String timeText=format.format(lEndTimestamp);

					//处理
					lEndTimestamp=lEndTimestamp/1000;

					//页码
					String startDate = "";
					String endDate = "";
					int position = 0;
					int count = 100;
					
					int pageCount = 0;
					int pageIndex = 0;
					
					while (more)
					{
						/**
						 * orderType 订单类型
						 *  All:全部
							NonShipping:未出貨
							Shipping:已出貨
							NonClose:未結案
							Closed:已結案
						 */
						String orderType = "NonShipping"; // 訂單類型 设置固定值：未出货
						String shippingType = ""; // 配送方式     HomeDelivery:宅配     StoreDelivery:超商
						String dateType = "TransferDate"; //日期类型  TransferDate:轉單日 ，LastShippingDate:最晚出貨日 ， OrderCloseDate:訂單結案日
						
						String shiptype="";
						
						int totalCount = 0;
						// 如果资料  总数 > 100, 设置position+1，  count+100 即可
						
						String resbody = Yahoo.getYaHooOrder(apiUrl, orderType, shippingType, 
								dateType, startDate, endDate, position, count);
						JSONObject jsonres = new JSONObject(resbody);
						JSONObject jsonres2 = jsonres.getJSONObject("Response");
						
						String status = jsonres2.getString("@Status").toUpperCase();
						if(jsonres.has("fail"))//错误
						{
							logger.error("\r\n******雅虎订单抓取EcYahoo报错信息error******\r\n");
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
							String sqlOrg="select * from DCP_ORG_lang where EID='"+eId+"' and organizationno='"+inShop+"' and status='100' and lang_type='"+langtype+"' ";
							List<Map<String, Object>> sqlGetORG=this.doQueryData(sqlOrg, null);
							if (sqlGetORG != null && sqlGetORG.isEmpty() == false)
							{
								inShopName=sqlGetORG.get(0).get("ORG_NAME").toString();
							}
							
							if(status.equals("OK")){
								// 获取到返回JSON 里的各订单编号
								JSONObject TransactionList = jsonres.getJSONObject("TransactionList");
								// 本次查询资料笔数
								int resCount = TransactionList.getInt("@Count"); // resCount 表示本次查询笔数, ==count,上面已经固定为100
								// 资料总数
								totalCount = TransactionList.getInt("@TotalCount");
								
								int c = totalCount/count;
								int d = totalCount%count;
								
								pageCount = (d > 0) ? c + 1 : c;	
//								//System.out.println("第 "+ pageIndex +"页");
								if(pageIndex < pageCount){
									more = true;
									pageIndex += 1;
									
									position = count +1 ;
									count = count + 100 ;
								}
								else{
									more = false;
								}
								
								if(totalCount > resCount){
									more = true ;
									position = count +1 ;
									
								}
								else{
									more = false;
								}
								
								
								JSONArray Transaction = TransactionList.getJSONArray("Transaction");
								// 查询订单之后， 得到交易序号 transId 和 订单编号数组 orderIdList
								String inMultiOrderno = "";
								for (int i = 0; i < Transaction.length(); i++) {
									
									List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();		
									
									JSONObject jo = Transaction.getJSONObject(i);
									String transId = jo.getString("@Id");
									JSONArray orderList = jo.getJSONArray("Order");
									String[] orderIdList=new String[orderList.length()];
									
									for (int j = 0; j < orderList.length(); j++) { 
										JSONObject order = orderList.getJSONObject(j);
										String orderId = order.getString("@Id");
																		
										orderIdList[j] = orderId;
										//System.out.println(orderId);
										inMultiOrderno=inMultiOrderno + "'" + orderId+"',";
									}
									
									String sqlExistOrdernoString="SELECT * FROM OC_ORDER T WHERE T.eId='"+eId+"' AND T.ORDERNO IN("+inMultiOrderno+")";
									List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sqlExistOrdernoString, null);
									
									if(getQData!= null){
										logger.error("\r\n******雅虎订单抓取EcYahoo 报错信息error 交易序号"+transId+" 中存在已导入的订单，请检查该购物车的订单信息******\r\n");
										continue;
									}
									
									//—***************** 查询购物车订单 GetMaster 接口*************
									String isActivity = "";
									String isUseCoupon = "";
									String payType = "";
									String shippingSupplier = "";
									String storeType = "";
									double transactionPrice = 0;
									String contMan = ""; 
									String contTel = "";
									String memo = "";
									// 得到配送方式、 付款方式、订购人 等信息 
									String masterResBody = Yahoo.getMaster(apiUrl, transId);
									JSONObject masterJsonRes = new JSONObject(masterResBody);
									JSONObject masterJsonRes2 = masterJsonRes.getJSONObject("Response");
									
									String masterStatus = masterJsonRes2.getString("@Status").toUpperCase();
									if(masterStatus.equals("OK"))
									{
										// 获取到返回JSON 里的各订单编号
										JSONObject masterTransactionList = jsonres2.getJSONObject("Transaction");
										int id = masterTransactionList.getInt("@Id");
										isActivity = masterTransactionList.getString("IsActivity"); //此笔交易是否又符合活动 ， 有Yes， 无No
										isUseCoupon = masterTransactionList.getString("IsUseCoupon"); //此笔交易是否有使用电子折扣券， 有Yes， 无No
										payType = masterTransactionList.getString("PayType"); // 付款方式 
										//付款方式
//										CreditCard:信用卡，     ATM:ATM,	StorePay:超商繳費(IBON/FAMI),	StoreDelivery:超商付款取貨
										
										String Installment = masterTransactionList.getString("Installment"); // 分期期数
										shippingType = masterTransactionList.getString("ShippingType"); // 配送方式
										
										
										
										if(shippingType.equals("HomeDelivery"))
										{
											shippingType = "2"; //宅配
											
											shiptype="2";
										}
										else if(shippingType.equals("StoreDelivery"))
										{
											shippingType = "1"; //超取
											
											shiptype="6";
										}
										else
										{
											shippingType = ""; 
										}
										
										
										
										//HomeDelivery：宅配    StoreDelivery：超商   ESD：ESD
										storeType = masterTransactionList.getString("StoreType"); // 超商种类
										transactionPrice = masterTransactionList.getDouble("TransactionPrice"); //订单总金额
										contMan = masterTransactionList.getString("_BuyerName");  //订购人
										contTel = masterTransactionList.getString("_BuyerPhone"); //订购人电话
										memo = masterTransactionList.getString("_TransactionRemark"); //备注
										
										JSONObject masterOrderList = masterTransactionList.getJSONObject("OrderList");
										
										JSONArray order = masterOrderList.getJSONArray("Order");
										//System.out.println("Order:"+order);
										
										for (int k = 0; k < order.length(); k++) {
											JSONObject mj = order.getJSONObject(k);
											String orderNo = mj.getString("@Id");
									        String orderCloseDate = mj.getString("OrderCloseDate"); // 订单结案日
									        String orderPackageDate = mj.getString("OrderPackageDate"); //包装确认日
									        String orderStatus = mj.getString("OrderStatus"); //订单状态 ， NEW:未结案 ； CANCEL:取消； SHIPPED:完成出货
									        String orderStatusDesc = mj.getString("OrderStatusDesc"); // 订单状态描述
										}
									}
									else
									{
										logger.error("\r\n******雅虎订单抓取EcYahoo，获取购物车订单 报错信息error 交易序号"+transId+"******\r\n");
									}
									
									// 查询购物车订单明细资料 GetDetail接口
									String getDetailResBody = Yahoo.getDetail(apiUrl, transId, orderIdList);
									
									JSONObject getDetailRes = new JSONObject(getDetailResBody);
									JSONObject getDetailRes2 = getDetailRes.getJSONObject("Response");
									String getDetailStatus = getDetailRes.getString("@Status").toUpperCase();
									
									if(getDetailStatus.equals("OK"))
									{
										
										// 获取到返回JSON 里的各订单编号
										JSONObject detailTransactionList = getDetailRes2.getJSONObject("Transaction");
										String id = detailTransactionList.getString("@Id");
										
										JSONObject receiver = detailTransactionList.getJSONObject("Receiver");
										JSONObject receiverNameNode = receiver.getJSONObject("ReceiverName");
										JSONObject receiverPhoneNode = receiver.getJSONObject("ReceiverPhone");
										JSONObject receiverMobileNode = receiver.getJSONObject("ReceiverMobile");
										JSONObject receiverZipCodeNode = receiver.getJSONObject("ReceiverZipcode");
										JSONObject receiverAddressNode = receiver.getJSONObject("ReceiverAddress");
										
										String receiverName = receiverNameNode.getString("#cdata-section");
										String receiverPhone = receiverPhoneNode.getString("#cdata-section");
										String receiverMobile = receiverMobileNode.getString("#cdata-section");
										String receiverZipCode = receiverZipCodeNode.getString("#cdata-section");
										String receiverAddress = receiverAddressNode.getString("#cdata-section");

										JSONObject successList = detailTransactionList.getJSONObject("SuccessList");
										String detailCount = successList.getString("@Count");
										JSONObject orderDetailList = successList.getJSONObject("OrderList");
										JSONObject orderDetail = orderDetailList.getJSONObject("Order");
										String orderNo = orderDetail.getString("@Id");
										JSONObject OrderProductList = orderDetail.getJSONObject("OrderProductList");
										JSONObject Product = OrderProductList.getJSONObject("Product");
										String orderNo2 = Product.getString("@Id");
										String saleType = Product.getString("SaleType");
										String productType = Product.getString("ProductType");
										JSONObject customizedProductIdNode = Product.getJSONObject("CustomizedProductId"); //自定货号
										String customizedProductId = customizedProductIdNode.getString("#cdata-section");
										JSONObject productNameNode = Product.getJSONObject("ProductName"); // 商品名称， ==物流服务费，此订单不需要执行出货确认
										String productName = productNameNode.getString("#cdata-section");
										
										JSONObject specNode = Product.getJSONObject("Spec"); //规格
										String spec = productNameNode.getString("#cdata-section");
										
										String amount = Product.getString("Amount"); //数量
										String originalPrice = Product.getString("OriginalPrice"); //购物车内商品单价	
										String listPrice = Product.getString("ListPrice"); // 商品页销售价格
										String productCostAmount = Product.getString("ProductCostAmount"); //
										String promotionCampaignId = Product.getString("PromotionCampaignId"); //折扣码活动编号
										String promotionReduction = Product.getString("PromotionReduction"); // 折扣码折抵金额
										String promotionCode = Product.getString("PromotionCode"); // 折扣码
										String usedPoint = Product.getString("UsedPoint"); // 超赠点点数
										String basicPointDiscount = Product.getString("BasicPointDiscount");// 超赠点 折抵金额
										
										// 折扣总金额 = 商家折抵 + 平台折抵
										// BasicPointDiscount 超赠折抵 + PromotionReduction 折扣码折抵  ==商家折抵
										if(promotionReduction==null){
											promotionReduction = "0";
										}
										if(basicPointDiscount==null){
											basicPointDiscount = "0";
										}
										double totalDisc = (Double.parseDouble(promotionReduction) + Double.parseDouble(basicPointDiscount));
										String totalDiscStr = String.valueOf(totalDisc);
										
										String productDiscountDetail = Product.getString("ProductDiscountDetail"); 
										String subtotal = Product.getString("Subtotal");
										String taxType = Product.getString("TaxType"); // 税别，Taxable: 应税； Taxfree：免税；  Nolnv: 免发票
										
										JSONObject DeliverTypeNode = orderDetail.getJSONObject("DeliverType");
										String deliverType = DeliverTypeNode.getString("#cdata-section");
										JSONObject OrderNoteNode = orderDetail.getJSONObject("DeliverType");
										String orderNote = OrderNoteNode.getString("#cdata-section");
										
										String transferDate = orderDetail.getString("TransferDate");
										String lastShippingDate = orderDetail.getString("LastShippingDate");
										String orderShippingDate = orderDetail.getString("OrderShippingDate");
										String orderCloseDate = orderDetail.getString("OrderCloseDate");
										String buyerConfirmDate = orderDetail.getString("BuyerConfirmDate");
										String entryAccountDate = orderDetail.getString("EntryAccountDate");
										String pickingDate = orderDetail.getString("PickingDate");
										String orderPackageDate = orderDetail.getString("OrderPackageDate");
										
										String invoiceNo = orderDetail.getString("InvoiceNo");
										String invoiveDate = orderDetail.getString("InvoiveDate");
										
										String lastDeliveryDate = orderDetail.getString("LastDeliveryDate");
										String orderStatus = orderDetail.getString("OrderStatus");
										
										JSONObject orderStatusDescNode = orderDetail.getJSONObject("OrderStatusDesc");
										String orderStatusDesc = orderStatusDescNode.getString("#cdata-section");
										
										JSONObject OrderShippingIdNode = orderDetail.getJSONObject("OrderShippingId");
										String orderShippingId = OrderShippingIdNode.getString("#cdata-section");
										
										JSONObject failList = TransactionList.getJSONObject("FailList");
										String failCount = failList.getString("@Count");
										// 如果是失败信息， 这里会有失败的订单信息
										JSONObject MessageNode = getDetailRes2.getJSONObject("Message");
										String message = MessageNode.getString("#cdata-section");
										
										//********* 插入订单表**********
										String deliveryType = "0";
										String getShop = "";
										String getShopName = "";
										if (storeType.contains("7-11")) 
										{
											deliveryType="7";
											getShop = storeType;
											getShopName = "7-11(统一超商)";
										}
										else if (storeType.contains("Family")) 
										{
											deliveryType="8";
											getShop = storeType;
											getShopName = "全家超商";										
										}
										else if (storeType.contains("HiLife")) 
										{
											deliveryType="10";
											getShop = storeType;
											getShopName = "莱尔富超商";		
										}
										else 
										{
											deliveryType ="0";//暂不支持
											getShop = "";
											getShopName = "";		
										}
										
										
										//插入单头
										String[] columnsOrder = {
												"EID",
												"SHOPID",
												"SHOPNAME",
												"ORDERNO",
												"LOAD_DOCTYPE",
												"CURRENCYNO",
												"PICKUPWAY",
												"SHIPPINGSHOP",
												"SHIPPINGSHOPNAME",
												"SHIPTYPE",
												"CONTMAN",
												"CONTTEL",
												"ADDRESS",
												"SHIPDATE",
												"SHIPTIME",
												"SHIPHOURTYPE",
												"MEMO",
												"CREATE_DATETIME",
												"ORDER_SN",
												"ISINVOICE",
												
												"SHIPFEE",
												"TOTSHIPFEE",
												"RSHIPFEE",
												
												"TOT_OLDAMT",
												"TOT_AMT",
												"TOT_QTY",
												"INCOMEAMT",
												"TOT_DISC",
												"SELLER_DISC",
												"PLATFORM_DISC",
												"GETMAN",
												"GETMANTEL",
												"GETMANEMAIL",
												"CARDNO",
												"MEMBERNAME",
												
												"STATUS",
												"DELIVERYTYPE",//yahoo 这里固定给 0 
												
												//以下四个省市等，都存空值即可
												"PROVINCE",
												"CITY",
												"COUNTY",
												"STREET",
												
												"PAYSTATUS",
												"PAYAMT",
												"EXPRESSBILLTYPE",//托运单单别 黑猫需要
												"ECCUSTOMERNO",//电商归属客户代号
												
												"SDATE",
												"STIME",
												"STATUS",
												"ORDERSHOP",//超商门店编码
												"ORDERSHOPNAME",//超商门店名称
										};
										
										
										DataValue[] insValueOrder =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 								
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(inShopName, Types.VARCHAR), 
														new DataValue(orderNo, Types.VARCHAR), 
														new DataValue("yahoosuper", Types.VARCHAR), // 来源类型
														new DataValue(currencyNo, Types.VARCHAR), 
														new DataValue(shippingType , Types.VARCHAR), // 取货方式， 超取/宅配
														new DataValue(inShop , Types.VARCHAR), // 配送门店、取货门店
														new DataValue(inShopName , Types.VARCHAR),
														new DataValue(shiptype , Types.VARCHAR),////1.外卖平台配送 2.门店配送(台湾显示为宅配) 3.顾客自提 5 总部配送 6.超商
														new DataValue(contMan , Types.VARCHAR),
														new DataValue(contTel , Types.VARCHAR),
														new DataValue(receiverAddress , Types.VARCHAR),
														new DataValue("" , Types.VARCHAR),
														new DataValue("" , Types.VARCHAR),
														new DataValue("" , Types.VARCHAR),
														
														new DataValue(memo , Types.VARCHAR),//备注
														new DataValue(buyerConfirmDate , Types.VARCHAR), // 下单日期， 取客户确认订单时间，需要更换时间格式
														new DataValue(transId , Types.VARCHAR),//交易序号
														new DataValue(isInvoice , Types.VARCHAR),// 是否开发票
														
														new DataValue("0" , Types.VARCHAR),//  实际配送费，yahoo 接口没有运费，程序设置给0
														new DataValue("0" , Types.VARCHAR),//  总配送费 
														new DataValue("0" , Types.VARCHAR),//  配送费减免 
														
														new DataValue(transactionPrice , Types.VARCHAR), //此笔订单总金额
														new DataValue(subtotal , Types.VARCHAR), //折扣后订单金额
														new DataValue(amount , Types.VARCHAR), // 数量
														new DataValue(subtotal , Types.VARCHAR), // 商家实收金额 （没有服务费 和 商户补贴金额， 所以商家实收金额 == 折扣后订单金额 ）
														
														// 折扣总金额 = 商家折抵 + 平台折抵
														// BasicPointDiscount 超赠折抵 + PromotionReduction 折扣码折抵  ==商家折抵
														new DataValue(totalDiscStr , Types.VARCHAR), // 折扣总额
														new DataValue(totalDiscStr , Types.VARCHAR), // 商家折扣额
														new DataValue("0" , Types.VARCHAR), // 平台折扣额
														new DataValue(receiverName , Types.VARCHAR),
														new DataValue(receiverPhone , Types.VARCHAR),
														new DataValue("" , Types.VARCHAR), // 收件人邮箱
														new DataValue("" , Types.VARCHAR), // 会员卡号
														new DataValue("" , Types.VARCHAR), // 会员姓名
														new DataValue("2" , Types.VARCHAR), // 订单状态  2已接单
														new DataValue( deliveryType , Types.VARCHAR), // deliveryType 可以固定为0
														//以下四个为省市地址
														new DataValue("" , Types.VARCHAR),
														new DataValue("" , Types.VARCHAR),
														new DataValue("" , Types.VARCHAR),
														new DataValue("" , Types.VARCHAR),
														
														new DataValue("3" , Types.VARCHAR), //payStatus 固定为3 已付清， yahoo 没有是否货到付款的标记
														new DataValue( totalDiscStr , Types.VARCHAR), // 已付金额
														new DataValue("A" , Types.VARCHAR), // 固定为 一般托运单
														new DataValue(inEccustomerno , Types.VARCHAR), // 电商归属客户代号
														
														new DataValue(sysDate , Types.VARCHAR),  
														new DataValue(sysTime , Types.VARCHAR), 
														new DataValue("100" , Types.VARCHAR) ,
														new DataValue(getShop , Types.VARCHAR), 
														new DataValue(getShopName , Types.VARCHAR) 
														
												};

										InsBean ibOrder = new InsBean("OC_ORDER", columnsOrder);
										ibOrder.addValues(insValueOrder);
										lstData.add(new DataProcessBean(ibOrder));	
										
										// ************ 插入订单明细表 ************
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
										DataValue[] insValueOrderDetail =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(orderNo, Types.VARCHAR), 
														new DataValue("yahoosuper", Types.VARCHAR), 
														new DataValue(1, Types.INTEGER), //item
														// customizedProductId 是电商品号 , 这里应该是pluNo, 商品映射做完之后,查商品映射中对应的商品编号 
														new DataValue(customizedProductId, Types.VARCHAR), 
														new DataValue("", Types.VARCHAR), //plubarcode
														new DataValue(productName, Types.VARCHAR), //pluname
														new DataValue(spec, Types.VARCHAR), //specname
														new DataValue("", Types.VARCHAR), //unit
														new DataValue(originalPrice, Types.DOUBLE),//price 
														new DataValue(amount, Types.DOUBLE), //qty
														new DataValue(totalDisc, Types.DOUBLE),//折扣 
														new DataValue(subtotal, Types.DOUBLE), //amt
														new DataValue("100", Types.VARCHAR), 
														new DataValue("", Types.VARCHAR), //order_sn
														new DataValue(customizedProductId, Types.VARCHAR), //电商品号
												};

										InsBean ibOrderDetail = new InsBean("OC_ORDER_DETAIL", columnsOrderDetail);
										ibOrderDetail.addValues(insValueOrderDetail);
										lstData.add(new DataProcessBean(ibOrderDetail));	

										
										
										
										// ************ 插入折扣信息 ************
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
										
										//订单折扣
										DataValue[] insValueOrderAgio =new DataValue[]
												{
														new DataValue(eId, Types.VARCHAR), 
														new DataValue(inShop, Types.VARCHAR), 
														new DataValue(orderNo, Types.VARCHAR), 
														new DataValue(inEccustomerno, Types.VARCHAR), //归属客户
														new DataValue(1, Types.INTEGER), //ITME
														new DataValue("雅虎折扣", Types.VARCHAR), //活动名称
														new DataValue(totalDiscStr, Types.DOUBLE), //总折扣额
														new DataValue(totalDiscStr, Types.DOUBLE), //商家承担额
														new DataValue(0, Types.DOUBLE), //平台承担额
														new DataValue(sysDate, Types.VARCHAR), //系统日期
														new DataValue(sysTime, Types.VARCHAR), //系统时间
														new DataValue("100", Types.VARCHAR), 
														new DataValue("Yahoo", Types.VARCHAR), //平台代码
												};

										InsBean ibOrderAgio = new InsBean("OC_ORDER_AGIO", columnsOrderAgio);
										ibOrderAgio.addValues(insValueOrderAgio);
										lstData.add(new DataProcessBean(ibOrderAgio));
										
										
										//************* 插入付款方式信息  **************

										//查询付款方式映射
										List<Map<String, Object>> sqllistPaymapping=this.doQueryData(sqlTvMappingPay, null);
										
										
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
										
										//对应的ERP支付方式
										String erp_paycode="";
										String erp_payName="";
										String erp_paycodeERP="";

										//过滤付款方式映射
										Map<String, Object> map_condition = new HashMap<String, Object>();
										map_condition = new HashMap<String, Object>();
										map_condition.put("ORDER_PAYCODE",payType ); // payType 取自GetMaster接口		
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
														new DataValue(orderNo, Types.VARCHAR), 
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
														new DataValue(subtotal, Types.DOUBLE), //pay
														new DataValue(0, Types.DOUBLE), //extra
														new DataValue(0, Types.DOUBLE), //changed
														new DataValue(sysDate, Types.VARCHAR), //bdate
														new DataValue("N", Types.VARCHAR), //isorderpay
														new DataValue("Yahoo", Types.VARCHAR), //load_doctype
														new DataValue(payType, Types.VARCHAR), //order_paycode
														new DataValue("N", Types.VARCHAR), //isonlinepay
														new DataValue("0", Types.DOUBLE), //rcpay
														new DataValue("100", Types.VARCHAR), //STATUS
												};

										InsBean ibOrderPay = new InsBean("OC_ORDER_PAY", columnsOrderPay);
										ibOrderPay.addValues(insValueOrderPay);
										lstData.add(new DataProcessBean(ibOrderPay));
										
										//更新上次获取时间, 下一次调用该时间点之后的数据
										// yahoo 不用设置 上次获取订单时间, 调用订单的参数中已经有 日期+时间 的参数, 每次切换该参数的值即可
										UptBean ubec=new UptBean("OC_ECOMMERCE");
										ubec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
										ubec.addCondition("ECPLATFORMNO", new DataValue("yahoosuper", Types.VARCHAR));

										ubec.addUpdateValue("YAHOO_LASTUPDATETIME", new DataValue(timeText, Types.VARCHAR));

										lstData.add(new DataProcessBean(ubec));		

										//批处理执行**********************************************
										//****************************************************
										StaticInfo.dao.useTransactionProcessData(lstData);

										getQData=null;
										
										
									}
									else{
										logger.error("\r\n******雅虎订单抓取EcYahoo，获取订单明细资料 报错信息error 交易序号"+transId+"******\r\n");
									}
									
									
							    }

							}


						}

						jsonres=null;
					}
				}
			}
			else				
			{
				logger.error("\r\n*********雅虎订单抓取EcYahoo API资料未设置:************\r\n");
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

				logger.error("\r\n******雅虎订单抓取EcYahoo报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******雅虎订单抓取EcYahoo报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			//more=false; 

			Yahoo=null;

			bRun=false;//
			logger.info("\r\n*********雅虎订单抓取EcYahoo定时调用End:************\r\n");
		}
		return sReturnInfo;

	}
	
	
}
