package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_TaxSubjectDeleteReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_TaxSubjectDeleteReq.Level1Elm request;

    @Data
    public class Level1Elm {
        private String status;
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String coaRefID;
        //@JSONFieldRequired
        private List<taxCodeList> taxCodeList;
    }

    @Data
    public class taxCodeList{
        @JSONFieldRequired
        private String taxCode;
    }

}
