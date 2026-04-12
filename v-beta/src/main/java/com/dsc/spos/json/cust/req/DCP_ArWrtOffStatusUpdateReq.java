package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ArWrtOffStatusUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private List<WrtOffList> wrtOffList;
        private String status;
        @JSONFieldRequired
        private String opType;
        private String eId;
    }

    @NoArgsConstructor
    @Data
    public class WrtOffList {
        @JSONFieldRequired
        private String arNo;

    }
}
