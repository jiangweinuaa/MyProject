package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_DistriOrderDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_DistriOrderDeleteReq.levelElm request;

    @Data
    public class levelElm {

        @JSONFieldRequired(display = "铺货单号")
        private String billNo;

    }
}
