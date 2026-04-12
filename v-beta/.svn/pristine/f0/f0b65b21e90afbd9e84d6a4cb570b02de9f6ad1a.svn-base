package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_MoConfirmReq  extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_MoConfirmReq.level1Elm request;

    @Data
    public class level1Elm{
        @JSONFieldRequired
        private String moNo;
    }

}
