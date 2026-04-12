package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_PurOrderStatusUpdateReq  extends JsonBasicReq {

    private DCP_PurOrderStatusUpdateReq.levelElm request;

    @Data
    public class levelElm {

        private String purOrderNo;
        private String oprType;
    }
}
