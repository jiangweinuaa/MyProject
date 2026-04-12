package com.dsc.spos.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.InsertWSLOG;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.waimai.HelpTools;

public class HttpSend {
    static Logger logger = LogManager.getLogger(HttpSend.class.getName());

    /**
     * 发送HTTP请求
     *
     * @param request
     * @param sevicename
     * @param eId
     * @param shopId
     * @return
     * @throws IOException
     */
    public static String Send(String request, String sevicename, String eId, String shopId, String organizationNO, String saleno) throws IOException {
        String res = "";
        String sendType = "0";  //0.httpPost  1.ETLHttpPost
        String ETLHttpPost = "";
        res = Send(request, sevicename, eId, shopId, organizationNO, saleno, sendType, ETLHttpPost);
        return res;
    }


    public static String doPost(String url, String requestBody, Map<String, Object> headers,String requestId) throws IOException {
        //
        if (Check.Null(requestId))
        {
            requestId=PosPub.getGUID(false);
        }

        logger.info("\r\n******requestId="+requestId+"直接通过传入的网址调用服务" + ":请求公用调用Start******\r\n");

        String res = "";

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

        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpUriRequest httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        ((HttpPost) httpPost).setEntity(new StringEntity(requestBody, "utf-8"));

        StringBuffer headerStr=new StringBuffer("");
        if (headers != null && !headers.isEmpty())
        {
            Set<String> headerKeys = headers.keySet();
            for (String headerKey : headerKeys)
            {
                httpPost.setHeader(headerKey, headers.get(headerKey).toString());
                headerStr.append(headerKey+"="+headers.get(headerKey).toString()+",");
            }

            logger.info("\r\nrequestId="+requestId+" url请求内容："+url+"\r\n");
            logger.info("\r\nrequestId="+requestId+" header请求内容："+headerStr+"\r\n");
            logger.info("\r\nrequestId="+requestId+" body请求内容："+requestBody+"\r\n");
        }
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        // 执行请求操作，并拿到结果（同步阻塞）
        try {
            response = client.execute(httpPost);
            // 获取结果实体
            entity = response.getEntity();
            if (entity != null) {
                // 按指定编码转换结果实体为String类型
                res = EntityUtils.toString(entity, "utf-8");

                logger.info("\r\n******requestId="+requestId+" http服务" + "请求返回：" + res + "******\r\n");
            } else {
                logger.info("\r\nrequestId="+requestId+" 返回结果entity为NULL******\r\n");
            }
        } catch (Exception e) {
            logger.info("\r\n******requestId="+requestId+"请求服务" + ":请求报错" + e.toString() + "******\r\n");
            logger.error("\r\n******requestId="+requestId+"请求服务" + ":请求报错" + e.toString() + "******\r\n");
        } finally {
            if (entity != null) {
                EntityUtils.consume(entity);
            }

            if (response != null) {
                response.close();
            }

            if (httpPost != null) {
                httpPost.abort();
            }
            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null) {
                client.close();
            }
            logger.info("\r\n******requestId="+requestId+" 公用请求服务" + ":请求调用End******\r\n");
        }
        return res;
    }

    /**
     * 发送HTTP请求
     *
     * @param request
     * @param sevicename
     * @param eId
     * @param shopId
     * @param sendType      //0.httpPost  1.ETLHttpPost
     * @return
     * @throws IOException
     */
    public static String Send(String request, String sevicename, String eId, String shopId, String organizationNO, String saleno, String sendType, String ETLHttpPost) throws IOException
    {
        logger.info("\r\n******服务" + sevicename + ":请求ERP调用Start******\r\n");

        String res = "";

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = null;

        StringBuffer digiservice = new StringBuffer("");

        List<ProdInterface> lstProd = StaticInfo.psc.getT100Interface().getProdInterface();

        int iExistT100 = -1;//注册产品企业编码信息索引

        //是否能找到对应的企业编码
        boolean bExistEId = false;

        String hostProd = "";
        String hostIP = "";
        String hostID = "";
        String hostLang = "";
        String hostAcct = "";
        String WS_To_Cross = "";

        for (int iProd = 0; iProd < lstProd.size(); iProd++)
        {
            String prodEId = lstProd.get(iProd).geteId().getValue();
            if (prodEId.equals(eId))//找到相同企业编码
            {
                hostProd = lstProd.get(iProd).getHostProd().getValue();
                hostIP = lstProd.get(iProd).getHostIP().getValue();
                hostID = lstProd.get(iProd).getHostID().getValue();
                hostLang = lstProd.get(iProd).getHostLang().getValue();
                hostAcct = lstProd.get(iProd).getHostAcct().getValue();
                WS_To_Cross = lstProd.get(iProd).getWS_To_Cross().getValue();

                if (sendType.equals("0"))  //0.httpPost  1.ETLHttpPost
                {
                    httpPost = new HttpPost(lstProd.get(iProd).getHttpPost().getValue());
                } else if (sendType.equals("1")) {
                    httpPost = new HttpPost(ETLHttpPost);
                }
                digiservice.setLength(0);
                digiservice.append( "{\"prod\":\"" + lstProd.get(iProd).getServiceProd().getValue() + "\",\"name\":\"" + sevicename + "\",\"srvver\":\"1.0\",\"ip\":\"" + lstProd.get(iProd).getServiceIP().getValue() + "\",\"id\":\"" + lstProd.get(iProd).getServiceID().getValue() + "\"}");

                iExistT100 = iProd;
                bExistEId = true;
                break;//跳出
            }

        }

        if (bExistEId == false)
        {
            logger.error("\r\n******服务" + sevicename + ":配置文件PlugInService.XML中找不到EID=" + eId + "配置信息\r\n");
            try
            {
                InsertWSLOG.insert_WSLOG(sevicename, saleno, eId, organizationNO, "1", request, "", "-1", "配置文件PlugInService.XML中找不到EID=" + eId + "配置信息");
            }
            catch (Exception e)
            {

            }
            return "";
        }

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        Calendar cal = Calendar.getInstance();// 获得当前时间
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
        String timestamp = dfDate.format(cal.getTime()) + dfTime.format(cal.getTime());
        timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());


        StringBuffer digihost =new StringBuffer( "{\"prod\":\"" + hostProd + "\",\"ver\":\"1.0\",\"ip\":\"" + hostIP + "\",\"id\":\"" + hostID + "\",\"lang\":\"" + hostLang + "\",\"timestamp\":\"" + timestamp + "\",\"acct\":\"" + hostAcct + "\"}");


        //digi-key的要求：digi-host+digi-service+28682266
        String digikey = PosPub.encodeMD5(digihost.toString() + digiservice.toString() + "28682266");


        //*********************走CROSS方式**************
        if (WS_To_Cross.equals("1") && (sendType.equals("0")))
        {
            //请求中台的设置
            httpPost.setHeader("digi-type", "sync");
            httpPost.setHeader("digi-host", digihost.toString());
            httpPost.setHeader("digi-service", digiservice.toString());
            httpPost.setHeader("digi-key", digikey);
            httpPost.setHeader("digi-datakey", "{\"EntId\":\"" + eId + "\",\"CompanyId\":\"" + shopId + "\"}");
        }
        else
        {
            //**********************直接呼叫T100方式**********
            try
            {
                // 请求内容重组
                JSONObject t100req = new JSONObject(request);

                JSONObject host = new JSONObject();

                JSONObject service = new JSONObject();

                JSONObject datakey = new JSONObject();

                // 给host赋值
                host.put("prod", hostProd);
                host.put("ip", hostIP);
                host.put("lang", hostLang);
                host.put("acct", hostAcct);
                host.put("timestamp", timestamp);

                service.put("name", sevicename);

                service.put("prod", lstProd.get(iExistT100).getServiceProd().getValue());
                service.put("ip", lstProd.get(iExistT100).getServiceIP().getValue());
                service.put("id", lstProd.get(iExistT100).getServiceID().getValue());

                // 给datakey赋值
                datakey.put("EntId", eId);
                datakey.put("CompanyId", shopId);

                t100req.put("key", lstProd.get(iExistT100).getKey().getValue());
                t100req.put("type", lstProd.get(iExistT100).getType().getValue());


                t100req.put("host", host);
                t100req.put("service", service);
                t100req.put("datakey", datakey);

                //加入T100请求头格式，重新生成请求
                request = t100req.toString();


                //
                datakey=null;
                service=null;
                host=null;
                t100req=null;
            }
            catch (Exception e)
            {
                logger.error("\r\n******服务" + sevicename + "请求ERP异常原因：" + e.getMessage() + "******\r\n");
                try
                {
                    InsertWSLOG.insert_WSLOG(sevicename, saleno, eId, organizationNO, "1", request, "", "-1", "请求ERP异常原因：" + e.getMessage());
                }
                catch (Exception e2)
                {

                }
            }
        }

        httpPost.setEntity(new StringEntity(request, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

        // 执行请求操作，并拿到结果（同步阻塞）
        try
        {
            logger.debug("\r\n******服务" + sevicename + "请求key:" + eId + organizationNO + saleno + " 请求内容:" + request + "\r\n");
            response = client.execute(httpPost);
            // 获取结果实体
            entity = response.getEntity();

            if (entity != null)
            {
                // 按指定编码转换结果实体为String类型
                res = EntityUtils.toString(entity, "utf-8");
                //logger.info("\r\n******服务"+sevicename+"请求ERP返回：" + res + "******\r\n");
                logger.debug("\r\n******服务" + sevicename + "请求key:" + eId + organizationNO + saleno + " ERP返回内容:" + res + "\r\n");
                JSONObject jsonres = new JSONObject(res);
                JSONObject std_data_res = jsonres.getJSONObject("std_data");
                JSONObject execution_res = std_data_res.getJSONObject("execution");

                String code = execution_res.getString("code");
                String description = "";
                if (!execution_res.isNull("description"))
                {
                    description = execution_res.getString("description");
                }
                if (!code.equals("0"))
                {
                    InsertWSLOG.insert_WSLOG(sevicename, saleno, eId, organizationNO, "1", request, res, code, description);
                    //res="";
                }

                //
                execution_res=null;
                std_data_res=null;
                jsonres=null;
            }
            else
            {
                logger.error("\r\n******服务" + sevicename + "请求key:" + eId + organizationNO + saleno + " ERP返回结果entity为NULL******\r\n");
                InsertWSLOG.insert_WSLOG(sevicename, saleno, eId, organizationNO, "1", request, "", "-1", "");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());
            //logger.error("\r\n******服务" +sevicename+":请求ERP报错" +e.toString()+"******\r\n");
            logger.error("\r\n******服务" + sevicename + "请求key:" + eId + organizationNO + saleno + " 请求ERP报错" + e.toString() + "******\r\n");
            try
            {
                if (response != null)
                {
                    InsertWSLOG.insert_WSLOG(sevicename, saleno, eId, organizationNO, "1", request, response.toString(), "-1", e.toString());
                }
                else
                {
                    InsertWSLOG.insert_WSLOG(sevicename, saleno, eId, organizationNO, "1", request, "", "-1", e.toString());
                }
            }
            catch (Exception ex)
            {

            }

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }

            res=null;

            logger.info("\r\n******服务" + sevicename + ":请求ERP调用End******\r\n");
        }

    }


    public static String waimaiSend(String request, String sevicename, String waimaisevicename, SPosConfig psc, String saleno) throws IOException
    {

        String waimaiHttpPost = psc.getNewretailTransferm().getWaimaiHttpPost().getValue() + waimaisevicename;
        logger.info("\r\n******服务" + sevicename + ":请求新零售外卖接口" + waimaiHttpPost + "Start******\r\n");

        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(45000);

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(waimaiHttpPost);


        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");


        //System.out.println("request的值	" + request);
        logger.info("\r\n\n" + sevicename + "请求新零售外卖接口" + waimaisevicename + "传入参数：  " + request + "\n");

        httpPost.setEntity(new StringEntity(request, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

        try
        {
            response = client.execute(httpPost);
            // 获取结果实体
            entity = response.getEntity();

            if (entity != null)
            {
                // 按指定编码转换结果实体为String类型
                res = EntityUtils.toString(entity, "utf-8");

                logger.info("\r\n******服务" + sevicename + "请求新零售外卖接口" + waimaisevicename + "返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n******服务" + sevicename + "请求新零售外卖接口" + waimaisevicename + "返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******服务" + sevicename + ":请求新零售外卖接口" + waimaisevicename + "报错" + e.toString() + "******\r\n");

            logger.info("\r\n******服务" + sevicename + ":请求新零售外卖接口" + waimaisevicename + "报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }

            res=null;

            logger.info("\r\n******服务" + sevicename + ":请求新零售外卖接口" + waimaiHttpPost + "End******\r\n");
        }
    }

    /**
     * 请求CRM商城(旧)
     *
     * @param request
     * @param eId
     * @param microMarkServiceName
     * @return
     * @throws Exception
     */
    public static String MicroMarkSend(String request, String eId, String microMarkServiceName) throws Exception {
        String channelId = "";
        return MicroMarkSend(request, eId, microMarkServiceName, channelId);
    }

    public static String GuanYiOrderSend(JSONObject js, String eId, String microMarkServiceName) throws Exception {
        String jddjLogFileName = "GuanYiOrderGet";

        String sql = "select A.*,B.org_name from OC_ECOMMERCE A left join DCP_ORG_lang B "
                + " on A.EID=B.EID and A.SHOPID=B.Organizationno and B.lang_type='zh_CN' where A.EID='" + eId + "' and A.ECPLATFORMNO='13' and A.status='100'  ";

        List<Map<String, Object>> lisdate = StaticInfo.dao.executeQuerySQL(sql, null);
        if (lisdate != null && !lisdate.isEmpty()) {
            for (Map<String, Object> ECOMmap : lisdate) {
                String API_URL = ECOMmap.get("API_URL").toString();
                String API_KEY = ECOMmap.get("API_KEY").toString();
                String API_SECRET = ECOMmap.get("API_SECRET").toString();
                String TOKEN = ECOMmap.get("TOKEN").toString();

                //加密签名这段可以写公用方法
                js.put("appkey", API_KEY);
                js.put("sessionkey", TOKEN);
                js.put("method", microMarkServiceName);

                //处理下sign
                String sign = PosPub.encodeMD5(API_SECRET + js.toString() + API_SECRET);
                js.put("sign", sign);

                //发送的内容
                HelpTools.writelog_fileName("【同步任务发送：" + "orderList " + js.toString(), jddjLogFileName);
                StringBuffer sb = new StringBuffer();
                String res = HttpSend.SendWuXiang("", js.toString(), API_URL, sb);
                HelpTools.writelog_fileName("【同步任务返回：" + "orderList" + res, jddjLogFileName);

                return res;
            }
        }

        return "";
    }

    /**
     * 请求CRM商城(新-有channelId)
     *
     * @param request
     * @param eId
     * @param microMarkServiceName
     * @param channelId            下单渠道（公众号appid）
     * @return
     * @throws Exception
     */
    public static String MicroMarkSend(String request, String eId, String microMarkServiceName, String channelId) throws Exception
    {

        //System.out.println("调用微商城服务!");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        String httpPost = PosPub.getCRM_INNER_URL(eId);
        String key = channelId;
        String sign = channelId;
        //

        String digikey = DigestUtils.md5Hex(request + sign);
        logger.info("\r\n******调用微商城服务接口URL：" + httpPost + ",下单渠道（公众号appid）:" + channelId + ",接口Service：" + microMarkServiceName + " 接口Key ：" + key
                + " 接口Sign ：" + sign + "    Start******\r\n");
        HelpTools.writelog_waimai("\r\n******调用微商城服务接口URL：" + httpPost + ",下单渠道（公众号appid）:" + channelId + ",接口Service：" + microMarkServiceName + ",接口Key ：" + key
                + ",接口Sign ：" + sign + "    Start******\r\n");

        String temp = "{\"serviceId\":\"" + microMarkServiceName + "\",\"request\":" + request + ","
                + "\"sign\":{\"key\":\"" + key + "\",\"sign\":\"" + digikey + "\"}}";

        logger.info("\r\n******调用微商城服务接口请求Request：" + temp + " 加密后签名：" + digikey + "     Start******\r\n");
        HelpTools.writelog_waimai("\r\n******调用微商城服务接口请求Request：" + temp + " 加密后签名：" + digikey + "     Start******\r\n");
        formparams.add(new BasicNameValuePair("json", temp));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        //HttpPost httppost = new HttpPost("http://eliutong2.digiwin.com.cn/sposWeb/services/jaxrs/openapi/member");
        HttpPost httppost = new HttpPost(httpPost);
        httppost.setEntity(entity);
        //System.out.println(httppost.getURI());

        //HttpClient httpClient = new DefaultHttpClient();

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(45000);

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        CloseableHttpResponse response = null;
        HttpEntity httpEntity = null;
        String result = null;
        try
        {
            response = client.execute(httppost);
            httpEntity = response.getEntity();
            if (httpEntity != null)
            {
                try
                {
                    ////System.out.println(EntityUtils.toString(httpEntity));
                    result = EntityUtils.toString(httpEntity);
                }
                catch (IOException e)
                {

                }
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

            if (httppost != null)
            {
                httppost.releaseConnection();
            }

            if (formparams != null)
            {
                formparams.clear();
                formparams=null;
            }

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            
            logger.info("\r\n******调用微商城服务接口Service：" + microMarkServiceName + " 请求内容Request:" + request + " 返回Response ：" + result + "     End******\r\n");
            HelpTools.writelog_waimai("\r\n******调用微商城服务接口Service：" + microMarkServiceName + " 请求内容Request:" + request + " 返回Response ：" + result + "     End******\r\n");

            result=null;

    
        }
    }

    /**
     * 直接发送HTTP请求 格式json={}
     */
    public static String Sendcom(String reques, String surl) throws IOException
    {
        logger.info("\r\n******直接通过传入的网址调用服务" + ":请求公用调用Start******\r\n");

        String res = "";

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

        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = null;
        httpPost = new HttpPost(surl);
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.setEntity(new StringEntity("json=" + reques, "utf-8"));
        CloseableHttpResponse response = null;
        HttpEntity entity = null;

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

                logger.info("\r\n******公用http服务" + "请求返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******公用请求服务" + ":请求报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******公用请求服务" + ":请求调用End******\r\n");

            res=null;
        }
    }

    /**
     * 首页直接发送HTTP请求 （超时时间设置为5秒）
     */
    public static String MainInfoSendcom(String reques, String surl) throws IOException
    {
        logger.info("\r\n******直接通过传入的网址调用服务" + ":请求公用调用Start******\r\n");

        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        //requestConfig.setSocketTimeout(60000);
        requestConfig.setSocketTimeout(5000);

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

        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = null;
        httpPost = new HttpPost(surl);
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.setEntity(new StringEntity("json=" + reques, "utf-8"));
        CloseableHttpResponse response = null;
        HttpEntity entity = null;

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

                logger.info("\r\n******公用http服务" + "请求返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******公用请求服务" + ":请求报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******公用请求服务" + ":请求调用End******\r\n");

            res=null;
        }
    }

    /**
     * 根据城市名称获取adcode城市编码
     *
     * @param Cityname
     * @return
     * @throws IOException
     */
    public static String SendDistrict(String Cityname) throws IOException
    {
        logger.info("\r\n******天气预报>>获取 " + Cityname + " 的adcode >> :请求高德地图平台调用Start******\r\n");

        StringBuffer response = new StringBuffer();

        HttpURLConnection connection = null;
        try
        {
            //参数url化
            Cityname = java.net.URLEncoder.encode(Cityname, "utf-8");

            URL url = new URL("http://restapi.amap.com/v3/config/district?key=" + StaticInfo.Using_GAODEMAP_key + "&keywords=" + Cityname + "&subdistrict=1&extensions=base");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));

            String line;
            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

            reader.close();
            reader=null;

            in.close();
            in=null;

            url=null;

            return response.toString();
        }
        catch (MalformedURLException e)
        {
            logger.info("\r\n天气预报>>获取 " + Cityname + " 的adcode >> 错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        catch (IOException e)
        {
            logger.info("\r\n天气预报>>获取 " + Cityname + " 的adcode >> 错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }

            logger.info("\r\n******天气预报>>获取 " + Cityname + " 的adcode >> :请求高德地图平台调用End******\r\n");

            response=null;
        }
    }

    /**
     * 获取天气信息
     *
     * @param adcode
     * @param adcityname
     * @param API
     * @return
     * @throws IOException
     */
    public static String SendWeatherInfo(String adcode, String adcityname, String API) throws IOException
    {
        logger.info("\r\n******天气预报>>城市=" + adcityname + ",adcode=" + adcode + ">> :请求高德地图平台调用Start******\r\n");

        StringBuffer response = new StringBuffer();

        HttpURLConnection connection = null;
        try
        {
            //参数url化
            adcode = java.net.URLEncoder.encode(adcode, "utf-8");

            //默认高德地图
            URL url = new URL("http://restapi.amap.com/v3/weather/weatherInfo?city=" + adcode + "&key=" + StaticInfo.Using_GAODEMAP_key + "&extensions=all");
            if (API.equals("GAODEMAP"))
            {
                url = new URL("http://restapi.amap.com/v3/weather/weatherInfo?city=" + adcode + "&key=" + StaticInfo.Using_GAODEMAP_key + "&extensions=all");
            }
            //后续增加其他平台天气信息

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream in = connection.getInputStream();

            InputStreamReader is=new InputStreamReader(in, "utf-8");

            BufferedReader reader = new BufferedReader(is);

            String line;
            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

            reader.close();
            reader=null;

            is.close();
            is=null;

            in.close();
            in=null;

            url=null;

            return response.toString();
        }
        catch (MalformedURLException e)
        {
            logger.info("\r\n天气预报>>城市=" + adcityname + ",adcode=" + adcode + ">> 错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        catch (IOException e)
        {
            logger.info("\r\n天气预报>>城市=" + adcityname + ",adcode=" + adcode + ">> 错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }

            logger.info("\r\n******天气预报>>城市=" + adcityname + ",adcode=" + adcode + ">> :请求高德地图平台调用End******\r\n");
            response=null;
        }
    }

    /**
     * 中台监控服务
     *
     * @param invokeUrl
     * @param description
     * @return
     * @throws IOException
     */
    public static String SendNCRRestfulStatus(String invokeUrl, String description)
    {
        //logger.info("\r\n******中台监控服务>>获取 [" + description + "]" + invokeUrl + " >> :请求调用Start******\r\n");

        StringBuffer response = new StringBuffer();

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(invokeUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream in = connection.getInputStream();

            InputStreamReader is=new InputStreamReader(in, "utf-8");

            BufferedReader reader = new BufferedReader(is);

            String line;
            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

            reader.close();
            reader=null;

            is.close();
            is=null;

            in.close();
            in=null;

            url=null;

            return response.toString();
        }
        catch (MalformedURLException e)
        {
            logger.error("\r\n中台监控服务>>获取 [" + description + "]" + invokeUrl + " >> 错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        catch (IOException e)
        {
            logger.error("\r\n中台监控服务>>获取 [" + description + "]" + invokeUrl + " >> 错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }

            //logger.info("\r\n******中台监控服务>>获取 [" + description + "]" + invokeUrl + " >> :请求调用End******\r\n");
            response=null;
        }
    }

    /**
     * 直接发送HTTP请求
     */
    public static String SendWuXiang(String method, String reques, String surl, StringBuffer... sb_buff) throws IOException
    {
        //记录错误原因
        StringBuffer sb = new StringBuffer("");
        if (sb_buff.length > 0)
        {
            if (sb_buff[0] != null)
            {
                sb = sb_buff[0];
            }
        }

        logger.info("\r\n******直接通过传入的网址调用服务" + ":请求公用调用Start******\r\n");

        String res = "";

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

        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = null;

        httpPost = new HttpPost(surl);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
        if (method.equals("salesDeliver"))
        {
            httpPost.setHeader("model", "order");
            httpPost.setHeader("method", "salesDeliver");
        }
        else
        {
            httpPost.setHeader("model", "order");
            httpPost.setHeader("method", "selfInviteSync");
        }
        httpPost.setEntity(new StringEntity(reques, "utf-8"));
        CloseableHttpResponse response = null;
        HttpEntity entity = null;

        // 执行请求操作，并拿到结果（同步阻塞）
        try
        {
            logger.info("\r\n******公用http服务请求地址:" + surl + ",\r\n内容" + reques + "******\r\n");

            response = client.execute(httpPost);
            // 获取结果实体
            entity = response.getEntity();

            if (entity != null)
            {
                // 按指定编码转换结果实体为String类型
                res = EntityUtils.toString(entity, "utf-8");

                logger.info("\r\n******公用http服务" + "请求返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            sb.append(e.getMessage());

            logger.error("\r\n******公用请求服务" + ":请求报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******公用请求服务" + ":请求调用End******\r\n");

            res=null;
        }
    }

    /**
     * 直接发送HTTP请求
     */
    public static String Sendhttp(String method, String reques, String surl) throws IOException
    {
        logger.info("\r\n******直接通过传入的网址调用服务" + ":请求公用调用Start******\r\n");

        String res = "";

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

        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpUriRequest httpPost = null;
        //URI ul=new URI(surl);

        if (method.equals("POST"))
        {
            httpPost = new HttpPost(surl);
        }
        else
        {
            httpPost = new HttpGet(surl);
            //httpPost=new HttpGet(ul);
        }
        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
        if (method.equals("POST"))
        {
            ((HttpPost) httpPost).setEntity(new StringEntity(reques, "utf-8"));
        }
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
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

                logger.info("\r\n******公用http服务" + "请求返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            logger.error("\r\n******公用请求服务" + ":请求报错" + e.toString() + "******\r\n");

            return res;
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
                httpPost.abort();
            }
            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******公用请求服务" + ":请求调用End******\r\n");

            res=null;
        }
    }


    /**
     * 这个给微信扫码点餐同步返佣使用的，状态码=204表示成功
     * @param method
     * @param reques
     * @param surl
     * @return
     * @throws IOException
     */
    public static String SendWechatScanOrder(String method, String reques, String surl,String Authorization ) throws IOException
    {
        logger.info("\r\n******直接通过传入的网址调用服务" + ":请求公用调用Start******\r\n");

        String res = "";

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

        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpUriRequest httpPost = null;
        //URI ul=new URI(surl);

        if (method.equals("POST"))
        {
            httpPost = new HttpPost(surl);
        }
        else
        {
            httpPost = new HttpGet(surl);
            //httpPost=new HttpGet(ul);
        }
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        //这个是微信认证
        httpPost.setHeader("Authorization",Authorization);
        httpPost.setHeader("Accept","application/json");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36");
        //User-Agent
        //Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36

        if (method.equals("POST"))
        {
            ((HttpPost) httpPost).setEntity(new StringEntity(reques, "utf-8"));
        }
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        // 执行请求操作，并拿到结果（同步阻塞）
        try
        {
            response = client.execute(httpPost);

            // 获取结果实体,返佣接口返回的是entity=null
            //getStatusCode=204
            entity = response.getEntity();
            if (entity != null)
            {
                // 按指定编码转换结果实体为String类型
                res = EntityUtils.toString(entity, "utf-8");

                logger.info("\r\n******公用http服务" + "请求返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n返回结果entity为NULL******\r\n");
            }

            //204表示成功
            if (response.getStatusLine().getStatusCode()==204)
            {
                res="204";
            }

            return res;
        }
        catch (Exception e)
        {
            logger.error("\r\n******公用请求服务" + ":请求报错" + e.toString() + "******\r\n");

            return res;
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
                httpPost.abort();
            }
            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******公用请求服务" + ":请求调用End******\r\n");

            res=null;
        }
    }

    public static String sendShunfengPost(String method, String reques, String url) throws Exception
    {

        CloseableHttpClient client = createSSLClientDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(5000).build();
        httpPost.setConfig(requestConfig);
        String result = "";

        CloseableHttpResponse response =null;
        HttpEntity entity=null;
        try
        {
            httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
            httpPost.setEntity(new StringEntity(reques, "UTF-8"));
            HelpTools.writelog_fileName("【调用物流HttpSend-sendShunfengPost】发送url:" + url + "reques-" + reques, "ShunfengPost");
            // 执行请求操作，并拿到结果
            response = client.execute(httpPost);
            response.getStatusLine();
            StatusLine statusLine = response.getStatusLine();
            if (statusLine != null)
            {
                int statusCode = statusLine.getStatusCode();
                if (statusCode != HttpStatus.SC_OK)
                {
                    throw new Exception("异常");
                }
                else
                {
                    // 获取结果实体
                    entity = response.getEntity();
                    if (entity != null)
                    {
                        // 按指定编码转换结果实体为String类型
                        result = EntityUtils.toString(entity, "UTF-8");
                    }
                }
            }

            result = unicodeToCn(result);

            return result;
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

            if (client != null)
            {
                try
                {
                    client.close();
                }
                catch (IOException e)
                {

                }
            }

            requestConfig=null;

            HelpTools.writelog_fileName("【调用物流HttpSend-sendShunfengPost】返回result:" + result, "ShunfengPost");
            result=null;
        }
    }

    // 单个字符的正则表达式
    private static final String singlePattern = "[0-9|a-f|A-F]";
    // 4个字符的正则表达式
    private static final String pattern = singlePattern + singlePattern +
            singlePattern + singlePattern;

    /**
     * 字符串中，所有以 \\u 开头的UNICODE字符串，全部替换成汉字
     *
     * @param str
     * @return
     */
    public static String unicodeToCn(final String str)
    {
        // 用于构建新的字符串
        StringBuffer sb = new StringBuffer();
        try
        {
            // 从左向右扫描字符串。tmpStr是还没有被扫描的剩余字符串。
            // 下面有两个判断分支：
            // 1. 如果剩余字符串是Unicode字符开头，就把Unicode转换成汉字，加到StringBuilder中。然后跳过这个Unicode字符。
            // 2.反之， 如果剩余字符串不是Unicode字符开头，把普通字符加入StringBuilder，向右跳过1.
            int length = str.length();
            for (int i = 0; i < length; )
            {
                String tmpStr = str.substring(i);
                if (isStartWithUnicode(tmpStr))
                { // 分支1
                    sb.append(ustartToCn(tmpStr));
                    i += 6;
                }
                else
                { // 分支2
                    sb.append(str.substring(i, i + 1));
                    i++;
                }
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            sb.setLength(0);
            return sb.toString();
        }
        finally
        {
            sb=null;
        }
    }

    /**
     * 把 \\u 开头的单字转成汉字，如 \\u6B65 ->　步
     *
     * @param str
     * @return
     */
    private static String ustartToCn(final String str)
    {
        String res="";
        try
        {
            StringBuffer sb = new StringBuffer().append("0x")
                    .append(str.substring(2, 6));
            Integer codeInteger = Integer.decode(sb.toString());
            int code = codeInteger.intValue();
            char c = (char) code;

            res=String.valueOf(c);


            sb.setLength(0);
            sb=null;

            return res;
        }
        catch (Exception e)
        {
            return res;
        }
        finally
        {
            res=null;
        }
    }

    /**
     * 字符串是否以Unicode字符开头。约定Unicode字符以 \\u开头。
     *
     * @param str 字符串
     * @return true表示以Unicode字符开头.
     */
    private static boolean isStartWithUnicode(final String str)
    {
        if (null == str || str.length() == 0) {
            return false;
        }
        if (!str.startsWith("\\u")) {
            return false;
        }
        // \u6B65
        if (str.length() < 6) {
            return false;
        }
        String content = str.substring(2, 6);

        boolean isMatch = Pattern.matches(pattern, content);
        return isMatch;
    }

    public static CloseableHttpClient createSSLClientDefault() throws Exception
    {
        CloseableHttpClient client=null;
        try
        {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);


            client=HttpClients.custom().setSSLSocketFactory(sslsf).build();


            return client;
        }
        catch (KeyManagementException e)
        {
            logger.error("\r\nSSLUtilsErrorKetManage", e);
            //			throw e;

            return client;
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("\r\nSSLUtilsErrorNOAlgorithm", e);
            //			throw e;

            return client;
        }
        catch (KeyStoreException e)
        {
            logger.error("\r\nSSLUtilsErrorKeyStore", e);
            return client;
        }
        finally
        {
            client=null;
        }
    }


    /**
     * 虾皮API发送
     *
     * @param method
     * @param request
     * @param url
     * @param Authorization 认证
     * @return
     * @throws IOException
     */
    public static String SendShopee(String method, String request, String url, String Authorization) throws IOException
    {

        logger.info("\r\n******服务" + method + ":请求虾皮调用Start******\r\n");

        //System.setProperty("javax.net.ssl.trustStore", HttpSend.class.getResource("/config").getPath());

        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(15000);

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        //认证:api接口地址+'|'+body

        httpPost.setHeader("Authorization", Authorization);

        //
        httpPost.setEntity(new StringEntity(request, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

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

                logger.info("\r\n******服务" + method + "请求虾皮返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n******服务" + method + "请求虾皮，返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******服务" + method + ":请求虾皮报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******服务" + method + ":请求虾皮调用End******\r\n");

            res=null;
        }
    }

    public static String SendNineApp(String method, String request, String url) throws IOException
    {
        logger.info("\r\n******服务" + method + ":请求91App调用Start******\r\n");

        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(15000);

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        //
        httpPost.setEntity(new StringEntity(request, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

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

                logger.info("\r\n******服务" + method + "请求91App返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n******服务" + method + "请求91App，返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******服务" + method + ":请求91App报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******服务" + method + ":请求91App调用End******\r\n");

            res=null;
        }
    }


    public static String SendMomo(String method, String request, String url) throws IOException
    {
        logger.info("\r\n******服务" + method + ":请求momo调用Start******\r\n");

        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(15000);

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        //
        httpPost.setEntity(new StringEntity(request, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

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

                logger.info("\r\n******服务" + method + "请求momo返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n******服务" + method + "请求momo，返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******服务" + method + ":请求momo报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******服务" + method + ":请求momo调用End******\r\n");

            res=null;
        }
    }

    /**
     * 乐天API发送,POST/PUT/PATCH/DELETE/GET
     *
     * @param method
     * @param request
     * @param url
     * @param Authorization
     * @param httpMode      POST/PUT/PATCH/DELETE/GET
     * @return
     * @throws IOException
     */
    public static String SendRakuten(String method, String request, String url, String Authorization, String httpMode) throws IOException
    {
        logger.info("\r\n******服务" + method + ":请求乐天SendRakuten调用Start******\r\n");

        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(15000);

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        HttpUriRequest httpRequest = null;
        if (httpMode.toUpperCase().equals("POST"))
        {
            // 创建请求对象
            httpRequest = new HttpPost(url);

            //强制转换
            ((HttpPost) httpRequest).setEntity(new StringEntity(request, "utf-8"));

        } else if (httpMode.toUpperCase().equals("PUT"))
        {

            // 创建请求对象
            httpRequest = new HttpPut(url);

            //强制转换
            ((HttpPut) httpRequest).setEntity(new StringEntity(request, "utf-8"));
        } else if (httpMode.toUpperCase().equals("PATCH"))
        {
            // 创建请求对象
            httpRequest = new HttpPatch(url);

            //强制转换
            ((HttpPatch) httpRequest).setEntity(new StringEntity(request, "utf-8"));

        } else if (httpMode.toUpperCase().equals("DELETE"))
        {
            // 创建请求对象
            httpRequest = new MyHttpDelete(url);//默认情况下不能发送body，另外继承实现

            //强制转换
            ((MyHttpDelete) httpRequest).setEntity(new StringEntity(request, "utf-8"));

        } else//其实还有别的方式,这里不写了，调GET
        {
            // 创建请求对象
            httpRequest = new HttpGet(url);
        }

        httpRequest.setHeader("Content-type", "application/json; charset=UTF-8");

        //认证:LicenseKey+':'+SecretKey      再base64

        httpRequest.setHeader("Authorization", Authorization);


        CloseableHttpResponse response = null;

        HttpEntity entity = null;

        // 执行请求操作，并拿到结果（同步阻塞）
        try
        {
            response = client.execute(httpRequest);
            // 获取结果实体
            entity = response.getEntity();

            if (entity != null)
            {
                // 按指定编码转换结果实体为String类型
                res = EntityUtils.toString(entity, "utf-8");

                logger.info("\r\n******服务" + method + "请求乐天SendRakuten返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n******服务" + method + "请求乐天SendRakuten，返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******服务" + method + ":请求乐天SendRakuten报错" + e.toString() + "******\r\n");

            return res;
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

            if (httpRequest != null)
            {
                httpRequest = null;//
            }

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******服务" + method + ":请求乐天SendRakuten调用End******\r\n");

            res=null;
        }
    }

    /**
     * 黑猫宅急便GET
     *
     * @param method
     * @param request
     * @param apiurl
     * @return
     * @throws IOException
     */
    public static String SendEGS(String method, String request, String apiurl) throws IOException
    {

        logger.info("\r\n******接口名=" + method + "请求黑猫宅急便平台调用Start******\r\n");

        StringBuffer response = new StringBuffer();

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(apiurl + "?" + request);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream in = connection.getInputStream();

            InputStreamReader is=new InputStreamReader(in, "utf-8");
            BufferedReader reader = new BufferedReader(is);

            String line;
            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

            reader.close();
            reader=null;

            is.close();
            is=null;

            in.close();
            in=null;

            url=null;

            return response.toString();
        }
        catch (MalformedURLException e)
        {
            logger.info("\r\n接口名=" + method + "黑猫宅急便>> 错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        catch (IOException e)
        {
            logger.info("\r\n接口名=" + method + "黑猫宅急便 >>错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }

            logger.info("\r\n******接口名=" + method + "请求黑猫宅急便平台调用End******\r\n");

            response=null;
        }
    }

    /**
     * 新竹物流货运状态
     *
     * @param method
     * @param request
     * @param apiurl
     * @return
     * @throws IOException
     */
    public static String SendHtc(String method, String request, String apiurl) throws IOException
    {
        logger.info("\r\n******接口名=" + method + "请求新竹物流状态平台调用Start******\r\n");

        StringBuilder response = new StringBuilder();

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(apiurl + "?" + request);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream in = connection.getInputStream();

            InputStreamReader is=new InputStreamReader(in, "utf-8");
            BufferedReader reader = new BufferedReader(is);

            String line;
            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

            reader.close();
            reader=null;

            is.close();
            is=null;

            in.close();
            in=null;

            url=null;

            return response.toString();

        }
        catch (MalformedURLException e)
        {
            logger.info("\r\n接口名=" + method + "新竹物流状态>> 错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        catch (IOException e)
        {
            logger.info("\r\n接口名=" + method + "新竹物流状态 >>错误信息：" + e.toString() + "\r\n");

            response.setLength(0);
            return response.toString();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }

            logger.info("\r\n******接口名=" + method + "请求新竹物流状态平台调用End******\r\n");
            response=null;
        }
    }

    /**
     * 调用新上铁客站商铺交易流水接口
     *
     * @param method
     * @param tradeChange
     * @param valiKey
     * @param url
     * @return
     * @throws IOException
     */
    public static String Save(String method, List tradeChange, String valiKey, String url) throws IOException
    {

        logger.info("\r\n******服务" + method + ":请求上铁交易接口 调用Start******\r\n");
        String ss = tradeChange.toString();
        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(15000);

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

        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        //认证:api接口地址+'|'+body
        ////
        httpPost.setHeader("valiKey", valiKey);

        //
        httpPost.setEntity(new StringEntity(ss, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

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

                logger.info("\r\n******服务" + method + "请求上铁返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n******服务" + method + "请求上铁，返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******服务" + method + ":请求上铁报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******服务" + method + ":请求虾皮调用End******\r\n");

            res=null;
        }
    }

    /**
     * 雅虎商城
     *
     * @param method
     * @param request
     * @param url
     * @param signature
     * @return
     * @throws IOException
     */
    public static String SendYahoo(String method, String request, String url, String signature) throws IOException
    {
        logger.info("\r\n******服务" + method + ":请求Yahoo调用Start******\r\n");

        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(15000);

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

        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        //认证:api接口地址+'|'+body
        ////
        httpPost.setHeader("Signature", signature);

        //
        httpPost.setEntity(new StringEntity(request, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

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

                logger.info("\r\n******服务" + method + "请求虾皮返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n******服务" + method + "请求虾皮，返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******服务" + method + ":请求虾皮报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******服务" + method + ":请求虾皮调用End******\r\n");

            res=null;
        }
    }

    /**
     * deliveroo户户送外卖
     *
     * @param method
     * @param request
     * @param url
     * @return
     * @throws IOException
     */
    public static String SendDeliveroo(String method, String request, String url, String sequence_guid, String hash) throws IOException
    {
        logger.info("\r\n******服务" + method + ":请求deliveroo调用Start******\r\n");

        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(15000);

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        httpPost.setHeader("X-Deliveroo-Sequence-Guid", sequence_guid);
        httpPost.setHeader("X-Deliveroo-Hmac-Sha256", hash);

        //
        httpPost.setEntity(new StringEntity(request, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

        // 执行请求操作，并拿到结果（同步阻塞）
        try
        {
            response = client.execute(httpPost);
            // 获取结果实体
            entity = response.getEntity();

            //状态码
            String statusCode = response.getStatusLine().toString();

            //System.out.println("deliveroo返回__状态码：" + statusCode);

            logger.info("\r\n******服务" + method + "请求deliveroo返回__状态码：" + statusCode + "******\r\n");

            if (entity != null)
            {
                // 按指定编码转换结果实体为String类型
                res = EntityUtils.toString(entity, "utf-8");

                logger.info("\r\n******服务" + method + "请求deliveroo返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n******服务" + method + "请求deliveroo，返回结果entity为NULL******\r\n");
            }

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******服务" + method + ":请求deliveroo报错" + e.toString() + "******\r\n");

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******服务" + method + ":请求deliveroo调用End******\r\n");

            res=null;
        }
    }

    /**
     * 台灣綠界物流
     *
     * @param url
     * @param urlParameters
     * @return response string
     */
    public static String SendGreeWorld(String method, String url, String urlParameters, String encoding)
    {
        logger.info("\r\n******服务" + method + ":请求台湾绿界调用Start******\r\n");

        URL obj = null;
        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        BufferedReader in = null;
        InputStreamReader is=null;

        String res = "";

        try
        {
            obj = new URL(url);

            if (obj.getProtocol().toLowerCase().equals("https"))
            {
                trustAllHosts();
                connection = (HttpsURLConnection) obj.openConnection();
            }
            else
            {
                connection = (HttpURLConnection) obj.openConnection();
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.2171.71 Safari/537.36 EcPay JAVA API Version 1.1.0");
            connection.setRequestProperty("Accept-Language", encoding);
            connection.setDoOutput(true);
            wr = new DataOutputStream(connection.getOutputStream());
            wr.write(urlParameters.getBytes(encoding));
            wr.flush();
            wr.close();

            is=new InputStreamReader(connection.getInputStream(), encoding);

            in = new BufferedReader(is);
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();

            is.close();


            res = response.toString();

            return res;
        }
        catch (Exception e)
        {
            logger.error("\r\n******服务" + method + ":请求台湾绿界报错" + e.toString() + "******\r\n");

            return res;
        }
        finally
        {
            is=null;
            in = null;
            wr = null;
            connection = null;
            obj = null;

            logger.info("\r\n******服务" + method + ":请求台湾绿界调用End******\r\n");

            res=null;
        }
    }

    /**
     * 虾皮API发送
     *
     * @param method
     * @param request
     * @param url
     * @return
     * @throws IOException
     */
    public static String SendShangYou(String method, String request, String url,StringBuffer sb) throws IOException
    {

        logger.info("\r\n******服务" + method + ":请求商有云管家调用Start******\r\n");

        //System.setProperty("javax.net.ssl.trustStore", HttpSend.class.getResource("/config").getPath());

        String res = "";

        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        RequestConfig.Builder requestConfig = RequestConfig.custom();

        //连接超时时间
        requestConfig.setConnectTimeout(5000);
        //从连接池中取连接的超时时间
        requestConfig.setConnectionRequestTimeout(5000);
        //连接建立后，请求超时时间
        requestConfig.setSocketTimeout(15000);

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        //
        httpPost.setEntity(new StringEntity(request, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

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

                logger.info("\r\n******服务" + method + "请求商有云管家返回：" + res + "******\r\n");
            }
            else
            {
                logger.info("\r\n******服务" + method + "请求商有云管家，返回结果entity为NULL******\r\n");
            }

            sb.append(res);

            return res;
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());

            logger.error("\r\n******服务" + method + ":请求商有云管家报错" + e.toString() + "******\r\n");

            sb.append(e.toString());

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******服务" + method + ":请求商有云管家调用End******\r\n");

            res=null;
        }
    }


    /**
     * https 處理
     */
    private static void trustAllHosts()
    {
        X509TrustManager easyTrustManager = new X509TrustManager() {
            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{easyTrustManager};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {

        }
    }

    /**
     * 便利达康超商取门店代码,017499
     *
     * @param City1
     * @param City2
     * @param shopname
     * @return
     * @throws IOException
     */
    public static String SendCVSShopno(String City1, String City2, String shopname) throws IOException
    {
        logger.info("\r\n******便利达康>>获取 " + City1 + City2 + " 的门店代码 >> :请求便利达康平台调用Start******\r\n");

        StringBuilder response = new StringBuilder();

        String mygetshopno = "";

        HttpURLConnection connection = null;
        try
        {
            //参数url化
            String City1code = PosPub.cnToUnicode(City1);
            City1code = City1code.replace("\\u", "%u");

            String City2code = PosPub.cnToUnicode(City2);
            City2code = City2code.replace("\\u", "%u");

            //http://cvs.map.com.tw/storelist.asp?City=%u65B0%u5317%u5E02&cvsname=&cvsid=&cvstemp=&exchange=&SerID=&City2=%u6DE1%u6C34%u5340

            URL url = new URL("http://cvs.map.com.tw/storelist.asp?City=" + City1code + "&cvsname=&cvsid=&cvstemp&exchange=&SerID=&City2=" + City2code);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream in = connection.getInputStream();

            InputStreamReader is=new InputStreamReader(in, "big5");

            BufferedReader reader = new BufferedReader(is);

            String line;
            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

            reader.close();
            reader=null;

            is.close();
            is=null;

            in.close();
            in=null;

            //解析
            Document doc = Jsoup.parse(response.toString());
            Elements ele = doc.getElementsByTag("option");

            for (int i = 0; i < ele.size(); i++)
            {
                mygetshopno = ele.get(i).attr("value");
                ////System.out.println(mygetshopno);

                String shopinfo = ele.get(i).childNode(0).toString();

                ////System.out.println(shopinfo);

                String[] splitShop = shopinfo.split("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                if (splitShop[0].contains("全家淡水市鎮店"))
                {
                    break;
                }
            }

            mygetshopno = mygetshopno.trim().substring(1);

            doc = null;
            url=null;

            return mygetshopno;
        }
        catch (MalformedURLException e)
        {
            mygetshopno = "";
            logger.info("\r\n便利达康>>获取 " + City1 + City2 + " 的门店代码 >> 错误信息：" + e.toString() + "\r\n");

            return mygetshopno;
        }
        catch (IOException e)
        {
            mygetshopno = "";
            logger.info("\r\n便利达康>>获取 " + City1 + City2 + " 的门店代码 >> 错误信息：" + e.toString() + "\r\n");

            return mygetshopno;
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }

            logger.info("\r\n******便利达康>>获取 " + City1 + City2 + " 的门店代码 >> :请求便利达康平台调用End******\r\n");
            mygetshopno=null;
        }
    }

    /**
     * 7-11去门店编码
     *
     * @param shopname
     * @return
     */
    public static String Send711Shopno(String shopname)
    {
        logger.info("\r\n******7-11>>获取 " + shopname + " 的门店代码 >> :请求7-11平台调用Start******\r\n");

        URL obj = null;
        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        BufferedReader in = null;
        InputStreamReader is=null;

        String POIID = "";//门店编码

        try
        {
            //7-11
            String url = "https://emap.pcsc.com.tw/EMapSDK.aspx";

            //去除門市2字
            if (shopname.endsWith("門市"))
            {
                shopname = shopname.substring(0, shopname.length() - 2);
            }
            shopname = java.net.URLEncoder.encode(shopname, "utf-8");

            String urlParameters = "commandid=SearchStore&city=&town=&roadname=&ID=&StoreName=" + shopname + "&SpecialStore_Kind=&leftMenuChecked=&address=";

            obj = new URL(url);

            if (obj.getProtocol().toLowerCase().equals("https"))
            {
                trustAllHosts();
                connection = (HttpsURLConnection) obj.openConnection();
            }
            else
            {
                connection = (HttpURLConnection) obj.openConnection();
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.2171.71 Safari/537.36 EcPay JAVA API Version 1.1.0");
            connection.setRequestProperty("Accept-Language", "utf-8");
            connection.setDoOutput(true);
            wr = new DataOutputStream(connection.getOutputStream());
            wr.write(urlParameters.getBytes("utf-8"));
            wr.flush();
            wr.close();

            is=new InputStreamReader(connection.getInputStream(), "utf-8");

            in = new BufferedReader(is);
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
            is.close();

            org.dom4j.Document document = DocumentHelper.parseText(response.toString());
            org.dom4j.Element iMapSDKOutput = document.getRootElement();

            @SuppressWarnings("unchecked")
            List<org.dom4j.Element> childElements = iMapSDKOutput.elements();
            for (org.dom4j.Element child : childElements)
            {
                ////System.out.println(child.getName());
                if (child.getName().equals("GeoPosition"))
                {
                    List<org.dom4j.Element> myPosList = child.elements();

                    boolean bExist = false;
                    for (org.dom4j.Element poschild : myPosList)
                    {
                        ////System.out.println(poschild.getName());

                        if (poschild.getName().equals("POIID"))
                        {
                            ////System.out.println(poschild.getText());
                            POIID = poschild.getText().trim();
                        }
                        else if (poschild.getName().equals("POIName"))
                        {
                            ////System.out.println(poschild.getText());
                            if (shopname.contains(poschild.getText()))
                            {
                                bExist = true;
                                break;
                            }
                        }
                    }
                    if (bExist)
                    {
                        break;
                    }
                }
            }
            document = null;


            return POIID;
        }
        catch (Exception e)
        {
            POIID = "";
            logger.info("\r\n******7-11>>获取 " + shopname + " 的门店代码 >> :请求7-11平台报错" + e.toString() + "******\r\n");

            return POIID;
        }
        finally
        {
            in = null;
            is=null;
            wr = null;
            connection = null;
            obj = null;

            logger.info("\r\n******7-11>>获取 " + shopname + " 的门店代码 >> :请求7-11平台调用End******\r\n");

            POIID=null;
        }
    }

    /**
     * 发送HTTP请求
     *
     * @param request
     * @param sevicename
     * @param eId
     * @param shopId
     * @param organizationNO
     * @param saleno
     * @param url
     * @return
     * @throws IOException
     */
    public static String SendWDM_ERP(String request, String sevicename, String eId, String shopId, String organizationNO, String saleno, String url) throws IOException
    {
        logger.info("\r\n******服务" + sevicename + ":请求ERP调用Start******\r\n");

        String res = "";

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        httpPost.setEntity(new StringEntity(request, "utf-8"));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

        // 执行请求操作，并拿到结果（同步阻塞）
        try
        {
            logger.info("\r\n******服务" + sevicename + "请求key:" + eId + organizationNO + saleno + " 请求内容:" + request + "\r\n");
            response = client.execute(httpPost);
            // 获取结果实体
            entity = response.getEntity();

            if (entity != null)
            {
                // 按指定编码转换结果实体为String类型
                res = EntityUtils.toString(entity, "utf-8");

                logger.info("\r\n******服务" + sevicename + "请求key:" + eId + organizationNO + saleno + " ERP返回内容:" + res + "\r\n");
            }
            else
            {
                logger.error("\r\n******服务" + sevicename + "请求key:" + eId + organizationNO + saleno + " ERP返回结果entity为NULL******\r\n");
                InsertWSLOG.insert_WSLOG(sevicename, saleno, eId, organizationNO, "1", request, "", "-1", "");
            }

            return res;
        }
        catch (Exception e)
        {

            logger.error("\r\n******服务" + sevicename + "请求key:" + eId + organizationNO + saleno + " 请求ERP报错" + e.toString() + "******\r\n");
            try
            {
                if (response != null)
                {
                    InsertWSLOG.insert_WSLOG(sevicename, saleno, eId, organizationNO, "1", request, response.toString(), "-1", e.toString());
                }
                else
                {
                    InsertWSLOG.insert_WSLOG(sevicename, saleno, eId, organizationNO, "1", request, "", "-1", e.toString());
                }
            }
            catch (Exception ex)
            {

            }

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }
            logger.info("\r\n******服务" + sevicename + ":请求ERP调用End******\r\n");

            res=null;
        }
    }

    /**
     * 管易云接口发送
     *
     * @param url
     * @param secret
     * @param method
     * @param secret
     * @param method
     * @param dataMap
     * @param eId
     * @param shopId
     * @param organizationNO
     * @param saleno
     * @param sb_buff
     * @return
     * @throws IOException
     */
    public static String SendGuanyiyuan(String url, String secret, String method, Map<String, Object> dataMap, String eId, String shopId, String organizationNO, String saleno, StringBuffer... sb_buff) throws IOException
    {
        //记录错误原因
        StringBuffer sb = new StringBuffer("");
        if (sb_buff.length > 0)
        {
            if (sb_buff[0] != null)
            {
                sb = sb_buff[0];
            }
        }

        logger.info("\r\n******服务" + method + ":请求管易云调用Start******\r\n");

        String res = "";

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


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);


        ParseJson pj = new ParseJson();

        String data = secret + pj.beanToJson(dataMap) + secret;

        StringBuffer sign = new StringBuffer();
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(data.getBytes("UTF-8"));
            for (int i = 0; i < bytes.length; i++)
            {
                String hex = Integer.toHexString(bytes[i] & 0xFF);
                if (hex.length() == 1)
                {
                    sign.append("0");
                }
                sign.append(hex.toUpperCase());
            }
            md=null;
        }
        catch (NoSuchAlgorithmException e)
        {
            sb.append(e.getMessage());

            logger.error("\r\n******服务" + method + "请求key:" + eId + organizationNO + saleno + " 请求管易云MD5加密报错" + e.toString() + "******\r\n");

            e.printStackTrace();
        }

        //加签名
        dataMap.put("sign", sign.toString());
        sign = null;

        //
        String requestStr = pj.beanToJson(dataMap);
        pj = null;

        httpPost.setEntity(new StringEntity(URLEncoder.encode(requestStr, "UTF-8"), ContentType.create("text/json", "UTF-8")));

        CloseableHttpResponse response = null;

        HttpEntity entity = null;

        // 执行请求操作，并拿到结果（同步阻塞）
        try
        {
            logger.info("\r\n******服务" + method + "请求key:" + eId + organizationNO + saleno + " 请求管易云内容:" + requestStr + "\r\n");
            response = client.execute(httpPost);
            // 获取结果实体
            entity = response.getEntity();

            if (entity != null)
            {
                // 按指定编码转换结果实体为String类型
                res = EntityUtils.toString(entity, "utf-8");

                logger.info("\r\n******服务" + method + "请求key:" + eId + organizationNO + saleno + " 管易云返回内容:" + res + "\r\n");
            }
            else
            {
                logger.error("\r\n******服务" + method + "请求key:" + eId + organizationNO + saleno + " 管易云返回结果entity为NULL******\r\n");
                InsertWSLOG.insert_WSLOG(method, saleno, eId, organizationNO, "1", requestStr, "", "-1", "");
            }

            return res;
        }
        catch (Exception e)
        {
            sb.append(e.getMessage());

            logger.error("\r\n******服务" + method + "请求key:" + eId + organizationNO + saleno + " 请求管易云报错" + e.toString() + "******\r\n");
            try
            {
                if (response != null)
                {
                    InsertWSLOG.insert_WSLOG(method, saleno, eId, organizationNO, "1", requestStr, response.toString(), "-1", e.toString());
                }
                else
                {
                    InsertWSLOG.insert_WSLOG(method, saleno, eId, organizationNO, "1", requestStr, "", "-1", e.toString());
                }
            }
            catch (Exception ex)
            {

            }

            return res;
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

            socketConfig = null;
            requestConfig = null;
            httpBuilder = null;

            if (client != null)
            {
                client.close();
            }

            logger.info("\r\n******服务" + method + ":请求管易云调用End******\r\n");

            res=null;
        }
    }

}
