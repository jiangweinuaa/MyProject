package com.dsc.spos.thirdpart.qimai;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.RandomStringUtils;

import com.dsc.spos.scheduler.job.OrderPostClient;
import com.dsc.spos.waimai.HelpTools;

/**
 * 工具类
 * 
 * @author LN 08546
 */
public class QiMaiUtils {
	
	public static QiMaiUtils getInstance() throws Exception {
		return new QiMaiUtils();
	}
	
	public QiMaiUtils(){
		
	}
	
	/**
	 * 标准接口post
	 */
	public String PostData(String serviceId,Map<String, Object> orderMap,Map<String, Object> params) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String requestId=sdf.format(new Date())+RandomStringUtils.randomAlphanumeric(3);
		String ramdomId="\r\n["+serviceId+"-"+requestId+"]";
		String resStr="";
		String url="";
		String reqStr = "";
		String logFileName = "";
		if (serviceId.contains("updateStock"))
		{
			logFileName = "qimai-stockSync";//库存同步单独记录一个日志
		}
		Map<String, Object> postMap=new HashMap<String, Object>();
		try{
			Map<String, Object> basicMap =QiMaiService.getInstance().getBasicMap(orderMap);
			url=basicMap.get("APIURL")==null?"":basicMap.get("APIURL").toString();
			if(url!=null&&!url.endsWith("/")){
				url=url+"/";
			}
			url+=serviceId;
			postMap=encrypt2String(serviceId, basicMap, params);
			Map<String, Object> headers=new HashMap<String, Object>();
			headers.put("Content-Type", "application/json");
			reqStr = com.alibaba.fastjson.JSON.toJSONString(postMap);
			resStr=OrderPostClient.sendSoapPost(serviceId, url, headers, reqStr);
			Log(ramdomId
					+ "\r\nurl:\r\n"+url
					+ "\r\nrequest:\r\n"+reqStr
					+ "\r\nreponse:\r\n"+resStr);
			Log("【库存同步】请求url:"+url+",请求req:"+reqStr+",返回res:"+resStr,logFileName);
		}catch(Exception e){
			Log(ramdomId
					+ "\r\nurl:"+url
					+ "\r\nrequest:"+reqStr
					+ "\r\nreponse:无响应");
			Log("【库存同步】请求url:"+url+",请求req:"+reqStr+",返回异常:"+e.getMessage(),logFileName);
			
		}
		return resStr;
	}

	/**
	 * 按照键值的字典顺序重新排序
	 */
	public String computeSignature(Map<String, Object> parameters) throws Exception {
		// 将参数Key按字典顺序排序
		String[] sortedKeys = parameters.keySet().toArray(new String[] {});
		Arrays.sort(sortedKeys);
		// 生成规范化请求字符串
		StringBuilder canonicalizedQueryString = new StringBuilder();
		for (String key : sortedKeys) {
			canonicalizedQueryString.append("&").append(key)
					.append("=").append(parameters.get(key));
		}
		String stringSignTemp=canonicalizedQueryString.toString();
		stringSignTemp=stringSignTemp.substring(1,stringSignTemp.length());
		return stringSignTemp;
	}
	
	public Map<String, Object> encrypt2String(String serviceId,Map<String, Object> basicMap,Map<String, Object> params) throws Exception {
		Map<String, Object> signMap=new HashMap<String, Object>();
		String openId=basicMap.get("APIKEY")==null?"":basicMap.get("APIKEY").toString();
		String openKey=basicMap.get("APISECRET")==null?"":basicMap.get("APISECRET").toString();
		String grantCode=basicMap.get("APISIGN")==null?"":basicMap.get("APISIGN").toString();
		signMap.put("openId", openId);
		signMap.put("grantCode", grantCode);
		signMap.put("timestamp", System.currentTimeMillis()/1000);
		signMap.put("nonce", randomCode(5));
		String computeSignature=computeSignature(signMap);
		String token=sign(computeSignature, openKey, "HmacSHA1");
		signMap.put("token", token);
		signMap.put("params", params);
		return signMap;
	}
	
	/**
	 * 获取随机正整数
	 * @param count
	 * @return
	 */
	public String randomCode(int count) {
		StringBuilder str = new StringBuilder("");
		Random random = new Random();
		for (int i = 0; i < count; i++) {
			str.append(1+random.nextInt(9));
		}
		return str.toString();
	}
   
   public String sign(String data, String key, String method)
			throws Exception {
		//HMAC-SHA1 算法签名
		Mac mac = Mac.getInstance(method);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"),
				mac.getAlgorithm());
		mac.init(secretKeySpec);
		byte[] hash = mac.doFinal(data.getBytes("UTF-8"));
		
		//将生成的签名串使用 Base64 进行编码
		String base=DatatypeConverter.printBase64Binary(hash);
		//URL编码
		String urlEncode=URLEncoder.encode(base, "UTF-8");
		return urlEncode;
	}
   
   public String getTrace(Throwable t) {
       StringWriter stringWriter= new StringWriter();
       PrintWriter writer= new PrintWriter(stringWriter);
       t.printStackTrace(writer);
       StringBuffer buffer= stringWriter.getBuffer();
       return buffer.toString();
   }
   
   public void Log(String log) throws Exception{
	   HelpTools.writelog_fileName(log,"QiMaiPost");
	}
	public void Log(String log,String fileName) throws Exception{
		if (fileName==null||fileName.isEmpty())
		{
			return;
		}
		HelpTools.writelog_fileName(log,fileName);
	}
	
	public void ErrorLog(String log,Throwable t) throws Exception{
		HelpTools.writelog_fileName(log+(t==null?"":getTrace(t)),"QiMaiPostError");
	}

}
