package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_CategorySubjectDeleteReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_CategorySubjectDeleteReq.Level1Elm request;

    @Data
    public class Level1Elm {
        private String status;
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String coaRefID;
    }
}
