package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ArSetupSubjectDeleteReq extends JsonBasicReq {
    private DCP_ArSetupSubjectDeleteReq.Level1Elm request;

    @Data
    public class Level1Elm {
        private String status;
        private String accountId;
        private String setupType;
        private String eid;
    }
}
