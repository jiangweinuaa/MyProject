package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author liyyd
 */
@Getter
@Setter
public class DCP_BankImportExcelRes extends JsonBasicRes {

  private String totalRecords;

  private String successRecords;

  private String failureRecords;

  private List<FailureData> failureDatas;

  @Getter
  @Setter
  public class FailureData {
    private String bankCode;
    private String sName;
    private String fName;
    private String failureDesc;
  }

}
