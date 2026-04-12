package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_CategorySubjectCreateReq  extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_CategorySubjectCreateReq.Level1Elm request;

    @Data
    public class Level1Elm{
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String coaRefId;

        @JSONFieldRequired
        private String status;

        @JSONFieldRequired
        private List<DCP_CategorySubjectCreateReq.SetUpList> setUpList;
    }


    @Data
    public class SetUpList{
        private String accountId;
        private String coaRefId;
        @JSONFieldRequired
        private String category;
        private String revSubject;
        private String costSubject;
        private String invSubject;
        private String discSubject;
        private String memo;

    }


}
