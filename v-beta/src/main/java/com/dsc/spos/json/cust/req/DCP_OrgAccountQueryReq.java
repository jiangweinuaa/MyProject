package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class DCP_OrgAccountQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request reuqest;

    @NoArgsConstructor
    @Data
    public class Request {
        private String status;
        private String keyTxt;
        private String account;
        private String bankNo;
    }
}
