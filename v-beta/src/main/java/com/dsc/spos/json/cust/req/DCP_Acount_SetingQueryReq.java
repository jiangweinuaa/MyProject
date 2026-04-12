package com.dsc.spos.json.cust.req;

import  com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/03/17
 */
@Getter
@Setter
public class DCP_Acount_SetingQueryReq extends JsonBasicReq{

   private Request request;

    @Getter
    @Setter
    public class Request{
          private String account;
          private String keyTxt;
          private String status;
    }

}