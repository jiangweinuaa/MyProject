package com.dsc.spos.service.imp.json;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.JsonIFReq;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.PosPub;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * 测试接口联通方式
 *
 */
public class DCP_TestPort extends SPosBasicService<JsonIFReq, JsonBasicRes> {

	@Override
	protected boolean isVerifyFail(JsonIFReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<JsonIFReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<JsonIFReq>(){};
	}

	@Override
	protected JsonBasicRes getResponseType() {
		// TODO Auto-generated method stub
		return new JsonBasicRes();
	}

	@Override
	protected JsonBasicRes processJson(JsonIFReq req) throws Exception {
		// TODO Auto-generated method stub
		JsonBasicRes res = null;
		res = this.getResponse();
		
		JsonObject reqGson = req.getRequest();
		String reqJsonStr = reqGson.toString();
		JSONObject reqJson = JSONObject.parseObject(reqJsonStr, Feature.OrderedField);
		
		JsonObject signGson = req.getSignJson();
		String signJsonStr = signGson.toString();
		JSONObject signJson = JSONObject.parseObject(signJsonStr);
		
		String userKey = signJson.getString("key");
		String sign = signJson.getString("sign");
		
		String userSignKey = "";
		String sql = "select user_signKey, user_Name ,user_key , EID   from  TestInterface where user_key = '"+userKey+"' and status='100' ";
		
		List<Map<String, Object>> getUserDatas=this.doQueryData(sql, null);
		if(getUserDatas!= null && !getUserDatas.isEmpty())
		{
			for (Map<String, Object> map : getUserDatas) {
				userSignKey = map.get("USER_SIGNKEY").toString();
				userKey = map.get("USER_KEY").toString();
			}
		}
		
		String result = PosPub.encodeMD5(reqJson + userSignKey);
		
		if(result.equals(sign)){
			/**
			 * 根据key 去查找signKey、公司别等信息  生成之后和 sign签名 对比
			 */
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务连接成功！");
		}
		else{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务连接失败！请检查账号密钥！");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(JsonIFReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
