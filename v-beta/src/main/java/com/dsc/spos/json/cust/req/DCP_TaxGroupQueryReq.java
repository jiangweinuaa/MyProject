package com.dsc.spos.json.cust.req;

import  com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/02/24
 */
@Getter
@Setter
public class DCP_TaxGroupQueryReq extends JsonBasicReq{

   private Request request;

    @Getter
    @Setter
    public class Request{
          private String taxCode;
          private String status;
          private String keyTxt;
    }

}