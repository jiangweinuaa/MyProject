package com.dsc.spos.waimai.isv;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ISV_WMJBPHttpSend {

    /**
     * POST发送application/x-www-form-urlencoded请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String doPost_form_urlencoded(String url, Map<String, Object> params) throws Exception {
        if (StringUtils.isEmpty(url) || params == null || params.isEmpty()) {
            return "";
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(1000)
                    .build();//设置请求和传输超时时间

            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
            for (Map.Entry<String, Object> entity : params.entrySet()) {
                basicNameValuePairs.add(new BasicNameValuePair(entity.getKey(), entity.getValue()==null?"":entity.getValue().toString()));
            }

            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, Consts.UTF_8);
            httpPost.setEntity(urlEncodedFormEntity);

            response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine != null)
            {
                int statusCode = statusLine.getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    return "";
                }
            }


            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);

            return result;

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
            }
        }

    }

}
