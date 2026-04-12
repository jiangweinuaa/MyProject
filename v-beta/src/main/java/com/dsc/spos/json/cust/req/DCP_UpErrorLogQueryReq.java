package com.dsc.spos.json.cust.req;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：UpErrorLogGet
 *   說明：门店上传异常查询
 * 服务说明：门店上传异常查询
 * @author panjing
 * @since  2016-12-13
 */
public class DCP_UpErrorLogQueryReq extends JsonBasicReq{

	private List<Map<String, String>> dates;
	private List<Map<String, String>> shops;
	private List<Map<String, String>> docTypes;
	public List<Map<String, String>> getDates() {
		return dates;
	}
	public void setDates(List<Map<String, String>> dates) {
		this.dates = dates;
	}
	public List<Map<String, String>> getShops() {
		return shops;
	}
	public void setShops(List<Map<String, String>> shops) {
		this.shops = shops;
	}
	public List<Map<String, String>> getDocTypes() {
		return docTypes;
	}
	public void setDocTypes(List<Map<String, String>> docTypes) {
		this.docTypes = docTypes;
	}




}
