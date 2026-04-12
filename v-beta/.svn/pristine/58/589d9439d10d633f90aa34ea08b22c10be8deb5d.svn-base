package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_SStockOutDetailQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_SStockOutDetailQueryReq.LevelElm request;

    @Data
    public class LevelElm{
        @JSONFieldRequired(display = "出库单号")
        private String sStockOutNo;
    }

}
