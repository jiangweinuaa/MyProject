package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：SaleSumGet
 *   說明：销售汇总查询
 * 服务说明：销售汇总查询
 * @author luoln
 * @since  2017-06-21
 */
public class DCP_SaleSumQueryReq extends JsonBasicReq{

	private String oShopId;
	private String bDate;
	private String eDate;
	private String keyTxt;
	private String[] pluNO;
	

	public String getoShopId() {
		return oShopId;
	}
	public void setoShopId(String oShopId) {
		this.oShopId = oShopId;
	}
	public String getbDate() {
		return bDate;
	}
	public void setbDate(String bDate) {
		this.bDate = bDate;
	}
	
	public String geteDate() {
		return eDate;
	}
	public void seteDate(String eDate) {
		this.eDate = eDate;
	}
	
	public String getKeyTxt() {
		return keyTxt;
	}
	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}
	
	public String[] getPluNO() {
		return pluNO;
	}
	public void setPluNO(String[] pluNO) {
		this.pluNO = pluNO;
	}
}
