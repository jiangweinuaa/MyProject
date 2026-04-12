package com.dsc.spos.waimai.dada;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;
import org.json.JSONException;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.dada.DadaAddOrderModel.product;


public class DadaService 
{
	public  String QA_HOST = "http://newopen.qa.imdada.cn";//测试域名
	public  String ONLINE_HOST = "https://newopen.imdada.cn";//线上域名
	
	public DadaService()
	{
		
	}
	public DadaService(boolean isTest)
	{
		if(isTest)
		{
			ONLINE_HOST = QA_HOST;			
		}
	}

	public  JSONObject getCityCode(List<Map<String, Object>> listsqldate, List<Map<String, Object>> listshop) {
		String url = "/api/cityCode/list";

		DadaBaseModel dbasemodel = new DadaBaseModel();
		dbasemodel.setSource_id("73753");
		dbasemodel.setV("1.0");
		dbasemodel.setFormat("json");
		dbasemodel.setApp_key("dada6f5846daffe8416");
		// 这里的BODY有可能是一个JSON
		dbasemodel.setBody("");
		dbasemodel.setTimestamp(String.valueOf(System.currentTimeMillis()));
		String sendjson = getRequestParams(dbasemodel);
		// 发送消息
		url = QA_HOST + url;
		String res = DadaHttpClientUtil.postRequest(url, sendjson);
		JSONObject resultJson = JSONObject.parseObject(res);
		return resultJson;
	}

	/**
	 * 
	 * @param setMap
	 * @param orderMap
	 * @param detailList
	 * @param callback
	 * @param calltype 1-预下单,2-直接下单，3-重下单
	 * @return
	 * @throws Exception
	 */
	public  JSONObject addOrder(Map<String, Object> setMap,Map<String, Object> orderMap,
			List<Map<String, Object>> detailList,String callback, String calltype) throws Exception 
	{
		String orderNo = orderMap.get("ORDERNO").toString();
		try
		{			
			String appKey = setMap.get("APPSIGNKEY").toString();// dada6f5846daffe8416
			String appSecret = setMap.get("APPSECRET").toString();// 1cf4d0cafb99b43699e57570746d62c7
			String sourceId = setMap.get("SHOPCODE").toString();// 73753
																				// 可以当一个商户号处理
			// String
			// ORDERSHOPNO=listshop.get(0).get("ORDERSHOPNO").toString();//11047059
			String DELMEMO = orderMap.get("DELMEMO").toString();//配送备注
			// 物流下单接口
			String url = "/api/order/addOrder";
			if (calltype.equals("1")) {
				// 预下单
				url = "/api/order/queryDeliverFee";
			}
			if (calltype.equals("2")) {
				// 下单
				url = "/api/order/addOrder";
			}
			if (calltype.equals("3")) {
				// 重下单
				url = "/api/order/reAddOrder";
			}
			
			DadaBaseModel dbasemodel = new DadaBaseModel();
			dbasemodel.setSource_id(sourceId);
			dbasemodel.setV("1.0");
			dbasemodel.setFormat("json");
			dbasemodel.setApp_key(appKey);
			dbasemodel.setApp_secret(appSecret);
		
			DadaAddOrderModel dfastprice = new DadaAddOrderModel();

			// 对应第三方店铺NO
			Object ORDERSHOPNO = setMap.get("ORDERSHOPNO");// 门店号处理3243279847393测试用
			if (ORDERSHOPNO != null && !ORDERSHOPNO.toString().trim().isEmpty()) {
				dfastprice.setShop_no(ORDERSHOPNO.toString());
			} else {
				// 订单的配送门店
				dfastprice.setShop_no(orderMap.get("SHIPPINGSHOP").toString());
			}

			dfastprice.setOrigin_id(orderNo);
			// 城市CODE需要从表里面取
			dfastprice.setCity_code("021");
			dfastprice.setCargo_price(Double.parseDouble(orderMap.getOrDefault("TOT_AMT","0").toString()));
			String PAYSTATUS = orderMap.getOrDefault("PAYSTATUS","3").toString();
			// 是否需要垫付 1:是 0:否 (垫付订单金额，非运费)
			if (PAYSTATUS.equals("3")) {
				dfastprice.setIs_prepay(0);
			} else {
				dfastprice.setIs_prepay(1);
			}
			String recipientName = orderMap.getOrDefault("GETMAN", "").toString();
			if (recipientName.isEmpty())
			{
				recipientName = orderMap.getOrDefault("CONTMAN", "").toString();
			}
			dfastprice.setReceiver_name(recipientName);// 改为使用CONTMAN
			dfastprice.setReceiver_address(orderMap.get("ADDRESS").toString());
			String recipientMobile = orderMap.getOrDefault("GETMANTEL", "").toString();
			if (recipientMobile.isEmpty())
			{
				recipientMobile = orderMap.getOrDefault("CONTTEL", "").toString();
			}
			dfastprice.setReceiver_phone(recipientMobile);
			callback = callback.replace("invoke", "");
			dfastprice.setCallback(callback + "WULIU/DADA");
			Object longitude = orderMap.get("LONGITUDE");
			if (longitude != null && !longitude.toString().trim().isEmpty()) {
				dfastprice.setReceiver_lng(Double.parseDouble(longitude.toString().trim()));
			}
			Object latitude = orderMap.get("LATITUDE");
			if (latitude != null && !latitude.toString().trim().isEmpty()) {
				dfastprice.setReceiver_lat(Double.parseDouble(latitude.toString().trim()));
			}
			dfastprice.setInfo(DELMEMO);
			dfastprice.setCargo_type(1);
			dfastprice.setCargo_weight(1);
			
			int wqty = 0;
			try
			{
				wqty = Integer.parseInt(orderMap.getOrDefault("TOT_QTY","1").toString());
				
			} catch (Exception e)
			{
				// TODO: handle exception
			}
			
			dfastprice.setCargo_num(wqty);

			String pickupTime = "";
			String shipDate = orderMap.get("SHIPDATE").toString();
			String sdtime = orderMap.get("SHIPSTARTTIME").toString();//SHIPENDTIME
			sdtime = sdtime.replace("-", "");
			if (sdtime.isEmpty())
			{
				sdtime = new SimpleDateFormat("HHmmss").format(new Date());
			}
			pickupTime = shipDate+ sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss
			
			// 1-72小时为预约单 超过72小时不能发 1小时之内不为预约单
			long longcur = System.currentTimeMillis();
			Date dateSta =new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
			long longsta = dateSta.getTime();
			// 差别到分钟
			long diff = (longsta - longcur) / (1000 * 60);
			
			if(diff>72*60)
			{
				return null;
			}
			if (diff >= 60) {
				String eId = orderMap.getOrDefault("EID","").toString();
				int delayMin = getPreTime(eId);
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateSta);
				//设置秒为0
				cal.set(Calendar.SECOND,00);
				//前退40分钟
				cal.add(Calendar.MINUTE, -delayMin);
				//计算分能否被10整除
				int singleMin=cal.get(Calendar.MINUTE)%10;
				if(singleMin!=0){
					cal.add(Calendar.MINUTE, -singleMin);
				}
				dfastprice.setDelay_publish_time((int) (cal.getTime().getTime()/1000));
			}
			
			dfastprice.setCallback(callback + "WULIU/DADA");
			
			/***************添加下商品明细******************/
			if(detailList!=null&&detailList.isEmpty()==false)
    		{
				dfastprice.setProduct_list(new ArrayList<DadaAddOrderModel.product>());
				for (Map<String, Object> mapGoods : detailList)
				{
    				String pluName = mapGoods.getOrDefault("PLUNAME", "").toString();
    				String pluNo = mapGoods.getOrDefault("PLUNO", "").toString();
    				String pluBarcode = mapGoods.getOrDefault("PLUBARCODE", "").toString();
    				
    				if(pluNo.isEmpty())
    				{
    					pluNo = pluBarcode;
    				}
    				
    				if(pluName.isEmpty())
    				{
    					pluName = pluNo;
    				}
					String featureName = mapGoods.getOrDefault("FEATURENAME", "").toString();
					if (!featureName.isEmpty())
					{
						pluName += " "+featureName;
					}
    				String unitName = mapGoods.getOrDefault("SUNITNAME", "").toString();
    				
    				if(unitName.isEmpty())
    				{
    					unitName = "件";
    				}
    				String qty = mapGoods.getOrDefault("QTY", "1").toString();
    				BigDecimal count = new BigDecimal(qty);
    				
					
    				product orderGoodsDto = new product();
    				orderGoodsDto.setSku_name(pluName);
    				orderGoodsDto.setSrc_product_no(pluNo);
    				orderGoodsDto.setCount(count.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
    				orderGoodsDto.setUnit(unitName);
    				dfastprice.getProduct_list().add(orderGoodsDto);
    				
				}
				
    		}

			ParseJson pj = new ParseJson();

			dbasemodel.setBody(pj.beanToJson(dfastprice));
			pj=null;
			
			dbasemodel.setTimestamp(String.valueOf(System.currentTimeMillis()));
			String sendjson = getRequestParams(dbasemodel);
			// 发送消息
			url = ONLINE_HOST + url;
			String res = DadaHttpClientUtil.postRequest(url, sendjson);
			JSONObject resultJson = JSONObject.parseObject(res);
			return resultJson;
		
			
		} 
		catch (Exception e)
		{
			this.Log("【达达物流】异常:"+e.getMessage()+",单号orderNo="+orderNo);
			return null;
		}
	}
	
	// 查询运费后发单接口
		public  JSONObject addAfterQuery(Map<String, Object> setMap,Map<String, Object> orderMap,
				String delno) throws Exception 
		{
			String orderNo = orderMap.get("ORDERNO").toString();
			try
			{

				String appKey = setMap.get("APPSIGNKEY").toString();// dada6f5846daffe8416
				String appSecret = setMap.get("APPSECRET").toString();// 1cf4d0cafb99b43699e57570746d62c7
				String sourceId = setMap.get("SHOPCODE").toString();// 73753
																					
				//查询运费后发单接口
				String url = "/api/order/addAfterQuery";
				DadaBaseModel dbasemodel = new DadaBaseModel();
				// 商户号需要处理下，因为味多美是2个商户号
				dbasemodel.setSource_id(sourceId);
				dbasemodel.setV("1.0");
				dbasemodel.setFormat("json");
				dbasemodel.setApp_key(appKey);
				dbasemodel.setApp_secret(appSecret);
				
				org.json.JSONObject dfastprice = new org.json.JSONObject();
				dfastprice.put("deliveryNo", delno);

				dbasemodel.setBody(dfastprice.toString());

				dbasemodel.setTimestamp(String.valueOf(System.currentTimeMillis()));
				String sendjson = getRequestParams(dbasemodel);
				// 发送消息
				url = ONLINE_HOST + url;
				String res = DadaHttpClientUtil.postRequest(url, sendjson);
				JSONObject resultJson = JSONObject.parseObject(res);
				return resultJson;
			
			}
			catch (Exception e)
			{
				this.Log("【达达物流】异常:"+e.getMessage()+",达达物流单号deliveryNo="+delno+",单号orderNo="+orderNo);
				return null;
			}
			
		}

	

	public  JSONObject cancelOrder(Map<String, Object> setMap,Map<String, Object> orderMap) {
		String appKey = setMap.get("APPSIGNKEY").toString();// dada6f5846daffe8416
		String appSecret = setMap.get("APPSECRET").toString();// 1cf4d0cafb99b43699e57570746d62c7
		String sourceId = setMap.get("SHOPCODE").toString();// 73753
	
		// 下单取消接口
		String url = "/api/order/formalCancel";
		DadaBaseModel dbasemodel = new DadaBaseModel();
		dbasemodel.setSource_id(sourceId);
		dbasemodel.setV("1.0");
		dbasemodel.setFormat("json");
		dbasemodel.setApp_key(appKey);
		dbasemodel.setApp_secret(appSecret);	
		// 这里的BODY有可能是一个JSON
		JSONObject bodyjson = new JSONObject();
		bodyjson.put("order_id", orderMap.get("ORDERNO").toString());
		bodyjson.put("cancel_reason_id", 4);

		dbasemodel.setBody(bodyjson.toJSONString());

		dbasemodel.setTimestamp(String.valueOf(System.currentTimeMillis()));
		String sendjson = getRequestParams(dbasemodel);
		// 发送消息
		url = ONLINE_HOST + url;
		String res = DadaHttpClientUtil.postRequest(url, sendjson);
		JSONObject resultJson = JSONObject.parseObject(res);
		return resultJson;

	}

	public  JSONObject addShipfee(Map<String, Object> setMap, Map<String, Object> orderMap,float shipfee) {
		String appKey = setMap.get("APPSIGNKEY").toString();// dada6f5846daffe8416
		String appSecret = setMap.get("APPSECRET").toString();// 1cf4d0cafb99b43699e57570746d62c7
		String sourceId = setMap.get("SHOPCODE").toString();// 73753
																			
		String ORDERSHOPNO = setMap.get("ORDERSHOPNO").toString();// 11047059
		String DELMEMO = orderMap.get("DELMEMO").toString();
		// 小费
		String url = "/api/order/addTip";
		DadaBaseModel dbasemodel = new DadaBaseModel();
		dbasemodel.setSource_id(sourceId);
		dbasemodel.setV("1.0");
		dbasemodel.setFormat("json");
		dbasemodel.setApp_key(appKey);
		dbasemodel.setApp_secret(appSecret);
		// 这里的BODY有可能是一个JSON
		JSONObject bodyjson = new JSONObject();
		bodyjson.put("order_id", orderMap.get("ORDERNO").toString());
		bodyjson.put("tips", shipfee);
		bodyjson.put("city_code", "021");

		dbasemodel.setBody(bodyjson.toJSONString());

		dbasemodel.setTimestamp(String.valueOf(System.currentTimeMillis()));
		String sendjson = getRequestParams(dbasemodel);
		// 发送消息
		url = ONLINE_HOST + url;
		String res = DadaHttpClientUtil.postRequest(url, sendjson);
		JSONObject resultJson = JSONObject.parseObject(res);
		return resultJson;

	}

	private  String getRequestParams(DadaBaseModel dbasemodel) {
		Map<String, String> requestParams = new HashMap<String, String>();
		String APPSecret = dbasemodel.getApp_secret();
		requestParams.put("source_id", dbasemodel.getSource_id());
		requestParams.put("app_key", dbasemodel.getApp_key());
		requestParams.put("timestamp", dbasemodel.getTimestamp());
		requestParams.put("format", dbasemodel.getFormat());
		requestParams.put("v", dbasemodel.getV());
		requestParams.put("body", dbasemodel.getBody());
		requestParams.put("signature", getSign(requestParams,APPSecret));
		return  DadaJsonUtil.toJson(requestParams);//JSONObject.toJSONString(requestParams);
	}

	private  String getSign(Map<String, String> requestParams,String APPSecret) {
		// 请求参数键值升序排序
		Map<String, String> sortedParams = new TreeMap<String, String>(requestParams);
		Set<Map.Entry<String, String>> entrySets = sortedParams.entrySet();

		// 拼参数字符串。
		StringBuilder signStr = new StringBuilder();
		for (Map.Entry<String, String> entry : entrySets) {
			signStr.append(entry.getKey()).append(entry.getValue());
		}

		// MD5签名并校验
		String toSign = APPSecret + signStr.toString() + APPSecret;
		String sign = encrypt(toSign);
		return sign.toUpperCase();
	}

	private  String encrypt(String inbuf) {
		String s = null;
		char[] hexDigits = { // 用来将字节转换成 16 进制表示的字符
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(inbuf.getBytes("UTF-8"));
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
				// >>> 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			
		}
		return s;
	}
	
	 public void Log(String log)throws Exception{
	    	String fileName="DaDaLog";	    	
			HelpTools.writelog_fileName(log, fileName);
	    }

	/**
	 * 预订单提前多久呼叫物流到店
	 * @param eId
	 * @return
	 */
	private int getPreTime (String eId)
	  {
	  	 int preTime = 60;//默认60分钟
		  try
		  {
			 String paraValue = PosPub.getPARA_SMS(StaticInfo.dao,eId,"","logisticsDuration");
			 int paraValue_i = Integer.parseInt(paraValue);
			 if (paraValue_i<=0||paraValue_i>60)
			 {

			 }
			 else
			 {
				 preTime = paraValue_i;
			 }


		  }
		  catch ( Exception e)
		  {

		  }


		  return  preTime;
	  }
}
