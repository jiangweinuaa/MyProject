package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_WoStatusQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @Data
    public class Request {
        private String corp;
        private String corpName;
        private String year;
        private String period;
        private String bDate;
        private String workType;
    }
}
