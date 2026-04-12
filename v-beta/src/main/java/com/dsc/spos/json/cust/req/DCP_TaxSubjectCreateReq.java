package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_TaxSubjectCreateReq  extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_TaxSubjectCreateReq.Level1Elm request;

    @Data
    public class Level1Elm{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String coaRefID;

        @JSONFieldRequired
        private String status;

        @JSONFieldRequired
        private List<DCP_TaxSubjectCreateReq.SetUpList> setupList;
    }


    @Data
    public class SetUpList{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String setupType;
        @JSONFieldRequired
        private String taxCode;
        private String subjectId;
        private String memo;

    }


}
