package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_InterSettProcessReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public  class Request {
        @JSONFieldRequired
        private String corp;
        private String corpName;
        private String bizpartner1;
        private String bizpartner2;
        @JSONFieldRequired
        private String startDate;
        @JSONFieldRequired
        private String endDate;
        @JSONFieldRequired
        private String isTaxRateSplit;
    }
}
