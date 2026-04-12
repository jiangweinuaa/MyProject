package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_BillDateQueryReq extends JsonBasicReq {
  private levelRequest request;


	@Getter
	@Setter
  public class levelRequest {
    private String status;
    private String keyTxt;

  }

}
