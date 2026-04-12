package com.dsc.spos.thirdpart.youzan.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.dsc.spos.thirdpart.youzan.YouZanConstants;


/**
 * Web工具类
 * 
 * @author LN 08546
 */



public abstract class YouZanWebUtils {
	
	/**
	 * 日志文件名称
	 */
	public static String logFileName = YouZanConstants.clientLogFileName;
	
	
//	public static byte[] digest(String signStr) {
//        MessageDigest md5Instance = null;
//        try {
//            md5Instance = MessageDigest.getInstance("MD5");
//            md5Instance.update(signStr.getBytes("utf-8"));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return md5Instance.digest();
//    }
//	
//	public static String byte2Hex(byte[] bytes) {
//         char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
//                 'A', 'B', 'C', 'D', 'E', 'F' };
//         int j = bytes.length;
//         char str[] = new char[j * 2];
//         int k = 0;
//         for (byte byte0 : bytes) {
//             str[k++] = hexDigits[byte0 >>> 4 & 0xf];
//             str[k++] = hexDigits[byte0 & 0xf];
//         }
//         return new String(str);
//     }
//	 
//	
//	 public static String sendComSSL(String method,String url,Map<String, Object> headers,String request) throws IOException, URISyntaxException {
//		 String res="";
//		 HttpClientBuilder httpBuilder = HttpClientBuilder.create();
//		 RequestConfig.Builder requestConfig = RequestConfig.custom();
//		 
//		 //连接超时时间
//		 requestConfig.setConnectTimeout(5000);
//		 //从连接池中取连接的超时时间
//		 requestConfig.setConnectionRequestTimeout(5000);							
//		 //连接建立后，请求超时时间
//		 requestConfig.setSocketTimeout(30000);
//		 
//		 
//		 httpBuilder.setDefaultRequestConfig(requestConfig.build());
//		 httpBuilder.disableAutomaticRetries();//禁止超时重试	
//		 httpBuilder.disableConnectionState();							
//		 httpBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0,false));//重试0次
//		 
//		 /**
//		  * @跳过域名检查
//		  */
//		 SSLContext sslContext = null;
//		 try {
//			 sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
//				 @Override
//				 public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//					 return true;
//				 }
//				 
//			 }).build();
//			 
//		 } catch (Exception e) {
//			 //logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"exec func Sendcom error", e);
//		 }
//		 
//		 HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
//		 SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
//		 httpBuilder.setSSLSocketFactory(connectionFactory);
//		 
//		 
//		 // 创建httpclient对象
//		 CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();
//		 
//		 // 创建post方式请求对象
//		 HttpPost httpPost = new HttpPost(url);; 
//		 
//		 if(headers==null||headers.isEmpty()){
//			 headers=new HashMap<String, Object>();
//			 headers.put("Content-Type", "application/json;charset=UTF-8");
//		 }
//		 if(headers!=null&&!headers.isEmpty()){
//			 // 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
//			 Set<String> se = headers.keySet();
//			 for (String headerSet : se) {
//				 // 循环
//				 httpPost.setHeader(headerSet, headers.get(headerSet).toString().trim());
//			 }
//		 }
//		 
//		 httpPost.setEntity(new StringEntity(request, "utf-8"));
//		 CloseableHttpResponse response = null;
//		 
//		 HttpEntity entity=null;
//		 
//		 // 执行请求操作，并拿到结果（同步阻塞）
//		 try 
//		 {
//			 response = client.execute(httpPost);
//			 // 获取结果实体
//			 entity = response.getEntity();
//			 
//			 if (entity != null) 
//			 {
//				 // 按指定编码转换结果实体为String类型
//				 res = EntityUtils.toString(entity, "utf-8");
//			 }
//			 else
//			 {
//				 YouZanUtils.writelogFileName(logFileName, "\r\n******服务"+method+ "返回结果entity为NULL******\r\n");
//			 }
//		 }
//		 catch (Exception e) 
//		 {
//			 YouZanUtils.writelogFileName(logFileName, "\r\n******服务" +method+":报错" +e.toString()+"******\r\n");
//			 throw e;
//		 }
//		 finally 
//		 {
//			 if (entity != null) 
//			 {
//				 EntityUtils.consume(entity);
//			 }															
//			 
//			 if (response != null) 
//			 {
//				 response.close();
//			 }								
//			 
//			 if (httpPost != null) 
//			 {
//				 httpPost.abort();
//				 //httpPost.releaseConnection();
//			 }	
//			 
//			 if (client != null) 
//			 {
//				 client.close();
//			 }	
//			 //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******公用请求服务"+":请求调用End******\r\n");
//		 }
//		 return res;
//		 
//	 }
//	 
//	 
//	public static String sendPost(String method,String url,Map<String, Object> headers,String request) throws Exception
//	{
//		String res="";
//		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
//		RequestConfig.Builder requestConfig = RequestConfig.custom();
//		//连接超时时间
//		requestConfig.setConnectTimeout(5000);
//		//从连接池中取连接的超时时间
//		requestConfig.setConnectionRequestTimeout(5000);							
//		//连接建立后，请求超时时间
//		requestConfig.setSocketTimeout(15000);
//		SocketConfig socketConfig = SocketConfig.custom()
//                .setSoKeepAlive(false)//是否检查连接状态
//                .setSoLinger(1)//最多被阻塞1秒
//                .setSoReuseAddress(true)//是否接收完延迟数据才关闭端口
//                .setSoTimeout(10000)//调用 read() 将只阻塞此时间长度毫秒
//                .setTcpNoDelay(true).build();//启动TCP_NODELAY，就意味着禁用了Nagle算法，允许小包的发送。对于延时敏感型，同时数据传输量比较小的应用，开启TCP_NODELAY选项无疑是一个正确的选择
//
//		httpBuilder.setDefaultSocketConfig(socketConfig);
//		
//		httpBuilder.setDefaultRequestConfig(requestConfig.build());
//		httpBuilder.disableAutomaticRetries();//禁止超时重试	
//		httpBuilder.disableConnectionState();							
//		httpBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0,false));//重试0次
//		
//		// 创建httpclient对象
//		CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();
//
//		// 创建post方式请求对象
//		HttpPost httpPost = new HttpPost(url);
//		if(YouZanUtils.isNotEmpty(headers)){
//			// 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
//			Set<String> se = headers.keySet();
//			for (String headerSet : se) {
//				// 循环
//				httpPost.setHeader(headerSet, headers.get(headerSet).toString().trim());
//			}
//		}
//					
//		if(request!=null&&!request.trim().isEmpty()){
//			httpPost.setEntity(new StringEntity(request, "UTF-8"));
//		}
//		CloseableHttpResponse response = null;
//		HttpEntity entity=null;
//
//		// 执行请求操作，并拿到结果（同步阻塞）
//		try 
//		{
//			response = client.execute(httpPost);
//			// 获取结果实体
//			entity = response.getEntity();
//			if (entity != null) 
//			{
//				// 按指定编码转换结果实体为String类型
//				res = EntityUtils.toString(entity, "UTF-8");
//			}
//			else
//			{
//				YouZanUtils.writelogFileName(logFileName, "\r\n******服务"+method+ "返回结果entity为NULL******\r\n");
//			}
//		}
//		catch (Exception e) 
//		{
//			YouZanUtils.writelogFileName(logFileName, "\r\n******服务" +method+":报错" +e.toString()+"******\r\n");
//		}
//		finally 
//		{
//			if (entity != null) 
//			{
//				EntityUtils.consume(entity);
//			}															
//
//			if (response != null) 
//			{
//				response.close();
//			}								
//
//			if (httpPost != null) 
//			{
//				httpPost.releaseConnection();
//			}	
//			socketConfig=null;
//			requestConfig=null;
//			httpBuilder=null;
//			if (client != null) 
//			{
//				client.close();
//			}	
//		}
//		return res;
//	}
	

}
