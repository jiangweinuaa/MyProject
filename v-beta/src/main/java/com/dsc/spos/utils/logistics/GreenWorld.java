package com.dsc.spos.utils.logistics;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.dsc.spos.utils.HttpSend;

//台灣綠界物流接口
//目前逆物流无法主动查询状态，只能通过推送信息,根据RtnMerchantTradeNo绿界订单号关联AllPayLogisticsID

//C2C流程:產生物流單號

//********************************************
//********************************************
//B2C流程：產生物流單號

public class GreenWorld 
{

	/**
	 * 超取物流訂單產生 B2C/C2C,返回1|開頭成功
	 * @param apiUrl 
	 * @param MerchantID 會員商店代號
	 * @param ServerReplyURL 綠界物流狀態通知地址
	 * @param LogisticsC2CReplyURL 取貨門店更新通知地址，C2C方式7-11用
	 * @param 
	 * MerchantTradeNo訂單號
	 * MerchantTradeDate上傳日期 yyyy/MM/dd HH:mm:ss 
	 * LogisticsSubType物流子類型  
	 * ---B2C--- FAMI:全家 UNIMART:7-ELEVEN 超商 HILIFE:萊爾富 
	 * ---C2C--- FAMIC2C:全家店到店 UNIMARTC2C:7-ELEVEN 超商交貨便 HILIFEC2C:萊爾富店到店 
	 * GoodsAmount商品金額，1-20000	
	 * IsCollection是否代收貨款
	 * CollectionAmount代收金額
	 * GoodsName商品名稱
	 * SenderName寄件人姓名
	 * SenderPhone寄件人電話
	 * SenderCellPhone寄件人手機
	 * ReceiverName收件人
	 * ReceiverPhone收件人電話
	 * ReceiverCellPhone收件人手機
	 * ReceiverEmail收件人email	
	 * Remark備註
	 * ReceiverStoreID取貨門店
	 * @return
	 */
	public String ExpressCreate_CVS(String apiUrl,String MerchantID,String HashKey,String HashIV, String ServerReplyURL,String LogisticsC2CReplyURL, Map<String, Object> oneData)
	{	
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Express/Create";
		}
		else			
		{
			apiUrl+="/Express/Create";
		}

		String result="";

		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);
			maps.put("ServerReplyURL", ServerReplyURL);
			maps.put("LogisticsC2CReplyURL", LogisticsC2CReplyURL);

			//绿界会产生
			//String MerchantTradeNo= oneData.get("MerchantTradeNo").toString();//訂單編號	
			//maps.put("MerchantTradeNo", MerchantTradeNo);

			String MerchantTradeDate= oneData.get("MerchantTradeDate").toString();//訂單日期 2019/08/02 17:37:05
			maps.put("MerchantTradeDate", MerchantTradeDate);

			String LogisticsType= oneData.get("LogisticsType").toString();//物流類型  CVS:超商取貨 Home:宅配  
			maps.put("LogisticsType", LogisticsType);
			//物流子類型  
			//---B2C--- FAMI:全家 UNIMART:7-ELEVEN 超商 HILIFE:萊爾富 
			//---C2C--- FAMIC2C:全家店到店 UNIMARTC2C:7-ELEVEN 超商交貨便 HILIFEC2C:萊爾富店到店 
			//---HOME--- TCAT:黑貓 ECAN:宅配通 
			String LogisticsSubType= oneData.get("LogisticsSubType").toString();
			maps.put("LogisticsSubType", LogisticsSubType);

			String GoodsAmount= oneData.get("GoodsAmount").toString();//商品金額，1-20000	
			maps.put("GoodsAmount", GoodsAmount);

			String IsCollection= oneData.get("IsCollection").toString();//是否代收貨款
			maps.put("IsCollection", IsCollection);

			String CollectionAmount= oneData.get("CollectionAmount").toString();//代收金額
			maps.put("CollectionAmount", CollectionAmount);

			String GoodsName= oneData.get("GoodsName").toString();//商品名稱
			maps.put("GoodsName", GoodsName);

			String SenderName= oneData.get("SenderName").toString();//寄件人姓名
			maps.put("SenderName", SenderName);

			String SenderPhone= oneData.get("SenderPhone").toString();//寄件人電話
			maps.put("SenderPhone", SenderPhone);

			String SenderCellPhone= oneData.get("SenderCellPhone").toString();//寄件人手機
			maps.put("SenderCellPhone", SenderCellPhone);

			String ReceiverName= oneData.get("ReceiverName").toString();//收件人
			maps.put("ReceiverName", ReceiverName);			

			String ReceiverPhone= oneData.get("ReceiverPhone").toString();//收件人電話
			maps.put("ReceiverPhone", ReceiverPhone);

			String ReceiverCellPhone= oneData.get("ReceiverCellPhone").toString();//收件人手機
			
			maps.put("ReceiverCellPhone", ReceiverCellPhone);

			String ReceiverEmail= oneData.get("ReceiverEmail").toString();//收件人email		
			maps.put("ReceiverEmail", ReceiverEmail);			

			String Remark = oneData.get("Remark").toString();//備註
			maps.put("Remark", Remark);


			String ReceiverStoreID= oneData.get("ReceiverStoreID").toString();//取貨門店
			maps.put("ReceiverStoreID", ReceiverStoreID);




			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("ExpressCreate_CVS", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception ex) 
		{
			//System.out.println(ex.getMessage());
		}

		return result;
	}

	/**
	 * 宅配物流訂單產生，黑貓+宅配通，,返回1|開頭成功
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param ServerReplyURL
	 * @param oneData
	 * MerchantTradeNo訂單號
	 * MerchantTradeDate上傳日期 yyyy/MM/dd HH:mm:ss 
	 * LogisticsSubType物流子類型  
	 * TCAT:黑貓 ECAN:宅配通 
	 * GoodsAmount商品金額，1-20000	
	 * IsCollection是否代收貨款
	 * CollectionAmount代收金額
	 * GoodsName商品名稱
	 * SenderName寄件人姓名
	 * SenderPhone寄件人電話
	 * SenderCellPhone寄件人手機
	 * ReceiverName收件人
	 * ReceiverPhone收件人電話
	 * ReceiverCellPhone收件人手機
	 * ReceiverEmail收件人email	
	 * Remark備註
	 * SenderZipCode寄件人郵編
	 * SenderAddress寄件人地址
	 * ReceiverZipCode收件人郵編
	 * ReceiverAddress收件人地址
	 * Temperature溫層
	 * Distance距離代碼
	 * Specification規格尺寸
	 * ScheduledPickupTime取貨的時段
	 * ScheduledDeliveryDate指定配達日yyyy/MM/dd
	 * PackageCount 包裝件數
	 * @return
	 */
	public String ExpressCreate_Home(String apiUrl,String MerchantID,String HashKey,String HashIV, String ServerReplyURL, Map<String, Object> oneData)
	{	
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Express/Create";
		}
		else			
		{
			apiUrl+="/Express/Create";
		}

		String result="";

		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);
			maps.put("ServerReplyURL", ServerReplyURL);

			//String MerchantTradeNo= oneData.get("MerchantTradeNo").toString();//訂單編號	
			//maps.put("MerchantTradeNo", MerchantTradeNo);

			String MerchantTradeDate= oneData.get("MerchantTradeDate").toString();//訂單日期 2019/08/02 17:37:05
			maps.put("MerchantTradeDate", MerchantTradeDate);

			//String LogisticsType= oneData.get("LogisticsType").toString();//物流類型  CVS:超商取貨 Home:宅配  
			maps.put("LogisticsType", "Home");
			//物流子類型  
			//---B2C--- FAMI:全家 UNIMART:7-ELEVEN 超商 HILIFE:萊爾富 
			//---C2C--- FAMIC2C:全家店到店 UNIMARTC2C:7-ELEVEN 超商交貨便 HILIFEC2C:萊爾富店到店 
			//---HOME--- TCAT:黑貓 ECAN:宅配通 
			String LogisticsSubType= oneData.get("LogisticsSubType").toString();
			maps.put("LogisticsSubType", LogisticsSubType);

			String GoodsAmount= oneData.get("GoodsAmount").toString();//商品金額，1-20000	
			maps.put("GoodsAmount", GoodsAmount);

			//(目前宅配不支援代收貨款，所以 LogisticsType 為 Home 時，請勿帶 Y) 
			//String IsCollection= oneData.get("IsCollection").toString();//是否代收貨款
			maps.put("IsCollection", "N");

			//String CollectionAmount= oneData.get("CollectionAmount").toString();//代收金額
			maps.put("CollectionAmount", 0);

			String GoodsName= oneData.get("GoodsName").toString();//商品名稱
			maps.put("GoodsName", GoodsName);

			String SenderName= oneData.get("SenderName").toString();//寄件人姓名
			maps.put("SenderName", SenderName);

			String SenderPhone= oneData.get("SenderPhone").toString();//寄件人電話
			maps.put("SenderPhone", SenderPhone);

			String SenderCellPhone= oneData.get("SenderCellPhone").toString();//寄件人手機
			maps.put("SenderCellPhone", SenderCellPhone);

			String ReceiverName= oneData.get("ReceiverName").toString();//收件人
			maps.put("ReceiverName", ReceiverName);			

			String ReceiverPhone= oneData.get("ReceiverPhone").toString();//收件人電話
			maps.put("ReceiverPhone", ReceiverPhone);

			String ReceiverCellPhone= oneData.get("ReceiverCellPhone").toString();//收件人手機
			maps.put("ReceiverCellPhone", ReceiverCellPhone);

			String ReceiverEmail= oneData.get("ReceiverEmail").toString();//收件人email		
			maps.put("ReceiverEmail", ReceiverEmail);			

			String Remark = oneData.get("Remark").toString();//備註
			maps.put("Remark", Remark);			

			String SenderZipCode= oneData.get("SenderZipCode").toString();//寄件人郵編
			maps.put("SenderZipCode", SenderZipCode);

			String SenderAddress= oneData.get("SenderAddress").toString();//寄件人地址
			maps.put("SenderAddress", SenderAddress);

			String ReceiverZipCode= oneData.get("ReceiverZipCode").toString();//收件人郵編
			maps.put("ReceiverZipCode", ReceiverZipCode);

			String ReceiverAddress= oneData.get("ReceiverAddress").toString();//收件人地址
			maps.put("ReceiverAddress", ReceiverAddress);
			//0001:常溫 (預設值) 0002:冷藏 0003:冷凍 
			//※注意事項 物流子類型 ECAN 宅配通 Temperature 溫 層只能用 0001 常溫
			String Temperature= oneData.get("Temperature").toString();//溫層
			if (Temperature.equals("")) 
			{
				Temperature="0001";
			}
			maps.put("Temperature", Temperature);
			//00:同縣市 (預設值) 01:外縣市  02:離島 
			String Distance= oneData.get("Distance").toString();//距離代碼
			if (Distance.equals("")) 
			{
				Distance="00";
			}
			maps.put("Distance", Distance);			
			//0001: 60cm (預設值)  0002: 90cm    0003: 120cm   0004: 150cm  注意事項: 溫層選擇 0003:冷凍時，規格不包含 0004:150cm選項  
			String Specification= oneData.get("Specification").toString();//規格尺寸
			if (Temperature.equals("0003")) 
			{
				if (Specification.equals("0004")) 
				{
					Specification="0001";
				}
			}
			if (Specification.equals("")) 
			{
				Specification="0001";
			}
			maps.put("Specification", Specification);			

			if (LogisticsSubType.equals("ECAN")) 
			{
				//取件時段 
				//宅配物流商預定取貨的時段 1: 9~12 2: 12~17 3: 17~20  4: 不限時(固定 4 不限時) (當子物流選擇宅配通時，該參數可不填) 
				String ScheduledPickupTime= oneData.get("ScheduledPickupTime").toString();//規格尺寸

				if (ScheduledPickupTime.equals("")) 
				{
					ScheduledPickupTime="4";
				}

				maps.put("ScheduledPickupTime", ScheduledPickupTime);

			}

			//當子物流選擇宅配通時，此參數才有作用。
			//注意事項: 日期指定限制 D+3 (D:該訂單建立時間) 			 
			String ScheduledDeliveryDate= oneData.get("ScheduledDeliveryDate").toString();//指定配達日yyyy/MM/dd
			if (ScheduledDeliveryDate.equals("")==false) 
			{
				maps.put("ScheduledDeliveryDate", ScheduledDeliveryDate);
			}			

			//當子物流選擇宅配通時，此參數才有作 用，作用於同訂單編號，包裹件數。 
			String PackageCount= oneData.get("PackageCount").toString();//包裝件數
			if (PackageCount.equals("")) 
			{
				PackageCount="1";
			}
			maps.put("PackageCount", PackageCount);

			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("ExpressCreate_Home", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception ex) 
		{
			//System.out.println(ex.getMessage());
		}

		return result;
	}



	/**
	 * C2C 7-11超商列印缴款单=小白单，返回htmlcode
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param AllPayLogisticsID
	 * @param CVSPaymentNo
	 * @param CVSValidationNo
	 * @return
	 */
	public String C2C_Print711OrderInfo(String apiUrl,String MerchantID,String HashKey,String HashIV,String AllPayLogisticsID,String CVSPaymentNo,String CVSValidationNo)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Express/PrintUniMartC2COrderInfo";
		}
		else			
		{
			apiUrl+="/Express/PrintUniMartC2COrderInfo";
		}

		String result="";
		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
			maps.put("MerchantID", MerchantID);
			maps.put("AllPayLogisticsID", AllPayLogisticsID);
			maps.put("CVSPaymentNo", CVSPaymentNo);
			maps.put("CVSValidationNo", CVSValidationNo);

			result=	genHtmlCode(apiUrl, HashKey, HashIV, maps, true);

		}
		catch (Exception e) 
		{
			result="";
		}

		return  result;
	}


	/**
	 * C2C 全家超商打印小白单，返回htmlcode
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param AllPayLogisticsID
	 * @param CVSPaymentNo
	 * @return
	 */
	public String C2C_PrintFamiOrderInfo(String apiUrl,String MerchantID,String HashKey,String HashIV,String AllPayLogisticsID,String CVSPaymentNo)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Express/PrintFAMIC2COrderInfo";
		}
		else			
		{
			apiUrl+="/Express/PrintFAMIC2COrderInfo";
		}

		String result="";
		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
			maps.put("MerchantID", MerchantID);
			maps.put("AllPayLogisticsID", AllPayLogisticsID);
			maps.put("CVSPaymentNo", CVSPaymentNo);

			result=	genHtmlCode(apiUrl, HashKey, HashIV, maps, true);

		}
		catch (Exception e) 
		{
			result="";
		}

		return  result;
	}

	/**
	 * C2C 萊而富超商打印小白單，返回htmlcode
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param AllPayLogisticsID
	 * @param CVSPaymentNo
	 * @return
	 */
	public String C2C_PrintHilifeOrderInfo(String apiUrl,String MerchantID,String HashKey,String HashIV,String AllPayLogisticsID,String CVSPaymentNo)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Express/PrintHILIFEC2COrderInfo";
		}
		else			
		{
			apiUrl+="/Express/PrintHILIFEC2COrderInfo";
		}

		String result="";
		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
			maps.put("MerchantID", MerchantID);
			maps.put("AllPayLogisticsID", AllPayLogisticsID);
			maps.put("CVSPaymentNo", CVSPaymentNo);

			result=	genHtmlCode(apiUrl, HashKey, HashIV, maps, true);

		}
		catch (Exception e) 
		{
			result="";
		}

		return  result;
	}


	/**
	 * C2C 7-11更新取貨門店/退件門店，返回1|OK表示成功
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param AllPayLogisticsID
	 * @param CVSPaymentNo
	 * @param CVSValidationNo
	 * @param StoreType 01：取件門市更新 	02：退件門市更新
	 * @param ReceiverStoreID 門店
	 * @return
	 */
	public String C2C_Print711UpdateStoreInfo(String apiUrl,String MerchantID,String HashKey,String HashIV,String AllPayLogisticsID,String CVSPaymentNo,String CVSValidationNo,String StoreType,String ReceiverStoreID)
	{

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Express/UpdateStoreInfo";
		}
		else			
		{
			apiUrl+="/Express/UpdateStoreInfo";
		}

		String result="";


		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);

			maps.put("AllPayLogisticsID", AllPayLogisticsID);
			maps.put("CVSPaymentNo", CVSPaymentNo);
			maps.put("CVSValidationNo", CVSValidationNo);
			//01：取件門市更新 	02：退件門市更新
			maps.put("StoreType", StoreType);			
			maps.put("ReceiverStoreID", ReceiverStoreID);

			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("C2C_Print711UpdateStoreInfo", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception e) 
		{
			result="";
		}		

		return result;

	}


	/**
	 * C2C 7-11取消物流單,返回1|OK表示成功
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param AllPayLogisticsID
	 * @param CVSPaymentNo
	 * @param CVSValidationNo
	 * @return
	 */
	public String C2C_Print711CancelOrderInfo(String apiUrl,String MerchantID,String HashKey,String HashIV,String AllPayLogisticsID,String CVSPaymentNo,String CVSValidationNo)
	{

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Express/CancelC2COrder";
		}
		else			
		{
			apiUrl+="/Express/CancelC2COrder";
		}

		String result="";


		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);

			maps.put("AllPayLogisticsID", AllPayLogisticsID);
			maps.put("CVSPaymentNo", CVSPaymentNo);
			maps.put("CVSValidationNo", CVSValidationNo);

			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("C2C_Print711CancelOrderInfo", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception e) 
		{
			result="";
		}		

		return result;

	}


	/**
	 * 查詢物流單，0|表示失敗,正確返回 參數=值...
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param AllPayLogisticsID
	 * @return
	 */
	public String QueryLogisticsTradeInfo(String apiUrl,String MerchantID,String HashKey,String HashIV,String AllPayLogisticsID)
	{

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Helper/QueryLogisticsTradeInfo/V2";
		}
		else			
		{
			apiUrl+="/Helper/QueryLogisticsTradeInfo/V2";
		}

		String result="";


		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);

			maps.put("AllPayLogisticsID", AllPayLogisticsID);

			maps.put("TimeStamp", genUnixTimeStamp());

			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("QueryLogisticsTradeInfo", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception e) 
		{
			result="";
		}		

		return result;

	}




	/**
	 * 超商選取貨門店，返回htmlcode
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param LogisticsSubType 
	 * ---B2C--- FAMI：全家 UNIMART：7-ELEVEN 超商 HILIFE：萊爾富 
	 * ---C2C--- FAMIC2C：全家店到店 UNIMARTC2C：7-ELEVEN 超商交貨便 HILIFEC2C:萊爾富店到店 
	 * @param IsCollection N：不代收貨款。 Y：代收貨款。 
	 * @param ServerReplyURL 中台處理地址 
	 * @param ExtraData 由 订单号@@@企业编码@@@门店编号@@@时间戳  组成
	 * @param Device 0：PC 1：Mobile 
	 * @return
	 */
	public String ExpressMapInfo(String apiUrl,String MerchantID,String HashKey,String HashIV,String LogisticsSubType,String IsCollection,String ServerReplyURL,String ExtraData,String Device)
	{

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Express/map";
		}
		else			
		{
			apiUrl+="/Express/map";
		}

		String result="";


		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);

			maps.put("LogisticsType", "CVS");

			maps.put("LogisticsSubType", LogisticsSubType);

			maps.put("IsCollection", IsCollection);

			maps.put("ServerReplyURL", ServerReplyURL);

			maps.put("ExtraData", ExtraData);

			maps.put("Device", Device);

			/*
			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("ExpressMapInfo", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  
			*/
			
			result=	genHtmlCode(apiUrl, HashKey, HashIV, maps, true);

		} 
		catch (Exception e) 
		{
			result="";
		}		

		return result;

	}

	
	
	/**
	 * 產生宅配托運單和B2C托運單格式，htmlcode，宅配不支持批量，超商支持批量(僅限同一種超商類型)
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param AllPayLogisticsID 同一種超商類型批量，例如：10011,10012 
	 * @return
	 */
	public String B2C_Home_PrintTradeDocInfo(String apiUrl,String MerchantID,String HashKey,String HashIV,String AllPayLogisticsID)
	{
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="helper/printTradeDocument";
		}
		else			
		{
			apiUrl+="/helper/printTradeDocument";
		}

		String result="";

		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);

			maps.put("AllPayLogisticsID", AllPayLogisticsID);


			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("B2C_Home_PrintTradeDocInfo", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception e) 
		{
			result="";
		}		

		return result;

	}


	/**
	 * B2C 7-11修改出货日期、取货门店,當 7-ELEVEN 貨態為 2037 門市關轉時，才能變更取貨門市,返回1|OK表示成功
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param AllPayLogisticsID
	 * @param ShipmentDate 出货日期 2014/01/01 
	 * @param ReceiverStoreID 取货门店
	 * @return
	 */
	public String B2C_Update711Shop_Date_Info(String apiUrl,String MerchantID,String HashKey,String HashIV,String AllPayLogisticsID,String ShipmentDate,String ReceiverStoreID)
	{

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Helper/UpdateShipmentInfo";
		}
		else			
		{
			apiUrl+="/Helper/UpdateShipmentInfo";
		}

		String result="";


		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);

			maps.put("AllPayLogisticsID", AllPayLogisticsID);
			maps.put("ShipmentDate", ShipmentDate);//2014/01/01 
			maps.put("ReceiverStoreID", ReceiverStoreID);

			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("B2C_Update711Shop_Date_Info", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception e) 
		{
			result="";
		}		

		return result;

	}



	/**
	 * 宅配物流訂單產生，黑貓+宅配通，,返回1|開頭成功
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param ServerReplyURL
	 * @param oneData
	 * LogisticsSubType物流子類型  
	 * TCAT:黑貓 ECAN:宅配通 
	 * GoodsAmount商品金額，1-20000	
	 * GoodsName商品名稱
	 * SenderName寄件人姓名
	 * SenderPhone寄件人電話
	 * SenderCellPhone寄件人手機
	 * ReceiverName收件人
	 * ReceiverPhone收件人電話
	 * ReceiverCellPhone收件人手機
	 * ReceiverEmail收件人email	
	 * Remark備註
	 * SenderZipCode寄件人郵編
	 * SenderAddress寄件人地址
	 * ReceiverZipCode收件人郵編
	 * ReceiverAddress收件人地址
	 * Temperature溫層
	 * Distance距離代碼
	 * Specification規格尺寸
	 * ScheduledPickupTime取貨的時段
	 * ScheduledDeliveryDate指定配達日yyyy/MM/dd
	 * PackageCount 包裝件數
	 * @return
	 */
	public String ExpressReturn_Home(String apiUrl,String MerchantID,String HashKey,String HashIV, String ServerReplyURL, Map<String, Object> oneData)
	{	
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Express/ReturnHome";
		}
		else			
		{
			apiUrl+="/Express/ReturnHome";
		}

		String result="";

		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);
			maps.put("ServerReplyURL", ServerReplyURL);

			String AllPayLogisticsID= oneData.get("AllPayLogisticsID").toString();
			maps.put("AllPayLogisticsID", AllPayLogisticsID);
			//物流子類型  
			//---HOME--- TCAT:黑貓 ECAN:宅配通 
			String LogisticsSubType= oneData.get("LogisticsSubType").toString();
			maps.put("LogisticsSubType", LogisticsSubType);

			String GoodsAmount= oneData.get("GoodsAmount").toString();//商品金額，1-20000	
			maps.put("GoodsAmount", GoodsAmount);

			String GoodsName= oneData.get("GoodsName").toString();//商品名稱
			maps.put("GoodsName", GoodsName);

			String SenderName= oneData.get("SenderName").toString();//寄件人姓名
			maps.put("SenderName", SenderName);

			String SenderPhone= oneData.get("SenderPhone").toString();//寄件人電話
			maps.put("SenderPhone", SenderPhone);

			String SenderCellPhone= oneData.get("SenderCellPhone").toString();//寄件人手機
			maps.put("SenderCellPhone", SenderCellPhone);

			String ReceiverName= oneData.get("ReceiverName").toString();//收件人
			maps.put("ReceiverName", ReceiverName);			

			String ReceiverPhone= oneData.get("ReceiverPhone").toString();//收件人電話
			maps.put("ReceiverPhone", ReceiverPhone);

			String ReceiverCellPhone= oneData.get("ReceiverCellPhone").toString();//收件人手機
			maps.put("ReceiverCellPhone", ReceiverCellPhone);

			String ReceiverEmail= oneData.get("ReceiverEmail").toString();//收件人email		
			maps.put("ReceiverEmail", ReceiverEmail);			

			String Remark = oneData.get("Remark").toString();//備註
			maps.put("Remark", Remark);			

			String SenderZipCode= oneData.get("SenderZipCode").toString();//寄件人郵編
			maps.put("SenderZipCode", SenderZipCode);

			String SenderAddress= oneData.get("SenderAddress").toString();//寄件人地址
			maps.put("SenderAddress", SenderAddress);

			String ReceiverZipCode= oneData.get("ReceiverZipCode").toString();//收件人郵編
			maps.put("ReceiverZipCode", ReceiverZipCode);

			String ReceiverAddress= oneData.get("ReceiverAddress").toString();//收件人地址
			maps.put("ReceiverAddress", ReceiverAddress);
			//0001:常溫 (預設值) 0002:冷藏 0003:冷凍 
			//※注意事項 物流子類型 ECAN 宅配通 Temperature 溫 層只能用 0001 常溫
			String Temperature= oneData.get("Temperature").toString();//溫層
			if (Temperature.equals("")) 
			{
				Temperature="0001";
			}
			maps.put("Temperature", Temperature);
			//00:同縣市 (預設值) 01:外縣市  02:離島 
			String Distance= oneData.get("Distance").toString();//距離代碼
			if (Distance.equals("")) 
			{
				Distance="00";
			}
			maps.put("Distance", Distance);			
			//0001: 60cm (預設值)  0002: 90cm    0003: 120cm   0004: 150cm  注意事項: 溫層選擇 0003:冷凍時，規格不包含 0004:150cm選項  
			String Specification= oneData.get("Specification").toString();//規格尺寸
			if (Temperature.equals("0003")) 
			{
				if (Specification.equals("0004")) 
				{
					Specification="0001";
				}
			}
			if (Specification.equals("")) 
			{
				Specification="0001";
			}
			maps.put("Specification", Specification);			

			if (LogisticsSubType.equals("ECAN")) 
			{
				//取件時段 
				//宅配物流商預定取貨的時段 1: 9~12 2: 12~17 3: 17~20  4: 不限時(固定 4 不限時) (當子物流選擇宅配通時，該參數可不填) 
				String ScheduledPickupTime= oneData.get("ScheduledPickupTime").toString();//規格尺寸

				if (ScheduledPickupTime.equals("")) 
				{
					ScheduledPickupTime="4";
				}

				maps.put("ScheduledPickupTime", ScheduledPickupTime);

			}

			//當子物流選擇宅配通時，此參數才有作用。
			//注意事項: 日期指定限制 D+3 (D:該訂單建立時間) 			 
			String ScheduledDeliveryDate= oneData.get("ScheduledDeliveryDate").toString();//指定配達日yyyy/MM/dd
			if (ScheduledDeliveryDate.equals("")==false) 
			{
				maps.put("ScheduledDeliveryDate", ScheduledDeliveryDate);
			}			

			//當子物流選擇宅配通時，此參數才有作 用，作用於同訂單編號，包裹件數。 
			String PackageCount= oneData.get("PackageCount").toString();//包裝件數
			if (PackageCount.equals("")) 
			{
				PackageCount="1";
			}
			maps.put("PackageCount", PackageCount);

			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("ExpressReturn_Home", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception ex) 
		{
			//System.out.println(ex.getMessage());
		}

		return result;
	}


	/**
	 * 全家 B2C超取产生逆物流单,返回RtnMerchantTradeNo|RtnOrderNo表示成功；|ErrorMessage 表示失败
	 * @param apiUrl 
	 * @param MerchantID 會員商店代號
	 * @param ServerReplyURL 綠界物流狀態通知地址
	 * @param 
	 * AllPayLogisticsID
	 * GoodsAmount商品金額，1-20000	
	 * GoodsName商品名稱
	 * SenderName寄件人姓名
	 * SenderPhone寄件人電話
	 * Remark備註
	 * @return
	 */
	public String B2C_ExpressReturnFami_CVS(String apiUrl,String MerchantID,String HashKey,String HashIV, String ServerReplyURL, Map<String, Object> oneData)
	{	
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="express/ReturnCVS";
		}
		else			
		{
			apiUrl+="/express/ReturnCVS";
		}

		String result="";

		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);
			maps.put("ServerReplyURL", ServerReplyURL);

			String AllPayLogisticsID= oneData.get("AllPayLogisticsID").toString();//
			maps.put("AllPayLogisticsID", AllPayLogisticsID);		

			String GoodsAmount= oneData.get("GoodsAmount").toString();//商品金額，1-20000	
			maps.put("GoodsAmount", GoodsAmount);

			//
			maps.put("CollectionAmount", 0);//代收金額

			//
			maps.put("ServiceType", 4);//4:退貨不付款

			String GoodsName= oneData.get("GoodsName").toString();//商品名稱
			maps.put("GoodsName", GoodsName);

			String SenderName= oneData.get("SenderName").toString();//寄件人姓名
			maps.put("SenderName", SenderName);

			String SenderPhone= oneData.get("SenderPhone").toString();//寄件人電話
			maps.put("SenderPhone", SenderPhone);				

			String Remark = oneData.get("Remark").toString();//備註
			maps.put("Remark", Remark);

			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("B2C_ExpressReturnFami_CVS", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception ex) 
		{
			//System.out.println(ex.getMessage());
		}

		return result;
	}


	/**
	 * B2C全家逆物流核账,當廠商收到退貨商品時，透過此 API 核對退貨商品，更新逆物流状态为[退貨已取]，1|OK表示成功
	 * @param apiUrl
	 * @param MerchantID
	 * @param HashKey
	 * @param HashIV
	 * @param RtnMerchantTradeNo 原订单编号
	 * @return
	 */
	public String B2C_LogisticsCheckAccountsFami(String apiUrl,String MerchantID,String HashKey,String HashIV,String RtnMerchantTradeNo)
	{

		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="Helper/LogisticsCheckAccoounts";
		}
		else			
		{
			apiUrl+="/Helper/LogisticsCheckAccoounts";
		}

		String result="";


		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);

			maps.put("RtnMerchantTradeNo", RtnMerchantTradeNo);
			
			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("B2C_LogisticsCheckAccountsFami", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception e) 
		{
			result="";
		}		

		return result;

	}


	/**
	 * 萊而富 B2C超取产生逆物流单,返回RtnMerchantTradeNo|RtnOrderNo表示成功；|ErrorMessage 表示失败
	 * @param apiUrl 
	 * @param MerchantID 會員商店代號
	 * @param ServerReplyURL 綠界物流狀態通知地址
	 * @param 
	 * AllPayLogisticsID
	 * GoodsAmount商品金額，1-20000	
	 * GoodsName商品名稱
	 * SenderName寄件人姓名
	 * SenderPhone寄件人電話
	 * Remark備註
	 * @return
	 */
	public String B2C_ExpressReturnHiLife_CVS(String apiUrl,String MerchantID,String HashKey,String HashIV, String ServerReplyURL, Map<String, Object> oneData)
	{	
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="express/ReturnHiLifeCVS";
		}
		else			
		{
			apiUrl+="/express/ReturnHiLifeCVS";
		}

		String result="";

		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);
			maps.put("ServerReplyURL", ServerReplyURL);

			String AllPayLogisticsID= oneData.get("AllPayLogisticsID").toString();//
			maps.put("AllPayLogisticsID", AllPayLogisticsID);		

			String GoodsAmount= oneData.get("GoodsAmount").toString();//商品金額，1-20000	
			maps.put("GoodsAmount", GoodsAmount);

			//
			maps.put("CollectionAmount", 0);//代收金額

			//
			maps.put("ServiceType", 4);//4:退貨不付款

			String GoodsName= oneData.get("GoodsName").toString();//商品名稱
			maps.put("GoodsName", GoodsName);

			String SenderName= oneData.get("SenderName").toString();//寄件人姓名
			maps.put("SenderName", SenderName);

			String SenderPhone= oneData.get("SenderPhone").toString();//寄件人電話
			maps.put("SenderPhone", SenderPhone);				

			String Remark = oneData.get("Remark").toString();//備註
			maps.put("Remark", Remark);

			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("B2C_ExpressReturnHiLife_CVS", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception ex) 
		{
			//System.out.println(ex.getMessage());
		}

		return result;
	}
	
	
	/**
	 * 7-11 B2C超取产生逆物流单,返回RtnMerchantTradeNo|RtnOrderNo表示成功；|ErrorMessage 表示失败
	 * @param apiUrl 
	 * @param MerchantID 會員商店代號
	 * @param ServerReplyURL 綠界物流狀態通知地址
	 * @param 
	 * AllPayLogisticsID
	 * GoodsAmount商品金額，1-20000	
	 * GoodsName商品名稱
	 * SenderName寄件人姓名
	 * SenderPhone寄件人電話
	 * Remark備註
	 * @return
	 */
	public String B2C_ExpressReturn711_CVS(String apiUrl,String MerchantID,String HashKey,String HashIV, String ServerReplyURL, Map<String, Object> oneData)
	{	
		if(apiUrl.endsWith("/"))
		{			
			apiUrl+="express/ReturnUniMartCVS";
		}
		else			
		{
			apiUrl+="/express/ReturnUniMartCVS";
		}

		String result="";

		try 
		{
			//排序字段處理
			Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);

			String content="";

			maps.put("MerchantID", MerchantID);
			maps.put("ServerReplyURL", ServerReplyURL);

			String AllPayLogisticsID= oneData.get("AllPayLogisticsID").toString();//
			maps.put("AllPayLogisticsID", AllPayLogisticsID);		

			String GoodsAmount= oneData.get("GoodsAmount").toString();//商品金額，1-20000	
			maps.put("GoodsAmount", GoodsAmount);

			//
			maps.put("CollectionAmount", 0);//代收金額

			//
			maps.put("ServiceType", 4);//4:退貨不付款

			String GoodsName= oneData.get("GoodsName").toString();//商品名稱
			maps.put("GoodsName", GoodsName);

			String SenderName= oneData.get("SenderName").toString();//寄件人姓名
			maps.put("SenderName", SenderName);

			String SenderPhone= oneData.get("SenderPhone").toString();//寄件人電話
			maps.put("SenderPhone", SenderPhone);				

			String Remark = oneData.get("Remark").toString();//備註
			maps.put("Remark", Remark);

			Iterator it = maps.entrySet().iterator();  
			while (it.hasNext()) 
			{  
				// entry的输出结果如key0=value0等  
				Map.Entry entry =(Map.Entry) it.next();  
				Object key = entry.getKey();  
				Object value=entry.getValue();  

				//System.out.println(key +"=" + value);  

				content=content + '&' +key+"=" + value;
			}  

			//數據組合
			String data=content.substring(1);
			//System.out.println(data);  

			//轉碼
			String urlEncode= URLEncoder.encode("HashKey=" + HashKey + content+"&HashIV=" +HashIV, "UTF-8").toLowerCase();;
			urlEncode = netUrlEncode(urlEncode);
			//System.out.println(urlEncode);  

			//檢查碼
			String CheckMacValue=hash(urlEncode.getBytes(), "MD5");
			//System.out.println(CheckMacValue);  
			//最終結果
			String urlParameters= data + "&CheckMacValue=" + CheckMacValue;
			//System.out.println(urlParameters);  

			result=HttpSend.SendGreeWorld("B2C_ExpressReturn711_CVS", apiUrl, urlParameters, "UTF-8");
			//System.out.println(result);  

		} 
		catch (Exception ex) 
		{
			//System.out.println(ex.getMessage());
		}

		return result;
	}
	
	


	/**
	 * 將做完的urlEncode字串做轉換符合 .NET語言的轉換規則
	 * @param url
	 * @return .Net url encoded string
	 */
	private static String netUrlEncode(String url)
	{
		String netUrlEncode = url.replaceAll("%21", "\\!").replaceAll("%28", "\\(").replaceAll("%29", "\\)");
		return netUrlEncode;
	}

	/**
	 * 將 byte array 資料做 hash md5或 sha256 運算，並回傳 hex值的字串資料
	 * @param data
	 * @param isMD5
	 * @return string
	 */
	private String hash(byte data[], String mode)
	{
		MessageDigest md = null;
		try
		{
			if(mode == "MD5")
			{
				md = MessageDigest.getInstance("MD5");
			}
			else if(mode == "SHA-256")
			{
				md = MessageDigest.getInstance("SHA-256");
			}
		} 
		catch(NoSuchAlgorithmException e)
		{

		}

		byte[] bytes=md.digest(data);

		md = null;

		return bytesToHex(bytes);
	}

	private char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * 將 byte array 資料轉換成 hex字串值
	 * @param bytes
	 * @return string
	 */
	private String bytesToHex(byte[] bytes) 
	{
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) 
		{
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}


	/**
	 * 产生htmlcode
	 * @param apiUrl
	 * @param maps
	 * @param needCheckMacValue
	 * @return
	 */
	private String genHtmlCode(String apiUrl,String HashKey,String HashIV,Map<String,Object> maps, boolean needCheckMacValue)
	{
		StringBuilder builder = new StringBuilder();

		Hashtable<String, Object> fieldValue=new Hashtable<String, Object>();

		Iterator it = maps.entrySet().iterator();  
		while (it.hasNext()) 
		{  
			// entry的输出结果如key0=value0等  
			Map.Entry entry =(Map.Entry) it.next();  
			Object key = entry.getKey();  
			Object value=entry.getValue();  

			//System.out.println(key +"=" + value);  

			fieldValue.put(key.toString(), value);
		}  

		//加入检查码
		if(needCheckMacValue == true)
		{
			String CheckMacValue = genCheckMacValue(HashKey, HashIV, fieldValue);
			fieldValue.put("CheckMacValue", CheckMacValue);
		}

		Set<String> key = fieldValue.keySet();
		String[] name = key.toArray(new String[key.size()]);
		builder.append("<form id=\"postForm\" action=\""+apiUrl+"\" method=\"post\">");
		for(int i = 0; i < name.length; i++)
			builder.append("<input type=\"hidden\" name=\""+name[i]+"\" value=\""+fieldValue.get(name[i])+"\">");
		builder.append("<script language=\"JavaScript\">");
		builder.append("postForm.submit()");
		builder.append("</script>");
		builder.append("</form>");

		return builder.toString();
	}

	/**
	 * 產生檢查碼
	 * @param key
	 * @param iv
	 * @param Hashtable<String, String> params
	 * @return
	 */
	private String genCheckMacValue(String key, String iv, Hashtable<String, Object> params)
	{
		String result="";

		try 
		{
			Set<String> keySet = params.keySet();
			TreeSet<String> treeSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			treeSet.addAll(keySet);
			String name[] = treeSet.toArray(new String[treeSet.size()]);
			String paramStr = "";
			for(int i = 0; i < name.length; i++)
			{
				if(!name[i].equals("CheckMacValue"))
				{
					paramStr += "&" + name[i] + "=" + params.get(name[i]);
				}
			}
			String urlEncode = URLEncoder.encode("Hashkey=" + key + paramStr + "&HashIV=" + iv,"UTF-8").toLowerCase();
			urlEncode = netUrlEncode(urlEncode);

			result=hash(urlEncode.getBytes(), "MD5");		

		} 
		catch (Exception e) 
		{
			result="";
		}

		return  result;

	}


	/**
	 * 產生 Unix TimeStamp
	 * @return TimeStamp
	 */
	private String genUnixTimeStamp()
	{
		Date date = new Date();
		Integer timeStamp = (int)(date.getTime() / 1000);
		return timeStamp.toString();
	}




}
