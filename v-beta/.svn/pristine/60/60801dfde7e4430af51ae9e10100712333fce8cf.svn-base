package com.dsc.spos.hll.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.hll.api.response.HllShopBillDetailResponse;
import com.dsc.spos.hll.api.util.HllUtils;
import com.dsc.spos.hll.api.util.HllWebUtils;

/**
 * 客户端
 * 
 * @author LN 08546
 */
public class HllClient {
	
	public static String logFileName = "HllShopBillJob";
	
	Logger logger = LogManager.getLogger(HllClient.class.getName());

	protected Long devID;
	protected Long merchantsID;
	protected Long groupID;
	protected double version;
	protected int connectTimeout = HllConstants.CONNECT_TIME_OUT; // 默认连接超时时间
	protected int readTimeout = HllConstants.READ_TIME_OUT; // 默认响应超时时间
	protected String contentType = HllConstants.CONTENT_TYPE;

	
	public HllClient() {
		this.devID = HllConstants.DEV_ID;
		this.merchantsID = HllConstants.MER_ID;
		this.groupID = HllConstants.GROUP_ID;
		this.version = HllConstants.VERSION;
	}

	public HllShopBillDetailResponse getShopBillDetail(String pageNo,String pageSize,String reportDate,String shopID) throws Exception{
		
		Long timestamp = System.currentTimeMillis();
		Map<String, Object> basicMap=new HashMap<String, Object>();
		basicMap.put("timestamp", timestamp);// 时间戳
		basicMap.put("devID", devID);// 开发者ID
		basicMap.put("merchantsID", merchantsID);// 商户ID(open Api提供)
		basicMap.put("groupID", groupID);// 集团ID(商户提供)
		basicMap.put("version", version);// 版本
		
		basicMap.put("shopID", shopID);// 店铺ID(商户提供)，获取店铺维度数据必传，获取集团维度数据可不传
		String url = "https://www-openapi.hualala.com/report/getShopBillDetail";
		Map<String, Object> requestBodyMap=new HashMap<String, Object>();
		requestBodyMap.put("reportDate", reportDate);
		requestBodyMap.put("pageNo", pageNo);
		requestBodyMap.put("pageSize", pageSize);
		JSONObject requestBodyJson = new JSONObject(requestBodyMap);
		
		
		Map<String, Object> signMap = new HashMap<String, Object>();
		signMap.put("devPwd", HllConstants.DEV_PWD);// 开发者秘钥
		signMap.putAll(basicMap);
		signMap.putAll(requestBodyMap);
		
		String sign = HllUtils.getSignature(signMap);
        String requestBody= HllUtils.AESEncode(HllConstants.MER_PWD,requestBodyJson.toJSONString());
		Map<String, Object> postMap=new HashMap<String, Object>();
		
		postMap.putAll(basicMap);
		postMap.put("signature", sign);
		postMap.put("requestBody", requestBody);
		HllShopBillDetailResponse billDetailRes=null;
		try{
			String res=HllWebUtils.doPost(url, HllConstants.GROUP_ID.toString(), postMap, HllConstants.CONNECT_TIME_OUT, HllConstants.READ_TIME_OUT);
			billDetailRes=JSON.parseObject(res, HllShopBillDetailResponse.class);
			if(billDetailRes!=null&&"000".equals(billDetailRes.getCode())){
//				writelogFileName("\r\n***********获取哗啦啦历史账单数据成功:\n入参:"+"url-"+url+"postMap-"+new JSONObject(postMap)+"signMap"+new JSONObject(signMap));
			}else{
				writelogFileName("\r\n***********获取哗啦啦历史账单数据发生异常:\n入参:"+"url-"+url+"postMap-"+new JSONObject(postMap)+"signMap"+new JSONObject(signMap)+"返参:"+res);
			}
//			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"***************哗啦啦返回历史账单数据****************日期:"+reportDate+"门店:"+shopID+"pageNo:"+pageNo+"pageSize:"+pageSize+"\r\n"+res+"\r\n");
		}catch(Exception e){
//			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"***********获取哗啦啦历史账单数据发生异常:\n入参:"+"url-"+url+"postMap-"+postMap,e);
			writelogFileName("\r\n***********获取哗啦啦历史账单数据发生异常:\n入参:"+"url-"+url+"postMap-"+postMap+getTrace(e));
//			throw e;
		}
		return billDetailRes;
	}

	// 写日志
	public static void writelogFileName(String log) throws IOException {
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
	}
	
	public String getTrace(Throwable t) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer= stringWriter.getBuffer();
		return buffer.toString();
	}

	
	   

}
