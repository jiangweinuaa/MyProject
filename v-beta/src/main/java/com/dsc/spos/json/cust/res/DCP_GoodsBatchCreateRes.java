package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

/*
 * 服务函数:GoodsBatchCreateDCP
 * 服务说明:商品批号新建
 * @author JZMA
 * @since  2019-07-18
 */
public class DCP_GoodsBatchCreateRes extends JsonRes {

	private String batchNo;
	private String serialNo;

	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}	
}
