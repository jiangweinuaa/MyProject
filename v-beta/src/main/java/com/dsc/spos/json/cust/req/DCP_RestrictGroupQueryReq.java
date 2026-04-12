package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_RestrictGroupQueryReq extends JsonBasicReq {
    private DCP_RestrictGroupQueryReq.level1Elm request;

    @Data
    public class level1Elm {
        private String status;
        private String groupType;
        private String keyTxt;
        private String dept;
        private String bizpartnerNo;
    }
}
