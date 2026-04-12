package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ArSetupSubjectStatusUpdateReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ArSetupSubjectStatusUpdateReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String coaRefID;
        @JSONFieldRequired
        private String accountId;
        private String status;
        @JSONFieldRequired
        private String setupType;
        @JSONFieldRequired
        private String opType;
        private String eId;
    }

}
