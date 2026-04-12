package com.dsc.spos.utils.ec;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;

//电商-----91APP
public class NineOneApp 
{


	/**
	 * 门店支持的物流托运商
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @return
	 */
	public String ShopGetShipping(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId)
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V1/Shop/GetShipping";
		}
		else			
		{
			apiUrl+="/V1/Shop/GetShipping";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("Id", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 

			//
			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("ShopGetShipping", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//
				JSONArray Data = jsonres.getJSONArray("Data");
				for(int i=0;i<Data.length();i++)
				{
					String Id=Data.getJSONObject(i).getString("Id");//物流方式编码
					String TypeName=Data.getJSONObject(i).getString("TypeName");//物流方式名称

					//System.out.println(Id+":"+TypeName);
				}
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;		
	}


	/**
	 * 查询订单单头
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param StartDateTime 开始时间
	 * @param EndDatetime 结束时间
	 * @param index 记录开始位置
	 * @return
	 */
	public String GetSalesOrder(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String StartDateTime,String EndDatetime, int index)
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/SalesOrder/GetList";
		}
		else			
		{
			apiUrl+="/V2/SalesOrder/GetList";
		}

		String resbody=""; 

		try
		{
			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 
			//订单配送方式 -全部 : 空白，請填入""  -宅配(含離島宅配) : Home -超商取貨付款 : StoreCashOnDelivery -付款後超商取貨 : StorePickup 
			//-付款後門市自取 : LocationPickup -貨到付款：CashOnDelivery 
			header.put("OrderDeliverType", "");
			//通路商 -全部 : 空白，請填入""  -全家 :Family -7-11:SevenEleven 
			header.put("DistributorDef", "");
			//溫層類別 -全部 : 空白，請填入"" -常溫 :Normal -冷藏 :Refrigerator -冷凍 :Freezer 
			header.put("TemperatureTypeDef", "");
			//查詢根據日期 
			//-訂單轉單日 : OrderDateTime 
			//-訂單預計出貨日 : OrderExpectShippingDate 
			//-訂單狀態日 : OrderStatusUpdatedDateTime (訂單被異動狀態的日期) 
			header.put("OrderDateType", "OrderDateTime");
			//起始日期带时间2014-06-16T00:00:00
			header.put("StartDate", StartDateTime);
			//結束日期带时间
			header.put("EndDate", EndDatetime);
			//訂單狀態 
			//- 全部: 空白，請填入”“ 
			//- 已成立: WaitingToShipping 
			//- 已確認待出貨: ConfirmedToShipping 
			//- 已完成: Finish 
			//- 已取消: Cancel 
			//- 付款確認中: WaitingToCreditCheck 
			//- 待付款: WaitingToPay 
			header.put("OrderStatus", "ConfirmedToShipping");
			//位置 
			//-第一筆資料的位置為 0，第二筆的資料位置為 1，以此類推 
			header.put("Position", index);
			//取回資料筆數  
			//-單次查詢最多 100 筆訂單清單 
			header.put("Count", 50);
			//出貨單狀態 
			//-全部 : 空白，請填入"" -NotYetAllocatedCode : 尚未配號 -Finish : 已出貨至消費者 
			//**超商付款取貨 / 超商純取貨專用貨態 
			//-AllocatedCode : 已配號 -VerifySuccess : 超商驗收成功 -VerifyFailLost : 超商驗收失敗.商品遺失 -VerifyFailAbnormalPackage :超商 驗收失敗.包裝異常   -VerifyFailRenovation : 超商驗收失敗.門市閉店/整修 -VerifyFailErrorCode : 超商驗收失敗.配送編號異常 -VerifyFailInvalIdCode : 超商驗收失敗.編號失效(未到貨) -ShippingProcessing : 出貨處理中 -ShippingFail : 消費者逾期未取，出貨失敗(逾期未取) -ShippingArrived : 貨到門市 
			//**門市自取專用貨態 
			//-AllocatedCode : 已配號 -ShippingProcessing : 出貨處理中 -ShippingFail : 消費者逾期未取，出貨失敗(逾期未取) -ShippingArrived : 貨到門市
			//**貨到付款專用貨態 CashOnDeliveryTransferring：宅配轉運中 CashOnDeliveryNotAtHome：宅配不在家 CashOnDeliveryDistributing：宅配已配送 CashOnDeliveryFailDamage：宅配異常-損壞 CashOnDeliveryFailLost：宅配異常-遺失 CashOnDeliveryFail：宅配出貨失敗 
			//CashOnDeliveryAddressError：宅配地址錯誤 CashOnDeliveryForwarding：宅配轉寄配送中 
			header.put("ShippingOrderStatus", "");

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("GetSalesOrder", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//				
				JSONObject data = jsonres.getJSONObject("Data");
				//总记录数
				int TotalCount= data.getInt("TotalCount");

				JSONArray List =data.getJSONArray("List");
				for(int i=0;i<List.length();i++)
				{
					String TMCode=List.getJSONObject(i).getString("TMCode");//主單編號
					String TSCode=List.getJSONObject(i).getString("TSCode");//訂單編號 

					//System.out.println(TMCode+":"+TSCode);
				}
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;		

	}


	/**
	 * 查询订单明细
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param TMCode 主单编号
	 * @param TSCode 订单编号
	 * @return
	 */
	public String GetSalesOrderDetail(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String TMCode,String TSCode)
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/SalesOrder/Get";
		}
		else			
		{
			apiUrl+="/V2/SalesOrder/Get";
		}

		String resbody=""; 

		try
		{

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 
			header.put("TMCode", TMCode);//主单编号
			header.put("TSCode", TSCode);//订单编号

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("GetSalesOrderDetail", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//				
				JSONObject data = jsonres.getJSONObject("Data");
				//总记录数
				//int TotalCount= data.getInt("TotalCount");

				JSONArray List =data.getJSONArray("List");
				for(int i=0;i<List.length();i++)
				{
					String dTMCode=List.getJSONObject(i).getString("TMCode");//主單編號
					String dTSCode=List.getJSONObject(i).getString("TSCode");//訂單編號 
					//付款方式 -信用卡一次付款：CreditCardOnce -信用卡分期付款：CreditCardInstallment 
					//-超商取貨付款：StorePay -ATM 付款：ATM -貨到付款：CashOnDelivery - LINE Pay：LinePay
					String OrderPayType=List.getJSONObject(i).getString("OrderPayType");// 
					long OrderShippingTypeId=List.getJSONObject(i).getLong("OrderShippingTypeId");//物流商编码 
					//配送方式 -宅配(含離島宅配) :Home -超商取貨付款 : StoreCashOnDelivery -付款後超商取貨 :StorePickup -付款後門市自取 : LocationPickup 
					//-貨到付款：CashOnDelivery -海外宅配：Oversea 
					String OrderDeliverType=List.getJSONObject(i).getString("OrderDeliverType");// 
					//通路商 -全家：Family -7-11：SevenEleven 
					String DistributorDef="";// 
					if (List.getJSONObject(i).isNull("DistributorDef")==false) 
					{
						DistributorDef=List.getJSONObject(i).getString("DistributorDef");// 
					}
					//溫層類別 -常溫：Normal -冷藏：Refrigerator -冷凍：Freezer
					String TemperatureTypeDef=List.getJSONObject(i).getString("TemperatureTypeDef");// 
					String TMShippingFee=List.getJSONObject(i).getString("TMShippingFee");//主單(TM)運費 
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
					long VipMemberId=List.getJSONObject(i).getLong("VipMemberId");//91APP VIP 會員序號  
					//客戶會員編號 (貴公司的會員編號) 如未使用會員模組，此欄位會回覆 null 
					String OuterMemberCode="";
					if (List.getJSONObject(i).isNull("OuterMemberCode")==false) 
					{
						OuterMemberCode=List.getJSONObject(i).getString("OuterMemberCode");// 
					}
					String OrderReceiverName=List.getJSONObject(i).getString("OrderReceiverName");// 收件人姓名 
					String OrderReceiverMobile=List.getJSONObject(i).getString("OrderReceiverMobile");// 收件人電話 
					String OrderReceiverZipCode=List.getJSONObject(i).getString("OrderReceiverZipCode");//收件人郵遞區號 
					String OrderReceiverCity=List.getJSONObject(i).getString("OrderReceiverCity");// 收件人縣市 
					String OrderReceiverDistrict=List.getJSONObject(i).getString("OrderReceiverDistrict");// 收件人鄉鎮市區 
					String OrderReceiverAddress=List.getJSONObject(i).getString("OrderReceiverAddress");// 收件人地址 
					String OrderReceiverStoreId=List.getJSONObject(i).getString("OrderReceiverStoreId");// 超商取貨的門市編號 
					String OrderReceiverStoreName=List.getJSONObject(i).getString("OrderReceiverStoreName");// 超商取貨的門市名稱 
					String ShippingOrderCode=List.getJSONObject(i).getString("ShippingOrderCode");//貨運單配送編號  
					//消費者備註 ※請注意：商店須設定使用訂單備註，訂購流 程中才會顯示消費者填寫的欄位 
					String OrderMemo="";
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
					double Price=List.getJSONObject(i).getDouble("Price");// 商品單價 
					int Qty=List.getJSONObject(i).getInt("Qty");//商品數量 
					double TotalPrice=List.getJSONObject(i).getDouble("TotalPrice");// 商品總金額(單價*數量) 
					double TotalDiscount=List.getJSONObject(i).getDouble("TotalDiscount");// 訂單總折扣金額 
					double TotalPayment=List.getJSONObject(i).getDouble("TotalPayment");// 訂單實際付款金額 
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
				}
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}


	/**
	 * 超取订单取得配送编号==产生物流单号
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param TMCode 主单编号 
	 * @param TSCodeList 订单编号列表 []为全部
	 * @return
	 */
	public String StoreShipping(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String TMCode,String[] TSCodeList) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/Store/Shipping";
		}
		else			
		{
			apiUrl+="/V2/Store/Shipping";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("TMCode", TMCode);
			header.put("TSCodeList", TSCodeList);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils(); 
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("StoreShipping", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//				
				JSONObject data = jsonres.getJSONObject("Data");
				String sTMCode=data.getString("TMCode");
				JSONArray sTSCodeList=data.getJSONArray("TSCodeList");//
				for(int a=0;a<sTSCodeList.length();a++)
				{
					String ckeyName=sTSCodeList.getString(a);
					//System.out.println("订单编号:"+ckeyName);
				}

				//配送方式。  超商取貨付款 : StoreCashOnDelivery  	付款後超商取貨 : StorePickup 
				String OrderDeliverType=data.getString("OrderDeliverType");
				//通路商 -全家 :Family	-7-11 :SevenEleven 
				String DistributorDef=data.getString("DistributorDef");
				//溫層類別 	-常溫 :Normal 	-冷藏 :Refrigerator -冷凍 :Freezer 
				String TemperatureTypeDef=data.getString("TemperatureTypeDef");
				//貨運單配送編號 
				String ShippingOrderCode=data.getString("ShippingOrderCode");
				//貨到物流中心日2013-11-04T17:30:00 
				String GoodsToLogisticCenterDate=data.getString("GoodsToLogisticCenterDate");
				//建議貨到期限 2013-11-14T17:30:00
				String SuggestGoodsArrivalDate=data.getString("SuggestGoodsArrivalDate");
				//出貨單狀態 -AllocatedCode : 已配號 
				String ShippingOrderStatus=data.getString("ShippingOrderStatus");
				//出貨狀態最後更新日 
				String ShippingOrderStatusUpdatedDateTime=data.getString("ShippingOrderStatusUpdatedDateTime");
				//收件人姓名
				String OrderReceiverName=data.getString("OrderReceiverName");
				//收件人電話 
				String OrderReceiverMobile=data.getString("OrderReceiverMobile");
				//收件人郵遞區號 
				String OrderReceiverZipCode=data.getString("OrderReceiverZipCode");
				//收件人縣市 
				String OrderReceiverCity=data.getString("OrderReceiverCity");
				//收件人鄉鎮市區 
				String OrderReceiverDistrict=data.getString("OrderReceiverDistrict");
				//收件人地址 *若為超商取貨的訂單，則為地址為超商 門市的地址 
				String OrderReceiverAddress=data.getString("OrderReceiverAddress");
				//超商取貨的門市編號 
				String OrderReceiverStoreId=data.getString("OrderReceiverStoreId");
				//超商取貨的門市名稱 
				String OrderReceiverStoreName=data.getString("OrderReceiverStoreName");
				//超商代收金額 
				int StorePaymentAmount=data.getInt("StorePaymentAmount");


			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;	
	}

	/**
	 * 取消超取订单配送编号==取消物流单号
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param ShippingOrderCode 超取配送编号
	 * @param OrderDeliverType 通路商  -超商取貨(全家) : Family  -超商取貨(7-11) : SevenEleven
	 * @return
	 */
	public String CancelStoreShipping(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String ShippingOrderCode,String OrderDeliverType) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V1/Order/CancelShippingOrderCode";
		}
		else			
		{
			apiUrl+="/V1/Order/CancelShippingOrderCode";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("ShippingOrderCode", ShippingOrderCode);
			header.put("OrderDeliverType", OrderDeliverType);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("CancelStoreShipping", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//失敗時回傳 null；成功時回傳 Success 				
				String data = jsonres.getString("Data");

				//System.out.println("取消配送编号:"+data);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}


	/**
	 * 超取订单确认出货
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param ShippingOrderCode 超取配送编号
	 * @param OrderDeliverType  通路商  -超商取貨(全家) : Family  -超商取貨(7-11) : SevenEleven
	 * @return
	 */
	public String ShippingOrderConfirm(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String ShippingOrderCode,String OrderDeliverType) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V1/Order/ShippingOrderConfirm";
		}
		else			
		{
			apiUrl+="/V1/Order/ShippingOrderConfirm";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("ShippingOrderCode", ShippingOrderCode);
			header.put("OrderDeliverType", OrderDeliverType);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("ShippingOrderConfirm", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//失敗時回傳 null；成功時回傳 Success 				
				String data = jsonres.getString("Data");

				//System.out.println("超商确认出货:"+data);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}


	/**
	 * 宅配订单确认出货
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param TMCode 主单编号
	 * @param TSCodeList 订单编号列表 []为全部
	 * @param ForwarderDef 配送商 -郵局 : 1 -黑貓 : 2 -宅配通：3 -新竹貨運：4 -嘉里大榮：5 -便利帶：6 -日通：7 -其他：8 -順豐：9 -郵局(國際)：10 -DHL：11 -Fedex：12 -優邦速運：13 (註：9~13 為海外宅配配送商) 
	 * @param ShippingOrderCode 配送編號 ==物流编号
	 * @param ShippingType  配送方式 - 宅配(含離島宅配): Home - 海外宅配: Oversea 
	 * @return
	 */
	public String DeliveryShipment(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String TMCode,String[] TSCodeList,int ForwarderDef,String ShippingOrderCode,String ShippingType) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V1/Order/DeliveryShipment";
		}
		else			
		{
			apiUrl+="/V1/Order/DeliveryShipment";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("TMCode", TMCode);
			header.put("TSCodeList", TSCodeList);
			header.put("ForwarderDef", ForwarderDef);
			header.put("ShippingOrderCode", ShippingOrderCode);
			header.put("ShippingType", ShippingType);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("DeliveryShipment", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//失敗時回傳 null；成功時回傳 Success 				
				JSONObject data = jsonres.getJSONObject("Data");
				String sTMCode=data.getString("TMCode");
				JSONArray sTSCodeList=data.getJSONArray("TSCodeList");//
				for(int a=0;a<sTSCodeList.length();a++)
				{
					String ckeyName=sTSCodeList.getString(a);
					//System.out.println("订单编号:"+ckeyName);
				}
				//貨運單配送編號 
				String sShippingOrderCode=data.getString("ShippingOrderCode");
				//建議貨到期限2013-11-14T17:30:00
				String SuggestGoodsArrivalDate=data.getString("SuggestGoodsArrivalDate");
				//出貨單狀態 Finish : 已出貨至消費者 
				String ShippingOrderStatus=data.getString("ShippingOrderStatus");
				//出貨狀態最後更新日 格式： yyyy/MM/dd HH:mm:ss 
				String ShippingOrderStatusUpdatedDateTime=data.getString("ShippingOrderStatusUpdatedDateTime");
				//收件人姓名 
				String OrderReceiverName=data.getString("OrderReceiverName");
				//收件人電話 
				String OrderReceiverMobile=data.getString("OrderReceiverMobile");
				//收件人地址
				String OrderReceiverAddress=data.getString("OrderReceiverAddress");
				//溫層類別 -常溫 :Normal -冷藏 :Refrigerator -冷凍 :Freezer 
				String TemperatureTypeDef=data.getString("TemperatureTypeDef");
				//收件人電話國碼 
				String OrderReceiverCountryCode=data.getString("OrderReceiverCountryCode");
				//收件國家/地區 
				String OrderReceiverCountry=data.getString("OrderReceiverCountry");

			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}



	/**
	 * 线下门店自取取得配送编号
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param TMCode 主单编号
	 * @param TSCodeList 订单编号列表 []为全部
	 * @param AvailablePickupDays 取货天数 ，貨到門市後系統計算{貨到門市日+取貨天數}作為消費者取貨截止日， 若为-1，系統會自動帶入預設值
	 * @return
	 */
	public String LocationPickupShipping(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String TMCode,String[] TSCodeList, int AvailablePickupDays) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/LocationPickup/Shipping";
		}
		else			
		{
			apiUrl+="/V2/LocationPickup/Shipping";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("TMCode", TMCode);
			header.put("TSCodeList", TSCodeList);
			if (AvailablePickupDays>=0)
			{
				header.put("AvailablePickupDays", AvailablePickupDays);
			}

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("LocationPickupShipping", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//				
				JSONObject data = jsonres.getJSONObject("Data");
				String sTMCode=data.getString("TMCode");
				JSONArray sTSCodeList=data.getJSONArray("TSCodeList");//
				for(int a=0;a<sTSCodeList.length();a++)
				{
					String ckeyName=sTSCodeList.getString(a);
					//System.out.println("订单编号:"+ckeyName);
				}

				//配送方式。  超商取貨付款 : StoreCashOnDelivery  	付款後超商取貨 : StorePickup 
				String OrderDeliverType=data.getString("OrderDeliverType");
				//溫層類別 	-常溫 :Normal 	-冷藏 :Refrigerator -冷凍 :Freezer 
				String TemperatureTypeDef=data.getString("TemperatureTypeDef");
				//貨運單配送編號 
				String ShippingOrderCode=data.getString("ShippingOrderCode");
				//出貨單狀態 -AllocatedCode : 已配號 
				String ShippingOrderStatus=data.getString("ShippingOrderStatus");
				//出貨狀態最後更新日 
				String ShippingOrderStatusUpdatedDateTime=data.getString("ShippingOrderStatusUpdatedDateTime");
				//收件人姓名
				String OrderReceiverName=data.getString("OrderReceiverName");
				//收件人電話 
				String OrderReceiverMobile=data.getString("OrderReceiverMobile");
				//收件人郵遞區號 
				String OrderReceiverZipCode=data.getString("OrderReceiverZipCode");
				//收件人縣市 
				String OrderReceiverCity=data.getString("OrderReceiverCity");
				//收件人鄉鎮市區 
				String OrderReceiverDistrict=data.getString("OrderReceiverDistrict");
				//門市自取取貨的門市地址 
				String OrderReceiverAddress=data.getString("OrderReceiverAddress");
				//門市自取的門市編號 
				String OrderReceiverStoreId=data.getString("OrderReceiverStoreId");
				//門市自取的門市名稱 
				String OrderReceiverStoreName=data.getString("OrderReceiverStoreName");
				//門市自取代收金額 
				int StorePaymentAmount=data.getInt("StorePaymentAmount");


			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;	
	}


	/**
	 * 门店自取订单确认出货
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param ShippingOrderCode 配送编号==物流编码
	 * @return
	 */
	public String LocationPickupShipConfirm(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String ShippingOrderCode) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/LocationPickup/ShipConfirm";
		}
		else			
		{
			apiUrl+="/V2/LocationPickup/ShipConfirm";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("ShippingOrderCode", ShippingOrderCode);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("LocationPickupShipConfirm", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//失敗時回傳 null；成功時回傳 Success 				
				String data = jsonres.getString("Data");

				//System.out.println("门店自取订单确认出货:"+data);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}


	/**
	 * 门店自取订单货到门店确认
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param ShippingOrderCode 配送编号==物流编码
	 * @return
	 */
	public String LocationPickupArrivedConfirm(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String ShippingOrderCode) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="/V2/LocationPickup/ArrivedConfirm";
		}
		else			
		{
			apiUrl+="/V2/LocationPickup/ArrivedConfirm";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("ShippingOrderCode", ShippingOrderCode);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("LocationPickupArrivedConfirm", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//失敗時回傳 null；成功時回傳 Success 				
				String data = jsonres.getString("Data");

				//System.out.println("门店自取订单货到门店确认:"+data);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}


	/**
	 * 门店自取订单取货确认
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param ShippingOrderCode 配送编号==物流编码
	 * @return
	 */
	public String LocationPickupPickupConfirm(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String ShippingOrderCode) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/LocationPickup/PickupConfirm";
		}
		else			
		{
			apiUrl+="/V2/LocationPickup/PickupConfirm";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("ShippingOrderCode", ShippingOrderCode);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("LocationPickupPickupConfirm", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//失敗時回傳 null；成功時回傳 Success 				
				String data = jsonres.getString("Data");

				//System.out.println("门店自取订单取货确认:"+data);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}



	/**
	 * 货到付款订单获取配送编号==目前只提供黑貓
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param TMCode 主单编号
	 * @param TSCodeList 订单编号列表 []为全部
	 * @param ForwarderDef 貨到付款物流商，目前只提供黑貓(TCat) 2 代表黑貓(TCat) 
	 * @param ExpectDeliveryArrivalDate 預計配達日 2017/12/1,可以为"" 
	 * @param SpecifiedTimePeriod 指定時段代碼  1：9~12 2：12~17 3：17~20  ,可以为""
	 * @return 
	 */
	public String CashOnDeliveryShipping(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String TMCode,String[] TSCodeList,int ForwarderDef,String ExpectDeliveryArrivalDate,String SpecifiedTimePeriod) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/CashOnDelivery/Shipping";
		}
		else			
		{
			apiUrl+="/V2/CashOnDelivery/Shipping";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("TMCode", TMCode);
			header.put("TSCodeList", TSCodeList);
			header.put("ForwarderDef", ForwarderDef);
			if (ExpectDeliveryArrivalDate.equals("")==false) 
			{
				header.put("ExpectDeliveryArrivalDate", ExpectDeliveryArrivalDate);
			}
			if (SpecifiedTimePeriod.equals("")==false) 
			{
				header.put("SpecifiedTimePeriod", SpecifiedTimePeriod);
			}

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("CashOnDeliveryShipping", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//失敗時回傳 null；成功時回傳 Success 				
				JSONObject data = jsonres.getJSONObject("Data");
				String sTMCode=data.getString("TMCode");
				JSONArray sTSCodeList=data.getJSONArray("TSCodeList");//
				for(int a=0;a<sTSCodeList.length();a++)
				{
					String ckeyName=sTSCodeList.getString(a);
					//System.out.println("订单编号:"+ckeyName);
				}
				//配送方式。  貨到付款 : CashOnDelivery 
				String OrderDeliverType=data.getString("OrderDeliverType");
				//溫層類別 -常溫 :Normal -冷藏 :Refrigerator -冷凍 :Freezer 
				String TemperatureTypeDef=data.getString("TemperatureTypeDef");
				//貨運單序號 
				long ShippingOrderId=data.getLong("ShippingOrderId");
				//貨運單配送編號 
				String ShippingOrderCode=data.getString("ShippingOrderCode");
				//出貨單狀態 -AllocatedCode : 已配號 
				String ShippingOrderStatus=data.getString("ShippingOrderStatus");
				//出貨狀態最後更新日 2013-11-11T17:30:00
				String ShippingOrderStatusUpdatedDateTime=data.getString("ShippingOrderStatusUpdatedDateTime");
				//收貨日 2017-07-20T14:32:09 
				String ShippingOrderSlaveDateTime=data.getString("ShippingOrderSlaveDateTime");
				//收件人姓名 
				String OrderReceiverName=data.getString("OrderReceiverName");
				//收件人電話 
				String OrderReceiverMobile=data.getString("OrderReceiverMobile");
				//收件人郵遞區號 
				String OrderReceiverZipCode=data.getString("OrderReceiverZipCode");
				//收件人縣市 
				String OrderReceiverCity=data.getString("OrderReceiverCity");
				//收件人鄉鎮市區 
				String OrderReceiverDistrict=data.getString("OrderReceiverDistrict");
				//收件人地址
				String OrderReceiverAddress=data.getString("OrderReceiverAddress");

				//寄件人 
				String SenderShopName=data.getString("SenderShopName");
				//寄件人電話 
				String SenderShopPhone=data.getString("SenderShopPhone");
				//寄件人郵遞區號 
				String SenderShopZipCode=data.getString("SenderShopZipCode");
				//寄件人縣市 
				String SenderShopCity=data.getString("SenderShopCity");
				//寄件人鄉鎮市區 
				String SenderShopDistrict=data.getString("SenderShopDistrict");
				//寄件人地址 
				String SenderShopAddress=data.getString("SenderShopAddress");				

				//代收貨款 
				double PaymentAmount=data.getDouble("PaymentAmount");
				//契客代號 
				String CustomerId=data.getString("CustomerId");
				//黑貓 Egs 7 碼 
				String TCatEgs7=data.getString("TCatEgs7");
				//預計配達日 2017/12/1 
				String sExpectDeliveryArrivalDate=data.getString("ExpectDeliveryArrivalDate");
				//指定時段代碼 1：9~12  2：12~17 	3：17~20
				String sSpecifiedTimePeriod=data.getString("SpecifiedTimePeriod");

			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}


	/**
	 * 货到付款订单取消配送编号==目前只提供黑貓
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param ShippingOrderCode 配送编号
	 * @param ForwarderDef 貨到付款物流商，目前只提供黑貓(TCat) 2 代表黑貓(TCat) 
	 * @return
	 */
	public String CashOnDeliveryCancelShipping(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String ShippingOrderCode,int ForwarderDef) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/CashOnDelivery/CancelShipping";
		}
		else			
		{
			apiUrl+="/V2/CashOnDelivery/CancelShipping";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("ShippingOrderCode", ShippingOrderCode);
			header.put("ForwarderDef", ForwarderDef);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("CashOnDeliveryCancelShipping", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//失敗時回傳 null；成功時回傳 Success 				
				String data = jsonres.getString("Data");

				//System.out.println("货到付款订单取消配送编号:"+data);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}



	/**
	 * 货到付款订单确认出货
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param ShippingOrderCode 配送编号
	 * @param ForwarderDef 貨到付款物流商，目前只提供黑貓(TCat) 2 代表黑貓(TCat) 
	 * @return
	 */
	public String CashOnDeliveryShipConfirm(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String ShippingOrderCode,int ForwarderDef) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/CashOnDelivery/ShipConfirm";
		}
		else			
		{
			apiUrl+="/V2/CashOnDelivery/ShipConfirm";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("ShippingOrderCode", ShippingOrderCode);
			header.put("ForwarderDef", ForwarderDef);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("CashOnDeliveryShipConfirm", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//失敗時回傳 null；成功時回傳 Success 				
				String data = jsonres.getString("Data");

				//System.out.println("货到付款订单确认出货:"+data);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}


	/**
	 * 货运单查询
	 * @param apiUrl
	 * @param TOKEN
	 * @param APIKEY
	 * @param SALTKEY
	 * @param shopId
	 * @param ShippingOrderCode 配送编码==物流单号
	 * @param TSCode  订单编号
	 * @return
	 */
	public String GetShippingOrder(String apiUrl,String TOKEN,String APIKEY,String SALTKEY,int shopId,String ShippingOrderCode,String TSCode) 
	{
		String timeStamp =GetCurrentTime(apiUrl);

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V2/ShippingOrder/Get";
		}
		else			
		{
			apiUrl+="/V2/ShippingOrder/Get";
		}

		String resbody=""; 

		try
		{
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String sNow=sdf.format(now);			
			//System.out.println(sNow);			

			JSONObject header = new JSONObject();
			header.put("ShopId", shopId);//此為商店序號。若不知序號，請至【OSM.功能-商店維護】查詢 			

			header.put("ShippingOrderCode", ShippingOrderCode);
			header.put("TSCode", TSCode);

			String request=header.toString();

			// String for signature calculation
			String content = "ts=" + timeStamp + "&data="+request+"&sk=" + SALTKEY;

			// Hash algorithm
			EncryptUtils eu = new EncryptUtils();
			String hash=eu.HMAC_SHA512(content, APIKEY);
			eu=null;
			
			// Signature and URL
			String url=apiUrl+"?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash;
			//System.out.println("Signature: " + hash);
			//System.out.println("URL: "+url);

			//处理一下请求
			request=String.format("{'Data':'%s'}", request);

			resbody=HttpSend.SendNineApp("GetShippingOrder", request, url);

			JSONObject jsonres = new JSONObject(resbody);

			String rStatus=jsonres.getString("Status");

			if(rStatus.equals("Failure"))//失败
			{
				String ErrorMessage=jsonres.getString("ErrorMessage");

				//System.out.println("错误信息:"+ErrorMessage);
			}
			else
			{
				//			
				JSONObject data = jsonres.getJSONObject("Data");
				String sTMCode=data.getString("TMCode");//主單編號
				String sTSCode=data.getString("TSCode");//訂單編號 
				//配送方式 -宅配(含離島宅配) : Home 
				//-超商取貨付款 : StoreCashOnDelivery -付款後超商取貨 : StorePickup -付款後門市自取 : LocationPickup -貨到付款：CashOnDelivery
				String OrderDeliverType =data.getString("OrderDeliverType");//
				//通路商 -全家 :Family -7-11 :SevenEleven 
				String DistributorDef="";
				if (data.isNull("DistributorDef")==false) 
				{
					DistributorDef=data.getString("DistributorDef");//
				}				

				//溫層類別 -常溫 :Normal -冷藏 :Refrigerator -冷凍 :Freezer 
				String TemperatureTypeDef="";
				if (data.isNull("TemperatureTypeDef")) 
				{
					TemperatureTypeDef=data.getString("TemperatureTypeDef");//
				}
				String sShippingOrderCode=data.getString("ShippingOrderCode");//貨運單配送編號 
				String GoodsToLogisticCenterDate=data.getString("GoodsToLogisticCenterDate");//货到物流中心日2013-1104T17:30:00 
				String SuggestGoodsArrivalDate=data.getString("SuggestGoodsArrivalDate");//建议货到期限2013-1114T17:30:00 
				//出貨單狀態 -Finish : 已出貨至消費者 
				//**超商付款取貨 / 超商純取貨專用貨態 -AllocatedCode : 已配號 -VerifySuccess : 超商驗收成功 
				//-VerifyFailLost : 超商驗收失敗.商品遺失 -VerifyFailAbnormalPackage :超商 驗收失敗.包裝異常 
				//-VerifyFailRenovation : 超商驗收失敗.門市閉店/整修 -VerifyFailErrorCode : 超商驗收失敗.配送編號異常 
				//-VerifyFailInvalIdCode : 超商驗收失敗.編號失效(未到 貨) -ShippingProcessing : 出貨處理中 -ShippingFail : 消費者逾期未取，出貨失敗
				//-ShippingArrived : 貨到門市 
				//**門市自取專用貨態 
				//-AllocatedCode : 已配號 -ShippingProcessing : 出貨處理中 -ShippingFail : 消費者逾期未取，出貨失敗(逾期未取) -ShippingArrived : 貨到門市
				String ShippingOrderStatus=data.getString("ShippingOrderStatus");//
				String ShippingOrderStatusUpdatedDateTime=data.getString("ShippingOrderStatusUpdatedDateTime");//出貨狀態最後更新日 2013-11-11T17:30:00
				String OrderReceiverName=data.getString("OrderReceiverName");//收件人姓名 
				String OrderReceiverMobile=data.getString("OrderReceiverMobile");//收件人電話 
				String OrderReceiverZipCode=data.getString("OrderReceiverZipCode");//收件人郵遞區號 
				String OrderReceiverCity=data.getString("OrderReceiverCity");//收件人縣市 
				String OrderReceiverDistrict=data.getString("OrderReceiverDistrict");//收件人鄉鎮市區 
				String OrderReceiverAddress=data.getString("OrderReceiverAddress");//收件人地址 *若為超商取貨的訂單，則為地址為超商門市的地址 
				String OrderReceiverStoreId=data.getString("OrderReceiverStoreId");//超商取貨的門市編號 
				String OrderReceiverStoreName=data.getString("OrderReceiverStoreName");//超商取貨的門市名稱 
				double StorePaymentAmount=data.getDouble("StorePaymentAmount");//超商代收金額 -為各筆訂單(TS)的代收金額 -若此筆貨運單包含主單(TM)運費金額時，系統會自動 納入其中一筆訂單(TS)的代收金額中 

				//System.out.println("货运单查询:"+ShippingOrderCode);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}		

		return resbody;			
	}





	/**
	 * 获取91APP服务器时间戳
	 * @param apiUrl
	 * @return
	 */
	public String GetCurrentTime(String apiUrl)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="V1/Utils/GetCurrentTime";
		}
		else			
		{
			apiUrl+="/V1/Utils/GetCurrentTime";
		}

		String resbody="";

		try 
		{
			resbody=HttpSend.SendNineApp("GetCurrentTime", "", apiUrl);
		} 
		catch (Exception e) 
		{

		}		

		return resbody;		
	}

	private String test()
	{
		String resbody=""; 
		/*
		try 
		{
            // Config
            String TOKEN = "12345678";
            String APIKEY = "5c28482a‐9a3e‐4126‐8d57‐fd59dff0363c";
            String SALTKEY = "0212345678";

            // Sample time stamp
            int timeStamp = 1497551195;

            // String for signature calculation
            String content = "ts=" + timeStamp + "&data={\"id\":8}&sk=" + SALTKEY;

            // Hash algorithm
            Mac sha_HMAC = Mac.getInstance("HmacSHA512");
            //System.out.println("Content: " + content);
            SecretKeySpec secret_key = new SecretKeySpec(APIKEY.getBytes(), "HmacSHA512");
            sha_HMAC.init(secret_key);

            String hash = DatatypeConverter.printHexBinary(sha_HMAC.doFinal(content.getBytes())).toLowerCase();

            // Signature and URL
            //System.out.println("Signature: " + hash);
            //System.out.println("URL: https://api.91mai.com/scm/v1/Shop/GetShipping?ts=" + timeStamp + "&t=" + TOKEN + "&s=" + hash);
        }
        catch (Exception e)
		{
            //System.out.println("Error");
        }
		 */
		return resbody;
	}

}
