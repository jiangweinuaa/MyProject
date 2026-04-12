package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_QualityCheckQueryReq.levelRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_QualityCheckPendingListQueryReq extends JsonBasicReq {
	@JSONFieldRequired
	  private levelRequest request;


		@Getter
		@Setter
	  public class levelRequest {
			@JSONFieldRequired
			private String qcType;
			private String beginDate;
			private String endDate;
	
			private String keyTxt;



	  }

}
