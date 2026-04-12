package com.dsc.spos.waimai.isv;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class ISV_WMELMHttpSend {

    /**
     * POST发送application/json请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String doPost_json(String url, String params) throws Exception {
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
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

            httpPost.setEntity(new StringEntity(params, "utf-8"));

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
