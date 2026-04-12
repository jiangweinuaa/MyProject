package com.dsc.spos.ninetyone.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.dsc.spos.ninetyone.NinetyOneConstants;



/**
 * 工具类
 * 
 * @author LN 08546
 */
public class NinetyOneUtils {
	
	/**
	 * 日志文件名称
	 */
	public String logFileName = NinetyOneConstants.clientLogFileName;
	
	public String formatRequestStr(Map<String, Object> map)throws Exception {
		map=getOrderByLexicographic(map);
		com.alibaba.fastjson.JSONObject paramsJs = new com.alibaba.fastjson.JSONObject(map);
		String request=paramsJs.toJSONString();
		return request;
	}
	public String getLastUrl(Map<String, Object> params,String timeStamp,String token,String apiKey,String saltKey) throws Exception {
		com.alibaba.fastjson.JSONObject paramsJs = new com.alibaba.fastjson.JSONObject(params);
		String content = "ts=" + timeStamp + "&data="+paramsJs.toJSONString().toLowerCase()+"&sk=" + saltKey;
		String signature=SHA512(content, apiKey);
		String url="?ts=" + timeStamp + "&t=" + token + "&s=" + signature;
		return url;
	}
	
	
	/**
	 * SHA512
	 * @param message
	 * @param secret
	 * @return
	 */
	public String SHA512(String message, String secret)throws Exception {
		String hash = "";
		try 
		{
			Mac sha256_HMAC = Mac.getInstance("HmacSHA512");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
			sha256_HMAC.init(secret_key);

			byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
			hash = byteArrayToHexString(bytes);
		} 
		catch (Exception e) 
		{
			writelogFileName(logFileName, "\r\n******字符串" +message+"SHA 加密异常" +ExceptionUtils.getRootCauseMessage(e)+"******\r\n");
			throw e;
		}
		return hash;
	}
	
	private String byteArrayToHexString(byte[] b) 
	{
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b!=null && n < b.length; n++) 
		{
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toLowerCase();
	}
    
	/**
	 * 按照键值的字典顺序重新排序
	 * @param params
	 * @return
	 */
	public static Map<String, Object> getOrderByLexicographic(Map<String, Object> params) {
		Map<String, Object> map=new HashMap<String, Object>();
		if(isNotEmpty(params)){
			TreeMap<String, Object> paramTreeMap = new TreeMap<String, Object>(params);
			map.putAll(paramTreeMap);
		}
		return map;
	}

	
	public static String getDefaultValueStr(Object obj){
		if(obj==null||obj.toString().isEmpty()){
			return "";
		}else{
			return obj.toString();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof String && obj.toString().trim().length() == 0) {
			return true;
		}
		if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
			return true;
		}
		if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
			return true;
		}
		if (obj instanceof Map && ((Map) obj).isEmpty()) {
			return true;
		}
		return false;
	}
	
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}
	
	// 写日志
	public static void writelogFileName(String logFileName,String log) {
		try{
			// 生成文件路径
			String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
			String path = System.getProperty("user.dir") + "\\log\\" + logFileName + sdFormat + ".txt";
			File file = new File(path);
			
			String dirpath = System.getProperty("user.dir") + "\\log";
			File dirfile = new File(dirpath);
			if (!dirfile.exists()) {
				dirfile.mkdir();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			
			BufferedWriter output = new BufferedWriter(new FileWriter(file, true));
			// 前面加上时间
			String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
			String slog = stFormat + " " + log + "\r\n";
			output.write(slog);
			output.close();
		}catch(Exception e) {
			
		}
	}

}
