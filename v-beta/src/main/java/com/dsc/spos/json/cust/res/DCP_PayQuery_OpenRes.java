package com.dsc.spos.json.cust.res;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.JsonBasicRes;

/**
 * 支付查询， 调用CRM : Query 接口 
 * @author Huawei
 *
 */
public class DCP_PayQuery_OpenRes extends JsonBasicRes {

	private JSONObject datas;

	public JSONObject getDatas() {
		return datas;
	}

	public void setDatas(JSONObject datas) {
		this.datas = datas;
	}
	
}
