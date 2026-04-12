package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DiscGetDCP
 *    說明：触屏折扣查询DCP
 * 服务说明：触屏折扣查询DCP
 * @author jinzma 
 * @since  2017-03-09
 */
public class DCP_ScutKeyQueryReq extends JsonBasicReq {

	private String keyTxt ;
	private String docType;

	
	public String getKeyTxt() {
		return keyTxt;
	}
	public void setKeyTxt(String keyTxt) {
		this.keyTxt = keyTxt;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	



}
