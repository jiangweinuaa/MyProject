package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_QualityCheckProQueryReq extends JsonBasicReq {
	@JSONFieldRequired
  private levelRequest request;


	@Getter
	@Setter
  public class levelRequest {
		@JSONFieldRequired
		private String beginDate;
		@JSONFieldRequired
		private String endDate;


  }

}
