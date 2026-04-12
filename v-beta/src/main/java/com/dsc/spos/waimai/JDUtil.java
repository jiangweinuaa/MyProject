package com.dsc.spos.waimai;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;

public class JDUtil 
{

	
	static Logger logger = LogManager.getLogger(JDUtil.class.getName());
	/**
	 * 直接发送HTTP请求
	 */
	public static String SendJD(String method,String reques,String surl,String smethod,String access_token,String app_key,String API_SECRET) throws IOException 
	{
		
		String timestamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime() );
		//处理签名
		String ssign="360buy_param_json"+reques+"access_token"+access_token+"app_key"+app_key+"method"+smethod+"timestamp"+timestamp+"v2.0";
		ssign=API_SECRET+ssign+API_SECRET;
		String sign=PosPub.encodeMD5(ssign).toUpperCase();
		//String ssurl="360buy_param_json="+reques+"&access_token="+access_token+"&app_key="+app_key+"&method="+smethod+"&timestamp="+timestamp+"&v=2.0"+"&sign="+sign;
		
		String ssurl="360buy_param_json="+URLEncoder.encode(reques, "utf-8" )+
				"&access_token="+URLEncoder.encode(access_token, "utf-8" )+"&app_key="+URLEncoder.encode(app_key, "utf-8" )
				+"&method="+URLEncoder.encode(smethod, "utf-8" )+"&timestamp="+URLEncoder.encode(timestamp, "utf-8" )+"&v=2.0"+"&sign="+sign;
		
		
		surl=surl+ssurl;
		
		return HttpSend.Sendhttp(method,"",surl);
		
		

	}
	

}
