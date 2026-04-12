package com.dsc.spos.waimai.dianwoda;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class DianwodaClientSigner {

	 public static String sign(String appkey,String timestamp,String nonce,String access_token,String api,String secret,String biz_params){
     String unsignStr = getUnsignString(appkey,timestamp,nonce,access_token,api,secret,biz_params);
     String sign = DigestUtils.sha1Hex(unsignStr);
     return sign;
 }

 public static Pair<String,String> getUnsignStringAndFinalSign(String appkey, String timestamp, String nonce, String access_token, String api, String secret, String biz_params){
     String unsignStr = getUnsignString(appkey,timestamp,nonce,access_token,api,secret,biz_params);
     String sign = DigestUtils.sha1Hex(unsignStr);
     return new ImmutablePair<>(unsignStr,sign);
 }

 private static String getUnsignString(String appkey,String timestamp,String nonce,String access_token,String api,String secret,String biz_params){
     SortedMap<String,Object> unsignRequestParams = new TreeMap<>();
     unsignRequestParams.put("appkey",appkey);
     unsignRequestParams.put("timestamp",timestamp);
     unsignRequestParams.put("nonce",nonce);
     if(access_token != null){
         unsignRequestParams.put("access_token",access_token);
     }
     unsignRequestParams.put("api",api);
     StringBuilder unsignStr = new StringBuilder();
     for(Map.Entry<String,Object> entry : unsignRequestParams.entrySet()){
         unsignStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
     }
     unsignStr.append("body=").append(biz_params);
     unsignStr.append("&secret=").append(secret);
     return unsignStr.toString();
 }
}
