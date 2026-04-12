package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_MoDeleteReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_MoDeleteReq.levelElm request;

    @Data
    public class levelElm{
        @JSONFieldRequired
        private String moNo;
    }

}
