package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 服務函數：发票类型 DCP_InvoiceType
 * @author 01029
 */
@Getter
@Setter
public class DCP_InvoiceTypeEnableReq extends JsonBasicReq {

  private levelRequest request;
  

  @Getter
  @Setter
  public class levelRequest {
	private String oprType;
    private List<level1Elm> invoiceTypeList; 
  }

  @Getter
  @Setter
  public class level1Elm {
	private String taxArea;  
	
    private String invoiceCode;


  }

}
