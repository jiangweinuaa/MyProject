package com.dsc.spos.waimai.kdniao.query;

import com.dsc.spos.json.utils.ParseJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import com.dsc.spos.waimai.HelpTools;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class kdniaoQueryService {

    private static  String test_ApiUrl = "http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json";//查询测试地址

    private static  String product_ApiUrl = "https://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";//查询正式地址

    //private static  String RequestType = "8002";//请求指令类型：8002

    private static  String DataType = "2";//请求、返回数据类型：2表示json格式；

    private static  String logFileName = "kdniaoLog";

    private String req_ApiUrl = "https://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";

    public kdniaoQueryService ()
    {
        req_ApiUrl = product_ApiUrl;
    }

    public kdniaoQueryService (boolean isTest)
    {
        if (isTest)
        {
            req_ApiUrl = test_ApiUrl;
        }
        else
        {
            req_ApiUrl = product_ApiUrl;
        }

    }

    public String queryTrace(String EBusinessID,String ApiKey,String RequestType,String ShipperCode,String LogisticCode,StringBuffer errorMessage) throws Exception
    {
        String res = "";
        if (RequestType==null||RequestType.isEmpty())
        {
            RequestType = "8002";//默认，8001-在途监控；8002-快递查询
        }
        String methodName = "【查询指令】【"+RequestType+"】";
        try
        {
            ParseJson pj = new ParseJson();
            RequestData requestObj = new RequestData();
            requestObj.setLogisticCode(LogisticCode);
            requestObj.setShipperCode(ShipperCode);
            String requestDataJson = pj.beanToJson(requestObj);

            // 组装系统级参数
            Map<String,String> params = new HashMap<String,String>();
            params.put("RequestData", urlEncoder(requestDataJson, "UTF-8"));
            params.put("EBusinessID", EBusinessID);
            params.put("RequestType", RequestType);//快递查询接口指令8002/地图版快递查询接口指令8004
            params.put("DataType", DataType);

            String dataSign=encrypt(requestDataJson, ApiKey, "UTF-8");

            params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
            HelpTools.writelog_fileName(methodName+"【调用快递鸟查询API】发送url:"+req_ApiUrl+",请求RequestData转码前:"+requestDataJson+",请求req:"+params.toString(),logFileName);
            res = sendPost(req_ApiUrl, params,errorMessage);
            HelpTools.writelog_fileName(methodName+"【调用快递鸟查询API】返回res:"+res,logFileName);
        }
        catch (Exception e)
        {
            errorMessage.append(e.getMessage());
            HelpTools.writelog_fileName(methodName+"【调用快递鸟查询API】返回异常:"+e.getMessage(),logFileName);
            return "";
        }
        return  res;
    }

    /**
     * MD5加密
     * str 内容
     * charset 编码方式
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private String MD5(String str,String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * base64编码
     * str 内容
     * charset 编码方式
     * @throws UnsupportedEncodingException
     */
    private String base64(String str, String charset) throws UnsupportedEncodingException{
        String encoded = Base64.encode(str.getBytes(charset));
        return encoded;
    }

    @SuppressWarnings("unused")
    private String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
        String result = URLEncoder.encode(str, charset);
        return result;
    }

    /**
     * 电商Sign签名生成
     * content 内容
     * keyValue ApiKey
     * charset 编码方式
     * @throws UnsupportedEncodingException ,Exception
     * @return DataSign签名
     */
    @SuppressWarnings("unused")
    private  String encrypt (String content,String keyValue,String charset) throws UnsupportedEncodingException, Exception
    {
        if (keyValue != null)
        {
            return base64(MD5(content + keyValue, charset), charset);
        }
        return base64(MD5(content, charset), charset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * url 发送请求的 URL
     * params 请求的参数集合
     * @return 远程资源的响应结果
     */
    @SuppressWarnings("unused")
    private  String sendPost(String url, Map<String,String> params,StringBuffer error) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new    StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (params != null) {
                StringBuilder param = new    StringBuilder();
                for (Map.Entry<   String,    String> entry : params.entrySet()) {
                    if(param.length()>0){
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());
                    //System.out.println(entry.getKey()+":"+entry.getValue());
                }
                //System.out.println("param:"+param.toString());
                out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            error.append(e.getMessage());
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                error.append(ex.getMessage());
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
}
