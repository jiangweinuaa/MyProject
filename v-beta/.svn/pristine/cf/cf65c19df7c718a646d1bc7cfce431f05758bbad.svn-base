package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ApSetupSubjectDetailQueryReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ApSetupSubjectDetailQueryReq.LevelRequest request;

    @Data
    public class LevelRequest {

        private String status;
        private String accountId;
        private String setupType;
    }
}
