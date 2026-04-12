package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_TaxSubjectStatusUpdateReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_TaxSubjectStatusUpdateReq.LevelRequest request;

    @Data
    public class LevelRequest{

        private String status;
        @JSONFieldRequired
        private String opType;
        private String eId;

        private List<AccList> acctList;
    }

    @Data
    public class AccList{
        @JSONFieldRequired
        private String coaRefID;
        @JSONFieldRequired
        private String accountId;
    }

}
