package com.dsc.spos.json.cust.req;

import  com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author liyyd
 * @date 2025/02/19
 */
@Getter
@Setter
public class DCP_SStockInDetailQueryReq extends JsonBasicReq{

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request{
          @JSONFieldRequired(display="入库类型")
          private String stockInType;
          @JSONFieldRequired(display="入库单号")
          private String sStockInNo;
    }

}