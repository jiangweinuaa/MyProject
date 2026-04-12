package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 服務函數：收付款日条件 DCP_PayDate
 * @author 01029
 */
@Getter
@Setter
public class DCP_BizPartnerEnableReq extends JsonBasicReq {

  private levelRequest request;
  

  @Getter
  @Setter
  public class levelRequest {
	private String oprType;
    private List<level1Elm> bizPartnerList; 
  }

  @Getter
  @Setter
  public class level1Elm {

	  private String bizPartnerNo;


  }

}
