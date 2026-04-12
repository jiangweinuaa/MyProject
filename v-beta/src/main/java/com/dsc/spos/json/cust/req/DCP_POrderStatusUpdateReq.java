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
public class DCP_POrderStatusUpdateReq extends JsonBasicReq {

  private levelRequest request;
  

  @Getter
  @Setter
  public class levelRequest {
	  @JSONFieldRequired
	private String oprType;
	  @JSONFieldRequired
		private String pOrderNo;
	  private String reason;
	@JSONFieldRequired
	private List<Detail1> confirmList;
  }
 
  @Getter
	@Setter
	public class Detail1 {
		@JSONFieldRequired
		private String item;
		@JSONFieldRequired
		private String pluNo;
		@JSONFieldRequired
		private String featureNo;
		@JSONFieldRequired
		private String reviewQty;
		@JSONFieldRequired
		private String reason;
		private String pUnit;
		private String pQty;

		//distriPrice,distriAmt, price, amt, isAdditional
	  private String distriPrice;
	  private String distriAmt;
	  private String price;
	  private String amt;
	  private String isAdditional;
 
	}
}
