package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_QualityCheckQueryReq extends JsonBasicReq {
	@JSONFieldRequired
  private levelRequest request;


	@Getter
	@Setter
  public class levelRequest {
		private String status;
		@JSONFieldRequired
		private String qcType;
		private String dateType;
		private String beginDate;
		private String endDate;
		private String keyTxt;



  }

}
