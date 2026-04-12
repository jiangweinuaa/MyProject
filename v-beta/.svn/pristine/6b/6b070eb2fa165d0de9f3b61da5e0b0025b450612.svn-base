package com.dsc.spos.waimai.candao;

import com.dsc.spos.waimai.HelpTools;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class candaoHttpClientUtil {

    private static  String UTF_8 = "UTF-8";
    private static  int TIME_OUT = 2000;
    private static  String logFileName = "candaoLog";

    /**
     * post 参数json字符串，返回String
     *
     * @param uri
     * @param jsonParams
     * @return
     */
    public static String postRequest(String uri, String jsonParams) {
        String output;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
           // HelpTools.writelog_fileName("【调用物流candao】发送url:"+uri+",请求req:"+jsonParams,logFileName);
            HttpPost postRequest = new HttpPost(uri);

            //设置超时时间。
            RequestConfig requestConfig = setRequestConfig();
            postRequest.setConfig(requestConfig);

            postRequest.setHeader("Content-Type", "application/json;charset=utf-8");
            StringEntity input = new StringEntity(jsonParams, UTF_8);
            postRequest.setEntity(input);

            HttpResponse response = httpClient.execute(postRequest);
          /*if (response.getStatusLine().getStatusCode() != 200) {
              throw new RuntimeException("POST调用异常 : " + response.getStatusLine().getStatusCode());
          }*/
            output = EntityUtils.toString(response.getEntity(), UTF_8);
        } catch (IOException e) {
            try{
                HelpTools.writelog_fileName("【调用物流candao】返回异常:"+e,logFileName);
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
            //HelpTools.writelog_fileName("【调用物流candao】返回res:"+output,logFileName);
        }catch (Exception e) {
        }
        return output;
    }

    private static RequestConfig setRequestConfig(){
        return RequestConfig.custom().setSocketTimeout(TIME_OUT).setConnectTimeout(TIME_OUT).build();
    }

}
