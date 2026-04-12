package com.dsc.spos.utils;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.ResultDatas;

public class CheckIFUtil {
	
	public ResultDatas check(DsmDAO dao, JSONObject reqJson, JSONObject signJson) throws Exception{
	  
		ResultDatas rd = new ResultDatas();
		
		String userKey = signJson.getString("key");
		String sign = signJson.getString("sign");
		
		String eId = "";
		String userSignKey = "";
		String sql = "select user_signKey, user_Name ,user_key , EID   from  DCP_USER_SIGN "
				+ " where user_key = '"+userKey+"' and STATUS='100' ";
		
		List<Map<String, Object>> getUserDatas = dao.executeQuerySQL(sql,null);
		if(getUserDatas!= null && !getUserDatas.isEmpty())
		{
			for (Map<String, Object> map : getUserDatas) {
				userSignKey = map.get("USER_SIGNKEY").toString();
				userKey = map.get("USER_KEY").toString();
				eId = map.get("EID").toString();
			}
		}
		
		String reqJsonStr = reqJson.toString();
		String result = PosPub.encodeMD5(reqJsonStr + userSignKey);
		
		if(result.equals(sign)){
			/**
			 * 根据key 去查找signKey、公司别等信息  生成之后和 sign签名 对比
			 */
			rd.seteId(eId);
			rd.setStatus("SUCCESS");
			rd.setDesctiption("签名校验成功！");
		}
		else{
			rd.seteId(eId);
			rd.setStatus("FAILED");
			rd.setDesctiption("签名校验失败！");
		}
		
		return rd;
	}

}
