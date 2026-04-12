package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：EDateGet
 *    說明：营业日查询
 * 服务说明：营业日查询
 * @author panjing 
 * @since  2016-09-22
 */
public class DCP_EDateQueryReq extends JsonBasicReq {
	private String oShopId;

	public String getoShopId() {
		return oShopId;
	}

	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}

}
