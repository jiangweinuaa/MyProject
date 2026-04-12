package com.dsc.spos.waimai;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.utils.PosPub;


public class WUXIANGUtil 
{
	
	static Logger logger = LogManager.getLogger(WUXIANGUtil.class.getName());
	/**
	 * 直接发送HTTP请求
	 */
	public static String SendWuXiang(String method,String reques,String surl,String store_code) throws IOException 
	{
		logger.info("\r\n******直接通过传入的网址调用服务"+":请求公用调用Start******\r\n");
		//surl="http://preopenapi.bigaka.net/wx-api/newOpenApi";
		surl="http://openapi.bigaka.com/wx-api/newOpenApi";
		
		String appId="$1$D.0Z3.So$H5eeED9AdL6Ooebb7vKhB/";
		String secret="$1$mi0bGFBX$CzaP4k8Ucz5HfuoKsjDQX0";
		String ts=new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
		//ts给个GUID避免重复
		ts=UUID.randomUUID().toString();
		
		String res="";

		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		RequestConfig.Builder requestConfig = RequestConfig.custom();

		//连接超时时间
		requestConfig.setConnectTimeout(5000);
		//从连接池中取连接的超时时间
		requestConfig.setConnectionRequestTimeout(5000);							
		//连接建立后，请求超时时间
		requestConfig.setSocketTimeout(60000);

		//
		SocketConfig socketConfig = SocketConfig.custom()
                .setSoKeepAlive(false)//是否检查连接状态
                .setSoLinger(1)//最多被阻塞1秒
                .setSoReuseAddress(true)//是否接收完延迟数据才关闭端口
                .setSoTimeout(10000)//调用 read() 将只阻塞此时间长度毫秒
                .setTcpNoDelay(true).build();//启动TCP_NODELAY，就意味着禁用了Nagle算法，允许小包的发送。对于延时敏感型，同时数据传输量比较小的应用，开启TCP_NODELAY选项无疑是一个正确的选择

		httpBuilder.setDefaultSocketConfig(socketConfig);
		
		httpBuilder.setDefaultRequestConfig(requestConfig.build());
		httpBuilder.disableAutomaticRetries();//禁止超时重试	
		httpBuilder.disableConnectionState();							
		httpBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0,false));//重试0次

		// 创建httpclient对象
		CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

		// 创建post方式请求对象
		HttpPost httpPost = null;

		httpPost=new HttpPost(surl);

		httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
		httpPost.setHeader("model", "order");
		httpPost.setHeader("method", method);
		
		httpPost.setHeader("store_code", store_code );
		httpPost.setHeader("app_id", appId);
		httpPost.setHeader("ts", ts);
		//处理下sign
		String sign=PosPub.encodeMD5(appId +secret +ts +reques);
		httpPost.setHeader("sign", sign);
		
		
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
		String timestamp = dfDate.format(cal.getTime()) + dfTime.format(cal.getTime());

		//
		httpPost.setEntity(new StringEntity(reques, "utf-8"));
		//httpPost.setParams(params);

		CloseableHttpResponse response = null;

		HttpEntity entity=null;

		// 执行请求操作，并拿到结果（同步阻塞）
		try 
		{
			response = client.execute(httpPost);
			// 获取结果实体
			entity = response.getEntity();

			if (entity != null) 
			{
				// 按指定编码转换结果实体为String类型
				res = EntityUtils.toString(entity, "utf-8");

				logger.info("\r\n******公用http服务"+"请求返回：" + res + "******\r\n");
			}
			else
			{
				logger.info("\r\n返回结果entity为NULL******\r\n");
			}
		}
		catch (Exception e) 
		{
			//System.out.println(e.toString());

			logger.error("\r\n******公用请求服务" +":请求报错" +e.toString()+"******\r\n");
		}
		finally 
		{
			if (entity != null) 
			{
				EntityUtils.consume(entity);
			}															

			if (response != null) 
			{
				response.close();
			}								

			if (httpPost != null) 
			{
				httpPost.releaseConnection();
			}	

			socketConfig=null;
			requestConfig=null;
			httpBuilder=null;
			
			if (client != null) 
			{
				client.close();
			}
			logger.info("\r\n******公用请求服务"+":请求调用End******\r\n");
		}
		return res;

	}
	
}
