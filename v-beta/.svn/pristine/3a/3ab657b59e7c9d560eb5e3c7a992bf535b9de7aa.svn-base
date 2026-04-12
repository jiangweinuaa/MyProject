package com.dsc.spos.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.waimai.HelpTools;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * http://172.16.2.100/archives/integrate_dap_cloud_app#%E4%BA%A4%E4%BA%92%E6%B5%81%E7%A8%8B-1
 * 三方应用集成云上应用说明手册
 *
 * 1.3 稳态应用-无地中台
 *
 *
 *
 * Digi-Middleware-Auth-App：eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6Ik5SQ19BSUluZm8iLCJzaWQiOjB9.WDP41DCVM_XbWL8Kuc9EEbpUk928rjuwH4EMKxRB3Fg
 * app-secret : i1QnbbOdO449r7uBPVj2VyyhI7+8YDpBO/Dtt5gmKNH0Cf1he6GLNGsW1QQGDDdU
 * 产品授权码: 99UD222fH37CnPBor1Fqk9STBKH8HH2p+Frpu4bS1bpKytmuP6deXTK3HwwpGuC3bHrnHOaKY/nfM10HerumnA==
 *
 */
public class AI_Digiwin
{
    
    static Logger logger = LogManager.getLogger(AI_Digiwin.class.getName());
    
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 245;
    
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;
    
    private static final String IV_STRING = "ghUb#er57HBh(u%g";
    
    
    /**
     * 获取AI访问的accessToken
     * @return
     * @throws Exception
     *
    private String licenseCode; //产品授权码    鼎捷云-控制台-应用管理中的 IndepthAl智能体(kai-wis) 的授权码 	"99UD222fH37CnPBor1Fqk9STBKH8HH2p+Frpu4bS1bpKytmuP6deXTK3HwwpGuC3bHrnHOaKY/nfM10HerumnA=="
    private String appId; //应用AppId  中间件管理系统-应用管理中获得 NRC_AIInfo
    private String appToken; //应用AppToken	中间件管理系统-应用管理中获得 "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6Ik5SQ19BSUluZm8iLCJzaWQiOjB9.WDP41DCVM_XbWL8Kuc9EEbpUk928rjuwH4EMKxRB3Fg";
    private String appSecret; //应用AppSecret 中间件管理系统-应用管理中获得    "i1QnbbOdO449r7uBPVj2VyyhI7+8YDpBO/Dtt5gmKNH0Cf1he6GLNGsW1QQGDDdU";
    private String userId; //鼎捷云-控制台-用户管理-归户管理中获得 IR0400
     
     */
    public static String getAI_AccessToken(String licenseCode,String appId,String appToken,String appSecret,String userId,String url ) throws Exception
    {
        String accessToken="";
        
        //1.客户端生成公私钥
        HashMap<String,String> keyMap = getKeyPairMap();
        String clientPublicKey = keyMap.get("publicKey");
        String privateKey = keyMap.get("privateKey");
        
        String requestId=PosPub.getGUID(false);
        
        String requestBody="";
        Map<String, Object> headers=new HashMap<>();
        
        //1.3.2.1 获取RSA公钥
        String path="/api/iam/v2/identity/publickey";
        headers.put("Content-Type","application/json");
        headers.put("digi-middleware-auth-app",appToken);
        
        //{"publicKey":"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq7CIjfdOxx/HcMm4pv37q8Cc43+FwNVTXtYZbDbAk31BfVLnQ4qfal0NboRH5qlDH0Iv7QsjmhKNPZLidhM2Ffy4EUgCW5nGFU0SIsClCXBUBUU6Ud520zjkyZZkP7oImUtsI3rVwCp9yOCgN85IXu+IqX3xpEUqd2YKQ9/yXC9zxO+j2ksDUXXhUF54eFHncdwgE/il874XZCrfOjEZ6ovW1oIsfCcNvBJxgG01eBevCQF88sw2amAyb3oV6GIwMG4FxVSBXBX4cQ/ejqS680GKLYgTCZ89/mIzqtoFa7f0oXFSZ5MKoNtZbfh7QAKFF3i9Wj51urLQ96MjlDuV2wIDAQAB"}
        
        
        StringBuffer sb1 = new StringBuffer();
        headers.forEach((key1, value1) -> sb1.append(key1).append(": ").append(value1).append(", "));
        HelpTools.writelog_fileName("鼎捷getAI_AccessToken GET请求1 url:"+url+path+" ", "digiwin_ai");
        HelpTools.writelog_fileName("鼎捷getAI_AccessToken GET请求1 headers: "+sb1, "digiwin_ai");
        HelpTools.writelog_fileName("鼎捷getAI_AccessToken GET请求1 requestBody:" +requestBody, "digiwin_ai");
        
        String res=Sendhttp("GET",requestBody,url+path,headers,requestId);
        HelpTools.writelog_fileName("鼎捷getAI_AccessToken GET返回1 :"+res+" ", "digiwin_ai");
        
        
        if (!Check.Null(res))
        {
            if ((res.startsWith("{") && res.endsWith("}")) || (res.startsWith("[") && res.endsWith("]")))
            {
                JSONObject jsonObject=JSON.parseObject(res);
                String serverPublicKey=jsonObject.getString("publicKey");
                //System.out.println("publicKey="+serverPublicKey);
                
                //3.根据服务端公钥加密客户端公钥
                String encryptPublicKey = encryptByPublicKey(clientPublicKey, serverPublicKey);
                
                //1.3.2.2 获取AES加密秘钥
                path="/api/iam/v2/identity/aeskey";
                headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("digi-middleware-auth-app",appToken);
                JSONObject req=new JSONObject();
                req.put("clientEncryptPublicKey",encryptPublicKey);
                requestBody=req.toJSONString();
                
                
                
                StringBuffer sb2 = new StringBuffer();
                headers.forEach((key2, value2) -> sb2.append(key2).append(": ").append(value2).append(", "));
                HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST请求2 url:"+url+path+" ", "digiwin_ai");
                HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST请求2 headers: "+sb2, "digiwin_ai");
                HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST请求2 requestBody:" +requestBody, "digiwin_ai");
                
                res=Sendhttp("POST",requestBody,url+path,headers,requestId);
                HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST返回2 :"+res+" ", "digiwin_ai");
                
                //System.out.println(res);
                //{"encryptAesKey":"FVluIgb/qIG29xGIwW+t4ZlHol380lNigH9d/T5Rce6EaYN314IBzuynoMADuZVhT8UUmse9cSs8ZK94oDoFWGZqsG03xi4aIepZdlbcy7kf9NTIU+QKG6RDRNfDr7CDo7y6ZgKzVeUc3D+r/TYToC9ws863UqzhWfNw4z/nVTA="}
                if (!Check.Null(res))
                {
                    if ((res.startsWith("{") && res.endsWith("}")) || (res.startsWith("[") && res.endsWith("]")))
                    {
                        jsonObject=JSON.parseObject(res);
                        String encryptAesKey=jsonObject.getString("encryptAesKey");
                        //System.out.println("encryptAesKey="+encryptAesKey);
                        
                        //5.根据客户端私有解密加密的aes的key值
                        String aesKey = new String(decryptByPrivateKey(Base64.decodeBase64(encryptAesKey), privateKey));
                        
                        //加密授权码,根据AES秘钥加密授权码
                        String encryptSecretKey = aesEncryptByBase64(licenseCode, aesKey);
                        
                        //1.3.2.3 获取租户token信息
                        path="/api/iam/v2/eai";
                        headers=new HashMap<>();
                        headers.put("Content-Type","application/json");
                        headers.put("digi-middleware-auth-app",appToken);
                        JSONObject digi_service=new JSONObject();
                        digi_service.put("prod",appId);//产品名称
                        digi_service.put("name","iam.identity.tenant.token.get");
                        headers.put("digi-service",digi_service.toJSONString());
                        req=new JSONObject();
                        JSONObject std_data=new JSONObject();
                        JSONObject parameter=new JSONObject();
                        parameter.put("secretKey",encryptSecretKey);//加密授权码
                        parameter.put("clientEncryptPublicKey",encryptPublicKey);//根据服务端公钥加密客户端公钥
                        std_data.put("parameter",parameter);
                        req.put("std_data",std_data);
                        requestBody=req.toJSONString();
                        
                        
                        StringBuffer sb3 = new StringBuffer();
                        headers.forEach((key3, value3) -> sb3.append(key3).append(": ").append(value3).append(", "));
                        HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST请求3 url:"+url+path+" ", "digiwin_ai");
                        HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST请求3 headers: "+sb3, "digiwin_ai");
                        HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST请求3 requestBody:" +requestBody, "digiwin_ai");
                        
                        
                        res=Sendhttp("POST",requestBody,url+path,headers,requestId);
                        HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST返回3 :"+res+" ", "digiwin_ai");
                        
                        
                        //{"std_data":{"execution":{"code":"0"},"parameter":{"token":"479cc09f-acba-4b2f-93f1-14ea94f9e927"}}}
                        if (!Check.Null(res))
                        {
                            if ((res.startsWith("{") && res.endsWith("}")) || (res.startsWith("[") && res.endsWith("]")))
                            {
                                jsonObject=JSON.parseObject(res);
                                JSONObject res_std_data=jsonObject.getJSONObject("std_data");
                                if (res_std_data != null)
                                {
                                    JSONObject res_parameter=res_std_data.getJSONObject("parameter");
                                    if (res_parameter != null)
                                    {
                                        //租户token
                                        String tenantToken=res_parameter.getString("token");
                                        //System.out.println("tenantToken:"+tenantToken);
                                        
                                        //1.3.2.4 根据地端用户id获取映射云端用户的accessToken
                                        
                                        path="/api/iam/v2/eai";
                                        headers=new HashMap<>();
                                        headers.put("Content-Type","application/json");
                                        headers.put("digi-middleware-auth-app",appToken);
                                        digi_service=new JSONObject();
                                        digi_service.put("prod",appId);//产品名称
                                        digi_service.put("name","iam.user.accesstoken.get");
                                        headers.put("digi-service",digi_service.toJSONString());
                                        req=new JSONObject();
                                        std_data=new JSONObject();
                                        parameter=new JSONObject();
                                        parameter.put("token",tenantToken);
                                        
                                        //eData处理
                                        String key=appSecret;
                                        JSONObject requestData=new JSONObject();
                                        requestData.put("verifyUserId",userId);
                                        requestData.put("appId",appId);
                                        String dataStr=requestData.toJSONString();
                                        //加密算法
                                        byte[] bytes = dataStr.getBytes(StandardCharsets.UTF_8);
                                        byte[] finalKey;
                                        if (key == null) {
                                            throw new Exception("AES key不能为空");
                                        }
                                        if (key.length() == 16) {
                                            finalKey= key.getBytes(StandardCharsets.UTF_8);
                                        }else {
                                            finalKey = new byte[16];
                                            int i = 0;
                                            for (byte b : key.getBytes(StandardCharsets.UTF_8)) {
                                                finalKey[i++ % 16] ^= b;
                                            }
                                        }
                                        String eData=  new String(java.util.Base64.getEncoder().encode(encryptIv(bytes, finalKey,"AES/CBC/PKCS5Padding")),StandardCharsets.UTF_8);
                                        parameter.put("eData",eData);
                                        std_data.put("parameter",parameter);
                                        req.put("std_data",std_data);
                                        requestBody=req.toJSONString();
                                        
                                        
                                        
                                        StringBuffer sb4 = new StringBuffer();
                                        headers.forEach((key4, value4) -> sb4.append(key4).append(": ").append(value4).append(", "));
                                        HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST请求4 url:"+url+path+" ", "digiwin_ai");
                                        HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST请求4 headers: "+sb4, "digiwin_ai");
                                        HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST请求4 requestBody:" +requestBody, "digiwin_ai");
                                        
                                        
                                        res=Sendhttp("POST",requestBody,url+path,headers,requestId);
                                        HelpTools.writelog_fileName("鼎捷getAI_AccessToken POST返回4 :"+res+" ", "digiwin_ai");
                                        
                                        
                                        //System.out.println(res);
                                        //{"std_data":{"execution":{"code":"0"},"parameter":{"accessToken":"67219109-7ae9-445c-90ed-777c739f0794"}}}
                                        if (!Check.Null(res))
                                        {
                                            if ((res.startsWith("{") && res.endsWith("}")) || (res.startsWith("[") && res.endsWith("]")))
                                            {
                                                jsonObject=JSON.parseObject(res);
                                                res_std_data=jsonObject.getJSONObject("std_data");
                                                if (res_std_data != null)
                                                {
                                                    
                                                    //ID:20251110048 娜娜登入提示语优化  by jinzma 20251111
                                                    //execution={"code":"500","description":"租户id: IR0400 下的应用: NRC_AIInfo 没有此用户: 00810 的映射关系"}
                                                    JSONObject execution = res_std_data.getJSONObject("execution");
                                                    String code = execution.getString("code");
                                                    if (!Check.Null(code) && !code.equals("0")){
                                                        String description = execution.toJSONString();
                                                        throw new Exception(description);
                                                    }
                                                    
                                                    
                                                    res_parameter=res_std_data.getJSONObject("parameter");
                                                    if (res_parameter != null)
                                                    {
                                                        accessToken=res_parameter.getString("accessToken");
                                                    }
                                                    else
                                                    {
                                                        throw new Exception("获取accessToken信息1-接口调用失败,返回值="+res);
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                throw new Exception("获取accessToken信息2-接口调用失败,返回值="+res);
                                            }
                                        }
                                        else
                                        {
                                            throw new Exception("获取accessToken信息-接口调用失败");
                                        }
                                    }
                                    else
                                    {
                                        throw new Exception("获取租户token信息1-接口调用失败,返回值="+res);
                                    }
                                }
                                else
                                {
                                    throw new Exception("获取租户token信息2-接口调用失败,返回值="+res);
                                }
                            }
                            else
                            {
                                throw new Exception("获取租户token信息3-接口调用失败,返回值="+res);
                            }
                        }
                        else
                        {
                            throw new Exception("获取租户token信息-接口调用失败");
                        }
                    }
                    else
                    {
                        throw new Exception("获取AES加密秘钥-接口调用失败,返回值="+res);
                    }
                }
                else
                {
                    throw new Exception("获取AES加密秘钥-接口调用失败");
                }
            }
            else
            {
                throw new Exception("获取RSA公钥-接口失败，返回值=" +res);
            }
        }
        else
        {
            throw new Exception("获取RSA公钥-接口调用失败");
        }
        
        return accessToken;
    }
    
    /**
     * http请求
     * @param method POST/GET
     * @param requestBody
     * @param url
     * @param headers
     * @param requestId
     * @return
     * @throws IOException
     */
    public static String Sendhttp(String method, String requestBody, String url, Map<String, Object> headers,String requestId) throws IOException
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

        /**
         * @跳过域名检查
         */
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }

            }).build();

        } catch (Exception e) {
            //logger.error("\r\nexec func Sendcom error", e);
        }

        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        httpBuilder.setSSLSocketFactory(connectionFactory);


        // 创建httpclient对象
        CloseableHttpClient client = httpBuilder.build();//HttpClients.createDefault();
        
        // 创建post方式请求对象
        HttpUriRequest httpPost = null;
        //URI ul=new URI(surl);
        
        if (method.equals("POST"))
        {
            httpPost = new HttpPost(url);
        }
        else
        {
            httpPost = new HttpGet(url);
        }
        
        if (method.equals("POST"))
        {
            ((HttpPost) httpPost).setEntity(new StringEntity(requestBody, "utf-8"));
        }
        
        logger.info("\r\nrequestId="+requestId+" url请求内容："+url+"\r\n");
        
        StringBuffer headerStr=new StringBuffer("");
        if (headers != null && !headers.isEmpty())
        {
            Set<String> headerKeys = headers.keySet();
            for (String headerKey : headerKeys)
            {
                httpPost.setHeader(headerKey, headers.get(headerKey).toString());
                headerStr.append(headerKey+"="+headers.get(headerKey).toString()+",");
            }
            
            logger.info("\r\nrequestId="+requestId+" header请求内容："+headerStr+"\r\n");
        }
        logger.info("\r\nrequestId="+requestId+" body请求内容："+requestBody+"\r\n");
        
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
    
    
    private static HashMap<String,String> getKeyPairMap() throws NoSuchAlgorithmException
    {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
        String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
        HashMap<String, String> keyMap = new HashMap<>();
        keyMap.put("privateKey", privateKey);
        keyMap.put("publicKey", publicKey);
        return keyMap;
    }
    
    public static String encryptByPublicKey(String data, String clientPublicKey) throws Exception
    {
        data = Base64.encodeBase64String(encryptByPublicKey(data.getBytes(), clientPublicKey));
        return data;
    }
    
    
    /** */
    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception
    {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
    
    /** */
    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception
    {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
    
    /**
     * 加密
     * 加密失败返回原文 2021-7-21
     *
     * @param src    加密字段
     * @param aesKey aesKey 长度16
     * @return 密文 string
     */
    public static String aesEncryptByBase64(String src, String aesKey)
    {
        try {
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
            
            byte[] initParam = IV_STRING.getBytes(StandardCharsets.UTF_8);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            
            byte[] cleartext = src.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertextBytes = cipher.doFinal(cleartext);
            java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
            return encoder.encodeToString(ciphertextBytes);
        } catch (Exception ex) {
            logger.error("AES加密失败[{}]", src);
            return src;
        }
        
    }
    
    /**
     * 加密
     *
     * @param data           被加密的bytes
     * @param key            密钥
     * @param transformation 算法/工作模式/填充模式
     * @return IV+加密后的bytes
     */
    public static byte[] encryptIv(byte[] data, byte[] key, String transformation)
    {
        try {
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec params = new IvParameterSpec(iv);
            
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, params);
            
            byte[] encrypted = cipher.doFinal(data);
            byte[] combined = new byte[iv.length + encrypted.length];
            
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return combined;
        } catch (Exception e) {
            throw new RuntimeException("AES加密异常", e);
        }
    }
    
    
    
}