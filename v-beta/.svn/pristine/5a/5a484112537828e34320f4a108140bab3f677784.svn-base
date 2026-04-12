package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 发票类型信息
 * @author 01029
 */
@Getter
@Setter
public class DCP_InvoiceTypeUpdateReq extends JsonBasicReq {

  private levelRequest request;

  public levelRequest getRequest() {
    return request;
  }

  public void setRequest(levelRequest request) {
    this.request = request;
  }

  @Getter
  @Setter
  public class levelRequest {
    private String nation;

    private String bankCode;

    private String ebankCode;
    private String status;

    private List<level1Elm> bankName_lang;


  }

  @Getter
  @Setter
  public class level1Elm {

    private String shortName;
    private String langType;

    private String fullName;

  }


}
