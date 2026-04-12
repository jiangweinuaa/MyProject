package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 同期五周销售额查询
 * @author yuanyy
 *
 */
public class DCP_SaleAmtOldQueryReq extends JsonBasicReq {
	private String eDate;

	public String geteDate() {
		return eDate;
	}

	public void seteDate(String eDate) {
		this.eDate = eDate;
	}
	
}
