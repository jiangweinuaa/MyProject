package com.dsc.spos.utils.ec;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;

//电商---乐天
//乐天文档 licensekey与secretkey组成的Authorization
//但实际给的是Authorization
//Authorization=ESA+空格+变量值
public class Rakuten 
{

	//
	//DateTime dt = new DateTime("2016-03-31T09:08:00");
	////System.out.println(dt.toString("yyyyMMdd"));
	////System.out.println(dt.toString("HHmmss"));

	/**
	 * 订单获取
	 * @param apiUrl https://openapi-rms.global.rakuten.com/2.0
	 * @param licensekey
	 * @param secretkey
	 * @param shopUrl
	 * @param createdAfter 开始时间 yyyy-MM-dd'T'HH:mm:ss.SSSZ
	 * @param createdBefore 结束时间 yyyy-MM-dd'T'HH:mm:ss.SSSZ
	 * @param pageIndex  0
	 * @return
	 */
	public String Order_POST(String apiUrl,String licensekey,String secretkey,String shopUrl,String createdAfter,String createdBefore, int pageIndex)
	{

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="ordersearch";
		}
		else			
		{
			apiUrl+="/ordersearch";
		}

		String resbody="";

		try 
		{
			//EncryptUtils eu = EncryptUtils.getInstance();	
			//String Authorization=eu.encodeBASE64(licensekey+":"+secretkey);
			String Authorization="ESA " + secretkey;

			////当前时间UTC
			//Calendar calendar = Calendar.getInstance();
			//SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			//String sysDatetime=dfDatetime.format(calendar.getTime());
			////System.out.println(sysDatetime);

			JSONObject header = new JSONObject();				

			JSONObject query = new JSONObject();	
			//
			JSONObject shopKey = new JSONObject();	
			shopKey.put("marketplaceIdentifier", "tw");//台湾乐天
			shopKey.put("shopUrl", shopUrl);//店家URL
			//
			query.put("shopKey", shopKey);
			query.put("createdAfter", createdAfter);
			query.put("createdBefore", createdBefore);		

			//*****订单状态(OrderStatusString)*****			
			//Unfixed	尚未計算訂單總金額運費必需輸入 
			//AwaitingPayment 訂單待付款 
			//ProcessingPayment 付款處理中 
			//NotShipped 訂單尚未配送==========================
			//AwaitingCompletion 付款與配送已完成，訂單待結帳 訂單狀態會在訂單配送完成七天後改為Complete 
			//Complete 訂單已完成，不能被取消或退回 
			//Cancelled 訂單已取消 
			//CancelledAndRefund 店家在付款後已取消訂單

			JSONArray orderStatus = new JSONArray();//订单状态
			orderStatus.put("NotShipped");//尚未配送的订单
			query.put("orderStatus", orderStatus);

			//
			header.put("query", query);

			header.put("sortOrder", "ASC");//排序
			header.put("maxResultsPerPage", "50");//每页记录数1-100
			header.put("pageIndex", pageIndex);//页码
			header.put("returnOrderDetail", true);//订单明细

			//
			String request=header.toString();

			resbody=HttpSend.SendRakuten("Order_POST", request, apiUrl, Authorization, "POST");
			JSONObject jsonres = new JSONObject(resbody);

			if (jsonres.has("errors"))
			{

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
				}				
			}
			else 
			{
				//订单总笔数
				int totalCount=jsonres.getInt("totalCount");
				//System.out.println("订单总笔数:" + totalCount);
				//订单
				JSONArray orders = jsonres.getJSONArray("orders");
				for (int i = 0; i < orders.length(); i++) 
				{
					//订单号,格式： [7位數 字]-[YYMMDD][10位數字]
					String orderNumber=orders.getJSONObject(i).getString("orderNumber");
					//System.out.println("订单號:" + orderNumber);

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
					//订单日期	
					String orderDate=orders.getJSONObject(i).getString("orderDate");
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


					//商品明细
					if (orders.getJSONObject(i).has("orderItems")) 
					{
						JSONArray orderItems = orders.getJSONObject(i).getJSONArray("orderItems");						
						for (int a = 0; a < orderItems.length(); a++) 
						{
							//名称zh_TW/en_us
							JSONObject name = orderItems.getJSONObject(a).getJSONObject("name");
							//商品名称
							String plunameString="";
							if (name.has("zh_TW")) 
							{
								plunameString=name.getString("zh_TW");
							}
							//baseSKU編號 ，电商品号
							String baseSku=orderItems.getJSONObject(a).getString("baseSku");
							//sku的規格 ,品号
							String sku=orderItems.getJSONObject(a).getString("sku");

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
						}
					}


					//配送
					if (orders.getJSONObject(i).has("shipping")) 
					{
						JSONObject shipping=orders.getJSONObject(i).getJSONObject("shipping");
						//包裹编号
						String orderPackageId=shipping.getString("orderPackageId");

						//商品配送地址
						JSONObject deliveryAddress=shipping.getJSONObject("deliveryAddress");
						//收件人姓名	
						String name=deliveryAddress.getString("name");		
						//收件人电话						
						String phoneNumber=deliveryAddress.getString("phoneNumber");
						//收件人地区/国家代码
						String countryCode=deliveryAddress.getString("countryCode");
						//收件人地址1
						String address1=deliveryAddress.getString("address1");
						//System.out.println("配送地址1:" + address1);
						
						//收件人邮号
						String postalCode="";
						if (deliveryAddress.has("postalCode")) 
						{
							postalCode=deliveryAddress.getString("postalCode");
						}						
						//收件人省份代码
						String stateCode="";
						if (deliveryAddress.has("stateCode")) 
						{
							stateCode=deliveryAddress.getString("stateCode");
						}
						//收件人省份名称
						String stateName="";
						if (deliveryAddress.has("stateName")) 
						{
							stateName=deliveryAddress.getString("stateName");
						}						
						//收件人城市代码
						String cityCode="";
						if (deliveryAddress.has("cityCode")) 
						{
							cityCode=deliveryAddress.getString("cityCode");
						}
						//收件人城市名称	
						String cityName="";
						if (deliveryAddress.has("cityName")) 
						{
							cityName=deliveryAddress.getString("cityName");
						}
						//收件人地址2
						String address2="";
						if (deliveryAddress.has("address2")) 
						{
							address2=deliveryAddress.getString("address2");
							//System.out.println("配送地址2:" + address1);
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
						String shippingMethod=shipping.getString("shippingMethod");
						//System.out.println("配送方式:" + shippingMethod);
						
						//配送状态
						String shippingStatus=shipping.getString("shippingStatus");						
						//System.out.println("配送状态:" + shippingStatus);
						
						//物流商
						String carrierName="";
						if (shipping.has("carrierName")) 
						{
							carrierName=shipping.getString("carrierName");
						}
						//System.out.println("物流商:" + carrierName);

						//运单号
						String trackingNumber="";
						if (shipping.has("trackingNumber")) 
						{
							trackingNumber=shipping.getString("trackingNumber");
						}
						//System.out.println("运单号:" + trackingNumber);
						
						

						//配送日期
						String shippingDate="";
						if (shipping.has("shippingDate")) 
						{
							shippingDate=shipping.getString("shippingDate");
						}
						//配送费			
						String shippingFee="";
						if (shipping.has("shippingFee")) 
						{
							shippingFee=shipping.getString("shippingFee");
						}						

					}

					//付款信息
					if (orders.getJSONObject(i).has("payment")) 
					{
						JSONObject payment=orders.getJSONObject(i).getJSONObject("payment");
						//付款方式编码GUID
						String orderPaymentId=payment.getString("orderPaymentId");
						//付款方式名称，信用卡付款
						String paymentMethod=payment.getString("paymentMethod");
						//付款状态，Paid
						String paymentStatus=payment.getString("paymentStatus");
						//使用点数
						String pointAmount=payment.getString("pointAmount");						
						//付款金额
						String payAmount="";
						if (payment.has("payAmount")) 
						{
							payAmount=payment.getString("payAmount");				
						}	
						//付款日期
						String paymentDate="";
						if (payment.has("paymentDate")) 
						{
							paymentDate=payment.getString("paymentDate");
						}						
						//付款手續費
						String paymentFee="";			
						if (payment.has("paymentFee")) 
						{
							paymentFee=payment.getString("paymentFee");			
						}									

					}

					//電子发票信息
					if (orders.getJSONObject(i).has("eInvoice")) 
					{
						JSONObject eInvoice=orders.getJSONObject(i).getJSONObject("eInvoice");
						//发票状态，Not issued未開發票，
						String status=eInvoice.getString("status");
						//发票号码
						String number="";
						if(eInvoice.has("number"))
						{
							number=eInvoice.getString("number");
						}
						//发票类型 DONATE捐贈， DUPLICATE二聯， TRIPLICATE，三聯
						String type="";
						if(eInvoice.has("type"))
						{
							type=eInvoice.getString("type");
						}
						//发票载具ID
						String carrierId="";
						if(eInvoice.has("carrierId"))
						{
							carrierId=eInvoice.getString("carrierId");
						}
						//发票载具类型 RAKUTEN_MEMBER
						String carrierType="";
						if(eInvoice.has("carrierType"))
						{
							carrierType=eInvoice.getString("carrierType");
						}
						//发票开了日期
						String issuedDate="";
						if(eInvoice.has("issuedDate"))
						{
							issuedDate=eInvoice.getString("issuedDate");
						}
						//发票最后可作废日期
						String dueDate="";
						if(eInvoice.has("dueDate"))
						{
							dueDate=eInvoice.getString("dueDate");
						}
						//买方码
						String buyerBAN="";
						if(eInvoice.has("buyerBAN"))
						{
							buyerBAN=eInvoice.getString("buyerBAN");
						}
						//买方Email
						String buyerEmail="";
						if(eInvoice.has("buyerEmail"))
						{
							buyerEmail=eInvoice.getString("buyerEmail");
						}
						//社会福利码
						String socialWelfareBAN="";
						if(eInvoice.has("socialWelfareBAN"))
						{
							socialWelfareBAN=eInvoice.getString("socialWelfareBAN");
						}
						//社会福利名称
						String socialWelfareName="";
						if(eInvoice.has("socialWelfareName"))
						{
							socialWelfareName=eInvoice.getString("socialWelfareName");
						}
						//强制性津贴
						boolean forcedAllowance=false;
						if(eInvoice.has("forcedAllowance"))
						{
							forcedAllowance=eInvoice.getBoolean("forcedAllowance");
						}
					}

				}

			}

		} 
		catch (Exception ex) 
		{			
			//System.out.println(ex.getMessage());	

			return "";
		}			

		return resbody;

	}

	/**
	 * 獲取取消的訂單
	 * @param apiUrl https://openapi-rms.global.rakuten.com/2.0
	 * @param licensekey
	 * @param secretkey
	 * @param shopUrl
	 * @param createdAfter 开始时间 yyyy-MM-dd'T'HH:mm:ss.SSSZ
	 * @param createdBefore 结束时间 yyyy-MM-dd'T'HH:mm:ss.SSSZ
	 * @param pageIndex  0
	 * @return
	 */
	public String Order_POST_Cancel(String apiUrl,String licensekey,String secretkey,String shopUrl,String createdAfter,String createdBefore, int pageIndex)
	{

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="ordersearch";
		}
		else			
		{
			apiUrl+="/ordersearch";
		}

		String resbody="";

		try 
		{
			//EncryptUtils eu = EncryptUtils.getInstance();	
			//String Authorization=eu.encodeBASE64(licensekey+":"+secretkey);
			String Authorization="ESA " + secretkey;

			////当前时间UTC
			//Calendar calendar = Calendar.getInstance();
			//SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			//String sysDatetime=dfDatetime.format(calendar.getTime());
			////System.out.println(sysDatetime);

			JSONObject header = new JSONObject();				

			JSONObject query = new JSONObject();	
			//
			JSONObject shopKey = new JSONObject();	
			shopKey.put("marketplaceIdentifier", "tw");//台湾乐天
			shopKey.put("shopUrl", shopUrl);//店家URL
			//
			query.put("shopKey", shopKey);
			query.put("createdAfter", createdAfter);
			query.put("createdBefore", createdBefore);		

			//*****订单状态(OrderStatusString)*****			
			//Unfixed	尚未計算訂單總金額運費必需輸入 
			//AwaitingPayment 訂單待付款 
			//ProcessingPayment 付款處理中 
			//NotShipped 訂單尚未配送==========================
			//AwaitingCompletion 付款與配送已完成，訂單待結帳 訂單狀態會在訂單配送完成七天後改為Complete 
			//Complete 訂單已完成，不能被取消或退回 
			//Cancelled 訂單已取消 
			//CancelledAndRefund 店家在付款後已取消訂單

			JSONArray orderStatus = new JSONArray();//订单状态
			orderStatus.put("Cancelled");//已取消的订单
			query.put("orderStatus", orderStatus);

			//
			header.put("query", query);

			header.put("sortOrder", "ASC");//排序
			header.put("maxResultsPerPage", "100");//每页记录数1-100
			header.put("pageIndex", pageIndex);//页码
			header.put("returnOrderDetail", false);//订单明细

			//
			String request=header.toString();

			resbody=HttpSend.SendRakuten("Order_POST_Cancel", request, apiUrl, Authorization, "POST");
			JSONObject jsonres = new JSONObject(resbody);

			if (jsonres.has("errors"))
			{

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
				}				
			}
			else 
			{
				//订单总笔数
				int totalCount=jsonres.getInt("totalCount");
				//System.out.println("订单总笔数:" + totalCount);
				//订单
				JSONArray orders = jsonres.getJSONArray("orders");
				for (int i = 0; i < orders.length(); i++) 
				{
					//订单号,格式： [7位數 字]-[YYMMDD][10位數字]
					String orderNumber=orders.getJSONObject(i).getString("orderNumber");
					//System.out.println("订单號:" + orderNumber);

					//guid 用于促销查询
					String orderId=orders.getJSONObject(i).getString("orderId");
					String rorderStatus=orders.getJSONObject(i).getString("orderStatus");//订单状态		

				}

			}

		} 
		catch (Exception ex) 
		{			
			//System.out.println(ex.getMessage());	

			return "";
		}			

		return resbody;

	}


	/**
	 *  预约配送日期
	 * @param apiUrl
	 * @param licensekey
	 * @param secretkey
	 * @param shopUrl
	 * @param orderNumber 订单号25码，格式： [7位數 字]-[YYMMDD]-[10位數字]
	 * @param orderPackageId 包裹编号 GUID36码
	 * @param shippingDate 配送日期 2020-09-30T15:06:53.000Z
	 * @return
	 */	 
	public String ScheduleShippingDate(String apiUrl,String licensekey,String secretkey,String shopUrl,String orderNumber,String orderPackageId, String shippingDate)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="orders/";
		}
		else			
		{
			apiUrl+="/orders/";
		}

		String resbody="";

		try 
		{
			//EncryptUtils eu = EncryptUtils.getInstance();	
			//String Authorization=eu.encodeBASE64(licensekey+":"+secretkey);

			String Authorization="ESA " + secretkey;

			JSONArray ja=new JSONArray();
			
			JSONObject header = new JSONObject();	

			header.put("op", "replace");
			header.put("path", "/orders/tw/"+shopUrl+"/"+orderNumber+"/shipping/"+orderPackageId);//
			
			JSONObject value = new JSONObject();	
			value.put("scheduleShippingDate", shippingDate);		
			
			header.put("value", value);
			
			/*
			//
			JSONObject shopKey = new JSONObject();	
			shopKey.put("marketplaceIdentifier", "tw");//台湾乐天
			shopKey.put("shopUrl", shopUrl);//店家URL

			//
			JSONObject shipping = new JSONObject();	
			shipping.put("orderPackageId", orderPackageId);//包裹编号
			shipping.put("shippingDate", shippingDate);//配送日期

			//
			value.put("shopKey", shopKey);
			value.put("shipping", shipping);
			value.put("orderNumber", orderNumber);

			//
			header.put("value", value);
            */
			
			ja.put(header);
			//
			String request=ja.toString();

			resbody=HttpSend.SendRakuten("ScheduleShippingDate", request, apiUrl, Authorization, "PATCH");
			JSONObject jsonres = new JSONObject(resbody);

			if (jsonres.has("errors"))
			{
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
				}				
			}
			else 
			{
				String operationId=jsonres.getString("operationId");//UUID
				//System.out.println(operationId);	

				//操作狀態
				//CANCELLED 請求已取消 
				//DONE 請求已處理 
				//INCOMPLETE 請求不完整並且終止，請聯繫台灣樂天市場支援中心獲取更多資訊 
				//INPROGRESS 請求已在處理中
				//SUBMITTED 請求已收到，但尚未開始處理
				String operationStatus=jsonres.getString("operationStatus");
				//System.out.println(operationStatus);	

				//操作類型
				//可能的值: POST_PRODUCTS PUT_PRODUCTS PATCH_PRODUCTS DELETE_PRODUCTS POST_SHOP_CATEGORY PUT_SHOP_CATEGORY DELETE_SHOP_CATEGORY PATCH_ORDERS PATCH_INVENTORIES PATCH_ORDER_REQUESTS
				String operationType=jsonres.getString("operationType");
				//System.out.println(operationType);	

				//請求送出的日期和時間  格式：'YYYY-MMDDThh:mm:ss' 
				String submittedDate=jsonres.getString("submittedDate");
				//System.out.println(submittedDate);	

				String successCount=jsonres.getString("successCount");//选填
				//System.out.println(successCount);	

				String failureCount=jsonres.getString("failureCount");//选填
				//System.out.println(failureCount);	
			}

		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());	

			return "";
		}

		return resbody;

	}



	/**
	 * 发货，Update Shipment Status
	 * @param apiUrl
	 * @param licensekey
	 * @param secretkey
	 * @param shopUrl
	 * @param orderNumber 订单号25码，格式： [7位數 字]-[YYMMDD]-[10位數字]
	 * @param orderPackageId 包裹编号 GUID36码
	 * @param ShippingStatus 配送状态 
	 * NotShipped 訂單尚未配送
	 * AwaitingScheduling 訂單等待預約，尚未配送 
	 * SchedulingInProgress 訂單預約處理中，尚未配送. 
	 * Scheduled 訂單已預約完成，但尚未配送
	 * Rejected 訂單已拒絕，尚未配送 
	 * Shipping 訂單正在配送，訂單狀態為AwaitingPayment或 AwaitingCompletion 
	 * Missing 訂單遺失 
	 * AwaitingPickup 訂單已配送且正在等待領取 
	 * Shipped 訂單已配送
	 * Returned 訂單退回
	 * @return
	 */	
	public String ConfirmShipping(String apiUrl,String licensekey,String secretkey,String shopUrl,String orderNumber,String orderPackageId,String ShippingStatus)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="orders/";
		}
		else			
		{
			apiUrl+="/orders/";
		}

		String resbody="";

		try 
		{
			//EncryptUtils eu = EncryptUtils.getInstance();	
			//String Authorization=eu.encodeBASE64(licensekey+":"+secretkey);

			String Authorization="ESA " + secretkey;

			JSONArray ja=new JSONArray();

			JSONObject header = new JSONObject();	

			header.put("op", "replace");
			header.put("path", "/orders/tw/"+shopUrl+"/"+orderNumber+"/shipping/"+orderPackageId+"/shippingstatus");//
			header.put("value", ShippingStatus);

			/*
			JSONObject value = new JSONObject();	

			//
			JSONObject shopKey = new JSONObject();	
			shopKey.put("marketplaceIdentifier", "tw");//台湾乐天
			shopKey.put("shopUrl", shopUrl);//店家URL

			//
			JSONObject shipping = new JSONObject();	
			shipping.put("orderPackageId", orderPackageId);//包裹编号

			//			
			value.put("shipping", shipping);
			value.put("orderNumber", orderNumber);
			value.put("shopKey", shopKey);
			//
			header.put("value", value);
			 */

			ja.put(header);

			//
			String request=ja.toString();

			resbody=HttpSend.SendRakuten("ConfirmShipping", request, apiUrl, Authorization, "PATCH");
			JSONObject jsonres = new JSONObject(resbody);

			if (jsonres.has("errors"))
			{
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
				}				
			}
			else 
			{
				String operationId=jsonres.getString("operationId");//UUID
				//System.out.println(operationId);	

				//操作狀態
				//CANCELLED 請求已取消 
				//DONE 請求已處理 
				//INCOMPLETE 請求不完整並且終止，請聯繫台灣樂天市場支援中心獲取更多資訊 
				//INPROGRESS 請求已在處理中
				//SUBMITTED 請求已收到，但尚未開始處理
				String operationStatus=jsonres.getString("operationStatus");
				//System.out.println(operationStatus);	

				//操作類型
				//可能的值: POST_PRODUCTS PUT_PRODUCTS PATCH_PRODUCTS DELETE_PRODUCTS POST_SHOP_CATEGORY PUT_SHOP_CATEGORY DELETE_SHOP_CATEGORY PATCH_ORDERS PATCH_INVENTORIES PATCH_ORDER_REQUESTS
				String operationType=jsonres.getString("operationType");
				//System.out.println(operationType);	

				//請求送出的日期和時間  格式：'YYYY-MMDDThh:mm:ss' 
				String submittedDate=jsonres.getString("submittedDate");
				//System.out.println(submittedDate);	

				if (jsonres.has("successCount")) 
				{
					String successCount=jsonres.getString("successCount");//选填
					//System.out.println(successCount);	
				}

				if (jsonres.has("failureCount")) 
				{
					String failureCount=jsonres.getString("failureCount");//选填
					//System.out.println(failureCount);	
				}
			}

		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());	

			return "";
		}

		return resbody;

	}



	/**
	 * 更新包裹追踪编号
	 * @param apiUrl
	 * @param licensekey
	 * @param secretkey
	 * @param shopUrl
	 * @param orderNumber
	 * @param orderPackageId
	 * @param carrierName 物流公司
	 * 黑貓宅急便、7-11門市 取貨、全家門市取貨
	 * 新竹物流,中華郵政, 嘉里大榮,統一速達,宅配通 
	 * @param trackingnumber 物流单号
	 * @return
	 */
	public String UpdateTracknumber(String apiUrl,String licensekey,String secretkey,String shopUrl,String orderNumber,String orderPackageId, String carrierName, String trackingnumber)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="orders/";
		}
		else			
		{
			apiUrl+="/orders/";
		}

		String resbody="";

		try 
		{
			//EncryptUtils eu = EncryptUtils.getInstance();	
			//String Authorization=eu.encodeBASE64(licensekey+":"+secretkey);

			String Authorization="ESA " + secretkey;

			JSONArray ja=new JSONArray();

			JSONObject header = new JSONObject();	

			header.put("op", "replace");
			header.put("path", "/orders/tw/"+shopUrl+"/"+orderNumber+"/shipping/"+orderPackageId+"/trackingnumber");//

			JSONObject value = new JSONObject();	
			value.put("carrierName", carrierName);
			value.put("trackingNumber", trackingnumber);

			header.put("value", value);
			/*
			JSONObject value = new JSONObject();	

			//
			JSONObject shopKey = new JSONObject();	
			shopKey.put("marketplaceIdentifier", "tw");//台湾乐天
			shopKey.put("shopUrl", shopUrl);//店家URL

			//
			JSONObject shipping = new JSONObject();	
			shipping.put("orderPackageId", orderPackageId);//包裹编号
			shipping.put("trackingnumber", trackingnumber);//物流单号

			//
			value.put("shopKey", shopKey);
			value.put("shipping", shipping);
			value.put("orderNumber", orderNumber);

			//
			header.put("value", value);
			 */

			ja.put(header);

			//
			String request=ja.toString();

			resbody=HttpSend.SendRakuten("UpdateTracknumber", request, apiUrl, Authorization, "PATCH");
			JSONObject jsonres = new JSONObject(resbody);

			if (jsonres.has("errors"))
			{
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
				}				
			}
			else 
			{
				String operationId=jsonres.getString("operationId");//UUID
				//System.out.println(operationId);	

				//操作狀態
				//CANCELLED 請求已取消 
				//DONE 請求已處理 
				//INCOMPLETE 請求不完整並且終止，請聯繫台灣樂天市場支援中心獲取更多資訊 
				//INPROGRESS 請求已在處理中
				//SUBMITTED 請求已收到，但尚未開始處理
				String operationStatus=jsonres.getString("operationStatus");
				//System.out.println(operationStatus);	

				//操作類型
				//可能的值: POST_PRODUCTS PUT_PRODUCTS PATCH_PRODUCTS DELETE_PRODUCTS POST_SHOP_CATEGORY PUT_SHOP_CATEGORY DELETE_SHOP_CATEGORY PATCH_ORDERS PATCH_INVENTORIES PATCH_ORDER_REQUESTS
				String operationType=jsonres.getString("operationType");
				//System.out.println(operationType);	

				//請求送出的日期和時間  格式：'YYYY-MMDDThh:mm:ss' 
				String submittedDate=jsonres.getString("submittedDate");
				//System.out.println(submittedDate);	

				if (jsonres.has("successCount")) 
				{
					String successCount=jsonres.getString("successCount");//选填
					//System.out.println(successCount);	
				}

				if (jsonres.has("failureCount")) 
				{
					String failureCount=jsonres.getString("failureCount");//选填
					//System.out.println(failureCount);	
				}
			}

		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());	

			return "";
		}

		return resbody;

	}



	/**
	 * 取消订单，Cancel an Order
	 * @param apiUrl
	 * @param licensekey
	 * @param secretkey
	 * @param shopUrl
	 * @param orderNumber 订单号25码，格式： [7位數 字]-[YYMMDD]-[10位數字]
	 * @param cancelReason 取消原因代码
	 * 101 顧客-詐騙付款
	 * 102 顧客-預期不會付款
	 * 103 顧客-認證錯誤
	 * 104 顧客-未收到商品
	 * 105 顧客-退換
	 * 106 顧客 -調整訂單商品項 目
	 * 301 店家-無庫存
	 * 302 店家-無法配送
	 * 303 店家-退換貨
	 * 401 其他-貨運業者無法配送
	 * 402 其他-付款方式改變
	 * 403 其他-訂單合併 
	 * 404 其他-系統錯誤,因為系統錯誤，訂單必需取消
	 * @return
	 */
	public String CancelOrder(String apiUrl,String licensekey,String secretkey,String shopUrl,String orderNumber,String cancelReason)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="orders";
		}
		else			
		{
			apiUrl+="/orders";
		}

		String resbody="";

		try 
		{
			//EncryptUtils eu = EncryptUtils.getInstance();	
			//String Authorization=eu.encodeBASE64(licensekey+":"+secretkey);

			String Authorization="ESA " + secretkey;

			JSONObject header = new JSONObject();	

			header.put("op", "replace");

			JSONObject value = new JSONObject();	

			//
			JSONObject shopKey = new JSONObject();	
			shopKey.put("marketplaceIdentifier", "tw");//台湾乐天
			shopKey.put("shopUrl", shopUrl);//店家URL

			//
			value.put("shopKey", shopKey);
			value.put("orderNumber", orderNumber);
			value.put("cancelReason", cancelReason);

			//
			header.put("value", value);

			//
			String request=header.toString();

			resbody=HttpSend.SendRakuten("CancelOrder", request, apiUrl, Authorization, "PATCH");
			JSONObject jsonres = new JSONObject(resbody);

			if (jsonres.has("errors"))
			{
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
				}				
			}
			else 
			{
				String operationId=jsonres.getString("operationId");//UUID
				//System.out.println(operationId);	

				//操作狀態
				//CANCELLED 請求已取消 
				//DONE 請求已處理 
				//INCOMPLETE 請求不完整並且終止，請聯繫台灣樂天市場支援中心獲取更多資訊 
				//INPROGRESS 請求已在處理中
				//SUBMITTED 請求已收到，但尚未開始處理
				String operationStatus=jsonres.getString("operationStatus");
				//System.out.println(operationStatus);	

				//操作類型
				//可能的值: POST_PRODUCTS PUT_PRODUCTS PATCH_PRODUCTS DELETE_PRODUCTS POST_SHOP_CATEGORY PUT_SHOP_CATEGORY DELETE_SHOP_CATEGORY PATCH_ORDERS PATCH_INVENTORIES PATCH_ORDER_REQUESTS
				String operationType=jsonres.getString("operationType");
				//System.out.println(operationType);	

				//請求送出的日期和時間  格式：'YYYY-MMDDThh:mm:ss' 
				String submittedDate=jsonres.getString("submittedDate");
				//System.out.println(submittedDate);	

				if (jsonres.has("successCount")) 
				{
					int successCount=jsonres.getInt("successCount");//选填
					//System.out.println(successCount);	
				}
				
				if (jsonres.has("failureCount")) 
				{
					int failureCount=jsonres.getInt("failureCount");//选填
					//System.out.println(failureCount);	
				}

				
			}

		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());	

			return "";
		}

		return resbody;

	}


	/**
	 * 
	 * @param apiUrl
	 * @param licensekey
	 * @param secretkey
	 * @param shopUrl
	 * @param orderNumber
	 * @param orderPackageId
	 * @param name  收件人姓名
	 * @param phoneNumber 收件人电话
	 * @param postalCode 邮编 可空
	 * @param countryCode TW
	 * @param stateCode 省份代码 可空
	 * @param stateName 省份名称 可空
	 * @param cityCode 城市代码 可空
	 * @param cityName 城市名称 可空
	 * @param address1 详细地址
	 * @return
	 */
	public String ChangeAddress(String apiUrl,String licensekey,String secretkey,String shopUrl,String orderNumber,String orderPackageId,String name,String phoneNumber, String postalCode,String countryCode,String stateCode,String stateName, String cityCode,String cityName, String address1)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="orders";
		}
		else			
		{
			apiUrl+="/orders";
		}

		String resbody="";

		try 
		{
			//EncryptUtils eu = EncryptUtils.getInstance();	
			//String Authorization=eu.encodeBASE64(licensekey+":"+secretkey);

			String Authorization="ESA " + secretkey;

			JSONObject header = new JSONObject();	

			header.put("op", "replace");

			JSONObject value = new JSONObject();	

			//
			JSONObject shopKey = new JSONObject();	
			shopKey.put("marketplaceIdentifier", "tw");//台湾乐天
			shopKey.put("shopUrl", shopUrl);//店家URL


			//
			header.put("value", value);

			//deliveryAddress
			JSONObject shippingAddress = new JSONObject();	
			shippingAddress.put("name", name);//
			shippingAddress.put("phoneNumber", phoneNumber);//			
			shippingAddress.put("countryCode", countryCode);//
			if (postalCode.equals("")==false) 
			{
				shippingAddress.put("postalCode", postalCode);//
			}
			if (stateCode.equals("")==false) 
			{
				shippingAddress.put("stateCode", stateCode);//
			}
			if (stateName.equals("")==false) 
			{
				shippingAddress.put("stateName", stateName);//
			}

			if (cityCode.equals("")==false) 
			{
				shippingAddress.put("cityCode", cityCode);//
			}

			if (cityName.equals("")==false) 
			{
				shippingAddress.put("cityName", cityName);//
			}

			shippingAddress.put("address1", address1);//

			//shipping
			JSONObject shipping = new JSONObject();	
			shipping.put("orderPackageId", orderPackageId);
			shipping.put("shippingAddress", shippingAddress);//

			//
			value.put("shopKey", shopKey);
			value.put("orderNumber", orderNumber);
			value.put("shipping", shipping);


			//
			String request=header.toString();

			resbody=HttpSend.SendRakuten("ChangeAddress", request, apiUrl, Authorization, "PATCH");
			JSONObject jsonres = new JSONObject(resbody);

			if (jsonres.has("errors"))
			{
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
				}				
			}
			else 
			{
				String operationId=jsonres.getString("operationId");//UUID
				//System.out.println(operationId);	

				//操作狀態
				//CANCELLED 請求已取消 
				//DONE 請求已處理 
				//INCOMPLETE 請求不完整並且終止，請聯繫台灣樂天市場支援中心獲取更多資訊 
				//INPROGRESS 請求已在處理中
				//SUBMITTED 請求已收到，但尚未開始處理
				String operationStatus=jsonres.getString("operationStatus");
				//System.out.println(operationStatus);	

				//操作類型
				//可能的值: POST_PRODUCTS PUT_PRODUCTS PATCH_PRODUCTS DELETE_PRODUCTS POST_SHOP_CATEGORY PUT_SHOP_CATEGORY DELETE_SHOP_CATEGORY PATCH_ORDERS PATCH_INVENTORIES PATCH_ORDER_REQUESTS
				String operationType=jsonres.getString("operationType");
				//System.out.println(operationType);	

				//請求送出的日期和時間  格式：'YYYY-MMDDThh:mm:ss' 
				String submittedDate=jsonres.getString("submittedDate");
				//System.out.println(submittedDate);	

				String successCount=jsonres.getString("successCount");//选填
				//System.out.println(successCount);	

				String failureCount=jsonres.getString("failureCount");//选填
				//System.out.println(failureCount);	
			}


		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());	

			return "";
		}

		return resbody;

	}




	/**
	 * 操作结果查询
	 * @param apiUrl
	 * @param licensekey
	 * @param secretkey
	 * @param operationId
	 * @return
	 */
	public String OperationResponse(String apiUrl,String licensekey,String secretkey,String operationId)
	{
		//https://openapi-rms.global.rakuten.com/2.0/operations?operationId=b38f8287-1d67-4116-8ea3-e46b54ef779a

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="operations?operationId=" +operationId;
		}
		else			
		{
			apiUrl+="/operations?operationId=" +operationId;
		}

		String resbody="";

		try 
		{
			//EncryptUtils eu = EncryptUtils.getInstance();	
			//String Authorization=eu.encodeBASE64(licensekey+":"+secretkey);

			String Authorization="ESA " + secretkey;

			//
			String request="";

			resbody=HttpSend.SendRakuten("OperationResponse", request, apiUrl, Authorization, "GET");
			JSONObject jsonres = new JSONObject(resbody);

			if (jsonres.has("operations")) 
			{
				JSONArray operations=jsonres.getJSONArray("operations");
				for(int ri=0;ri<operations.length();ri++)
				{				
					String soperationId=operations.getJSONObject(ri).getString("operationId");//
					//System.out.println(soperationId);

					String soperationType=operations.getJSONObject(ri).getString("operationType");//
					//System.out.println(soperationType);

					String soperationStatus=operations.getJSONObject(ri).getString("operationStatus");//
					//System.out.println(soperationStatus);

					String ssubmittedDate=operations.getJSONObject(ri).getString("submittedDate");//
					//System.out.println(ssubmittedDate);

					String scompletedDate=operations.getJSONObject(ri).getString("completedDate");//
					//System.out.println(scompletedDate);

					int ssubmittedCount=operations.getJSONObject(ri).getInt("submittedCount");//
					//System.out.println(ssubmittedCount);

					int ssuccessCount=operations.getJSONObject(ri).getInt("successCount");//
					//System.out.println(ssuccessCount);

					int sfailureCount=operations.getJSONObject(ri).getInt("failureCount");//
					//System.out.println(sfailureCount);

					if (operations.getJSONObject(ri).has("errors"))
					{
						JSONObject jsonErr = operations.getJSONObject(ri).getJSONObject("errors");

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
						}				
					}

				}
			}
			else 
			{

			}
		} 
		catch (Exception e) 
		{
			//System.out.println(e.getMessage());	

			return "";
		}

		return resbody;

	}











}
