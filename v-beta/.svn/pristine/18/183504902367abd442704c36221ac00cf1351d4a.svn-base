package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
 
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *  
 * @author 01029
 */
@Getter
@Setter
public class DCP_QualityCheckStateUpdateReq extends JsonBasicReq {

  private levelRequest request;
  

  @Getter
  @Setter
  public class levelRequest {
	  @JSONFieldRequired
	private String oprType;
	@JSONFieldRequired
	private List<Detail1> dataList;
  }
 
  @Getter
	@Setter
	public class Detail1 {
		@JSONFieldRequired
		private String qcBillNo;
		@JSONFieldRequired
		private String passQty;
		@JSONFieldRequired
		private String rejectQty;
		private String result;
		private String memo;
		private String inspector;
		private String inspectDate;
		private String inspectTime;
	}
}
