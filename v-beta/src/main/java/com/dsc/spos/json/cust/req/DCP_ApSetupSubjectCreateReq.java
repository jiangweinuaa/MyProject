package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ApSetupSubjectCreateReq  extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ApSetupSubjectCreateReq.LevelRequest request;

    @Data
    public class LevelRequest{
       @JSONFieldRequired
       private String coaRefID;
       @JSONFieldRequired
       private String accountId;
       //private String setupType;
       @JSONFieldRequired
       private String status;

       private List<SetupList> setupList;

    }

    @Data
    public class SetupList{
        @JSONFieldRequired
        private String setupType;
        @JSONFieldRequired
        private String setupItem;
        private String setupDiscrip;
        private String subjectId;
        private String subjectName;
        private String subjectDBCR;
        private String subjectSumType;
        private String discSubject;
        private String discSubjectName;
        private String memo;
    }


}
