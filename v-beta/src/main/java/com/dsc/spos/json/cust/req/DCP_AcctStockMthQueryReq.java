package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_AcctStockMthQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {

        private String eId;
        @JSONFieldRequired(display = "账套")
        private String accountID;
        @JSONFieldRequired(display = "年度")
        private String year;
        @JSONFieldRequired(display = "期别")
        private String period;
        private String isDiffQty;
    }
}
