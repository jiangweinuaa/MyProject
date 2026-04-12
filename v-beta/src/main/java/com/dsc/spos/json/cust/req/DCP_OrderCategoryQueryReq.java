package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：OrderCategoryGetDCP
 * 服务说明：外卖分类查询
 * @author jinzma 
 * @since  2019-03-11
 */
public class DCP_OrderCategoryQueryReq extends JsonBasicReq 
{
	private String keyTxt;

	public String getKeyTxt() {
		return keyTxt;
	}
	
	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}	

}
