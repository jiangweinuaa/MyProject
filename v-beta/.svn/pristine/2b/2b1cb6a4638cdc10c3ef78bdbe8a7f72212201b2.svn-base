package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_ArSetupSubjectQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ArSetupSubjectQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        private String status;
        private String accountId;
        private String setupType;
    }

}
