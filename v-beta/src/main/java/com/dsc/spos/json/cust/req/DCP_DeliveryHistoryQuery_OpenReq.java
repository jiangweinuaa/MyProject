package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_DeliveryHistoryQuery_OpenReq extends JsonBasicReq {

    private level1Elm request;
    @Data
    public class level1Elm
    {
        private String shipperNo;
        private String shipperCode;
    }
}
