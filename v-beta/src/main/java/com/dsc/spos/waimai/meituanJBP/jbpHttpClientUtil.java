package com.dsc.spos.waimai.meituanJBP;

import com.dsc.spos.waimai.HelpTools;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class jbpHttpClientUtil {



    private static final String UTF_8 = "UTF-8";

    private static final int TIME_OUT = 2000;
    private static final String logFileName = "jbpRequestLog";

    /**
     * post 参数json字符串，返回String
     *
     * @param uri
     * @param params
     * @return
     */
    public static String postFormRequest(String uri, Map<String,String> params) {
        String output;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            if (params!=null)
            {
                for (String key : params.keySet())
                {
                    formparams.add(new BasicNameValuePair(key,params.get(key)));
                }
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
            HelpTools.writelog_fileName("【调用美团JBP接口】发送url:"+uri+",请求req:"+params.toString(),logFileName);
            HttpPost postRequest = new HttpPost(uri);

            //设置超时时间。
            RequestConfig requestConfig = setRequestConfig();
            postRequest.setConfig(requestConfig);

            postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");

            postRequest.setEntity(entity);

            HttpResponse response = httpClient.execute(postRequest);
          /*if (response.getStatusLine().getStatusCode() != 200) {
              throw new RuntimeException("POST调用异常 : " + response.getStatusLine().getStatusCode());
          }*/
            output = EntityUtils.toString(response.getEntity(), UTF_8);
        } catch (IOException e) {
            try{
                HelpTools.writelog_fileName("【调用美团JBP接口】返回异常:"+e,logFileName);
            }catch (Exception ex) {
            }
            throw new RuntimeException("POST调用异常", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {

            }
        }
        try{
            HelpTools.writelog_fileName("【调用美团JBP接口】返回res:"+output,logFileName);
        }catch (Exception e) {
        }
        return output;
    }

    private static RequestConfig setRequestConfig(){
        return RequestConfig.custom().setSocketTimeout(TIME_OUT).setConnectTimeout(TIME_OUT).build();
    }

}
