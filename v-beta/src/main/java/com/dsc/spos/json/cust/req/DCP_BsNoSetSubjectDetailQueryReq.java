package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_BsNoSetSubjectDetailQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_BsNoSetSubjectDetailQueryReq.levelRequest request;

    @Data
    public class levelRequest{

        private String status;
        @JSONFieldRequired
        private String accountId;
    }

}