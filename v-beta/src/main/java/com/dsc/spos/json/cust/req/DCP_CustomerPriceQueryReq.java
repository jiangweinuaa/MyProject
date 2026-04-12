package com.dsc.spos.json.cust.req;

import  com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/03/11
 */
@Getter
@Setter
public class DCP_CustomerPriceQueryReq extends JsonBasicReq{

   private Request request;

    @Getter
    @Setter
    public class Request{
          private String customerType;
          private String customerNo;
          @JSONFieldRequired(display="编码/名称模糊搜索")
          private String keyTxt;
    }

}