package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 服務函數：结算日条件 DCP_BillDate
 * @author 01029
 */
@Getter
@Setter
public class DCP_BillDateEnableReq extends JsonBasicReq {

  private levelRequest request;
  

  @Getter
  @Setter
  public class levelRequest {
	private String oprType;
    private List<level1Elm> enableList; 
  }

  @Getter
  @Setter
  public class level1Elm {

    private String billDateNo;


  }

}
