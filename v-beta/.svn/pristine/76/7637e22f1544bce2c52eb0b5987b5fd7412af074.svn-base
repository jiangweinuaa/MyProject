package com.dsc.spos.waimai.dianwoda;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.dsc.spos.waimai.HelpTools;

import okhttp3.*;

public class DianwodaHttpClientUtil {

	public static String dwdLogFileName = "dianwodalog";
	public static String appkey_def = "t1000316";
	public static String app_secret_def = "f6b9fe764b2557e7f7a2ad30b50d4849";
	public static String access_token_def = "TEST2018-a444-4e50-b785-f48ba984bd9c";
	
	/**
	 * 
	 * @param appkey
	 * @param app_secret
	 * @param access_token
	 * @param IsSandBox 是否测试环境 Y/N
	 * @param api 接口名称
	 * @param apiParam_json 请求内容json格式
	 * @return
	 * @throws Exception
	 */
	public static String sendPostRequest(String appkey, String app_secret, String access_token,String IsSandBox,String api,String apiParam_json) throws Exception
	{
		
			String gateway = "https://open.dianwoda.com/gateway";//正式环境
			if(IsSandBox!=null&&IsSandBox.equals("Y"))
			{
				gateway = "https://open-test.dianwoda.com/gateway";//测试环境			
				appkey = appkey_def;				
				app_secret = app_secret_def;					
				access_token = access_token_def;
				
			}
			
			
			
			String bodyStr = apiParam_json == null ? "" : apiParam_json;

	    String timestamp = Long.toString(System.currentTimeMillis());
	    String nonce = Integer.toString(ThreadLocalRandom.current().nextInt(1000000));
	    
	    String sign = DianwodaClientSigner.sign(appkey,timestamp,nonce,access_token,api,app_secret,bodyStr);
	    
	    String url = String.format(
	        "%s?appkey=%s&timestamp=%s&nonce=%s&access_token=%s&api=%s&sign=%s",
	        gateway,appkey,timestamp,nonce,access_token,api,sign);
	  
	    HelpTools.writelog_fileName("【Dianwoda接口完整URL】："+url+" 请求内容："+bodyStr, dwdLogFileName);
	    
	    
	    OkHttpClient client = new OkHttpClient();
	    /*OkHttpClient client = new OkHttpClient.Builder()
	        .connectTimeout(15, TimeUnit.SECONDS)
	        .readTimeout(15,TimeUnit.SECONDS)
	        .writeTimeout(15,TimeUnit.SECONDS)
	        .build();*/

	    MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
	    RequestBody body = RequestBody.create(jsonType, bodyStr);
	    Request request = new Request.Builder()
	            .url(url)
	            .post(body)
	            .build();

	    Call call = client.newCall(request);
	    Response response = call.execute();
	    if(response.code() != 200){
	        throw new IOException("请求gateway返回HTTP响应码:"+response.code());
	    }
	    String result = response.body().string();
			
			return result;	
		
	}
}
