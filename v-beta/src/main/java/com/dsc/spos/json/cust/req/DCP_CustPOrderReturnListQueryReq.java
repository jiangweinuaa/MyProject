package com.dsc.spos.json.cust.req;

import  com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/04/07
 */
@Getter
@Setter
public class DCP_CustPOrderReturnListQueryReq extends JsonBasicReq{

   private Request request;

    @Getter
    @Setter
    public class Request{
          private String beginDate;
          private String endDate;
          private String pOrderNo;
          private String customer;
          private String keyTxt;
    }

}