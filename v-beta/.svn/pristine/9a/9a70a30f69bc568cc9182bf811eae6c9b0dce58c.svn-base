package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ApSetupSubjectDeleteReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ApSetupSubjectDeleteReq.LevelRequest request;

    @Data
    public class LevelRequest{
        private String status;
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String setupType;
        private String eid;
    }

}
