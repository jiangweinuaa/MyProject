package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_FeeSetupSubjectUpdateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public static class Request {
        @JSONFieldRequired
        private String accountId;
        @JSONFieldRequired
        private String coaRefID;
        private String status;
        @JSONFieldRequired
        private List<SetupList> setupList;

        private String create_Date;
        private String create_Time;

    }

    @NoArgsConstructor
    @Data
    public class SetupList {
        private String fee;
        private String feeNature;
        private String accSubject;
        private String revSubject;
        private String advSubject;
    }
}

