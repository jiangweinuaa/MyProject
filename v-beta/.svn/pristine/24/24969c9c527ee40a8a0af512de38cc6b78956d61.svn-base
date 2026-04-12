package com.dsc.spos.utils;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class RRKDUtil {
private static final Logger log = LoggerFactory.getLogger(RRKDUtil.class);
	
	/**
	 * 创建http头信息
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @return
	 * @author  longyingan 工号
	 * @see [类、类#方法、类#成员]
	 */
	private static  Header[] createCommonHeader(JSONObject params) {
        Header[] headers = new Header[4];
        headers[0] = new BasicHeader("timestamp", params.getString("timestamp"));
        headers[1] = new BasicHeader("charset", "UTF-8");
        headers[2] = new BasicHeader("Accept", "application/json");
        headers[3] = new BasicHeader("Content-Type", "application/json");
        return headers;
    }
	
	/**
     * 执行调用第三方API接口
     * @param url 调用接口的 url
     * @param params 参数列表
     * @return 调用返回的结果
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static JSONObject sendPost(String url,JSONObject params) throws ClientProtocolException, IOException{
        Header[] headers = createCommonHeader(params);
        StringEntity entity = new StringEntity(params.toString(),"utf-8");//解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        //entity.setContentType("text/html");
        entity.setContentType("text/html");
        
        HttpClientBuilder httpClient = HttpClientBuilder.create();
        HttpPost method = new HttpPost(url);
        method.setEntity(entity);
        method.setHeaders(headers);
        HttpResponse result = httpClient.build().execute(method);

        String resData = EntityUtils.toString(result.getEntity());
        log.info("调用第三方API接口返回结果为：{}>参数： {}>地址:{}",resData,params,url);
        JSONObject resultJson = JSONObject.parseObject(resData);
        return resultJson;
    }
	

}
