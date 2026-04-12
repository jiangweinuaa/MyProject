package com.dsc.spos.waimai.shansong;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: wangzyc
 * @create: 2022-01-13
 */
public class SHANSONGUtil {

    private static final String logFileName = "shansongLog";

    //把字节数组转换成大写md5
    public static String bytesToMD5(byte[] input) {
        String md5str = null;
        try {
            //创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            //计算后获得字节数组
            byte[] buff = md.digest(input);
            //把数组每一字节换成16进制连成md5字符串
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str.toUpperCase();
    }

    //把字节数组转成16进位制数
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString();
    }

    public static String sendPost(String url, Map<String,Object> params) {
        String response = null;
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (String key : params.keySet()) {
                    pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
            }
            CloseableHttpClient httpClient = null;
            CloseableHttpResponse httpResponse = null;
            try {
                httpClient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(url);
                if (pairs != null && pairs.size() > 0) {
                    httppost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
                }
                httpResponse = httpClient.execute(httppost);
                response = EntityUtils
                        .toString(httpResponse.getEntity());
                System.out.println(response);
            } finally {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (httpResponse != null) {
                    httpResponse.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
