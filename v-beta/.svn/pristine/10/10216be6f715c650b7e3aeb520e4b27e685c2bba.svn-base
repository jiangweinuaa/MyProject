package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ArSetupSubjectUpdateReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_ArSetupSubjectUpdateReq.Level1Elm request;

    @Data
    public class Level1Elm{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String coaRefID;
        @JSONFieldRequired
        private String setupType;
        @JSONFieldRequired
        private String status;

        private List<DCP_ArSetupSubjectUpdateReq.SetUpList> setUpList;
    }


    @Data
    public class SetUpList{
        //private String accountId;
        //private String setupType;
        @JSONFieldRequired
        private String setupItem;
        private String setupDiscrip;
        private String subjectId;
        private String subjectDBCR;
        private String subjectSumType;
        private String discSubject;
        private String memo;
    }


}
