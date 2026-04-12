package com.dsc.spos.utils;

import java.io.BufferedWriter;

import java.io.FileOutputStream;

import java.io.OutputStreamWriter;

import java.io.StringWriter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.hutool.core.convert.Convert;
import com.dsc.spos.model.ShangyouOrder;
import com.dsc.spos.model.ShangyouReturnDish;
import com.dsc.spos.utils.ec.shangyou;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import org.json.JSONArray;
import org.json.JSONObject;


import com.dsc.spos.utils.ec.NineOneApp;
import com.dsc.spos.utils.ec.Rakuten;
import com.dsc.spos.utils.ec.Shopee;
import com.dsc.spos.utils.logistics.Cvs;
import com.dsc.spos.utils.logistics.Egs;
import com.dsc.spos.utils.logistics.GreenWorld;
import com.dsc.spos.utils.logistics.Htc;
import com.dsc.spos.utils.logistics.SevenEleven;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;




public class XmlAndJsonConvert
{

	//需要jar包：
	//jackson-annotations-2.3.0.jar，
	//jackson-core-2.3.0.jar，jackson-databind-2.1.3.jar，
	//jackson-dataformat-xml-2.1.3.jar，stax2-2.1.jar

	private static ObjectMapper objectMapper = new ObjectMapper();

	private static XmlMapper xmlMapper = new XmlMapper();

	/**
	 * JSON转XML
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	public static String json2xml(String jsonStr)throws Exception
	{
		JsonNode rootnode = objectMapper.readTree(jsonStr);
		String xml = xmlMapper.writeValueAsString(rootnode);
		return xml;
	}

	/**
	 * XML转JSON
	 * @param xml
	 * @return
	 */
	public static String xml2json(String xml)
	{
		StringWriter sw = new StringWriter();
		JsonParser jp;
		try
		{
			jp = xmlMapper.getFactory().createParser(xml);
			JsonGenerator jg = objectMapper.getFactory().createGenerator(sw);
			while (jp.nextToken() != null)
			{
				jg.copyCurrentEvent(jp);
			}
			jp.close();
			jg.close();
		}
		catch (Exception e)
		{

		}

		return sw.toString();
	}



	public static void main(String[] args) throws Exception
	{
		String xx="";


		try
		{
			shangyou sy=new shangyou();
			String apiUrl="http://steward-qa.syoo.cn";
			String authToken="bf63d86b-439d-46d4-993d-62bf32be82a1";
			String signKey="0c71becfd51747ae8d678f6c01eca52a";
			long storeId=100961;
			long thirdShopId= Long.parseLong("01");

			String json="";

//			//门店绑定，商有切换店铺到云管家可以新建店铺，我们负责映射绑定就行，
//			json=sy.storeBinding(apiUrl,authToken,signKey,storeId,thirdShopId);
//			System.out.println(json);

			//获取所有商有云管家门店，用于门店映射
			json=sy.getOpenStoreInfoList(apiUrl,authToken,signKey);
			System.out.println(json);

//
//			//查询门店信息
//			json=sy.getOpenStoreInfo(apiUrl,authToken,signKey,storeId);
//			System.out.println(json);
//
//			//获取门店配送方式和物流状态是否可用
//			json=sy.getStoreExpressAndStatus(apiUrl,authToken,signKey,storeId);
//			System.out.println(json);

			//上商城订单+已接单状态，新建订单
			//目前只搞必传字段
			ShangyouOrder order=new ShangyouOrder();
			order.setOrderNo("5040345603043716463");//第三方订单id
			order.setDaySeq("3");//店铺当日订单流水
			order.setMemberId("");//会员id
			order.setSalesType(2);//1。预约单 2。现售单
			if (order.getSalesType() == 1)
			{
				order.setDeliveryTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate("20211102104822")));//预计送达时间（预约单必传）
			}
			order.setDeliveryType(1);//1.外送 2.自提
			if (order.getDeliveryType() == 2)
			{
				order.setPickupAddress("江苏省苏州市工业园区斜塘街道敦煌三村");//自提点地址
				order.setPickupTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate("20211102104822")));//用户自提时间
			}
			order.setPayType(1);//1.在线 2.货到付款
			order.setPayStatus(2);//1.未支付 2.已支付
			order.setRecipientPhone("13120511712");//收货人电话
			order.setCustomerName("康康");//收货人名称
			order.setOrderStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate("20211102104822")));//下单时间

			//优惠相关--这个看上去是支付信息，先不給吧
			order.setActivityTotal(new BigDecimal(1));//活动优惠总价(订单维度 元)
			order.setOrderActivities(new ArrayList<>());
			ShangyouOrder.Activities activitie=new ShangyouOrder.Activities();
			activitie.setRemark("折扣1");
			activitie.setPrice("1");
			order.getOrderActivities().add(activitie);

			//已接单
			order.setStatus(3);
			order.setDescription("备注");
			order.setAddress("江苏省苏州市工业园区斜塘街道敦煌四村");//送餐地址
			order.setDeliverFee(new BigDecimal(0));//配送费 元
			order.setMerchantDeliverySubsidy(new BigDecimal(0));//商家承担配送费 元
			order.setVipDeliveryFeeDiscount(new BigDecimal(0));//会员减免运费 元
			order.setTotalPrice(new BigDecimal(10));//订单总价(实付 元)
			order.setOriginalPrice(new BigDecimal(11));//订单原价 元
			order.setFoodNum(1);//sku数量
			order.setDinnersNumber(1);//用餐人数
			order.setOrderReceiveTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate("20211102104822")));//商户收到时间
			order.setOrderConfirmTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Convert.toDate("20211102104822")));//商户接单时间
			order.setBoxNum(1);//餐盒数量
			order.setPackageFee(new BigDecimal(1));//餐盒费(总价 元)
			order.setMerchantPhone("13120511712");//商家电话
			order.setLongitude("120.738956");//收货地址经度
			order.setLatitude("31.292101");//收货地址纬度
			order.setLalType(1);//经纬度类型(默认) 1为高德(GCJ02) 2 百度(BD09) 3 WGS84
			//商品明细
			order.setFoodDtoList(new ArrayList<>());
			ShangyouOrder.Food food=new ShangyouOrder.Food();
			food.setFoodId(10001);//第三方商品id
			food.setName("测试商品1");
			food.setQuantity(1);//商品数量
			food.setUnit("GE");//单位
			food.setPrice(new BigDecimal(10));//商品价格（单价 元）
			food.setUserPrice(new BigDecimal(9));//用户侧价格（商品总价,去掉商品活动之后的总价 元
			food.setShopPrice(new BigDecimal(10));//商户侧价格（商品总价 元）
			food.setBoxNum(1);//餐盒数量
			food.setBoxPrice(new BigDecimal(1));//餐盒价格 元
			food.setGoodType(2);//1.单品 2套餐 3 加菜
			//套餐子商品
			food.setChildFoodList(new ArrayList<>());
			if (food.getGoodType()==2)
			{
				ShangyouOrder.Food food1=new ShangyouOrder.Food();
				food1.setFoodId(10002);//第三方商品id
				food1.setName("测试商品2(子商品)");
				food1.setQuantity(1);//商品数量
				food1.setUnit("GE");//单位
				food1.setPrice(new BigDecimal(0));//商品价格（单价 元）
				food1.setUserPrice(new BigDecimal(0));//用户侧价格（商品总价,去掉商品活动之后的总价 元
				food1.setShopPrice(new BigDecimal(10));//商户侧价格（商品总价 元）
				food1.setBoxNum(1);//餐盒数量
				food1.setBoxPrice(new BigDecimal(1));//餐盒价格 元
				food1.setGoodType(1);//1.单品 2套餐 3 加菜
				food.getChildFoodList().add(food1);
			}
			order.getFoodDtoList().add(food);
			//新建订单
			json=sy.saveNewOrder(apiUrl,authToken,signKey,thirdShopId,order);
			System.out.println(json);

//			//商家主动取消订单，取消后状态变成6、无效订单
//			json=sy.orderValid(apiUrl,authToken,signKey,thirdShopId,"5040345603043716451","商家主动取消订单");
//			System.out.println(json);

			List<ShangyouReturnDish> refundFood=new ArrayList<>();
			ShangyouReturnDish dish=new ShangyouReturnDish();
			dish.setName("测试商品1");
			dish.setPrice(new BigDecimal(10));
			dish.setNum(1);
			dish.setTotalPrice(new BigDecimal(10));
			refundFood.add(dish);
//			//用户申请退单
//			json=sy.orderChargeBack(apiUrl,authToken,signKey,thirdShopId,"5040345603043716451","用户申请退单",0,refundFood);
//			System.out.println(json);

//			//同意退款
//			json=sy.orderChargeBackFinish(apiUrl,authToken,signKey,thirdShopId,"5040345603043716452");
//			System.out.println(json);

//			//订单完成
//			json=sy.orderFinishOrder(apiUrl,authToken,signKey,thirdShopId,"5040345603043716452");
//			System.out.println(json);

//			//拒绝退款
//			json=sy.orderChargeBackFail(apiUrl,authToken,signKey,thirdShopId,"5040345603043716451");
//			System.out.println(json);

//			//查询外卖订单详情
//			json=sy.orderFindOrder(apiUrl,authToken,signKey,thirdShopId,"5040345603043716456");
//			System.out.println(json);

//			//商家主动退款
//			json=sy.orderWorkRefundAll(apiUrl,authToken,signKey,storeId,thirdShopId,"5040345603043716453","卖完啦",new BigDecimal(10));
//			System.out.println(json);

//			//查询物流详情，根据商有云管家订单号，没配送之前data对象是null要注意解析
//			json=sy.logisticsOrderdetail(apiUrl,authToken,signKey,thirdShopId,"8042021102900018270",2);
//			System.out.println(json);
//
//			//查询物流状态列表，根据商有云管家订单号，没配送之前data对象是null要注意解析
//			json=sy.logisticsStatuslist(apiUrl,authToken,signKey,thirdShopId,"8042021102900018270",2);
//			System.out.println(json);

//			//取消物流
//			json=sy.logisticsCancelorder(apiUrl,authToken,signKey,thirdShopId,"8042021102900018268",2);
//			System.out.println(json);



			/*
			TrustManager[] trustManager = new TrustManager[] 
					{ 
							new X509TrustManager() 
							{

								public void checkClientTrusted(X509Certificate[] chain,
										String authType) throws CertificateException {
									// TODO Auto-generated method stub

								}

								public void checkServerTrusted(X509Certificate[] chain,
										String authType) throws CertificateException {
									// TODO Auto-generated method stub

								}

								public X509Certificate[] getAcceptedIssuers() {
									// TODO Auto-generated method stub
									return null;
								}

							} };
			SSLContext sslContext = null;
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustManager, new SecureRandom());

*/
			FTPClient client = new FTPClient();
			//client.setSSLSocketFactory(sslSocketFactory);

			client.connect("103.234.81.11", 21);
			boolean bc=client.login("FTA", "2020F04T21A");
			System.out.println(bc);


			//client.connect("101.37.33.19", 21);
			//boolean bc=client.login("admin", "admin");
			//System.out.println(bc);

			int replyCode = client.getReplyCode(); //是否成功登录服务器

			if(!FTPReply.isPositiveCompletion(replyCode))
			{
				//System.out.println("connect failed...ftp服务器:"+this.hostname+":"+this.port); 
			}

			String LOCAL_CHARSET = "GBK";
			// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
			if (FTPReply.isPositiveCompletion(client.sendCommand("OPTS UTF8", "ON")))
			{
				LOCAL_CHARSET = "UTF-8";
			}
			client.setControlEncoding(LOCAL_CHARSET);
			client.enterLocalPassiveMode();// 设置被动模式
			client.setFileType(FTPClient.BINARY_FILE_TYPE);// 设置传

			client.changeWorkingDirectory("Receive");

			//被动模式
			client.enterLocalPassiveMode();

			FTPFile[] ftpFiles = client.listFiles();

			String[] files=client.listNames();

			for (int i = 0; i < files.length; i++)
			{
				System.out.println(files[i]);
			}

			client.logout();

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}




		//xx=HttpSend.SendGreeWorld("shopinfo", "https://emap.pcsc.com.tw/EMapSDK.aspx", "commandid=SearchStore&city=&town=&roadname=&ID=&StoreName=%E8%99%B9%E6%B5%B7&SpecialStore_Kind=&leftMenuChecked=&address=", "utf-8");


		Mail mail=new Mail();
		String[] receiverEmail={"418056790@qq.com",""};
		String[] filenames=null;
		mail.sendMail(receiverEmail, "主题", "测试邮件", filenames);
		mail=null;

		//全家淡水市鎮店 新北市淡水區中山北路二段185號1樓
		//xx=HttpSend.SendCVSShopno("新北市", "淡水區", "全家淡水市鎮店");
		//System.out.println(xx);

		/*
		Document doc = Jsoup.parse(xx);
		Elements ele=doc.getElementsByTag("option");
		String mygetshopno="";
		for (int i = 0; i < ele.size(); i++) 
		{
			mygetshopno=ele.get(i).attr("value");
			//System.out.println(mygetshopno);

			String shopinfo=ele.get(i).childNode(0).toString();

			//System.out.println(shopinfo);

			String[] splitShop=shopinfo.split("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			if (splitShop[0].contains("全家淡水市鎮店")) 
			{
				break;
			}			
		}
		doc=null;
		//System.out.println("************************");
		//System.out.println(mygetshopno);
		 */

		GreenWorld gWorld=new GreenWorld();

		String xapiUrl="https://logistics-stage.ecpay.com.tw";
		//String MerchantID="2000933";
		//String HashKey="XBERn1YOvpM9nfZc";
		//String HashIV="h1ONHk4P4yqbl5LK";

		String MerchantID="2000132";
		String HashKey="5294y06JbISpM5x9";
		String HashIV="v77hoKGq4kWxNNIS";

		String ServerReplyURL="http://eliutong2.digiwin.com.cn/retail/";
		String LogisticsC2CReplyURL="http://eliutong2.digiwin.com.cn/retail/";

		Map<String, Object> oneData=new HashMap<String, Object>();
		oneData.put("MerchantTradeNo", "abc01234567891234170");
		oneData.put("MerchantTradeDate", "2019/08/05 17:37:05");
		oneData.put("LogisticsType", "CVS");
		//---B2C--- FAMI:全家 UNIMART:7-ELEVEN 超商 HILIFE:萊爾富 
		//---C2C--- FAMIC2C:全家店到店 UNIMARTC2C:7-ELEVEN 超商交貨便 HILIFEC2C:萊爾富店到店 
		oneData.put("LogisticsSubType", "FAMI");
		//訂單金額
		oneData.put("GoodsAmount", "200");
		//是否代收貨款
		oneData.put("IsCollection", "N");
		//代收貨款金額
		oneData.put("CollectionAmount", 0);//物流子類型為 UNIMARTC2C(7-ELEVEN 超 商交貨便)時，代收金額需要與商品金額 一致。 
		oneData.put("GoodsName", "百貨商品");
		//寄件人
		oneData.put("SenderName", "寄件人");
		//寄件人電話
		oneData.put("SenderPhone","0911429215");
		//寄件人手機
		oneData.put("SenderCellPhone", "0911429215");
		//收件人
		oneData.put("ReceiverName","收件人");
		//收件人電話
		oneData.put("ReceiverPhone", "0911429215");
		//收件人手機
		oneData.put("ReceiverCellPhone", "0911429215");
		//收件人email
		oneData.put("ReceiverEmail","");
		//訂單備註
		oneData.put("Remark", "");
		//取貨門店
		////測試環境請使用以下門市進行測試，7-ELEVEN 超商：991182、全家：001779、萊爾富：2001。 
		oneData.put("ReceiverStoreID","001779");


		//xx=gWorld.ExpressCreate_CVS(xapiUrl, MerchantID,HashKey,HashIV, ServerReplyURL, LogisticsC2CReplyURL, oneData);
		//System.out.println(xx);

		//TCAT:黑貓 ECAN:宅配通 
		oneData.put("LogisticsSubType", "TCAT");
		oneData.put("SenderZipCode","801");
		oneData.put("SenderAddress","高雄市前金區市中一路166號6樓");
		oneData.put("ReceiverZipCode","300");
		oneData.put("ReceiverAddress","新竹市東區關東路151號16樓之1");
		oneData.put("Temperature","");
		oneData.put("Distance","");
		oneData.put("Specification","");
		oneData.put("ScheduledPickupTime","");
		oneData.put("ScheduledDeliveryDate","");
		oneData.put("PackageCount","");
		//xx=gWorld.ExpressCreate_Home(xapiUrl, MerchantID, HashKey, HashIV, ServerReplyURL, oneData);
		//System.out.println(xx);

		String AllPayLogisticsID="199453";
		oneData.put("AllPayLogisticsID",AllPayLogisticsID);
		//xx=gWorld.ExpressReturn_Home(xapiUrl, MerchantID, HashKey, HashIV, ServerReplyURL, oneData);
		//System.out.println(xx);

		//
		oneData=new HashMap<String, Object>();
		AllPayLogisticsID="200250";
		oneData.put("AllPayLogisticsID",AllPayLogisticsID);
		//寄件人
		oneData.put("SenderName", "寄件人");
		//寄件人電話
		oneData.put("SenderPhone","0911429215");
		oneData.put("GoodsName", "百貨商品");
		//訂單金額
		oneData.put("GoodsAmount", "200");
		//訂單備註
		oneData.put("Remark", "");

		//xx=gWorld.B2C_ExpressReturnFami_CVS(xapiUrl, MerchantID, HashKey, HashIV, ServerReplyURL, oneData);
		//System.out.println(xx);

		String RtnMerchantTradeNo="1908071623566";
		//xx=gWorld.B2C_LogisticsCheckAccountsFami(xapiUrl, MerchantID, HashKey, HashIV, RtnMerchantTradeNo);
		//System.out.println(xx);


		/*
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


		//xx="1|AllPayLogisticsID=199185&BookingNote=&CheckMacValue=5BA761C51C3197EA59B6E10EA57E8C6C&CVSPaymentNo=09936343518&CVSValidationNo=&GoodsAmount=200&LogisticsSubType=FAMIC2C&LogisticsType=CVS&MerchantID=2000933&MerchantTradeNo=abc01234567891234167&ReceiverAddress=&ReceiverCellPhone=0911429215&ReceiverEmail=&ReceiverName=收件人&ReceiverPhone=0911429215&RtnCode=300&RtnMsg=訂單處理中(已收到訂單資料)&UpdateStatusDate=2019/08/05 17:49:46";
		if (xx.startsWith("1|")) 
		{
			String urlPara=xx.substring(2);

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

				}

			}

		}
		 */


		AllPayLogisticsID="199199";
		String CVSPaymentNo="G3423302";
		String CVSValidationNo="6887";

		//xx=gWorld.C2C_Print711OrderInfo(xapiUrl, MerchantID, HashKey, HashIV, AllPayLogisticsID, CVSPaymentNo, CVSValidationNo);

		//System.out.println(xx);

		AllPayLogisticsID="199185";
		CVSPaymentNo="09936343518";
		//xx=gWorld.C2C_PrintFamiOrderInfo(xapiUrl, MerchantID, HashKey, HashIV, AllPayLogisticsID, CVSPaymentNo);

		//System.out.println(xx);

		AllPayLogisticsID="199200";
		CVSPaymentNo="01600199200";
		//xx=gWorld.C2C_PrintHilifeOrderInfo(xapiUrl, MerchantID, HashKey, HashIV, AllPayLogisticsID, CVSPaymentNo);

		//System.out.println(xx);

		AllPayLogisticsID="199199";
		CVSPaymentNo="G3423302";
		CVSValidationNo="6887";
		String StoreType="01";
		String ReceiverStoreID="870766";

		//xx=gWorld.C2C_Print711UpdateStoreInfo(xapiUrl, MerchantID, HashKey, HashIV, AllPayLogisticsID, CVSPaymentNo, CVSValidationNo,StoreType,ReceiverStoreID);

		//System.out.println(xx);


		AllPayLogisticsID="199199";
		CVSPaymentNo="G3423302";
		CVSValidationNo="6887";

		//xx=gWorld.C2C_Print711CancelOrderInfo(xapiUrl, MerchantID, HashKey, HashIV, AllPayLogisticsID, CVSPaymentNo, CVSValidationNo);

		//System.out.println(xx);

		AllPayLogisticsID="199453";

		//xx=gWorld.QueryLogisticsTradeInfo(xapiUrl, MerchantID, HashKey, HashIV, AllPayLogisticsID);

		//System.out.println(xx);

		String LogisticsSubType="FAMIC2C";
		String IsCollection="N";
		String ExtraData="abc01234567891234164@@@99@@@01@@@1564389167149";
		String Device="0";

		//xx=gWorld.ExpressMapInfo(xapiUrl, MerchantID, HashKey, HashIV,LogisticsSubType,IsCollection,ServerReplyURL,ExtraData,Device);

		//System.out.println(xx);

		AllPayLogisticsID="199453";
		//xx=gWorld.B2C_Home_PrintTradeDocInfo(xapiUrl, MerchantID, HashKey, HashIV,AllPayLogisticsID);

		//System.out.println(xx);

		AllPayLogisticsID="199203";
		String ShipmentDate="2019/08/07";
		ReceiverStoreID="";
		//xx=gWorld.B2C_Update711Shop_Date_Info(xapiUrl, MerchantID, HashKey, HashIV,AllPayLogisticsID,ShipmentDate,ReceiverStoreID);

		//System.out.println(xx);


		/*
		//目前物流狀態
		String GoodsAmount="";
		//物流狀態說明
		String LogisticsType="";
		//綠界物流編號
		AllPayLogisticsID="";		
		//C2C寄貨編號
		String HandlingCharge="";
		//C2C 7-11驗證碼
		String TradeDate="";
		//
		String LogisticsStatus="";
		//超商
		String ShipmentNo="";
		//宅配托運單號
		String BookingNote="";

		xx="AllPayLogisticsID=199199&BookingNote=&GoodsAmount=200&GoodsName=百貨商品&HandlingCharge=5&LogisticsStatus=9999&LogisticsType=CVS_UNIMARTC2C&MerchantID=2000933&MerchantTradeNo=abc01234567891234161&ShipmentNo=G34233026887&TradeDate=2019/08/05 17:37:05&CheckMacValue=23E159CB546038F70B698338B0B7DA48";
		if (xx.contains("AllPayLogisticsID=")) 
		{
			String urlPara=xx;

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
					else if (sTempStr[0].equals("GoodsAmount")) 
					{
						GoodsAmount=sTempStr[1];
					}
					else if (sTempStr[0].equals("LogisticsType")) 
					{
						LogisticsType=sTempStr[1];
					}
					else if (sTempStr[0].equals("HandlingCharge")) 
					{
						HandlingCharge=sTempStr[1];
					}
					else if (sTempStr[0].equals("TradeDate")) 
					{
						TradeDate=sTempStr[1];
					}		
					else if (sTempStr[0].equals("LogisticsStatus")) 
					{
						LogisticsStatus=sTempStr[1];
					}		
					else if (sTempStr[0].equals("ShipmentNo")) 
					{
						ShipmentNo=sTempStr[1];
					}		


				}

			}

		}
		else 
		{
			//0|找不到訂單資料

		}
		 */


		/*
		String paraString="MerchantID=2000132&MerchantTradeNo=ECPay&LogisticsSubType=FAMI&CVSStoreID=001779&CVSStoreName=%E5%B7%A5%E6%A5%AD%E5%BA%97&CVSAddress=%E5%8F%B0%E5%8C%97%E5%B8%82%E5%8D%97%E6%B8%AF%E5%8D%80%E4%B8%89%E9%87%8D%E8%B7%AF%EF%BC%91%EF%BC%99%E4%B9%8B%EF%BC%94%E8%99%9F&CVSTelephone=02-24326001&CVSOutSide=&ExtraData=0028587-190719-0918575867%40%40%4099%40%40%4001%40%40%401564389167149";

		String[] splitPostData= paraString.split("&");

		String MerchantID="";
		String MerchantTradeNo="";
		String LogisticsSubType="";
		String CVSStoreID="";
		String CVSStoreName="";
		String CVSAddress="";
		String CVSTelephone="";
		String CVSOutSide="";
		String ExtraData="";		

		for (int i = 0; i < splitPostData.length; i++) 
		{
			String nodes=splitPostData[i];			
			//转义
			nodes = nodes.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
			nodes = nodes.replaceAll("\\+", "%2B");  //+		
			nodes = URLDecoder.decode(nodes, "UTF-8");		
			//System.out.println(nodes);
			if (nodes.startsWith("MerchantID")) 
			{
				String[] splitTemp=nodes.split("=");
				if (splitTemp.length>1) 
				{
					MerchantID=splitTemp[1];
				}
			}
			else if (nodes.startsWith("MerchantTradeNo")) 
			{
				String[] splitTemp=nodes.split("=");
				if (splitTemp.length>1) 
				{
					MerchantTradeNo=splitTemp[1];
				}
			}
			else if (nodes.startsWith("LogisticsSubType")) 
			{
				String[] splitTemp=nodes.split("=");
				if (splitTemp.length>1) 
				{
					LogisticsSubType=splitTemp[1];
				}
			}
			else if (nodes.startsWith("CVSStoreID")) 
			{
				String[] splitTemp=nodes.split("=");
				if (splitTemp.length>1) 
				{
					CVSStoreID=splitTemp[1];
				}
			}
			else if (nodes.startsWith("CVSStoreName")) 
			{
				String[] splitTemp=nodes.split("=");
				if (splitTemp.length>1) 
				{
					CVSStoreName=splitTemp[1];
				}
			}
			else if (nodes.startsWith("CVSAddress")) 
			{
				String[] splitTemp=nodes.split("=");
				if (splitTemp.length>1) 
				{
					CVSAddress=splitTemp[1];
				}
			}
			else if (nodes.startsWith("CVSTelephone")) 
			{
				String[] splitTemp=nodes.split("=");
				if (splitTemp.length>1) 
				{
					CVSTelephone=splitTemp[1];
				}
			}
			else if (nodes.startsWith("CVSOutSide")) 
			{
				String[] splitTemp=nodes.split("=");
				if (splitTemp.length>1) 
				{
					CVSOutSide=splitTemp[1];
				}
			}
			else if (nodes.startsWith("ExtraData")) 
			{
				String[] splitTemp=nodes.split("=");
				if (splitTemp.length>1) 
				{
					ExtraData=splitTemp[1];
				}
			}			

		}

		//System.out.println(MerchantID);
		//System.out.println(MerchantTradeNo);
		//System.out.println(LogisticsSubType);
		//System.out.println(CVSStoreID);
		//System.out.println(CVSStoreName);
		//System.out.println(CVSAddress);
		//System.out.println(CVSTelephone);
		//System.out.println(CVSOutSide);
		//System.out.println(ExtraData);
		 */



		String guid="1499765769";
		String payload="{" +
				"\"occurred_at\": \"2017-03-20T15:21:00Z\"," +
				"\"status\": \"failed\"," +
				"\"reason\": \"price_mismatched\"," +
				"\"notes\": \"\"" +
				"}";

		String request=String.format("%s \n %s", guid,payload);

		//EncryptUtils eu = EncryptUtils.getInstance();
		//xx=eu.HMAC_SHA256_Deliveroo(request, "63e3cda3d8dd96dd4b166e9599ff5802b59ca91ee3c9584e0119f4b38d5b4822");
		////System.out.println(xx);


		/*
		//月日时分格式
		SimpleDateFormat myTempdf = new SimpleDateFormat("MMddHHmm");
		Calendar myTempcal = Calendar.getInstance();
		String sMMddHHmm=myTempdf.format(myTempcal.getTime());
		//System.out.println(sMMddHHmm);
		//
		Date dateTemp= myTempdf.parse(sMMddHHmm);
		myTempcal.setTime(dateTemp);
		//+-分钟
		myTempcal.add(Calendar.MINUTE, 1);
		sMMddHHmm=myTempdf.format(myTempcal.getTime());
		//System.out.println(sMMddHHmm);
		 */

		//
		//1556172350125
		Date now1 = new Date();
		long x=now1.getTime();
		//System.out.println(x);

		//1556263110
		//long lt=(1556263110*1000L);
		//Date date = new Date(lt);
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		//xx=simpleDateFormat.format(date);
		////System.out.println(xx);

		//91APP
		NineOneApp na=new NineOneApp();

		String apiUrlA="https://api.91mai.com/scm/";
		String TOKEN="12345678";
		String APIKEY="5c28482a-9a3e-4126-8d57-fd59dff0363c";
		String SALTKEY="0212345678";
		int shopId=8;

		//xx= na.GetCurrentTime("https://api.91mai.com/scm/");
		////System.out.println(xx);
		//xx= na.ShopGetShipping(apiUrlA, TOKEN, APIKEY, SALTKEY, shopId);
		////System.out.println(xx);


		String nLasttime = "20161024215906";
		SimpleDateFormat sdfA = new SimpleDateFormat("yyyyMMddHHmmss");
		Date dLasttime = sdfA.parse(nLasttime);
		sdfA = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String StartDateTime=sdfA.format(dLasttime);
		//System.out.println(StartDateTime);		

		Date now = new Date();
		SimpleDateFormat sdfapp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String sNow=sdfapp.format(now);
		//System.out.println(sNow);		

		//xx= na.GetSalesOrder(apiUrlA, TOKEN, APIKEY, SALTKEY, shopId, StartDateTime,sNow, 0);
		////System.out.println(xx);

		Htc htc=new Htc();
		String apiUrl="http://hctrt.hct.com.tw/EDI_WebService2/Service1.asmx";
		String eId="test";
		String password="test";
		String ordersn="A00013";
		String receiver="marry";
		String receiver_phone="0911123456";
		String receiver_address="台中市大雅區中清路三段 513";
		String pieces="1";
		String weight="5";
		String sender_site="";
		String shipdate="";
		String collectAmt="";
		String memo="";
		String productType="";
		String apiUrlTwo="https://www.hct.com.tw/phone/searchGoods_Main_Xml.ashx";
		String privateKey="176";
		String iv="LOZQGLLZ";
		String  v="9AEBDE4CB426EB1847A5A1E663D700C6";
		String[] expressNoList=new String[2];
		expressNoList[0]="8674437785";
		expressNoList[1]="8674437774";

		//xx= htc.GetHtcStatus_Page("https://www.hct.com.tw/phone/searchGoods_Main.aspx", privateKey, iv, v, "8674437774");
		////System.out.println(xx);


		//xx= htc.GetHtcStatus_Xml(apiUrlTwo, privateKey, iv, v, expressNoList);
		////System.out.println(xx);


		Egs egs=new Egs();
		//String apiUrl="http://101.37.33.19:8800/egs";

		//xx=egs.query_egs_info(apiUrl);
		////System.out.println(xx);

		//String[] suda5=new String[1];
		//suda5[0]="33042";
		//xx=egs.query_base("http://localhost:8800/egs", suda5);
		////System.out.println(xx);

		//String[] address=new String[1];
		//address[0]="桃園縣桃園市中山北路17之1號2樓";
		//xx=egs.query_suda7_dash(apiUrl, address);
		////System.out.println(xx);



		//String[] address=new String[1];
		//address[0]="桃園縣桃園市中山北路17之1號2樓";
		//xx=egs.query_suda5("http://localhost:8800/egs", address);
		////System.out.println(xx);

		//xx=egs.query_waybill_id_range("http://localhost:8800/egs", "7076259101", "A", 3);
		////System.out.println(xx);

		//xx=egs.query_waybill_id_remain("http://localhost:8800/egs", "7076259101", "A");
		////System.out.println(xx);

		//
		//String[] suda5_senderpostcode=new String[2];
		//suda5_senderpostcode[0]="00000";
		//suda5_senderpostcode[1]="00000";

		//String[] suda5_customerpostcode=new String[2];
		//suda5_customerpostcode[0]="99999";
		//suda5_customerpostcode[1]="99999";

		//xx=egs.query_distance("http://localhost:8800/egs", suda5_senderpostcode, suda5_customerpostcode);
		////System.out.println(xx);

		/*
		String customer_id="7076259101";
		String tracking_number="10001";
		String order_no="190322150599DX1";
		String receiver_name="董事长";
		String receiver_address="台北市士林區中正路601號";
		String receiver_suda5="11111";
		String receiver_mobile="0912-345678";
		String receiver_phone="12345678";
		String sender_name="发件人";
		String sender_address="台北市南港區重陽路200號";
		String sender_suda5="11501";
		String sender_phone="02-27887887";
		String product_price="100";
		String product_name="egs测试单据";
		String comment="egs订单上传";
		String package_size="0001";
		String temperature="0001";
		String distance="01";
		String delivery_date="2019-03-26";
		String delivery_timezone="4";
		String create_time="2019-03-26 16:30:05";
		String print_time="2019-03-26 16:30:05";
		String account_id="";
		String member_no="";
		String taxin="0";
		String insurance="0";

		xx=egs.transfer_waybill("http://localhost:8800/egs", "7076259101", tracking_number, order_no, receiver_name, receiver_address, receiver_suda5, receiver_mobile, receiver_phone, sender_name, sender_address, sender_suda5, sender_phone, product_price, product_name, comment, package_size, temperature, distance, delivery_date, delivery_timezone, create_time, print_time, account_id, member_no, taxin, insurance);
		//System.out.println(xx);
		 */


		List<Map<String, Object>> OrderEGSlist=new ArrayList<Map<String,Object>>();

		for (int i = 0; i < 2; i++)
		{
			Map<String, Object> mapEGSOrder=new HashMap<String, Object>();
			mapEGSOrder.put("eod01", i+"");//託運類別 1码, 1:客戶自行列印託運單 2:速達協助列印 (由速達系統分配託運單號) 3:已有單號，由速達列印(A4二模) –逆物流收退貨
			mapEGSOrder.put("eod02", "02");//託運單號 12码, 若上欄為2時，則此欄空白
			mapEGSOrder.put("eod03", "03");//訂單編號(客戶端),契約客戶端之訂單編號 出货单号
			mapEGSOrder.put("eod04", "04");//契客代號 10码
			mapEGSOrder.put("eod05", "05");//溫層 4码，0001:常溫  0002:冷藏 0003:冷凍  (default 0001)
			mapEGSOrder.put("eod06", "06");//距離 2码，00:同縣市 01:外縣市 02:離島   (default 00)
			mapEGSOrder.put("eod07", "");//規格 4码，0001: 60cm   0002: 90cm   0003: 120cm  0004: 150cm (default 0001) 
			mapEGSOrder.put("eod08", "");//是否代收貨款 1码，N:否  Y:是 (default N)
			mapEGSOrder.put("eod09", "");//代收金額 13码，若上一欄為Y，則為代收金額,例如：100元，則值為100	若上一欄為N，此欄固定帶0即可
			mapEGSOrder.put("eod10", "N");//是否到付 1码，無作用，請固定填N
			mapEGSOrder.put("eod11", "01");//是否付現 2码，00:付現 01:月結  (default 01)
			mapEGSOrder.put("eod12", "***");//收件人姓名 30码，若「託運類別」為1：為1客戶自行印託運單，因應資訊安全，收件人姓名帶*即可
			mapEGSOrder.put("eod13", "***");//收件人電話 20码，若「託運類別」為1：為1客戶自行印託運單，因應資訊安全，收件人電話帶*即可
			mapEGSOrder.put("eod14", "***");//收件人手機 20码，若「託運類別」為1：為1客戶自行印託運單，因應資訊安全，收件人手機帶*即可
			mapEGSOrder.put("eod15", "15");//收件人郵遞區號 5码，必須輸入
			mapEGSOrder.put("eod16", "向阳北路198号");//收件人地址 120码，必須輸入
			mapEGSOrder.put("eod17", "17");//寄件人姓名 30码，必須輸入
			mapEGSOrder.put("eod18", "");//寄件人電話 20码
			mapEGSOrder.put("eod19", "");//寄件人手機 20码
			mapEGSOrder.put("eod20", "20");//寄件人郵遞區號 5码，必須輸入，速達前往集貨地址的五碼郵號
			mapEGSOrder.put("eod21", "");//寄件人地址 120码，必須輸入，速達前往集貨地址
			mapEGSOrder.put("eod22", "22");//契客出貨日期或速達印託運單日期 14码，YYYYMMDDhhmmss，或系統日期	ss秒數若無的話，ss用00補齊14位	或是只提供到YYYYMMDD即可
			mapEGSOrder.put("eod23", "");//預定取件時段 1码，1: 9~12    2: 12~17    3: 17~20   4: 不限時(固定4不限時)
			mapEGSOrder.put("eod24", "");//預定配達時段 1码，1: 9~12    2: 12~17   3: 17~20   4: 不限時  5:20~21(需限定區域)
			mapEGSOrder.put("eod25", "");//會員編號 30码，可選擇性填入
			mapEGSOrder.put("eod26", "26");//物品名稱 60码，可選擇性填入
			mapEGSOrder.put("eod27", "N");//易碎物品 1码，N:否  Y:是  (default N) 目前無作用：固定填N
			mapEGSOrder.put("eod28", "N");//精密儀器 1码，N:否  Y:是  (default N) 目前無作用：固定填N
			mapEGSOrder.put("eod29", "");//備註 100码，可選擇性填入
			mapEGSOrder.put("eod30", "30");//SD路線代碼 9码，不必填
			OrderEGSlist.add(mapEGSOrder);
		}


		//egs.EOD("101.37.33.19","123", "0516", "ftptest", "ftptest", OrderEGSlist);


		Shopee shopee=new Shopee();



		//xx=shopee.GetLogistics("https://partner.shopeemobile.com/api", 842354, "63e3cda3d8dd96dd4b166e9599ff5802b59ca91ee3c9584e0119f4b38d5b4822", 418685);
		////System.out.println(xx);

		//shopee.GetPaymentList("https://partner.uat.shopeemobile.com/api", 101209, "68cd3ed8b40cb344676d32764ad39eb069d3935af467b09e4ab2390487a3d310");


		//xx=shopee.shopAuthPartner("https://partner.uat.shopeemobile.com/api", 101209, "68cd3ed8b40cb344676d32764ad39eb069d3935af467b09e4ab2390487a3d310", "http://www.baidu.com");
		////System.out.println(xx);
		//190322104799DS1已取消
		//xx=shopee.CancelOrder("https://partner.uat.shopeemobile.com/api", 101209, "68cd3ed8b40cb344676d32764ad39eb069d3935af467b09e4ab2390487a3d310", 214629,"190322104799DS1","CUSTOMER_REQUEST");
		////System.out.println(xx);

		String lastUpdatetime="20190813090150";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date tempdate = sdf.parse(lastUpdatetime);
		long lastTimestamp = tempdate.getTime()/1000;

		long lEndTimestamp=System.currentTimeMillis()/1000;

		//xx=shopee.GetLogisticsMessage("https://partner.shopeemobile.com/api", 842354, "63e3cda3d8dd96dd4b166e9599ff5802b59ca91ee3c9584e0119f4b38d5b4822", 418685, "19081114485U8KV", "905991758820");
		//System.out.println(xx);

		//19081114485U8KV
		//xx=shopee.GetReturnList("https://partner.shopeemobile.com/api", 842354, "63e3cda3d8dd96dd4b166e9599ff5802b59ca91ee3c9584e0119f4b38d5b4822", 418685,0, lastTimestamp, lEndTimestamp);
		//System.out.println(xx);

		//测试环境
		//xx=shopee.GetOrdersByStatus("https://partner.uat.shopeemobile.com/api", 101209, "68cd3ed8b40cb344676d32764ad39eb069d3935af467b09e4ab2390487a3d310", 214629,0,lastTimestamp,lEndTimestamp);
		//安琪儿正式
		//xx=shopee.GetOrdersByStatus("https://partner.shopeemobile.com/api", 842354, "63e3cda3d8dd96dd4b166e9599ff5802b59ca91ee3c9584e0119f4b38d5b4822", 418685,0,lastTimestamp,lEndTimestamp);

		////System.out.println(xx);

		String[] ordersn_list=new String[1];
		ordersn_list[0]="19081114485U8KV";	//190327152699F1C   190322150599DX1 190322100199DRS 190328180999FBP 190401120099FS7
		////测试环境
		//xx=shopee.GetOrderDetails("https://partner.uat.shopeemobile.com/api", 101209, "68cd3ed8b40cb344676d32764ad39eb069d3935af467b09e4ab2390487a3d310", 214629,ordersn_list);

		//安琪儿正式190424141850MFG/19042412005K9PM/19042410575EFFC/1904240857569JM/19042408345574N/190423200854NFP/190418202153YJY/1904241516557PF/19042110125GJ51
		//xx=shopee.GetOrderDetails("https://partner.shopeemobile.com/api", 842354, "63e3cda3d8dd96dd4b166e9599ff5802b59ca91ee3c9584e0119f4b38d5b4822", 418685,ordersn_list);

		//System.out.println(xx);

		////安琪儿正式
		//xx=shopee.GetEscrowDetails("https://partner.shopeemobile.com/api", 842354, "63e3cda3d8dd96dd4b166e9599ff5802b59ca91ee3c9584e0119f4b38d5b4822", 418685,"19052702145X48M");

		////System.out.println(xx);

		//xx=shopee.GetLogisticInfo("https://partner.uat.shopeemobile.com/api", 101209, "68cd3ed8b40cb344676d32764ad39eb069d3935af467b09e4ab2390487a3d310", 214629,"190402125599G5A");
		////System.out.println(xx);

		//xx=shopee.Init("https://partner.uat.shopeemobile.com/api", 101209, "68cd3ed8b40cb344676d32764ad39eb069d3935af467b09e4ab2390487a3d310", 214629,"190402125599G5A",2,0,"","",0,"andra");
		////System.out.println(xx);




		//String[] ordersn_list=new String[1];
		//ordersn_list[0]="190322150599DX1";
		//xx=shopee.GetAirwayBill("https://partner.shopeemobile.com/api", 842354, "63e3cda3d8dd96dd4b166e9599ff5802b59ca91ee3c9584e0119f4b38d5b4822", 418685,ordersn_list);
		////System.out.println(xx);

		//订单物流信息
		//xx=shopee.GetOrderLogistics("https://partner.shopeemobile.com/api", 842354, "63e3cda3d8dd96dd4b166e9599ff5802b59ca91ee3c9584e0119f4b38d5b4822", 418685,"190402125599G5A");
		//System.out.println(xx);


		Cvs cvs=new Cvs();

		//cvs.F01();//上传订单
		//cvs.F03();//大物流验收
		//cvs.F04();//进店档
		//cvs.F05();//取货完成档
		//cvs.F07();//大物流验退档
		//cvs.F09();//取消出货档
		//cvs.F17();//代收即时回复档
		//cvs.F20();//对账档
		//cvs.F21();//对账档
		//cvs.F44();//進店即時檔 
		//cvs.F45();//取消代收檔 
		//cvs.F61();//
		//cvs.F84();//
		//cvs.F11();//

		List<Map<String, Object>> OrderList=new ArrayList<Map<String,Object>>();

		Map<String, Object> mapOrder=new HashMap<String, Object>();
		mapOrder.put("ODNO", "123456");
		mapOrder.put("STNO", "000001");
		mapOrder.put("AMT", 0);
		mapOrder.put("CUTKNM", "张三");
		mapOrder.put("CUTKTL", "456");
		mapOrder.put("PRODNM", "0");
		mapOrder.put("ECWEB", "鼎捷软件 www.digiwin.com");
		mapOrder.put("ECSERTEL", "021-12345678");
		mapOrder.put("REALAMT", 180);
		mapOrder.put("TRADETYPE", 3);
		OrderList.add(mapOrder);

		mapOrder=new HashMap<String, Object>();
		mapOrder.put("ODNO", "AAAAA");
		mapOrder.put("STNO", "000001");
		mapOrder.put("AMT", 0);
		mapOrder.put("CUTKNM", "张三");
		mapOrder.put("CUTKTL", "456");
		mapOrder.put("PRODNM", "0");
		mapOrder.put("ECWEB", "鼎<捷软件 www.digiwin.com");
		mapOrder.put("ECSERTEL", "021-12345678");
		mapOrder.put("REALAMT", 180);
		mapOrder.put("TRADETYPE", 3);
		OrderList.add(mapOrder);

		//cvs.F10_OrderUpload("ftp://101.37.33.19", "101", "201", "301", OrderList);

		SevenEleven se=new SevenEleven();

		//订单上传
		JSONObject OrderDoc = new JSONObject();
		JSONArray Order = new JSONArray();

		//货运单多笔，多笔
		for (int a = 100; a < 102; a++)
		{
			JSONObject OrderBody = new JSONObject();
			OrderBody.put("OPMode", "A");//通路別 A 表示7-Eleven 
			OrderBody.put("EshopOrderNo", "HYD2019041000003");//eShop訂單編號 ，shipmentno出货单号
			OrderBody.put("EshopOrderDate", "2019-03-29");//eShop 訂單日期，出货单日期yyyy-mm-dd
			OrderBody.put("ServiceType", "3");//服務型態代碼， 付款取貨：1   取貨不付款：3 
			OrderBody.put("ShopperName", "金大爷");//購買人姓名，
			//OrderBody.put("ShopperPhone", "");//購買人電話，
			//OrderBody.put("ShopperMobilPhone", "");//購買人行動電，
			//OrderBody.put("ShopperEmail", "");//購買人 E-mail，
			OrderBody.put("ReceiverName", "金二爷");//收貨人姓名，
			//OrderBody.put("ReceiverPhone", "");//收貨人電話，
			OrderBody.put("ReceiverMobilPhone", "678");//收貨人行動電話，手機末三碼 (如 678) 
			OrderBody.put("ReceiverEmail", "jin@163.com");//收貨人 E-mail，
			//OrderBody.put("ReceiverIDNumber", "");//收貨人身分證字號 ，
			OrderBody.put("OrderAmount", a +"");//訂單總金額，請務必帶入金額 

			//货运单商品明细，多笔
			JSONArray OrderDetail = new JSONArray();
			for (int b = 0; b < 2; b++)
			{
				JSONObject OrderDetailBody = new JSONObject();
				OrderDetailBody.put("ProductId", "");//品項編號
				OrderDetailBody.put("ProductName", "");//品項名稱
				OrderDetailBody.put("Quantity", "");//數量
				OrderDetailBody.put("Unit", "");//單位
				OrderDetailBody.put("UnitPrice", "");//單價
				OrderDetail.put(OrderDetailBody);
			}
			OrderBody.put("OrderDetail", OrderDetail);

			//货运单
			JSONArray ShipmentDetail  = new JSONArray();
			JSONObject ShipmentDetailBody = new JSONObject();
			ShipmentDetailBody.put("ShipmentNo", "1000772964");//配送編號,物流单号
			ShipmentDetailBody.put("ShipDate", "2019-03-29");//出貨日期,yyyy-mm-dd 包裹需於 ShipDate 當日 14:00 前送達物流中心
			ShipmentDetailBody.put("ReturnDate", "2019-04-07");//門市店退貨日期 (為 ShipDate+8 天) 
			ShipmentDetailBody.put("LastShipment", "Y");//是否為本訂單的最 後一次出貨(Y/N) 
			ShipmentDetailBody.put("ShipmentAmount", "500");//出貨單金額 
			ShipmentDetailBody.put("StoreId", "011222");//門市店代碼 
			ShipmentDetailBody.put("EshopType", "04");//商品型態代,請固定帶入 04 				
			ShipmentDetail.put(ShipmentDetailBody);
			OrderBody.put("ShipmentDetail", ShipmentDetail);

			Order.put(OrderBody);
		}
		OrderDoc.put("Order",Order);

		String resbody=OrderDoc.toString();

		//se.SIN("101.37.33.19", "ABC", "DEF","20190515","ftptest","ftptest", resbody);

		//se.FILEOK(apiUrlTwo, "ABC", "DEF");
		//se.SRP(apiUrlTwo, "ABC", "DEF");
		//se.ETA(apiUrlTwo, "ABC", "DEF");
		//se.EIN(apiUrlTwo, "ABC", "DEF");
		//se.PPS(apiUrlTwo, "ABC", "DEF");
		//se.OL(apiUrlTwo, "ABC", "DEF");
		//se.ERT(apiUrlTwo, "ABC", "DEF");
		//se.EDR(apiUrlTwo, "ABC", "DEF");
		//se.EVR(apiUrlTwo, "ABC", "DEF");
		//se.ACC(apiUrlTwo, "ABC", "DEF");
		//se.ACTR(apiUrlTwo, "ABC", "DEF");


		JSONObject supOrderDoc = new JSONObject();
		JSONArray supOrder = new JSONArray();

		for (int a = 100; a < 102; a++)
		{
			JSONObject supOrderBody = new JSONObject();
			supOrderBody.put("expressno", "abc0001");//物流单号8码
			supOrderBody.put("adate", "20190409");//刷验日期 8码
			supOrderBody.put("amt", "00199");//5码
			supOrderBody.put("shipmentno", "              HYD2019040900001");//出货单号 30码左补空格
			supOrderBody.put("lastshipment", "Y");//最后一次出货
			supOrderBody.put("servicetype", "1");//服务类型 付款取貨：1 取貨不付款：3 
			supOrderBody.put("updateshopno", "999999");//更新后的门店编码
			supOrderBody.put("goodtype", "04");//商品型态,請固定帶入 04 	
			supOrder.put(supOrderBody);
		}
		supOrderDoc.put("suporder", supOrder);

		//上传更新档
		//se.SUP(apiUrlTwo, "ABC", "DEF",supOrderDoc.toString());

		//se.SURP(apiUrlTwo, "ABC", "DEF");

		/*
		File file = new File("c:\\json.txt");  
		//读
		FileInputStream  in = new FileInputStream(file); 
		InputStreamReader fr = new InputStreamReader(in,"utf-8");  
		BufferedReader br = new BufferedReader(fr); 

		String json="";
		String line;  
		while ((line=br.readLine()) != null)
		{  
			//UTF-8 BOM标记
			if(line.length()>0)
			{
				if((int)line.charAt(0)==65279)
				{
					json+=line.substring(1);
				}
				else
				{
					json+=line;
				}
			}

		}             

		br.close();  
		fr.close();  
		in.close();     

		////System.out.println("------------JSON-----------------\r\n" +json);

		//String xml=json2xml(json);

		////System.out.println("------------XML-----------------\r\n" + xml);
		 */


		Rakuten rt=new Rakuten();

		//上次拉取订单的时间戳
		SimpleDateFormat sdfpp = new SimpleDateFormat("yyyyMMddHHmmss");
		Date tempdatepp = sdfpp.parse(lastUpdatetime);
		sdfpp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String sNowpp=sdfpp.format(tempdatepp);
		//System.out.println(sNowpp);	


		Date nowRT = new Date();
		SimpleDateFormat sdfRT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String sNowRT=sdfRT.format(nowRT);
		//System.out.println(sNowRT);	
		String startDate=sNowRT;
		//
		nowRT.setDate(nowRT.getDate()-3);
		sNowRT=sdfRT.format(nowRT);
		//System.out.println(sNowRT);		
		String endDate=sNowRT;

		apiUrl="https://openapi-rms.global.rakuten.com/2.0";

		//xx=rt.Order_POST(apiUrl, "", "NnZqTGg3dERQVkxWaTN5ZDpReGlBN3l4Vm5lb3NKSGgz", "angelbaby", endDate,startDate,  0);
		////System.out.println(xx);

		rt.ScheduleShippingDate(apiUrlTwo, "AAAAA", "NnZqTGg3dERQVkxWaTN5ZDpReGlBN3l4Vm5lb3NKSGgz", "angelbaby", "123456789", "0001198-130906-0694004504", sNowpp);

		//xx=rt.UpdateTracknumber(apiUrl, "", "NnZqTGg3dERQVkxWaTN5ZDpReGlBN3l4Vm5lb3NKSGgz", "angelbaby", "0028587-190718-1309225864", "4963e247-199e-4413-a44f-30e5f129c29d", "黑貓宅急便", "905977141521");
		////System.out.println(xx);

		//
		xx=rt.OperationResponse(apiUrl, "", "NnZqTGg3dERQVkxWaTN5ZDpReGlBN3l4Vm5lb3NKSGgz", "88a08f31-8558-4b29-a6c5-9bd09c455efb");
		//System.out.println(xx);


		xx=rt.ConfirmShipping(apiUrl, "", "NnZqTGg3dERQVkxWaTN5ZDpReGlBN3l4Vm5lb3NKSGgz", "angelbaby", "0028587-190718-1309225864","4963e247-199e-4413-a44f-30e5f129c29d","Shipped");
		//System.out.println(xx);


		String sjson = "要写入的JSON字符串";
		String sfile = "D:\\1.SUP";
		FileOutputStream writerStream = new FileOutputStream(sfile);
		OutputStreamWriter osw=new OutputStreamWriter(writerStream, "UTF-8");
		BufferedWriter writer = new BufferedWriter(osw);
		writer.write(sjson);
		writer.newLine();
		writer.close();
		osw.close();
		writerStream.close();



	}




}
