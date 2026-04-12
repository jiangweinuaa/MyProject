package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：GoodGet
 *    說明：商品输入检查
 * 服务说明： 商品输入检查
 * @author panjing 
 * @since  2016-09-22
 */
public class DCP_GoodQueryReq extends JsonBasicReq{

	private String keyTxt;
	private String docType;
	private String docNO;

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
	
	public String getDocNO() {
		return docNO;
	}
	public void setDocNO(String docNO) {
		this.docNO = docNO;
	}
}
