package com.dsc.spos.thirdpart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 美团跑腿服务
 * https://peisong.meituan.com/open/doc#section1-1
 * API调用协议
 */
public class PAOTUIService {
	
	/**
	 * url为空时，默认给https://peisongopen.meituan.com/api/
	 * 不为空时，判断是否以/结尾，没有斜杠则补上
	 */
	public String getMainUrl(String url) throws Exception {
		if(url==null||url.trim().length()<1){
			url="https://peisongopen.meituan.com/api/";
		}else if(!url.endsWith("/")){
			url=url+"/";
		}
		return url;
	}
	
	public Map<String,Object> getSysParams(String appKey) throws Exception {
		Map<String,Object> json1=new HashMap<String,Object>();
		json1.put("appkey", appKey);
		json1.put("version", "1.0");//API协议版本，可选值：1.0
		json1.put("timestamp", System.currentTimeMillis()/1000);//时间戳
		return json1;
	}
	
	/**
	 * https://peisong.meituan.com/open/doc#section2-1
	 * 订单创建(门店方式)
	 */
	public JSONObject addOrder(Map<String, Object> setMap,Map<String, Object> orderMap,
			List<Map<String, Object>> detailList) throws Exception {
		//https://peisongopen.meituan.com/api
		//单号
		String orderNo=orderMap.get("ORDERNO")==null?"":orderMap.get("ORDERNO").toString();
		try
		{
			String url = setMap.get("APIURL")==null?"":setMap.get("APIURL").toString();
			url=getMainUrl(url);
			url=url+"order/createByShop";

			String isTest = "";
			//物流签名key
			String appKey = setMap.get("APPSIGNKEY")==null?"":setMap.get("APPSIGNKEY").toString();
			//8c0ef0ab9d4e4ff59d553eee231f1ebe  嘉华 测试KEY
			if("8c0ef0ab9d4e4ff59d553eee231f1ebe".equals(appKey)){
				isTest="Y";
			}
			//物流密钥
			String appSecret = setMap.get("APPSECRET")==null?"":setMap.get("APPSECRET").toString();
			//配送服务代码，详情见合同
			String delivery_service_code = setMap.get("IV")==null?"100035":setMap.get("IV").toString();
			try
			{
				int delivery_service_code_i = Integer.parseInt(delivery_service_code);
			}
			catch (Exception e)
			{
				delivery_service_code = "100035";//荷家默认
			}

					//配送门店信息
			String shopId=orderMap.get("SHIPPINGSHOP")==null?"":orderMap.get("SHIPPINGSHOP").toString();

			String receiverName=orderMap.get("CONTMAN")==null?"":orderMap.get("CONTMAN").toString();
			String receiverPhone=orderMap.get("CONTTEL")==null?"":orderMap.get("CONTTEL").toString();
			//地址
			String receiverAddress=orderMap.get("ADDRESS")==null?"":orderMap.get("ADDRESS").toString();
			//经度
			//美团跑腿 收件人经度（火星坐标或百度坐标，和 coordinate_type 字段配合使用），坐标 * （10的六次方），如 116398419
			String longitude=orderMap.get("LONGITUDE")==null?"":orderMap.get("LONGITUDE").toString();
			int receiver_lng=0;
			if(longitude!=null&&longitude.trim().length()>0){
				receiver_lng=new BigDecimal(longitude.trim()).multiply(new BigDecimal(1000000)).intValue();
			}
			//纬度
			String latitude=orderMap.get("LATITUDE")==null?"":orderMap.get("LATITUDE").toString();
			int receiver_lat=0;
			if(latitude!=null&&latitude.trim().length()>0){
				receiver_lat=new BigDecimal(latitude.trim()).multiply(new BigDecimal(1000000)).intValue();
			}
			BigDecimal totAmt=new BigDecimal(orderMap.get("TOT_AMT").toString());
			//将小数位设置为2，且设置四舍五入
			totAmt=totAmt.setScale(2, BigDecimal.ROUND_HALF_UP);

			//外卖流水号/电商交易流水号		ORDER_SN
			Object orderSn = orderMap.get("ORDER_SN");

			Map<String,Object> json1=getSysParams(appKey);
			//即配送活动标识，由外部系统生成，不同order_id应对应不同的delivery_id，
			//若因美团系统故障导致发单接口失败，可利用相同的delivery_id重新发单，系统视为同一次配送活动，
			//若更换delivery_id，则系统视为两次独立配送活动。
			json1.put("delivery_id", orderNo);
			//订单id，即该订单在合作方系统中的id，最长不超过32个字符
			//注：目前若某一订单正在配送中（状态不为取消），再次发送同一订单（order_id相同）将返回同一mt_peisong_id
			json1.put("order_id", orderNo);
			//outer_order_source_desc  订单来源：其他，请直接填写中文字符串，最长不超过20个字符
			json1.put("outer_order_source_desc", "商家平台");


			//shop_id 取货门店id，即合作方向美团提供的门店id
			//注：测试门店的shop_id固定为 test_0001 ，仅用于对接时联调测试。
			if("Y".equalsIgnoreCase(isTest)){
				shopId="test_0001";
			}
			json1.put("shop_id", shopId);
			//delivery_service_code  配送服务代码    跑腿-帮送:4031 服务包 100035
			json1.put("delivery_service_code", delivery_service_code);
			//收件人名称，最长不超过256个字符
			json1.put("receiver_name", receiverName);
			//收件人地址，最长不超过512个字符
			json1.put("receiver_address", receiverAddress);
			//收件人电话，最长不超过64个字符
			json1.put("receiver_phone", receiverPhone);
			//receiver_lng	int	是	收件人经度（火星坐标或百度坐标，和 coordinate_type 字段配合使用），坐标 * （10的六次方），如 116398419
			json1.put("receiver_lng", receiver_lng);
			json1.put("receiver_lat", receiver_lat);
			//coordinate_type 坐标类型，0：火星坐标（高德，腾讯地图均采用火星坐标） 1：百度坐标 （默认值为0）
			json1.put("coordinate_type", "0");//目前使用高德系

			//货物价格，单位为元，精确到小数点后两位（如果小数点后位数多于两位，则四舍五入保留两位小数），范围为0-5000
			//跑腿-帮送服务包：范围0-3000
			json1.put("goods_value", totAmt);
			//货物重量，单位为kg，精确到小数点后两位（如果小数点后位数多于两位，则四舍五入保留两位小数），范围为0-50
			//跑腿-帮送服务包：范围0-20
			json1.put("goods_weight", 1.00);

			String pickupTime = "";
			String shipDate = orderMap.get("SHIPDATE").toString();
			String sdtime = orderMap.get("SHIPSTARTTIME").toString();//SHIPENDTIME
			sdtime = sdtime.replace("-", "");
			if (sdtime.isEmpty())
			{
				sdtime = new SimpleDateFormat("HHmmss").format(new Date());
			}
			pickupTime = shipDate+ sdtime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss

			// 1-48小时为预约单 超过48小时不能发 1小时之内不为预约单
			long longcur = System.currentTimeMillis();
			Date dateSta =new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime);
			long longsta = dateSta.getTime();
			// 差别到分钟
			long diff = (longsta - longcur) / (1000 * 60);

			if(diff>48*60)
			{
				this.Log("【美团跑腿物流】预约只能48小时内,无需发物流,配送开始时间="+pickupTime+",单号orderNo="+orderNo);
                JSONObject jobject = new JSONObject();
                jobject.put("code","9999");
                jobject.put("message","预约只能48小时内,无需发物流");
                return jobject;
			}
			if (diff >= 60)
			{
				String eId = orderMap.getOrDefault("EID","").toString();
				int delayMin = getPreTime(eId);
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateSta);
				//设置秒为0
				cal.set(Calendar.SECOND,00);
				//前退40分钟
				cal.add(Calendar.MINUTE, -delayMin);
				//订单类型， 0: 即时单(尽快送达，限当日订单)1: 预约单 默认为0
				json1.put("order_type", 1);
				//期望取货时间
				long expected_pickup_time = cal.getTimeInMillis()/1000;
				json1.put("expected_pickup_time", expected_pickup_time);
				//期望送达时间
				long expected_delivery_time = longsta/1000;
				json1.put("expected_delivery_time", expected_delivery_time);
			}



			//支付状态/货到付款标记		PAYSTATUS	NVARCHAR2(10)		1.未支付 2.部分支付 3.付清
			String PAYSTATUS = orderMap.get("PAYSTATUS").toString();
			if (PAYSTATUS.equals("3")) {
			} else {
				// 先查TOTAMT 再查PAYAMT 剩下的就是要收用户的钱
				String pamt = orderMap.get("PAYAMT").toString();
				BigDecimal damt=totAmt.subtract(new BigDecimal(pamt)).setScale(0, BigDecimal.ROUND_HALF_UP);
				//cash_on_pickup	double	否	骑手应收金额，单位为元，精确到分【预留字段】
				json1.put("cash_on_pickup", damt);
			}
			if(detailList!=null&&detailList.size()>0){
				com.alibaba.fastjson.JSONArray goodslistArray=new com.alibaba.fastjson.JSONArray();
				for(Map<String, Object> detailMap:detailList){
					com.alibaba.fastjson.JSONObject goods=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
					String goodCount=detailMap.get("QTY")==null?"1":detailMap.get("QTY").toString();
					String goodName=detailMap.get("PLUNAME")==null?"品名异常":detailMap.get("PLUNAME").toString();
					String featureName = detailMap.getOrDefault("FEATURENAME", "").toString();
					if (!featureName.isEmpty())
					{
						goodName += " "+featureName;
					}
					String goodPrice=detailMap.get("PRICE")==null?"0":detailMap.get("PRICE").toString();
					String goodUnit=detailMap.get("SUNITNAME")==null?"":detailMap.get("SUNITNAME").toString();
					if(goodUnit==null||goodUnit.trim().length()<1){
						goodUnit=detailMap.get("SUNIT")==null?"":detailMap.get("SUNIT").toString();
					}
					goods.put("goodCount", goodCount);
					goods.put("goodName", goodName);
					goods.put("goodPrice", goodPrice);
					goods.put("goodUnit", goodUnit);
					goodslistArray.add(goods);
				}
				json1.put("goods", goodslistArray);
			}

			//poi_seq	String	否	门店订单流水号，建议提供，方便骑手门店取货，最长不超过32个字符
			json1.put("poi_seq", orderSn);
			JSONObject jobject = postData(url, appSecret, json1);
			return jobject;

		}
		catch ( Exception e)
		{
			this.Log("【美团跑腿物流】异常:"+e.getMessage()+",单号orderNo="+orderNo);
			return null;
		}

	}
	
	/**
	 * 跑腿-取消订单
	 * https://peisong.meituan.com/open/doc#section2-3
	 */
	public JSONObject cancelOrder(Map<String, Object> setMap,Map<String, Object> orderMap) throws Exception {
		
		//https://peisongopen.meituan.com/api
		String url = setMap.get("APIURL")==null?"":setMap.get("APIURL").toString();
		url=getMainUrl(url);
		url=url+"order/delete";
		
		//物流签名key
		String appKey = setMap.get("APPSIGNKEY")==null?"":setMap.get("APPSIGNKEY").toString();
		//物流密钥
		String appSecret = setMap.get("APPSECRET")==null?"":setMap.get("APPSECRET").toString();
		//单号
		String orderNo=orderMap.get("ORDERNO")==null?"":orderMap.get("ORDERNO").toString();
		String deliveryNo=orderMap.get("DELIVERYNO")==null?"":orderMap.get("DELIVERYNO").toString();
		String reason="取消";
		String refundReason=orderMap.get("REFUNDREASON")==null?"":orderMap.get("REFUNDREASON").toString();
		String refundReasonName=orderMap.get("REFUNDREASONNAME")==null?"":orderMap.get("REFUNDREASONNAME").toString();
		if(refundReason!=null&&refundReason.trim().length()>0){
			reason=refundReason.trim();
		}else if(refundReasonName!=null&&refundReasonName.trim().length()>0){
			reason=refundReasonName.trim();
		}
				
				
		Map<String,Object> json1=getSysParams(appKey);
		json1.put("delivery_id", orderNo);
		json1.put("mt_peisong_id", deliveryNo);
		json1.put("cancel_reason_id", 199);//101-顾客主动取消;199-其他接入方原因
		json1.put("cancel_reason", reason);
		
		JSONObject jobject = postData(url, appSecret, json1);
		return jobject;
	}
	
	public JSONObject postData(String url,String appSecret,Map<String,Object> params) throws Exception {
		String sign=getSignature(params, appSecret);
		params.put("sign", sign);
		String res="";
		try{
			res=doPost(url, params);
			Log("请求url:"+url+",请求req:"+params+"\r\n返回res:"+res);
		}catch(Exception e){
			//ThirdpartService ts=new ThirdpartService();
			//Log("请求nurl:"+url+",请求req:"+params+"返回异常"+ts.getTrace(e));
			Log("请求nurl:"+url+",请求req:"+params+"返回异常"+e.getMessage());
			return null;
		}
		JSONObject jobject = JSONObject.parseObject(res);
		return jobject;
	}
	
	/**
	 * 将除signature，requestBody之外的公共参数、接口所需业务参数以及开发者秘钥（devPwd）按照键值的字典顺序拼接排序(忽略大小写)，
	 * 注意：业务参数中键对应的值为空的键值均不参与拼接，键对应的值为基本类型列表的键值对不参与签名，
	 * 键对应的值为复杂类型的键不参与签名，键对应的值为复杂类型列表的取其中第一个参与签名
	 */
	public String getSignature(Map<String, Object> params,String appSecret) throws Exception {
		//1）将所有系统参数及业务参数（其中sign，byte[]及值为空的参数除外）按照参数名的字典顺序排序
		//2）将参数以参数1值1参数2值2...的顺序拼接，例如a=&c=3&b=1，变为b1c3，参数使用utf-8编码
		//3）按照secret + 排序后的参数的顺序进行连接，得到加密前的字符串
		String sign1 = appSecret+getOrderByLexicographic(params);
		//4）对加密前的字符串进行sha1加密并转为小写字符串，得到签名
		//5）将得到的签名赋给sign作为请求的参数
		return sha1(sign1).toLowerCase();
		
	}

	/**
	 * 按照键值的字典顺序拼接
	 */
	public String getOrderByLexicographic(Map<String, Object> params) {
		List<String> list = new ArrayList<String>();
		
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			Object value=entry.getValue();
			if(value==null||value.toString().trim().length()<1){
				continue;
			}else{
				list.add(entry.getKey());
			}
		}
		Collections.sort(list);
		String paramStr = "";
		for(String key:list){
			paramStr+=key+params.get(key);
		}
		return paramStr;
	}

	/**
	 * sha1加密
	 */
	public String sha1(String data) throws Exception {
		// 信息摘要器 算法名称
		MessageDigest md = MessageDigest.getInstance("SHA1");
		// 把字符串转为字节数组
		byte[] b = data.getBytes();
		// 使用指定的字节来更新我们的摘要
		md.update(b);
		// 获取密文 （完成摘要计算）
		byte[] b2 = md.digest();
		// 获取计算的长度
		int len = b2.length;
		// 16进制字符串
		String str = "0123456789abcdef";
		// 把字符串转为字符串数组
		char[] ch = str.toCharArray();

		// 创建一个40位长度的字节数组
		char[] chs = new char[len * 2];
		// 循环20次
		for (int i = 0, k = 0; i < len; i++) {
			byte b3 = b2[i];// 获取摘要计算后的字节数组中的每个字节
			// >>>:无符号右移
			// &:按位与
			// 0xf:0-15的数字
			chs[k++] = ch[b3 >>> 4 & 0xf];
			chs[k++] = ch[b3 & 0xf];
		}

		// 字符数组转为字符串
		return new String(chs);
	}
	
	/**
     * post 方法
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public String doPost(String url, Map<String, Object> params) throws Exception {
        if (StringUtils.isEmpty(url) || params == null || params.isEmpty()) {
            return "";
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setSocketTimeout(10000)
                    .setConnectTimeout(10000)
                    .build();//设置请求和传输超时时间

            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
            for (Map.Entry<String, Object> entity : params.entrySet()) {
                basicNameValuePairs.add(new BasicNameValuePair(entity.getKey(), entity.getValue()==null?"":entity.getValue().toString()));
            }

            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, Consts.UTF_8);
            httpPost.setEntity(urlEncodedFormEntity);

            response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            Log(String.format("request url: %s, params: %s, response status: %s",
                    url, JSON.toJSONString(params), statusLine.getStatusCode()));

            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
            Log(String.format("response data: %s", result));

            return result == null ? "" : result.trim();

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
//                Log("close http client failed \r\n"+OrderPostClient.getTrace(e));
            }
        }

    }
    
    
    public void Log(String log)throws Exception{
    	String fileName="PaoTuiLog";
    	/*ThirdpartService ts=new ThirdpartService();
		ts.writelogFileName(fileName,log);*/
		HelpTools.writelog_fileName(log, fileName);
    }

	/**
	 * 预订单提前多久呼叫物流到店
	 * @param eId
	 * @return
	 */
	private int getPreTime (String eId)
	{
		int preTime = 50;//默认50分钟
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

