package com.dsc.spos.hll.api.response;

import java.util.List;

/**
 * 基础response
 * @author LN 08546
 */
public class HllData {

	public HllData() {
		
	}

	private List<HllOrderDetail> billDetail;
	
	private HllPageResponse pageResponseInfo;

	public List<HllOrderDetail> getBillDetail() {
		return billDetail;
	}

	public void setBillDetail(List<HllOrderDetail> billDetail) {
		this.billDetail = billDetail;
	}

	public HllPageResponse getPageResponseInfo() {
		return pageResponseInfo;
	}

	public void setPageResponseInfo(HllPageResponse pageResponseInfo) {
		this.pageResponseInfo = pageResponseInfo;
	}

}
