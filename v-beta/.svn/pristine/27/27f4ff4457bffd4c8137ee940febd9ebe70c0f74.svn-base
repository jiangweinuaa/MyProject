package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BsNoSetSubjectUpdateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_BsNoSetSubjectUpdateReq.Level1Elm request;

    @Data
    public class Level1Elm{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String coaRefId;

        @JSONFieldRequired
        private String status;

        @JSONFieldRequired
        private List<DCP_BsNoSetSubjectUpdateReq.SetUpList> setUpList;
    }


    @Data
    public class SetUpList{
        //private String accountId;
        //private String coaRefId;
        private String bsNo;
        private String manuExpSubject;
        private String salesExpSubject;
        private String mgmtExpSubject;
        private String rdExpSubject;
        private String memo;

    }


}
