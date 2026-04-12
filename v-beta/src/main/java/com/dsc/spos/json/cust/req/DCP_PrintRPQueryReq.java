package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DCP_PrintRPQueryReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_PrintRPQueryReq.Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired
        private String modularNo;
        @JSONFieldRequired
        private String queryType;
        private List<String> customer;

        private String status;

    }

}