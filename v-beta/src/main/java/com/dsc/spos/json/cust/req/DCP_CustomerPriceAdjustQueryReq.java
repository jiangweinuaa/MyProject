package com.dsc.spos.json.cust.req;

import  com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/03/12
 */
@Getter
@Setter
public class DCP_CustomerPriceAdjustQueryReq extends JsonBasicReq{

   private Request request;

    @Getter
    @Setter
    public class Request{
          @JSONFieldRequired(display="状态码")
          private String status;
          @JSONFieldRequired(display="关键字：单号")
          private String keyTxt;
    }

}