package com.dsc.spos.thirdpart.youzan;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;

import com.dsc.spos.scheduler.job.OrderPostClient;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.thirdpart.youzan.util.YouZanUtils;
import com.dsc.spos.waimai.entity.orderLoadDocType;


/**
 * 工具类
 * 
 * @author LN 08546
 */
public class YouZanUtilsV3 {
	
	private static final String KEY_ALGORITHM = "RSA";
    private static final String CHARSET_NAME = "UTF-8";
    private static final int MAX_BATCH_ENCRYPT_LENGTH = 245;
    
    public static YouZanUtilsV3 getInstance() throws Exception {
		return new YouZanUtilsV3();
	}
    
    /**
     * 目前对接的是零售连锁一个商家只会有一个appid,如后期有调整，则启用DCP_ECOMMERCE_DETAIL表
     * 启用DCP_ECOMMERCE_DETAIL的sql示例:
     * SELECT A.* ,DE.APIKEY AS DEAPIKEY,DE.APISECRET AS DEAPISECRET,DE.APISIGN AS DEAPISIGN  ,DE.TOKEN AS DETOKEN,DE.PUBLICKEY AS DEPUBLICKEY  
FROM DCP_ECOMMERCE A  
LEFT JOIN DCP_ECOMMERCE_DETAIL DE ON A.EID=DE.EID AND A.LOADDOCTYPE=DE.LOADDOCTYPE AND A.CHANNELID=DE.CHANNELID  
WHERE  A.EID='99' AND A.LOADDOCTYPE='YOUZAN' and A.STATUS='100'
     */
    public List<Map<String, Object>> getYouZanList(Map<String, Object> map) throws Exception {
		StringBuffer sql=new StringBuffer("SELECT A.*"
				+ " FROM DCP_ECOMMERCE A "
				+ " WHERE A.LOADDOCTYPE='"+orderLoadDocType.YOUZAN+"' and A.STATUS='100' "
				+ " ");
		if(map==null){
			map=new HashMap<String, Object>();
		}
		String eId=map.get("EID")==null?"":map.get("EID").toString().trim();
		if(eId!=null&&eId.length()>0){
			sql.append(" AND A.EID='" + eId + "' ");
		}
		List<Map<String, Object>> basicList = null;
		try{
			basicList = StaticInfo.dao.executeQuerySQL(sql.toString(), null);
		}catch(Exception e){
			ErrorLog("[SQL执行异常]:\r\n"+sql.toString()+"\r\n错误详情:\r\n"+YouZanUtils.getTrace(e));
		}
		if(basicList==null){
			throw new Exception("DCP_ECOMMERCE 有赞资料未配置!");
		}else if(basicList.size()>1){
			throw new Exception("DCP_ECOMMERCE 有赞资料配置资料异常,生效数量大于一笔!");
		}
		return basicList;
	}
    
    public static String encrypt( String str, String publicKey ) throws Exception{
		//base64编码的公钥
		byte[] decoded = Base64.decodeBase64(publicKey);
		for (int i = 0; i < decoded.length; ++i) {
			if (decoded[i] < 0) {// 调整异常数据
				decoded[i] += 256;
			}
		}
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		//RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
		return outStr;
	}
    
    private byte[] handleByteByBatch(Cipher cipher, int batchLength, byte[] bodyByte) throws Exception {
        if (bodyByte.length <= batchLength) {
            return cipher.doFinal(bodyByte);
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int start = 0; start < bodyByte.length; start += batchLength) {
                byte[] cache = cipher.doFinal(bodyByte, start, Math.min(batchLength, bodyByte.length - start));
                out.write(cache, 0, cache.length);
            }

            byte[] data = out.toByteArray();
            try {
                out.close();
            } catch (Exception ignored) {
                ;
            }
            return data;
        }
    }
	
	public String formatRequestStr(Map<String, Object> map)throws Exception {
		map=getOrderByLexicographic(map);
		String request="";
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			request+=entry.getKey()+entry.getValue();
		}
		return request;
	}
    
	/**
	 * 按照键值的字典顺序重新排序
	 * @param params
	 * @return
	 */
	public Map<String, Object> getOrderByLexicographic(Map<String, Object> params) {
		Map<String, Object> map=new HashMap<String, Object>();
		if(params!=null&&!params.isEmpty()){
			TreeMap<String, Object> paramTreeMap = new TreeMap<String, Object>(params);
			map.putAll(paramTreeMap);
		}
		return map;
	}

	
	public String getDefaultValueStr(Object obj){
		if(obj==null||obj.toString().isEmpty()){
			return "";
		}else{
			return obj.toString();
		}
	}
    
	/**
	 * 标准接口post
	 */
	public String PostData(String serviceId,Map<String, Object> bMap,Map<String, Object> params) throws Exception{
		//注意参数名称需要与getYouZanList对应
		Map<String, Object> basicMap =new HashMap<String, Object>();
		String appId=bMap.get("APIKEY")==null?"":bMap.get("APIKEY").toString();
		basicMap.put("appId", appId);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String requestId=sdf.format(new Date())+RandomStringUtils.randomAlphanumeric(3);
		String ramdomId="\r\n["+serviceId+"-"+requestId+"]";
		YouzanUrlConfig g1=GetUrlConfig(serviceId);
		
		if(g1==null||g1.getUrl()==null||g1.getUrl().trim().length()<=0){
			ErrorLog(ramdomId
					+ "\r\n警告:DCP_ECOMMERCE_YOUZAN["+serviceId+"]:url未配置");
			ErrorLog(ramdomId
					+ "\r\nrequest加密前:\r\n"+com.alibaba.fastjson.JSON.toJSONString(params));
			throw new Exception("有赞接口["+serviceId+"]:url地址未配置(对应配置表DCP_ECOMMERCE_YOUZAN)");
		}else{
			bMap.put("APIURL", g1.getUrl());
		}
		String url=bMap.get("APIURL")==null?"":bMap.get("APIURL").toString();
		
		String publicKey=bMap.get("PUBLICKEY")==null?"":bMap.get("PUBLICKEY").toString();
		basicMap.put("publicKey", publicKey);
		
		basicMap.put("requestId", requestId);
		
//		if(!"".equals(serviceId)){
//			params.put("appId", appId);
//		}
		Log(ramdomId
				+ "\r\nurl:\r\n"+url
				+ "\r\nrequest加密前:\r\n"+com.alibaba.fastjson.JSON.toJSONString(params));
		Map<String, Object> postMap=encrypt2String(serviceId, basicMap, params);
		String resStr="";
		try{
			Map<String, Object> headers=new HashMap<String, Object>();
			headers.put("Content-Type", "application/json");
			resStr=OrderPostClient.sendSoapPost(serviceId, url, headers, com.alibaba.fastjson.JSON.toJSONString(postMap));
			Log(ramdomId
					+ "\r\nurl:\r\n"+url
					+ "\r\nrequest加密后:\r\n"+com.alibaba.fastjson.JSON.toJSONString(postMap)
					+ "\r\nreponse:\r\n"+resStr);
		}catch(Exception e){
			Log(ramdomId
					+ "\r\nurl:"+url
					+ "\r\nrequest加密后:"+com.alibaba.fastjson.JSON.toJSONString(postMap)
					+ "\r\nreponse:无响应"
					+ "\r\n"+getTrace(e)
					);
			
		}
		
		return resStr;
	}
	
	/**
     * RSA公钥加密，并 JSON 序列化
     *
     * @param preNodeRequest
     * @param publicKey      公钥
     * @throws Exception 加密过程中的异常信息
     */
	public Map<String, Object> encrypt2String(String serviceId,Map<String, Object> basicMap,Map<String, Object> params) throws Exception {
    	String appId=basicMap.get("appId")==null?"":basicMap.get("appId").toString();
    	String publicKey=basicMap.get("publicKey")==null?"":basicMap.get("publicKey").toString();
    	String requestId=basicMap.get("requestId")==null?"":basicMap.get("requestId").toString();
    	Map<String, Object> postMap=new HashMap<String, Object>();
    	postMap.put("requestId", requestId);
		postMap.put("service", serviceId);
		postMap.put("version", "V1");
		postMap.put("targetSystem", "YOUZAN");
		postMap.put("appId", appId);
//		publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtkPo9AGTULkaHbdWogCXcPZTrF8K83g6\n9A8/CtvXMAfPHnQrolCLc334Tj0l1q0E3tAVLuBsX+amjHycWb33Fg9Qyllb1VXsJx40VbqH1M0f\n08yy+QZe2KeYY5IrQ2hq/1zY7D4QhAiN/Uoj7x311xVVUcqRk4pGWV1daWkGNWIKs53z9wpQG0Yj\nGC3ul5/TxT34soXDoYf40ET6BcI8jGe8IPtsH/BrdIvfj6RrNwIRV36R4CEJbCfv7Kb4Fem3RXOs\nssgXcbyDgvI/mVWrt8INxacC+3MLYfaFF68od7NKoycom8ATMlVtDY0nqAeG61DVM+OmpjonHp/c\nXoCNywIDAQAB";
		publicKey=publicKey.replace("\\n", "");
		try{
			//base64编码的公钥
			byte[] decoded = Base64.decodeBase64(publicKey);
			RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
			//RSA加密
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			
			byte[] bodyByte = com.alibaba.fastjson.JSON.toJSONString(params).getBytes(CHARSET_NAME);
			
			String encryptString = new String(Base64.encodeBase64(handleByteByBatch(cipher, MAX_BATCH_ENCRYPT_LENGTH, bodyByte)), CHARSET_NAME);
	        postMap.put("requestBody", encryptString);
		}catch(Exception e){
			ErrorLog("[appId"+appId+"]公钥Base64加密异常"+getTrace(e));
		}
		
       

        return postMap;
    }
	
	public void Log(String log) throws Exception{
		writelogFileName("YouZanPost", log);
	}
	
	public void ErrorLog(String log) throws Exception{
		writelogFileName("YouZanPostError", log);
	}
	
	// 写日志
	public void writelogFileName(String logFileName,String log) {
		try{
			// 生成文件路径
			String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
			String path = System.getProperty("user.dir") + "\\log\\" + logFileName + sdFormat + ".log";
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
	
	public String getTrace(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }
	
	public static class YouzanUrlConfig{
		private String serviceId; //
		private String url; //
		public String getServiceId() {
			return serviceId;
		}
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	private static Map<String,YouzanUrlConfig> accounts;
	
	public static YouzanUrlConfig GetUrlConfig(String serviceId) throws Exception
	{
		//accounts = new HashMap<String,YouzanUrlConfig>();
		if(accounts == null)
		{
			accounts = new HashMap<String,YouzanUrlConfig>();
		}
		
		if(!accounts.containsKey(serviceId))
		{
			String sql = "SELECT A.* "
					+ " FROM DCP_ECOMMERCE_YOUZAN A ";
			List<Map<String,Object>> maps =  StaticInfo.dao.executeQuerySQL(sql, null);			
			for(int i=0;i<maps.size();i++)
			{
				Map<String,Object> map = maps.get(i);
				YouzanUrlConfig a = new YouzanUrlConfig();
				String url=map.get("URL")==null?"":map.get("URL").toString();
				String serviceId1=map.get("SERVICEID")==null?"":map.get("SERVICEID").toString();
				a.setServiceId(serviceId1);
				a.setUrl(url);
				accounts.put(serviceId1, a);
			}
		}
		
		
		return accounts.get(serviceId);
		

	}
	

}
