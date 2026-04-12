package com.dsc.spos.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;




public class SoapUtil{
	
	public static String request(SoapObject request, String TradeUrl) {
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
		
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		envelope.encodingStyle = SoapSerializationEnvelope.ENV;
		// 构建传输对象，并指明端口
		// 这里的wsdl 地址需要做成参数， 方便以后其他服务调用
//		HttpTransportSE ht = new HttpTransportSE("http://58.213.118.119:8127/Ajax/TradeChange.asmx");
		HttpTransportSE ht = new HttpTransportSE(TradeUrl);
		
		try {
			ht.call(null, envelope);
		} catch (Exception e) {
			
		}
		Object resultObject = null;
		try {
			resultObject = envelope.getResponse();// 使用getResponse方法获得WebService方法的返回结果
		} catch (Exception e) {
			
		}
		if (resultObject != null) {
			return resultObject.toString();
		} else {
			return " ！！ ";
		}
	}
	
	
	/**
	 * 解析传入的参数，分解为接口的参数
	 * @param url
	 * @param method
	 * @param paramMap
	 * @return
	 */
	public static SoapObject setRequestParam(String url,String method, Map<String,Object> paramMap){
	
		SoapObject request = new SoapObject(url, method);
		
		if(paramMap.size() > 0){
			for(Entry<String,Object> entry : paramMap.entrySet()){
				String paramName = entry.getKey();
				Object paramValue = entry.getValue();
				request.addProperty(paramName, paramValue);
			}
		}
		return request;
	}	
	
	
	
} 



