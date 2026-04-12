package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 服務函數：收付款条件 DCP_PayDate
 * `
 */
@Getter
@Setter
public class DCP_PayDateDeleteReq extends JsonBasicReq {

  private levelRequest request;


  @Getter
  @Setter
  public class levelRequest {
    private List<level1Elm> deleteList;
  }

  @Getter
  @Setter
  public class level1Elm {
 
	private String payDateType;
    private String payDateNo;


  }

}
