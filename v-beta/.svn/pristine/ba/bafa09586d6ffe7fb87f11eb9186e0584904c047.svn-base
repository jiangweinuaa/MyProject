package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 网点信息
 * @author liyyd
 */

@Getter
@Setter
public class DCP_BankImportExcelReq extends JsonBasicReq {

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
    private List<Bank> banklist;
  }

  @Getter
  @Setter
  public class Bank {
    private String bankCode;
    private String nation;
    private String sname;
    private String fname;
    private String ebank;

  }

}
