package com.dsc.spos.json.cust.res;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 修改原来服务，改为移动支付 定金补录
 * @author wangzyc
 *
 */
@Data
public class DCP_OrderPay_OpenRes extends JsonRes {
	
	private level1Elm datas;


	@Data
	public class level1Elm{
	    private JSONObject createPay;    // 移动支付节点
	    private JSONObject memberPay;    // 会员支付节点
	}

	
}
