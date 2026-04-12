package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PurStockOutDetailQueryReq extends JsonBasicReq {

    private DCP_PurStockOutDetailQueryReq.levelElm request;

    @Data
    public class levelElm {

        @JSONFieldRequired(display = "退货出库单号")
        private String pStockOutNo;

    }
}
