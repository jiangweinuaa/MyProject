package com.dsc.spos.waimai;

import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WMDYUtilTools {

    public static String redis_key = "DYWM_Token";
    private static String logFileName_token = "douyinWMtoken";

    public static String GetTokenInRedis(boolean isTest,String client_key,String client_secret) throws Exception
    {
        String accessToken = "";
        try
        {
            if (isTest)
            {
                //测试环境，直接去数据库
                accessToken = GetTestTokenInDB(client_key,client_secret);
                return accessToken;
            }
            long curTimestmap = System.currentTimeMillis()/1000;
            RedisPosPub redis = new RedisPosPub();
            String CurRedisKey = redis_key + "_" + client_key;
            String accessTokenStr = redis.getString(CurRedisKey);//包含了过期时间戳
            boolean isNeedGetOnLine = false;//是否需要在线获取，调用CRM接口获取
            if (accessTokenStr==null||accessTokenStr.isEmpty())
            {
                isNeedGetOnLine = true;
            }
            else
            {
                long token_expires_in = 0L;
                try
                {
                    String[] ss =accessTokenStr.split("&");
                    accessToken = ss[0];
                    token_expires_in = Long.parseLong(ss[1]);
                }
                catch (Exception e)
                {
                    isNeedGetOnLine = true;
                }

                //如果获取的token为空，或者时间戳小于当前时间
                if (accessToken==null||accessToken.isEmpty()||token_expires_in<curTimestmap)
                {
                    isNeedGetOnLine = true;
                }
                else
                {
                    return accessToken;
                }

            }

            if (isNeedGetOnLine)
            {
                accessToken = GetTokenInCRM(isTest,client_key,client_secret);
            }


        }
        catch (Exception e)
        {

        }
        return accessToken;
    }

    /**
     * 从crm接口获取最新token
     * @param isTest
     * @param client_key
     * @param client_secret
     * @return
     * @throws Exception
     */
    public static String GetTokenInCRM(boolean isTest,String client_key,String client_secret) throws Exception
    {
        String accessToken = "";
        try
        {
            String CrmUrl = "";//http://IP/crmService/sposWeb/openapi/member
            if(!Check.Null(StaticInfo.CRM_INNER_URL))
            {
                CrmUrl=StaticInfo.CRM_INNER_URL;//http://localhost:8010/sposWeb/openapi/member
            }
            else
            {
                String sql = null;
                sql="select ITEMVALUE from platform_basesettemp  WHERE ITEM = 'CrmUrl' and ITEMVALUE is not null order by update_time desc";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false)
                {
                    for (Map<String, Object> map : getQData)
                    {
                        //http://IP/crmService/sposWeb/openapi/member
                        //http://retaildev.digiwin.com.cn/crmService_3.0/sposWeb/openapi/member
                        //http://xls.bawangcanyin.com/crmService/sposWeb/services/jaxrs/openapi/member
                        String URL=getQData.get(0).get("ITEMVALUE").toString();
                        if (URL==null||URL.trim().isEmpty())
                        {
                            continue;
                        }
                        //有2种格式
                        URL = URL.replace("/sposWeb/services/jaxrs/openapi/member","/sposWeb/openapi/member").trim();
                        if (!URL.contains("/sposWeb/openapi/member"))
                        {
                            continue;
                        }
                        if (URL.toUpperCase().contains("//IP"))
                        {
                            continue;
                        }
                        if (URL.toUpperCase().contains("CRMSERVICE_3.0"))
                        {
                            String sss= "1111";
                            String sss2= "1111";
                            //continue;
                        }
                        //找到一个就break
                        CrmUrl = URL;
                        break;
                    }

                }
            }
            if (CrmUrl==null||CrmUrl.trim().isEmpty())
            {
                return "";
            }

            String douyinTokenUrl = CrmUrl.replace("/sposWeb/openapi/member","/sposWeb/douyin/token");
            if (douyinTokenUrl.endsWith("/"))
            {

            }
            else
            {
                douyinTokenUrl +="/";
            }
            douyinTokenUrl += "?client_key="+client_key+"&client_secret="+client_secret;
            long curTimestamp = Calendar.getInstance().getTimeInMillis()/1000;//当前时间戳，转成秒
            String resStr = HttpSend.Sendhttp("POST","",douyinTokenUrl);
            if (resStr==null||resStr.isEmpty())
            {
                return "";
            }
            JSONObject resJson = new JSONObject(resStr);
            String success = resJson.optString("success","");
            String serviceDescription = resJson.optString("serviceDescription","");
            String access_token = resJson.optString("access_token","");//包含时间戳
            if ("true".equalsIgnoreCase(success)&&!access_token.isEmpty())
            {
                String[] ss =access_token.split("&");
                accessToken = ss[0];
                long token_expires_in = Long.parseLong(ss[1]);
                long exprieSeconds = token_expires_in - curTimestamp;
                //写缓存
                RedisPosPub redis = new RedisPosPub();
                String CurRedisKey = redis_key + "_" + client_key;
                boolean nret = redis.setString(CurRedisKey, access_token);
                if (nret)
                {
                    HelpTools.writelog_waimai("【写抖音外卖DYWM_Token缓存】成功！"+ access_token+" redis_key:"+CurRedisKey);
                }
                else
                {
                    HelpTools.writelog_waimai("【写抖音外卖DYWM_Token缓存】失败！"+ access_token+" redis_key:"+CurRedisKey);
                }
                boolean nret2 =	redis.setExpire(CurRedisKey,new Long(exprieSeconds).intValue());
                if (nret2)
                {
                    HelpTools.writelog_waimai("【设置抖音外卖DYWM_Token缓存过期时间】成功！"+ exprieSeconds+" redis_key:"+CurRedisKey);
                }
                else
                {
                    HelpTools.writelog_waimai("【设置抖音外卖DYWM_Token缓存过期时间】失败！"+ exprieSeconds+" redis_key:"+CurRedisKey);
                }
            }
            else
            {
                return "";
            }

        }
        catch (Exception e)
        {

        }

        return accessToken;

    }

    /**
     * 测试环境token是固定的，走数据库查询
     * @param client_key
     * @param client_secret
     * @return
     * @throws Exception
     */
    private static String GetTestTokenInDB(String client_key,String client_secret) throws Exception
    {
        String accessToken = "";
        try
        {
            String sql = null;
            sql="select TOKEN from DCP_ECOMMERCE where token is not null and  loaddoctype='"+ orderLoadDocType.DYWM+"' and apikey='"+client_key+"' order by LASTMODITIME desc";
            List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
            if (getQData != null && getQData.isEmpty() == false)
            {
                for (Map<String, Object> map : getQData)
                {
                    String token = map.get("TOKEN").toString();
                    if (token==null||token.trim().isEmpty())
                    {
                        continue;
                    }
                    else
                    {
                        accessToken = token;
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {

        }

        return accessToken;

    }

    /**
     * 调用抖音post请求
     * @param url 接口地址
     * @param paramsHeader 请求header
     * @param req 请求body
     * @return
     * @throws Exception
     */
    public static String postJson(String url, Map<String,Object> paramsHeader, String req) throws Exception
    {
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

        httpPost=new HttpPost(url);
        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
        if (paramsHeader!=null&&!paramsHeader.isEmpty())
        {
            Set<Map.Entry<String, Object>> entry = paramsHeader.entrySet();
            for (Map.Entry<String, Object> s : entry)
            {
                try {
                    String key = s.getKey();
                    String value = s.getValue()==null?"":s.getValue().toString();
                    if (key==null||key.trim().isEmpty()||value.trim().isEmpty())
                    {
                        continue;
                    }
                    httpPost.setHeader(key, value);
                }
                catch (Exception e)
                {

                }


            }
        }
        httpPost.setEntity(new StringEntity(req, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entityRes=null;

        try
        {
            response = client.execute(httpPost);
            // 获取结果实体
            entityRes = response.getEntity();
            if(entityRes!=null)
            {
                res = EntityUtils.toString(entityRes, "utf-8");
            }



        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        finally
        {
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

    /**
     * 抖音get请求
     * @param url
     * @param paramsHeader
     * @return
     * @throws Exception
     */
    public static String getJson(String url, Map<String,Object> paramsHeader) throws Exception
    {
        CloseableHttpClient dhc = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
        //httpGet.setHeader("access-token",token);
        if (paramsHeader!=null&&!paramsHeader.isEmpty())
        {
            Set<Map.Entry<String, Object>> entry = paramsHeader.entrySet();
            for (Map.Entry<String, Object> s : entry)
            {
                try {
                    String key = s.getKey();
                    String value = s.getValue()==null?"":s.getValue().toString();
                    if (key==null||key.trim().isEmpty()||value.trim().isEmpty())
                    {
                        continue;
                    }
                    httpGet.setHeader(key, value);
                }
                catch (Exception e)
                {

                }


            }
        }
        String result = "";

        try {
            CloseableHttpResponse hr = dhc.execute(httpGet);
            HttpEntity he = hr.getEntity();
            if (he != null) {
                result = EntityUtils.toString(he, "UTF-8");
                //logger.info("\r\ndoGetReturn-----------------" + result);

            }
        } catch (Exception e) {
            //logger.debug("\r\ndoGetStr-------"+e);
        } finally {
            if (dhc != null) {
                try {
                    dhc.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (httpGet != null) {
                httpGet.abort();
            }
        }
        return result;
    }

}
