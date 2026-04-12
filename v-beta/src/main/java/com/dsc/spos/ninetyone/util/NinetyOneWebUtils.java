package com.dsc.spos.ninetyone.util;

import java.util.Map;
import java.util.Set;

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

import com.dsc.spos.ninetyone.NinetyOneConstants;


/**
 * Web工具类
 * 
 * @author LN 08546
 */



public abstract class NinetyOneWebUtils {
	
	/**
	 * 日志文件名称
	 */
	public static String logFileName = NinetyOneConstants.clientLogFileName;
	
	/**
	 * @param method 调用的91APP函数名，用于记录日志，编号查错
	 * @param url 调用91APP的完整URL
	 * @param headers headers部分 例：x-api-key:81leLwxuLKe33JKlTyg04801mvniD1g5Nv2HTHwa||Content-Type:application/json
	 * @param request 参数 例：{"Id":"123456"}
	 * @return
	 * @throws Exception
	 */
	public static String sendNineApp(String method,String url,Map<String, Object> headers,String request) throws Exception 
	{
		String res="";
		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		RequestConfig.Builder requestConfig = RequestConfig.custom();
		//连接超时时间
		requestConfig.setConnectTimeout(NinetyOneConstants.CONNECT_TIME_OUT);
		//从连接池中取连接的超时时间
		requestConfig.setConnectionRequestTimeout(5000);							
		//连接建立后，请求超时时间
		requestConfig.setSocketTimeout(15000);
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
		HttpPost httpPost = new HttpPost(url);
		if(NinetyOneUtils.isNotEmpty(headers)){
			// 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
			Set<String> se = headers.keySet();
			for (String headerSet : se) {
				// 循环
				httpPost.setHeader(headerSet, headers.get(headerSet).toString().trim());
			}
		}
					
		if(request!=null&&!request.trim().isEmpty()){
			httpPost.setEntity(new StringEntity(request, "UTF-8"));
		}
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
				res = EntityUtils.toString(entity, "UTF-8");
			}
			else
			{
				NinetyOneUtils.writelogFileName(logFileName, "\r\n******服务"+method+ "请求91App,返回结果entity为NULL******\r\n");
			}
		}
		catch (Exception e) 
		{
			NinetyOneUtils.writelogFileName(logFileName, "\r\n******服务" +method+":请求91App报错" +e.toString()+"******\r\n");
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
		}
		return res;
	}
	

}
