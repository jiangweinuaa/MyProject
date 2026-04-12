package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_WOGoodsQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private RequestLevel request;

    @NoArgsConstructor
    @Data
    public  class RequestLevel {
        @JSONFieldRequired
        private String stockTakeNo;
        private String accountDate;
        private String beginDate;
        private String endDate;
        private String departId;
    }
}
