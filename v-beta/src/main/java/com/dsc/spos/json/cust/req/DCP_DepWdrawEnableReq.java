package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_DepWdrawEnableReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String oprType;
        @JSONFieldRequired
        private List<DepList> depList;
    }

    @NoArgsConstructor
    @Data
    public class DepList {
        @JSONFieldRequired
        private String depWdrawCode;
    }

}
