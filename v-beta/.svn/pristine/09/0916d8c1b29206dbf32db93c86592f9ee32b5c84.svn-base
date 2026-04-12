package com.dsc.spos.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class GaoDeUtils
{

    /**
     * 根据地址查询经纬度
     *
     * @param address
     * @param key
     * @return
     */
    public static JSONObject getLngAndLat(String address, String key)
    {
        JSONObject positionObj = new JSONObject();
        
        try
        {
            // 拼接请求高德的url
            String url = "http://restapi.amap.com/v3/geocode/geo?address=" + address + "&output=JSON&key=" + key;
            // 请求高德接口
            String result = sendHttpGet(url);
            JSONObject resultJOSN = JSONObject.parseObject(result);
//            System.out.println("高德接口返回原始数据：");
//            System.out.println(resultJOSN);
            JSONArray geocodesArray = resultJOSN.getJSONArray("geocodes");
            if (geocodesArray.size() > 0)
            {
                String position = geocodesArray.getJSONObject(0).getString("location");
                String[] lngAndLat = position.split(",");
                String longitude = lngAndLat[0];
                String latitude = lngAndLat[1];
                positionObj.put("longitude", longitude);
                positionObj.put("latitude", latitude);
            }
            geocodesArray.getJSONObject(0).getString("location");


            //
            geocodesArray=null;
            resultJOSN=null;

            return positionObj;
        }
        catch (Exception e)
        {
            return positionObj;
        }
        finally
        {
            positionObj=null;
        }
    }
 
    /**
     * 发送Get请求
     *
     * @param url
     * @return
     */
    public static String sendHttpGet(String url) throws IOException
    {
        HttpGet httpGet = new HttpGet(url);

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
        httpBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));//重试0次

        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();
        String result = "";

        CloseableHttpResponse response =null;
        HttpEntity entity =null;

        try
        {
            response = client.execute(httpGet);
            entity = response.getEntity();

            if (entity != null)
            {
                result = EntityUtils.toString(entity, "UTF-8");
            }

            return result;
        }
        catch (IOException e)
        {
            return result;
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

            if (httpGet != null)
            {
                httpGet.releaseConnection();
            }

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }

            result=null;
        }
    }
}
