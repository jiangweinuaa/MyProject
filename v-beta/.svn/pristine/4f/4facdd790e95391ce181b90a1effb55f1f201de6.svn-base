package com.dsc.spos.utils.ec;

import org.json.JSONArray;

import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import org.json.JSONObject;

/**
 * 雅虎商城接口 
 * yuanyy
 */
public class Yahoo {
	
	// https://tw.ews.mall.yahooapis.com/stauth/v1/Order/Query?OrderType=All&ShippingSupplier=HomeDelivery
	// &DateType=TransferDate&StartDate=2010/01/01&EndDate=2010/01/02&Position=1&Count=1&Format=xml
	//String YAHOOURL = "https://tw.ews.mall.yahooapis.com/stauth/v1";
	
	String APIKEY = "123";
	String SIGNATURE = "123";
	
	public String getYaHooOrder(String apiUrl , String orderType, String shippingType, String dateType, 
			String startDate,  String endDate , int position , int count){
		//获取时间戳
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		//查询订单
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="v1/Order/";
		}
		else			
		{
			apiUrl+="v1/Order/";
		}

		String resbody="";
		try
		{
			JSONObject header = new JSONObject();
			header.put("OrderType", orderType);//订单类型： All全部、 NonShipping 未出货、 Shipping 已出货、 NonClosed未结案、 Closed已结案		
			header.put("ShippingType", shippingType);//配送方式 : HomeDelivery:宅配  、 StoreDelivery:超商	、ESD:電子下載
			header.put("DateType", dateType);//日期条件： TransferDate:轉單日 、 LastShippingDate:最晚出貨日    、OrderCloseDate:訂單結案日		
			header.put("StartDate", startDate);//起始日期		
			header.put("EndDate", endDate);//结束日期
			header.put("Position", position);//由第几笔开始拿资料
			header.put("Count", count); // 资料笔数
			//下面这三个值先不传入   
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();	
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Query"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject TransactionList = jsonres2.getJSONObject("TransactionList");
				int resCount = TransactionList.getInt("@Count");
				int totalCount = TransactionList.getInt("@TotalCount");
				JSONArray Transaction = TransactionList.getJSONArray("Transaction");
				
				for (int i = 0; i < Transaction.length(); i++) {
					JSONObject jo = Transaction.getJSONObject(i);
					String transId = jo.getString("@Id");
					JSONArray orderList = jo.getJSONArray("Order");
					String[] orderIdList=new String[orderList.length()];
					
					for (int j = 0; j < orderList.length(); j++) { 
						JSONObject order = orderList.getJSONObject(j);
						String orderId = order.getString("@Id");
						orderIdList[j] = orderId;
						//System.out.println(orderId);
						
					}
					//System.out.println(orderIdList);
			          
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
	 * 取得購物車資料
	 * @param apiUrl
	 * @param transactionId
	 * @return
	 */
	public String getMaster(String apiUrl ,String transactionId){
		//获取时间戳
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Order/";
		}
		else			
		{
			apiUrl+="/Order/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("TransactionId", transactionId);// 交易序号		
			
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();

			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "GetMaster"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject TransactionList = jsonres2.getJSONObject("Transaction");
				int id = TransactionList.getInt("@Id");
				String isActivity = TransactionList.getString("IsActivity"); //此笔交易是否又符合活动 ， 有Yes， 无No
				String isUseCoupon = TransactionList.getString("IsUseCoupon"); //此笔交易是否有使用电子折扣券， 有Yes， 无No
				String payType = TransactionList.getString("PayType"); // 付款方式 
				//付款方式
//				CreditCard:信用卡，     ATM:ATM,	StorePay:超商繳費(IBON/FAMI),	StoreDelivery:超商付款取貨
				
				String Installment = TransactionList.getString("Installment"); // 分期期数
				String ShippingSupplier = TransactionList.getString("ShippingSupplier"); // 配送方式
				//HomeDelivery：宅配    StoreDelivery：超商   ESD：ESD
				String StoreType = TransactionList.getString("StoreType"); // 超商种类
				double TransactionPrice = TransactionList.getDouble("TransactionPrice"); //订单总金额
				String contMan = TransactionList.getString("_BuyerName");  //订购人
				String contTel = TransactionList.getString("_BuyerPhone"); //订购人电话
				String memo = TransactionList.getString("_TransactionRemark"); //备注
				
				JSONObject orderList = TransactionList.getJSONObject("OrderList");
				
				JSONArray order = orderList.getJSONArray("Order");
				//System.out.println("Order:"+order);
				
				for (int i = 0; i < order.length(); i++) {
					JSONObject jo = order.getJSONObject(i);
					String orderNo = jo.getString("@Id");
			        String orderCloseDate = jo.getString("OrderCloseDate"); // 订单结案日
			        String orderPackageDate = jo.getString("OrderPackageDate"); //包装确认日
			        String orderStatus = jo.getString("OrderStatus"); //订单状态 ， NEW:未结案 ； CANCEL:取消； SHIPPED:完成出货
			        String orderStatusDesc = jo.getString("OrderStatusDesc"); // 订单状态描述
				}
				
			}
			else{
				
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		
		return resbody;
	}
	
	
	/**
	 * 获取购物车中订单明细资料
	 * @param apiUrl
	 * @param transactionId
	 * @param orderId
	 * @return
	 */
	public String getDetail(String apiUrl, String transactionId, String[] orderIdList){
		//获取时间戳
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Order/";
		}
		else			
		{
			apiUrl+="/Order/";
		}
		try
		{
			
			JSONObject header = new JSONObject();
			header.put("TransactionId", transactionId);// 交易序号		
			String orderId = "";
			for (int i = 0; i < orderIdList.length; i++) {
				// 每次最多查询 100笔订单
				if(i > 100){
					break;
				}
				
				orderId = orderIdList[i];
				header.put("OrderId", orderId);
			}
			
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "GetDetail"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){

				// 获取到返回JSON 里的各订单编号
				JSONObject TransactionList = jsonres2.getJSONObject("Transaction");
				String id = TransactionList.getString("@Id");
				
				JSONObject receiver = TransactionList.getJSONObject("Receiver");
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

				JSONObject successList = TransactionList.getJSONObject("SuccessList");
				String count = successList.getString("@Count");
				JSONObject orderList = successList.getJSONObject("OrderList");
				JSONObject orderDetail = orderList.getJSONObject("Order");
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
				JSONObject MessageNode = jsonres2.getJSONObject("Message");
				String message = MessageNode.getString("#cdata-section");
			
			}
			else{
				return "";
			}
			
		}
		catch (Exception ex) 
		{			
			return "";
		}
		
		return resbody;
	}
	
	/**
	 * 查询订单状态 
	 * @param apiUrl
	 * @param transactionId
	 * @param orderId
	 * @return
	 */
	public String getStatus(String apiUrl, String transactionId, String orderId){
		//获取时间戳
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Order/";
		}
		else			
		{
			apiUrl+="/Order/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("TransactionId", transactionId);// 交易序号		
			header.put("OrderId", orderId);
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "GetStatus"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject TransactionList = jsonres2.getJSONObject("Transaction");
				int id = TransactionList.getInt("@Id");
				
				JSONObject failList = TransactionList.getJSONObject("FailList");
				int failCount = failList.getInt("@Count");
				// 如果是失败信息， 这里会有失败的订单信息
				JSONObject failOrderList = failList.getJSONObject("OrderList");
				//System.out.println("failOrderList:"+failOrderList);
				JSONArray failOrder = failOrderList.getJSONArray("Order");
				//System.out.println("failOrder:"+failOrder);
				
				//失败的订单号， 可以在这里声明一个数组。用来存储失败的订单号
				for (int i = 0; i < failOrder.length(); i++) {
					JSONObject jo = failOrder.getJSONObject(i);
					String failOrderNo = jo.getString("@Id");
				}
				JSONObject successList = TransactionList.getJSONObject("SuccessList");
				
				int sucCount = successList.getInt("@Count");
				JSONObject sucOrderList = successList.getJSONObject("OrderList");
				//System.out.println("sucOrderList:"+sucOrderList);
				JSONArray sucOrder = sucOrderList.getJSONArray("Order");
				
				for (int i = 0; i < sucOrder.length(); i++) {
					JSONObject jo = sucOrder.getJSONObject(i);
					String sucOrderNo = jo.getString("@Id");
					String sucOrderStatus = jo.getString("OrderStatus");
					String sucOrderStatusDesc = jo.getString("OrderStatusDesc");
				}
			}
			else{
				return "";
			}
			
		}
		catch (Exception ex) 
		{			
			return "";
		}
		
		return resbody;
	}
	
	/**
	 * 商店取消订单
	 * @param apiUrl
	 * @param transactionId
	 * @param orderId
	 * @param CancelRemarkw  取消备注
	 * @return
	 */
	public String orderCancel(String apiUrl, String transactionId, String orderId , String cancelRemark){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Order/";
		}
		else			
		{
			apiUrl+="/Order/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("TransactionId", transactionId);// 交易序号		
			header.put("OrderId", orderId);
			header.put("CancelRemark",cancelRemark);
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Cancel"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				JSONObject TransactionList = jsonres2.getJSONObject("Transaction");
				int Id = TransactionList.getInt("@Id");
				JSONObject order = TransactionList.getJSONObject("Order");
				String orderNo = order.getString("@Id");
				String orderCloseDate = order.getString("OrderCloseDate");
				
				//System.out.println("orderNo: "+orderNo  + "   date:"+orderCloseDate);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	/**
	 * 查询取消订单
	 * @param apiUrl
	 * @param cancelReason
	 * @param dateType
	 * @param startDate
	 * @param endDate
	 * @param position
	 * @param count
	 * @return
	 */
	public String getCancelOrder(String apiUrl, String cancelReason, String dateType ,
			String startDate, String endDate, String position, String count){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Cancel/";
		}
		else			
		{
			apiUrl+="/Cancel/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("CancelReason", cancelReason);		
			header.put("DateType", dateType);
			header.put("StartDate",startDate);
			header.put("EndDate", endDate); 	
			header.put("Position", position);
			header.put("Count",count);
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Query"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject TransactionList = jsonres2.getJSONObject("TransactionList");
				int num = TransactionList.getInt("@Count"); //避免与参数中的count 重复，这里写num
				int totalCount = TransactionList.getInt("@TotalCount");
				JSONArray transaction = TransactionList.getJSONArray("Transaction");
				
				for (int i = 0; i < transaction.length(); i++) {
					JSONObject jo = transaction.getJSONObject(i);
					String id = jo.getString("@Id");
					
					JSONArray order = jo.getJSONArray("Order");
					for (int j = 0; j < order.length(); j++) {
						JSONObject orderDetail = order.getJSONObject(j);
						String orderNo = orderDetail.getString("@Id");
						String orderStatus = orderDetail.getString("OrderStatus");
						String orderStatusDesc = orderDetail.getString("OrderStatusDesc");
						//System.out.println("orderNo :"+ orderNo + "  orderStatus:"+orderStatus
						//		+ "    orderStatusDesc:"+orderStatusDesc);
						
					}
					
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
	 * 对宅配订单进行出货确认
	 * @param transactionId 交易序号
	 * @param orderId 订单编号
	 * @param shippingSupplierCode 物流商代号
	 * @param shippingNumber 货运单号
	 * @return
	 */
	public String confirmHomeDelivery(String apiUrl, String transactionId, String orderId, String shippingSupplierCode, String shippingNumber){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderShipping/";
		}
		else			
		{
			apiUrl+="/OrderShipping/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("TransactionId", transactionId);		
			header.put("OrderId", orderId);
			header.put("ShippingSupplierCode",shippingSupplierCode);
			header.put("ShippingNumber", shippingNumber); 	
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "ConfirmHomeDelivery"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				JSONObject transaction = jsonres2.getJSONObject("Transaction");
				String id = transaction.getString("@Id");
				JSONObject order = transaction.getJSONObject("Order");	
				String orderNo = order.getString("@Id");
				String orderShippingConfirmDate = order.getString("OrderShippingConfirmDate");
				
				//System.out.println("orderNo: "+orderNo  + "   date:"+orderShippingConfirmDate);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	/**
	 * 
	 * @param apiUrl
	 * @param startOrderShippingConfirmDate 起始出货确认日期
	 * @param endOrderShippingConfirmDate 迄止出货确认日期
	 * @param position 从第几笔开始拿资料
	 * @param count 资料笔数
	 * @return
	 */
	public String queryStoreDeliveryReceipt(String apiUrl, String startOrderShippingConfirmDate, String endOrderShippingConfirmDate, String position, String count){
		
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderShipping/";
		}
		else			
		{
			apiUrl+="/OrderShipping/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("StartOrderShippingConfirmDate", startOrderShippingConfirmDate);		
			header.put("EndOrderShippingConfirmDate", endOrderShippingConfirmDate);
			header.put("Position",position);
			header.put("Count", count); 	
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "QueryStoreDeliveryReceipt"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject transactionList = jsonres2.getJSONObject("TransactionList");
				int num = transactionList.getInt("@Count");
				int totalCount = transactionList.getInt("@TotalCount");
				JSONArray transaction = transactionList.getJSONArray("Transaction");
				for (int i = 0; i < transaction.length(); i++) {
					JSONObject jo = transaction.getJSONObject(i);
					String id = jo.getString("@Id");
					String storeType = jo.getString("StoreType");
					
					JSONArray deliveryReceiptList = jo.getJSONArray("DeliveryReceiptList");
					for (int j = 0; j < deliveryReceiptList.length(); j++) {
						JSONObject detail = deliveryReceiptList.getJSONObject(j);
						String detailNum = detail.getString("@Count");
						
						JSONArray deliveryReceipt = detail.getJSONArray("DeliveryReceipt");
						for (int k = 0; k < deliveryReceipt.length(); k++) {
							JSONObject receiptDetail = deliveryReceipt.getJSONObject(k);
							int orderId =  receiptDetail.getInt("@Id");
							String distributionChannelStatus = receiptDetail.getString("DistributionChannelStatus");
							String distributionChannelStatusDesc = receiptDetail.getString("_DistributionChannelStatusDesc");
							JSONObject orderList = receiptDetail.getJSONObject("OrderList");
							String orderCount = orderList.getString("@Count");
							
							JSONArray order = orderList.getJSONArray("Order");
							for (int m = 0; m < order.length(); m++) {
								JSONObject orderDetail = order.getJSONObject(m);
								String orderNo = orderDetail.getString("@Id");
								String orderStatus = orderDetail.getString("OrderStatus");
								String orderStatusDesc = orderDetail.getString("_OrderStatusDesc");
								//System.out.println(orderNo + "   "+orderStatus + "   " +orderStatusDesc);
							}
						
						}
					}
					
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
	 * 取得超商出货单明细资料
	 * @param apiUrl
	 * @param deliveryReceiptId
	 * @return
	 */
	public String getStoreDeliveryReceiptDetail(String apiUrl, String deliveryReceiptId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderShipping/";
		}
		else			
		{
			apiUrl+="/OrderShipping/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("DeliveryReceiptId", deliveryReceiptId);		
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "GetStoreDeliveryReceiptDetail"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject transaction = jsonres2.getJSONObject("Transaction");
				String id = transaction.getString("@Id");
				
				JSONObject deliveryReceipt = transaction.getJSONObject("DeliveryReceipt");
				String orderId = deliveryReceipt.getString("@Id");
				
				String storeType = deliveryReceipt.getString("StoreType");
				String serialNumber = deliveryReceipt.getString("SerialNumber");
				String deliveryReceiptPrice = deliveryReceipt.getString("DeliveryReceiptPrice");
				String distributionChannelStatus = deliveryReceipt.getString("DistributionChannelStatus");
				String distributionChannelStatusDesc = deliveryReceipt.getString("_DistributionChannelStatusDesc");
				String orderShippingConfirmDate = deliveryReceipt.getString("OrderShippingConfirmDate");
				String lastDeliveryDate = deliveryReceipt.getString("LastDeliveryDate");
				String orderDelayShippingDate = deliveryReceipt.getString("OrderDelayShippingDate");
				String receiverName = deliveryReceipt.getString("_ReceiverName");
				String takeDeliveryConvenienceStoreName = deliveryReceipt.getString("_TakeDeliveryConvenienceStoreName");
				String takeDeliveryConvenienceStoreId = deliveryReceipt.getString("TakeDeliveryConvenienceStoreId");
				String barcode = deliveryReceipt.getString("Barcode");
				String deliveryToConvenienceStoreDate = deliveryReceipt.getString("DeliveryToConvenienceStoreDate");
				String convenienceStoreReturnDate = deliveryReceipt.getString("ConvenienceStoreReturnDate");
				String convenienceStoreOrderId = deliveryReceipt.getString("_ConvenienceStoreOrderId");
				String storeName = deliveryReceipt.getString("_StoreName");
				String customerCareInfo = deliveryReceipt.getString("_CustomerCareInfo");
				String url = deliveryReceipt.getString("_Url");
				
				JSONArray OrderList = deliveryReceipt.getJSONArray("OrderList");
				for (int i = 0; i < OrderList.length(); i++) {
					JSONObject jo = OrderList.getJSONObject(i);
					String num = jo.getString("@Count");
					JSONArray Order = jo.getJSONArray("Order");
					for (int j = 0; j < Order.length(); j++) {
						JSONObject orderDetail = Order.getJSONObject(j);
						String orderNo = orderDetail.getString("@Id");
						String orderStatus = orderDetail.getString("OrderStatus"); 
						String orderStatusDesc = orderDetail.getString("_OrderStatusDesc"); 
						String orderDate = orderDetail.getString("OrderDate"); 
						
						JSONObject orderProductList = orderDetail.getJSONObject("OrderProductList");
						String count =  orderProductList.getString("@Count");
						
						JSONArray product = orderProductList.getJSONArray("Product");
						for (int k = 0; k < product.length(); k++) {
							JSONObject productDetail = product.getJSONObject(k);
							String productId = productDetail.getString("@Id");
							String saleType = productDetail.getString("SaleType");
							String customizedProductId = productDetail.getString("_CustomizedProductId");
							String productName = productDetail.getString("_ProductName");
							String spec = productDetail.getString("_Spec");
							String amount = productDetail.getString("Amount");
							String subTotal = productDetail.getString("Subtotal");
							
						}
					}
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
	 * 對超商訂單執行出貨確認(物流交寄)
	 * 訂單編號，可接受多筆，但須屬於同一個交易序號下的訂單編號
		ex. TransactionId=T123456&OrderId=O1234567&OrderId=Y123457&OrderId=E123456
		請注意：
		1."物流服務費“不須執行出貨確認(系統會依此[交易序號]之第一筆出貨確認之出貨單出貨)
		2.傳入之訂單總金額需>0
	 * @param apiUrl
	 * @param transactionId
	 * @param OrderId
	 * @return
	 */
	public String ConfirmStoreDelivery(String apiUrl, String transactionId, String orderId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderShipping/";
		}
		else			
		{
			apiUrl+="/OrderShipping/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("TransactionId", transactionId);	
			header.put("OrderId", orderId);	
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();	
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "ConfirmStoreDelivery"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject deliveryReceipt = jsonres2.getJSONObject("DeliveryReceipt");
				String id = deliveryReceipt.getString("@Id");
				
				String storeType = deliveryReceipt.getString("StoreType");
				String deliveryReceiptPrice = deliveryReceipt.getString("DeliveryReceiptPrice");
				String orderShippingConfirmDate = deliveryReceipt.getString("OrderShippingConfirmDate");
				String lastDeliveryDate = deliveryReceipt.getString("LastDeliveryDate");
				String orderDelayShippingDate = deliveryReceipt.getString("OrderDelayShippingDate");
				String deliveryToConvenienceStoreDate = deliveryReceipt.getString("DeliveryToConvenienceStoreDate");
				String convenienceStoreReturnDate = deliveryReceipt.getString("ConvenienceStoreReturnDate");
				
				JSONArray OrderList = deliveryReceipt.getJSONArray("OrderList");
				for (int i = 0; i < OrderList.length(); i++) {
					JSONObject jo = OrderList.getJSONObject(i);
					JSONArray Order = jo.getJSONArray("Order");
					for (int j = 0; j < Order.length(); j++) {
						JSONObject orderDetail = Order.getJSONObject(j);
						String orderNo = orderDetail.getString("@Id");
						//System.out.println("orderNo:"+orderNo);
						
					}
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
	 * 取消超商出货单
	 * @param apiUrl
	 * @param deliveryReceiptId
	 * @return
	 */
	public String cancelStoreDeliveryReceipt(String apiUrl, String deliveryReceiptId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="OrderShipping/";
		}
		else			
		{
			apiUrl+="/OrderShipping/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("DeliveryReceiptId", deliveryReceiptId);	
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();	
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "CancelStoreDeliveryReceipt"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject successList = jsonres2.getJSONObject("SuccessList");
				JSONObject deliveryReceipt = successList.getJSONObject("DeliveryReceipt");
				
				String Id = deliveryReceipt.getString("@Id");
				JSONObject orderList = deliveryReceipt.getJSONObject("OrderList");
				int count = orderList.getInt("@Count");
				
				JSONArray order = orderList.getJSONArray("Order");
				for (int j = 0; j < order.length(); j++) {
					JSONObject orderDetail = order.getJSONObject(j);
					String orderNo = orderDetail.getString("@Id");
					//System.out.println("orderNo:"+orderNo);
					
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
	 * 退货查询
	 * @param apiUrl
	 * @param returnStatus 
	 * 退貨單狀態
		All：全部
		NonClose：未結案
		Close：已結案
		Cancel：取消
	 * @param dateType
	 * 日期條件
		ReturnCreateDate：退貨單建立日
		ReturnCloseDate：退貨結案日
	 * @param startDate
	 * @param endDate    EndDate-StartDate 要小於等於7天
	 * @param position
	 * @param count
	 * @return
	 */
	public String queryReturnOrder(String apiUrl, String returnStatus, String dateType, String startDate, String endDate, String position, String count){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Return/";
		}
		else			
		{
			apiUrl+="/Return/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("ReturnStatus", returnStatus);	
			header.put("DateType", dateType);
			header.put("StartDate", startDate);
			header.put("EndDate", endDate);
			header.put("Position", position);
			header.put("Count", count);
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Query"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject transactionList = jsonres2.getJSONObject("TransactionList");
				int num = transactionList.getInt("@Count");
				int totalCount = transactionList.getInt("@TotalCount");
				
				JSONArray transaction = transactionList.getJSONArray("Transaction");
				for (int i = 0; i < transaction.length(); i++) {
					JSONObject jo = transaction.getJSONObject(i);
					String id = jo.getString("@Id");
				
					JSONArray Order = jo.getJSONArray("Order");
					for (int j = 0; j < Order.length(); j++) {
						JSONObject orderDetail = Order.getJSONObject(j);
						String orderNo = orderDetail.getString("@Id");
						//System.out.println("orderNo:"+orderNo);
					}
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
	 * 取得退货明细资料
	 * @param apiUrl
	 * @param transactionId
	 * @param orderId
	 * @return
	 */
	public String queryReturnOrderDetail(String apiUrl, String transactionId, String orderId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Return/";
		}
		else			
		{
			apiUrl+="/Return/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("TransactionId", transactionId);	
			header.put("OrderId", orderId);
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Get"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject transaction = jsonres2.getJSONObject("Transaction");
				int id = transaction.getInt("@Id");
				
				JSONObject order = transaction.getJSONObject("Order");
				String orderNo = order.getString("@Id");
				JSONObject returnList = order.getJSONObject("returnMsg");
				int returnCount =returnList.getInt("@Count");
				JSONArray returnMsg = returnList.getJSONArray("Return");
				for (int i = 0; i < returnMsg.length(); i++) {
					JSONObject returnDetail = returnMsg.getJSONObject(i);
					String returnId = returnDetail.getString("@Id");
					String returnCreateDate = returnDetail.getString("ReturnCreateDate");
					String returnReason = returnDetail.getString("_ReturnReason");
					String returnReasonRemark = returnDetail.getString("_ReturnReasonRemark");
					String returnPickupName = returnDetail.getString("_ReturnPickupName");
					String returnPickupMobile = returnDetail.getString("_ReturnPickupMobile");
					String returnPickupPhone = returnDetail.getString("_ReturnPickupPhone");
					String returnPickupAddress = returnDetail.getString("_ReturnPickupAddress");
					int returnPrice = returnDetail.getInt("ReturnPrice");
					String returnStatus = returnDetail.getString("ReturnStatus");
					String returnCloseDate = returnDetail.getString("ReturnCloseDate");
					String returnDebitDate = returnDetail.getString("ReturnDebitDate");
					String productId = returnDetail.getString("ProductId");
					String customizeId = returnDetail.getString("CustomizeId");
					String productName = returnDetail.getString("ProductName");
					String spec = returnDetail.getString("Spec");
					
					JSONObject returnAbnormalityList = returnDetail.getJSONObject("ReturnAbnormalityList");
					int num = returnAbnormalityList.getInt("@Count");
					
					JSONArray returnAbnormality = returnAbnormalityList.getJSONArray("ReturnAbnormality");
					for (int j = 0; j < returnAbnormality.length(); j++) {
						JSONObject detail = returnAbnormality.getJSONObject(j);
						String detailId = detail.getString("@Id");
						String returnAbnormalityCreateDate = detail.getString("ReturnAbnormalityCreateDate");
						int returnAbnormalityReason = detail.getInt("ReturnAbnormalityReason");
						String returnAbnormalityRemark = detail.getString("_ReturnAbnormalityRemark");
						String returnAbnormalityStatus = detail.getString("ReturnAbnormalityStatus");
						
						//System.out.println(""+returnAbnormalityCreateDate + "  "+returnAbnormalityReason);
					
					}
					
				}
				
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	public String returnOrderConfirm(String apiUrl, String transactionId, String orderId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Return/";
		}
		else			
		{
			apiUrl+="/Return/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("TransactionId", transactionId);	
			header.put("OrderId", orderId);
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Confirm"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject transaction = jsonres2.getJSONObject("Transaction");
				String  id = transaction.getString("@Id");
				
				JSONObject order = transaction.getJSONObject("Order");
				String orderNo = order.getString("@Id");
				String returnCloseDate = order.getString("ReturnCloseDate");
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	
	/**
	 * 换货单查询
	 * @param apiUrl
	 * @param exchangeStatus
	 * @param dateType
	 * @param startDate
	 * @param endDate
	 * @param position
	 * @param count
	 * @return
	 */
	public String getExchange(String apiUrl, String exchangeStatus, String dateType, String startDate, String endDate, String position, String count){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Exchange/";
		}
		else			
		{
			apiUrl+="/Exchange/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("ExchangeStatus", exchangeStatus);	
			header.put("DateType", dateType);
			header.put("StartDate", startDate);
			header.put("EndDate", endDate);
			header.put("Position", position);
			header.put("Count", count);
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Query"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject transactionList = jsonres2.getJSONObject("TransactionList");
				int num = transactionList.getInt("@Count");
				int totalCount = transactionList.getInt("@TotalCount");
				
				JSONArray transaction = transactionList.getJSONArray("Transaction");
				for (int i = 0; i < transaction.length(); i++) {
					JSONObject jo = transaction.getJSONObject(i);
					String orderId = jo.getString("@Id");
					
					JSONArray order = jo.getJSONArray("Order");
					for (int j = 0; j < order.length(); j++) {
						JSONObject detail = order.getJSONObject(j);
						String orderNo = detail.getString("@Id");
						JSONObject exchange = detail.getJSONObject("Exchange");
						String excId = exchange.getString("@Id");
						//System.out.println("excId:"+excId);
					}
					
				}
				
				
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	
	
	public String exchangeConfirm(String apiUrl, String exchangeId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Exchange/";
		}
		else			
		{
			apiUrl+="/Exchange/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("ExchangeId", exchangeId);	
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Confirm"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject Exchange = jsonres2.getJSONObject("Exchange");
				String excId = Exchange.getString("@Id");
				String exchangeConfirmDate = Exchange.getString("ExchangeConfirmDate");
				//System.out.println(excId + "  "+exchangeConfirmDate);
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	
	/**
	 * 刊登主商品
	 * @param apiUrl
	 * @param saleType
	 * @param saleTypeInfo
	 * @param productName
	 * @param mallCategoryId
	 * @param storeCategoryId
	 * @param marketPrice
	 * @param salePrice
	 * @param costPrice
	 * @param maxBuyNum
	 * @param shortDescription
	 * @param videoPath
	 * @param longDescription
	 * @param linkAddon
	 * @param linkFreebie
	 * @param payTypeId
	 * @param shippingId
	 * @param attrName
	 * @param attrValue
	 * @param productAmount
	 * @param productPrice
	 * @param isTaxFree
	 * @param specTypeDimension
	 * @param customizedMainProductId
	 * @param stock
	 * @param saftyStock
	 * @param specDimension1
	 * @param specDimension2
	 * @param specDimension1Description
	 * @param specDimension2Description
	 * @return
	 */
	public String productSubmitMain(String apiUrl, String saleType, String saleTypeInfo, String productName, String mallCategoryId, String storeCategoryId,
			String marketPrice, String salePrice, String costPrice, String maxBuyNum, String shortDescription, String videoPath, String longDescription,
			String linkAddon, String linkFreebie, String payTypeId, String shippingId, String attrName,String attrValue , String productAmount, String productPrice, 
			String isTaxFree, String specTypeDimension,
			String customizedMainProductId, String stock, String saftyStock, String specDimension1, String specDimension2,
			String specDimension1Description, String specDimension2Description){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Product/";
		}
		else			
		{
			apiUrl+="/Product/";
		}
		try
		{
			String n = "1"; // n 的值，需要参考商品属性，暂时都写为1 
			JSONObject header = new JSONObject();
			header.put("SaleType", saleType);	
			header.put("SaleTypeInfo", saleTypeInfo);	
			header.put("ProductName", productName);	
			header.put("MallCategoryId", mallCategoryId);	
			header.put("StoreCategoryId", storeCategoryId);	
			header.put("MarketPrice", marketPrice);	
			header.put("SalePrice", salePrice);	
			header.put("CostPrice", costPrice);	
			header.put("MaxBuyNum", maxBuyNum);	
			header.put("ShortDescription", shortDescription);	
			header.put("VideoPath", videoPath);	
			header.put("LongDescription", longDescription);	
			header.put("LinkAddon", linkAddon);	
			header.put("LinkFreebie", linkFreebie);	
			header.put("PayTypeId", payTypeId);	
			header.put("ShippingId", shippingId);	
			header.put("Attribute."+n+".Name", attrName);	
			header.put("Attribute."+n+".Value", attrValue);	
			header.put("ProductPromotion."+n+".Amount", productAmount);	
			header.put("ProductPromotion."+n+".Price", productPrice);	
			header.put("IsTaxFree", isTaxFree);	
			header.put("SpecTypeDimension", specTypeDimension);	
			header.put("CustomizedMainProductId", customizedMainProductId);	
			header.put("Stock", stock);	
			header.put("SaftyStock", saftyStock);	
			header.put("SpecDimension1", specDimension1);	
			header.put("SpecDimension2", specDimension2);	
			header.put("SpecDimension1Description", specDimension1Description);	
			header.put("SpecDimension2Description", specDimension2Description);	
			
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "SubmitMain"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各编号
				String productId = jsonres2.getString("ProductId");
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	/**
	 * 商品上架
	 * @param apiUrl
	 * @param productId
	 * @return
	 */
	public String productOnline(String apiUrl, String productId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Product/";
		}
		else			
		{
			apiUrl+="/Product/";
		}
		try
		{
			String n = "1"; // n 的值，需要参考商品属性，暂时都写为1 
			JSONObject header = new JSONObject();
			header.put("ProductId", productId);	
			
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Online"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject successList = jsonres2.getJSONObject("SuccessList");
				JSONArray product = successList.getJSONArray("Product");
				for (int i = 0; i < product.length(); i++) {
					JSONObject jo = product.getJSONObject(i);
					String id = jo.getString("@Id");
					//System.out.println("Id:"+id);
				}
				
				JSONArray failProduct = jsonres2.getJSONArray("FailList");
				for (int i = 0; i < failProduct.length(); i++) {
					JSONObject jo = failProduct.getJSONObject(i);
					String id = jo.getString("@Id");
					//System.out.println("Id:"+id);
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
	 * 商品下架
	 * @param apiUrl
	 * @param productId
	 * @return
	 */
	public String productOffline(String apiUrl, String productId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Product/";
		}
		else			
		{
			apiUrl+="/Product/";
		}
		try
		{
			String n = "1"; // n 的值，需要参考商品属性，暂时都写为1 
			JSONObject header = new JSONObject();
			header.put("ProductId", productId);	
			
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();	
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Offline"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject successList = jsonres2.getJSONObject("SuccessList");
				JSONArray product = successList.getJSONArray("Product");
				for (int i = 0; i < product.length(); i++) {
					JSONObject jo = product.getJSONObject(i);
					String id = jo.getString("@Id");
					//System.out.println("Id:"+id);
				}
				
				JSONArray failProduct = jsonres2.getJSONArray("FailList");
				for (int i = 0; i < failProduct.length(); i++) {
					JSONObject jo = failProduct.getJSONObject(i);
					String id = jo.getString("@Id");
					//System.out.println("Id:"+id);
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
	 * 删除商品
	 * @param apiUrl
	 * @param productId
	 * @return
	 */
	public String productDelete(String apiUrl, String productId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Product/";
		}
		else			
		{
			apiUrl+="/Product/";
		}
		try
		{
			String n = "1"; // n 的值，需要参考商品属性，暂时都写为1 
			JSONObject header = new JSONObject();
			header.put("ProductId", productId);	
			
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Delete"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject successList = jsonres2.getJSONObject("SuccessList");
				JSONArray product = successList.getJSONArray("Product");
				for (int i = 0; i < product.length(); i++) {
					JSONObject jo = product.getJSONObject(i);
					String id = jo.getString("@Id");
					//System.out.println("Id:"+id);
				}
				
				JSONArray failProduct = jsonres2.getJSONArray("FailList");
				for (int i = 0; i < failProduct.length(); i++) {
					JSONObject jo = failProduct.getJSONObject(i);
					String id = jo.getString("@Id");
					//System.out.println("Id:"+id);
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
	 * 上传商品图片
	 * @param apiUrl
	 * @param productId
	 * @param imageFilen
	 * @param mainImage
	 * @param purge
	 * @return
	 */
	private String uploadImage(String apiUrl, String productId, String imageFilen, String mainImage, String purge){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Product/";
		}
		else			
		{
			apiUrl+="/Product/";
		}
		try
		{
			String n = "1"; // n 的值，需要参考商品属性，暂时都写为1 
			JSONObject header = new JSONObject();
			header.put("ProductId", productId);	
			header.put("MainImage", mainImage);	
			header.put("Purge", purge);	
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			header.put("ImageFilen", imageFilen);	//不加入signature
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "UploadImage"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject successList = jsonres2.getJSONObject("SuccessList");
				JSONArray image = successList.getJSONArray("Image");
				for (int i = 0; i < image.length(); i++) {
					JSONObject jo = image.getJSONObject(i);
					String name = jo.getString("@Name");
					String isMainImage = jo.getString("@IsMainImage");
					String fileName = jo.getString("@FileName");
					//System.out.println("name:"+name + "    isMainImage:"+isMainImage+ "   FileName:"+fileName);
				}
				
				JSONArray failProduct = jsonres2.getJSONArray("FailList");
				for (int i = 0; i < failProduct.length(); i++) {
//					JSONObject jo = failProduct.getJSONObject(i);
//					String id = jo.getString("@Id");
//					//System.out.println("Id:"+id);
				}
				
				JSONObject picList = jsonres2.getJSONObject("PicList");
				JSONArray imageName = picList.getJSONArray("ImageName");
				for (int i = 0; i < imageName.length(); i++) {
					String imageNameValue = imageName.get(i).toString();
					//System.out.println("名称："+imageNameValue);
				}
				
				
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	
	public String updateMain(String apiUrl, String productId, String saleType, String saleTypeInfo, String productName, String mallCategoryId, String storeCategoryId,
			String marketPrice, String salePrice, String costPrice, String maxBuyNum, String shortDescription, String videoPath, String longDescription,
			String linkAddon, String linkFreebie, String payTypeId, String shippingId, String attrName, String attrValue, 
			String productAmount, String productPrice, String isTaxFree, String deleteAddon, String deleteFreebie,
			String customizedMainProductId, String specDimension1, String specDimension2, String deleteProductPromotion){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Product/";
		}
		else			
		{
			apiUrl+="/Product/";
		}
		try
		{
			String n = "1"; // n 的值，需要参考商品属性，暂时都写为1 
			JSONObject header = new JSONObject();
			header.put("ProductId", productId);	
			header.put("SaleType", saleType);	
			header.put("SaleTypeInfo", saleTypeInfo);	
			header.put("ProductName", productName);	
			header.put("MallCategoryId", mallCategoryId);	
			header.put("StoreCategoryId", storeCategoryId);	
			header.put("MarketPrice", marketPrice);	
			header.put("SalePrice", salePrice);	
			header.put("CostPrice", costPrice);	
			header.put("MaxBuyNum", maxBuyNum);	
			header.put("ShortDescription", shortDescription);	
			header.put("VideoPath", videoPath);	
			header.put("LongDescription", longDescription);	
			header.put("LinkAddon", linkAddon);	
			header.put("LinkFreebie", linkFreebie);	
			header.put("PayTypeId", payTypeId);	
			header.put("ShippingId", shippingId);	
			header.put("Attribute."+n+".Name", attrName);	
			header.put("Attribute."+n+".Value", attrValue);	
			header.put("ProductPromotion."+n+".Amount", productAmount);	
			header.put("ProductPromotion."+n+".Price", productPrice);	
			header.put("IsTaxFree", isTaxFree);	
			header.put("DeleteAddon", deleteAddon);	
			header.put("DeleteFreebie", deleteFreebie);	
			header.put("CustomizedMainProductId", customizedMainProductId);	
			header.put("SpecDimension1", specDimension1);	
			header.put("SpecDimension2", specDimension2);	
			header.put("DeleteProductPromotion", deleteProductPromotion);	
			
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "UpdateMain"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				String id = jsonres2.getString("ProductId");
				
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	
	/**
	 * 更新商品库存
	 * @param apiUrl
	 * @param specId
	 * @param specCustomizedProductId
	 * @param specAction
	 * @param specStock
	 * @param specExpectStock
	 * @param saftyStock
	 * @return
	 */
	public String updateStock(String apiUrl, String productId, String specId, String specCustomizedProductId, String specAction, String specStock, String specExpectStock, String specSaftyStock){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Product/";
		}
		else			
		{
			apiUrl+="/Product/";
		}
		try
		{
			String n = "1"; // n 的值，需要参考商品属性，暂时都写为1 
			JSONObject header = new JSONObject();
			header.put("ProductId", productId);	
			header.put("Spec."+n+".Id", specId);	
			header.put("Spec."+n+".CustomizedProductId", specCustomizedProductId);	
			header.put("Spec."+n+".Action", specAction);	
			header.put("Spec."+n+".Stock", specStock);	
			header.put("Spec."+n+".ExpectStock", specExpectStock);	
			header.put("Spec."+n+".SaftyStock", specSaftyStock);	
			
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "UpdateStock"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				String id = jsonres2.getString("ProductId");
				
				JSONObject successList = jsonres2.getJSONObject("SuccessList");
				JSONArray spec = successList.getJSONArray("Spec");
				for (int i = 0; i < spec.length(); i++) {
					JSONObject jo = spec.getJSONObject(i);
					int specNo = jo.getInt("@Id");
					//System.out.println("Id:"+id);
				}
				
				JSONArray failProduct = jsonres2.getJSONArray("FailList");
				for (int i = 0; i < failProduct.length(); i++) {
					JSONObject jo = failProduct.getJSONObject(i);
//					String id = jo.getString("@Id");
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
	 * 查询上下级分类
	 * @param apiUrl
	 * @param categoryId
	 * @return
	 */
	public String getMallCategory(String apiUrl, String categoryId){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="MallCategory/";
		}
		else			
		{
			apiUrl+="/MallCategory/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("CategoryId", categoryId);	
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();	
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Get"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject categoryList = jsonres2.getJSONObject("CategoryList");
				int count = categoryList.getInt("@Count");
				
				JSONArray category = categoryList.getJSONArray("Category");
				for (int i = 0; i < category.length(); i++) {
					JSONObject jo = category.getJSONObject(i);
					String id = jo.getString("@Id");
					String isLeaf = jo.getString("IsLeaf");
					String isLink = jo.getString("IsLink");
					String level = jo.getString("Level");
					int sorting = jo.getInt("Sorting");
					String url = jo.getString("Url");
					String name = jo.getString("Name");
					//System.out.println("name:"+name );
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
	 * 查询店铺支付方式
	 * @param apiUrl
	 * @return
	 */
	public String getStorePayment(String apiUrl){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="StorePayment/";
		}
		else			
		{
			apiUrl+="/StorePayment/";
		}
		try
		{
			JSONObject header = new JSONObject();
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();	
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Get"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject ShippingSupplierList = jsonres2.getJSONObject("ShippingSupplierList");
				int count = ShippingSupplierList.getInt("@Count");
				
				JSONArray payType = ShippingSupplierList.getJSONArray("PayType");
				for (int i = 0; i < payType.length(); i++) {
					JSONObject jo = payType.getJSONObject(i);
					int id = jo.getInt("@Id");
					String name = jo.getString("Name");
					String userDefaultOn = jo.getString("UserDefaultOn");
					//System.out.println("name:"+name );
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
	 * 查询店铺物流方式
	 * @param apiUrl
	 * @return
	 */
	public String getStoreShipping(String apiUrl){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="StoreShipping/";
		}
		else			
		{
			apiUrl+="/StoreShipping/";
		}
		try
		{
			JSONObject header = new JSONObject();
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "Get"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject ShippingSupplierList = jsonres2.getJSONObject("ShippingSupplierList");
				int count = ShippingSupplierList.getInt("@Count");
				
				JSONArray ShippingSupplier = ShippingSupplierList.getJSONArray("PayType");
				for (int i = 0; i < ShippingSupplier.length(); i++) {
					JSONObject jo = ShippingSupplier.getJSONObject(i);
					int id = jo.getInt("@Id");
					String name = jo.getString("Name");
					String userDefaultOn = jo.getString("UserDefaultOn");
					String feeType = jo.getString("FeeType");
					boolean Desc = jo.getBoolean("Desc");
					
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
	 * 取得商店的主商品列表資料
	 * @param apiUrl
	 * @param storeCategoryId
	 * @param productStatus
	 * @param position
	 * @param count
	 * @return
	 */
	public String getMainList(String apiUrl, String storeCategoryId, String productStatus, String position, String count){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Product/";
		}
		else			
		{
			apiUrl+="/Product/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("StoreCategoryId", storeCategoryId);
			header.put("ProductStatus", productStatus);
			header.put("Position", position);
			header.put("Count", count);
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "GetMainList"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				String totalCount = jsonres2.getString("TotalCount");
				JSONObject mainList = jsonres2.getJSONObject("MainList");
				int num = mainList.getInt("@Count");
				
				JSONArray product = mainList.getJSONArray("Product");
				for (int i = 0; i < product.length(); i++) {
					JSONObject jo = product.getJSONObject(i);
					JSONObject main = jo.getJSONObject("Main");
					
					String productId = main.getString("@Id");
					String name = main.getString("Name");
					double salePrice = main.getDouble("SalePrice");
					double costPrice = main.getDouble("CostPrice");
					//System.out.println("name:"+name +" salePrice:"+salePrice);
					
					JSONArray spec = main.getJSONArray("Spec");
					for (int j = 0; j < spec.length(); j++) {
						JSONObject specDetail = spec.getJSONObject(j);
						int specId = specDetail.getInt("@Id");
						//mad,接口文档中SpecDescription参数名后有个空格
						String SpecDescription = specDetail.getString("SpecDescription");
						String customizeId = specDetail.getString("CustomizeId");
						int currentStock = specDetail.getInt("CurrentStock");
						int saftyStock = specDetail.getInt("SaftyStock");
						
					}
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
	 * 取得目前設定的貨運商代碼對應表,於出貨確認時使用
	 * @param apiUrl
	 * @return
	 */
	public String getShippingSupplierList(String apiUrl){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Order/";
		}
		else			
		{
			apiUrl+="/Order/";
		}
		try
		{
			JSONObject header = new JSONObject();
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();	
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "GetShippingSupplierList"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject shippingSupplierList = jsonres2.getJSONObject("ShippingSupplierList");
				
				JSONArray shippingSupplier = shippingSupplierList.getJSONArray("ShippingSupplier");
				for (int i = 0; i < shippingSupplier.length(); i++) {
					JSONObject jo = shippingSupplier.getJSONObject(i);
					String name = jo.getString("_Name");
					String code = jo.getString("@Code");
					
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
	 * 取得单一主商品资料
	 * @param apiUrl
	 * @param productId
	 * @param dispLongDesc
	 * @return
	 */
	public String getMain(String apiUrl, String productId, String dispLongDesc){
		String timeStamp = YahooTimeStamp.getTimeStamp(apiUrl);
		String resbody = null;
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Product/";
		}
		else			
		{
			apiUrl+="/Product/";
		}
		try
		{
			JSONObject header = new JSONObject();
			header.put("ProductId", productId);
			header.put("DispLongDesc", dispLongDesc);
			// 固定传三个值
			header.put("TimeStamp", timeStamp);
			header.put("ApiKey", APIKEY);
			header.put("Signature", SIGNATURE);
			String request=header.toString();
			
			EncryptUtils eu = new EncryptUtils();
			////暂时先不设置 加密密钥
			String signature=eu.HMAC_SHA256(apiUrl+"|"+request, null);
			eu=null;
			
			String method = "GetMain"; 
			resbody = HttpSend.SendYahoo(method, request, apiUrl, signature);
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject jsonres2 = jsonres.getJSONObject("Response");
			
			//////获取到返回的订单编码后， 需要调查询订单明细接口， 暂时保留
			String status = jsonres2.getString("@Status").toUpperCase();
			if(status.equals("OK")){
				// 获取到返回JSON 里的各订单编号
				JSONObject Product = jsonres2.getJSONObject("Product");
				double costPrice = Product.getDouble("CostPrice");
				String customizedMainProductId = Product.getString("CustomizedMainProductId");
				
				JSONObject imageList = Product.getJSONObject("ImageList");
				JSONArray image = imageList.getJSONArray("Image");
				for (int i = 0; i < image.length(); i++) {
					JSONObject jo = image.getJSONObject(i);
					String fullSizeImageUrl = jo.getString("FullSizeImageUrl");
					String imageUrl = jo.getString("ImageUrl");
					String isMainImage = jo.getString("IsMainImage");
					String name = jo.getString("Name");
				}
				
				int isTaxFree = Product.getInt("IsTaxFree");
				JSONObject linkAddonList = Product.getJSONObject("LinkAddonList"); //自定货号
				JSONArray linkAddon = linkAddonList.getJSONArray("LinkAddon");
				for (int i = 0; i < linkAddon.length(); i++) {
					JSONObject jo = linkAddon.getJSONObject(i);
					String linkAddonId = jo.getString("@Id");
					String desc = jo.getString("Desc");
					
					JSONObject Image2 = jo.getJSONObject("Image");
					String fullSizeImageUrl2 = Image2.getString("FullSizeImageUrl");
					String imageUrl2 = Image2.getString("ImageUrl");
					
					String productStatus = jo.getString("ProductStatus");
					String title = jo.getString("_Title");
					
				}
				
				
				JSONObject linkFreebieList = Product.getJSONObject("LinkFreebieList"); //自定货号
				JSONArray linkFreebie = linkFreebieList.getJSONArray("LinkFreebie");
				for (int i = 0; i < linkFreebie.length(); i++) {
					JSONObject jo = linkFreebie.getJSONObject(i);
					String linkFreebieId = jo.getString("@Id");
					String desc = jo.getString("Desc");
					
					JSONObject Image = jo.getJSONObject("Image");
					String fullSizeImageUrl = Image.getString("FullSizeImageUrl");
					String imageUrl = Image.getString("ImageUrl");
					
					String productStatus = jo.getString("ProductStatus");
					String title = jo.getString("_Title");
					
				}
				
				int mallCategoryId = Product.getInt("MallCategoryId");
				double marketPrice = Product.getDouble("MarketPrice");
				int maxBuyNum = Product.getInt("MaxBuyNum");
				JSONObject PayTypeIdList = Product.getJSONObject("PayTypeIdList"); //自定货号
				JSONArray PayTypeIdDetail = PayTypeIdList.getJSONArray("PayTypeId");
				for (int i = 0; i < PayTypeIdDetail.length(); i++) {
					JSONObject jo = PayTypeIdDetail.getJSONObject(i);
					int payTypeId = jo.getInt("@Id");
				}
				
				String productId2 = Product.getString("ProductId");
				String productStatus = Product.getString("ProductStatus");
				double salePrice = Product.getDouble("SalePrice");
				String saleType = Product.getString("SaleType");
				String saleTypeInfo = Product.getString("SaleTypeInfo");
				
				JSONObject ShippingIdList = Product.getJSONObject("ShippingIdList"); //自定货号
				JSONArray ShippingIdDetail = ShippingIdList.getJSONArray("ShippingId");
				for (int i = 0; i < ShippingIdDetail.length(); i++) {
					JSONObject jo = ShippingIdDetail.getJSONObject(i);
					int shippingId = jo.getInt("@Id");
				}
				
				JSONObject SpecDimensionList = Product.getJSONObject("SpecDimensionList"); //自定货号
				JSONArray SpecDimension = SpecDimensionList.getJSONArray("SpecDimension");
				for (int i = 0; i < SpecDimension.length(); i++) {
					JSONObject jo = SpecDimension.getJSONObject(i);
					int specDimensionId = jo.getInt("@Id");
					JSONObject DescriptionList = jo.getJSONObject("DescriptionList");
					
					JSONArray Description = DescriptionList.getJSONArray("Description");
					for (int j = 0; j < Description.length(); j++) {
						JSONObject DescriptionDetail = Description.getJSONObject(j);
						int descriptionId = DescriptionDetail.getInt("@Id");
						String descriptionName =  DescriptionDetail.getString("_Name");
					}
				}
				
				JSONObject SpecList = Product.getJSONObject("SpecList"); 
				JSONArray Spec = SpecList.getJSONArray("Spec");
				for (int i = 0; i < Spec.length(); i++) {
					JSONObject jo = Spec.getJSONObject(i);
					int specId = jo.getInt("@Id");
					int currentStock = jo.getInt("CurrentStock");
					String customizedProductId = jo.getString("CustomizedProductId");
					String barCode = jo.getString("BarCode");
					
					JSONObject DimensionList = jo.getJSONObject("DimensionList"); 
					JSONArray Dimension = DimensionList.getJSONArray("Dimension");
					for (int j = 0; j < Dimension.length(); j++) {
						JSONObject dimensionDetail = Dimension.getJSONObject(j);
						int dimensionId = dimensionDetail.getInt("@Id");
						
						JSONObject description = dimensionDetail.getJSONObject("Description");
						int descriptionId = description.getInt("@Id");
					}
					
					int saftyStock = jo.getInt("SaftyStock");
					int stock = jo.getInt("Stock");
					String specDescription = jo.getString("_SpecDescription");
				}
				
				int specTypeDimension = Product.getInt("SpecTypeDimension");
				String storeCategoryList = Product.getString("StoreCategoryList");
				String longDescription = Product.getString("_LongDescription");
				String productName = Product.getString("_ProductName");
				String shortDescription = Product.getString("_ShortDescription");
				String specDimension1 = Product.getString("_SpecDimension1");
				String specDimension2 = Product.getString("_SpecDimension2");
				String videoPath = Product.getString("_VideoPath");
				
				//System.out.println("specTypeDimension:"+specTypeDimension);
				
			}
		}
		catch (Exception ex) 
		{			
			return "";
		}
		return resbody;
	}
	
	
}
