package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

public class DCP_DifferenceQueryReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

    @Data
	public class levelElm{
		private String ofNo;
		private String docType;

        private String beginDate;
        private String endDate;
        private String keyTxt;
        private String queryType;


	}
}