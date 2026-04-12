package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.cust.JsonReq;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DCP_WrtOffDeleteReq extends JsonReq {

    private DCP_WrtOffDeleteReq.Request request;

    @NoArgsConstructor
    @Data
    public static class Request {
        private String status;
        private String accountId;
        private String wrtOffType;
        private String wrtOffNo;
        private String item;
    }
}
