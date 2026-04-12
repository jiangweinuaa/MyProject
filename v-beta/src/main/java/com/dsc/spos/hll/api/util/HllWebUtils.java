package com.dsc.spos.hll.api.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Web工具类
 * 
 * @author LN 08546
 */
public abstract class HllWebUtils {

	public static String doPost(String url, String groupId, Map<String, Object> postMap,
			int connectTimeout, int readTimeout) throws Exception {

		CloseableHttpClient dhc = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(1000)
				.setSocketTimeout(5000).build();
		httpPost.setConfig(requestConfig);
		String result = "";
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		if (postMap != null) {
			for (Entry<String, Object> entry : postMap.entrySet()) {
				Object valueObj = entry.getValue();
				if (valueObj != null) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
				}
			}
		}
		try {
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpPost.setHeader("groupID", groupId);
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

//			//System.out.println("请求地址：" + url);
//			//System.out.println("请求参数：" + nvps.toString());
			// 执行请求操作，并拿到结果
			CloseableHttpResponse catResponse = dhc.execute(httpPost);
			catResponse.getStatusLine();
			StatusLine statusLine = catResponse.getStatusLine();
			if (statusLine != null) {
				int statusCode = statusLine.getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					throw new Exception("异常");
				} else {
					// 获取结果实体
					HttpEntity entity = catResponse.getEntity();
					if (entity != null) {
						// 按指定编码转换结果实体为String类型
						result = EntityUtils.toString(entity, "UTF-8");
					}
					EntityUtils.consume(entity);
					catResponse.close();
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (dhc != null) {
				try {
					dhc.close();
				} catch (IOException e) 
				{
					
				}
			}
			if (httpPost != null) {
				httpPost.abort();
			}
		}
		return result;
	}

}
