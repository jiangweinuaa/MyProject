package com.dsc.spos.thirdpart.youzan;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.thirdpart.youzan.response.YouZanTokenRes;
import com.dsc.spos.thirdpart.youzan.util.YouZanUtils;

public class YouZanTokenService{
	
	private static Object LockToken = new Object();
	
	public static String cacheName_yz_access_token = "youZanAccessToken";

//	public String getToken(String kdtId,Map<String, Object> basicMap) throws Exception {
//		String token="";
//		RedisPosPub redis = null;
//		String tokenResStr=null;
//		try{
//			String grantId="";
//			if(basicMap!=null&&!basicMap.isEmpty()){
//				grantId=basicMap.get("SHOPSN")==null?"":basicMap.get("SHOPSN").toString();
//			}
//			if(grantId==null||grantId.length()<1){
//				grantId=kdtId;
//			}
//			redis = new RedisPosPub();
//			String redisField=cacheName_yz_access_token+"_"+grantId;
//			tokenResStr=redis.getHashMap(YouZanUtils.redisKey, redisField);
//			YouZanTokenRes res1=new YouZanTokenRes();
//			res1=com.alibaba.fastjson.JSON.parseObject(tokenResStr, YouZanTokenRes.class);
//			if (!checkToken(res1)) {
//				// 负载均衡下，单个tomcat做lock也不太保险
//				synchronized (LockToken) {
//					res1=getNewToken(kdtId,basicMap);
//					saveRedis(YouZanUtils.redisKey, redisField, new com.google.gson.Gson().toJson(res1));
//				}
//			}
//			token=res1.getData().getAccess_token();
//			
//		}catch(Exception e){
//			
//		}finally{
//			if(redis!=null){
//				redis.Close();
//			}
//		}
//		return token;
//		
//	}
//	
//	public void saveRedis(String redisKey,String hashKey,String value) throws Exception{
//		RedisPosPub redis = null;
//		try {
//			redis = new RedisPosPub();
//			boolean isexistHashkey = redis.IsExistHashKey(redisKey, hashKey);
//			if(isexistHashkey)
//			{
//				redis.DeleteHkey(redisKey, hashKey);//
//				YouZanUtils.writelogFileName(YouZanUtils.redisFileName,"【删除存在hashKey的缓存】成功！"+" redisKey:"+redisKey+" hashKey:"+hashKey);
//			}
//			boolean nret = redis.setHashMap(redisKey, hashKey, value);
//			if(nret)
//			{
//				YouZanUtils.writelogFileName(YouZanUtils.redisFileName,"【写缓存】OK"+" redisKey:"+redisKey+" hashKey:"+hashKey+" value:"+value);
//			}
//			else
//			{
//				YouZanUtils.writelogFileName(YouZanUtils.redisFileName,"【写缓存】Error"+" redisKey:"+redisKey+" hashKey:"+hashKey+" value:"+value);
//			}
//			
//
//		} 
//		catch (Exception e) {
//			YouZanUtils.writelogFileName(YouZanUtils.redisFileName,"【写缓存】Exception:"+ExceptionUtils.getRootCauseMessage(e));	
//		}
//		finally{
//			if(redis!=null){
//				
//			}
//		}
//	}
//	
//	public YouZanTokenRes getNewToken(String kdtId,Map<String, Object> basicMap) throws Exception {
////		String thisToken="";
//		String url="https://open.youzanyun.com";
//		String clientId="";
//		String clientSecret="";
//		String grantId="";
//		if(basicMap==null||basicMap.isEmpty()){
//			if(kdtId==null||kdtId.trim().length()<1){
//				throw new Exception("有赞渠道资料配置异常!");
//			}
//			YouZanCallBackService ycb=new YouZanCallBackService();
//			basicMap=ycb.getYouZanMap(kdtId);
//		}
//		if(basicMap!=null&&!basicMap.isEmpty()){
//			url=basicMap.get("API_URL")==null?"":basicMap.get("API_URL").toString();
//			clientId=basicMap.get("API_KEY")==null?"":basicMap.get("API_KEY").toString();
//			clientSecret=basicMap.get("API_SECRET")==null?"":basicMap.get("API_SECRET").toString();
//			grantId=basicMap.get("SHOPSN")==null?"":basicMap.get("SHOPSN").toString();
//		}
//
//		if(url==null||url.trim().length()<1){
//			throw new Exception("有赞渠道URL资料配置异常!");
//		}
//		if(clientId==null||clientId.trim().length()<1){
//			throw new Exception("有赞渠道API_KEY资料配置异常!");
//		}
//		if(clientSecret==null||clientSecret.trim().length()<1){
//			throw new Exception("有赞渠道API_SECRET资料配置异常!");
//		}
//		if(grantId==null||grantId.trim().length()<1){
//			throw new Exception("有赞渠道SHOPSN资料配置异常!");
//		}
//		
//		
//		url+="/auth/token";
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("client_id", clientId);
//		params.put("client_secret", clientSecret);
//		params.put("authorize_type", "silent");
//		params.put("grant_id", grantId);
//		Map<String, Object> headersMap = new HashMap<String, Object>();
//		headersMap.put("Content-Type", "application/json;charset=UTF-8");
//		String resStr=YouZanUtils.PostDataToken("getToken", url, headersMap, JSON.toJSONString(params));
//		YouZanTokenRes token=JSON.parseObject(resStr, YouZanTokenRes.class);
//		if(token!=null&&"TRUE".equals(token.getSuccess().toUpperCase())){
////			thisToken=token.getData().getAccess_token();
//		}else{
//			throw new Exception("获取有赞access_token异常");
//		}
//		return token;
//	}
//	
//	private static boolean checkToken(YouZanTokenRes res) {
//		if (res == null) {
//			return false;
//		}
//		Calendar cal = Calendar.getInstance();
//		long nowUtcTime = cal.getTimeInMillis();
//		try{
//			//Expires 毫秒级
//			long time1=(Long.valueOf(res.getData().getExpires())-nowUtcTime)/1000-300;//失效前300秒就刷新
////			Log(youZanFileName, "有赞access_token失效时间(毫秒):"+res.getData().getExpires()+";距离失效"+time1+"秒");
//			if (time1<=0) {
//				return false;
//			}
//			return true;
//		}catch(Exception e){
//			
//		}
//		return false;
//		
//	}


}
