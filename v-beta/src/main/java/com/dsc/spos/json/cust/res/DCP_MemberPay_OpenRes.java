package com.dsc.spos.json.cust.res;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 会员支付： 调用 CRM ： MemberPay 接口
 * 
 * @author Huawei
 *
 */
public class DCP_MemberPay_OpenRes extends JsonRes {
	
	private JSONObject datas;

	public JSONObject getDatas() {
		return datas;
	}

	public void setDatas(JSONObject datas) {
		this.datas = datas;
	}
	
}
