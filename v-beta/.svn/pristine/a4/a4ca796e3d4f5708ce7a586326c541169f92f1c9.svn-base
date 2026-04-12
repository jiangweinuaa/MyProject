package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：POAdiseGet
 *    說明：要货建议量查询
 * 服务说明：要货建议量查询
 * @author luoln 
 * @since  2017-07-04
 */
public class DCP_POAdiseQueryReq extends JsonBasicReq{
	/**JSON Request
	 * 
	 * 	"serviceId": "POAdiseGet",	必傳，服務名	
		"token": "f14ee75ff5b220177ac0dc538bdea08c",	必傳且非空，訪問令牌	
		"gDate":[ "20170707","20170101"]	必傳且非空，参考日期	
		"rDate": "20170707"	必傳且非空，需求日期	
		"pluNO":[ "1","2"]	必傳且非空，商品编码	传的时候 plunofeatureno  比如100011101
	 * 
	**/
	
	private String[] gDate;
	private String rDate;
	private String[] pluNO;	
	//private String[] pluNOFeatureNO;
	
	public String[] getgDate() {
		return gDate;
	}
	public void setgDate(String[] gDate) {
		this.gDate = gDate;
	}
	
	public String getrDate() {
		return rDate;
	}
	public void setrDate(String rDate) {
		this.rDate = rDate;
	}
	
	public String[] getPluNO() {
		return pluNO;
	}
	public void setPluNO(String[] pluNO) {
		this.pluNO = pluNO;
	}
	
}
