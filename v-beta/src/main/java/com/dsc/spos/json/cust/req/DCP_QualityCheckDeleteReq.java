package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_QualityCheckUpdateReq.Detail1;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

 
@Getter
@Setter
public class DCP_QualityCheckDeleteReq extends JsonBasicReq {
	@JSONFieldRequired
  private levelRequest request;


  @Getter
	@Setter
	public class levelRequest {

		@JSONFieldRequired
		private List<Detail1> deleteList;


	}

	@Getter
	@Setter
	public class Detail1 {
		@JSONFieldRequired
		private String qcBillNo;
  }

 

}
