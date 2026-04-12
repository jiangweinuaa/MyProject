package com.dsc.spos.scheduler.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.OrderWeiscCreate.NewOrderList;
import com.dsc.spos.scheduler.job.OrderWeiscCreate.OrderDetail;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

/**
 * 客户端
 * 
 * @author LN 08546
 */
public class OrderPostClient {

	private Logger logger = LogManager.getLogger(OrderPostClient.class);

	public OrderPostClient() {
	}

	/**
	 * 请求微商城GetNewOrderList（主动查询未推送的订单（拉式））服务，并将结果转换为实体类
	 * @param url
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public NewOrderList getNewOrderList(String url,Map<String,Object> json) throws Exception{
		NewOrderList newOrderList=null;
		try
		{
			HelpTools.writelog_waimai("【微商城订单OrderWeiscCreate】 主动抓单接口调用URL="+url+",请求map="+json);
			String res=doPostStr(url, json);
			HelpTools.writelog_waimai("【微商城订单OrderWeiscCreate】 主动抓单接口调用URL="+url+",返回res="+res);
			ParseJson pj=new ParseJson();
			newOrderList=pj.jsonToBean(res,  new TypeToken<NewOrderList>(){});
			pj=null;
		}
		catch(Exception e)
		{
			HelpTools.writelog_waimai("【微商城订单OrderWeiscCreate】 主动抓单接口调用URL="+url+",异常："+e.getMessage());
			logger.error("\r\ngetNewOrderList错误",e);
			throw e;
		}
		return newOrderList;
	}

	/**
	 * 请求微商城GetOrderDetail（获取订单详细信息）服务，并将结果转换为实体类
	 * @param url
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public String getOrderDetailList(String url,Map<String,Object> json) throws Exception{
		OrderDetail orderDetail=null;
		String res=null;
		try{
			res=doPostStr(url, json);
			//			ParseJson pj=ParseJson.getInstance();
			//			orderDetail=pj.jsonToBean(res,  new TypeToken<OrderDetail>(){});
			//			logger.error("\r\ngetOrderDetailList返回"+res);
		}catch(Exception e){
			logger.error("\r\ngetOrderDetailList错误",e);
			throw e;
		}
		return res;
	}

	public String doPostStr(String url,Map<String,Object> map) throws Exception 
	{

		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		RequestConfig.Builder requestConfig = RequestConfig.custom();				
		//连接超时时间
		requestConfig.setConnectTimeout(5000);
		//从连接池中取连接的超时时间
		requestConfig.setConnectionRequestTimeout(5000);							
		//连接建立后，请求超时时间
		requestConfig.setSocketTimeout(20000);

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

		HttpPost httpPost = new HttpPost(url);
		
		
		String result = "";

		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		if(map!=null){
			for (Entry<String, Object> entry : map.entrySet()) {
				Object valueObj=entry.getValue();
				if(valueObj!=null){
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
				}
			}
		}

		CloseableHttpResponse response = null;

		HttpEntity entity=null;
		
		try 
		{
			httpPost.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

			//执行请求操作，并拿到结果
			response = client.execute(httpPost);

			StatusLine statusLine=response.getStatusLine();
			if(statusLine!=null)
			{
				if(statusLine.getStatusCode() != HttpStatus.SC_OK)
				{
					throw new Exception("异常");
				}
				else
				{
					//获取结果实体
					entity = response.getEntity();
					if (entity != null) 
					{
						//按指定编码转换结果实体为String类型
						result = EntityUtils.toString(entity, "UTF-8");
					}
										
				}
			}


		} 
		catch (Exception e) 
		{
			logger.info("\r\ndoPostStr-------",e);
			throw e;
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
		return result;
	}
	
	public static String sendSoapPost(String method,String url,Map<String, Object> headers,String request) throws Exception 
	{
		String res="";
		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		RequestConfig.Builder requestConfig = RequestConfig.custom();
		//连接超时时间
		requestConfig.setConnectTimeout(10000);
		//从连接池中取连接的超时时间
		requestConfig.setConnectionRequestTimeout(10000);							
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
		if(headers!=null&&!headers.isEmpty()){
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
			}
		}
		catch (Exception e) 
		{
			throw e;
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
