package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 服務函數：DCP_BankDelete
 * `
 */
@Getter
@Setter
public class DCP_BankDeleteReq extends JsonBasicReq {

  private levelRequest request;


  @Getter
  @Setter
  public class levelRequest {
    private List<level1Elm> banklist;
  }

  @Getter
  @Setter
  public class level1Elm {
    private String bankcode;


  }

}
