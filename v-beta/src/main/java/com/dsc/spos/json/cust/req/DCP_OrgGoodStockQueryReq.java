package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
 

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_OrgGoodStockQueryReq extends JsonBasicReq {
  private levelRequest request;


	@Getter
	@Setter
  public class levelRequest {
		@JSONFieldRequired
		private List<OrgList> orgList;
		@JSONFieldRequired
		private List<PluList> pluList;

  }
	
	@Getter
	@Setter
	public class OrgList {
		@JSONFieldRequired
		private String orgNo;
		private String wareHouse;
	}
	
	@Getter
	@Setter
	public class PluList {
		@JSONFieldRequired
		private String pluNo;
		private String featureNo;
		private String pUnit;

	}

}
