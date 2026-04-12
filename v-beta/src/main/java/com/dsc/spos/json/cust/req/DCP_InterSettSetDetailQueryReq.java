package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_InterSettSetDetailQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_InterSettSetDetailQueryReq.levelRequest request;

    @Data
    public class levelRequest{
        @JSONFieldRequired
        private String eId;
        @JSONFieldRequired
        private String processNo;

        private String versionNum;

    }

}