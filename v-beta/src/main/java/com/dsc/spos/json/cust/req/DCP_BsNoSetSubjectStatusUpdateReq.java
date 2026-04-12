package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BsNoSetSubjectStatusUpdateReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_BsNoSetSubjectStatusUpdateReq.LevelRequest request;

    @Data
    public class LevelRequest{
        private String status;
        @JSONFieldRequired
        private String opType;
        private String eId;

        @JSONFieldRequired
        private String accountId;

        @JSONFieldRequired
        private String coaRefID;

        //@JSONFieldRequired
        //private List<accList> accList;
    }

    @Data
    public class accList{
        @JSONFieldRequired
        private String accountId;

        @JSONFieldRequired
        private String coaRefID;
    }

}
