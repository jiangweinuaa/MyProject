package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PurStockOutDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_PurStockOutDeleteReq.levelElm request;

    @Data
    public class levelElm {

        @JSONFieldRequired(display = "采购退货出库单号")
        private String pStockOutNo;
        @JSONFieldRequired(display = "单据类型")
        private String billType;
    }
}
