package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_StockOutNoticeQueryReq extends JsonBasicReq {
  private levelRequest request;


	@Getter
	@Setter
  public class levelRequest {
		private String status;
		private String billType;
		private String dateType;
		private String beginDate;
		private String endDate;
		private String keyTxt;
		private String deliverOrgNo;
		private String searchScope;

		private String getType;


  }

}
