package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_StockOutNoticeDetailQueryReq extends JsonBasicReq {
  private levelRequest request;


	@Getter
	@Setter
  public class levelRequest {
    private String billNo;
    
    private String detailFilter;
    @JSONFieldRequired
    private String billType;

    private String getType;

  }

}
