package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_PurReceiveQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        private String status;
        private String orgNo;
        private String dateType;
        private String beginDate;
        private String endDate;
        private String keyTxt;
        private String receiptOrgNo;
        private String searchScope;
    }


}
