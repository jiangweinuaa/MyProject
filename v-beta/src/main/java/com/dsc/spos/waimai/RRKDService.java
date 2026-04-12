package com.dsc.spos.waimai;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.OrderUtil;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.RRKDUtil;

public class RRKDService 
{
	
	public static JSONObject getFastPrice(List<Map<String, Object>> listsqldate,List<Map<String, Object>> listshop,String callbake) throws ClientProtocolException, IOException, ParseException {	
	  //人人下单询价接口
		String ssurl=listshop.get(0).get("CURL").toString();
		String APPKey=listshop.get(0).get("CAPPKEY").toString();
		String APPSecret=listshop.get(0).get("CAPPSECRET").toString();
		String APPSignKey=listshop.get(0).get("CAPPSIGNKEY").toString();
		
		String shop=listsqldate.get(0).get("SHOP").toString();
		String tot_amt=listsqldate.get(0).get("TOT_AMT").toString();
		int itotamt= (int)(Double.parseDouble(tot_amt));
		
		String consigneeProvince=listsqldate.get(0).get("PROVINCE").toString();
		String consigneeCity=listsqldate.get(0).get("CITY").toString();
		String consigneeAddress=listsqldate.get(0).get("ADDRESS").toString();
		String pickupTime="";
		pickupTime=listsqldate.get(0).get("SHIPDATE").toString();
	  String sdtime=listsqldate.get(0).get("SHIPTIME").toString();
	  
	  String DeliveryStutas=listsqldate.get(0).get("DELIVERYSTUTAS").toString();
	  String DeliveryType=listsqldate.get(0).get("DELIVERYTYPE").toString();
	  String SHIPTYPE=listsqldate.get(0).get("SHIPTYPE").toString();
	  int idex= 0;
	  if(sdtime.contains("-"))
	  {
	  	idex=sdtime.indexOf("-");
	  }
	  else
	  {
	  	idex=sdtime.length();
	  }
	  sdtime=sdtime.substring(0, idex);
	  pickupTime+=sdtime;
	  pickupTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime));
		
		String startingPhone="";
		String startingProvince="";
		String city="";
		String startingAddress="";
		
		double[] baidulist= OrderUtil.gaoDeToBaidu(Double.parseDouble(listsqldate.get(0).get("LONGITUDE").toString()), Double.parseDouble(listsqldate.get(0).get("LATITUDE").toString()));
		
		if(listshop!=null&&!listshop.isEmpty())
		{
			startingPhone=listshop.get(0).get("PHONE").toString();
			startingProvince=listshop.get(0).get("PROVINCE").toString();
			city=listshop.get(0).get("CITY").toString();
			//city=listshop.get(0).get("CITY").toString();
		  startingAddress=listshop.get(0).get("ADDRESS").toString();
		}
		
		RRKDFastInfoVO vo = new RRKDFastInfoVO();
		vo.setUserName(APPKey);
		vo.setGoodsWeight(1);
		vo.setGoodsWorth(itotamt);
		vo.setStartingPhone(startingPhone);
		vo.setStartingProvince(startingProvince);
		vo.setStartingCity(city);
		vo.setStartingAddress(startingAddress);
		vo.setConsigneeProvince(consigneeProvince);
		vo.setConsigneeCity(consigneeCity);
		vo.setConsigneeAddress(consigneeAddress);
		
		
		if(pickupTime!=null&&!pickupTime.isEmpty())
		{
			vo.setPickupTime(pickupTime);
		}
		
		//发送参数封装
		JSONObject param = (JSONObject) JSONObject.toJSON(vo);
		String timestampStr = System.currentTimeMillis()+"";
		String timestampMD5 = PosPub.encodeMD5(timestampStr).toLowerCase() ;
		String otherMD5 = PosPub.encodeMD5(APPKey +vo.getStartingAddress() + vo.getConsigneeAddress()).toLowerCase() ;
		String sign = PosPub.encodeMD5(APPSecret + timestampMD5 + otherMD5).toLowerCase();
		param.put("sign", sign);
		param.put("version", "2.0");
		param.put("timestamp", timestampStr);
		//发送请求
		//JSONObject result = RRKDUtil.sendPost("http://code.rrkd.cn/v2"+"getfastprice", param);
		JSONObject result = RRKDUtil.sendPost(ssurl+"getfastprice", param);
		
		return result;
	}
	
	public static JSONObject addOrderFortdd(List<Map<String, Object>> listsqldate,List<Map<String, Object>> listshop,String callbake,String calltype) throws ClientProtocolException, IOException, ParseException, SPosCodeException {
		
		String ssurl=listshop.get(0).get("CURL").toString();//"http://code.rrkd.cn/v2/"+"addorderfortdd"
		String APPKey=listshop.get(0).get("CAPPKEY").toString();//"18017014029"
		String APPSecret=listshop.get(0).get("CAPPSECRET").toString();//"88638d9ffaad83b7a9856fc799b5d98f"
		String APPSignKey=listshop.get(0).get("CAPPSIGNKEY").toString();
		String cururl="";
		if(calltype.equals("1"))
		{
			//预下单
			cururl="getfastprice";
		}
		if(calltype.equals("2"))
		{
			//下单
			cururl="addorderfortdd";
		}
		if(calltype.equals("3"))
		{
			//重下单
			cururl="againorder";
		}
		ssurl+=cururl;
		
		String SHIPFEE="0";
		String DeliveryNO="";
		String shop="";
		String shippingShopNO="";
		String loadDocType = "";
		
		shop=listsqldate.get(0).get("SHOP").toString();
		shippingShopNO=listsqldate.get(0).get("SHIPPINGSHOP").toString();
		loadDocType = listsqldate.get(0).get("LOAD_DOCTYPE").toString();
		String tot_amt=listsqldate.get(0).get("TOT_AMT").toString();
		int itotamt= (int)(Double.parseDouble(tot_amt));
		
		String consigneeProvince=listsqldate.get(0).get("PROVINCE").toString();
		String consigneeCity=listsqldate.get(0).get("CITY").toString();
		String consigneeAddress=listsqldate.get(0).get("ADDRESS").toString();
		String consigneeName=listsqldate.get(0).get("CONTMAN").toString();
		String consigneePhone=listsqldate.get(0).get("CONTTEL").toString();
		String DeliveryType=listsqldate.get(0).get("DELIVERYTYPE").toString();
		
		String DeliveryStutas=listsqldate.get(0).get("DELIVERYSTUTAS").toString();
	  String SHIPTYPE=listsqldate.get(0).get("SHIPTYPE").toString();
		
		String pickupTime="";
		pickupTime=listsqldate.get(0).get("SHIPDATE").toString();
	  String sdtime=listsqldate.get(0).get("SHIPTIME").toString();
	  String[] datalist = new String[2];
	  if(sdtime.contains("-"))
	  {
	  	datalist=sdtime.split("-");
	  }
	  sdtime= datalist[0];
	  pickupTime+=" "+sdtime;
	  pickupTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(pickupTime));
		
		String startingPhone="";
		String startingProvince="";
		String city="";
		String startingAddress="";
		
		if(listshop!=null&&!listshop.isEmpty()&&DeliveryType!=null&&!DeliveryType.isEmpty())
		{
			startingPhone=listshop.get(0).get("PHONE").toString();
			startingProvince=listshop.get(0).get("PROVINCE").toString();
			city=listshop.get(0).get("CITY").toString();
			
		  startingAddress=listshop.get(0).get("ADDRESS").toString();

		}
		
			RRKDFastInfoVO vo = new RRKDFastInfoVO();
			vo.setUserName(APPKey);
			vo.setBusinessNo(listsqldate.get(0).get("ORDERNO").toString());
			String goodname="";
			for (Map<String, Object> map : listsqldate) 
			{
				goodname+=map.get("PLUNAME").toString()+",";
		  }
			goodname=goodname.substring(0, goodname.length()-1);
			
			vo.setGoodsName(goodname);
			vo.setGoodsWeight(1);
			vo.setGoodsWorth(itotamt);
			vo.setStartingPhone(startingPhone);
			vo.setMapFrom(1);
			double[] baiduliststa= OrderUtil.gaoDeToBaidu(Double.parseDouble(listshop.get(0).get("LONGITUDE").toString()), Double.parseDouble(listshop.get(0).get("LATITUDE").toString()));
			vo.setStartingLng(BigDecimal.valueOf(baiduliststa[0]));
			vo.setStartingLat(BigDecimal.valueOf(baiduliststa[1]) );
			double[] baidulistrev= OrderUtil.gaoDeToBaidu(Double.parseDouble(listsqldate.get(0).get("LONGITUDE").toString()), Double.parseDouble(listsqldate.get(0).get("LATITUDE").toString()));
			vo.setConsigneeLng(BigDecimal.valueOf(baidulistrev[0]));
			vo.setConsigneeLat(BigDecimal.valueOf(baidulistrev[1]));
			
			vo.setStartingName(listshop.get(0).get("ORG_NAME").toString());
			
			vo.setStartingProvince(startingProvince);
			vo.setStartingCity(city);
			vo.setStartingAddress(startingAddress);
			
			vo.setConsigneeProvince(consigneeProvince);
			vo.setConsigneeCity(consigneeCity);
			vo.setConsigneeAddress(consigneeAddress);
			vo.setConsigneeName(consigneeName);
			vo.setConsigneePhone(consigneePhone);
			
			//1-72小时为预约单  超过72小时不能发  1小时之内不为预约单
			long longcur=System.currentTimeMillis();
			long longsta= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(pickupTime).getTime();
			//差别小时数
			long diff=(longsta-longcur)/(1000*60*60);
			if(diff>=1&&diff<=72)
			{
				vo.setPickupTime(pickupTime);
			}
			if(diff>72)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "距离送货时间大于72小时！"); 
			}
				
			vo.setCallbackUrl(callbake+"WULIU/RRKD");
			
		//发送参数封装
		JSONObject param = (JSONObject) JSONObject.toJSON(vo);
		String timestampStr = System.currentTimeMillis()+"";
		
		String timestampMD5 = getMD5(timestampStr).toLowerCase() ;
		String sother=APPKey +vo.getStartingAddress() + vo.getConsigneeAddress();		
		String otherMD5 = getMD5(sother).toLowerCase() ;
		
		String sign = getMD5(APPSecret + timestampMD5 + otherMD5).toLowerCase();
		param.put("sign", sign);
		param.put("version", "2.0");
		param.put("timestamp", timestampStr);
		//发送请求
		JSONObject result = RRKDUtil.sendPost(ssurl, param);
		return result;
	}
	
	
	public static JSONObject getCancelOrder(List<Map<String, Object>> listsqldate,List<Map<String, Object>> listshop) throws ClientProtocolException, IOException {
		//发送参数封装
		String ssurl=listshop.get(0).get("CURL").toString();
		String APPKey=listshop.get(0).get("CAPPKEY").toString();
		String APPSecret=listshop.get(0).get("CAPPSECRET").toString();
		String APPSignKey=listshop.get(0).get("CAPPSIGNKEY").toString();
		
		String status=listsqldate.get(0).get("STATUS").toString();
		//配送方式
		String SHIPTYPE=listsqldate.get(0).get("SHIPTYPE").toString(); 
		String DeliveryType=listsqldate.get(0).get("DELIVERYTYPE").toString(); 
		String DeliveryNO=listsqldate.get(0).get("DELIVERYNO").toString(); 
		String ORDERNO=listsqldate.get(0).get("ORDERNO").toString(); 
		
		RRKDFastInfoVO vo = new RRKDFastInfoVO();
		vo.setUserName(APPKey);
		vo.setOrderNo(DeliveryNO);
		vo.setBusinessNo(ORDERNO);
		vo.setReason("缺货");
		
		JSONObject param = (JSONObject) JSONObject.toJSON(vo);
		String timestampStr = System.currentTimeMillis()+"";
		String timestampMD5 = PosPub.encodeMD5(timestampStr).toLowerCase() ;
		String otherMD5 = PosPub.encodeMD5(APPKey +vo.getBusinessNo()).toLowerCase() ;
		String sign = PosPub.encodeMD5(APPSecret + timestampMD5 + otherMD5).toLowerCase();
		param.put("sign", sign);
		param.put("version", "2.0");
		param.put("timestamp", timestampStr);
		//发送请求
		//JSONObject result = RRKDUtil.sendPost("http://code.rrkd.cn/v2"+"getfastprice", param);
		JSONObject result = RRKDUtil.sendPost(ssurl+"cancelorder", param);
		
		return result;
	}
	
	public static void main(String[] args) 
	{
		String ss="18017014029味多美(平型关店)宝山区盛桥一村66号402室";
		String aa=getMD5(ss);
	}
	
	public static String getMD5(String s) {
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes("utf-8");
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            
            return null;
        }
	}
	
	
}
